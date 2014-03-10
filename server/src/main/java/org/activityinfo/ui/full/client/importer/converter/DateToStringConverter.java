package org.activityinfo.ui.full.client.importer.converter;
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

import org.activityinfo.api2.client.formatter.GwtDateFormatterFactory;
import org.activityinfo.api2.shared.formatter.DateFormatter;

import javax.annotation.Nonnull;
import java.util.Date;

/**
 * @author yuriyz on 3/7/14.
 */
public class DateToStringConverter implements DateConverter<String> {

    public final static DateToStringConverter INSTANCE = new DateToStringConverter();

    private final static DateFormatter FORMATTER = new GwtDateFormatterFactory().create();

    private DateToStringConverter() {
    }

    @Nonnull
    @Override
    public String convert(@Nonnull Date value) {
        return FORMATTER.format(value);
    }
}
