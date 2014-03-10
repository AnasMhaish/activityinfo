package org.activityinfo.api2.server.formatter;
/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import org.activityinfo.api2.shared.formatter.DateFormatter;
import org.activityinfo.api2.shared.formatter.DateFormatterFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author yuriyz on 3/10/14.
 */
public class JavaDateFormatterFactory implements DateFormatterFactory {

    private static final SimpleDateFormat JAVA_FORMAT = new SimpleDateFormat(FORMAT);

    @Override
    public DateFormatter create() {
        return new DateFormatter() {
            @Override
            public String format(Date value) {
                return JAVA_FORMAT.format(value);
            }

            @Override
            public Date parse(String valueAsString) {
                try {
                    return JAVA_FORMAT.parse(valueAsString);
                } catch (ParseException e) {
                    e.printStackTrace(); // todo log
                    return null;
                }
            }
        };
    }
}
