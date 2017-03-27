package fr.inria.sacha.logic.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class BugFixQuizItem extends QuizItem implements Serializable {

	
	private List<BfInstance> ASTbugFixes = new ArrayList<BfInstance>();

	private List<BfInstance> TokenBugFixes = new ArrayList<BfInstance>();

	private List<BfInstance> CommonBugFixes = new ArrayList<BfInstance>();

	private List<BfInstance> TokenASTBugFixes = new ArrayList<BfInstance>();
	
	public List<BfInstance> getASTbugFixes() {
		return ASTbugFixes;
	}

	public void setASTbugFixes(List<BfInstance> aSTbugFixes) {
		ASTbugFixes = aSTbugFixes;
	}

	public List<BfInstance> getTokenBugFixes() {
		return TokenBugFixes;
	}

	public void setTokenBugFixes(List<BfInstance> tokenBugFixes) {
		TokenBugFixes = tokenBugFixes;
	}

	public List<BfInstance> getCommonBugFixes() {
		return CommonBugFixes;
	}

	public void setCommonBugFixes(List<BfInstance> commonBugFixes) {
		CommonBugFixes = commonBugFixes;
	}

	public List<BfInstance> getTokenASTBugFixes() {
		return TokenASTBugFixes;
	}

	public void setTokenASTBugFixes(List<BfInstance> tokenASTBugFixes) {
		TokenASTBugFixes = tokenASTBugFixes;
	}
	
}
