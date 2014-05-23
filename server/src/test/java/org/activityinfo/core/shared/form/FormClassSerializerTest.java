package org.activityinfo.core.shared.form;

import com.google.gson.*;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.LocalizedString;
import org.activityinfo.core.shared.criteria.ClassCriteria;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class FormClassSerializerTest {

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    public void basicTest() {

        FormField quantityField = new FormField(new Cuid("number"));
        quantityField.setLabel(new LocalizedString("Choose a number"));
        quantityField.setUnit(new LocalizedString("foobars"));
        quantityField.setType(FormFieldType.QUANTITY);

        FormField referenceField = new FormField(new Cuid("ref"));
        referenceField.setType(FormFieldType.REFERENCE);
        referenceField.setLabel(new LocalizedString("location"));
        referenceField.setRange(new ClassCriteria(new Cuid("xyz")));

        FormClass formClass = new FormClass(new Cuid("abc"));
        formClass.setLabel(new LocalizedString("My form"));
        formClass.setElements(Arrays.<FormElement>asList(quantityField, referenceField));

        FormClassSerializer serializer = new FormClassSerializer();
        JsonObject instance = serializer.toJson(formClass);

        String json = gson.toJson(instance);
        System.out.println(json);

        FormClass reformClass = serializer.fromJson(json);

        assertThat(reformClass.getId(), equalTo(formClass.getId()));
        assertThat(reformClass.getLabel(), equalTo(formClass.getLabel()));
        assertThat(reformClass.getElements(), hasSize(2));

        FormField reQuantity = (FormField) reformClass.getElements().get(0);
        assertThat(reQuantity.getLabel(), Matchers.equalTo(quantityField.getLabel()));


    }

}
