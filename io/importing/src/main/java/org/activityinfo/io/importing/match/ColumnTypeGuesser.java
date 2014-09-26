package org.activityinfo.io.importing.match;
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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.NarrativeType;
import org.activityinfo.model.type.converter.Converter;
import org.activityinfo.model.type.converter.ConverterFactory;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.BooleanType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.model.type.time.LocalDateType;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Guesser use converters to guess column type.
 * <p/>
 * Note: If we can NOT convert value to particular type then we
 * don't have other choice then consider it as string.
 *
 * @author yuriyz on 5/9/14.
 */
public class ColumnTypeGuesser {

    public static final FieldTypeClass[] TYPES = new FieldTypeClass[] {
        QuantityType.TYPE_CLASS,
        TextType.TYPE_CLASS,
        LocalDateType.TYPE_CLASS,
        BooleanType.TYPE_CLASS };
    /**
     * Defined exact length of string to differ between FREE_TEXT and NARRATIVE types.
     * If string length less than #FREE_TEXT_LENGTH then type is #FREE_TEXT otherwise it is NARRATIVE.
     */
    public static final int FREE_TEXT_LENGTH = 80;


    private final Map<FieldTypeClass, Integer> typeMap = newFieldMap();

    private final List<String> columnValues;
    private final ConverterFactory converterFactory;

    public ColumnTypeGuesser(List<String> columnValues, ConverterFactory converterFactory) {
        this.columnValues = columnValues;
        this.converterFactory = converterFactory;
    }



    public FieldTypeClass guessType() {
        calculateTypeScores();

        final List<Map.Entry<FieldTypeClass, Integer>> copyEntrySet = Lists.newArrayList(typeMap.entrySet());
        Collections.sort(copyEntrySet, new Comparator<Map.Entry<FieldTypeClass, Integer>>() {
            @Override
            public int compare(Map.Entry<FieldTypeClass, Integer> o1, Map.Entry<FieldTypeClass, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        return copyEntrySet.get(copyEntrySet.size() - 1).getKey();
    }

    private void calculateTypeScores() {
        for (String value : columnValues) {
            final Map<FieldTypeClass, Integer> copyMap = Maps.newHashMap(typeMap);

            // we don't need to iterate over string types because input is always string
            copyMap.remove(TextType.TYPE_CLASS);
            copyMap.remove(NarrativeType.TYPE_CLASS);

            boolean hasMatch = false;
            for (Map.Entry<FieldTypeClass, Integer> entry : copyMap.entrySet()) {
                try {
                    final Converter stringConverter = converterFactory.createStringConverter(entry.getKey());
                    final Object convertedValue = stringConverter.convert(value);

                    // analyze converted value
                    if (convertedValue != null) {
                        increaseValue(entry.getKey());
                        hasMatch = true;
                        break;
                    }
                } catch (Exception e) {
                    // ignore
                }
            }

            // if no match then we fallback to string type
            if (!hasMatch) {
                final int length = value.length();
                if (length < FREE_TEXT_LENGTH) {
                    increaseValue(TextType.TYPE_CLASS);
                } else {
                    increaseValue(NarrativeType.TYPE_CLASS);
                }
            }
        }
    }

    private void increaseValue(FieldTypeClass type) {
        typeMap.put(type, typeMap.get(type) + 1);
    }

    private static Map<FieldTypeClass, Integer> newFieldMap() {
        Map<FieldTypeClass, Integer> typeMap = Maps.newHashMap();
        for (FieldTypeClass type : TYPES) {
            typeMap.put(type, 0);
        }
        return typeMap;
    }
}
