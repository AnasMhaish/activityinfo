package org.activityinfo.core.shared.table.provider;
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

import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.form.tree.FieldPath;

/**
* @author yuriyz on 5/29/14.
*/
public class CacheKey {

    private final Cuid formClassId;
    private final FieldPath columnPath;

    public CacheKey(Cuid formClassId, FieldPath columnPath) {
        this.formClassId = formClassId;
        this.columnPath = columnPath;
    }

    public Cuid getFormClassId() {
        return formClassId;
    }

    public FieldPath getColumnPath() {
        return columnPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CacheKey key = (CacheKey) o;

        return !(columnPath != null ? !columnPath.equals(key.columnPath) : key.columnPath != null) && !(formClassId != null ? !formClassId.equals(key.formClassId) : key.formClassId != null);
    }

    @Override
    public int hashCode() {
        int result = formClassId != null ? formClassId.hashCode() : 0;
        result = 31 * result + (columnPath != null ? columnPath.hashCode() : 0);
        return result;
    }
}
