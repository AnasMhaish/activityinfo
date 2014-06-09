package org.activityinfo.core.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.Resource;
import org.activityinfo.core.shared.application.ApplicationProperties;
import org.activityinfo.core.shared.application.FolderClass;
import org.activityinfo.core.shared.criteria.Criteria;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.legacy.shared.adapter.InstanceQueryResultAdapter;

import java.util.*;

/**
 * Created by alex on 4/1/14.
 */
public class InMemResourceLocator implements ResourceLocator {

    private Map<Cuid, FormInstance> instances = Maps.newHashMap();

    private Map<Cuid, FormClass> formClasses = Maps.newHashMap();

    public InMemResourceLocator() {
        FormInstance rootFolder = new FormInstance(Cuid.create("home"), FolderClass.CLASS_ID);
        rootFolder.set(FolderClass.LABEL_FIELD_ID, "Home");
        persist(rootFolder);
    }

    @Override
    public Promise<FormClass> getFormClass(Cuid formId) {
        if(formClasses.containsKey(formId)) {
            return Promise.resolved(formClasses.get(formId));
        } else {
            return Promise.rejected(new NotFoundException());
        }
    }

    @Override
    public Promise<FormInstance> getFormInstance(Cuid formId) {
        if(instances.containsKey(formId)) {
            return Promise.resolved(instances.get(formId));
        } else {
            return Promise.rejected(new NotFoundException());
        }
    }

    @Override
    public Promise<Void> persist(Resource resource) {
        if(resource instanceof FormClass) {
            formClasses.put(resource.getId(), (FormClass) resource);

            // create a coressponding FormInstance
            FormInstance instance = new FormInstance(resource.getId(), FormClass.CLASS_ID);
            instance.setParentId(((FormClass) resource).getParentId());
            instance.set(FormClass.LABEL_FIELD_ID, ((FormClass) resource).getLabel().getValue());
            instance.set(ApplicationProperties.LABEL_PROPERTY,  ((FormClass) resource).getLabel().getValue());
            instances.put(resource.getId(), instance);
        } else {
            instances.put(resource.getId(), (FormInstance) resource);
        }
        return Promise.done();
    }

    @Override
    public Promise<Void> persist(List<? extends Resource> resources) {
        for(Resource resource : resources) {
            persist(resource);
        }
        return Promise.done();
    }

    @Override
    public Promise<Integer> countInstances(Criteria criteria) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Promise<List<FormInstance>> queryInstances(Criteria criteria) {
        List<FormInstance> matching = findMatching(criteria);
        return Promise.resolved(matching);
    }

    private List<FormInstance> findMatching(Criteria criteria) {
        List<FormInstance> matching = Lists.newArrayList();
        for(FormInstance instance : instances.values()) {
            if(criteria.apply(instance)) {
                matching.add(instance);
            }
        }
        return matching;
    }

    public Promise<QueryResult<Projection>> queryProjection(InstanceQuery query) {
        return query(query).then(new InstanceQueryResultAdapter(query));
    }

    @Override
    public Promise<List<Projection>> query(InstanceQuery query) {
        List<Projection> projections = Lists.newArrayList();
        for(FormInstance instance : findMatching(query.getCriteria())) {
            Projection projection = new Projection(instance.getId(), instance.getClassId());
            for(FieldPath path : query.getFieldPaths()) {
                projection.setValue(path, findValue(path.iterator(), instance));
            }
            projections.add(projection);
        }
        return Promise.resolved(projections);
    }

    private Object findValue(Iterator<Cuid> iterator, FormInstance instance) {
        Cuid fieldId = iterator.next();
        if(iterator.hasNext()) {
            Set<Cuid> refs = instance.getReferences(fieldId);
            if(refs.isEmpty()) {
                return null;
            } else {
                FormInstance nestedInstance = instances.get(refs.iterator().next());
                return findValue(iterator, nestedInstance);
            }
        } else {
            return instance.get(fieldId);
        }
    }

    @Override
    public Promise<Void> remove(Collection<Cuid> resources) {
        for (Cuid cuid : resources) {
            instances.remove(cuid);
            formClasses.remove(cuid);
        }
        return Promise.done();
    }
}
