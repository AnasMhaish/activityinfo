package org.activityinfo.server.endpoint.odk;

import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.Cardinality;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.enumerated.EnumFieldValue;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.enumerated.EnumValue;

import java.util.List;
import java.util.Map;

class CodeEnumFieldValueParser implements FieldValueParser {
    final private Cardinality cardinality;
    final private Map<String, EnumValue> values;

    CodeEnumFieldValueParser(EnumType enumType) {
        this.cardinality = enumType.getCardinality();
        values = Maps.newHashMapWithExpectedSize(enumType.getValues().size());

        for (EnumValue value : enumType.getValues()) {
            values.put(value.getCode(), value);
        }
    }

    @Override
    public FieldValue parse(String text) {
        if (text == null) throw new IllegalArgumentException("Malformed Element passed to OdkFieldValueParser.parse()");

        switch (cardinality) {
            default:
                String selected[] = text.split(" ");
                List<ResourceId> resourceIds = Lists.newArrayListWithCapacity(selected.length);

                for (String item : selected) {
                    EnumValue enumValue = values.get(item);
                    if (enumValue != null) resourceIds.add(enumValue.getId());
                }

                return new EnumFieldValue(resourceIds);

            case SINGLE:
                return values.get(text);
        }
    }
}
