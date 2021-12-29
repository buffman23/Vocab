package buff;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class PracticeSet {
	public static final int NONE = 0, LEFT_TO_RIGHT = 1, RIGHT_TO_LEFT = 2, IN_TO_OUT = 3, OUT_TO_IN = 4, RANDOM = 5;
	
	List<Vocab> vocab_list;
	List<Vocab> working_vocab_list;
	List<Integer> streak_list;
	int current_vocab_idx = -1;
	int hint_format = NONE;
	int current_hint_count;
	Vocab current_vocab;
	
	public static int CORRECT_SCORE = 2, INCORRECT_SCORE = -2, STREAK_MAX = 3;
	
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
		if(hint_format == NONE || current_vocab == null) return "";
		return getHint(++current_hint_count);
	}
	
	private String getHint(int hint_count)
	{
		Vocab vocab = current_vocab;
		String word_str = learn_tgt ? vocab.target : vocab.source;
		
		Stats stats = learn_tgt ? current_vocab.getStatsTgt() : current_vocab.getStatsSrc();
		hint_count += (int)(word_str.length() * Math.max(1 - 2*stats.getScore()/100.0, 0) + 0.5);
		
		if(hint_count == 0) return "";
		
		String current_hint = StringUtils.repeat("_", word_str.length());
		StringBuilder sb = new StringBuilder(current_hint);
		
		
		
		for(int k = 0; k < hint_count && k < current_hint.length(); ++k) {
			int idx = 0;
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
			
			sb.setCharAt(idx, word_str.charAt(idx));
			current_hint = sb.toString();
		}

		return current_hint;
	}
	
	public boolean submit(String word)
	{
		boolean result = (compare(word) == 0);
		
		Stats stats = learn_tgt ? current_vocab.getStatsTgt() : current_vocab.getStatsSrc();
		if(result) {
			int current_streak = streak_list.get(current_vocab_idx);
			 ++current_vocab.session_correct;
			 current_vocab.last_practiced = LocalDateTime.now();
			 double score = CORRECT_SCORE * (1 + Math.log(working_vocab_list.size())/Math.log(8));
			 if(hint_format == NONE) score *= 2;
			 
			 System.out.printf("Score: %f\n", score);
			 stats.addScore(score);
			 stats.incrementAttempts();
			
			 current_streak += 1;
			 if(current_streak == STREAK_MAX) {
				 working_vocab_list.remove(current_vocab_idx);
				 streak_list.remove(current_vocab_idx);
			 }else {
				 streak_list.set(current_vocab_idx, current_streak);
			 }
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
	
	public int compare(String word)
	{
		String current = learn_tgt ? current_vocab.target : current_vocab.source;
		
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
		return compare(learn_tgt ? vocab.target : vocab.source);
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
