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

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * @author yuriyz on 6/11/14.
 */
@AutoBeanFactory.Category({CuidCategory.class, FieldPathCategory.class})
public interface CuidAutoFactory extends AutoBeanFactory {

    AutoBean<Cuid> cuid();

    AutoBean<Cuid> cuid(Cuid bean);

    AutoBean<FieldPath> fieldPath();

    AutoBean<FieldPath> fieldPath(FieldPath bean);


}
