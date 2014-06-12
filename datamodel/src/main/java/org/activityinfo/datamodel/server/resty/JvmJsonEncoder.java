package org.activityinfo.datamodel.server.resty;
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

import org.activityinfo.datamodel.shared.resty.JsonEncoder;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * @author yuriyz on 6/12/14.
 */
public class JvmJsonEncoder implements JsonEncoder {

    private static ObjectMapper createMapper() {
//        final AnnotationIntrospector jackson = new JacksonAnnotationIntrospector();

        final ObjectMapper mapper = new ObjectMapper();
//        final DeserializationConfig deserializationConfig = mapper.getDeserializationConfig().withAnnotationIntrospector(jackson);
//        final SerializationConfig serializationConfig = mapper.getSerializationConfig().withAnnotationIntrospector(jackson);
//        if (deserializationConfig != null && serializationConfig != null) {
//            // do nothing for now
//        }
        return mapper;
    }

    @Override
    public String encode(Object o) {
        try {
            return createMapper().writeValueAsString(o);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Object decode(Class clazz, String json) {
        try {
            return createMapper().readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
