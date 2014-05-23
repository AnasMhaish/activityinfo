package org.activityinfo.server.forms.timeline;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.form.FormField;
import org.activityinfo.core.shared.form.FormFieldType;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.server.forms.FormClassProvider;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class TimelineBuilder {

    private final FormClassProvider formClassProvider;
    private Map<Cuid, FormInstance> instanceStates = Maps.newHashMap();
    private List<TimelineEvent> events = Lists.newArrayList();
    private Set<Cuid> references = Sets.newHashSet();

    public TimelineBuilder(FormClassProvider formClassProvider) {
        this.formClassProvider = formClassProvider;
    }

    public void addSnapshot(long timeCreated, User user, FormInstance snapshot) {

        FormInstance previous = instanceStates.get(snapshot.getId());

        if(previous == null) {
            events.add(new TimelineEvent(timeCreated, user, snapshot, TimelineEvent.Type.CREATE));

        } else {
            List<FieldChange> changes = changes(snapshot, previous);
            if(!changes.isEmpty()) {
                events.add(new TimelineEvent(timeCreated, user, snapshot, TimelineEvent.Type.UPDATE, changes));
            }
        }

        instanceStates.put(snapshot.getId(), snapshot);
    }

    private List<FieldChange> changes(FormInstance snapshot, FormInstance previous) {
        Set<Cuid> fields = Sets.newHashSet();
        fields.addAll(snapshot.getValueMap().keySet());
        fields.addAll(previous.getValueMap().keySet());

        List<FieldChange> changes = Lists.newArrayList();

        for(FormField field : formClassProvider.getFormClass(snapshot.getClassId()).getFields()) {
            Object newValue = snapshot.get(field.getId());
            Object oldValue = previous.get(field.getId());
            if(!Objects.equals(newValue, oldValue)) {
                if(field.getType() == FormFieldType.REFERENCE) {
                    references.addAll(snapshot.getReferences(field.getId()));
                    references.addAll(previous.getReferences(field.getId()));
                }
                changes.add(new FieldChange(field, oldValue, newValue));
            }
        }
        return changes;
    }

    public Set<Cuid> getReferences() {
        return references;
    }

    public List<TimelineEvent> getEvents() {
        return events;
    }

    @Override
    public String toString() {
        return Joiner.on("\n").join(events);
    }
}
