package org.activityinfo.ui.client.component.form.field.hierarchy;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.ui.client.component.form.field.ReferenceFieldWidget;
import org.activityinfo.ui.client.component.form.model.HierarchyViewModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Widget for Reference fields which presents multi-level combo boxes
 *
 */
public class HierarchyFieldWidget implements ReferenceFieldWidget {

    private final FlowPanel panel;
    private final Map<Cuid, LevelView> widgets = new HashMap<>();
    private final Presenter presenter;

    public HierarchyFieldWidget(ResourceLocator locator, HierarchyViewModel viewModel, ValueUpdater valueUpdater) {

        this.panel = new FlowPanel();
        for(Level level : viewModel.getTree().getLevels()) {
            LevelWidget widget = new LevelWidget(level.getLabel());
            widgets.put(level.getClassId(), widget);
            this.panel.add(widget);
        }

        this.presenter = new Presenter(locator, viewModel.getTree(), widgets, valueUpdater);
        this.presenter.setInitialSelection(viewModel.getSelection());
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        for(LevelView widget : widgets.values()) {
            widget.setReadOnly(readOnly);
        }
    }

    @Override
    public void setValue(Set<Cuid> value) {

    }

    @Override
    public Widget asWidget() {
        return panel;
    }

}

