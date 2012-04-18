/**
 * File: TaggerThread.java
 * Date: Apr 17, 2012
 * Author: Morteza Ansarinia <ansarinia@me.com>
 */
package fuschia.tagger;

import java.util.Map;

import org.eclipse.swt.widgets.Display;

import fuschia.tagger.ui.MainWindow;

public class TaggerThread extends Thread {

	private String strWorkingDirectory;
	
	
	public TaggerThread(String strWorkingDirectory) {
		super();
		this.strWorkingDirectory = new String(strWorkingDirectory);
	}


	public void run() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					MainWindow.getInstance().taggerResult = MaxentPOSTagger.INSTANCE.extractTags(strWorkingDirectory);

					MainWindow.getInstance().log("OK!");
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
