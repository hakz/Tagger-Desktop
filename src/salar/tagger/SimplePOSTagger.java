package salar.tagger;

import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

public class SimplePOSTagger {
	public static void main(String[] args) throws IOException {
		POSModel model = new POSModelLoader().load(new File("resources/en-pos-maxent.bin"));
		PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
		POSTaggerME tagger = new POSTaggerME(model);

		String input = "I was sitting in my 5th grade geography class at my small school in Whitby, Ontario when our teacher told us there had been an attack in New York City, but he didn’t give us specifics on what had happened. He explained to us what terrorism was for the first time. Ironically, we had learned the day before about democracy, and how lucky we were to live in a safe country that was run by the people. My grandmother is American, so I saw no difference between the United States and Canada at the time- I hurt as deeply as any American did. I remember not understanding why someone would want to hurt Americans, and I worried for the rest of the day. We weren’t able to watch TV or see pictures until we left school, and I ran home past the houses of my neighbours, many of whom were crying audibly in their living rooms. When I got inside, my mother was on the (primitive) computer looking at pictures of the towers. She sat me down, and told me that a lot of people were missing, probably dead. She then told me that the CN Tower had been shut down, in case there was a similar attack on Toronto. Looking back, this day where I realized that living in North America was not a guarantee of security was truly the day my childhood ended.";
		ObjectStream<String> lineStream = new PlainTextByLineStream(new StringReader(input));

		perfMon.start();
		String line;
		while ((line = lineStream.read()) != null) {

			String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE.tokenize(line);
			String[] tags = tagger.tag(whitespaceTokenizerLine);

			POSSample sample = new POSSample(whitespaceTokenizerLine, tags);
			System.out.println(sample.toString());

			perfMon.incrementCounter();
		}
		perfMon.stopAndPrintFinalResult();
	}
}