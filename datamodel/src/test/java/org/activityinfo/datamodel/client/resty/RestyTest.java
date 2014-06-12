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

import junit.framework.Assert;
import org.activityinfo.datamodel.shared.resty.JsonEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yuriyz on 6/11/14.
 */
public abstract class RestyTest {

    public abstract JsonEncoder getJsonEncoder();

    public void testCuid() {
        final Cuid cuid = new Cuid("cuid1");

        String cJson = getJsonEncoder().encode(cuid);
        Cuid cRoundTrip = (Cuid) getJsonEncoder().decode(Cuid.class, cJson);
        Assert.assertEquals(cuid, cRoundTrip);
        Assert.assertEquals(cRoundTrip.asString(), "cuid1");
    }

    public void testExtendedCuid() {
        final ExtendedCuid extendedCuid = new ExtendedCuid("cuid1", "extendedCuid1");

        String cJson = getJsonEncoder().encode(extendedCuid);
        ExtendedCuid cRoundTrip = (ExtendedCuid) getJsonEncoder().decode(ExtendedCuid.class, cJson);
        Assert.assertEquals(extendedCuid, cRoundTrip);
        Assert.assertEquals(cRoundTrip.asString(), "cuid1");
        Assert.assertEquals(cRoundTrip.getExtendedCuid(), "extendedCuid1");

        final Cuid cuid = new ExtendedCuid("cuid1", "extendedCuid1");
        String json = getJsonEncoder().encode(cuid);
        Cuid decodedCuid = (Cuid) getJsonEncoder().decode(Cuid.class, json);

        Assert.assertEquals(cuid, decodedCuid);
        Assert.assertTrue(decodedCuid instanceof ExtendedCuid);
    }

    public void testFieldPath() {

        final List<Cuid> path = new ArrayList<>();

        // ATTENTION : break serialization !!! it will NOT work!
        // ignore please ;), read below
        path.add(new Cuid("cuid1")); // OMG -> Trick is to declare itself in @JsonSubTypes
        path.add(new ExtendedCuid("cuid1", "extendedCuid1")); // this will work
        path.add(new ExtendedCuid("cuid2", "extendedCuid2")); // this will work

        final FieldPath fieldPath = new FieldPath(path);
        String json = getJsonEncoder().encode(fieldPath);
        FieldPath decodedPath = (FieldPath) getJsonEncoder().decode(FieldPath.class, json);

        Assert.assertEquals(fieldPath, decodedPath);
    }

    public void testA() {
        A a =new A();
        a.list.add("1");
        a.list.add("2");

        String json = getJsonEncoder().encode(a);
        A decodedA = (A) getJsonEncoder().decode(A.class , json);

        Assert.assertEquals(a, decodedA);
    }

    public void testAll() {
        testCuid();
        testExtendedCuid();
        testFieldPath();
        testA();
    }
}
