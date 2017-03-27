package fr.inria.sacha.quizBFP.client;

import java.util.Iterator;
import java.util.List;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.AbstractHasData;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;

import fr.inria.sacha.logic.shared.BFPatternHunkResult;
import fr.inria.sacha.logic.shared.BfInstance;
import fr.inria.sacha.logic.shared.BugFixItemResult;
import fr.inria.sacha.logic.shared.BugFixPatternResult;
import fr.inria.sacha.logic.shared.BugFixQuizItem;
import fr.inria.sacha.logic.shared.BugFixQuizResult;
import fr.inria.sacha.logic.shared.ResultType;
import fr.inria.sacha.logic.shared.WaitDialog;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SurveyBFP implements EntryPoint {
	private Button startButton;
	private Button nextQuizButton;
	
	public BugFixQuizResult resultSurvey = new BugFixQuizResult();

	private CellTable cellTableToken = new CellTable();
	private MultiSelectionModel<BfInstance> selectionModelToken;

	private CellTable cellTableAST = new CellTable();
	private MultiSelectionModel<BfInstance> selectionModelAST;

	private CellTable cellTableCommon = new CellTable();
	private MultiSelectionModel<BfInstance> selectionModelCommon;

	private MultiSelectionModel<BfInstance> selectionModelTokenAST;
	private CellTable cellTableTokenAST =  new CellTable();;
	
	BugFixQuizItem currentItem = null;
	WaitDialog waitDialog = new WaitDialog();
	final TextArea commentBox = new TextArea();
	final Label title = new Label();

	final VerticalPanel projectPanel = new VerticalPanel();
	final ListBox datasetlb = new ListBox();
	final ListBox userlb = new ListBox();
	String userNameSelected = "";
	String datasetSelected = "";
	Integer nrRevisions;


	public void onModuleLoad() {

		startButton = new Button();

		// RootPanel introPanel = RootPanel.get();
		// introPanel.add(title);
		// introPanel.add(startButton);

		waitDialog.setModal(true);
		waitDialog.setPopupPosition(RootPanel.get().getOffsetWidth() / 2, (int) RootPanel.get().getOffsetHeight() / 2);

		startButton.setText("Start");
		startButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				userNameSelected = userlb.getValue(userlb.getSelectedIndex());
				datasetSelected = datasetlb.getValue(datasetlb.getSelectedIndex());
				waitDialog.show();
				getInstance().start(userNameSelected, datasetSelected, new AsyncCallback<Integer>() {

					@Override
					public void onSuccess(Integer result) {
						nrRevisions = result;
						resultSurvey = new BugFixQuizResult();
						waitDialog.hide();
						showQuizPanels();
						nextRevisionAnalysis();

					}

					@Override
					public void onFailure(Throwable caught) {

					}
				});

			}
		});

		nextQuizButton = new Button();
		nextQuizButton.setText("Next");
		nextQuizButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				analyzeHunkResults();
				nextRevisionAnalysis();

			}

		});
		createProjectTab();
		RootPanel.get().setWidth("100%");

		RootPanel.get("intro").add(projectPanel);
		//
		createResultBotton();
		// --
		createPatternPanel();

		createComment();
		// --
		createTabPanel();

		hideQuizPanels();
		// RootPanel.get("patternContainer").setVisible(false);
		// RootPanel.get("tabContainer").setVisible(false);
	}

	public void nextRevisionAnalysis() {
		// selectionModel
		getInstance().getNextBugFixItem(userNameSelected, new AsyncCallback<BugFixQuizItem>() {

			@Override
			public void onSuccess(BugFixQuizItem result) {
				if (result == null) {
					resultSurvey.setAuthorName(userNameSelected);
					resultSurvey.setProject(datasetSelected);
					waitDialog.show();
					getInstance().saveResult(resultSurvey, new AsyncCallback<Void>() {

						@Override
						public void onSuccess(Void result) {
							waitDialog.hide();
							Window.alert("Survey End");
							hideQuizPanels();
							resetControls();
						}

						@Override
						public void onFailure(Throwable caught) {
							waitDialog.hide();
							Window.alert(caught.getMessage());
							caught.printStackTrace();

						}
					});

				} else {
					updateView(result);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

			}
		});
	}

	public void createPatternPanel() {
		VerticalPanel verticalP = new VerticalPanel();
		// RootPanel.get("patternContainer").add(patternHpanel);
		patternHpanel.setWidth("100%");
		patternHpanel.setBorderWidth(1);
		RootPanel.get("patternContainer").add(verticalP);
		verticalP.add(title);
		verticalP.add(patternHpanel);
		verticalP.add(nextQuizButton);
		// -----
		// Add a selection model so we can select cells.
		selectionModelToken = new MultiSelectionModel<BfInstance>(this.KEY_PROVIDER_TOKEN);
		cellTableToken.setSelectionModel(selectionModelToken,
				DefaultSelectionEventManager.<String> createCheckboxManager());
		cellTableToken.setSelectionModel(selectionModelToken);
		
		selectionModelTokenAST = new MultiSelectionModel<BfInstance>(this.KEY_PROVIDER_TOKEN_AST);
		cellTableTokenAST.setSelectionModel(selectionModelTokenAST,
				DefaultSelectionEventManager.<String> createCheckboxManager());
		cellTableTokenAST.setSelectionModel(selectionModelTokenAST);
		

		selectionModelAST = new MultiSelectionModel<BfInstance>(this.KEY_PROVIDER_AST);
		cellTableAST
				.setSelectionModel(selectionModelAST, DefaultSelectionEventManager.<String> createCheckboxManager());
		// cellTableAST.setSelectionModel(selectionModelAST);

		selectionModelCommon = new MultiSelectionModel<BfInstance>(this.KEY_PROVIDER_COMMON);
		cellTableCommon.setSelectionModel(selectionModelCommon,
				DefaultSelectionEventManager.<String> createCheckboxManager());

		
		
		
		// --
		createPatternList("Only Token Patterns", pagerPanelToken, cellTableToken, this.selectionModelToken);
		createPatternList("Only AST Token Patterns", pagerPanelTokenAST, cellTableTokenAST, this.selectionModelTokenAST);
		createPatternList("AST and Token", pagerPanelCommon, cellTableCommon, this.selectionModelCommon);
		createPatternList("Only AST", pagerPanelAST, cellTableAST, this.selectionModelAST);

	}

	public void createComment() {

		VerticalPanel vPanel = new VerticalPanel();
		patternHpanel.add(vPanel);
		Label label = new Label("Comment");
		label.setStyleName("titulo1Small");
		vPanel.setWidth("100%");
		vPanel.add(label);
		vPanel.add(commentBox);
	}

	HorizontalPanel patternHpanel = new HorizontalPanel();

	private void resetControls() {
		userlb.setSelectedIndex(-1);
		datasetlb.setEnabled(false);
		startButton.setEnabled(false);
		commentBox.setText("");

	}

	public void createPatternList(String title, ShowMorePagerPanel pagerPanel, CellTable cellTable,
			final MultiSelectionModel<BfInstance> selectionModel) {

		// RootPanel.get().add(pagerPanel);

		VerticalPanel vPanel = new VerticalPanel();
				
		ScrollPanel sp = new ScrollPanel();
		sp.add(vPanel);
		//sp.setAlwaysShowScrollBars(true);
		patternHpanel.add(sp);
		sp.setHeight("100%");
		////patternHpanel.add(vPanel);
		patternHpanel.setHeight("100%");
		Label label = new Label(title);
		label.setStyleName("titulo1Small");
		vPanel.setWidth("100%");
		vPanel.add(label);
		vPanel.add(pagerPanel);
		cellTable.setPageSize(4);
		
		pagerPanel.setDisplay(cellTable);
		//pagerPanel.setStyleName("scrollable");
		//vPanel.setHeight("15%");
	
		Column<BfInstance, Boolean> checkColumn = new Column<BfInstance, Boolean>(new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(BfInstance object) {

				return selectionModel.isSelected(object);
			}
		};

		cellTable.addColumn(checkColumn);
		cellTable.setColumnWidth(checkColumn, 10, Unit.PCT);

		// Address.
		Column<BfInstance, String> patternColumn = new Column<BfInstance, String>(new TextCell()) {
			@Override
			public String getValue(BfInstance object) {
				return object.data;
			}
		};

		cellTable.addColumn(patternColumn);
		cellTable.setColumnWidth(patternColumn, 90, Unit.PCT);
		//cellTable.setHeight("100%");
		//pagerPanel.setHeight("100%");
		pagerPanel.setIncrementSize(4);
	}

	ShowMorePagerPanel pagerPanelToken = new ShowMorePagerPanel();
	ShowMorePagerPanel pagerPanelAST = new ShowMorePagerPanel();
	ShowMorePagerPanel pagerPanelCommon = new ShowMorePagerPanel();
	ShowMorePagerPanel pagerPanelTokenAST = new ShowMorePagerPanel();

	
	final TabPanel tabPanel = new TabPanel();
	final HTML grHTMLPanel = new HTML("");

	public void createTabPanel() {
		tabPanel.setVisible(true);
		// tabPanel.add(new
		// HTML("<div id=\"compare\" width=\"100%\" height=\"100%\"></div>"),
		tabPanel.add(new HTML("<div id=\"compare\" width=\"100%\" style=\"display:block !important\"></div>"), "Diff");
		ScrollPanel scrollPanelGT = new ScrollPanel();
		// scrollPanelGT.setHeight("400px");
		scrollPanelGT.setHeight("100%");

		scrollPanelGT.add(grHTMLPanel);
		scrollPanelGT.setAlwaysShowScrollBars(true);

		tabPanel.add(scrollPanelGT, "GumTree");
		tabPanel.setWidth("100%");
		tabPanel.selectTab(0);
		// -----
		// RootPanel.get().add(tabPanel);
		FlowPanel panelTab = new FlowPanel();
		panelTab.setWidth("100%");
		panelTab.add(tabPanel);
		RootPanel.get("tabContainer").add(panelTab);
	}

	private static SurveyBFPServiceAsync instance;

	public static SurveyBFPServiceAsync getInstance() {
		if (instance == null) {
			instance = GWT.create(SurveyBFPService.class);
		}
		return instance;
	}

	public void updateView(BugFixQuizItem item) {

		currentItem = item;
		selectionModelToken.clear();
		selectionModelAST.clear();
		selectionModelCommon.clear();
		selectionModelTokenAST.clear();
		
		commentBox.setText("");
		// ---
		putSourceCodeContent(item.getLeftSourceCode(), item.getRightSourceCode());
		title.setText("Dataset " + datasetSelected + " Revision id " + item.getTransactionID() + ", Progress: "
				+ item.getIndex() + " / " + this.nrRevisions);
		title.setStyleName("titulo1");
		setIdToElements(item.getTokenBugFixes());
		cellTableToken.setRowData(item.getTokenBugFixes());

		setIdToElements(item.getASTbugFixes());
		cellTableAST.setRowData(item.getASTbugFixes());

		setIdToElements(item.getCommonBugFixes());
		cellTableCommon.setRowData(item.getCommonBugFixes());

		setIdToElements(item.getTokenASTBugFixes());
		cellTableTokenAST.setRowData(item.getTokenASTBugFixes());
		
		grHTMLPanel.setHTML(item.getGumtreeDiff());

	}

	private void setIdToElements(List<BfInstance> patterns) {
		int id = 0;
		for (BfInstance pattern : patterns) {
			id++;
			pattern.id = id;
		}

	}

	public void analyzeHunkResults() {
		if (currentItem == null)
			return;

		BugFixItemResult item = new BugFixItemResult();
		item.setTransactionID(currentItem.getTransactionID());
		item.setComments(commentBox.getText());

	/*	System.out.println(" TOKEN selected " + selectionModelToken.getSelectedSet().size());
		System.out.println(" AST selected " + selectionModelAST.getSelectedSet().size());
		System.out.println(" Common selected " + selectionModelCommon.getSelectedSet().size());
*/
		checkSelected("AST", item.getAstEvaluation(), currentItem.getASTbugFixes(), selectionModelAST);
		checkSelected("Token", item.getTokenEvaluation(), currentItem.getTokenBugFixes(), selectionModelToken);
		checkSelected("TokenAST", item.getTokenASTEvaluation(), currentItem.getTokenASTBugFixes(), selectionModelTokenAST);
		checkSelected("Common", item.getCommonEvaluation(), currentItem.getCommonBugFixes(), selectionModelCommon);

		this.resultSurvey.getResultados().add(item);

	}

	public void checkSelected(String discoveredBy, List<BFPatternHunkResult> evaluation, List<BfInstance> instances,
			MultiSelectionModel<BfInstance> selectionModel) {
		if (instances != null)
			for (BfInstance bfi : instances) {
				BFPatternHunkResult hunkP = new BFPatternHunkResult(bfi.getHunk(), bfi.getData());
				hunkP.setDiscoverBy(discoveredBy);
				hunkP.setPanBugFixParttern(selectionModel.isSelected(bfi) ? ResultType.BUGFIXPATTERN.toString()
						: ResultType.NOTBUGFIXPATTERN.toString());
				evaluation.add(hunkP);
			}
	}

	private static native void putSourceCodeContent(String left, String right)/*-{
		$wnd.putSourceCodeContent(left, right);

	}-*/;

	public ProvidesKey<BfInstance> KEY_PROVIDER_TOKEN_AST = new KeyProvider();
	public ProvidesKey<BfInstance> KEY_PROVIDER_TOKEN = new KeyProvider();
	public ProvidesKey<BfInstance> KEY_PROVIDER_AST = new KeyProvider();
	public ProvidesKey<BfInstance> KEY_PROVIDER_COMMON = new KeyProvider();

	class KeyProvider implements ProvidesKey<BfInstance> {

		@Override
		public Object getKey(BfInstance item) {
			return item == null ? null : item.id;
		}

	}

	private void initUserCombos() {
		userlb.setSelectedIndex(-1);
		datasetlb.setEnabled(false);
		startButton.setEnabled(false);
	}

	public void showQuizPanels() {
		RootPanel.get("patternContainer").setVisible(true);
		RootPanel.get("tabContainer").setVisible(true);
		// tabPanel.setVisible(true);
		// tabPanel.setWidth("100%");
		RootPanel.get("intro").setVisible(false);
	}

	public void hideQuizPanels() {
		RootPanel.get("patternContainer").setVisible(false);
		RootPanel.get("tabContainer").setVisible(false);
		RootPanel.get("intro").setVisible(true);
		userlb.setSelectedIndex(-1);
	}

	final Label nameLabel = new Label("User Name");

	public void createResultBotton() {
		Button b = new Button("See Results", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				waitDialog.show();
				getInstance().analyzeResults(new AsyncCallback<List<BugFixPatternResult>>() {

					@Override
					public void onSuccess(List<BugFixPatternResult> result) {
						waitDialog.hide();
						DialogBox d = createDialogBox(result);
						d.center();
						d.show();
					}

					@Override
					public void onFailure(Throwable caught) {
						waitDialog.hide();
						Window.alert(caught.toString());

					}
				});

			}
		});
		projectPanel.add(b);
	}

	public void createProjectTab() {
		VerticalPanel userPanel = new VerticalPanel();
		Label welcomeLabel = new Label("Welcome to survey: Analyzing BF Patterns");
		welcomeLabel.setStyleName("titulo1");
		projectPanel.add(welcomeLabel);
		projectPanel.setCellHorizontalAlignment(welcomeLabel, HorizontalPanel.ALIGN_CENTER);
		userPanel.add(nameLabel);
		nameLabel.setStyleName("titulo1Small");
		userPanel.add(userlb);
		startButton.setEnabled(false);
		userlb.addItem("Martin", "Martin");
		// userlb.addItem("Jean-Remy", "Jean-Remy");
		userlb.addItem("Matias", "Matias");
		// userlb.addItem("user1", "user1");

		projectPanel.setSpacing(10);
		projectPanel.add(userPanel);
		Label datasetlabel = new Label("Dataset");
		userPanel.add(datasetlabel);
		datasetlabel.setStyleName("titulo1Small");
		userPanel.add(datasetlb);
		userPanel.add(startButton);
		projectPanel.setCellHorizontalAlignment(userPanel, HorizontalPanel.ALIGN_CENTER);
		this.startButton.addStyleName("sendButton");

		userlb.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {

				waitDialog.show();
				getInstance().getClassification(userNameSelected, new AsyncCallback<List<String>>() {

					@Override
					public void onSuccess(List<String> result) {
						waitDialog.hide();
						datasetlb.clear();
						for (Iterator iterator = result.iterator(); iterator.hasNext();) {
							String project = (String) iterator.next();
							datasetlb.addItem(project);
						}
						datasetlb.setEnabled(true);
						startButton.setEnabled(true);
					}

					@Override
					public void onFailure(Throwable caught) {
						waitDialog.hide();
						Window.alert(caught.getMessage());
					}
				});
			}
		});

	}

	/**
	 * Create the dialog box for this example.
	 * 
	 * @return the new dialog box
	 */
	private DialogBox createDialogBox(List<BugFixPatternResult> result) {

		// Create a dialog box and set the caption text
		final DialogBox dialogBox = new DialogBox();
		dialogBox.ensureDebugId("cwDialogBox");
		dialogBox.setText("Results");

		// Create a table to layout the content
		VerticalPanel dialogContents = new VerticalPanel();
		dialogContents.setSpacing(4);
		dialogBox.setWidget(dialogContents);

		/*// Add some text to the top of the dialog
		HTML details = new HTML();
		dialogContents.add(details);
		dialogContents.setCellHorizontalAlignment(details, HasHorizontalAlignment.ALIGN_CENTER);
*/
		for (BugFixPatternResult bugFixPatternResult : result) {
			drawTable(bugFixPatternResult, dialogContents);
		}
		

		
		
		// Add a close button at the bottom of the dialog
		Button closeButton = new Button("Close", new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});
		dialogContents.add(closeButton);

		// Return the dialog box
		return dialogBox;
	}

	public void drawTable(BugFixPatternResult result, VerticalPanel dialogContents) {
		
		Label label = new Label(result.getDataset());
		dialogContents.add(label);
		label.setStyleName("titulo1Small");
		
		int[][] resultD = result.getSummary();
		Grid grid = new Grid(4, 4);
		grid.setText(1, 0, "AST");
		grid.setText(2, 0, "Token-AST");
		grid.setText(3, 0, "Token");
		
		grid.setText(0, 1, "Not Valid");
		grid.setText(0, 2, "Valid");
		grid.setText(0, 2, "Missing");
		
		grid.setText(1, 1, Integer.toString(resultD[0][0]));
		grid.setText(1, 2, Integer.toString(resultD[0][1]));
		grid.setText(1, 3, Integer.toString(resultD[0][2]));
		
		grid.setText(2, 1, Integer.toString(resultD[1][0]));
		grid.setText(2, 2, Integer.toString(resultD[1][1]));
		grid.setText(2, 3, Integer.toString(resultD[1][2]));
		
		grid.setText(3, 1, Integer.toString(resultD[2][0]));
		grid.setText(3, 2, Integer.toString(resultD[2][1]));
		grid.setText(3, 3, Integer.toString(resultD[2][2]));
		
		dialogContents.add(grid);
	}

}
