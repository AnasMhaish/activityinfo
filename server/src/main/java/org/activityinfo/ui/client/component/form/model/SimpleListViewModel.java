package org.activityinfo.ui.client.component.form.model;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.activityinfo.core.client.InMemResourceLocator;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.form.FormField;
import org.activityinfo.core.shared.form.FormFieldCardinality;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.core.shared.form.tree.FormTree;
import org.activityinfo.fp.client.Promise;

import java.util.List;

/**
 * View model of a reference field's range
 */
public class SimpleListViewModel implements FieldViewModel {

    private int count;
    private List<FormInstance> instances;
    private FormFieldCardinality cardinality;

    public SimpleListViewModel(FormFieldCardinality cardinality, List<FormInstance> instances) {
        this.cardinality = cardinality;
        this.instances = instances;
        this.count = instances.size();
    }

    @Override
    public Cuid getFieldId() {
        return null;
    }

    public int getCount() {
        return count;
    }

    public List<FormInstance> getInstances() {
        return instances;
    }

    public FormFieldCardinality getCardinality() {
        return cardinality;
    }

    public static Promise<FieldViewModel> build(ResourceLocator resourceLocator, final FormTree.Node node) {
        return resourceLocator
        .queryInstances(node.getRange())
        .then(new Function<List<FormInstance>, FieldViewModel>() {
            @Override
            public FieldViewModel apply(List<FormInstance> input) {
                return new SimpleListViewModel(node.getField().getCardinality(), input);
            }
        });
    }

    @Override
    public String toString() {
        return "RangeViewModel{" +
                "count=" + count +
                ", instances=" + instances +
                ", cardinality=" + cardinality +
                '}';
    }
}

