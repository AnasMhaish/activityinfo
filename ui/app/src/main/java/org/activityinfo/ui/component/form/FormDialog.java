package org.activityinfo.ui.component.form;
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

import com.google.common.base.Supplier;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.ResourceLocator;
import org.activityinfo.service.store.UpdateResult;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.request.SaveRequest;
import org.activityinfo.ui.component.form.field.FieldWidgetMode;
import org.activityinfo.ui.component.form.field.FormFieldWidgetFactory;
import org.activityinfo.ui.widget.loading.ExceptionOracle;
import org.activityinfo.ui.widget.loading.LoadingPanel;
import org.activityinfo.ui.widget.loading.PageLoadingPanel;
import org.activityinfo.ui.widget.modal.ModalDialog;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author yuriyz on 3/28/14.
 */
public class FormDialog {

    private static final Logger LOGGER = Logger.getLogger(FormDialog.class.getName());

    private FormDialogCallback callback;

    private final Application application;
    private final ResourceLocator resourceLocator;

    private final ModalDialog dialog;
    private final SimpleFormPanel formPanel;
    private final LoadingPanel<FormInstance> loadingPanel;

    public FormDialog(ResourceLocator resourceLocator, Application application) {
        this.resourceLocator = resourceLocator;
        this.application = application;

        //ModalStylesheet.INSTANCE.ensureInjected();


        formPanel = new SimpleFormPanel(
                resourceLocator,
                new VerticalFieldContainer.Factory(),
                new FormFieldWidgetFactory(resourceLocator, FieldWidgetMode.NORMAL));


        loadingPanel = new LoadingPanel<>(new PageLoadingPanel());
        loadingPanel.setDisplayWidget(formPanel);
        dialog = new ModalDialog(loadingPanel);
        dialog.getPrimaryButton().setText(I18N.CONSTANTS.save());
        dialog.getPrimaryButton().setStyleName("btn btn-primary");
        dialog.getPrimaryButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                save();
            }
        });
    }

    public void setDialogTitle(String text) {
  //      dialog.setDialogTitle(text);
    }

    public void show(final FormInstance instance, FormDialogCallback callback) {
        this.callback = callback;
        loadingPanel.show(new Supplier<Promise<FormInstance>>() {
            @Override
            public Promise<FormInstance> get() {
                return Promise.resolved(instance);
            }
        });
        dialog.show();
    }


    public void show(final ResourceId instanceId, FormDialogCallback callback) {
        this.callback = callback;
        loadingPanel.show(new Supplier<Promise<FormInstance>>() {

            @Override
            public Promise<FormInstance> get() {
                return resourceLocator.getFormInstance(instanceId);
            }
        });
        dialog.show();
    }

    public void save() {
        dialog.getStatusLabel().setText(I18N.CONSTANTS.saving());
        dialog.getPrimaryButton().setEnabled(false);

        application.getRequestDispatcher().execute(new SaveRequest(formPanel.getInstance())).then(new AsyncCallback<UpdateResult>() {
            @Override
            public void onFailure(Throwable caught) {
                LOGGER.log(Level.SEVERE, "Save failed", caught);
                dialog.getStatusLabel().setText(ExceptionOracle.getExplanation(caught));
                dialog.getPrimaryButton().setEnabled(true);
            }

            @Override
            public void onSuccess(UpdateResult result) {
                dialog.hide();
                callback.onPersisted(formPanel.getInstance());
            }
        });
    }

}
