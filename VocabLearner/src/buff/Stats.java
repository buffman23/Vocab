package buff;

import java.io.Serializable;

public class Stats implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * history: bit field representing correct or incorrect vocab submissions
	 * every 2 bits represents a submission score 0-3.
	 * LSB is most recent submission
	 * 
	 */
	private long history;
	
	private int total_attempts;
	
	public Stats() {
		this(0, 0);
	}
	
	public Stats(int history, int total_attempts)
	{
		this.history = history;
		this.total_attempts = total_attempts;
	}

	



	public void append(byte b)
	{
		// b can't be greater than 2 bits
		if(b > 0b11)
			b = 0b11;
		history <<= 2;
		history |= b;
		
		++total_attempts;
	}
	
	public void clear()
	{
		history = 0;
		total_attempts = 0;
	}
	
	public byte[] getScores(int most_recent)
	{
		long number = history;
		byte[] scores = new byte[most_recent];
		
	    for (int i = 0; i < most_recent && number != 0; ++i) {
	        
	        byte score = (byte)(number & 0b11);
	        scores[i] += score;
	        
	        number >>= 2L;
	    }
	    return scores;
	}
	
	public byte[] getScores() {
		return getScores(Long.SIZE/2);
	}
	
	public int getTotalAttempts() {
	    return total_attempts;
	}
	
	public String toString()
	{
		byte[] scores = getScores(total_attempts < Long.SIZE ? total_attempts : Long.SIZE);
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for(int i = 0; i < scores.length - 1; ++i) {
			sb.append(scores[i]);
			sb.append(", ");
		}
		if(scores.length > 0)
			sb.append(scores[scores.length -1 ]);
		sb.append(']');
		
		return sb.toString();
	}
}
