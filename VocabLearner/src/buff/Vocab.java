package buff;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class Vocab implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String source, target;

	public Stats src_to_tgt;
	public Stats tgt_to_src;
	public int session_correct, session_incorrect;
	public LocalDateTime last_practiced;
	
	private static DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;
	
	public Vocab(String[] csv_data)
	{
		source = csv_data[0];
		target = csv_data[1];
		
		clearStats();
		
		try {
			last_practiced = LocalDateTime.parse(csv_data[4], dtf);
		} catch(DateTimeParseException e) {
			last_practiced = null;
		}
	}
	
	public void clearStats()
	{
		src_to_tgt = new Stats();
		tgt_to_src = new Stats();
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

	public Stats getSrc_to_tgt() {
		return src_to_tgt;
	}

	public void setSrc_to_tgt(Stats src_to_tgt) {
		this.src_to_tgt = src_to_tgt;
	}

	public Stats getTgt_to_src() {
		return tgt_to_src;
	}

	public void setTgt_to_src(Stats tgt_to_src) {
		this.tgt_to_src = tgt_to_src;
	}

	public LocalDateTime getLastPracticed() {
		return last_practiced;
	}

	public void setLastPracticed(LocalDateTime last_practiced) {
		this.last_practiced = last_practiced;
	}
}
