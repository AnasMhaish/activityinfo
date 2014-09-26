package org.activityinfo.model.type.enumerated;

import com.google.common.collect.Lists;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormClassVisitor;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.record.Records;
import org.activityinfo.model.type.*;

import java.util.ArrayList;
import java.util.List;

public class EnumType implements ParametrizedFieldType {

    public interface EnumTypeClass extends ParametrizedFieldTypeClass, RecordFieldTypeClass<EnumFieldValue> { }

    public static final EnumTypeClass TYPE_CLASS = new EnumTypeClass() {

        @Override
        public String getId() {
            return "enumerated";
        }

        @Override
        public FieldType deserializeType(Record typeParameters) {

            Cardinality cardinality = Cardinality.valueOf(typeParameters.getString("cardinality"));

            List<EnumValue> enumValues = Lists.newArrayList();
            List<Record> enumValueRecords = typeParameters.getRecordList("values");
            for(Record record : enumValueRecords) {
                enumValues.add(EnumValue.fromRecord(record));
            }
            return new EnumType(cardinality, enumValues);
        }

        @Override
        public EnumType createType() {
            return new EnumType();
        }

        @Override
        public FormClass getParameterFormClass() {
            return new FormClass(Types.parameterFormClassId(this));
        }

        @Override
        public EnumFieldValue deserialize(Record record) {
            return EnumFieldValue.fromRecord(record);
        }
    };

    private final Cardinality cardinality;
    private final List<EnumValue> values;
    private final List<EnumValue> defaultValues = Lists.newArrayList();

    public EnumType() {
        this.cardinality = Cardinality.SINGLE;
        this.values = Lists.newArrayList();
    }

    public EnumType(Cardinality cardinality, List<EnumValue> values) {
        this.cardinality = cardinality;
        this.values = values != null ? values : new ArrayList<EnumValue>();
    }

    public Cardinality getCardinality() {
        return cardinality;
    }

    public List<EnumValue> getValues() {
        return values;
    }

    public List<EnumValue> getDefaultValues() {
        return defaultValues;
    }

    @Override
    public ParametrizedFieldTypeClass getTypeClass() {
        return TYPE_CLASS;
    }

    @Override
    public <T> T accept(FormField field, FormClassVisitor<T> visitor) {
        return visitor.visitEnumField(field, this);
    }

    @Override
    public Record getParameters() {

        List<Record> enumValueRecords = Lists.newArrayList();
        for(EnumValue enumValue : getValues()) {
            enumValueRecords.add(enumValue.asRecord());
        }

        return Records.builder()
                .set("classId", getTypeClass().getParameterFormClass().getId())
                .set("cardinality", cardinality.name())
                .set("values", enumValueRecords)
                .build();
    }

    @Override
    public boolean isValid() {
        return values.size() > 0;
    }

    @Override
    public Record asRecord() {
        return TypeFieldType.asRecord(this);
    }

}
