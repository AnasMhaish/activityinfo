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

import com.google.gwt.junit.client.GWTTestCase;
import org.activityinfo.datamodel.shared.resty.JsonEncoder;

/**
 * @author yuriyz on 6/12/14.
 */
public class GwtRestyTest extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "org.activityinfo.datamodel.DataModelTest";
    }

    public void test() {
        RestyTest test = new RestyTest() {
            @Override
            public JsonEncoder getJsonEncoder() {
                return GwtJsonEncoder.getInstance();
            }
        };
        test.testAll();
    }
}

