package org.activityinfo.ui.full.client.component.form;
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

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.LocalizedString;
import org.activityinfo.core.shared.form.FormSection;
import org.activityinfo.core.shared.validation.*;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.ui.full.client.widget.CompositeWithMirror;

import java.util.List;

/**
 * @author yuriyz on 2/25/14.
 */
public class FormSectionInlineEdit extends CompositeWithMirror {
    private static FormSectionInlineEditBinder uiBinder = GWT
            .create(FormSectionInlineEditBinder.class);

    interface FormSectionInlineEditBinder extends UiBinder<Widget, FormSectionInlineEdit> {
    }

    private FormSection formSection;
    private Validator validator;

    @UiField
    Button okButton;
    @UiField
    TextBox sectionLabel;
    @UiField
    DivElement errorContainer;

    public FormSectionInlineEdit() {
        initWidget(uiBinder.createAndBindUi(this));
        validator = ValidatorBuilder.instance().
                addNotEmpty(sectionLabel, I18N.CONSTANTS.sectionLabel()).
                addValidator(new Validator() {
                    @Override
                    public List<ValidationFailure> validate() {
                        final List<ValidationFailure> failures = Lists.newArrayList();
                        if (formSection != null && formSection.getLabel().getValue().equals(sectionLabel.getValue())) {
                            final String message = ValidationUtils.format(I18N.CONSTANTS.sectionLabel(), I18N.CONSTANTS.valueNotChanged());
                            failures.add(new ValidationFailure(new ValidationMessage(message)));
                        }
                        return failures;
                    }
                }).
                build();
        sectionLabel.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent event) {
                fireState();
            }
        });
    }

    private void fireState() {
        final List<ValidationFailure> failures = validator.validate();
        ValidationUtils.show(failures, errorContainer);
        okButton.setEnabled(failures.isEmpty());
    }

    public void applyNew(Element... mirrorElements) {
        final Cuid newCuid = CuidAdapter.newSectionField();
        final FormSection newSection = new FormSection(newCuid);
        apply(newSection, mirrorElements);
    }

    public void apply() {
        this.sectionLabel.setValue(formSection != null ? formSection.getLabel().getValue() : "");
        fireState();
    }

    public void apply(FormSection formSection, Element... mirrorElements) {
        setFormSection(formSection);
        setMirrorElements(mirrorElements);
        apply();
    }

    public void setFormSection(FormSection formSection) {
        this.formSection = formSection;
    }

    public FormSection getFormSection() {
        return formSection;
    }

    public void updateModel() {
        if (formSection != null) {
            formSection.setLabel(new LocalizedString(sectionLabel.getValue()));
        }
    }

    public Button getOkButton() {
        return okButton;
    }

    @UiHandler("okButton")
    public void onOk(ClickEvent event) {
        hide();
    }

    @UiHandler("cancelButton")
    public void cancelButton(ClickEvent event) {
        hide();
    }

    public void hide() {
        setVisible(false);
    }
}
