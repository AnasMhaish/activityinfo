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

import com.google.common.collect.Iterables;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.core.shared.form.FormInstanceLabeler;
import org.activityinfo.ui.client.component.form.field.suggest.InstanceSuggestOracle;
import org.activityinfo.ui.client.component.form.field.suggest.InstanceSuggestion;
import org.activityinfo.ui.client.component.form.model.SimpleListViewModel;
import org.activityinfo.ui.client.widget.SuggestBox;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * @author yuriyz on 2/10/14.
 */
public class SuggestBoxWidget implements ReferenceFieldWidget {

    private final SuggestBox suggestBox;

    private Cuid value;
    private SimpleListViewModel range;

    public SuggestBoxWidget(SimpleListViewModel range, final ValueUpdater valueUpdater) {
        this.range = range;
        this.suggestBox = new SuggestBox(new InstanceSuggestOracle(range));
        this.suggestBox.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
            @Override
            public void onSelection(SelectionEvent<SuggestOracle.Suggestion> event) {
                InstanceSuggestion suggestion = (InstanceSuggestion) event.getSelectedItem();
                if(!Objects.equals(suggestion.getInstanceId(), value)) {
                    valueUpdater.update(Collections.singleton(suggestion.getInstanceId()));
                }
            }
        });
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        suggestBox.setEnabled(!readOnly);
    }

    @Override
    public void setValue(Set<Cuid> value) {
        Cuid newValue = Iterables.getFirst(value, null);
        if(!Objects.equals(newValue, this.value)) {
            this.value = newValue;
            if(newValue == null) {
                suggestBox.setValue(null);
            } else {
                suggestBox.setValue(findDisplayLabel(newValue));
            }
        }
    }

    private String findDisplayLabel(Cuid newValue) {
        for(FormInstance instance : range.getInstances()) {
            if(instance.getId().equals(newValue)) {
                return FormInstanceLabeler.getLabel(instance);
            }
        }
        return newValue.asString();
    }

    @Override
    public Widget asWidget() {
        return suggestBox;
    }
}
