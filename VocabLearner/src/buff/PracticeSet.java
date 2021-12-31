package buff;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class PracticeSet {
	public static final int NONE = 0, LEFT_TO_RIGHT = 1, RIGHT_TO_LEFT = 2, IN_TO_OUT = 3, OUT_TO_IN = 4, RANDOM = 5;
	public static final int CORRECT_SCORE = 2, INCORRECT_SCORE = -2, STREAK_FINISH_LENGTH = 3, STREAK_FINISHED_MAX_MULT = 2;
	private static final char NON_BREAKING_SPACE = '\u00A0';
	List<Vocab> vocab_list;
	List<Vocab> working_vocab_list;
	List<Integer> streak_list;
	int current_vocab_idx = -1;
	int hint_format = NONE;
	int current_hint_count;
	int set_iterations; // total iterations over the current vocab set
	int finished_this_iteration; // count of vocab that have finished on same set_iteration
	Vocab current_vocab;
	String delimiter_regex, delimiters;
	
	boolean ignore_case, ignore_accents, ignore_special;
	boolean learn_tgt = true; // false = learn_target
	

	public PracticeSet() 
	{
	}
	
	
	public void reset() 
	{
		current_vocab_idx = -1;
		current_vocab = null;
		current_hint_count = 0;
		set_iterations = 0;
		finished_this_iteration = 0;
		working_vocab_list = new ArrayList<Vocab>(vocab_list);
		
		streak_list = new ArrayList<Integer>(vocab_list.size());
		for(int i = 0; i < vocab_list.size(); ++i) {
			streak_list.add(0);
		}
	}
	
	public Vocab getVocab()
	{
		return current_vocab;
	}
	
	public Vocab nextVocab()
	{
		if(working_vocab_list.size() == 0) {
			return null;
		}
		
		if(current_vocab_idx >= working_vocab_list.size() - 1) {
			current_vocab_idx = -1;
			finished_this_iteration = 0;
			++set_iterations;
		}
		
		current_vocab = working_vocab_list.get(++current_vocab_idx);

		current_hint_count = 0;
		
		return current_vocab;
	}
	
	public String getHint()
	{
		if(hint_format == NONE || current_vocab == null) return "";
		return getHint(current_hint_count);
	}
	
	public String nextHint()
	{
		++current_hint_count;
		return getHint();
	}
	
	private String getHint(int hint_count)
	{
		Vocab vocab = current_vocab;
		String full_word_str = learn_tgt ? vocab.target : vocab.source;
		
		Stats stats = learn_tgt ? current_vocab.getStatsTgt() : current_vocab.getStatsSrc();
		//hint_count += (int)(full_word_str.length() * Math.max(1 - 2*stats.getScore()/100.0, 0) + 0.5);
		
		//if(hint_count == 0) return "";
		
		String[] word_str_split;
		if(delimiter_regex != null)
			word_str_split = split(full_word_str, delimiters, true);
		else 
			word_str_split = new String[] {full_word_str};
		
		StringBuilder full_sb = new StringBuilder(full_word_str.length());
		
		for(int m = 0; m < word_str_split.length; ++m)
		{
			String word_str = word_str_split[m];
			
			int split_hint_count = hint_count + (int)(word_str.length() * Math.max(1 - 2*stats.getScore()/100.0, 0) + 0.5);
			if(split_hint_count == 0) continue;
			
			StringBuilder sb = new StringBuilder(word_str.length());
			
			for(int i = 0; i < word_str.length(); ++i) {
				char c = word_str.charAt(i);
				if(delimiters.indexOf(c) == -1 && !Character.isWhitespace(c) && c != NON_BREAKING_SPACE) 
					sb.append('_');
				else
					sb.append(c);
			}
			
			int mid_idx = (sb.lastIndexOf("_") - sb.indexOf("_"))/2;
			
			for(int k = 0; k < split_hint_count && k < word_str.length(); ++k) {
				int idx = -1;
				switch(hint_format)
				{
					case LEFT_TO_RIGHT: 
						idx = sb.indexOf("_");
						break;
					case RIGHT_TO_LEFT: 
						idx = sb.lastIndexOf("_");
						break;
					case IN_TO_OUT:
						for(int i = 0; i <= sb.length()/2; ++i) {
							if(mid_idx + i < sb.length() && sb.charAt(mid_idx + i) == '_') {
								idx = mid_idx + i;
								break;
							}else if(mid_idx - i >= 0 && sb.charAt(mid_idx - i) == '_') {
								idx = mid_idx - i;
								break;
							}
						}
						break;
					case OUT_TO_IN:
						int dist1 = mid_idx - sb.indexOf("_");
						int dist2 = sb.lastIndexOf("_") - mid_idx;
						if(dist1 >= dist2) {
							idx = sb.indexOf("_");
						}else {
							idx = sb.lastIndexOf("_");
						}
						break;
					case RANDOM:
						int count = StringUtils.countMatches(sb, "_");
						if(count == 0)
							break;
						int[] indecies = new int[count];
						int j = 0;
						for(int i = 0; i < sb.length(); ++i) {
							if(sb.charAt(i) == '_') {
								indecies[j++] = i;
							}
						}
						
						idx = indecies[(int)(Math.random()*indecies.length)];
						break;
					default:
						return null;
				}
				
				if(idx == -1)
					break;
				
				sb.setCharAt(idx, word_str.charAt(idx));
			}
			
			full_sb.append(sb);
		}

		return full_sb.toString();
	}
	
	public boolean submit(String word)
	{
		String full_vocab_word = learn_tgt ? current_vocab.target : current_vocab.source;
		
		boolean result = false;
		
		if(compare(word, full_vocab_word) == 0) {
			result = true;
		}else {
			String[] vocab_word_split;
			if(delimiter_regex != null)
				vocab_word_split = split(full_vocab_word, delimiters, false);
			else 
				vocab_word_split = new String[] {full_vocab_word};
			
			for(String vocab_word : vocab_word_split) {
				if(compare(word, vocab_word) == 0) 
					result = true;
			}
		}
			
		Stats stats = learn_tgt ? current_vocab.getStatsTgt() : current_vocab.getStatsSrc();
		if(result) {
			int current_streak = streak_list.get(current_vocab_idx);
			 ++current_vocab.session_correct;
			 current_vocab.last_practiced = LocalDateTime.now();
			 /* 
			  * score points based on:
			  * 1. base points
			  * 2. vocab set size multiplier
			  */
			 double score = CORRECT_SCORE;
			 if(hint_format == NONE) score *= 2;
			 
			 
			
			 current_streak += 1;
			 if(current_streak == STREAK_FINISH_LENGTH) {
				 working_vocab_list.remove(current_vocab_idx);
				 streak_list.remove(current_vocab_idx);
				 --current_vocab_idx;
				 score *= (1 + Math.log(++finished_this_iteration + working_vocab_list.size())/Math.log(2));
				 stats.addScore(score);
			 }else {
				 streak_list.set(current_vocab_idx, current_streak);
				 stats.addScore(score);
			 }
			 
			 System.out.printf("Score: %f\n", score);
			 
			 stats.incrementAttempts();
			 //System.out.printf("Streak: %d\n", current_streak);
		}else {
			streak_list.set(current_vocab_idx, 0);
			//System.out.printf("Streak: %d\n", 0);
			double score = (int)(INCORRECT_SCORE /** (1 + Math.log(working_vocab_list.size())/Math.log(8)) + 0.5*/);
			System.out.printf("Score: %f\n", score);
			stats.addScore(score);
		}
		
		return result;
	}
	
	public int compare(String word, String other)
	{
		if(ignore_case) {
			word = word.toLowerCase();
			other = other.toLowerCase();
		}
		
		if(ignore_accents) {
			word = StringUtils.stripAccents(word);
			other = StringUtils.stripAccents(other);
		}
		
		if(ignore_special) {
			word = stripSpecial(word);
			other = stripSpecial(other);
		}
			

		return word.compareTo(other);
	}
	
	public int compareVocab(Vocab vocab)
	{
		if(learn_tgt)
			return compare(vocab.target, current_vocab.target);
		
		return compare(vocab.source , current_vocab.source);
	}
	
	public String swap()
	{
		learn_tgt = !learn_tgt;
		return  learn_tgt ? current_vocab.target : current_vocab.source;
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

	public boolean isLearnTarget() {
		return learn_tgt;
	}

	public void setLearnTarget(boolean learn_tgt) {
		this.learn_tgt = learn_tgt;
	}
	
	public int getHintFormat() {
		return hint_format;
	}

	public void setHintFormat(int hint_format) {
		this.hint_format = hint_format;
	}
	
	public List<Vocab> getVocabList() {
		return vocab_list; 
	}
	
	public void setVocabList(List<Vocab> vocab_list){
		this.vocab_list = vocab_list;
		reset();
	}

	public String getDelimiters() {
		return delimiters;
	}


	public void setDelimiters(String delimiters) {
		if(delimiters.length() == 0) {
			this.delimiters = null;
			this.delimiter_regex = null;
		}else {
			this.delimiters = delimiters;
			this.delimiter_regex = String.format("[%s]", delimiters);
		}
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
	
	private String[] split(String str, String delimiters, boolean include_delimiter) {
		List<String> strings = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < str.length(); ++i) {
			char c = str.charAt(i);
			
			if(delimiters.indexOf(c) == -1) {
				sb.append(c);
			}else {
				if(include_delimiter)
					sb.append(c);
				strings.add(sb.toString());
				sb.setLength(0);
			}
		}
		
		if(sb.length() > 0) {
			strings.add(sb.toString());
		}
		
		
		return strings.toArray(new String[0]);
	}
	
}
