package org.activityinfo.ui.client.component.form.field;
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

import com.google.common.collect.Sets;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.core.shared.form.FormInstanceLabeler;
import org.activityinfo.ui.client.component.form.model.SimpleListViewModel;

import java.util.Set;

/**
 * @author yuriyz on 2/10/14.
 */
public class ComboBoxFieldWidget implements ReferenceFieldWidget {

    private final ListBox dropBox;

    public ComboBoxFieldWidget(final SimpleListViewModel range, final ValueUpdater valueUpdater) {
        dropBox = new ListBox(false);
        dropBox.addStyleName("form-control");

        for (FormInstance instance : range.getInstances()) {
            dropBox.addItem(
                    FormInstanceLabeler.getLabel(instance),
                    instance.getId().asString());
        }
        dropBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                valueUpdater.update(updatedValue());
            }
        });
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        dropBox.setEnabled(!readOnly);
    }

    private Set<Cuid> updatedValue() {
        Set<Cuid> value = Sets.newHashSet();
        int selectedIndex = dropBox.getSelectedIndex();
        if(selectedIndex != -1) {
            value.add(new Cuid(dropBox.getValue(selectedIndex)));
        }
        return value;
    }

    @Override
    public void setValue(Set<Cuid> value) {
        for(int i=0;i!=dropBox.getSelectedIndex();++i) {
            Cuid id = new Cuid(dropBox.getValue(i));
            if(value.contains(id)) {
                dropBox.setSelectedIndex(i);
                break;
            }
        }
    }

    @Override
    public Widget asWidget() {
        return dropBox;
    }
}
