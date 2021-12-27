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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class Learner {
	public static final int NONE = 0, LEFT_TO_RIGHT = 1, RIGHT_TO_LEFT = 2, IN_TO_OUT = 3, OUT_TO_IN = 4, RANDOM = 5;
	
	List<Vocab> vocab_list;
	int current_vocab_idx = -1;
	int hint_format = NONE;
	String current_hint;
	int current_hint_count;
	Vocab current_vocab;
	
	public static int CORRECT_SCORE = 10, INCORRECT_SCORE = -4;
	
	boolean ignore_case, ignore_accents, ignore_special;
	boolean learn_src = true; // false = learn_target

	public Learner() 
	{
	}
	
	public void reset() 
	{
		current_vocab_idx = -1;
		current_vocab = null;
		current_hint = null;
		current_hint_count = 0;
	}
	
	public Vocab nextVocab()
	{
		current_hint = null;
		current_hint_count = 0;

		if(current_vocab_idx != vocab_list.size() - 1) {
			current_vocab = vocab_list.get(++current_vocab_idx);
			return current_vocab;
		}
		
		return null;
	}
	
	
	public String nextHint()
	{
		Vocab vocab = current_vocab;
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
		
		++current_hint_count;
		
		return current_hint;
	}
	
	public boolean submit(String word)
	{
		boolean result = (compare(word) == 0);
		
		Stats stats = learn_src ? current_vocab.src_to_tgt : current_vocab.tgt_to_src;
		if(result) {
			 ++current_vocab.session_correct;
			 current_vocab.last_practiced = LocalDateTime.now();
			 stats.addScore(CORRECT_SCORE);
			 stats.incrementSubmissions();
			
		}else {
			stats.addScore(INCORRECT_SCORE - current_hint_count);
		}
		
		return result;
	}
	
	public int compare(String word)
	{
		String current = learn_src ? current_vocab.target : current_vocab.source;
		
		if(ignore_case) {
			word = word.toLowerCase();
			current = current.toLowerCase();
		}
		
		if(ignore_accents) {
			word = StringUtils.stripAccents(word);
			current = StringUtils.stripAccents(current);
		}
		
		if(ignore_special) {
			word = stripSpecial(word);
			current = stripSpecial(current);
		}
			

		return word.compareTo(current);
	}
	
	public int compareVocab(Vocab vocab)
	{
		return compare(learn_src ? vocab.source : vocab.target);
	}
	
	public String swap()
	{
		current_hint = null;
		learn_src = !learn_src;
		return  learn_src ? current_vocab.source : current_vocab.target;
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
	
	public void setVocabList(List<Vocab> vocab_list){
		this.vocab_list = vocab_list;
		reset();
	}

	public boolean isLoaded() {
		return vocab_list!= null;
	}
	
	public Vocab getCurrentVocab() {
		return current_vocab;
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
