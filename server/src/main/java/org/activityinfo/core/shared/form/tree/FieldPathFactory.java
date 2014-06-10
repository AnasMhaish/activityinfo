package org.activityinfo.core.shared.form.tree;
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
import org.activityinfo.core.shared.form.FormField;
import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.datamodel.shared.form.FieldPath;

import java.util.List;

/**
 * @author yuriyz on 6/10/14.
 */
public class FieldPathFactory {

    public static FieldPath create(List<FormField> prefix, FormField field) {
        List<Cuid> path = Lists.newArrayList();
        for (FormField prefixField : prefix) {
            path.add(prefixField.getId());
        }
        path.add(field.getId());
        return new FieldPath(path);
    }

    public static FieldPath create(FieldPath parent, FormField field) {
        List<Cuid> path = Lists.newArrayList();

        if (parent != null) {
            path.addAll(parent.getPath());
        }
        path.add(field.getId());
        return new FieldPath(path);
    }

    public static FieldPath create(FormField... fields) {
        List<Cuid> path = Lists.newArrayList();
        for (FormField field : fields) {
            path.add(field.getId());
        }
        return new FieldPath(path);
    }

    public static FieldPath child(FieldPath path, FormField field) {
        return create(path, field);
    }

}
