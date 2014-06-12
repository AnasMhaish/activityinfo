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

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONParser;
import org.activityinfo.datamodel.shared.resty.JsonEncoder;
import org.fusesource.restygwt.client.JsonEncoderDecoder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yuriyz on 6/12/14.
 */
public class GwtJsonEncoder implements JsonEncoder {

    private static JsonEncoder INSTANCE = null;

    public interface CuidCodec extends JsonEncoderDecoder<Cuid> {
    }

    public interface ExtendedCuidCodec extends JsonEncoderDecoder<ExtendedCuid> {
    }

    public interface FieldPathCodec extends JsonEncoderDecoder<FieldPath> {
    }

    public interface ACodec extends JsonEncoderDecoder<A> {
    }

    private static final Map<Class, JsonEncoderDecoder> MAP = new HashMap<>();

    public static JsonEncoder getInstance() {
        if (INSTANCE == null) {
            initMap();

            INSTANCE = new GwtJsonEncoder();
        }
        return INSTANCE;
    }

    private static void initMap() {
        MAP.put(Cuid.class, GWT.<JsonEncoderDecoder>create(CuidCodec.class));
        MAP.put(ExtendedCuid.class, GWT.<JsonEncoderDecoder>create(ExtendedCuidCodec.class));
        MAP.put(FieldPath.class, GWT.<JsonEncoderDecoder>create(FieldPathCodec.class));
        MAP.put(A.class, GWT.<JsonEncoderDecoder>create(ACodec.class));
    }

    @Override
    public String encode(Object o) {
        return MAP.get(o.getClass()).encode(o).toString();
    }

    @Override
    public Object decode(Class clazz, String json) {
        return MAP.get(clazz).decode(JSONParser.parseLenient(json));
    }

}
