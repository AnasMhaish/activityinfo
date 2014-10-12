package org.activityinfo.io.odk;

import org.activityinfo.model.type.Cardinality;
import org.activityinfo.io.odk.xform.Item;
import org.activityinfo.io.odk.xform.PresentationElement;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.List;

import static org.activityinfo.model.type.Cardinality.SINGLE;

class SelectBuilder implements OdkFormFieldBuilder {
    final private String modelBindType;
    final private Cardinality cardinality;
    final private List<Item> item;

    SelectBuilder(String modelBindType, SelectOptions selectOptions) {
        this.modelBindType = modelBindType;
        this.cardinality = selectOptions.getCardinality();
        this.item = selectOptions.getItem();
    }

    @Override
    public String getModelBindType() {
        return modelBindType;
    }

    @Override
    public JAXBElement<PresentationElement> createPresentationElement(String ref, String label, String hint) {
        PresentationElement presentationElement = new PresentationElement();

        presentationElement.ref = ref;
        presentationElement.label = label;
        presentationElement.item = item;
        presentationElement.hint = hint;

        QName qName = new QName("http://www.w3.org/2002/xforms", SINGLE.equals(cardinality) ? "select1" : "select");
        return new JAXBElement<>(qName, PresentationElement.class, presentationElement);
    }
}
