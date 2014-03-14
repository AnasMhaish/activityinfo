package org.activityinfo.ui.client.widget;

import com.google.gwt.uibinder.client.UiConstructor;

/**
 * Subclass of {@link RadioButton} that applies our application styles
 */
public class RadioButton extends com.google.gwt.user.client.ui.RadioButton {

    @UiConstructor
    public RadioButton(String name) {
        super(name);
        setStyleName("radio");
    }

    public RadioButton(String name, String label) {
        super(name, label);
        setStyleName("radio");
    }
}
