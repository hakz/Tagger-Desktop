package salar.tagger;

import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class SimplePOSTagger {
	
	private static SimplePOSTagger instance = null;
	public SimplePOSTagger() {
		instance = null;
	}

	public static SimplePOSTagger getInstance() {
		if (instance == null)
			instance = new SimplePOSTagger();
		return instance;
	}
	
	public ArrayList<String> extractTags(String strDirPath) throws Exception {

		ArrayList<String> results = new ArrayList<String>();
		POSModel model = new POSModelLoader().load(new File("resources/en-pos-maxent.bin"));
		POSTaggerME tagger = new POSTaggerME(model);

		String[] filesPath = new File(strDirPath).list();
		
		if(filesPath == null || filesPath.length==0)
			return null;
		
		Scanner lineScanner;
		File txtFile;
		for ( String txtFilePath : filesPath ) {
			txtFile = new File(txtFilePath);
			lineScanner = new Scanner(new FileReader(txtFile));

			while (lineScanner.hasNextLine()) {

				String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE.tokenize(lineScanner.nextLine());
				String[] tags = tagger.tag(whitespaceTokenizerLine);
				
				POSSample sample = new POSSample(whitespaceTokenizerLine, tags);

				String[] posTags = sample.getTags();
				for (String tag:posTags)
					results.add(tag);
			}
			lineScanner.close();
			lineScanner = null;
		}

		return results;
	}
}