package org.activityinfo.model.type.primitive;
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

import org.activityinfo.model.form.FormClassVisitor;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.SingletonTypeClass;
import org.activityinfo.model.type.TypeFieldType;

/**
 * Value type that represents a boolean value, either true or false.
 */
public class BooleanType implements FieldType {

    public static final BooleanType INSTANCE = new BooleanType();

    public static final FieldTypeClass TYPE_CLASS = new SingletonTypeClass() {
        @Override
        public String getId() {
            return "boolean";
        }

        @Override
        public String getLabel() {
            return "Boolean";
        }

        @Override
        public BooleanType createType() {
            return INSTANCE;
        }
    };

    @Override
    public FieldTypeClass getTypeClass() {
        return TYPE_CLASS;
    }

    @Override
    public <T> T accept(FormField field, FormClassVisitor<T> visitor) {
        return visitor.visitBooleanField(field, this);
    }

    @Override
    public Record asRecord() {
        return TypeFieldType.asRecord(this);
    }


}
