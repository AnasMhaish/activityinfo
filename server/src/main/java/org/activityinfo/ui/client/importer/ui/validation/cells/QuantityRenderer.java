package org.activityinfo.ui.client.importer.ui.validation.cells;

import com.google.common.base.Function;
import org.activityinfo.core.shared.formatter.QuantityFormatter;

import javax.annotation.Nullable;

/**
 * Renders a QUANTITY field value to SafeHtml
 */
public class QuantityRenderer implements Function<Object, String> {

    private QuantityFormatter formatter;

    public QuantityRenderer(QuantityFormatter formatter) {
        this.formatter = formatter;
    }

    @Nullable
    @Override
    public String apply(@Nullable Object input) {
        return input == null ? null : formatter.format((Double)input);
    }
}
