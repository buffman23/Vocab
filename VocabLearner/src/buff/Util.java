package buff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class Util {
	private static final String USERS_DIR_NAME = "resources/users";
	public static final int CSV_DELIMITER = ',';
	
	public static Image resize(Image image, int width, int height) {
		  Image scaled = new Image(Display.getDefault(), width, height);
		  GC gc = new GC(scaled);
		  gc.setAntialias(SWT.ON);
		  gc.setInterpolation(SWT.HIGH);
		  gc.drawImage(image, 0, 0,image.getBounds().width, image.getBounds().height, 0, 0, width, height);
		  gc.dispose();
		  image.dispose(); // don't forget about me!
		  return scaled;
	}
	
	public static Image resize(Image image, double scale){
		return resize(image, (int)(image.getBounds().height * scale), (int)(image.getBounds().width * scale));
	}
	
	public static List<Vocab> loadFromCSV(String csv_file_name) throws IOException
	{
		File vocab_file = new File(csv_file_name);
		
		if(!vocab_file.exists()) return null;
		
		BufferedReader br = new BufferedReader(new FileReader(vocab_file));
		
		int lines = 0;
		while(br.readLine() != null) ++lines;
		br.close();
		br = new BufferedReader(new FileReader(vocab_file));
		
		List<Vocab> vocab_list = new ArrayList<Vocab>(lines);
		
		
		String[] header = Util.parseCSV(br.readLine());// skip header
		if(header == null)
			return vocab_list;
		
		if(header.length == 2) {
			String[] line = header;
			header = Util.parseCSV(Vocab.simple_csv_header);
			vocab_list.add(new Vocab(header, line));
		}
		for(String line; (line = br.readLine()) != null;) {
			if(line.length() > 0)
				vocab_list.add(new Vocab(header, Util.parseCSV(line)));
		}
		
		return vocab_list;
	}
	
	public static void saveToCSV(String output_file_name, List<Vocab> vocab_list) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		sb.append(Vocab.csv_header);
		sb.append('\n');
		for(Vocab vocab : vocab_list) {
			vocab.appendToCSV(sb);
			sb.append('\n');
		}
		Files.write(Paths.get(output_file_name), sb.toString().getBytes());
	}
	
	public static User loadUser(String username) throws IOException {
		File vocab_file = new File(String.format("%s/%s/vocab.csv", USERS_DIR_NAME, username));
		List<Vocab> vocab_list = loadFromCSV(vocab_file.getPath());
		
		return new User(username, vocab_list);
	}
	
	public static void saveUser(User user) throws IOException {
		if(user == null || user.getUsername() == null || user.getVocabList() == null) return;
		File vocab_file = new File(String.format("%s/%s/vocab.csv", USERS_DIR_NAME, user.getUsername()));
		saveToCSV(vocab_file.getPath(), user.getVocabList());
	}
	
	public static void replaceVocab(List<Vocab> other_vocab, List<Vocab> user_vocab)
	{
		if(user_vocab == null) 
			return;
		
		int idx;
		for(int i = 0; i < other_vocab.size(); ++i) {
			Vocab v = other_vocab.get(i);

			if((idx = user_vocab.indexOf(v)) != -1) {
				other_vocab.set(i, user_vocab.get(idx));
			}
		}
		
	}
	
	public static String[] parseCSV(String csv_entry) {
		if(csv_entry == null)
			return null;
		
		StringBuilder sb = new StringBuilder();
		List<String> entries = new ArrayList<String>();
		
		boolean in_quote = false;
		for(char c : csv_entry.toCharArray()) {
			if(c == '"') {
				in_quote = !in_quote;
				continue;
			}
			
			if(!in_quote && c == CSV_DELIMITER) {
				entries.add(sb.toString());
				sb.setLength(0);
				continue;
			}
				
			sb.append(c);
		}
		
		if(sb.length() > 0) {
			entries.add(sb.toString());
		}
		
		return entries.toArray(new String[0]);
	}
}
