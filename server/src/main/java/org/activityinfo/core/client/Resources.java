package org.activityinfo.core.client;

import com.google.common.base.Function;
import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.fp.client.Promise;

import java.util.List;

/**
 * Functional forms of the {@code ResourceLocator} methods
 */
public class Resources {

    public final ResourceLocator resourceLocator;

    public Resources(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
    }

    public Function<FormInstance, Promise<Void>> persist() {
        return new Function<FormInstance, Promise<Void>>() {
            @Override
            public Promise<Void> apply(FormInstance formInstance) {
                return resourceLocator.persist(formInstance);
            }
        };
    }

    public Function<InstanceQuery, Promise<List<Projection>>> query() {
        return new Function<InstanceQuery, Promise<List<Projection>>>() {

            @Override
            public Promise<List<Projection>> apply(InstanceQuery instanceQuery) {
                return resourceLocator.query(instanceQuery);
            }
        };
    }

    public Function<Cuid, Promise<FormInstance>> fetchInstance() {
        return new Function<Cuid, Promise<FormInstance>>() {
            @Override
            public Promise<FormInstance> apply(Cuid input) {
                return resourceLocator.getFormInstance(input);
            }
        };
    }

    public Promise<List<Projection>> query(InstanceQuery query) {
        return resourceLocator.query(query);
    }

}
