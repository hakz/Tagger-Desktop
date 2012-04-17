/**
 * File: TaggerThread.java
 * Date: Apr 17, 2012
 * Author: Morteza Ansarinia <ansarinia@me.com>
 */
package fuschia.tagger;

import java.util.Map;

public class TaggerThread extends Thread {

	private String strWorkingDirectory;
	public Map<String, Document> result;
	
	
	public TaggerThread(String strWorkingDirectory) {
		super();
		this.strWorkingDirectory = new String(strWorkingDirectory);
	}


	public void run() {
		try {
			result = MaxentPOSTagger.INSTANCE.extractTags(strWorkingDirectory);

			//MainWindow.getInstance().log("OK!");
			//MainWindow.getInstance().btnSearch.setEnabled(true);
		} catch (Exception e) {
			e.printStackTrace();
			result = null;
			//MainWindow.getInstance().log(e.toString());
			//MainWindow.getInstance().btnSearch.setEnabled(false);
		}

	}
}
