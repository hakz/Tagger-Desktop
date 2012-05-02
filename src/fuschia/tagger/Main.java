package fuschia.tagger;

import fuschia.tagger.ui.MainWindow;

public class Main {
	public static void main(String[] args) {
		try {
						
			//ConstrualProcessor construal = new ConstrualProcessor("/Volumes/Personal HD/Friends/Salar/");
			//construal.start();
			//while(construal.isAlive()){}
			
			MainWindow.getInstance().open();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
