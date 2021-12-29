package buff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class User {
	private String username;
	private List<Vocab> vocab_list;
	
	public User(String username) {
		this(username, new ArrayList<Vocab>());
	}
	
	public User(String username, List<Vocab> vocab_list) {
		this.username = username;
		this.vocab_list = vocab_list;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<Vocab> getVocabList() {
		return vocab_list;
	}

	public void setVocabList(List<Vocab> vocab_list) {
		this.vocab_list = vocab_list;
	}
	
	public void addVocab(Collection<Vocab> vocab_collection) {
		if(this.vocab_list == null)
			this.vocab_list = new ArrayList<Vocab>(vocab_collection);
		else {
			for(Vocab v : vocab_collection)
			if(!this.vocab_list.contains(v))
				this.vocab_list.add(v);
		}
	}
}
