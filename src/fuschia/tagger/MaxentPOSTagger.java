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
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class MaxentPOSTagger implements POSTagger{
	
	public static MaxentPOSTagger INSTANCE = new MaxentPOSTagger();
	public MaxentPOSTagger() {
	}
	
	public Map<String,Document> extractTags(String dirPath) throws Exception {

		Map<String,Document> results = new HashMap<String,Document>();
		POSModel model = new POSModelLoader().load(new File("resources/en-pos-maxent.bin"));
		POSTaggerME tagger = new POSTaggerME(model);

		List<File> files = getAllFiles(dirPath);
		
		System.out.println("Num of Files: " + files.size());
		System.out.println("-------------------");
		if(files == null || files.size() == 0)
			return null;
		
		for ( File file : files ) {

			Scanner lineScanner = new Scanner(new FileReader(file));

			String txt = new String();
			while (lineScanner.hasNextLine()) {
				txt = txt + lineScanner.nextLine();
			}

			lineScanner.close();
			lineScanner = null;
			
			String tokens[] = WhitespaceTokenizer.INSTANCE.tokenize(txt);
			String[] tags = tagger.tag(tokens);	
			//POSSample sample = new POSSample(tokens, tags);
			//String[] posTags = sample.getTags();

			results.put(file.getName().substring(0, file.getName().length() - 4), new Document(file.getName(), tokens, tags));
		}

		return results;
	}
	
	public List<File> getAllFiles(String rootPath) {
		
		List<File> result = new ArrayList<File>();
		
		File[] files = new File(rootPath).listFiles();

		String filenameRegex = "[A-Z]{2,4}\\d{1,3}-Q[0-9]*\\.txt";
		
		for(File file : files) {
			// Directories
			if ( file.isDirectory() && file.exists() && file.canRead()) {
				List<File> children = getAllFiles(file.getPath());
				result.addAll(children);
				continue;
			}

			// Files
			if (file.isFile() && file.exists() && file.canRead()) {
				if ( Pattern.matches(filenameRegex, file.getName()) ) {
					result.add(file);				
				}
			}
		}
		
		return result;
	}
}