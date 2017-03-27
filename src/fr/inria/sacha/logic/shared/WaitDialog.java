package fr.inria.sacha.logic.shared;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;

public class WaitDialog extends DialogBox  {

	public WaitDialog() {
	    setText("Survey");
	    HTML msg = new HTML("<center>Please wait...</center>",true);
	    DockPanel dock = new DockPanel();
	    dock.setSpacing(4);
	    dock.add(msg, DockPanel.NORTH);
	    dock.setWidth("100%");
	    setWidget(dock);
	  }
}
