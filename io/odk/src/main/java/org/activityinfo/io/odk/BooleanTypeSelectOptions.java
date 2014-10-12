package org.activityinfo.io.odk;

import com.google.common.collect.Lists;
import org.activityinfo.model.type.Cardinality;
import org.activityinfo.io.odk.xform.Item;

import java.util.List;

class BooleanTypeSelectOptions implements SelectOptions {
    final private List<Item> item;

    BooleanTypeSelectOptions() {
        Item no = new Item();
        no.label = "no";
        no.value = "FALSE";
        Item yes = new Item();
        yes.label = "yes";
        yes.value = "TRUE";
        item = Lists.newArrayList(yes, no);
    }

    @Override
    public Cardinality getCardinality() {
        return Cardinality.SINGLE;
    }

    @Override
    public List<Item> getItem() {
        return item;
    }
}
