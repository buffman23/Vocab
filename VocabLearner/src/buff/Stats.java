package buff;

import java.io.Serializable;

public class Stats implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final byte MAX_SCORE = 100;
	/*
	 * history: bit field representing correct or incorrect vocab submissions
	 * every 2 bits represents a submission score 0-3.
	 * LSB is most recent submission
	 * 
	 */
	private byte score;
	
	private int submissions;
	
	public Stats() {
		this((byte)0, 0);
	}
	
	public Stats(byte score, int submissions)
	{
		this.score = score;
		this.submissions = submissions;
	}

	public void addScore(int i) {
		addScore((byte)i);
	}
	
	public void addScore(byte b)
	{
		if(b >= 0) {
			if(b + score < MAX_SCORE)
				score += b;
			else
				score = MAX_SCORE;
		}else {
			if(b + score > 0)
				score += b;
			else
				score = 0;
		}
		
	}
	
	public void setScore(byte b) {
		this.score = b;
	}
	
	public void clear()
	{
		score = 0;
		submissions = 0;
	}
	
	public byte getScore()
	{
	    return score;
	}
	
	public int getSubmissions() {
	    return submissions;
	}
	
	public void incrementSubmissions()
	{
		++submissions;
	}
}
