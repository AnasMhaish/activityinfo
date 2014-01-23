package org.activityinfo.server.command.handler;

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

import com.google.inject.Inject;
import org.activityinfo.analysis.server.generator.ReportGenerator;
import org.activityinfo.analysis.shared.model.DateRange;
import org.activityinfo.api.shared.command.GenerateElement;
import org.activityinfo.api.shared.command.result.CommandResult;
import org.activityinfo.api.shared.exception.CommandException;
import org.activityinfo.server.database.hibernate.entity.User;

import java.util.logging.Logger;

/**
 * @author Alex Bertram
 */
public class GenerateElementHandler implements CommandHandler<GenerateElement> {

    private static final Logger LOGGER = Logger
            .getLogger(GenerateElementHandler.class.getName());

    private final ReportGenerator generator;

    @Inject
    public GenerateElementHandler(ReportGenerator generator) {
        this.generator = generator;
    }

    @Override
    public CommandResult execute(GenerateElement cmd, User user)
            throws CommandException {

        LOGGER.info("GenerateElement.element = " + cmd.getElement());

        return generator.generateElement(user, cmd.getElement(), null,
                new DateRange());

    }
}
