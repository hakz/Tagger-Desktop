package fuschia.tagger;

public class Document {
	String id;
	String filePath;
	String fileName;
	String[] tokens;
	String[] tags;
	
	enum gender {Male, Female};
	
	public Document(String id, String[] tokens, String[] tags) {
		this.tokens = new String[tokens.length];
		System.arraycopy(tags, 0, this.tokens, 0, tokens.length);

		this.tags = new String[tags.length];
		System.arraycopy(tags, 0, this.tags, 0, tags.length);
	}
	
}

class Dual {
	String token;
	String tag;
}