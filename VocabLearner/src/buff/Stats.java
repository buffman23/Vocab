package buff;

import java.io.Serializable;

public class Stats implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final double MAX_SCORE = 100;
	/*
	 * history: bit field representing correct or incorrect vocab attempts
	 * every 2 bits represents a submission score 0-3.
	 * LSB is most recent submission
	 * 
	 */
	private double score;
	
	private int attempts;
	
	public Stats() {
		this((double)0, 0);
	}
	
	public Stats(double score, int attempts)
	{
		this.score = score;
		this.attempts = attempts;
	}

	public void addScore(int i) {
		addScore((double)i);
	}
	
	public void addScore(double b)
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
	
	public void setScore(double b) {
		this.score = b;
	}
	
	public void clear()
	{
		score = 0;
		attempts = 0;
	}
	
	public double getScore()
	{
	    return score;
	}
	
	public int getAttempts() {
	    return attempts;
	}
	
	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}
	
	public void incrementAttempts()
	{
		++attempts;
	}
}
