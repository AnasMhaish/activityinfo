package org.activityinfo.server.forms.timeline;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.server.database.hibernate.entity.User;

import java.util.Date;
import java.util.List;

public class TimelineEvent {


    public enum Type {
        CREATE,
        UPDATE
    }

    private long time;
    private User user;
    private FormInstance instance;
    private Type type;
    private List<FieldChange> changes;

    public TimelineEvent(long time, User user, FormInstance instance, Type type, List<FieldChange> changes) {
        this.time = time;
        this.user = user;
        this.instance = instance;
        this.type = type;
        this.changes = changes;
    }

    public TimelineEvent(long time, User user, FormInstance instance, Type type) {
        this.instance = instance;
        this.type = type;
        this.user = user;
        this.time = time;
        this.changes = Lists.newArrayList();
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<FieldChange> getChanges() {
        return changes;
    }

    public void setChanges(List<FieldChange> changes) {
        this.changes = changes;
    }

    public FormInstance getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(user.getEmail() + " at " + new Date(time) + " " + type + " " + instance.getId());
        if(changes != null) {
            sb.append("\n");
            Joiner.on("\n").appendTo(sb, changes);
        }
        return sb.toString();
    }
}
