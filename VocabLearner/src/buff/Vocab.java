package buff;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Vocab implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String source, target;

	public Stats stats_src;
	public Stats stats_tgt;
	public int session_correct, session_incorrect;
	public LocalDateTime last_practiced;
	
	public static String csv_header = "source,target,last_practiced,attempts_src,score_src,attempts_tgt,score_tgt";
	public static String simple_csv_header = "source,target";
	
	private static DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;
	
	public Vocab(String[] header, String[] csv_data)
	{
		clearStats();
		for(int i = 0; i < header.length; ++i) {
			switch(header[i]) {
				case "source":
					this.source = csv_data[i];
					break;
				case "target":
					this.target = csv_data[i];
					break;
				case "last_practiced":
					if(!csv_data[i].equals("null"))
						this.last_practiced = LocalDateTime.parse(csv_data[i], dtf);
					break;
				case "attempts_src":
					int attempts = Integer.parseInt(csv_data[i]);
					this.stats_src.setAttempts(attempts);
					break;
				case "score_src":
					double score = Double.parseDouble(csv_data[i]);
					this.stats_src.setScore(score);
					break;
				case "attempts_tgt":
					attempts = Integer.parseInt(csv_data[i]);
					this.stats_tgt.setAttempts(attempts);
					break;
				case "score_tgt":
					score = Double.parseDouble(csv_data[i]);
					this.stats_tgt.setScore(score);
					break;
			}
		}
	}
	
	public void clearStats()
	{
		stats_src = new Stats();
		stats_tgt = new Stats();
	}
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public Stats getStatsSrc() {
		return stats_src;
	}

	public void setStatsSrc(Stats stats_src) {
		this.stats_src = stats_src;
	}

	public Stats getStatsTgt() {
		return stats_tgt;
	}

	public void setStatsTgt(Stats stats_tgt) {
		this.stats_tgt = stats_tgt;
	}

	public LocalDateTime getLastPracticed() {
		return last_practiced;
	}

	public void setLastPracticed(LocalDateTime last_practiced) {
		this.last_practiced = last_practiced;
	}
	
	public void swapSrcTgt()
	{
		String tmp = this.source;
		this.source = this.target;
		this.target = tmp;
		
		Stats tmp_stat = this.stats_src;
		this.stats_src = this.stats_tgt;
		this.stats_tgt = tmp_stat;
	}
	
	public void appendToCSV(StringBuilder sb) {
		if(this.source.contains(",")) {
			sb.append('"');
			sb.append(this.source);
			sb.append('"');
		}else {
			sb.append(this.source);
		}
		
		sb.append(',');
		if(this.target.contains(",")) {
			sb.append('"');
			sb.append(this.target);
			sb.append('"');
		}else {
			sb.append(this.target);
		}
		sb.append(',');
		sb.append(this.last_practiced);
		sb.append(',');
		sb.append(this.stats_src.getAttempts());
		sb.append(',');
		sb.append(this.stats_src.getScore());
		sb.append(',');
		sb.append(this.stats_tgt.getAttempts());
		sb.append(',');
		sb.append(this.stats_tgt.getScore());
	}
	
	public boolean equals(Object other)
	{
		if(!(other instanceof Vocab))
			return super.equals(other);
		
		Vocab v = (Vocab)other;
		
		String ts = this.source.toLowerCase();
		String tt = this.target.toLowerCase();
		String os = v.source.toLowerCase();
		String ot = v.target.toLowerCase();
		
		if(ts.equals(os) && tt.equals(ot)) {
			return true;
		}
		
		if(ts.equals(ot) && tt.equals(os)) {
			return true;
		}
		
		return false;
	}
}
