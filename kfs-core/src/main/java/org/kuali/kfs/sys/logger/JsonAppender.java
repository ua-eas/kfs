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
package org.kuali.kfs.sys.logger;

import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.spi.LoggingEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class JsonAppender extends RollingFileAppender {
    protected SimpleDateFormat dateFormatter;

    public JsonAppender() {
        dateFormatter = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z");
    }

    protected void appendField(StringBuffer json,String field,String value,boolean last) {
        json.append("  \"");
        json.append(field);
        json.append("\": \"");
        json.append(value.replaceAll("\"","'"));
        json.append("\"");
        if ( ! last ) {
            json.append(",");
        }
        json.append("\n");
    }

    @Override
    protected void subAppend(LoggingEvent event) {
        StringBuffer json = new StringBuffer();
        json.append("{\n");

        appendField(json,"time","[" + dateFormatter.format(new Date(event.getTimeStamp())) + "]",false);
        appendField(json,"level",event.getLevel().toString(),false);
        appendField(json,"message",event.getMessage().toString(),false);
        appendField(json,"thread",event.getThreadName(),false);

        Map<String,String> props = event.getProperties();
        for (String key : props.keySet()) {
            appendField(json,key,event.getProperty(key),false);
        }

        if ( event.getThrowableStrRep() != null ) {
            json.append("  \"exception\": \"");
            for (int i = 0; i < event.getThrowableStrRep().length; i++) {
                json.append(event.getThrowableStrRep()[i]);
                json.append("\n");
            }
            json.append("\",\n");
        }
        appendField(json,"class",event.getLoggerName(),true);

        json.append("},\n");

        this.qw.write(json.toString());

        if(shouldFlush(event)) {
            this.qw.flush();
        }
    }
}
