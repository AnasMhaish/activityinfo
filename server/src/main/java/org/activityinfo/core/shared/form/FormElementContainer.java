package org.activityinfo.core.shared.form;


import java.io.Serializable;
import java.util.List;

public interface FormElementContainer {

    List<FormElement> getElements();

    void addElement(FormElement element);
}
