package org.activityinfo.ui.component.formdesigner.properties;
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

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.record.RecordBuilder;
import org.activityinfo.model.record.Records;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.ParametrizedFieldType;
import org.activityinfo.model.type.ParametrizedFieldTypeClass;
import org.activityinfo.model.type.expr.CalculatedFieldType;
import org.activityinfo.model.type.expr.ExprValue;
import org.activityinfo.service.store.ResourceLocator;
import org.activityinfo.ui.component.form.SimpleFormPanel;
import org.activityinfo.ui.component.form.VerticalFieldContainer;
import org.activityinfo.ui.component.form.field.FieldWidgetMode;
import org.activityinfo.ui.component.form.field.FormFieldWidgetFactory;
import org.activityinfo.ui.component.formdesigner.container.FieldWidgetContainer;
import org.activityinfo.ui.component.formdesigner.container.WidgetContainer;
import org.activityinfo.ui.component.formdesigner.event.HeaderSelectionEvent;
import org.activityinfo.ui.component.formdesigner.event.WidgetContainerSelectionEvent;
import org.activityinfo.ui.component.formdesigner.header.HeaderPresenter;
import org.activityinfo.ui.component.formdesigner.skip.SkipDialog;

import java.util.List;

/**
 * @author yuriyz on 7/9/14.
 */
public class PropertiesPresenter {

    private final PropertiesPanel view;

    private SimpleFormPanel currentDesignWidget = null;
    private HandlerRegistration labelKeyUpHandler;
    private HandlerRegistration descriptionKeyUpHandler;
    private HandlerRegistration codeKeyUpHandler;
    private HandlerRegistration requiredValueChangeHandler;
    private HandlerRegistration readonlyValueChangeHandler;
    private HandlerRegistration visibleValueChangeHandler;
    private HandlerRegistration relevanceButtonClickHandler;
    private HandlerRegistration relevanceEnabledValueHandler;
    private HandlerRegistration relevanceEnabledIfValueHandler;

    public PropertiesPresenter(PropertiesPanel view, EventBus eventBus) {
        this.view = view;
        eventBus.addHandler(WidgetContainerSelectionEvent.TYPE, new WidgetContainerSelectionEvent.Handler() {
            @Override
            public void handle(WidgetContainerSelectionEvent event) {
                WidgetContainer widgetContainer = event.getSelectedItem();
                if (widgetContainer instanceof FieldWidgetContainer) {
                    show((FieldWidgetContainer) widgetContainer);
                }
            }
        });
        eventBus.addHandler(HeaderSelectionEvent.TYPE, new HeaderSelectionEvent.Handler() {
            @Override
            public void handle(HeaderSelectionEvent event) {
                show(event.getSelectedItem());
            }
        });
        reset();
    }

    public PropertiesPanel getView() {
        return view;
    }

    private void reset() {
        if (currentDesignWidget != null) {
            view.getPanel().remove(currentDesignWidget);
            currentDesignWidget = null;
        }

        view.getRequiredGroup().setVisible(false);
        view.getReadOnlyGroup().setVisible(false);
        view.getVisibleGroup().setVisible(false);
        view.getRelevanceGroup().setVisible(false);
        view.getCodeGroup().setVisible(false);
        view.getInvalidCodeMessage().addClassName("hide");
        view.getDuplicateCodeMessage().addClassName("hide");

        if (labelKeyUpHandler != null) {
            labelKeyUpHandler.removeHandler();
        }
        if (descriptionKeyUpHandler != null) {
            descriptionKeyUpHandler.removeHandler();
        }
        if (codeKeyUpHandler != null) {
            codeKeyUpHandler.removeHandler();
        }
        if (requiredValueChangeHandler != null) {
            requiredValueChangeHandler.removeHandler();
        }
        if (readonlyValueChangeHandler != null) {
            readonlyValueChangeHandler.removeHandler();
        }
        if (relevanceButtonClickHandler != null) {
            relevanceButtonClickHandler.removeHandler();
        }
        if (visibleValueChangeHandler != null) {
            visibleValueChangeHandler.removeHandler();
        }
        if (relevanceEnabledValueHandler != null) {
            relevanceEnabledValueHandler.removeHandler();
        }
        if (relevanceEnabledIfValueHandler != null) {
            relevanceEnabledIfValueHandler.removeHandler();
        }
    }

    private void show(final FieldWidgetContainer fieldWidgetContainer) {
        reset();

        final FormField formField = fieldWidgetContainer.getFormField();

        view.setVisible(true);
        view.getRequiredGroup().setVisible(true);
        view.getReadOnlyGroup().setVisible(true);
        view.getVisibleGroup().setVisible(true);
        view.getRelevanceGroup().setVisible(true);
        view.getCodeGroup().setVisible(true);

        view.getLabel().setValue(Strings.nullToEmpty(formField.getLabel()));
        view.getDescription().setValue(Strings.nullToEmpty(formField.getDescription()));
        view.getRequired().setValue(formField.isRequired());
        view.getVisible().setValue(formField.isVisible());
        view.getCode().setValue(Strings.nullToEmpty(formField.getCode()));

        setRelevanceState(formField, true);
        validateCode(fieldWidgetContainer);

        relevanceButtonClickHandler = view.getRelevanceButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                SkipDialog dialog = new SkipDialog(fieldWidgetContainer, PropertiesPresenter.this);
                dialog.show();
            }
        });

        labelKeyUpHandler = view.getLabel().addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                formField.setLabel(view.getLabel().getValue());
                fieldWidgetContainer.syncWithModel();
            }
        });
        descriptionKeyUpHandler = view.getDescription().addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                formField.setDescription(view.getDescription().getValue());
                fieldWidgetContainer.syncWithModel();
            }
        });
        codeKeyUpHandler = view.getCode().addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent keyUpEvent) {
                if (validateCode(fieldWidgetContainer)) {
                    formField.setCode(view.getCode().getValue());
                    fieldWidgetContainer.syncWithModel();
                }
            }
        });
        requiredValueChangeHandler = view.getRequired().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                formField.setRequired(view.getRequired().getValue());
                fieldWidgetContainer.syncWithModel();
            }
        });
        readonlyValueChangeHandler = view.getReadOnly().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                formField.setReadOnly(view.getReadOnly().getValue());
                fieldWidgetContainer.syncWithModel();
            }
        });
        visibleValueChangeHandler = view.getVisible().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                formField.setVisible(view.getVisible().getValue());
                if (!view.getVisible().getValue()) {
                    // invisible formfield must not be required -> user is not able to set value for invisible field
                    view.getRequired().setValue(false);
                    formField.setRequired(false);
                }
                fieldWidgetContainer.syncWithModel();
            }
        });
        relevanceEnabledValueHandler = view.getRelevanceEnabled().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> booleanValueChangeEvent) {
                formField.setRelevanceConditionExpression(null);
                setRelevanceState(formField, false);
            }
        });
        relevanceEnabledIfValueHandler = view.getRelevanceEnabledIf().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> booleanValueChangeEvent) {
                setRelevanceState(formField, false);
            }
        });

        ResourceLocator locator = fieldWidgetContainer.getFormDesigner().getResourceLocator();
        currentDesignWidget = new SimpleFormPanel(locator, new VerticalFieldContainer.Factory(),
                new FormFieldWidgetFactory(locator, FieldWidgetMode.NORMAL), false) {
            @Override
            public void onFieldUpdated(FormField field, FieldValue newValue) {
                super.onFieldUpdated(field, newValue);
                ParametrizedFieldType parametrizedFieldType = (ParametrizedFieldType) formField.getType();
                RecordBuilder param = Records.buildCopyOf(parametrizedFieldType.getParameters());
                param.set(field.getId().asString(), newValue);
                ParametrizedFieldTypeClass typeClass = (ParametrizedFieldTypeClass) parametrizedFieldType.getTypeClass();
                if (formField.getType() instanceof CalculatedFieldType && newValue instanceof ExprValue) {
                    // for calculated fields we updated expression directly because it is handled via ExprFieldType
                    ExprValue exprValue = (ExprValue) newValue;
                    ((CalculatedFieldType) formField.getType()).setExpression(exprValue.getExpression());
                } else {
                    formField.setType(typeClass.deserializeType(param.build()));
                }
                fieldWidgetContainer.syncWithModel();
            }
        };
        if (formField.getType() instanceof ParametrizedFieldType) {
            ParametrizedFieldType parametrizedType = (ParametrizedFieldType) formField.getType();
            currentDesignWidget.asWidget().setVisible(true);
            currentDesignWidget.setValidationFormClass(fieldWidgetContainer.getFormDesigner().getFormClass());
            currentDesignWidget.show(Resources.createResource(parametrizedType.getParameters())).then(new AsyncCallback<Void>() {


                @Override
                public void onFailure(Throwable caught) {
                    GWT.log("Exception thrown while showing properties form", caught);
                }

                @Override
                public void onSuccess(Void result) {

                }
            });

        } else {
            currentDesignWidget.asWidget().setVisible(false);
        }
        view.getPanel().add(currentDesignWidget);
    }

    /**
     * Returns whether code is valid.
     *
     * @return whether code is valid
     */
    private boolean validateCode(FieldWidgetContainer fieldWidgetContainer) {
        String code = view.getCode().getValue();
        if (Strings.isNullOrEmpty(code)) {
            return true;
        }

        if (!FormField.isValidCode(code)) {
            view.getInvalidCodeMessage().removeClassName("hide");
            return false;
        } else {
            view.getInvalidCodeMessage().addClassName("hide");

            // check whether code is unique
            List<FormField> formFields = Lists.newArrayList(fieldWidgetContainer.getFormDesigner().getFormClass().getFields());
            formFields.remove(fieldWidgetContainer.getFormField());

            for (FormField formField : formFields) {
                if (code.equals(formField.getCode())) {
                    view.getDuplicateCodeMessage().removeClassName("hide");
                    return false;
                }
            }

            return true;
        }
    }

    public void setRelevanceState(FormField formField, boolean setRadioButtonsState) {
        if (setRadioButtonsState) {
            if (formField.hasRelevanceConditionExpression()) {
                view.getRelevanceEnabledIf().setValue(true);
            } else {
                view.getRelevanceEnabled().setValue(true);
            }
        }
        view.getRelevanceButton().setEnabled(view.getRelevanceEnabledIf().getValue());

//        view.getRelevanceState().setText(formField.hasRelevanceConditionExpression() ? I18N.CONSTANTS.defined() : I18N.CONSTANTS.no());
//        view.getRelevanceExpression().setInnerText(formField.getRelevanceConditionExpression());
//        if (formField.hasRelevanceConditionExpression()) {
//            view.getRelevanceExpression().removeClassName("hide");
//        } else if (!view.getRelevanceExpression().getClassName().contains("hide")) {
//            view.getRelevanceExpression().addClassName("hide");
//        }
    }

    public void show(final HeaderPresenter headerPresenter) {
        reset();

        final FormClass formClass = headerPresenter.getFormClass();

        view.setVisible(true);
        view.getLabel().setValue(Strings.nullToEmpty(formClass.getLabel()));
        view.getDescription().setValue(Strings.nullToEmpty(formClass.getDescription()));
        labelKeyUpHandler = view.getLabel().addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                formClass.setLabel(view.getLabel().getValue());
                headerPresenter.show();
            }
        });

        descriptionKeyUpHandler = view.getDescription().addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                formClass.setDescription(view.getDescription().getValue());
                headerPresenter.show();
            }
        });
    }
}
