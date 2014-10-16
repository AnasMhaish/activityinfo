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

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.inject.Inject;
import org.activityinfo.legacy.shared.command.GetReportModel;
import org.activityinfo.legacy.shared.command.UpdateReportModel;
import org.activityinfo.legacy.shared.command.result.CommandResult;
import org.activityinfo.legacy.shared.exception.CommandException;
import org.activityinfo.legacy.shared.exception.IllegalAccessCommandException;
import org.activityinfo.legacy.shared.exception.UnexpectedCommandException;
import org.activityinfo.server.database.hibernate.entity.ReportDefinition;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.server.report.ReportParserJaxb;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.xml.bind.JAXBException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdateReportModelHandler implements CommandHandler<UpdateReportModel> {

    private static final Logger LOGGER = Logger.getLogger(UpdateReportModelHandler.class.getName());

    private final EntityManager em;

    @Inject
    public UpdateReportModelHandler(final EntityManager em) {
        this.em = em;
    }

    @Override
    public CommandResult execute(final UpdateReportModel cmd, final User user) throws CommandException {

        Query query = em.createQuery("select r from ReportDefinition r where r.id in (:id)")
                        .setParameter("id", cmd.getModel().getId());

        ReportDefinition result = (ReportDefinition) query.getSingleResult();
        if (result.getOwner().getId() != user.getId()) {
            throw new IllegalAccessCommandException("Current user does not have the right to edit this report");
        }

        result.setTitle(cmd.getModel().getTitle());
        // result.setJson(cmd.getReportJsonModel());
        try {
            result.setXml(ReportParserJaxb.createXML(cmd.getModel()));
        } catch (JAXBException e) {
            throw new UnexpectedCommandException(e);
        }

        em.persist(result);
        invalidateMemcache(cmd.getModel().getId());

        return null;
    }

    public static void invalidateMemcache(Integer reportId) {
        try {
            final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
            memcacheService.delete(new GetReportModel(reportId, false), TimeUnit.SECONDS.toMillis(1));
            memcacheService.delete(new GetReportModel(reportId, true), TimeUnit.SECONDS.toMillis(1));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to invalidate report cache", e);
        }
    }

}
