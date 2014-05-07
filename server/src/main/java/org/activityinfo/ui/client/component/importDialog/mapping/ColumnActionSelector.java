package org.activityinfo.ui.client.component.importDialog.mapping;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ScrollPanel;
import org.activityinfo.core.shared.form.FormField;
import org.activityinfo.core.shared.importing.model.ColumnAction;
import org.activityinfo.core.shared.importing.model.IgnoreAction;
import org.activityinfo.core.shared.importing.model.MapExistingAction;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.ui.client.widget.RadioButton;

import java.util.List;
import java.util.Map;

/**
 * A simple panel that provides the user with a list of possible properties
 * to which to map the column as a list of radioButton buttons.
 */
public class ColumnActionSelector extends Composite implements HasValue<ColumnAction> {

    public interface Template extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("<span class='mandatory'> * </span> " +
                "{0}")
        SafeHtml html(String label);
    }

    private static final Template TEMPLATE = GWT.create(Template.class);

    private static int nextUniqueGroupNum = 1;

    private String group = "columnBinding" + (nextUniqueGroupNum++);

    private Map<ColumnAction, RadioButton> buttons = Maps.newHashMap();
    private ColumnAction value = IgnoreAction.INSTANCE;
    private final RadioButton ignoreButton;


    public ColumnActionSelector(List<MapExistingAction> actions) {

        FlowPanel panel = new FlowPanel();

        ignoreButton = createRadioButton(SafeHtmlUtils.fromString(I18N.CONSTANTS.ignoreColumnAction()), IgnoreAction.INSTANCE);
        ignoreButton.addStyleName(ColumnMappingStyles.INSTANCE.stateIgnored());
        ignoreButton.setValue(true);
        panel.add(ignoreButton);

        for(final MapExistingAction action : actions) {
            final FormField formField = action.getTarget().getFormField();
            final String plainLabel = action.getTarget().getLabel();
            SafeHtml label = !formField.isRequired() ?
                    SafeHtmlUtils.fromString(plainLabel) :
                    TEMPLATE.html(plainLabel);
            RadioButton button = createRadioButton(label, action);
            panel.add(button);
        }

        initWidget(new ScrollPanel(panel));
    }

    private RadioButton createRadioButton(SafeHtml label, final ColumnAction action) {
        RadioButton button = new RadioButton(group, label);
        button.setTabIndex(buttons.size() + 1);

        if(action == IgnoreAction.INSTANCE) {
            button.addStyleName(ColumnMappingStyles.INSTANCE.stateIgnored());
        }

        button.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if (!Objects.equal(action, value)) {
                    value = action;
                    ValueChangeEvent.fire(ColumnActionSelector.this, value);
                }
            }
        });
        buttons.put(action, button);
        return button;
    }


    public void setFocus() {
        if(ignoreButton.getValue()) {
            ignoreButton.setFocus(true);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(
            ValueChangeHandler<ColumnAction> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public ColumnAction getValue() {
        return value;
    }

    @Override
    public void setValue(ColumnAction value) {
        setValue(value, true);
    }

    @Override
    public void setValue(ColumnAction newValue, boolean fireEvents) {
        if (!Objects.equal(this.value, newValue)) {

            if(newValue == null) {
                // clear the old selection
                buttons.get(value).setValue(false);
                this.value = newValue;

            } else {
                this.value = newValue;

                // update the UI
                RadioButton button = buttons.get(newValue);
                assert button != null : "No button for " + newValue;
                button.setValue(true);

                // update our internal value
                buttons.get(newValue).setValue(true);
            }

            // notify listeners
            if (fireEvents) {
                ValueChangeEvent.fire(this, value);
            }
        }
    }

}
