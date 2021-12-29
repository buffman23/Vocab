package buff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class Util {
	private static String users_dir_name = "resources/users";
	
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
		
		
		String[] header = br.readLine().split(","); // skip header
		if(header.length == 2) {
			String[] line = header;
			header = Vocab.simple_csv_header.split(",");
			vocab_list.add(new Vocab(header, line));
		}
		for(String line; (line = br.readLine()) != null;) {
			if(line.length() > 0)
				vocab_list.add(new Vocab(header, line.split(",")));
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
		File vocab_file = new File(String.format("%s/%s/vocab.csv", users_dir_name, username));
		List<Vocab> vocab_list = loadFromCSV(vocab_file.getPath());
		
		return new User(username, vocab_list);
	}
	
	public static void saveUser(User user) throws IOException {
		if(user == null || user.getUsername() == null || user.getVocabList() == null) return;
		File vocab_file = new File(String.format("%s/%s/vocab.csv", users_dir_name, user.getUsername()));
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
}
