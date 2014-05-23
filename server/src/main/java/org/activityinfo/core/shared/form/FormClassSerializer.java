package org.activityinfo.core.shared.form;

import com.google.common.collect.Sets;
import com.google.gson.*;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.LocalizedString;
import org.activityinfo.core.shared.criteria.ClassCriteria;
import org.activityinfo.core.shared.criteria.Criteria;
import org.activityinfo.core.shared.criteria.CriteriaUnion;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Se/deserializes FormClasses to JSON
 */
public class FormClassSerializer {

    private JsonParser parser = new JsonParser();

    public JsonObject toJson(FormClass formClass) {
        JsonObject instance = new JsonObject();
        instance.addProperty("id", formClass.getId().asString());
        instance.addProperty("class", FormClass.CLASS_ID.asString());
        if(formClass.getParentId() != null) {
            instance.addProperty("parent", formClass.getParentId().asString());
        }

        if(formClass.getLabel() != null) {
            instance.addProperty("label", formClass.getLabel().getValue());
        }
        instance.add("elements", toJson(formClass.getElements()));

        return instance;
    }

    public FormClass fromJson(JsonObject instance) {
        FormClass formClass = new FormClass(new Cuid(instance.get("id").getAsString()));
        formClass.setLabel(new LocalizedString(instance.get("label").getAsString()));
        if(instance.has("parent")) {
            formClass.setParentId(new Cuid(instance.get("parent").getAsString()));
        }
        formClass.setElements(parseArray(instance.getAsJsonArray("elements")));
        return formClass;
    }

    public FormClass fromJson(String json) {
        return fromJson((JsonObject)parser.parse(json));
    }

    private List parseArray(JsonArray elements) {
        List list = new ArrayList();
        for(int i=0;i!=elements.size();++i) {
            JsonObject instance = elements.get(i).getAsJsonObject();
            if(instance.get("class").getAsString().equals(FormField.CLASS_ID.asString())) {
                list.add(parseField(instance));
            } else {
                throw new UnsupportedOperationException();
            }
        }
        return list;
    }


    private JsonElement toJson(List<FormElement> elements) {
        JsonArray array = new JsonArray();
        for(FormElement element : elements) {
            if(element instanceof FormField) {
                array.add(toJson((FormField)element));
            }
        }
        return array;
    }

    private JsonElement toJson(FormField field) {
        JsonObject instance = new JsonObject();
        instance.addProperty("id", field.getId().asString());
        instance.addProperty("class", FormField.CLASS_ID.asString());
        instance.addProperty("label", field.getLabel().getValue());
        instance.addProperty("description", field.getDescription().getValue());
        instance.addProperty("type", field.getType().name());
        if(field.getType() == FormFieldType.QUANTITY) {
            instance.addProperty("unit", field.getUnit().getValue());

        } else if(field.getType() == FormFieldType.REFERENCE) {
            instance.add("range", toJson(field.getRange()));
            if(field.getCardinality() != null) {
                instance.addProperty("cardinality", field.getCardinality().name());
            }
        }
        return instance;
    }



    private FormField parseField(JsonObject instance) {
        FormField field = new FormField(new Cuid(instance.get("id").getAsString()));
        field.setLabel(new LocalizedString(instance.get("label").getAsString()));
        field.setDescription(new LocalizedString(instance.get("description").getAsString()));
        field.setType(FormFieldType.valueOf(instance.get("type").getAsString()));

        if(field.getType() == FormFieldType.QUANTITY) {
            field.setUnit(new LocalizedString(instance.get("unit").getAsString()));

        } else if(field.getType() == FormFieldType.REFERENCE) {
            field.setRange(parseRange(instance.get("range")));
            if(instance.has("cardinality")) {
                field.setCardinality(FormFieldCardinality.valueOf(instance.get("cardinality").getAsString()));
            }
        }
        return field;
    }

    private Criteria parseRange(JsonElement range) {
        JsonArray array = range.getAsJsonArray();
        Set<Cuid> classIds = Sets.newHashSet();
        for(int i = 0;i!=array.size();++i) {
            classIds.add(new Cuid(array.get(i).getAsString()));
        }
        return ClassCriteria.union(classIds);
    }

    private JsonArray toJson(Criteria range) {
        JsonArray classes = new JsonArray();
        if(range instanceof ClassCriteria) {
            classes.add(new JsonPrimitive(((ClassCriteria) range).getClassId().asString()));
        } else if(range instanceof CriteriaUnion) {
            CriteriaUnion union = (CriteriaUnion)range;
            for(Criteria criteria : union.getElements()) {
                if(criteria instanceof ClassCriteria) {
                    classes.add(new JsonPrimitive(((ClassCriteria) criteria).getClassId().asString()));
                }
            }
        }
        return classes;
    }
}
