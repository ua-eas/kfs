/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.kfs.sys.datatools.exportdata;

import org.kuali.kfs.sys.datatools.util.PropertyLoadingFactoryBean;
import org.kuali.kfs.sys.datatools.util.TableDataLoader;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.sql.DataSource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Exports a database schema in a format suitable for phase 2 of demo database creation.
 *
 * Usage:  java -cp path_to_kfs/WEB-INF/lib/*:path_to_jdbc_connector.jar -Dadditional.kfs.config.locations=path_to_config.properties org.kuali.kfs.sys.datatools.exportdata.ExportData
 *
 * On Windows, the classpath separator is a semicolon, rather than a colon.
 *
 */
public class ExportData {
    private String rootDirectory;
    private String schema;
    private List<String> skipTableExpressions;
    private Map<Integer, String> typeNames;
    private ClassPathXmlApplicationContext applicationContext;
	private SimpleDateFormat dateFormat;

    public static final String DELIMITER = "~&~\t~&~";
    public static final String QUOTE = "'";
    public static final String HEADER_BEGIN = "HEADER[";
    public static final String HEADER_END = "]HEADER";
    public static final String RECORD_BEGIN = "RECORD[";
    public static final String RECORD_END = "]RECORD";
    public static final String COLUMN_SEPARATOR = "::";

    private static final String LIQUIBASE_BEGIN= "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
    		+ "<!--\n"
    		+ "   - The Kuali Financial System, a comprehensive financial management system for higher education.\n"
    		+ "   - \n"
       		+ "   - Copyright 2005-2016 The Kuali Foundation\n"
    		+ "   - \n"
    		+ "   - This program is free software: you can redistribute it and/or modify\n"
    		+ "   - it under the terms of the GNU Affero General Public License as\n"
    		+ "   - published by the Free Software Foundation, either version 3 of the\n"
    		+ "   - License, or (at your option) any later version.\n"
    		+ "   - \n"
    		+ "   - This program is distributed in the hope that it will be useful,\n"
    		+ "   - but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
    		+ "   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"
    		+ "   - GNU Affero General Public License for more details.\n"
    		+ "   - \n"
    		+ "   - You should have received a copy of the GNU Affero General Public License\n"
    		+ "   - along with this program.  If not, see <http://www.gnu.org/licenses/>.\n"
    		+ " -->\n"
    		+ "<databaseChangeLog xmlns=\"http://www.liquibase.org/xml/ns/dbchangelog\" xmlns:ext=\"http://www.liquibase.org/xml/ns/dbchangelog-ext\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.liquibase.org/xml/ns/dbchangelog-ext http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-ext.xsd http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.4.xsd\">\n";
    private static final String LIQUIBASE_END = "</databaseChangeLog>\n";

    public static final void main(String[] args) throws IOException {
        ExportData c = new ExportData();
        long startTime = System.nanoTime();
        c.go();
        long estimatedTime = System.nanoTime() - startTime;
        System.out.printf("\nTime run: %d seconds\n", estimatedTime / 1000 / 1000 / 1000);
    }

    public void go() throws IOException {
		dateFormat = new SimpleDateFormat(TableDataLoader.DATE_FORMAT);

        initialize();
        DataSource kfsDataSource = applicationContext.getBean("dataSource", DataSource.class);

        try (Connection con = kfsDataSource.getConnection()) {
            List<String> tableNames = getTableNames(con);
            for (String table : tableNames) {
                extractTable(con, table, rootDirectory);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates a single data file, corresponding to a single database table.
     *
     * @param con
     * @param table
     * @param root
     * @throws Exception
     */
    private void extractTable(Connection con,String table,String root) throws Exception {
    	System.out.println(table);

    	try (PreparedStatement ps = con.prepareStatement("select * from " + table)) {
    		try (ResultSet rs = ps.executeQuery()) {
    			if (!rs.isBeforeFirst()) {
    				// Empty rowset; don't create files
    				return;
    			}

    			// Create Liquibase file
    			try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(root + table + ".xml")))) {
    				out.print(LIQUIBASE_BEGIN);
    				out.print("  <changeSet id=\"IMPORT_" + table + "\" author=\"kfs\" context=\"demo,unit\">\n");
    				out.print("    <customChange class=\"org.kuali.kfs.sys.datatools.util.TableDataLoader\">\n");
					out.print("      <param name=\"table\" value=\"" + table + "\"/>\n");
    				out.print("      <param name=\"file\" value=\"org/kuali/kfs/core/db/phase2/" + table + ".dat\" />\n");
    				out.print("    </customChange>\n");
    				out.print("  </changeSet>\n");
    				out.print(LIQUIBASE_END);
    			}

    			// Create data file
    			try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(root + table + ".dat")))) {
    				// Print column list for header
    				ResultSetMetaData rsmd = rs.getMetaData();
    				out.print(HEADER_BEGIN + DELIMITER);
    				for ( int i = 1; i <= rsmd.getColumnCount(); i++ ) {
    					String typeName = typeNames.get(rsmd.getColumnType(i));
    					if (typeName == null) {
    						throw new RuntimeException("Unknown JDBC type: " + i);
    					}
    					out.print(rsmd.getColumnName(i) + COLUMN_SEPARATOR + typeName + DELIMITER);
    				}
    				out.print(HEADER_END + "\n");

    				// Loop through records
    				while (rs.next()) {
    					out.print(RECORD_BEGIN + DELIMITER);
    					for ( int i = 1; i <= rsmd.getColumnCount(); i++ ) {
    						out.print(columnData(rsmd,rs,i) + DELIMITER);
    					}
    					out.print(RECORD_END + "\n");
    				}
    			}
    		}
    	}
    }

	/**
	 * Formats a column's value.
	 *
	 * @param rsmd
	 * @param rs
	 * @param i
	 * @return
	 * @throws SQLException
	 */
    private String columnData(ResultSetMetaData rsmd,ResultSet rs,int i) throws SQLException {
        String value = rs.getString(i);
        int columnType = rsmd.getColumnType(i);

        if (value != null) {
        	switch (columnType) {
	        	case Types.BLOB:
	        		Blob blob = rs.getBlob(i);
		        	if (blob != null) {
		        		byte[] bytes = blob.getBytes(1, (int) blob.length());
		        		value = Base64.getEncoder().encodeToString(bytes);
	        		}
	                break;
	        	case Types.DATE:
	        		value = dateFormat.format(rs.getDate(i));
	        		break;
	        	case Types.TIMESTAMP:
					value = dateFormat.format(rs.getTimestamp(i));
	        		break;
        	}
            return QUOTE + value + QUOTE;
        } else {
            return "NULL";
        }
    }

    /**
     * Gets the list of tables to export.
     *
     * @param con
     * @return
     * @throws SQLException
     */
    private List<String> getTableNames(Connection con) throws SQLException {
        List<String> tableNames = new ArrayList<>();
        try (ResultSet rs = con.getMetaData().getTables(null, schema, "%",  new String[]{ "TABLE" })) {
        	while (rs.next()) {
        		String table = rs.getString(3);
                if ( ! skipTable(table) ) {
                    tableNames.add(table);
                } else {
                    System.out.println("Skipping " + table);
                }
        	}
        }
        return tableNames;
    }

    /**
     * Determines whether a table is in the exclusion list.
     *
     * @param table
     * @return
     */
    private boolean skipTable(String table) {
        for (String expression : skipTableExpressions) {
            if ( table.matches(expression) ) {
                return true;
            }
        }
        return false;
    }

    private void initialize() {
        applicationContext = new ClassPathXmlApplicationContext("org/kuali/kfs/sys/datatools/liquirelational/kfs-liqui-relational-bootstrap.xml");
        applicationContext.start();
        readProperties();
        loadTypeNames();
    }

    private void readProperties() {
    	rootDirectory = PropertyLoadingFactoryBean.getBaseProperty("export.rootDirectory");
    	if (!rootDirectory.endsWith(File.separator)) {
    		rootDirectory += File.separator;
    	}

    	schema = PropertyLoadingFactoryBean.getBaseProperty("export.schema");

        String skipTables = PropertyLoadingFactoryBean.getBaseProperty("export.skip");
        if (skipTables != null) {
        	skipTableExpressions = Arrays.asList(skipTables.split(";"));
        } else {
        	skipTableExpressions = new ArrayList<String>();
        }
    }


    /**
     * For convenience, loads a map to turn JDBC column type values into their names.
     */
    private void loadTypeNames() {
		typeNames = new HashMap<Integer, String>();
		for (Field field : Types.class.getFields()) {
			try {
				typeNames.put((Integer)field.get(null), field.getName());
			} catch (IllegalArgumentException | IllegalAccessException e) {	}
		}
	}
}
