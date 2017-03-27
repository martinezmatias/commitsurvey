package fr.inria.sacha.result.client;

import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import fr.inria.sacha.logic.shared.SummaryResult;
import fr.inria.sacha.logic.shared.WaitDialog;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Result implements EntryPoint {
	
	public Label response = new Label();
	RootPanel rootPanel = null;
	WaitDialog wd = new WaitDialog();

	public void onModuleLoad() {
		wd.setModal(true);
		wd.setPopupPosition(100, 100);

		rootPanel = RootPanel.get();
		rootPanel.add(response);
		
		wd.show();
		getInstance().getResults(new AsyncCallback<List<SummaryResult>>() {

			@Override
			public void onSuccess(List<SummaryResult> result) {

				if (result == null || result.size() == 0) {
					response.setText("Any survey made");
				} else {

					response.setText(result.size() + " datasets evaluated");
					for (SummaryResult resultDataset : result) {
						VerticalPanel vpanel = new VerticalPanel();
						
						Label tituloLabel = new Label(resultDataset.dataset);
						vpanel.add(tituloLabel);
						tituloLabel.setStyleName("titulo1");
						
						//BUGFIX
						Label lbf = new Label("Bug Fix evaluation");
						lbf.setStyleName("titulo1Small");
						vpanel.add(lbf);
						Label datasetLabel = new Label("Number surveys made:"
								+ resultDataset.surveys);
						datasetLabel.setStyleName("titulo1Small");
						vpanel.add(datasetLabel);

						vpanel.add(createGridBF(resultDataset));

						Label completeLabel = new Label("Evaluated Items: " + resultDataset.completeBF);
						vpanel.add(completeLabel);
						completeLabel.setStyleName("titulo1Small");
						Label partialLabel = new Label("Partially Evaluated Items: " + resultDataset.partialBF);
						vpanel.add(partialLabel);
						partialLabel.setStyleName("titulo1Small");
						
						//GUMTREE GOOD?
						vpanel.add(new HTML("<hr size=\"10\">"));
					
						Label goodGTLabel = new Label("Is GumTree good?");
						vpanel.add(goodGTLabel);
						goodGTLabel.setStyleName("titulo1Small");
						Label datasetLabelM = new Label("Number surveys made:"
								+ resultDataset.surveys);
						datasetLabelM.setStyleName("titulo1Small");
						vpanel.add(datasetLabelM);
						vpanel.add(createGridIsGTGood(resultDataset));
						
						Label completeLabelG = new Label("Evaluated Items: " + resultDataset.completeGTBest);
						vpanel.add(completeLabelG);
						completeLabelG.setStyleName("titulo1Small");
						Label partialLabelG = new Label("Partially Evaluated Items: " + resultDataset.partialGTBest);
						vpanel.add(partialLabelG);
						partialLabelG.setStyleName("titulo1Small");
					
						
						
						//GT vs. DIFF
						vpanel.add(new HTML("<hr size=\"10\">"));
						Label gtdiffLabel = new Label("GumTree vs. Diff ");
						vpanel.add(gtdiffLabel);
						gtdiffLabel.setStyleName("titulo1Small");
						Label datasetLabelVS = new Label("Number surveys made:"
								+ resultDataset.surveys);
						datasetLabelVS.setStyleName("titulo1Small");
						vpanel.add(datasetLabelVS);

						vpanel.add(createGridGTBetterDiff(resultDataset));
						
					
						Label completeLabelM = new Label("Evaluated Items: " + resultDataset.completeGTPerformance);
						vpanel.add(completeLabelM);
						completeLabelM.setStyleName("titulo1Small");
						Label partialLabelM = new Label("Partially Evaluated Items: " + resultDataset.partialGTPerformance);
						vpanel.add(partialLabelM);
						partialLabelM.setStyleName("titulo1Small");
					
						//----
						rootPanel.add(vpanel);
						
						vpanel.add(new HTML("<hr size=\"10\" noshade>"));
					}
				}
				wd.hide();
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
		});
		
		
	}

	public Grid createGridBF(SummaryResult summaryResult) {
		Grid g = new Grid(4, 5);
		g.setBorderWidth(1);
		g.setText(0, 0, "Evaluation \\ Agreement");
		g.setText(0, 1, "Full");
		g.setText(0, 2, "Majority");
		g.setText(0, 3, "Partial");
		g.setText(0, 4, "None");
		g.setText(1, 0, "Bug Fix");
		g.setText(2, 0, "Not Bug Fix");
		g.setText(3, 0, "I dont know");

		for (int row = 1; row <= 3; row++) {
			for (int column = 1; column <= 4; column++) {
				g.setText(row, column, Integer.toString(summaryResult.isBugFix[row - 1][column - 1]));
			}
		}
		return g;

	}

	public Grid createGridGTBetterDiff(SummaryResult summaryResult) {
		Grid g = new Grid(4, 5);
		g.setBorderWidth(1);
		g.setText(0, 0, "Evaluation \\ Agreement");
		g.setText(0, 1, "Full");
		g.setText(0, 2, "Majority");
		g.setText(0, 3, "Partial");
		g.setText(0, 4, "None");
	//
		g.setText(1, 0, "GumTree");
		g.setText(2, 0, "Diff");
		g.setText(3, 0, "The Same");

		for (int row = 1; row <= 3; row++) {
			for (int column = 1; column <= 4; column++) {
				g.setText(row, column, Integer.toString(summaryResult.gtBetterThanDiff[row - 1][column - 1]));
			}
		}
		return g;

	}

	public Grid createGridIsGTGood(SummaryResult summaryResult) {
		Grid g = new Grid(4, 5);
		g.setBorderWidth(1);
		g.setText(0, 0, "Evaluation \\ Agreement");
		g.setText(0, 1, "Full");
		g.setText(0, 2, "Majority");
		g.setText(0, 3, "Partial");
		g.setText(0, 4, "None");
		g.setText(1, 0, "GumTree does good job");
		g.setText(2, 0, "GumTree does not good job");
		g.setText(3, 0, "Neutral");

		for (int row = 1; row <= 3; row++) {
			for (int column = 1; column <= 4; column++) {
				g.setText(row, column, Integer.toString(summaryResult.gtGoodJob[row - 1][column - 1]));
			}
		}
		return g;

	}
	
	private static ResultServiceAsync instance;

	public static ResultServiceAsync getInstance() {
		if (instance == null) {
			instance = GWT.create(ResultService.class);
		}
		return instance;
	}
}
