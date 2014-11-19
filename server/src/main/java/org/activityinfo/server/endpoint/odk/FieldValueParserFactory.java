package org.activityinfo.server.endpoint.odk;

import com.google.inject.Inject;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.NarrativeType;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.barcode.BarcodeType;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.geo.GeoPointType;
import org.activityinfo.model.type.image.ImageType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.BooleanType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.model.type.time.LocalDateType;
import org.activityinfo.server.command.ResourceLocatorSync;

public class FieldValueParserFactory {
    final private ResourceLocatorSync table;

    @Inject
    public FieldValueParserFactory(ResourceLocatorSync table) {
        this.table = table;
    }

    public FieldValueParser fromFieldType(FieldType fieldType) {
        if (fieldType instanceof BarcodeType) return new BarcodeFieldValueParser();
        if (fieldType instanceof BooleanType) return new BooleanFieldValueParser();
        if (fieldType instanceof EnumType) return new EnumFieldValueParser((EnumType) fieldType);
        if (fieldType instanceof GeoPointType) return new GeoPointFieldValueParser();
        if (fieldType instanceof ImageType) return new ImageFieldValueParser();
        if (fieldType instanceof LocalDateType) return new LocalDateFieldValueParser();
        if (fieldType instanceof NarrativeType) return new NarrativeFieldValueParser();
        if (fieldType instanceof QuantityType) return new QuantityFieldValueParser((QuantityType) fieldType);
        if (fieldType instanceof ReferenceType) return new ReferenceFieldValueParser();
        if (fieldType instanceof TextType) return new TextFieldValueParser();

        // If this happens, it means this class needs to be expanded to support the new FieldType class.
        throw new IllegalArgumentException("Unknown FieldType passed to FieldValueParserFactory.get()");
    }
}
