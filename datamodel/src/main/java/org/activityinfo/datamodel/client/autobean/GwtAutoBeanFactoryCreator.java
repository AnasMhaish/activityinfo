package org.activityinfo.datamodel.client.autobean;
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

import com.google.gwt.core.shared.GWT;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import org.activityinfo.datamodel.shared.autobean.AutoBeanFactoryCreator;

/**
 * @author yuriyz on 6/11/14.
 */
public class GwtAutoBeanFactoryCreator implements AutoBeanFactoryCreator {

    private static final GwtAutoBeanFactoryCreator INSTANCE = new GwtAutoBeanFactoryCreator();

    @Override
    public <T extends AutoBeanFactory> T create(Class<T> clazz) {
        return GWT.create(clazz);
    }

    public static AutoBeanFactoryCreator instance() {
        return INSTANCE;
    }
}