package org.activityinfo.legacy.shared.command;

import org.activityinfo.legacy.shared.model.ActivityDTO;

/**
 * Retrieves a ViewModel for a FormClass (ActivityDTO for now)
 *
 * The ViewModel is a combination of the FormClass instance along
 * with related instances and classes necessary to render a
 * form for the user.
 *
 */
public class GetFormViewModel implements Command<ActivityDTO> {

    private int activityId;

    public GetFormViewModel(int activityId) {
        this.activityId = activityId;
    }

    public GetFormViewModel() {
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }
}
