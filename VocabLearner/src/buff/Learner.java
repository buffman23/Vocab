package buff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class Learner {
	public static final int NONE = 0, LEFT_TO_RIGHT = 1, RIGHT_TO_LEFT = 2, IN_TO_OUT = 3, OUT_TO_IN = 4, RANDOM = 5;
	
	String vocab_file_name;
	File vocab_file;
	List<Vocab> vocab_list;
	int current_vocab_idx = -1;
	int hint_format = NONE;
	String current_hint;
	
	boolean ignore_case, ignore_accents, ignore_special;
	boolean learn_src = true; // false = learn_target

	public Learner() 
	{
	}
	

	@SuppressWarnings("unchecked")
	public void load(String input_file_name) throws ClassNotFoundException, IOException
	{
		File vocab_file = new File(input_file_name);
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(vocab_file));
		
		vocab_list = (ArrayList<Vocab>) ois.readObject();
		ois.close();
		
		this.vocab_file_name = input_file_name;
	}
	
	public void loadCSV(String vocab_file_name) throws IOException
	{
		File vocab_file = new File(vocab_file_name);
		BufferedReader br = new BufferedReader(new FileReader(vocab_file));
		
		int lines = 0;
		while(br.readLine() != null) ++lines;
		
		vocab_list = new ArrayList<>(lines);
		
		br.close();
		br = new BufferedReader(new FileReader(vocab_file));
		
		br.readLine(); // skip header
		for(String line; (line = br.readLine()) != null;) {
			if(line.length() > 0)
				vocab_list.add(new Vocab(line.split(",")));
		}
		
		this.vocab_file_name = vocab_file_name;
	}
	
	public void save(String output_file_name) throws IOException
	{
		File vocab_file = new File(output_file_name);
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(vocab_file));
		
		oos.writeObject(vocab_list);
		oos.close();
	}
	
	public void reset() 
	{
		current_vocab_idx = -1;
	}
	
	public void shuffle()
	{
		Collections.shuffle(vocab_list);
		reset();
	}
	
	public String next()
	{
		Vocab vocab = nextWord();
		
		if(vocab == null) return null;
		
		return  learn_src ? vocab.source : vocab.target;
	}
	
	public Vocab nextWord()
	{
		current_hint = null;
		
		if(current_vocab_idx != vocab_list.size() - 1)
			return vocab_list.get(++current_vocab_idx);
		
		return null;
	}
	
	
	public String nextHint()
	{
		Vocab vocab = vocab_list.get(current_vocab_idx);
		String word_str = learn_src ? vocab.target : vocab.source;
		if(current_hint == null) {
			current_hint = StringUtils.repeat("_", learn_src ? word_str.length() : word_str.length());
		}
		
		int idx = current_hint.indexOf('_');
		
		if(idx == -1)
			return null;
		
		switch(hint_format)
		{
			case LEFT_TO_RIGHT: 
				idx = current_hint.indexOf('_');
				break;
			case RIGHT_TO_LEFT: 
				idx = current_hint.lastIndexOf('_');
				break;
			case IN_TO_OUT:
				int mid_idx = current_hint.length()/2;
				for(int i = 0; i <= current_hint.length()/2; ++i) {
					if(mid_idx + i < current_hint.length() && current_hint.charAt(mid_idx + i) == '_') {
						idx = mid_idx + i;
						break;
					}else if(mid_idx - i >= 0 && current_hint.charAt(mid_idx - i) == '_') {
						idx = mid_idx - i;
						break;
					}
				}
				break;
			case OUT_TO_IN:
				mid_idx = current_hint.length()/2;
				int dist1 = mid_idx - current_hint.indexOf('_');
				int dist2 = current_hint.lastIndexOf('_') - mid_idx;
				if(dist1 >= dist2) {
					idx = current_hint.indexOf('_');
				}else {
					idx = current_hint.lastIndexOf('_');
				}
				break;
			case RANDOM:
				int count = StringUtils.countMatches(current_hint, "_");
				int[] indecies = new int[count];
				int j = 0;
				for(int i = 0; i < current_hint.length(); ++i) {
					if(current_hint.charAt(i) == '_') {
						indecies[j++] = i;
					}
				}
				
				idx = indecies[(int)(Math.random()*indecies.length)];
				break;
			default:
				return null;
		}
		
		
		StringBuilder sb = new StringBuilder(current_hint);
		sb.setCharAt(idx, word_str.charAt(idx));
		current_hint = sb.toString();
		
		return current_hint;
	}
	
	public int compare(String vocab)
	{
		Vocab current_vocab = vocab_list.get(current_vocab_idx);
		String current = learn_src ? current_vocab.target : current_vocab.source;
		
		if(ignore_case) {
			vocab = vocab.toLowerCase();
			current = current.toLowerCase();
		}
		
		if(ignore_accents) {
			vocab = StringUtils.stripAccents(vocab);
			current = StringUtils.stripAccents(current);
		}
		
		if(ignore_special) {
			vocab = stripSpecial(vocab);
			current = stripSpecial(current);
		}
			

		return vocab.compareTo(current);
	}
	
	public int compareVocab(Vocab vocab)
	{
		return compare(learn_src ? vocab.source : vocab.target);
	}
	
	public String swap()
	{
		current_hint = null;
		learn_src = !learn_src;
		Vocab vocab = vocab_list.get(current_vocab_idx);
		return  learn_src ? vocab.source : vocab.target;
	}
	
	public boolean getIgnoreCase() {
		return ignore_case;
	}

	public void setIgnoreCase(boolean ignore_case) {
		this.ignore_case = ignore_case;
	}

	public boolean getIgnoreAccents() {
		return ignore_accents;
	}

	public void setIgnoreAccents(boolean ignore_accents) {
		this.ignore_accents = ignore_accents;
	}

	public boolean getIgnoreSpecial() {
		return ignore_special;
	}

	public void setIgnoreSpecial(boolean ignore_special) {
		this.ignore_special = ignore_special;
	}

	public boolean isLearnSource() {
		return learn_src;
	}

	public void setLearnSource(boolean learn_src) {
		this.learn_src = learn_src;
	}
	
	public int getHintFormat() {
		return hint_format;
	}

	public void setHintFormat(int hint_format) {
		this.hint_format = hint_format;
		current_hint = null;
	}
	
	public List<Vocab> getVocabList() {
		return vocab_list; 
	}

	public String getVocabFileName() {
		return vocab_file_name;
	}

	public boolean isLoaded() {
		return vocab_list!= null;
	}
	
	private String stripSpecial(String str)
	{
		StringBuilder sb = new StringBuilder(str.length());
		
		for(int i = 0; i < str.length(); ++i)
		{
			char c = str.charAt(i);
			if(Character.isLetter(c)) {
				sb.append(c);
			}
		}
		
		return sb.toString();
	}
	
}
