package org.sigmah.client.page.report;

import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.callback.DownloadCallback;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.NavigationCallback;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageId;
import org.sigmah.client.page.PageState;
import org.sigmah.client.page.common.toolbar.ExportCallback;
import org.sigmah.client.page.report.editor.CompositeEditor;
import org.sigmah.client.page.report.editor.EditorProvider;
import org.sigmah.client.page.report.editor.ReportElementEditor;
import org.sigmah.client.page.report.json.ReportSerializer;
import org.sigmah.client.page.report.resources.ReportResources;
import org.sigmah.shared.command.BatchCommand;
import org.sigmah.shared.command.GetReportModel;
import org.sigmah.shared.command.GetReports;
import org.sigmah.shared.command.RenderElement;
import org.sigmah.shared.command.RenderElement.Format;
import org.sigmah.shared.command.UpdateReportModel;
import org.sigmah.shared.command.UpdateReportSubscription;
import org.sigmah.shared.command.result.BatchResult;
import org.sigmah.shared.command.result.ReportsResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.ReportMetadataDTO;
import org.sigmah.shared.report.model.Report;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.EditorEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.common.base.Strings;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class ReportDesignPage extends ContentPanel implements Page, ExportCallback {

	private class SaveCallback implements AsyncCallback<VoidResult> {
		@Override
		public void onSuccess(VoidResult result) {
			Info.display(I18N.CONSTANTS.saved(), I18N.MESSAGES.reportSaved(currentModel.getTitle()));
			onSaved();
		}

		@Override
		public final void onFailure(Throwable caught) {
			MessageBox.alert(I18N.CONSTANTS.serverError(), caught.getMessage(), null);
		}
		
		public void onSaved() {
			
		}
	}


	public static final PageId PAGE_ID = new PageId("reportdesign");

	private final EventBus eventBus;
	private final Dispatcher dispatcher;
	private final EditorProvider editorProvider;
	
	private boolean reportEdited;
	private ReportBar reportBar;
	
	
	/**
	 * The model being edited on this page
	 */
	private Report currentModel;
	private ReportMetadataDTO currentMetadata;
	 

	/**
	 * The editor for the model
	 */
	private ReportElementEditor currentEditor;


	
	@Inject
	public ReportDesignPage(EventBus eventBus, Dispatcher service, EditorProvider editorProvider) {
		this.eventBus = eventBus;
		this.dispatcher = service;
		this.editorProvider = editorProvider;
		
		
		ReportResources.INSTANCE.style().ensureInjected();

		setLayout(new BorderLayout());
		setHeaderVisible(false);

		createToolbar();
	}

	public void createToolbar() {
		reportBar = new ReportBar();
		BorderLayoutData reportBarLayout = new BorderLayoutData(LayoutRegion.NORTH);
		reportBarLayout.setSize(35);
		add(reportBar, reportBarLayout);

		reportBar.getExportButton().setCallback(this);

		
		reportBar.addTitleEditCompleteListener(new Listener<EditorEvent>() {
			@Override
			public void handleEvent(EditorEvent be) {
				String newTitle = (String)be.getValue();
				if(newTitle != null && !newTitle.equals(currentModel.getTitle())) {
					currentModel.setTitle(newTitle);
					reportBar.setReportTitle(newTitle);
					save(new SaveCallback());
				}
			}
		});

		reportBar.getSaveButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				if(untitled()) {
					promptForTitle(new SaveCallback());
				} else {
					save(new SaveCallback());
				}
			}
		});
		
		reportBar.getShareButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				showShareForm();
			}
			
		});
		
		reportBar.getDashboardButton().addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				pinToDashboard(reportBar.getDashboardButton().isPressed());
			}
		});
	}


	@Override
	public boolean navigate(PageState place) {
		if(place instanceof ReportDesignPageState) {
			go(((ReportDesignPageState) place).getReportId());
			return true;
		}
		return false;
	}


	public void go(int reportId) {
		loadReport(reportId);
	}

	private void loadReport(int reportId) {

		BatchCommand batch = new BatchCommand();
		batch.add(new GetReportModel(reportId));
		batch.add(new GetReports());
		
		dispatcher.execute(batch, new MaskingAsyncMonitor(this, I18N.CONSTANTS.loading()),
				new AsyncCallback<BatchResult>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO show appropriate message.
			}

			@Override
			public void onSuccess(BatchResult result) {						
				onModelLoaded((Report) result.getResult(0), (ReportsResult)result.getResult(1));
			}
		});
	}

	private void onModelLoaded(Report report, ReportsResult reportsResult) {
		try {
			this.currentMetadata = reportsResult.forId(report.getId());
		} catch(IllegalArgumentException e) {
			this.currentMetadata = new ReportMetadataDTO();
			this.currentMetadata.setId(report.getId());
		}
		this.currentModel = report;
		
		reportBar.setReportTitle(currentModel.getTitle());
		reportBar.getDashboardButton().toggle(currentMetadata.isDashboard());
		
		if(currentModel.getElements().size() == 1) {
			ReportElementEditor editor = editorProvider.create(currentModel.getElement(0));
			editor.bind(currentModel.getElement(0));
			installEditor( editor );
		} else {
			CompositeEditor editor = (CompositeEditor)editorProvider.create(currentModel);
			editor.bind(currentModel);
			installEditor( editor );
		}
		
	}

	private void installEditor(ReportElementEditor editor) {
		if(currentEditor != null) {
			remove(currentEditor.getWidget());
		}
		
		reportBar.getExportButton().setFormats(editor.getExportFormats());
		
		add(editor.getWidget(), new BorderLayoutData(LayoutRegion.CENTER));
		this.currentEditor = editor;
		layout();
	}

	public void save(final AsyncCallback<VoidResult> callback) {
		UpdateReportModel updateReport = new UpdateReportModel();
		updateReport.setModel(currentModel);
		
		dispatcher.execute(updateReport, null, new AsyncCallback<VoidResult>() {
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(VoidResult result) {
				callback.onSuccess(result);
			}
		});
	}
	
	private void pinToDashboard(final boolean pressed) {
		ensureTitled(new SaveCallback() {
			@Override
			public void onSaved() {
				final UpdateReportSubscription update = new UpdateReportSubscription();
				update.setReportId(currentModel.getId());
				update.setPinnedToDashboard(pressed);
				
				dispatcher.execute(update, null, new SaveCallback() {

					@Override
					public void onSuccess(VoidResult result) {
						if(update.getPinnedToDashboard()) {
							Info.display(I18N.CONSTANTS.saved(), 
									I18N.MESSAGES.addedToDashboard(currentModel.getTitle()));
						} else {
							Info.display(I18N.CONSTANTS.saved(), 
									I18N.MESSAGES.removedFromDashboard(currentModel.getTitle()));
						}
					}
					
				});
			}
		});
	}

	
	public void promptForTitle(final AsyncCallback<VoidResult> callback) {
		MessageBox.prompt(I18N.CONSTANTS.save(), I18N.CONSTANTS.chooseReportTitle(), new Listener<MessageBoxEvent>() {
			
			@Override
			public void handleEvent(MessageBoxEvent be) {
				String newTitle = be.getMessageBox().getTextBox().getValue();
				if(!Strings.isNullOrEmpty(newTitle)) {
					currentModel.setTitle(newTitle);
					reportBar.setReportTitle(newTitle);
					save(callback);
				}
			}
		});
	}

	public void setReportEdited(boolean edited){
		reportEdited = edited;
	}

	public boolean reportEdited(){
		return reportEdited;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

	@Override
	public PageId getPageId() {
		return PAGE_ID;
	}

	@Override
	public Object getWidget() {
		return this;
	}

	@Override
	public void requestToNavigateAway(PageState place,
			NavigationCallback callback) {
		callback.onDecided(true);
	}

	@Override
	public String beforeWindowCloses() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void ensureTitled(SaveCallback callback) {
		if(untitled()) {
			promptForTitle(callback);
		} else {
			callback.onSaved();
		}
	}

	public void showShareForm() {
		ensureTitled(new SaveCallback() {

			@Override
			public void onSaved() {
				final ShareReportDialog dialog = new ShareReportDialog(dispatcher);
				//form.updateForm(currentReportId);
				dialog.show(currentModel);
				
			}
		});
	}

	@Override
	public void export(Format format) {
		dispatcher.execute(new RenderElement(currentEditor.getModel(), format), null, new DownloadCallback(eventBus));
	}

	private boolean untitled() {
		return currentModel.getTitle()==null;
	}
}
