package fuschia.tagger;

import fuschia.tagger.ui.*;

public class Main {
	public static void main(String[] args) {
		try {
			MainWindow window = new MainWindow();
			window.open();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
