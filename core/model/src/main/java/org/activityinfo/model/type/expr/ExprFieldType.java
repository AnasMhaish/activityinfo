package org.activityinfo.model.type.expr;

import org.activityinfo.model.form.FormClassVisitor;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.RecordFieldTypeClass;
import org.activityinfo.model.type.TypeFieldType;

/**
 * Value type that represents an expression
 */
public class ExprFieldType implements FieldType {

    public interface TypeClass extends RecordFieldTypeClass<ExprValue> {
        ExprValue deserialize(Record record);
    }

    public static final TypeClass TYPE_CLASS = new TypeClass() {
        @Override
        public String getId() {
            return "expr";
        }

        @Override
        public String getLabel() {
            return "Expression";
        }

        @Override
        public FieldType createType() {
            return INSTANCE;
        }

        @Override
        public ExprValue deserialize(Record record) {
            return ExprValue.fromRecord(record);
        }
    };

    public static final ExprFieldType INSTANCE = new ExprFieldType();


    private ExprFieldType() {
    }


    @Override
    public FieldTypeClass getTypeClass() {
        return TYPE_CLASS;
    }

    @Override
    public <T> T accept(FormField field, FormClassVisitor<T> visitor) {
        return visitor.visitExprField(field, this);
    }

    @Override
    public Record asRecord() {
        return TypeFieldType.asRecord(this);
    }

}
