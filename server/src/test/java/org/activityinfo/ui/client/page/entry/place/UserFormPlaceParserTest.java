package org.activityinfo.ui.client.page.entry.place;
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
import org.activityinfo.datamodel.shared.Cuid;
import org.junit.Test;

/**
 * @author yuriyz on 2/3/14.
 */
public class UserFormPlaceParserTest {

    @Test
    public void empty() {
        assertCorrect(new UserFormPlace());
    }

    @Test
    public void formAndInstanceIdPresent() {
        assertCorrect(new UserFormPlace(Cuid.create("adf"), Cuid.create("sdf")));
    }

    @Test
    public void fromManualToken() {
        String token = "#site-form/adp/sxdg425";
        final UserFormPlace parsedPlace = UserFormPlaceParser.parseToken(token);
        Assert.assertTrue(parsedPlace != null && parsedPlace.getUserFormId() != null);
    }

    private void assertCorrect(UserFormPlace userFormPlace) {
        Assert.assertNotNull(userFormPlace);

        final String token = UserFormPlaceParser.serialize(userFormPlace);
        final UserFormPlace parsedPlace = UserFormPlaceParser.parseToken(token);

        Assert.assertEquals(userFormPlace, parsedPlace);
    }
}
