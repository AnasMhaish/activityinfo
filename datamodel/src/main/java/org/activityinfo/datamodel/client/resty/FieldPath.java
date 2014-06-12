package org.activityinfo.datamodel.client.resty;
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

import java.util.ArrayList;
import java.util.List;

/**
 * @author yuriyz on 6/12/14.
 */
public class FieldPath {

//    @JsonProperty("path")
    List<Cuid> path = new ArrayList<>();

//    /**
//     * The name of the field component
//     */
//    @JsonProperty("componentName")
//    String componentName;


    public FieldPath() {
    }

    public FieldPath(List<Cuid> fieldIds) {
        path = fieldIds;
    }

//    @JsonCreator
//    public FieldPath(@JsonProperty("path") List<Cuid> fieldIds) {
//        path = fieldIds;
//    }

//    public FieldPath(Cuid rootFieldId, FieldPath relativePath) {
//        path = new ArrayList<>();
//        path.addAll(relativePath.path);
//    }
//
//
//    public FieldPath(FieldPath prefix, Cuid key) {
//        path = new ArrayList<>();
//        if (prefix != null) {
//            path.addAll(prefix.path);
//        }
//        path.add(key);
//    }
//
//    public FieldPath(FieldPath parent, FieldPath relativePath) {
//        this.path = new ArrayList<>();
//        this.path.addAll(parent.path);
//        this.path.addAll(relativePath.path);
//    }
//
//    public boolean isNested() {
//        return path.size() > 1;
//    }
//
//    public int getDepth() {
//        return path.size();
//    }

//    public FieldPath relativeTo(Cuid rootFieldId) {
////        Preconditions.checkArgument(path.get(0).equals(rootFieldId)); todo
//        return new FieldPath(path.subList(1, path.size()));
//    }
//
//    public Cuid getLeafId() {
//        return path.get(path.size() - 1);
//    }

    /**
     * Creates a new FieldPath that describes the nth ancestor of this
     * path. n=1 is the direct parent, n=2, grand parent, etc.
     */
//    public FieldPath ancestor(int n) {
//        return new FieldPath(path.subList(0, path.size() - n));
//    }
//
//    public Cuid getRoot() {
//        return path.get(0);
//    }
//
//    public boolean isDescendantOf(Cuid fieldId) {
//        return path.get(0).equals(fieldId);
//    }

//    public FieldPath component(String componentName) {
//        FieldPath path = new FieldPath(this.path);
//        path.componentName = componentName;
//        return path;
//    }
//
//    public String getComponentName() {
//        return componentName;
//    }

    public List<Cuid> getPath() {
        return path;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Cuid fieldId : path) {
            if (s.length() > 0) {
                s.append(".");
            }
            s.append(fieldId.asString());
        }
        return s.toString();
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof FieldPath)) {
            return false;
        }
        FieldPath otherPath = (FieldPath) obj;

        return path.equals(otherPath.path);
    }
//
//    public Iterator<Cuid> iterator() {
//        return path.iterator();
//    }
}
