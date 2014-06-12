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

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author yuriyz on 6/11/14.
 */
public class Cuid {

    @JsonProperty
    String cuid;

    public static Cuid create(String cuid) {
        return new Cuid(cuid);
    }

    @JsonCreator
    public Cuid(@JsonProperty("cuid") String cuid) {
        this.cuid = cuid;
    }

    public String asString() {
        return this.cuid;
    }

    public char getDomain() {
        return cuid.charAt(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Cuid cuid = (Cuid) o;
        return this.cuid.equals(cuid.cuid);
    }

    @Override
    public int hashCode() {
        return cuid.hashCode();
    }
}
