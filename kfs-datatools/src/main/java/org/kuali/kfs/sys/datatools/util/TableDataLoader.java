/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kuali.kfs.sys.datatools.util;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import liquibase.util.StreamUtil;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.datatools.exportdata.ExportData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Liquibase extension to load data files created by org.kuali.kfs.sys.datatools.exportdata.ExportData.
 */
public class TableDataLoader implements CustomTaskChange {
    public static final String DATE_FORMAT = "yyyyMMddHHmmssSSSS";

    private String file;
    private String table;
    private ResourceAccessor resourceAccessor;
    private int count = 0;
    private SimpleDateFormat dateFormat;

    @Override
    public String getConfirmationMessage() {
        return table + ": loaded " + count + " rows";
    }

    @Override
    public void setFileOpener(ResourceAccessor resourceAccessor) {
        this.resourceAccessor = resourceAccessor;
    }

    @Override
    public void setUp() throws SetupException {
    }

    @Override
    public ValidationErrors validate(Database database) {
        return null;
    }

    @Override
    public void execute(Database database) throws CustomChangeException {
        if (table == null || table.length() == 0) {
            throw new CustomChangeException("Invalid table name " + table);
        }

        dateFormat = new SimpleDateFormat(DATE_FORMAT);

        JdbcConnection connection = (JdbcConnection) database.getConnection();
        try (InputStream in = StreamUtil.singleInputStream(file, resourceAccessor)) {
            if (in == null) {
                throw new CustomChangeException("Cannot find input file " + file);
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader((in)))) {
                List<Column> columns = readColumns(br);
                String sql = insertStatement(table, columns);

                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    List<String> fields;
                    while ((fields = readFields(br)) != null) {
                        if (fields.size() != columns.size()) {
                            throw new RuntimeException("Record has incorrect number of fields.");
                        }
                        ps.clearParameters();
                        for (int i = 0; i < columns.size(); i++) {
                            setParameter(ps, fields.get(i), columns.get(i).type, i + 1);
                        }
                        ps.addBatch();

                        if (++count % 1000 == 0) {
                            ps.executeBatch();
                        }
                    }
                    ps.executeBatch();
                    connection.commit();
                }
            }
        } catch (Exception e) {
            throw new CustomChangeException(e);
        }
    }

    /**
     * Generate insert statement for a table, given the list of columns.
     *
     * @param table
     * @param columns
     * @return
     */
    private String insertStatement(String table, List<Column> columns) {
        StringBuilder sql = new StringBuilder();
        sql.append("insert into ");
        sql.append(table);
        sql.append(" (");
        List<String> columnNames = columns.stream().map(c -> c.name).collect(Collectors.toList());
        sql.append(StringUtils.join(columnNames, ","));
        sql.append(") values (");
        String[] placeholders = new String[columns.size()];
        Arrays.fill(placeholders, "?");
        sql.append(StringUtils.join(placeholders, ","));
        sql.append(")");
        return sql.toString();
    }

    /**
     * Sets a parameter in the prepared statement.
     *
     * @param ps
     * @param value The value of the parameter.  Should be quoted, unless null.
     * @param type  The JDBC type of the column.
     * @param pos   The number of the parameter.  The first parameter is 1.
     * @throws SQLException
     */
    private void setParameter(PreparedStatement ps, String value, int type, int pos) throws SQLException {
        if ("NULL".equals(value)) {
            ps.setString(pos, null);
            return;
        }

        if (!(value.startsWith(ExportData.QUOTE) && value.endsWith(ExportData.QUOTE))) {
            throw new RuntimeException("Value is not quoted");
        }

        String unquoted = value.substring(ExportData.QUOTE.length(), value.length() - ExportData.QUOTE.length());

        switch (type) {
            case Types.DATE:
                try {
                    java.util.Date d = dateFormat.parse(unquoted);
                    ps.setDate(pos, new java.sql.Date(d.getTime()));
                } catch (ParseException e) {
                    throw new RuntimeException("Unable to convert data to date: " + unquoted, e);
                }
                return;
            case Types.TIMESTAMP:
                try {
                    java.util.Date d = dateFormat.parse(unquoted);
                    ps.setTimestamp(pos, new Timestamp(d.getTime()));
                } catch (ParseException e) {
                    throw new RuntimeException("Unable to convert data to date: " + unquoted, e);
                }
                return;
            case Types.BLOB:
                byte[] bytes = Base64.getDecoder().decode(unquoted);
                Blob blob = ps.getConnection().createBlob();
                blob.setBytes(0, bytes);
                ps.setBlob(pos, blob);
                return;
            default:
                ps.setString(pos, unquoted);
        }
    }

    /**
     * Called by Liquibase based on the contents of the table XML attribute
     *
     * @param table name of table
     */
    public void setTable(String table) {
        this.table = table;
    }

    /**
     * Called by Liquibase based on the contents of the file XML attribute
     *
     * @param file name of data file
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * Reads the file header to get the list of columns.
     *
     * @param br
     * @return
     * @throws Exception
     */
    private List<Column> readColumns(BufferedReader br) throws Exception {
        List<Column> result = new ArrayList<Column>();

        List<String> headerfields = readRecord(br, ExportData.HEADER_BEGIN, ExportData.HEADER_END);
        if (headerfields == null) {
            throw new RuntimeException("Error: Cannot read header");
        }

        for (String headerfield : headerfields) {
            result.add(new Column(headerfield));
        }

        return result;
    }

    /**
     * Reads a data record from the file.
     *
     * @param br
     * @return
     * @throws IOException
     */
    private List<String> readFields(BufferedReader br) throws IOException {
        return readRecord(br, ExportData.RECORD_BEGIN, ExportData.RECORD_END);
    }

    /**
     * Reads a record (either header or data) from the file.
     *
     * @param br
     * @param start
     * @param end
     * @return
     * @throws IOException
     */
    private List<String> readRecord(BufferedReader br, String start, String end) throws IOException {
        String line;
        StringBuilder record = new StringBuilder();

        // Read possible multi-line record.
        while ((line = br.readLine()) != null) {
            record.append(line);
            if (line.endsWith(ExportData.DELIMITER + end)) {
                break;
            } else {
                record.append("\n");
            }
        }

        if (record.length() == 0) {
            return null;
        }

        // Split into fields
        List<String> result = Arrays.asList(record.toString().split(ExportData.DELIMITER));

        // Verify that start and end markers are there
        if (!start.equals(result.get(0))) {
            throw new RuntimeException("Error: missing starting record marker");
        }
        if (!end.equals(result.get(result.size() - 1))) {
            throw new RuntimeException("Error reading ending record marker");
        }

        // Strip start and end markers
        return result.subList(1, result.size() - 1);
    }

    /**
     * Represents a table column.
     */
    private class Column {
        String name;
        int type;

        Column(String columntext) throws Exception {
            name = StringUtils.substringBefore(columntext, ExportData.COLUMN_SEPARATOR);
            String typename = StringUtils.substringAfter(columntext, ExportData.COLUMN_SEPARATOR);
            type = Types.class.getField(typename).getInt(null);
        }
    }
}
