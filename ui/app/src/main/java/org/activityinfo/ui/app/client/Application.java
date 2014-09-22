package org.activityinfo.ui.app.client;

import org.activityinfo.service.store.RemoteStoreService;
import org.activityinfo.ui.app.client.chrome.connectivity.ConnectivityStore;
import org.activityinfo.ui.app.client.chrome.tasks.TaskStore;
import org.activityinfo.ui.app.client.request.RequestDispatcher;
import org.activityinfo.ui.app.client.store.*;
import org.activityinfo.ui.flux.dispatcher.Dispatcher;

public class Application {

    private final Dispatcher dispatcher;

    private final Router router;

    private final ResourceStore resourceStore;
    private final FolderStore folderStore;
    private final WorkspaceStore workspaceStore;
    private final DraftStore draftStore;
    private final ConnectivityStore connectivityStore;
    private final TaskStore taskStore;

    private final RemoteStoreService remoteService;
    private final RequestDispatcher requestDispatcher;

    public Application(RemoteStoreService remoteStoreService) {
        dispatcher = new Dispatcher();

        router = new Router(dispatcher);

        workspaceStore = new WorkspaceStore(dispatcher);
        folderStore = new FolderStore(dispatcher);
        draftStore = new DraftStore(dispatcher);
        resourceStore = new ResourceStore(dispatcher);
        connectivityStore = new ConnectivityStore(dispatcher);
        taskStore = new TaskStore(dispatcher);

        remoteService = remoteStoreService;
        requestDispatcher = new RequestDispatcher(dispatcher, remoteService);
    }

    public Router getRouter() {
        return router;
    }

    public WorkspaceStore getWorkspaceStore() {
        return workspaceStore;
    }

    public ResourceStore getResourceStore() {
        return resourceStore;
    }

    public FolderStore getFolderStore() {
        return folderStore;
    }

    public DraftStore getDraftStore() {
        return draftStore;
    }

    public RemoteStoreService getRemoteService() {
        return remoteService;
    }

    public RequestDispatcher getRequestDispatcher() {
        return requestDispatcher;
    }

    public ConnectivityStore getConnectivityStore() {
        return connectivityStore;
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public TaskStore getTaskStore() {
        return taskStore;
    }
}
