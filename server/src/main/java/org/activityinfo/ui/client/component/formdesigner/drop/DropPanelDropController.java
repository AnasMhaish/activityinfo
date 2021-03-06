package org.activityinfo.ui.client.component.formdesigner.drop;
/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.model.form.FormElement;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.form.FormSection;
import org.activityinfo.ui.client.component.form.field.FormFieldWidget;
import org.activityinfo.ui.client.component.formdesigner.FormDesigner;
import org.activityinfo.ui.client.component.formdesigner.container.FieldWidgetContainer;
import org.activityinfo.ui.client.component.formdesigner.container.SectionWidgetContainer;
import org.activityinfo.ui.client.component.formdesigner.container.WidgetContainer;
import org.activityinfo.ui.client.component.formdesigner.event.PanelUpdatedEvent;
import org.activityinfo.ui.client.component.formdesigner.palette.DnDLabel;
import org.activityinfo.ui.client.component.formdesigner.palette.FieldTemplate;
import org.activityinfo.ui.client.component.formdesigner.palette.SectionTemplate;
import org.activityinfo.ui.client.component.formdesigner.palette.Template;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author yuriyz on 07/07/2014.
 */
public class DropPanelDropController extends FlowPanelDropController implements DropControllerExtended {

    private final Positioner positioner = new Positioner();
    private FormDesigner formDesigner;
    private FlowPanel dropTarget;

    public DropPanelDropController(FlowPanel dropTarget, FormDesigner formDesigner) {
        super(dropTarget);
        this.formDesigner = formDesigner;
        this.dropTarget = dropTarget;
    }

    @Override
    public void onPreviewDrop(DragContext context) throws VetoDragException {
        super.onPreviewDrop(context); // important ! - calculates drop index

        if (context.draggable instanceof DnDLabel) {
            previewDropNewWidget(context);
        } else {
            drop(context.draggable, context);

            Scheduler.get().scheduleDeferred(new Command() {
                @Override
                public void execute() {
                    formDesigner.updateFieldOrder();
                    removePositioner();
                }
            });
        }
    }

    private void previewDropNewWidget(final DragContext context) throws VetoDragException {
        final Template template = ((DnDLabel) context.draggable).getTemplate();
        if (template instanceof FieldTemplate) {
            final FormField formField = ((FieldTemplate)template).create();

            formDesigner.getFormFieldWidgetFactory().createWidget(formDesigner.getFormClass(), formField, NullValueUpdater.INSTANCE).then(new Function<FormFieldWidget, Void>() {
                @Nullable
                @Override
                public Void apply(@Nullable FormFieldWidget formFieldWidget) {
                    final FieldWidgetContainer fieldWidgetContainer = new FieldWidgetContainer(formDesigner, formFieldWidget, formField);

                    drop(fieldWidgetContainer, context, formField);

                    return null;
                }
            });
        } else if (template instanceof SectionTemplate) {
            final FormSection formSection = ((SectionTemplate)template).create();

            SectionWidgetContainer widgetContainer = new SectionWidgetContainer(formDesigner, formSection);
            drop(widgetContainer, context, formSection);
        }


        // forbid drop of source control widget
        throw new VetoDragException();
    }

    private void drop(final Widget widget, DragContext context) {
        // hack ! - replace original selected widget with our container, drop it and then restore selection
        final List<Widget> originalSelectedWidgets = context.selectedWidgets;
        context.selectedWidgets = Lists.newArrayList(widget);
        DropPanelDropController.super.onDrop(context); // drop container
        context.selectedWidgets = originalSelectedWidgets; // restore state;

        formDesigner.getSavedGuard().setSaved(false);
    }

    private void drop(final WidgetContainer widgetContainer, DragContext context, final FormElement formElement) {
        drop(widgetContainer.asWidget(), context);

        formDesigner.getEventBus().fireEvent(new PanelUpdatedEvent(widgetContainer, PanelUpdatedEvent.EventType.ADDED));
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                int widgetIndex = dropTarget.getWidgetIndex(widgetContainer.asWidget());

                // update model
                formDesigner.getFormClass().insertElement(widgetIndex, formElement);
                formDesigner.getDragController().makeDraggable(widgetContainer.asWidget(), widgetContainer.getDragHandle());

                removePositioner();
            }
        });
    }

    @Override
    protected Widget newPositioner(DragContext context) {
        return positioner.asWidget();
    }

    @Override
    public void setPositionerToEnd() {
        removePositioner();
        dropTarget.insert(positioner, (dropTarget.getWidgetCount()));
    }

    private void removePositioner() {
        int currentIndex = dropTarget.getWidgetIndex(positioner);
        if (currentIndex != -1) {
            dropTarget.remove(currentIndex);
        }
    }
}
