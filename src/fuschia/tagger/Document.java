package fuschia.tagger;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Document implements Serializable {

	private static final long serialVersionUID = 4890126462310701519L;

	String id;
	String filePath;
	String fileName;
	public String[] tokens;
	public String[] tags;
	public Map<String, Integer> cumulativeTags;

	enum gender {
		Male, Female
	};

	public Document(String id, String[] tokens, String[] tags) {
		this.tokens = new String[tokens.length];
		System.arraycopy(tokens, 0, this.tokens, 0, tokens.length);

		this.tags = new String[tags.length];
		System.arraycopy(tags, 0, this.tags, 0, tags.length);

		cumulativeTags = new HashMap<String, Integer>();
		for (int i = 0; i < tags.length; i++) {
			if (cumulativeTags.get(tags[i]) == null)
				cumulativeTags.put(tags[i], 0);
			cumulativeTags.put(tags[i], cumulativeTags.get(tags[i]) + 1);
		}
	}

	public int size() {
		if (tokens == null || tags == null || tokens.length != tags.length)
			return -1;
		return tokens.length;
	}

}

class Dual {
	String token;
	String tag;
}