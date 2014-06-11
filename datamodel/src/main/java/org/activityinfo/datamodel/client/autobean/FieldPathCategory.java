package org.activityinfo.datamodel.client.autobean;
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

import com.google.web.bindery.autobean.shared.AutoBean;

import java.util.List;

/**
 * @author yuriyz on 6/11/14.
 */
public class FieldPathCategory {

    public static boolean isNested(AutoBean<FieldPath> fieldPath) {
        return fieldPath.as().getPath().size() > 1;
    }

    public static int getDepth(AutoBean<FieldPath> fieldPath) {
        return fieldPath.as().getPath().size();
    }

    public static FieldPath relativeTo(AutoBean<FieldPath> fieldPath, Cuid rootFieldId) {
        FieldPath as = fieldPath.as();
        List<Cuid> path = as.getPath();
        if (path.get(0).equals(rootFieldId)) {
            path.iterator().next();
            path.iterator().remove(); // remove first element
        }
        return as;
    }

    public static Cuid getLeafId(AutoBean<FieldPath> fieldPath) {
        List<Cuid> path = fieldPath.as().getPath();
        return path.get(path.size() - 1);
    }

    /**
     * Creates a new FieldPath that describes the nth ancestor of this
     * path. n=1 is the direct parent, n=2, grand parent, etc.
     */
    public static FieldPath ancestor(AutoBean<FieldPath> fieldPath, int n) {
        FieldPath as = fieldPath.as();
        List<Cuid> path = as.getPath();
        List<Cuid> subList = path.subList(0, path.size() - n);
        path.clear();
        path.addAll(subList);
        return as;
    }

    public static Cuid getRoot(AutoBean<FieldPath> fieldPath) {
        FieldPath as = fieldPath.as();
        List<Cuid> path = as.getPath();
        return path.get(0);
    }

    public static boolean isDescendantOf(AutoBean<FieldPath> fieldPath, Cuid fieldId) {
        FieldPath as = fieldPath.as();
        List<Cuid> path = as.getPath();
        return path.get(0).equals(fieldId);
    }

    public static FieldPath component(AutoBean<FieldPath> fieldPath, String componentName) {
        FieldPath copy = Auto.copy(fieldPath).as();
        copy.setComponentName(componentName);

        return copy;
    }

//    public static String toString(AutoBean<FieldPath> fieldPath) {
//        StringBuilder s = new StringBuilder();
//        for (Cuid fieldId : fieldPath.as().getPath()) {
//            if (s.length() > 0) {
//                s.append(".");
//            }
//            s.append(fieldId.asString());
//        }
//        return s.toString();
//    }
}
