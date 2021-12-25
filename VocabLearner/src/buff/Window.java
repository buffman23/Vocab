package buff;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class Window {

	protected Shell shell;
	private TabFolder tabFolder;
	private TabItem tbtmLearn;
	private Composite LearnLayer;
	private Composite InputLayer;
	private TabItem tbtmLoad;
	private Composite TextInLayer;
	private Composite InkInLayer;
	private Text TextField;
	private Canvas canvas;
	private TabItem tbtmSettings;
	private Composite SettingsLayer;
	private Button EraserButton;
	private CCombo PenFontCombo;
	private Label Settings_InkCanvas_Label;
	private Label Settings_PenSize_Label;
	private Label CorrectLabel;
	private Composite LoadLayer;
	private Button SelectFileButton;
	private Label SelectedFileLabel;
	private Label SelectedFileLabel2;
	
	private Learner learner = new Learner();
	private String current_word;
	
	private static Image correct_img = Util.resize(SWTResourceManager.getImage(Window.class, "/images/correct.png"), .25);
	private static Image incorrect_img = Util.resize(SWTResourceManager.getImage(Window.class, "/images/incorrect.png"), .25);
	private static Image swap_img = Util.resize(SWTResourceManager.getImage(Window.class, "/images/swap.png"), 25, 25);
	private GridData gd_CorrectLabel;
	private Button ToggleInkCanvas;
	private Button ToggleCase;
	private Label Settings_Ignore_Case_Label;
	private Label Settings_Ignore_Accents_Label;
	private Button ToggleAccents;
	private Button ToggleSpecial;
	private Label Settings_Ignore_Special_Label;
	private Label Settings_Hints_Label;
	private CCombo ToggleHints;
	private Composite DisplayLayer;
	private Label VocabLabel;
	private Label SwapVocabButton;
	private Composite TextBorderColor;
	private Label TopLayerPad;
	private Label TextInPad;
	
	private static volatile int icon_clear_count = 0;
	private Label HintLabel;
	private TabItem tbtmStats;
	private ScrolledComposite TableScrollLayer;
	private Composite TableLayer;
	private Table StatsTable;
	private TableColumn tblclmnSource;
	private TableColumn tblclmnTarget;
	private TableColumn tblclmnAttempts;
	private TableColumn tblclmnScores;
	private TableColumn tblclmnLastPracticed;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Window window = new Window();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				try {
					
					String file_name = learner.getVocabFileName();
					if(file_name != null) {
						if(!file_name.endsWith(".vocab")) {
							file_name = file_name.substring(0, file_name.lastIndexOf('.'));
							file_name += ".vocab";
						}
						learner.save(file_name);
					}
					
					saveSession("session");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		shell.setImage(SWTResourceManager.getImage(Window.class, "/images/choppa.png"));
		shell.setVisible(true);
		shell.setSize(450, 300);
		shell.setText("Vocab Learner");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		tabFolder = new TabFolder(shell, SWT.NONE);
		
		tbtmLearn = new TabItem(tabFolder, SWT.NONE);
		tbtmLearn.setText("Learn");
		
		LearnLayer = new Composite(tabFolder, SWT.NONE);
		tbtmLearn.setControl(LearnLayer);
		LearnLayer.setLayout(new GridLayout(3, false));
		new Label(LearnLayer, SWT.NONE);
		new Label(LearnLayer, SWT.NONE);
		
		SwapVocabButton = new Label(LearnLayer, SWT.NONE);
		GridData gd_SwapVocabButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_SwapVocabButton.heightHint = 25;
		gd_SwapVocabButton.widthHint = 25;
		SwapVocabButton.setLayoutData(gd_SwapVocabButton);
		SwapVocabButton.setImage(swap_img);
		SwapVocabButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if(learner.isLoaded()) {
					VocabLabel.setText(learner.swap());
					HintLabel.setText("");
					LearnLayer.layout(true);
				}
			}
		});
		new Label(LearnLayer, SWT.NONE);
		
		DisplayLayer = new Composite(LearnLayer, SWT.NONE);
		DisplayLayer.setLayout(new GridLayout(1, false));
		DisplayLayer.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 1, 1));
		
		VocabLabel = new Label(DisplayLayer, SWT.SHADOW_NONE | SWT.CENTER);
		VocabLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		VocabLabel.setText("[Vocab Here]");
		VocabLabel.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.NORMAL));
		
		HintLabel = new Label(DisplayLayer, SWT.NONE);
		HintLabel.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		HintLabel.setAlignment(SWT.CENTER);
		HintLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		HintLabel.setText("[Hint]");
		new Label(LearnLayer, SWT.NONE);
		new Label(LearnLayer, SWT.NONE);
		
		InputLayer = new Composite(LearnLayer, SWT.NO_MERGE_PAINTS | SWT.NO_REDRAW_RESIZE);
		GridData gd_InputLayer = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
		gd_InputLayer.heightHint = 160;
		InputLayer.setLayoutData(gd_InputLayer);
		StackLayout sl_InputLayer = new StackLayout();
		sl_InputLayer.marginWidth = 20;
		InputLayer.setLayout(sl_InputLayer);
		
		TextInLayer = new Composite(InputLayer, SWT.NONE);
		TextInLayer.setTouchEnabled(true);
		TextInLayer.setLayout(new GridLayout(3, false));
		
		((StackLayout)InputLayer.getLayout()).topControl = TextInLayer;
		
		TextInPad = new Label(TextInLayer, SWT.NONE);
		GridData gd_TextInPad = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_TextInPad.widthHint = 32;
		TextInPad.setLayoutData(gd_TextInPad);
		
		TextBorderColor = new Composite(TextInLayer, SWT.NONE);
		TextBorderColor.setBackground(new Color(0,0,0));
		GridLayout gl_TextBorderColor = new GridLayout(1, false);
		gl_TextBorderColor.marginHeight = 1;
		gl_TextBorderColor.marginWidth = 1;
		TextBorderColor.setLayout(gl_TextBorderColor);
		TextBorderColor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
				TextField = new Text(TextBorderColor, SWT.CENTER);
				GridData gd_TextField = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
				gd_TextField.heightHint = 17;
				TextField.setLayoutData(gd_TextField);
				TextField.addKeyListener(new KeyAdapter() {
					@Override
					public void keyReleased(KeyEvent e) {
						switch(e.keyCode)
						{
							case SWT.Selection:
								if(current_word == null)
									return;
								
								String user_guess = TextField.getText();
								if(learner.compare(user_guess) == 0){
									CorrectLabel.setImage(correct_img);
									
									if((current_word = learner.next()) == null) {
										System.out.println("Finished set. restarting...");
										learner.reset();
										current_word = learner.next();
									}
									
									VocabLabel.setText(current_word);
									HintLabel.setText("");
								}else {
									CorrectLabel.setImage(incorrect_img);
									
									String hint = learner.nextHint();
									if(hint != null)
										HintLabel.setText(hint);
								}
								

								++icon_clear_count;
								shell.getDisplay().timerExec(1000, () -> {
									if(icon_clear_count == 1)
										CorrectLabel.setImage(null);
									--icon_clear_count;
								});
								
								TextField.setText("");
								
								LearnLayer.layout(true);
								TextInLayer.layout(true);
								break;
						}
					}
				});
		
		CorrectLabel = new Label(TextInLayer, SWT.NONE);
		gd_CorrectLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		setGd_CorrectLabelWidthHint(correct_img.getBounds().width);
		CorrectLabel.setLayoutData(gd_CorrectLabel);
	
		InkInLayer = new Composite(InputLayer, SWT.NONE);
		InkInLayer.setLayout(new GridLayout(2, false));
		
		//((StackLayout)InputLayer.getLayout()).topControl = InkInLayer;
		new Label(InkInLayer, SWT.NONE);
		
		EraserButton = new Button(InkInLayer, SWT.NONE);
		EraserButton.setImage(SWTResourceManager.getImage(Window.class, "/images/eraser.png"));
		EraserButton.setToolTipText("Clear Canvas");
		EraserButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		canvas = new Canvas(InkInLayer, SWT.BORDER);
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		new Label(LearnLayer, SWT.NONE);
		
		TopLayerPad = new Label(LearnLayer, SWT.NONE);
		GridData gd_TopLayerPad = new GridData(SWT.LEFT, SWT.CENTER, false, false, 0, 1);
		gd_TopLayerPad.heightHint = 25;
		gd_TopLayerPad.widthHint = 25;
		TopLayerPad.setLayoutData(gd_TopLayerPad);
		new Label(LearnLayer, SWT.NONE);
		new Label(LearnLayer, SWT.NONE);
		canvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
			}
			@Override
			public void mouseUp(MouseEvent e) {
			}
		});
		
		tbtmLoad = new TabItem(tabFolder, SWT.NONE);
		tbtmLoad.setText("Load");
		
		LoadLayer = new Composite(tabFolder, SWT.NONE);
		tbtmLoad.setControl(LoadLayer);
		LoadLayer.setLayout(new GridLayout(3, false));
		
		SelectFileButton = new Button(LoadLayer, SWT.NONE);
		SelectFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(shell);
				String full_path = fd.open();
				if(full_path == null) return;
				String working_dir = System.getProperty("user.dir");
				int idx = full_path.indexOf(working_dir);
				if(idx != -1) {
					Path p1 = Paths.get(full_path);
					Path p2 = Paths.get(working_dir);
					loadVocab(p2.relativize(p1).toString());
				}else {
					loadVocab(full_path);
				}
			}
		});
		SelectFileButton.setText("Select File");
		
		SelectedFileLabel = new Label(LoadLayer, SWT.NONE);
		SelectedFileLabel.setText("Selected file:");
		
		SelectedFileLabel2 = new Label(LoadLayer, SWT.NONE);
		
		tbtmStats = new TabItem(tabFolder, SWT.NONE);
		tbtmStats.setText("Stats");
		
		TableScrollLayer = new ScrolledComposite(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmStats.setControl(TableScrollLayer);
		TableScrollLayer.setExpandHorizontal(true);
		TableScrollLayer.setExpandVertical(true);
		
		TableLayer = new Composite(TableScrollLayer, SWT.NONE);
		TableLayer.setLayout(new FillLayout(SWT.VERTICAL));
		
		StatsTable = new Table(TableLayer, SWT.BORDER | SWT.FULL_SELECTION);
		StatsTable.setHeaderVisible(true);
		StatsTable.setLinesVisible(true);
		
		tblclmnSource = new TableColumn(StatsTable, SWT.NONE);
		tblclmnSource.setWidth(103);
		tblclmnSource.setText("Source");
		
		tblclmnTarget = new TableColumn(StatsTable, SWT.NONE);
		tblclmnTarget.setWidth(84);
		tblclmnTarget.setText("Target");
		
		tblclmnLastPracticed = new TableColumn(StatsTable, SWT.NONE);
		tblclmnLastPracticed.setWidth(63);
		tblclmnLastPracticed.setText("Practiced");
		
		tblclmnAttempts = new TableColumn(StatsTable, SWT.NONE);
		tblclmnAttempts.setWidth(63);
		tblclmnAttempts.setText("Attempts");
		
		tblclmnScores = new TableColumn(StatsTable, SWT.NONE);
		tblclmnScores.setWidth(100);
		tblclmnScores.setText("Scores");
		TableScrollLayer.setContent(TableLayer);
		TableScrollLayer.setMinSize(TableLayer.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		tbtmSettings = new TabItem(tabFolder, SWT.NONE);
		tbtmSettings.setText("Settings");
		
		SettingsLayer = new Composite(tabFolder, SWT.NONE);
		
		tbtmSettings.setControl(SettingsLayer);
		SettingsLayer.setLayout(new GridLayout(2, false));
		
		Settings_InkCanvas_Label = new Label(SettingsLayer, SWT.NONE);
		Settings_InkCanvas_Label.setToolTipText("Use canvas to draw/write input instead of keyboard input");
		Settings_InkCanvas_Label.setText("Ink Canvas");
		
		ToggleInkCanvas = new Button(SettingsLayer, SWT.CHECK);
		ToggleInkCanvas.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(ToggleInkCanvas.getSelection()) {
					((StackLayout)InputLayer.getLayout()).topControl = InkInLayer;
					PenFontCombo.setEnabled(true);
				}else {
					((StackLayout)InputLayer.getLayout()).topControl = TextInLayer;
					PenFontCombo.setEnabled(false);
				}
				InputLayer.layout(true);
			}
		});
		
		Settings_PenSize_Label = new Label(SettingsLayer, SWT.NONE);
		Settings_PenSize_Label.setToolTipText("Change thickness of pen stroke");
		Settings_PenSize_Label.setText("Pen Size");
		
		PenFontCombo = new CCombo(SettingsLayer, SWT.BORDER);
		PenFontCombo.setEditable(false);
		PenFontCombo.setEnabled(false);
		PenFontCombo.setToolTipText("Select pencil thickness");
		PenFontCombo.setLayoutDeferred(true);
		PenFontCombo.setItems(new String[] {"4", "8", "10", "12", "14", "16"});
		PenFontCombo.select(0);
		
		Settings_Ignore_Case_Label = new Label(SettingsLayer, SWT.NONE);
		Settings_Ignore_Case_Label.setToolTipText("Ignore Case, like 'a' and 'A'  are treated the same");
		Settings_Ignore_Case_Label.setText("Ignore Case");
		
		ToggleCase = new Button(SettingsLayer, SWT.CHECK);
		ToggleCase.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				learner.setIgnoreCase(ToggleCase.getSelection());
			}
		});
		
		Settings_Ignore_Accents_Label = new Label(SettingsLayer, SWT.NONE);
		Settings_Ignore_Accents_Label.setToolTipText("Ignore Accents, like 'o' and '\u00F6' are treated the same");
		Settings_Ignore_Accents_Label.setText("Ignore Accents");
		
		ToggleAccents = new Button(SettingsLayer, SWT.CHECK);
		ToggleAccents.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				learner.setIgnoreAccents(ToggleAccents.getSelection());
			}
		});
		
		Settings_Ignore_Special_Label = new Label(SettingsLayer, SWT.NONE);
		Settings_Ignore_Special_Label.setToolTipText("Ignore non-aplha chars, like '!@#$%^&*()-_~'");
		Settings_Ignore_Special_Label.setText("Ignore Special");
		
		ToggleSpecial = new Button(SettingsLayer, SWT.CHECK);
		ToggleSpecial.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				learner.setIgnoreSpecial(ToggleSpecial.getSelection());
			}
		});
		
		Settings_Hints_Label = new Label(SettingsLayer, SWT.NONE);
		Settings_Hints_Label.setToolTipText("Give progreesively more hints with every failed attempt");
		Settings_Hints_Label.setText("Hints");
		
		ToggleHints = new CCombo(SettingsLayer, SWT.BORDER);
		ToggleHints.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				learner.setHintFormat(ToggleHints.getSelectionIndex());
				
				if(!learner.isLoaded()) return;
				
				String current_hint = HintLabel.getText();
				int hints_given = 0;
				
				if(current_hint != null)
					hints_given = current_hint.length() - StringUtils.countMatches(current_hint, "_");
				
				String hint = null;
				for(int i = 0; i < hints_given; ++i) {
					hint = learner.nextHint();
				}

				if(hint == null)
					hint = "";
				HintLabel.setText(hint);
				
				LearnLayer.layout(true);
			}
		});
		ToggleHints.setEditable(false);
		ToggleHints.setItems(new String[] {"None", "LeftToRight", "RightToLeft", "InToOut", "OutToIn", "Random"});
		ToggleHints.select(0);
		
		try {
			loadSession("session");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	public int getGd_CorrectLabelWidthHint() {
		return gd_CorrectLabel.widthHint;
	}
	public void setGd_CorrectLabelWidthHint(int widthHint) {
		gd_CorrectLabel.widthHint = 32;
	}
	
	public void loadSession(String session_file_name) throws IOException
	{
		File session_file = new File(session_file_name);
		BufferedReader br = new BufferedReader(new FileReader(session_file));
		
		
		for(String line = null; (line = br.readLine()) != null;) {
			String[] split = line.split(",");
			switch(split[0])
			{
				case "ink_canvas":
					boolean b = Boolean.parseBoolean(split[1]);
					ToggleInkCanvas.setSelection(b);
					ToggleInkCanvas.notifyListeners(SWT.Selection, new Event());
					break;
				case "pen_size":
					int i = Integer.parseInt(split[1]);
					PenFontCombo.select(i);
					PenFontCombo.notifyListeners(SWT.Selection, new Event());
					break;
				case "ignore_case":
					b = Boolean.parseBoolean(split[1]);
					ToggleCase.setSelection(b);
					ToggleCase.notifyListeners(SWT.Selection, new Event());
					break;
				case "ignore_accents":
					b = Boolean.parseBoolean(split[1]);
					ToggleAccents.setSelection(b);
					ToggleAccents.notifyListeners(SWT.Selection, new Event());
					break;
				case "ignore_special":
					b = Boolean.parseBoolean(split[1]);
					ToggleSpecial.setSelection(b);
					ToggleSpecial.notifyListeners(SWT.Selection, new Event());
					break;
				case "hints":
					b = Boolean.parseBoolean(split[1]);
					i = Integer.parseInt(split[1]);
					ToggleHints.select(i);
					ToggleHints.notifyListeners(SWT.Selection, new Event());
					break;
				case "vocab":
					loadVocab(split[1]);
					break;
					
				default:
					System.err.println("Unrecognized config in session file.");
					break;
			}
		}
		
		br.close();
	}
	
	public void saveSession(String session_file_name) throws IOException
	{
		File session_file = new File(session_file_name);
		PrintWriter pw = new PrintWriter(session_file);
		
		
		pw.println("ink_canvas,"+ToggleInkCanvas.getSelection());
		pw.println("pen_size,"+PenFontCombo.getSelectionIndex());
		pw.println("ignore_case,"+ToggleCase.getSelection());
		pw.println("ignore_accents,"+ToggleAccents.getSelection());
		pw.println("ignore_special,"+ToggleSpecial.getSelection());
		pw.println("hints,"+ToggleHints.getSelectionIndex());
		String str = learner.getVocabFileName();
		
		if(str != null) {
			if(!str.endsWith(".vocab")) {
				str = str.substring(0, str.lastIndexOf('.'));
				str += ".vocab";
			}
			pw.println("vocab,"+str);
		}
		
		pw.close();
	}
	
	private void loadVocab(String file_path)
	{
		if(file_path == null) return;
		
		Path path = Paths.get(file_path);

		SelectedFileLabel2.setText(path.getFileName().toString());
		LoadLayer.layout(true);
		
		try {
			if(file_path.endsWith(".csv")) {
				learner.loadCSV(file_path);
			}else {
				try {
					learner.load(file_path);
					
				} catch (ClassNotFoundException e) {
					System.err.println("Couldn't load vocab file. Invalid version/format.");
				}
			}
			refreshStats();
			current_word = learner.next();
			VocabLabel.setText(current_word);
			HintLabel.setText("");
			LearnLayer.layout(true);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private void refreshStats()
	{
		for(Vocab vocab : learner.getVocabList()) {
			Label l = new Label(TableLayer, SWT.NONE);
			TableItem item = new TableItem(StatsTable, SWT.NONE);
			boolean isLearnSource = learner.isLearnSource();
			if(isLearnSource)
			{
				item.setText(0, vocab.source);
				item.setText(1, vocab.target);
				item.setText(3, vocab.src_to_tgt.toString());
			}else {
				item.setText(0, vocab.target);
				item.setText(1, vocab.source);
				item.setText(3, vocab.tgt_to_src.toString());
			}
			item.setText(2, vocab.last_practiced == null ? "" : vocab.last_practiced.toString());
		}
	}
}