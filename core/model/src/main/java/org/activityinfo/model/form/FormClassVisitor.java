package org.activityinfo.model.form;

import org.activityinfo.model.type.NarrativeType;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.barcode.BarcodeType;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.expr.CalculatedFieldType;
import org.activityinfo.model.type.expr.ExprFieldType;
import org.activityinfo.model.type.geo.GeoPointType;
import org.activityinfo.model.type.image.ImageType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.BooleanType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.model.type.time.*;

public interface FormClassVisitor<T> {

    T visitTextField(FormField field, TextType type);

    T visitQuantityField(FormField field, QuantityType type);

    T visitReferenceField(FormField field, ReferenceType type);

    T visitEnumField(FormField field, EnumType type);

    T visitBarcodeField(FormField field, BarcodeType type);

    T visitBooleanField(FormField field, BooleanType type);

    T visitCalculatedField(FormField field, CalculatedFieldType type);

    T visitGeoPointField(FormField field, GeoPointType type);

    T visitImageField(FormField field, ImageType type);

    T visitLocalDateIntervalField(FormField field, LocalDateIntervalType type);

    T visitExprField(FormField field, ExprFieldType type);

    T visitLocalDateField(FormField field, LocalDateType localDateType);

    T visitNarrativeField(FormField field, NarrativeType narrativeType);

    T visitMonthField(FormField field, MonthType monthType);

    T visitYearField(FormField field, YearType yearType);

    T visitMissingField(FormField field, MissingFieldType missingFieldType);

}
