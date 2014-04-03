package org.activityinfo.ui.client.pageView.formClass;

import com.google.common.base.Function;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.LocalizedString;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.ui.client.component.form.FormPanel;
import org.activityinfo.ui.client.pageView.InstancePageView;
import org.activityinfo.ui.client.widget.EditableHeader;

import javax.annotation.Nullable;

/**
 * This is page view for designing a FormClass. It is shown for the /design url
 *
 * Created by Mithun on 4/3/2014.
 */
public class FormClassDesignView implements InstancePageView{


    interface FormClassDesignViewUiBinder extends UiBinder<HTMLPanel, FormClassDesignView> {
    }

    private static FormClassDesignViewUiBinder ourUiBinder = GWT.create(FormClassDesignViewUiBinder.class);

    private ResourceLocator resourceLocator;
    private final HTMLPanel rootElement;

    private FormClass formClass;

    @UiField
    EditableHeader formHeader;
    @UiField(provided = true)
    FormPanel formPanel;

    public FormClassDesignView(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
        this.formPanel = new FormPanel(resourceLocator);
        rootElement = ourUiBinder.createAndBindUi(this);
    }

    @Override
    public Promise<Void> show(FormInstance value) {
        return this.resourceLocator.getFormClass(value.getId())
                .then(new Function<FormClass, Void>() {

                    @Nullable
                    @Override
                    public Void apply(@Nullable FormClass formClass) {
                        FormClassDesignView.this.formClass = formClass;
                        formHeader.setValue(formClass.getLabel().getValue());
                        formPanel.setFormClass(formClass);
                        formPanel.setDesignEnabled(true);
                        return null;
                    }
                });
    }

    @Override
    public Widget asWidget() {
        return rootElement;
    }

    @UiHandler("formHeader")
    public void onChange(ValueChangeEvent<String> event){
        formClass.setLabel(new LocalizedString(event.getValue()));
        resourceLocator.persist(formClass);
    }

}