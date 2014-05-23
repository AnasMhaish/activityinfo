package org.activityinfo.server.forms.history;

import com.google.common.base.Joiner;
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
    private Type type;
    private List<FieldChange> changes;

    public TimelineEvent(long time, User user, Type type, List<FieldChange> changes) {
        this.time = time;
        this.user = user;
        this.type = type;
        this.changes = changes;
    }

    public TimelineEvent(long time, User user, Type type) {
        this.type = type;
        this.user = user;
        this.time = time;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(user.getEmail() + " at " + new Date(time) + " " + type);
        if(changes != null) {
            sb.append("\n");
            Joiner.on("\n").appendTo(sb, changes);
        }
        return sb.toString();
    }
}
