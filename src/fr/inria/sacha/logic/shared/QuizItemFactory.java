package fr.inria.sacha.logic.shared;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class QuizItemFactory {

	private static final Logger log = Logger.getLogger(QuizItemFactory.class.getName());

	public QuizItem createQuizItem(File transactionFolder) {
		// log.info("Pick commit message") ;
		QuizItem quizItem = new QuizItem();
		return createQuizItem(transactionFolder, quizItem);
	}

	public QuizItem createQuizItem(File transactionFolder, QuizItem quizItem) {
		quizItem.setCommitMessage(getCommitMessage(transactionFolder));
		log.info("Case "+transactionFolder.getName());
		// log.info("Pick left ") ;
		File leftf = pickFileWithPrefix(transactionFolder.listFiles(), "left_");
		String leftcontent = readFile(leftf);
		quizItem.setLeftSourceCode(leftcontent);

		// log.info("Pick right") ;
		File rightf = pickFileWithPrefix(transactionFolder.listFiles(), "right_");
		String rightcontent = readFile(rightf);
		quizItem.setRightSourceCode(rightcontent);

		// log.info("Pick ast list") ;
		File astf = pickFileWithPrefix(transactionFolder.listFiles(), "ast");
		String astcontent = readFile(astf);
		quizItem.setAstContent(astcontent);

		// log.info("Pick html GT");
		File gt = pickFileWithPrefix(transactionFolder.listFiles(), "outhmtGT");
		if (gt != null) {
			String gtdiff = readFile(gt);
			quizItem.setGumtreeDiff(gtdiff);
		}
		// log.info("end loading");
		// CHANGE DISTILLER HTML
		/*
		 * File cd = pickFileWithPrefix(transactionFolder.listFiles(),
		 * "outhmtCD"); if(cd != null){ String cddiff = readFile(cd);
		 * quizItem.setChangeDistillerDiff(cddiff); }
		 */

		quizItem.setTransactionID(getId(transactionFolder));

		return quizItem;
	}

	private String getId(File file) {

		return file.getName().replace("t_", "");
	}

	private String getCommitMessage(File dir) {
		File commit = new File(dir.getPath() + File.separator + "commitMessage.txt.java");
		return readFile(commit);
	}

	private String readFile(File p) {
		String allText = "";
		FileInputStream fis;
		try {
			fis = new FileInputStream(p);
			Scanner scan = new Scanner(fis, "UTF-8");
			if(scan.useDelimiter("\\A").hasNext())
				allText = scan.useDelimiter("\\A").next();

		} catch (FileNotFoundException e1) {

			log.severe(e1.getMessage());
			// e1.printStackTrace();
		}

		return allText;

	}

	public File pickFileWithPrefix(File[] files, String name) {
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().startsWith(name)) {
				return files[i];
			}
		}
		return null;
	}

	public BugFixQuizItem createBugFixItem(File transactionFolder) {
		log.info(transactionFolder.getName());
		BugFixQuizItem bitem = new BugFixQuizItem();
		createQuizItem(transactionFolder, bitem);
		File common = pickFileWithPrefix(transactionFolder.listFiles(), "common");
		retrievePatterns(bitem.getCommonBugFixes(),common);
		
		/*File token = pickFileWithPrefix(transactionFolder.listFiles(), "token");
		retrievePatterns(bitem.getTokenBugFixes(),token);*/
		
		File ast = pickFileWithPrefix(transactionFolder.listFiles(), "ast");
		retrievePatterns(bitem.getASTbugFixes(),ast);
		
		File token = pickFileWithPrefix(transactionFolder.listFiles(), "token.");
		retrievePatterns(bitem.getTokenBugFixes(),token);
		
		File tokenAST = pickFileWithPrefix(transactionFolder.listFiles(), "token-ast");
		retrievePatterns(bitem.getTokenASTBugFixes(),tokenAST);
		
		return bitem;

	}

	public void retrievePatterns(List<BfInstance> patterns, File pan) {
		if (pan != null) {
			String panAll = readFile(pan);
			if ("".equals(panAll))
				return;
			String[] lines = panAll.split("\n");
			for (String line : lines) {
				String lineMod = line.replace("[", "").replace("]", "");
				String[] patternsInLine = lineMod.split(",");
				for (String pi : patternsInLine) {
							
				String[] sp = pi.split("\\*");
				String loff = (sp.length>1 && sp[1].startsWith("L"))?sp[1]:"";  
				String roff = ""; 
				if((sp.length>2 && sp[2].startsWith("R")))
					roff = sp[2];
				else if(sp.length>1 && sp[1].startsWith("R"))
					roff= sp[1];
					BfInstance bfi = new BfInstance(0, "", sp[0].trim() + " "
							+ BugFixNameResolver.names.get(sp[0].trim()) + " " + loff + " " + roff);
				patterns.add(bfi);
				}
			}

		}
	}

}
