package fr.inria.sacha.logic.shared;

import java.io.Serializable;

public enum ResultType implements Serializable {

	BUGFIX,
	NOBUGFIX,
	DONTKNOW,
	
	CHANGEDISTILLER,
	GUMTREE,
	DIFF,
	
	GTGOODJOB,
	GTNOTGOODJOB,
	NEUTRAL,
	
	BUGFIXPATTERN,
	NOTBUGFIXPATTERN;
	
	private ResultType() {
	}
	
	
}
