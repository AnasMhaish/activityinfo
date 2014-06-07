package org.activityinfo.datamodel.shared.record;

import org.activityinfo.datamodel.shared.Cuid;

import java.util.List;

/**
 * The {@code Record} is the basic unit of (semi)structured storage.
 */
public interface Record {

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
    Double getNumber(Cuid fieldId);

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

    Record getDataRecord(Cuid fieldId);

    List<Record> getDataRecordList(Cuid fieldId);

    void set(Cuid fieldId, String value);

    void set(Cuid fieldId, double value);

    void set(Cuid fieldId, Record record);

    void set(Cuid fieldId, boolean value);
}
