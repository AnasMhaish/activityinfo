package org.sigmah.client.page.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.client.AppEvents;
import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.UIConstants;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.PageId;
import org.sigmah.client.page.PageState;
import org.sigmah.client.page.common.grid.AbstractEditorGridPresenter;
import org.sigmah.client.page.common.grid.TreeGridView;
import org.sigmah.client.page.common.nav.Link;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.util.state.StateProvider;
import org.sigmah.shared.command.BatchCommand;
import org.sigmah.shared.command.Command;
import org.sigmah.shared.command.Delete;
import org.sigmah.shared.command.UpdateEntity;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.AttributeDTO;
import org.sigmah.shared.dto.AttributeGroupDTO;
import org.sigmah.shared.dto.EntityDTO;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.TargetDTO;
import org.sigmah.shared.dto.UserDatabaseDTO;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;

public class TargetIndicatorPresenter extends AbstractEditorGridPresenter<ModelData> {

	@ImplementedBy(TargetIndicatorView.class)
	public interface View extends TreeGridView<TargetIndicatorPresenter, ModelData> {
		public void init(TargetIndicatorPresenter presenter, UserDatabaseDTO db, TreeStore store);
	}

	private final EventBus eventBus;
	private final Dispatcher service;
	private final View view;
	private final UIConstants messages;
	private TargetDTO targetDTO;

	private UserDatabaseDTO db;
	private TreeStore<ModelData> treeStore;

	@Inject
	public TargetIndicatorPresenter(EventBus eventBus, Dispatcher service,
			StateProvider stateMgr, View view, UIConstants messages) {
		super(eventBus, service, stateMgr, view);
		this.eventBus = eventBus;
		this.service = service;
		this.view = view;
		this.messages = messages;
	}

	public void go(UserDatabaseDTO db) {

		this.db = db;

		treeStore = new TreeStore<ModelData>();
		fillStore(messages);
		initListeners(treeStore, null);

		this.view.init(this, db, treeStore);
		this.view.setActionEnabled(UIActions.delete, false);
	}

	public void load(TargetDTO targetDTO) {
		this.targetDTO = targetDTO;
//		TODO should re fill store on some events eg selection change.
	}

	
	private void fillStore(UIConstants messages) {

		Map<String, Link> categories = new HashMap<String, Link>();
		for (ActivityDTO activity : db.getActivities()) {

			if (activity.getCategory() != null) {
				Link actCategoryLink = categories.get(activity.getCategory());

				if (actCategoryLink == null) {
					
					actCategoryLink =createCategoryLink(activity, categories);
					categories.put(activity.getCategory(), actCategoryLink);
					treeStore.add(actCategoryLink, false);
				}

				Link activityLink = createActivityLink(activity);
				treeStore.add(actCategoryLink, activityLink, false);

				addIndicatorLinks(activity, actCategoryLink);
				
			} else {
				Link activityLink = createActivityLink(activity);
				treeStore.add(activityLink, false);
				addIndicatorLinks(activity, activityLink);
			}

		}
	}
	
	private void addIndicatorLinks(ActivityDTO activity, ModelData parent){
		Map<String, Link> indicatorCategories = new HashMap<String, Link>();
		
		for (IndicatorDTO indicator : activity.getIndicators()) {
			
			if(indicator.getCategory()!=null){
				Link indCategoryLink = indicatorCategories.get(indicator.getCategory());
				
				if(indCategoryLink  == null){
					indCategoryLink = createIndicatorCategoryLink(indicator, indicatorCategories);							
					indicatorCategories.put(indicator.getCategory(), indCategoryLink);
					treeStore.add(parent, indCategoryLink, false);
				}
			
				treeStore.add(indCategoryLink, indicator, false);
			}else{
				treeStore.add(parent, indicator, false);
			}
		}

	}

	private Link createIndicatorCategoryLink(IndicatorDTO indicatorNode, Map<String, Link> categories){
		return Link.folderLabelled(indicatorNode.getCategory())
				.usingKey(categoryKey(indicatorNode, categories))
				.withIcon(IconImageBundle.ICONS.folder()).build();
	}
	
	private Link createCategoryLink(ActivityDTO activity,Map<String, Link> categories) {

		return Link.folderLabelled(activity.getCategory())
				.usingKey(categoryKey(activity, categories))
				.withIcon(IconImageBundle.ICONS.folder()).build();
	}

	private Link createActivityLink(ActivityDTO activity) {

		return Link.folderLabelled(activity.getName())
				.usingKey(activityKey(activity))
				.withIcon(IconImageBundle.ICONS.folder()).build();
	}

	private String categoryKey(ActivityDTO activity, Map<String, Link> categories) {
		return "category" + activity.getDatabase().getId()	+ activity.getCategory() + categories.size();
	}

	private String categoryKey(IndicatorDTO indicatorNode, Map<String, Link> categories) {
		return "category-indicator" +  indicatorNode.getCategory() + categories.size();
	}
	
	private String activityKey(ActivityDTO activity) {
		return "activity" + activity.getDatabase().getId() + activity.getName();
	}


	@Override
	public Store<ModelData> getStore() {
		return treeStore;
	}

	public TreeStore<ModelData> getTreeStore() {
		return treeStore;
	}

	public void onNodeDropped(ModelData source) {

		// update sortOrder

		ModelData parent = treeStore.getParent(source);
		List<ModelData> children = parent == null ? treeStore.getRootItems()
				: treeStore.getChildren(parent);

		for (int i = 0; i != children.size(); ++i) {
			Record record = treeStore.getRecord(children.get(i));
			record.set("sortOrder", i);
		}

	}

	protected ActivityDTO findActivityFolder(ModelData selected) {

		while (!(selected instanceof ActivityDTO)) {
			selected = treeStore.getParent(selected);
		}

		return (ActivityDTO) selected;
	}

	protected AttributeGroupDTO findAttributeGroupNode(ModelData selected) {
		if (selected instanceof AttributeGroupDTO) {
			return (AttributeGroupDTO) selected;
		}
		if (selected instanceof AttributeDTO) {
			return (AttributeGroupDTO) treeStore.getParent(selected);
		}
		throw new AssertionError("not a valid selection to add an attribute !");

	}

	@Override
	protected void onDeleteConfirmed(final ModelData model) {
		service.execute(new Delete((EntityDTO) model),
				view.getDeletingMonitor(), new AsyncCallback<VoidResult>() {
					public void onFailure(Throwable caught) {

					}

					public void onSuccess(VoidResult result) {
						treeStore.remove(model);
						eventBus.fireEvent(AppEvents.SchemaChanged);
					}
				});
	}

	@Override
	protected String getStateId() {
		return "target" + db.getId();
	}

	@Override
	protected Command createSaveCommand() {
		BatchCommand batch = new BatchCommand();

		for (ModelData model : treeStore.getRootItems()) {
			prepareBatch(batch, model);
		}
		return batch;
	}

	protected void prepareBatch(BatchCommand batch, ModelData model) {
		if (model instanceof EntityDTO) {
			Record record = treeStore.getRecord(model);
			if (record.isDirty()) {
				batch.add(new UpdateEntity((EntityDTO) model, this
						.getChangedProperties(record)));
			}
		}

		for (ModelData child : treeStore.getChildren(model)) {
			prepareBatch(batch, child);
		}
	}

	public void onSelectionChanged(ModelData selectedItem) {
		view.setActionEnabled(UIActions.delete, this.db.isDesignAllowed()
				&& selectedItem instanceof EntityDTO);
	}

	public Object getWidget() {
		return view;
	}

	@Override
	protected void onSaved() {

	}

	@Override
	public PageId getPageId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean navigate(PageState place) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}
}
