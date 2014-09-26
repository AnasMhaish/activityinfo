package org.activityinfo.model.type.expr;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormClassVisitor;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.record.Records;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.*;

/**
 * A Value Type that represents a value calculated from a symbolic expression,
 * such as "A + B"
 */
public class CalculatedFieldType implements ParametrizedFieldType {

    public static final ParametrizedFieldTypeClass TYPE_CLASS = new ParametrizedFieldTypeClass() {
        @Override
        public String getId() {
            return "calculated";
        }

        @Override
        public FieldType createType() {
            return new CalculatedFieldType();
        }

        @Override
        public FieldType deserializeType(Record parameters) {
            return new CalculatedFieldType(parameters.isString("expression"));
        }

        @Override
        public FormClass getParameterFormClass() {

            FormField exprField = new FormField(ResourceId.valueOf("expression"));
            exprField.setLabel("Expression");
            exprField.setDescription("Set expression if you would like to calculate field value dynamically (otherwise leave blank). Example: {A}+{B}+({C}/{D})");
            exprField.setType(ExprFieldType.INSTANCE);

            FormClass formClass = new FormClass(Types.parameterFormClassId(this));
            formClass.addElement(exprField);

            return formClass;
        }
    };

    private String expression;

    public CalculatedFieldType() {
    }

    public CalculatedFieldType(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public ParametrizedFieldTypeClass getTypeClass() {
        return TYPE_CLASS;
    }

    @Override
    public <T> T accept(FormField field, FormClassVisitor<T> visitor) {
        return visitor.visitCalculatedField(field, this);
    }

    @Override
    public Record getParameters() {
        return Records.builder(getTypeClass())
                .set("expression", expression)
                .build();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Record asRecord() {
        return TypeFieldType.asRecord(this);
    }

}
