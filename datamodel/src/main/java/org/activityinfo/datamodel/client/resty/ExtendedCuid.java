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
 * @author yuriyz on 6/12/14.
 */
public class ExtendedCuid extends Cuid {

    @JsonProperty
    String extendedCuid;

    @JsonCreator
    public ExtendedCuid(@JsonProperty("cuid") String cuid, @JsonProperty("extendedCuid") String extendedCuid) {
        super(cuid);
        this.extendedCuid = extendedCuid;
    }

    public String getExtendedCuid() {
        return extendedCuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ExtendedCuid that = (ExtendedCuid) o;

        return !(extendedCuid != null ? !extendedCuid.equals(that.extendedCuid) : that.extendedCuid != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (extendedCuid != null ? extendedCuid.hashCode() : 0);
        return result;
    }
}
