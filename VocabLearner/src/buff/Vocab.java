package buff;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
	public Date last_practiced;
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public Vocab(String[] csv_data)
	{
		source = csv_data[0];
		target = csv_data[1];
		
		src_to_tgt = new Stats();
		tgt_to_src = new Stats();
		
		try {
			last_practiced = sdf.parse(csv_data[4]);
		} catch (ParseException e) {
			last_practiced = null;
		}
	}
}
