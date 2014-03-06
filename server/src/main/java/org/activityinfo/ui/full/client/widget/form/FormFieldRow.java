package org.activityinfo.ui.full.client.widget.form;
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
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.LocalizedString;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.api2.shared.form.FormFieldType;
import org.activityinfo.ui.full.client.Log;
import org.activityinfo.ui.full.client.style.TransitionUtil;
import org.activityinfo.ui.full.client.widget.HasReadOnly;
import org.activityinfo.ui.full.client.widget.undo.IsUndoable;

/**
 * @author yuriyz on 1/28/14.
 */
public class FormFieldRow extends Composite {

    private static FormFieldWidgetUiBinder uiBinder = GWT
            .create(FormFieldWidgetUiBinder.class);

    interface FormFieldWidgetUiBinder extends UiBinder<Widget, FormFieldRow> {
    }

    @UiField
    DivElement label;
    @UiField
    DivElement description;
    @UiField
    DivElement unit;
    @UiField
    FlowPanel control;
    @UiField
    RowToolbar toolbar;
    @UiField
    DivElement rowContainer;
    @UiField
    FormSectionInlineEdit addSectionPanel;
    @UiField
    FormFieldInlineEdit editFieldPanel;
    @UiField
    FormFieldInlineEdit addFieldPanel;

    private FormField formField;
    private IsWidget formFieldWidget;
    private final ElementNode node;
    private final FormPanel formPanel;

    public FormFieldRow(FormField formField, FormPanel formPanel, final ElementNode node) {
        TransitionUtil.ensureBootstrapInjected();
        initWidget(uiBinder.createAndBindUi(this));

        this.formField = formField;
        this.node = node;
        this.formPanel = formPanel;
        this.formFieldWidget = FormFieldWidgetFactory.create(formField, formPanel);
        this.toolbar.attach(this);
        this.toolbar.setFormPanel(formPanel);
        this.addSectionPanel.getOkButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addSectionPanel.updateModel();
                final int rowIndexOnPanel = node.getContentPanel().getWidgetIndex(FormFieldRow.this);
                node.addSection(addSectionPanel.getFormSection(), rowIndexOnPanel);
            }
        });

        initPanels();
        addHandlers();
        updateUI();
        control.add(formFieldWidget);
    }

    private void initPanels() {
        editFieldPanel.setRow(this);
        addFieldPanel.setRow(this);

        editFieldPanel.getOkButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                final LocalizedString oldLabel = formField.getLabel();
                final LocalizedString oldDescription = formField.getDescription();
                final FormFieldType oldType = formField.getType();
                final LocalizedString oldUnit = formField.getUnit();
                final boolean oldRequired = formField.isRequired();

                editFieldPanel.updateModel();

                final LocalizedString newLabel = formField.getLabel();
                final LocalizedString newDescription = formField.getDescription();
                final FormFieldType newType = formField.getType();
                final LocalizedString newUnit = formField.getUnit();
                final boolean newRequired = formField.isRequired();

                final FlowPanel widgetContainer = FormFieldRow.this.control;
                final IsWidget oldWidget = FormFieldRow.this.formFieldWidget;
                final boolean isTypeChanged = oldType != newType;
                if (isTypeChanged) {
                    widgetContainer.remove(oldWidget);
                    FormFieldRow.this.formFieldWidget = FormFieldWidgetFactory.create(formField, formPanel);
                    widgetContainer.add(FormFieldRow.this.formFieldWidget);
                }

                updateUI();
                formPanel.getUndoManager().addUndoable(new IsUndoable() {
                    @Override
                    public void undo() {
                        formField.setLabel(oldLabel);
                        formField.setDescription(oldDescription);
                        formField.setType(oldType);
                        formField.setUnit(oldUnit);
                        formField.setRequired(oldRequired);

                        if (isTypeChanged) {
                            widgetContainer.remove(FormFieldRow.this.formFieldWidget);
                            widgetContainer.add(oldWidget);
                            FormFieldRow.this.formFieldWidget = oldWidget;
                        }

                        updateUI();
                    }

                    @Override
                    public void redo() {
                        formField.setLabel(newLabel);
                        formField.setDescription(newDescription);
                        formField.setType(newType);
                        formField.setUnit(newUnit);
                        formField.setRequired(newRequired);

                        if (isTypeChanged) {
                            widgetContainer.remove(oldWidget);
                            FormFieldRow.this.formFieldWidget = FormFieldWidgetFactory.create(formField, formPanel);
                            widgetContainer.add(FormFieldRow.this.formFieldWidget);
                        }

                        updateUI();
                    }
                });
            }
        });
        addFieldPanel.getOkButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addFieldPanel.updateModel();
                int rowIndexOnPanel = node.getContentPanel().getWidgetIndex(FormFieldRow.this);
                node.addField(addFieldPanel.getFormField(), rowIndexOnPanel);
            }
        });
    }

    private void updateUI() {
        label.setInnerSafeHtml(SafeHtmlUtils.fromString(formField.getLabel().getValue()));
        description.setInnerSafeHtml(SafeHtmlUtils.fromString(formField.getDescription().getValue()));
        unit.setInnerSafeHtml(SafeHtmlUtils.fromString(formField.getUnit().getValue()));
    }

    private void addHandlers() {
        toolbar.getEditButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                editFieldPanel.apply(formField, rowContainer);
                editFieldPanel.setVisible(true);
            }
        });
        toolbar.getAddButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addFieldPanel.applyNew();
                addFieldPanel.setVisible(true);
            }
        });
        toolbar.getAddSectionButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addSectionPanel.applyNew();
                addSectionPanel.setVisible(true);
            }
        });
        toolbar.getRemoveButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                node.remove(FormFieldRow.this);
            }
        });
        toolbar.getUpButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                node.moveUpWidget(FormFieldRow.this, formField, true);
            }
        });
        toolbar.getDownButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                node.moveDownWidget(FormFieldRow.this, formField, true);
            }
        });
    }


    public void setValue(Object value) {
        if (value instanceof Cuid && formFieldWidget instanceof FormFieldWidgetReference) { // autofix of wrong data in form instance
            ((FormFieldWidgetReference) formFieldWidget).setValue(Sets.newHashSet((Cuid) value));
        } else if (formFieldWidget instanceof HasValue) { // run here is data in form instance is correct
            ((HasValue) formFieldWidget).setValue(value);
        }
    }

    public Object setValue() {
        if (formFieldWidget instanceof HasValue) {
            return ((HasValue) formFieldWidget).getValue();
        }
        return null;
    }

    public void setReadOnly(boolean readOnly) {
        if (formFieldWidget instanceof ValueBoxBase) {
            ((ValueBoxBase) formFieldWidget).setReadOnly(readOnly);
        } else if (formFieldWidget instanceof HasReadOnly) {
            ((HasReadOnly) formFieldWidget).setReadOnly(readOnly);
        } else {
            Log.error("Widget doesn't support read-only flag");
            assert true;
        }
    }

    public boolean isReadOnly() {
        if (formFieldWidget instanceof ValueBoxBase) {
            return ((ValueBoxBase) formFieldWidget).isReadOnly();
        } else if (formFieldWidget instanceof HasReadOnly) {
            return ((HasReadOnly) formFieldWidget).isReadOnly();
        } else {
            Log.error("Widget doesn't support read-only flag");
            assert true;
            return false;
        }
    }

    public IsWidget getFormFieldWidget() {
        return formFieldWidget;
    }

    public void clear() {
        setValue(null);
    }

    public FormField getFormField() {
        return formField;
    }

    public void setFormField(FormField formField) {
        this.formField = formField;
    }

    public FormPanel getFormPanel() {
        return formPanel;
    }

    public ElementNode getNode() {
        return node;
    }

    @Override
    public String toString() {
        return "FormFieldRow{" +
                "formField=" + formField +
                '}';
    }
}
