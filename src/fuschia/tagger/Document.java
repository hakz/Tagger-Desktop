package fuschia.tagger;

public class Document {
	String id;
	String filePath;
	String fileName;
	public String[] tokens;
	public String[] tags;
	
	enum gender {Male, Female};
	
	public Document(String id, String[] tokens, String[] tags) {
		this.tokens = new String[tokens.length];
		System.arraycopy(tags, 0, this.tokens, 0, tokens.length);

		this.tags = new String[tags.length];
		System.arraycopy(tags, 0, this.tags, 0, tags.length);
	}
	
	public int size() {
		if (tokens == null
				|| tags == null
				|| tokens.length != tags.length )
			return -1;
		return tokens.length;
	}
	
}

class Dual {
	String token;
	String tag;
}