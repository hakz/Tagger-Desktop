package fuschia.tagger;

import java.util.Map;

import fuschia.tagger.ui.*;

public class Main {
	public static void main(String[] args) {
		try {
			//MainWindow window = new MainWindow();
			//window.open();
			
			String strSelectedDir = "/Volumes/Personal HD/Friends/Salar/ARCHIVE TYPING - SURVEY 1/ARCHIVE TYPING.S1-Q1/S1-Q1 TEXT FILE FORMAT";
			Map<String, Document> posTags = MaxentPOSTagger.INSTANCE.extractTags(strSelectedDir);
			System.out.println(strSelectedDir);
			System.out.println("---------");
			System.out.println(posTags.size());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
