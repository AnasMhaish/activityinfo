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

import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.ui.client.page.PageId;
import org.activityinfo.ui.client.page.PageState;
import org.activityinfo.ui.client.page.app.Section;

import java.util.Arrays;
import java.util.List;

/**
 * @author yuriyz on 1/31/14.
 */
public class UserFormPlace implements PageState {

    public static final PageId PAGE_ID = new PageId("site-form");

    private Cuid userFormId;
    private Cuid userFormInstanceId;
    private boolean createNewForm;

    public UserFormPlace() {
    }

    public UserFormPlace(Cuid userFormId, Cuid userFormInstanceId) {
        this.userFormId = userFormId;
        this.userFormInstanceId = userFormInstanceId;
    }

    @Override
    public PageId getPageId() {
        return PAGE_ID;
    }

    @Override
    public String serializeAsHistoryToken() {
        return UserFormPlaceParser.serialize(this);
    }

    public String serializeAsPlaceHistoryToken() {
        return getPageId() + "/" + serializeAsHistoryToken();
    }

    @Override
    public List<PageId> getEnclosingFrames() {
        return Arrays.asList(PAGE_ID);
    }

    @Override
    public Section getSection() {
        return Section.DATA_ENTRY;
    }

    public Cuid getUserFormId() {
        return userFormId;
    }

    public void setUserFormId(Cuid userFormId) {
        this.userFormId = userFormId;
    }

    public Cuid getUserFormInstanceId() {
        return userFormInstanceId;
    }

    public void setUserFormInstanceId(Cuid userFormInstanceId) {
        this.userFormInstanceId = userFormInstanceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserFormPlace that = (UserFormPlace) o;

        if (userFormId != null ? !userFormId.equals(that.userFormId) : that.userFormId != null) {
            return false;
        }
        if (userFormInstanceId != null ? !userFormInstanceId.equals(that.userFormInstanceId) :
                that.userFormInstanceId != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = userFormId != null ? userFormId.hashCode() : 0;
        result = 31 * result + (userFormInstanceId != null ? userFormInstanceId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserFormPlace{" +
               ", userFormId=" + userFormId +
               ", userFormInstanceId=" + userFormInstanceId +
               '}';
    }
}
