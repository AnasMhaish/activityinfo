package org.activityinfo.ui.full.client.util;
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

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.UIObject;

/**
 * @author yuriyz on 1/27/14.
 */
public class GwtUtil {

    /**
     * Avoid instance creation.
     */
    private GwtUtil() {
    }

    public static void setFormControlStyles(UIObject uiObject) {
        uiObject.setStyleName("ai");
        uiObject.addStyleName("form-control");
    }

    public static void setVisibleInline(boolean visible, Element... elements) {
        if (elements != null) {
            for (Element element : elements) {
                if (visible) {
                    element.removeClassName("hidden");
                    element.addClassName("show-inline");
                } else {
                    element.removeClassName("show-inline");
                    element.addClassName("hidden");
                }
            }
        }
    }

    public static void setVisible(Element element, boolean visible) {
        if (element != null) {
            if (visible) {
                element.removeClassName("hidden");
                element.addClassName("show");
            } else {
                element.removeClassName("show");
                element.addClassName("hidden");
            }
        }
    }
}
