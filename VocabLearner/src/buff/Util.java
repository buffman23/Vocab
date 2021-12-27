package buff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class Util {
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
	
	public static List<Vocab> load(String input_file_name) throws IOException, ClassNotFoundException {
		File file = new File(input_file_name);
		if(! file.exists()) return null;
		
		if(input_file_name.endsWith(".csv"))
			return loadCSV(input_file_name);
		
		return loadVocab(input_file_name);
	}
	
	@SuppressWarnings("unchecked")
	private static List<Vocab> loadVocab(String vocab_file_name) throws ClassNotFoundException, IOException
	{
		File vocab_file = new File(vocab_file_name);
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(vocab_file));
		
		List<Vocab> vocab_list = (ArrayList<Vocab>) ois.readObject();
		ois.close();
		
		return vocab_list;
	}
	
	private static List<Vocab> loadCSV(String csv_file_name) throws IOException
	{
		File vocab_file = new File(csv_file_name);
		BufferedReader br = new BufferedReader(new FileReader(vocab_file));
		
		int lines = 0;
		while(br.readLine() != null) ++lines;
		
		List<Vocab> vocab_list = new ArrayList<Vocab>(lines);
		
		br.close();
		br = new BufferedReader(new FileReader(vocab_file));
		
		br.readLine(); // skip header
		for(String line; (line = br.readLine()) != null;) {
			if(line.length() > 0)
				vocab_list.add(new Vocab(line.split(",")));
		}
		
		return vocab_list;
	}
	
	public static void save(String output_file_name, List<Vocab> vocab_list) throws IOException
	{
		File vocab_file = new File(output_file_name);
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(vocab_file));
		
		oos.writeObject(vocab_list);
		oos.close();
	}
}
