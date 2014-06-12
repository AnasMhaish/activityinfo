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
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.junit.client.GWTTestCase;
import junit.framework.Assert;
import org.fusesource.restygwt.client.JsonEncoderDecoder;

/**
 * @author yuriyz on 6/11/14.
 */
public class RestyTest extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "org.activityinfo.datamodel.DataModelTest";
    }

    public interface CuidCodec extends JsonEncoderDecoder<Cuid> {
    }

    public void testCuid() {
        final Cuid cuid = new Cuid("cuid1");

        CuidCodec cuidCodec = GWT.create(CuidCodec.class);
        JSONValue cJson = cuidCodec.encode(cuid);
        Cuid cRoundTrip = cuidCodec.decode(cJson);
        Assert.assertEquals(cuid, cRoundTrip);
        Assert.assertEquals(cRoundTrip.asString(), "cuid1");
    }
}
