/**
 * File: TaggerThread.java
 * Date: Apr 17, 2012
 * Author: Morteza Ansarinia <ansarinia@me.com>
 */
package fuschia.tagger;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;

import org.eclipse.swt.widgets.Display;

import fuschia.tagger.common.Document;
import fuschia.tagger.common.DocumentRepository;
import fuschia.tagger.ui.MainWindow;

public class TaggerThread extends Thread {

	private String strWorkingDirectory;
	public DocumentRepository results;

	public TaggerThread(String strWorkingDirectory) {
		super();
		this.strWorkingDirectory = new String(strWorkingDirectory);
		results = null;
	}

	public List<File> getAllFiles(String rootPath) {

		List<File> result = new ArrayList<File>();

		File[] files = new File(rootPath).listFiles();

		String filenameRegex = "[A-Z]{2,4}\\d{1,3}[BC]?(\\sunsure)?-Q[0-9]*\\.txt";

		for (File file : files) {
			// Directories
			if (file.isDirectory() && file.exists() && file.canRead()) {
				List<File> children = getAllFiles(file.getPath());
				result.addAll(children);
				continue;
			}

			// Files
			if (file.isFile() && file.exists() && file.canRead()) {
				if (Pattern.matches(filenameRegex, file.getName())) {
					result.add(file);
				}
			}
		}

		return result;
	}

	public void run() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {

					results = new DocumentRepository();
					POSModel model = new POSModelLoader().load(new File(
							"resources/en-pos-maxent.bin"));
					POSTaggerME tagger = new POSTaggerME(model);

					List<File> files = getAllFiles(strWorkingDirectory);

					MainWindow.getInstance().log(
							"Loading " + strWorkingDirectory + " ...");
					MainWindow.getInstance().log(
							"Number of files to be processed: " + files.size());

					if (files == null || files.size() == 0) {
						results = null;
						return;
					}

					MainWindow.getInstance().log("Processing ...");

					sleep(10); // FIXME: Just to update logs view

					int index = 0;
					
					String strSurveyPrefix = "";
					for (File file : files) {

						MainWindow.getInstance().setProgress(
								(int) (100 * (index++) / files.size()));
						Scanner lineScanner = new Scanner(new FileReader(file));

						String txt = new String();
						while (lineScanner.hasNextLine()) {
							txt = txt + lineScanner.nextLine();
						}

						lineScanner.close();
						lineScanner = null;

						String tokens[] = WhitespaceTokenizer.INSTANCE
								.tokenize(txt);
						String[] tags = tagger.tag(tokens);
						// POSSample sample = new POSSample(tokens, tags);
						// String[] posTags = sample.getTags();

						if (file.getPath().indexOf("SURVEY 1") != -1)
							strSurveyPrefix = "s1.";
						else if (file.getPath().indexOf("SURVEY 2") != -1)
							strSurveyPrefix = "s2.";
						else if (file.getPath().indexOf("SURVEY 3") != -1)
							strSurveyPrefix = "s3.";
						else
							strSurveyPrefix = ".";
						
						String documentId = strSurveyPrefix + file.getName().substring(0,file.getName().length() - 4);
						results.addDocument(documentId, new Document(file.getName(), tokens, tags));
					}

					MainWindow.getInstance().setProgress(100);
					MainWindow.getInstance().log("Finished!");
					MainWindow.getInstance().btnSave.setEnabled(true);
					MainWindow.getInstance().btnSearch.setEnabled(true);

				} catch (Exception e) {
					e.printStackTrace();
					MainWindow.getInstance().log(e.toString());
					MainWindow.getInstance().btnSearch.setEnabled(false);
				}
			}
		});
	}
}
