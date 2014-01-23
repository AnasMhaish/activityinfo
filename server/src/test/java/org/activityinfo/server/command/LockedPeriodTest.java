package org.activityinfo.server.command;

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

import com.bedatadriven.rebar.time.calendar.LocalDate;
import com.google.common.collect.Maps;
import org.activityinfo.api.shared.command.CreateLockedPeriod;
import org.activityinfo.api.shared.command.UpdateEntity;
import org.activityinfo.api.shared.command.result.CreateResult;
import org.activityinfo.api.shared.exception.CommandException;
import org.activityinfo.api.shared.model.LockedPeriodDTO;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.server.database.OnDataSet;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

@RunWith(InjectionSupport.class)
public class LockedPeriodTest extends CommandTestCase {

    @Test
    @OnDataSet("/dbunit/sites-simple1.db.xml")
    public void createTest() throws CommandException {

        setUser(1);

        LockedPeriodDTO dto = new LockedPeriodDTO();
        dto.setName("my name");
        dto.setFromDate(new LocalDate(2011, 1, 1));
        dto.setToDate(new LocalDate(2011, 1, 31));
        dto.setEnabled(true);

        CreateLockedPeriod create = new CreateLockedPeriod(dto);
        create.setUserDatabaseId(1);

        CreateResult result = execute(create);

        Map<String, Object> changes = Maps.newHashMap();
        changes.put("toDate", new LocalDate(2011, 2, 28));

        execute(new UpdateEntity("LockedPeriod", result.getNewId(), changes));

    }

}
