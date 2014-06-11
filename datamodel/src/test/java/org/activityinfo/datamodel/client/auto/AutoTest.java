package org.activityinfo.datamodel.client.auto;
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
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import junit.framework.Assert;
import org.activityinfo.datamodel.client.autobean.Cuid;
import org.activityinfo.datamodel.client.autobean.CuidAutoFactory;
import org.activityinfo.datamodel.client.autobean.FieldPath;
import org.activityinfo.datamodel.shared.autobean.AutoBeanFactoryCreator;

import java.util.Arrays;
import java.util.List;

/**
 * @author yuriyz on 6/11/14.
 */
public abstract class AutoTest {

    public void test() {
        testCuid();
        testFieldPath();
    }

    public void testCuid() {
        CuidAutoFactory factory = creator().create(CuidAutoFactory.class);
        Cuid cuid = factory.cuid().as();
        cuid.setCuid("xyz123");

        AutoBean<Cuid> cuidAutoBean = AutoBeanUtils.getAutoBean(cuid);

        String json = AutoBeanCodex.encode(cuidAutoBean).getPayload();

        AutoBean<Cuid> decodedAutoBean = AutoBeanCodex.decode(factory, Cuid.class, json);
        Assert.assertEquals(decodedAutoBean.as().getCuid(), "xyz123");
        Assert.assertTrue(AutoBeanUtils.deepEquals(cuidAutoBean, decodedAutoBean));
    }

    public void testFieldPath() {
        CuidAutoFactory factory = creator().create(CuidAutoFactory.class);
        Cuid cuid = factory.cuid().as();
        cuid.setCuid("cuid2");

        AutoBean<FieldPath> fieldPathBean = factory.fieldPath();
        FieldPath fieldPath = fieldPathBean.as();
        fieldPath.setPath(Arrays.asList(cuid)); // ???
//        fieldPath.setPath(Arrays.asList(cuid));

        String json = AutoBeanCodex.encode(fieldPathBean).getPayload();
        AutoBean<FieldPath> decodedAutoBean = AutoBeanCodex.decode(factory, FieldPath.class, json);
        List<Cuid> decodedPath = decodedAutoBean.as().getPath();
        //System.out.println(new ArrayList<>(decodedPath));
        Assert.assertEquals(decodedPath.get(0).getCuid(), "cuid2");

//        FieldPathRecord fieldPathImpl = JsonUtils.unsafeEval("{\"path\":[{\"id\":\"cuid2\"}, {\"id\":\"cuid3\"}]}");
//        fieldPathImpl.get(Cuid.create("path"));
//        assertEquals("cuid2", fieldPathImpl.getPath().get(0).getId().asString());
//        GWT.log(fieldPathImpl.getPath().get(0).getId().asString());
    }

    public abstract AutoBeanFactoryCreator creator();

}
