package org.activityinfo.datamodel.shared.record;

import org.activityinfo.datamodel.shared.Cuid;

/**
 * The {@code Record} is the basic unit of (semi)structured storage.
 */
public interface Record {

    /**
     * Returns true if this Record has a value for this
     * field.
     */
    boolean has(Cuid fieldId);

    /**
     *
     * @return the {@code FieldType} of the given field.
     */
    FieldType getFieldType(Cuid fieldId);

    /**
     * Returns the value of the field in this {@code Record} as a Java object
     * of one of the following classes:
     * <ul>
     *     <li>{@code java.lang.String}</li>
     *     <li>{@code java.lang.Double}</li>
     *     <li>{@code java.lang.Boolean}</li>
     *     <li>{@code org.activityinfo.datamodel.shared.record.Record}</li>
     * </ul>
     *
     * or {@code null} if this field has no value in this {@code Record}
     *
     */
    Object get(Cuid fieldId);

    /**
     * Returns the value of the field if the field has a string value,
     * otherwise {@code null}
     */
    String getString(Cuid fieldId);

    /**
     * Returns:
     * <ul>
     *     <li>the value of the field if the field has numeric value</li>
     *     <li>1 if the value has a boolean value of {@code true}, and
     *        0 if the field has a boolean value of {@code false}</li>
     *     <li>otherwise {@code null}</li>
     * </ul>
     */
    Double getDouble(Cuid fieldId);

    /**
     * Returns:
     * <ul>
     *     <li>the value of the field if the field has a boolean value</li>
     *     <li>{@code false} if the field has a numerical value equal to zero</li>
     *     <li>{@code true} if the field has a numerical value not equal to zero</li>
     *     <li>otherwise {@code null}</li>
     * </ul>
     */
    Boolean getBoolean(Cuid fieldId);

    Record getRecord(Cuid fieldId);

    void set(Cuid fieldId, String value);

    void set(Cuid fieldId, double value);

    void set(Cuid fieldId, Record record);

    void set(Cuid fieldId, boolean value);
}
