package fr.inria.sacha.quizGumTree.client;

import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import fr.inria.sacha.logic.shared.QuizItem;
import fr.inria.sacha.logic.shared.QuizItemResult;
import fr.inria.sacha.logic.shared.QuizResult;
import fr.inria.sacha.logic.shared.ResultType;
import fr.inria.sacha.logic.shared.WaitDialog;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Survey implements EntryPoint {

	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	private final QuizServiceAsync service = GWT.create(QuizService.class);

	int clicks = 0;
	
	CheckBox anomaliesCB = new CheckBox("Anomaly/Error?");
	
	QuizResult resultQuiz = new QuizResult();
	/**
	 * This is the entry point method.
	 */

	final Label nameLabel = new Label("User Name");

	final Button startButton = new Button("Start");

	final Label astLabel = new Label();
	final TextArea astTA = new TextArea();

	final Button sendButton = new Button("Send");
	final Label errorLabel = new Label();
	final Label itemLabel = new Label();
	final Label commitMessage = new Label();
	final TextArea commitMessageTA = new TextArea();
	final VerticalPanel bugfixlPanel = new VerticalPanel();
	final TabPanel tabPanel = new TabPanel();

	final VerticalPanel projectPanel = new VerticalPanel();
	final ListBox datasetlb = new ListBox();
	final ListBox userlb = new ListBox();

	final VerticalPanel isBFradioButtonPanel = new VerticalPanel();
	final VerticalPanel representationradioButtonPanel = new VerticalPanel();
	final VerticalPanel gtVsDiffPanel = new VerticalPanel();
	final RadioButton rbBF = new RadioButton("fixRadioGroup", "Yes");
	final RadioButton rbNOBF = new RadioButton("fixRadioGroup", "No");
	final RadioButton rbNothing = new RadioButton("fixRadioGroup",
			"I don't know");
	final RadioButton rbGTgood = new RadioButton("rRadioGroup", "GumTree does a good job");
	final RadioButton rbGTnotgood = new RadioButton("rRadioGroup", "GumTree does not a good job");
	final RadioButton rbGTNeutral = new RadioButton("rRadioGroup",
			"Neutral");
	
	final RadioButton rbBestGT = new RadioButton("rBestRadioGroup", "GumTree is better");
	final RadioButton rbBestDiff = new RadioButton("rBestRadioGroup", "Diff is better");
	final RadioButton rbBestNothing = new RadioButton("rBestRadioGroup",
			"The same/No difference");
	
	final TextArea bfCommentBox = new TextArea();

	final HTML grHTMLPanel = new HTML("");
	final HTML cdHTMLPanel = new HTML("");
	String datasetSelected;
	String userNameSelected;
	WaitDialog waitDialog = new WaitDialog();
	Integer numberquizItems = 0;

	PopupPanel lp = new PopupPanel();

	int textareaHeight = 3;

	public void onModuleLoad() {
		
		createTitle();
		
		createSurveyPanel();
		
		createCommentPanel();

		createProjectTab();
		
		createTabPanel();

		bugfixlPanel
				.setHorizontalAlignment(com.google.gwt.user.client.ui.HorizontalPanel.ALIGN_CENTER);
		bugfixlPanel.getElement().setAttribute("align", "center");

		VerticalPanel mainPanel = new VerticalPanel();
		mainPanel
				.setHorizontalAlignment(com.google.gwt.user.client.ui.HorizontalPanel.ALIGN_CENTER);
		mainPanel.setWidth("100%");
		VerticalPanel labelPanel = new VerticalPanel();
		labelPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		mainPanel.add(labelPanel);

		
		mainPanel.add(bugfixlPanel);
		mainPanel.setCellHorizontalAlignment(bugfixlPanel,
				com.google.gwt.user.client.ui.HorizontalPanel.ALIGN_CENTER);
		mainPanel.add(projectPanel);

		RootPanel.get("bugfixContainer").add(mainPanel);
	
		//RootPanel.get("commitContainer").
		
		FlowPanel panelTab = new FlowPanel();
		panelTab.setWidth("100%");
		panelTab.add(tabPanel);
		RootPanel.get("tabContainer").add(panelTab);
		waitDialog.setModal(true);
		waitDialog.setPopupPosition(RootPanel.get().getOffsetWidth() / 2,
				(int) RootPanel.get().getOffsetHeight() / 2);

		/*startButton.setEnabled(false);
		datasetlb.setEnabled(false);
		userlb.setSelectedIndex(-1);*/
		initUserCombos();
		
		userlb.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				waitDialog.show();
				userNameSelected =  userlb.getValue(userlb.getSelectedIndex());
				System.out.println("-->"+userNameSelected);
				datasetlb.setEnabled(false);
				startButton.setEnabled(false);
				service.getProjects(userNameSelected,new AsyncCallback<List<String>>() {

					@Override
					public void onSuccess(List<String> result) {
						datasetlb.clear();
						for (Iterator iterator = result.iterator(); iterator.hasNext();) {
							String project = (String) iterator.next();
							datasetlb.addItem(project);
						}
						datasetlb.setEnabled(true);
						startButton.setEnabled(true);
						waitDialog.hide();
					}

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();

					}
				});
				
				
			}
		});
		
		// /--------------------------------------------------------
	
		startButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				datasetSelected = datasetlb.getItemText(datasetlb
						.getSelectedIndex());
				waitDialog.show();
				service.startQuiz(userNameSelected,datasetSelected,
						new AsyncCallback<Integer>() {

							@Override
							public void onSuccess(Integer result) {
								numberquizItems = result;
								System.out.println("quiz " + result);
								resetSelection();
								showQuizPanels();
								hireProjectPanel();
								waitDialog.hide();
								nextQuizItem();

							}

							@Override
							public void onFailure(Throwable caught) {
								caught.printStackTrace();

							}
						});
			}
		});

		// Create a handler for the sendButton and nameField
		class ButtonHandler implements ClickHandler, KeyUpHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				savePartialQuizItemResult();
				nextQuizItem();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					savePartialQuizItemResult();
					nextQuizItem();
				}
			}

		}

		// Add a handler to send the name to the server
		ButtonHandler handler = new ButtonHandler();
		sendButton.addClickHandler(handler);
		sendButton.setStyleName("titulo1Small");
		startButton.setStyleName("titulo1Small");
	}

	/**
	 * Send the name from the nameField to the server and wait for a response.
	 */
	private void nextQuizItem() {
		waitDialog.show();
		System.out.println("Asking next to server");
		// sendButton.setEnabled(false);
		service.getQuizTransaction(userNameSelected,new AsyncCallback<QuizItem>() {
			public void onFailure(Throwable caught) {
				// caught.printStackTrace();
				System.err.println("Problems to get item from server");
				// If fails, give another item
				nextQuizItem();
			}

			public void onSuccess(QuizItem nextQuiz) {
				sendButton.setFocus(true);
				if (nextQuiz == null) {

					resultQuiz.setAuthorName(userlb.getItemText(userlb
							.getSelectedIndex()));
					String[] datasetNames = datasetSelected.split("-");
					resultQuiz.setProject(datasetNames[0]);
					resultQuiz.setHeuristic(datasetNames[1]);
					service.saveResult(resultQuiz,
							new AsyncCallback<Boolean>() {
								@Override
								public void onSuccess(Boolean result) {
									resultQuiz = new QuizResult();
									bugfixlPanel.setVisible(false);
									tabPanel.setVisible(false);
									projectPanel.setVisible(true);
									resetSelection();
									waitDialog.hide();
									initUserCombos();
									Window.alert("Survey has finished. Thanks!!!");
								}

								@Override
								public void onFailure(Throwable caught) {
									caught.printStackTrace();

								}
							});

				} else {
					resetSelection();
					updateView(nextQuiz);
					waitDialog.hide();
				}
			}
		});
	}

	public void createProjectTab() {
		VerticalPanel userPanel = new VerticalPanel();
		Label welcomeLabel = new Label(
				"Welcome to survey: Analyzing Versioning Transactions");
		welcomeLabel.setStyleName("titulo1");
		projectPanel.add(welcomeLabel);
		projectPanel.setCellHorizontalAlignment(welcomeLabel,
				HorizontalPanel.ALIGN_CENTER);
		userPanel.add(nameLabel);
		nameLabel.setStyleName("titulo1Small");
		userPanel.add(userlb);

		userlb.addItem("Martin", "Martin");
		userlb.addItem("Jean-Remy", "Jean-Remy");
		userlb.addItem("Matias", "Matias");
		userlb.addItem("user1", "user1");

		projectPanel.setSpacing(10);
		projectPanel.add(userPanel);
		Label datasetlabel = new Label("Dataset");
		userPanel.add(datasetlabel);
		datasetlabel.setStyleName("titulo1Small");
		userPanel.add(datasetlb);
		userPanel.add(startButton);
		projectPanel.setCellHorizontalAlignment(userPanel,
				HorizontalPanel.ALIGN_CENTER);
		sendButton.addStyleName("sendButton");
		
		
	}

	public void createSurveyPanel() {

		HorizontalPanel hp = new HorizontalPanel();
		hp.getElement().getStyle().setProperty("margin", "1px");
		// ---
		bugfixlPanel.add(hp);
		bugfixlPanel.setCellHorizontalAlignment(hp,
				HorizontalPanel.ALIGN_CENTER);
		Label isbxLabel = new Label("Is it a bug fix change?");
		isbxLabel.setStyleName("titulo1Small");
		isBFradioButtonPanel.setStyleName("tableQuiz");
		isBFradioButtonPanel.add(isbxLabel);
		isBFradioButtonPanel.add(rbBF);
		isBFradioButtonPanel.add(rbNOBF);
		isBFradioButtonPanel.add(rbNothing);
		isBFradioButtonPanel.setCellHorizontalAlignment(rbBF,
				HorizontalPanel.ALIGN_CENTER);
		isBFradioButtonPanel.setCellHorizontalAlignment(rbNOBF,
				HorizontalPanel.ALIGN_CENTER);
		isBFradioButtonPanel.setCellHorizontalAlignment(rbNothing,
				HorizontalPanel.ALIGN_CENTER);
		isBFradioButtonPanel
				.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		isBFradioButtonPanel.getElement().getStyle()
				.setProperty("margin", "1px");
		hp.add(isBFradioButtonPanel);

		bugfixlPanel.setCellHorizontalAlignment(isBFradioButtonPanel,
				HorizontalPanel.ALIGN_CENTER);
		Label repLab = new Label(
				"Does GumTree a good job?");
		repLab.setStyleName("titulo1Small");
		representationradioButtonPanel.setStyleName("tableQuiz");
		representationradioButtonPanel.add(repLab);
		representationradioButtonPanel.add(rbGTgood);
		representationradioButtonPanel.add(rbGTnotgood);
		representationradioButtonPanel.add(rbGTNeutral);
		representationradioButtonPanel.setCellHorizontalAlignment(rbGTgood,
				HorizontalPanel.ALIGN_CENTER);
		representationradioButtonPanel.setCellHorizontalAlignment(rbGTnotgood,
				HorizontalPanel.ALIGN_CENTER);
		representationradioButtonPanel.setCellHorizontalAlignment(rbGTNeutral,
				HorizontalPanel.ALIGN_CENTER);
		representationradioButtonPanel.getElement().getStyle()
				.setProperty("margin", "1px");
		hp.add(representationradioButtonPanel);
		
		Label betterLab = new Label(
				"Is GumTree better than Diff?");
		betterLab.setStyleName("titulo1Small");
		gtVsDiffPanel.setStyleName("tableQuiz");
		gtVsDiffPanel.add(betterLab);
		gtVsDiffPanel.add(rbBestGT);
		gtVsDiffPanel.add(rbBestDiff);
		gtVsDiffPanel.add(rbBestNothing);
		gtVsDiffPanel.setCellHorizontalAlignment(rbBestGT, HorizontalPanel.ALIGN_CENTER);
		gtVsDiffPanel.setCellHorizontalAlignment(rbBestDiff, HorizontalPanel.ALIGN_CENTER);
		gtVsDiffPanel.setCellHorizontalAlignment(rbBestNothing, HorizontalPanel.ALIGN_CENTER);
		gtVsDiffPanel.setCellHorizontalAlignment(betterLab, HorizontalPanel.ALIGN_CENTER);
	gtVsDiffPanel.getElement().getStyle().setProperty("margin", "1px");
		hp.add(gtVsDiffPanel);
		
		// -------------
		VerticalPanel vcommentpanel = new VerticalPanel();
		vcommentpanel.getElement().getStyle().setProperty("margin", "1px");
		Label commentLabel = new Label("Write a comment (Optional)");
		commentLabel.setStyleName("titulo1Small");
		hp.add(vcommentpanel);
		vcommentpanel.add(commentLabel);
		vcommentpanel.setCellHorizontalAlignment(commentLabel,
				HorizontalPanel.ALIGN_CENTER);
		bfCommentBox.setVisibleLines(textareaHeight);
		bfCommentBox.setCharacterWidth(100);
		bfCommentBox.setWidth("70%");
		vcommentpanel.add(bfCommentBox);
		vcommentpanel.setCellHorizontalAlignment(bfCommentBox,
				HorizontalPanel.ALIGN_CENTER);
		// -------------
		sendButton.setStyleName("titulo1Small");
		//bugfixlPanel.add(sendButton);
		bugfixlPanel.setVisible(false);
		//bugfixlPanel.setCellHorizontalAlignment(sendButton,	HorizontalPanel.ALIGN_CENTER);

	}

	public void createCommentPanel() {

		commitMessageTA.setWidth("50%");
		commitMessageTA.setCharacterWidth(200);
		commitMessageTA.setVisibleLines(textareaHeight);
		commitMessageTA.setEnabled(true);
	
		Label commitLabel = new Label("Commit Message");
		commitLabel.setStyleName("titulo1Small");
		
		HorizontalPanel hpcommit = new HorizontalPanel();
		VerticalPanel mg = new VerticalPanel();
		mg.add(commitLabel);
		mg.setCellHorizontalAlignment(commitLabel, HorizontalPanel.ALIGN_CENTER);
		mg.add(commitMessageTA);
		mg.setCellHorizontalAlignment(commitMessageTA,
				HorizontalPanel.ALIGN_CENTER);
		hpcommit.add(mg);
		//
		hpcommit.add(sendButton);
		hpcommit.setCellHorizontalAlignment(sendButton,
				HorizontalPanel.ALIGN_CENTER);
		//
		astTA.setEnabled(true);
		astTA.setCharacterWidth(200);
		astTA.setWidth("60%");
		VerticalPanel astpanel = new VerticalPanel();
		astLabel.setText("ChangeDistiller AST repr");
		astLabel.setStyleName("titulo1Small");
		astpanel.add(astLabel);
		astpanel.setCellHorizontalAlignment(astLabel,
				HorizontalPanel.ALIGN_CENTER);
		
		astTA.setVisibleLines(textareaHeight);
		
		HorizontalPanel hpSem = new HorizontalPanel();
		hpSem.add(astTA);
		hpSem.add(anomaliesCB);
		//astpanel.add(astTA);
		//astpanel.setCellHorizontalAlignment(astTA, HorizontalPanel.ALIGN_CENTER);
		hpSem.setCellHorizontalAlignment(astTA, HorizontalPanel.ALIGN_RIGHT);
		astpanel.add(hpSem);
		astpanel.setCellHorizontalAlignment(hpSem, HorizontalPanel.ALIGN_CENTER);
		
		hpcommit.add(astpanel);
	
		bugfixlPanel.add(hpcommit);
	
	}
	public void createTitle(){
		bugfixlPanel.setWidth("100%");
		bugfixlPanel.add(itemLabel);
		bugfixlPanel.setCellHorizontalAlignment(itemLabel,
				HorizontalPanel.ALIGN_CENTER);
		itemLabel.setStyleName("titulo1");

	}
	
	public void createTabPanel() {
		tabPanel.setVisible(false);
		//tabPanel.add(new HTML("<div id=\"compare\" width=\"100%\" height=\"100%\"></div>"),
		tabPanel.add(new HTML("<div id=\"compare\" width=\"100%\" style=\"display:block !important\"></div>"),
				"Diff");
		ScrollPanel scrollPanelGT = new ScrollPanel();
		//scrollPanelGT.setHeight("400px");
		scrollPanelGT.setHeight("100%");
		
		scrollPanelGT.add(grHTMLPanel);
		scrollPanelGT.setAlwaysShowScrollBars(true);

		ScrollPanel scrollPanelCD = new ScrollPanel();
		//scrollPanelCD.setHeight("400px");
		scrollPanelCD.setHeight("100%");
		scrollPanelCD.add(cdHTMLPanel);
		scrollPanelCD.setAlwaysShowScrollBars(true);
	//	tabPanel.add(scrollPanelCD, "ChangeDistiller");
		tabPanel.add(scrollPanelGT, "GumTree");
		tabPanel.setWidth("100%");
		tabPanel.selectTab(0);
	}

	public void updateView(QuizItem result) {
		putSourceCodeContent(result.getLeftSourceCode(),
				result.getRightSourceCode());
		commitMessageTA.setText(result.getCommitMessage());
		itemLabel.setText(datasetSelected + " -  Item id: "+result.getTransactionID()+ ", Progress: "  + (result.getIndex())
				+ "/" + numberquizItems);
		grHTMLPanel.setHTML(result.getGumtreeDiff());
		//cdHTMLPanel.setHTML(result.getChangeDistillerDiff());
		astTA.setText(result.getAstContent());

	}

	public void hireProjectPanel() {
		projectPanel.setVisible(false);
	}

	public void resetSelection() {
		this.rbBF.setValue(false);
		this.rbNOBF.setValue(false);
		this.rbNothing.setValue(false);

		this.rbGTnotgood.setValue(false);
		this.rbGTgood.setValue(false);
		this.rbGTNeutral.setValue(false);
		
		this.rbBestDiff.setValue(false);
		this.rbBestGT.setValue(false);
		this.rbBestNothing.setValue(false);
		
		this.bfCommentBox.setText("");
		this.anomaliesCB.setValue(false);
	}

	public void showQuizPanels() {
		bugfixlPanel.setVisible(true);
		tabPanel.setVisible(true);
		tabPanel.setWidth("100%");
	}

	public void savePartialQuizItemResult() {
		errorLabel.setText("");
		clicks++;
		QuizItemResult result = extractResult();
		resultQuiz.getResultados().add(result);
		System.out.println("Partial quiz " + resultQuiz.getResultados());
	}

	public QuizItemResult extractResult() {
		QuizItemResult result = new QuizItemResult();
		//IS BUGFIX?
		if (rbBF.isChecked()) {
			result.setIsBugFix(ResultType.BUGFIX.toString());
		} else if (rbNOBF.isChecked()) {
			result.setIsBugFix(ResultType.NOBUGFIX.toString());
		} else if (rbNothing.isChecked()) {
			result.setIsBugFix(ResultType.DONTKNOW.toString());
		}

		//----GT good?
		if (rbGTnotgood.isChecked()) {
			result.setRepresentation(ResultType.GTNOTGOODJOB.toString());
		} else if (rbGTgood.isChecked()) {
			result.setRepresentation(ResultType.GTGOODJOB.toString());
		}else
		if (rbGTNeutral.isChecked()) {
			result.setRepresentation(ResultType.NEUTRAL.toString());
		}

		//----GT VS DIFF
		if(rbBestDiff.isChecked()){
			result.setGtVsDiff(ResultType.DIFF.toString());
		}else if(rbBestGT.isChecked()){
			result.setGtVsDiff(ResultType.GUMTREE.toString());
		}else if(rbBestNothing.isChecked()){
			result.setGtVsDiff(ResultType.DONTKNOW.toString());
		}
			
		//
		result.setComments(bfCommentBox.getText());
		
		//
		result.setAnomalyCD(anomaliesCB.getValue());
		return result;
	}

	private void initUserCombos(){
		userlb.setSelectedIndex(-1);
		datasetlb.setEnabled(false);
		startButton.setEnabled(false);
	}
	
	private static native void putSourceCodeContent(String left, String right)/*-{ 
																				$wnd.putSourceCodeContent(left,right);
																				
																				}-*/;
}
