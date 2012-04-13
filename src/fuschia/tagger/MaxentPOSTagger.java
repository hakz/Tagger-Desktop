package fuschia.tagger;

import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MaxentPOSTagger implements POSTagger{
	
	public static MaxentPOSTagger INSTANCE = new MaxentPOSTagger();
	public MaxentPOSTagger() {
	}
	
	public Map<String,Document> extractTags(String dirPath) throws Exception {

		Map<String,Document> results = new HashMap<String,Document>();
		POSModel model = new POSModelLoader().load(new File("resources/en-pos-maxent.bin"));
		POSTaggerME tagger = new POSTaggerME(model);

		File[] files = new File(dirPath).listFiles();
		
		System.out.println("Num of Files: " + files.length);
		System.out.println("-------------------");
		if(files == null || files.length == 0)
			return null;
		
		for ( File file : files ) {

			if (!file.exists())
				continue;

			//TODO: check if filename is correct, otherwise continue to the next one
			
			Scanner lineScanner = new Scanner(new FileReader(file));

			String txt = new String();
			while (lineScanner.hasNextLine()) {
				txt = txt + lineScanner.nextLine();
			}

			lineScanner.close();
			lineScanner = null;
			
			String tokens[] = WhitespaceTokenizer.INSTANCE.tokenize(txt);
			String[] tags = tagger.tag(tokens);	
			POSSample sample = new POSSample(tokens, tags);
			String[] posTags = sample.getTags();
			
			System.out.print("> " + file.getName() + " : ");
			System.out.println(posTags[0]);
			results.put(file.getName(), new Document(file.getName(), posTags));
		}

		return results;
	}
}