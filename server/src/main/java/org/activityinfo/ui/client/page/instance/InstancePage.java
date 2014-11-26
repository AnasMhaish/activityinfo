package org.activityinfo.ui.client.page.instance;

import com.google.common.base.Function;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Provider;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.client.component.formdesigner.FormSavedGuard;
import org.activityinfo.ui.client.page.*;
import org.activityinfo.ui.client.pageView.InstancePageViewFactory;
import org.activityinfo.ui.client.pageView.InstanceViewModel;
import org.activityinfo.ui.client.style.Icons;
import org.activityinfo.ui.client.widget.LoadingPanel;
import org.activityinfo.ui.client.widget.loading.PageLoadingPanel;

import javax.annotation.Nullable;

/**
 * Adapter that hosts a view of a given instance.
 */
public class InstancePage implements Page {

    public static final PageId PAGE_ID = new PageId("i");

    // scrollpanel.bs > div.container > loadingPanel
    private final ScrollPanel scrollPanel;
    private final SimplePanel container;
    private final LoadingPanel<InstanceViewModel> loadingPanel;

    private final ResourceLocator locator;

    public InstancePage(ResourceLocator resourceLocator) {
        this.locator = resourceLocator;

        Icons.INSTANCE.ensureInjected();

        this.loadingPanel = new LoadingPanel<>(new PageLoadingPanel());

        this.container = new SimplePanel(loadingPanel.asWidget());
        this.container.addStyleName("container");

        this.scrollPanel = new ScrollPanel(container);
        this.scrollPanel.addStyleName("bs");
    }

    @Override
    public PageId getPageId() {
        return PAGE_ID;
    }

    @Override
    public Object getWidget() {
        return scrollPanel;
    }

    @Override
    public void requestToNavigateAway(PageState place, NavigationCallback callback) {
        FormSavedGuard.callNavigationCallback(scrollPanel, callback);
    }

    @Override
    public String beforeWindowCloses() {
        return null;
    }

    @Override
    public boolean navigate(PageState place) {
        final InstancePlace instancePlace = (InstancePlace) place;
        this.loadingPanel.setDisplayWidgetProvider(
                new InstancePageViewFactory(locator));
        this.loadingPanel.show(new Provider<Promise<InstanceViewModel>>() {
            @Override
            public Promise<InstanceViewModel> get() {
                return locator.getFormInstance(instancePlace.getInstanceId())
                        .then(new Function<FormInstance, InstanceViewModel>() {
                            @Nullable
                            @Override
                            public InstanceViewModel apply(@Nullable FormInstance input) {
                                return new InstanceViewModel(input, instancePlace.getView());
                            }
                        });
            }
        });
        return true;
    }

    @Override
    public void shutdown() {

    }
}
