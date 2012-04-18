package fuschia.tagger;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import fuschia.tagger.ui.*;

public class Main {
	public static void main(String[] args) {
		try {
			MainWindow.getInstance().open();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
