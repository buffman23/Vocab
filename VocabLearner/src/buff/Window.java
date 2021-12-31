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
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseTrackAdapter;
import java.util.HashSet;

public class Window {
	private DataBindingContext m_bindingContext;

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
	private Composite SettingsLeftLayer;
	private Composite SettingsTopLayer;
	private Button EraserButton;
	private CCombo PenFontCombo;
	private Label Settings_InkCanvas_Label;
	private Label Settings_PenSize_Label;
	private Label CorrectLabel;
	private Composite LoadLayer;
	
	private PracticeSet practice_set = new PracticeSet();

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
	
	private static long auto_save_interval = 5 * 60 * 1000; // milliseconds
	private static volatile int icon_clear_count = 0;
	private static volatile int applied_count = 0;
	private Label HintLabel;
	private TabItem tbtmStats;
	private Composite StatsLayer;
	private TableColumn load_sorted_column = null;
	private TableColumn stats_sorted_column = null;
	private List<Vocab> loaded_vocab_list;
	private HashMap<TableItem, Vocab> loaded_vocab_map;
	private HashMap<TableItem, Vocab> stats_vocab_map;
	private String loaded_file_name = "";
	private boolean stats_swap_src_tgt;
	private boolean load_swap_src_tgt;
	private List<Vocab> deleted_vocab;
	private List<Runnable> undo_list = new LinkedList<Runnable>();
	private boolean shuffle_vocab = false;
	private List<TextPair> macro_widgets = new ArrayList<TextPair>();
	private HashMap<String, String> macros = new HashMap<String, String>();
	
	private static Color RED = new Color(249, 0, 0);
	private static Color DARK_RED = new Color(215, 0, 0);
	
	private static Color GREEN = new Color(0, 255, 128);
	private static Color DARK_GREEN = new Color(0, 176, 88);
	
	private static Color YELLOW = new Color(255, 255, 0);
	private static Color DARK_YELLOW = new Color(217, 217, 0);
	
	private static Color ORANGE = new Color(255, 128, 0);
	private static Color DARK_ORANGE = new Color(221, 111, 0);

	private TabItem previous_tab;
	private List<Vocab> vocab_list;
	private String vocab_file_name;
	private Composite TableLayer;
	private Table StatsTable;
	private TableColumn tblclmnSource;
	private TableColumn tblclmnTarget;
	private TableColumn tblclmnLastPracticed;
	private TableColumn tblclmnAttempts;
	private TableColumn tblclmnScores;
	private Composite LoginLayer;
	private Label lblVocabPractice;
	private Label LoginFill1;
	private Label LoginFill2;
	private Composite LoginForumStack;
	private Composite LoginFieldsLayer;
	private Composite UsernameLayer;
	private Label LUsernameLabel;
	private Text LUsernameField;
	private Composite PasswordLayer;
	private Label LPasswordLabel;
	private Text LPasswordField;
	private Button btnLogin;
	private Composite SignupLinkLayer;
	private Link CALink;
	private ScrolledComposite MacroScrollLayer;
	private Composite CreateAccountFieldsLayer;
	private Composite UsernameLayer_1;
	private Label CAUsernameField_1;
	private Text CAUsernameField;
	private Composite PasswordLayer_1;
	private Label CAPasswordLabel;
	private Text CAPasswordField;
	private Button btnCreateAccount;
	private Composite SignupLinkLayer_1;
	private Composite ConfPasswordLayer;
	private Label CAConfPasswordLabel;
	private Text CAConfPasswordField;
	private Link LoginLink;
	private Label LErrorLabel;
	private Button btnLogout;
	private Display display;
	private User user;
	private Group SettingsUserGroup;
	private Table LoadTable;
	private TableColumn tblclmnSource_1;
	private TableColumn tblclmnTarget_1;
	private TableColumn tblclmnLastPracticed_1;
	private TableColumn tblclmnAttempts_1;
	private TableColumn tblclmnScores_1;
	private Composite LoadTopWidgetsLayer;
	private Button SelectFileButton;
	private Label LoadSelectedFileLabel;
	private Composite LoadOptionsLayer;
	private Label lblLoadFrom;
	private CCombo LoadOptionsCombo;
	private Composite LoadSelectFileLayer;
	private Composite LoadSelectVocabLayer;
	private Label lblSelect;
	private CCombo LoadSelectVocabCombo;
	private Composite LoadSelectVocabStack;
	private Composite LoadCountLayer;
	private Composite LoadRangeLayer;
	private Text LoadCountField;
	private Label lblFrom;
	private Text LoadFromField;
	private Label lblTo;
	private Text LoadToField;
	private Button btnGo_1;
	private Button btnApply;
	private Composite LoadBottomLayer;
	private TableColumn tableColumn;
	private Button btnSwapStats;
	private Button btnNewButton;
	private Button btnClearVocab;
	private MenuItem mntmRemove;
	private MenuItem mntmUndo;
	private Label lblShuffleOrder;
	private Button ToggleShuffle;
	private Label SettingsDelimitersLabel;
	private Text SettingsDelimitersField;
	private Button btnMacros;
	private Composite SettingsRightLayer;
	private Composite MacroButtonsLayer;
	private Button btnAdd;
	private Composite MacroListLayer;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			public void run() {
				System.setProperty("file.encoding", "UTF-8");
				try {
					Window window = new Window();
					window.open();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.setSize(750, 500);
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
		display = Display.getCurrent();
		
		shell = new Shell();
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				try {
					PrintWriter pw;
					pw = new PrintWriter("resources/users/session");
					if(user == null) {
						pw.write("");
						pw.close();
						return;
					}

					pw.write(user.getUsername());
					pw.close();
					
					saveSession(String.format("resources/users/%s/session", user.getUsername()));
					
					if(user.getVocabList().size() != 0) {
						MessageBox dialog = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES| SWT.NO);
						dialog.setText("Save Progress");
						dialog.setMessage("Would you like to save your session?");

						if(dialog.open() == SWT.YES) 
							Util.saveUser(user);
					}
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		shell.setImage(SWTResourceManager.getImage(Window.class, "/images/choppa.png"));
		shell.setVisible(true);
		shell.setSize(450, 300);
		shell.setText("Vocab Practice");
		shell.setLayout(new StackLayout());
		
		
		tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(tabFolder.getSelection()[0] == tbtmStats) {
					if(user.getVocabList() != null)
						refreshStats(user.getVocabList());
				}else if(tabFolder.getSelection()[0] == tbtmLoad){
					LoadOptionsCombo.notifyListeners(SWT.Selection, new Event());
				}else if(tabFolder.getSelection()[0] == tbtmLearn) {
					refreshLearnTab();
				}
				
				if(previous_tab == tbtmSettings) {
					if(SettingsDelimitersField != null)
						practice_set.setDelimiters(SettingsDelimitersField.getText());
					
					macros.clear();
					for(TextPair tp : macro_widgets) {
						if(tp.getCheckBox().getSelection())
							macros.put(tp.getText1().getText(), tp.getText2().getText());
					}
				}
				
				previous_tab = tabFolder.getSelection()[0];
			}
		});
		
		
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
				if(practice_set.isLoaded()) {
					practice_set.swap();
					refreshLearnTab();
				}
			}
		});
		new Label(LearnLayer, SWT.NONE);
		
		DisplayLayer = new Composite(LearnLayer, SWT.NONE);
		DisplayLayer.setLayout(new GridLayout(1, false));
		DisplayLayer.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false, 1, 1));
		
		VocabLabel = new Label(DisplayLayer, SWT.SHADOW_NONE | SWT.CENTER);
		VocabLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		VocabLabel.setText("[Vocab Here]");
		VocabLabel.setFont(SWTResourceManager.getFont("Segoe UI", 24, SWT.NORMAL));
		new Label(LearnLayer, SWT.NONE);
		new Label(LearnLayer, SWT.NONE);
		
		InputLayer = new Composite(LearnLayer, SWT.NO_MERGE_PAINTS | SWT.NO_REDRAW_RESIZE);
		InputLayer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1));
		StackLayout sl_InputLayer = new StackLayout();
		sl_InputLayer.marginWidth = 20;
		InputLayer.setLayout(sl_InputLayer);
		
		TextInLayer = new Composite(InputLayer, SWT.NONE);
		TextInLayer.setTouchEnabled(true);
		GridLayout gl_TextInLayer = new GridLayout(3, false);
		gl_TextInLayer.marginTop = 20;
		TextInLayer.setLayout(gl_TextInLayer);
		
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
				TextField.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.NORMAL));
				GridData gd_TextField = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
				gd_TextField.heightHint = 34;
				TextField.setLayoutData(gd_TextField);
				TextField.addKeyListener(new KeyAdapter() {
					@Override
					public void keyReleased(KeyEvent e) {
						switch(e.keyCode)
						{
							case SWT.Selection:
								Vocab current_vocab = practice_set.getVocab();
								
								if(current_vocab == null)
									return;
								
								String user_guess = TextField.getText();
								if(practice_set.submit(user_guess)){
									CorrectLabel.setImage(correct_img);
									
									if((current_vocab = practice_set.nextVocab()) == null) {
										System.out.println("Finished set. restarting...");
										practice_set.reset();
										current_vocab = practice_set.nextVocab();
										
										VocabLabel.setText("Vocab Set Finished.");
										HintLabel.setText("");
										TextField.setText("");
										LearnLayer.layout(true);
										try {
											Util.saveUser(user);
										} catch (IOException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
										++icon_clear_count;
										shell.getDisplay().timerExec(3000, () -> {
											refreshLearnTab();
										});
										shell.getDisplay().timerExec(1000, () -> {
											if(icon_clear_count == 1)
												CorrectLabel.setImage(null);
											--icon_clear_count;
										});
										return;
									}

									VocabLabel.setText(practice_set.isLearnTarget() ? current_vocab.source : current_vocab.target);
									String hint = practice_set.getHint();
									
									if(hint != null)
										HintLabel.setText(hint);
								}else {
									CorrectLabel.setImage(incorrect_img);
									String hint = practice_set.nextHint();
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
								
								return;
						}
						
						if(e.keyCode != SWT.ARROW_LEFT && e.keyCode != SWT.ARROW_RIGHT && e.keyCode != SWT.ARROW_UP && e.keyCode != SWT.ARROW_DOWN) {
							int idx = TextField.getSelection().x;
							String txt = TextField.getText();
					
							for (Map.Entry<String,String> entry : macros.entrySet()) {
								String key = entry.getKey();
								String value = entry.getValue();
								if(txt.substring(0, idx).contains(key)) {
									String replacement =  macros.get(key);
									TextField.setText(txt.replaceFirst(key, replacement));
								  
									TextField.setSelection(idx + value.length() - key.length());
									break;
								}
							
							}
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
		
				HintLabel = new Label(LearnLayer, SWT.NONE);
				HintLabel.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false, false, 1, 1));
				HintLabel.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.NORMAL));
				HintLabel.setAlignment(SWT.CENTER);
				HintLabel.setText("[Hint]");
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
		GridLayout gl_LoadLayer = new GridLayout(2, false);
		gl_LoadLayer.verticalSpacing = 0;
		LoadLayer.setLayout(gl_LoadLayer);
		
		LoadTopWidgetsLayer = new Composite(LoadLayer, SWT.NONE);
		LoadTopWidgetsLayer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		GridLayout gl_LoadTopWidgetsLayer = new GridLayout(2, false);
		gl_LoadTopWidgetsLayer.marginWidth = 0;
		gl_LoadTopWidgetsLayer.verticalSpacing = 0;
		LoadTopWidgetsLayer.setLayout(gl_LoadTopWidgetsLayer);
		
		LoadOptionsLayer = new Composite(LoadTopWidgetsLayer, SWT.NONE);
		LoadOptionsLayer.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		GridLayout gl_LoadOptionsLayer = new GridLayout(2, false);
		gl_LoadOptionsLayer.marginWidth = 0;
		gl_LoadOptionsLayer.verticalSpacing = 0;
		LoadOptionsLayer.setLayout(gl_LoadOptionsLayer);
		
		lblLoadFrom = new Label(LoadOptionsLayer, SWT.NONE);
		lblLoadFrom.setBounds(0, 0, 55, 15);
		lblLoadFrom.setText("Load from");
		
		LoadOptionsCombo = new CCombo(LoadOptionsLayer, SWT.BORDER);
		LoadOptionsCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String selected_option = LoadOptionsCombo.getItem(LoadOptionsCombo.getSelectionIndex());
				switch(selected_option) {
					case "csv file":
						LoadSelectFileLayer.setVisible(true);
						
						if(loaded_file_name.length() > 0) {
							Event ev = new Event();
							ev.data = loaded_file_name;
							SelectFileButton.notifyListeners(SWT.Selection, ev);
						}
						break;
					case "user vocab":
						LoadSelectFileLayer.setVisible(false);
						
						loaded_vocab_list = user.getVocabList();
						refreshLoadTable(loaded_vocab_list);
						break;
				}
				
			}
		});
		LoadOptionsCombo.setEditable(false);
		LoadOptionsCombo.setItems(new String[] {"csv file", "user vocab"});
		LoadOptionsCombo.select(0);
		
		LoadSelectFileLayer = new Composite(LoadTopWidgetsLayer, SWT.NONE);
		LoadSelectFileLayer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		GridLayout gl_LoadSelectFileLayer = new GridLayout(2, false);
		LoadSelectFileLayer.setLayout(gl_LoadSelectFileLayer);
		//LoadSelectFileLayer.setVisible(false);
		
		SelectFileButton = new Button(LoadSelectFileLayer, SWT.NONE);
		SelectFileButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		SelectFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String file_path;
				
				if(e.data == null) {
					// skip FileDialog if this method is called through program ("load").
					FileDialog fd = new FileDialog(shell);
					file_path = fd.open();
				}else {
					file_path = (String)e.data;
				}
				
				if(file_path == null) return; //canceled
				
				String working_dir = System.getProperty("user.dir");
				int idx = file_path.indexOf(working_dir);
				
				if(idx != -1) {
					Path p1 = Paths.get(file_path);
					Path p2 = Paths.get(working_dir);
					file_path = p2.relativize(p1).toString();
				}			


				try {
					loaded_vocab_list = Util.loadFromCSV(file_path);
					loaded_file_name = file_path;
					
					List<Vocab> user_vocab = user.getVocabList();
					Util.replaceVocab(loaded_vocab_list, user_vocab);
					
					refreshLoadTable(loaded_vocab_list);
					LoadTable.layout(true);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				}
				
				
				LoadSelectedFileLabel.setText(String.format("Selecteed file: %s", Paths.get(file_path).getFileName().toString()));
				LoadSelectedFileLabel.getParent().layout(true);
			}
		});
		SelectFileButton.setText("Select File");
		
		LoadSelectedFileLabel = new Label(LoadSelectFileLayer, SWT.WRAP);
		LoadSelectedFileLabel.setText("Selected file: ");
		
		LoadSelectVocabLayer = new Composite(LoadTopWidgetsLayer, SWT.NONE);
		LoadSelectVocabLayer.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 2, 1));
		GridLayout gl_LoadSelectVocabLayer = new GridLayout(4, false);
		gl_LoadSelectVocabLayer.marginWidth = 0;
		gl_LoadSelectVocabLayer.verticalSpacing = 0;
		gl_LoadSelectVocabLayer.marginHeight = 0;
		LoadSelectVocabLayer.setLayout(gl_LoadSelectVocabLayer);
		
		lblSelect = new Label(LoadSelectVocabLayer, SWT.NONE);
		lblSelect.setBounds(0, 0, 55, 15);
		lblSelect.setText("Select");
		
		LoadSelectVocabCombo = new CCombo(LoadSelectVocabLayer, SWT.BORDER);
		LoadSelectVocabCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String selection_option = LoadSelectVocabCombo.getItem(LoadSelectVocabCombo.getSelectionIndex());
				switch(selection_option){
					case "top":
						((StackLayout)LoadSelectVocabStack.getLayout()).topControl = LoadCountLayer;
						break;
					case "in range":
						((StackLayout)LoadSelectVocabStack.getLayout()).topControl = LoadRangeLayer;
						
						break;
					case "random":
						((StackLayout)LoadSelectVocabStack.getLayout()).topControl = LoadCountLayer;
						break;
				}
				
				LoadSelectVocabStack.pack();
				LoadSelectVocabStack.layout(true);
			}
		});
		LoadSelectVocabCombo.setEditable(false);
		LoadSelectVocabCombo.setItems(new String[] {"top", "in range", "random"});
		LoadSelectVocabCombo.select(0);
		
		LoadSelectVocabStack = new Composite(LoadSelectVocabLayer, SWT.NONE);
		LoadSelectVocabStack.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		LoadSelectVocabStack.setLayout(new StackLayout());
		
		LoadCountLayer = new Composite(LoadSelectVocabStack, SWT.NONE);
		LoadCountLayer.setLayout(new GridLayout(2, false));
		
		LoadCountField = new Text(LoadCountLayer, SWT.BORDER);
		
		Button btnGo = new Button(LoadCountLayer, SWT.NONE);
		btnGo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String selection_option = LoadSelectVocabCombo.getItem(LoadSelectVocabCombo.getSelectionIndex());
				int count;
				try {
					count = Integer.parseInt(LoadCountField.getText());
				}catch(NumberFormatException ex) {
					return;
				}
				
				switch(selection_option) {
					case "top":
						
						TableItem[] items = LoadTable.getItems();
						LoadTable.deselectAll();
						for(int i = 0; i < items.length && i < count; ++i) {
							LoadTable.select(i);
						}
						break;
						
					case "random":
						items = LoadTable.getItems();
						List<Integer> num_list = new ArrayList<Integer>(items.length);
						
						LoadTable.deselectAll();
						for(int i = 0; i < items.length; ++i) {
							num_list.add(i);
						}
						
						for(int i = 0; i < count && num_list.size() > 0; ++i) {
							int idx = (int)(Math.random() * num_list.size());
							idx = num_list.set(idx, num_list.get(num_list.size() - 1));
							num_list.remove(num_list.size() - 1);
							LoadTable.select(idx);
						}
						break;
				}
				
			}
		});
		btnGo.setText("Go");
		
		LoadRangeLayer = new Composite(LoadSelectVocabStack, SWT.NONE);
		LoadRangeLayer.setLayout(new GridLayout(5, false));
		
		lblFrom = new Label(LoadRangeLayer, SWT.NONE);
		lblFrom.setText("from");
		
		LoadFromField = new Text(LoadRangeLayer, SWT.BORDER);
		GridData gd_LoadFromField = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_LoadFromField.widthHint = 50;
		LoadFromField.setLayoutData(gd_LoadFromField);
		
		lblTo = new Label(LoadRangeLayer, SWT.NONE);
		lblTo.setText("to");
		
		LoadToField = new Text(LoadRangeLayer, SWT.BORDER);
		GridData gd_LoadToField = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_LoadToField.widthHint = 50;
		LoadToField.setLayoutData(gd_LoadToField);
		
		btnGo_1 = new Button(LoadRangeLayer, SWT.NONE);
		btnGo_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int from = Integer.parseInt(LoadFromField.getText());
				int to = Integer.parseInt(LoadToField.getText());
				
				TableItem[] items = LoadTable.getItems();
				LoadTable.deselectAll();
				for(int i = from; i <= to && i < items.length; ++i) {
					LoadTable.select(i - 1);
				}
			}
		});
		btnGo_1.setText("Go");
		
		LoadTable = new Table(LoadLayer, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		LoadTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		LoadTable.setLinesVisible(true);
		LoadTable.setHeaderVisible(true);
		
		tableColumn = new TableColumn(LoadTable, SWT.NONE);
		tableColumn.setWidth(34);
		tableColumn.setText("#");
		
		tblclmnSource_1 = new TableColumn(LoadTable, SWT.NONE);
		tblclmnSource_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Comparator<Vocab> c = (Vocab v1, Vocab v2) -> {
					return v1.source.toLowerCase().compareTo(v2.source.toLowerCase());
				};
				
				if(load_sorted_column == tblclmnSource_1) {
					load_sorted_column = null;
					refreshLoadTable(loaded_vocab_list, c, true);
				}else {
					load_sorted_column = tblclmnSource_1;
					refreshLoadTable(loaded_vocab_list, c, false);
				}
			}
		});
		tblclmnSource_1.setWidth(112);
		tblclmnSource_1.setText("Source");
		
		tblclmnTarget_1 = new TableColumn(LoadTable, SWT.NONE);
		tblclmnTarget_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Comparator<Vocab> c = (Vocab v1, Vocab v2) -> {
					return v1.target.toLowerCase().compareTo(v2.target.toLowerCase());
				};
				
				if(load_sorted_column == tblclmnTarget_1) {
					load_sorted_column = null;
					refreshLoadTable(loaded_vocab_list, c, true);
				}else {
					load_sorted_column = tblclmnTarget_1;
					refreshLoadTable(loaded_vocab_list, c, false);
				}

			}
		});
		tblclmnTarget_1.setWidth(103);
		tblclmnTarget_1.setText("Target");
		
		tblclmnLastPracticed_1 = new TableColumn(LoadTable, SWT.NONE);
		tblclmnLastPracticed_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Comparator<Vocab> c = (Vocab v1, Vocab v2) -> {
					if(v1.last_practiced == null)
						return -1;
					if(v2.last_practiced == null)
						return 1;
					
					return v1.last_practiced.compareTo(v2.last_practiced);
				};
				
				if(load_sorted_column == tblclmnLastPracticed_1) {
					load_sorted_column = null;
					refreshLoadTable(loaded_vocab_list, c, true);
				}else {
					load_sorted_column = tblclmnLastPracticed_1;
					refreshLoadTable(loaded_vocab_list, c, false);
				}
				
				
			}
		});
		tblclmnLastPracticed_1.setWidth(65);
		tblclmnLastPracticed_1.setText("Practiced");
		
		tblclmnAttempts_1 = new TableColumn(LoadTable, SWT.NONE);
		tblclmnAttempts_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Comparator<Vocab> c = (Vocab v1, Vocab v2) -> {
					if(practice_set.isLearnTarget())
						return v1.stats_tgt.getAttempts() - v2.stats_tgt.getAttempts();
					return v1.stats_src.getAttempts() - v2.stats_src.getAttempts();
				};
				
				if(load_sorted_column == tblclmnAttempts_1) {
					load_sorted_column = null;
					refreshLoadTable(loaded_vocab_list, c, true);
				}else {
					load_sorted_column = tblclmnAttempts_1;
					refreshLoadTable(loaded_vocab_list, c, false);
				}
			}
		});
		tblclmnAttempts_1.setWidth(65);
		tblclmnAttempts_1.setText("Attempts");
		
		tblclmnScores_1 = new TableColumn(LoadTable, SWT.NONE);
		tblclmnScores_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Comparator<Vocab> c = (Vocab v1, Vocab v2) -> {
					if(practice_set.isLearnTarget())
						return (int)(v1.stats_tgt.getScore() - v2.stats_tgt.getScore());
					return (int)(v1.stats_src.getScore() - v2.stats_src.getScore());
				};
				
				if(load_sorted_column == tblclmnScores_1) {
					load_sorted_column = null;
					refreshLoadTable(loaded_vocab_list, c, true);
				}else {
					load_sorted_column = tblclmnScores_1;
					refreshLoadTable(loaded_vocab_list, c, false);
				}
			}
		});
		tblclmnScores_1.setWidth(100);
		tblclmnScores_1.setText("Scores");
		
		tbtmStats = new TabItem(tabFolder, SWT.NONE);
		tbtmStats.setText("Stats");
		
		StatsLayer = new Composite(tabFolder, SWT.NONE);
		tbtmStats.setControl(StatsLayer);
		StatsLayer.setLayout(new GridLayout(1, false));
		
		TableLayer = new Composite(StatsLayer, SWT.NONE);
		TableLayer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		TableLayer.setLayout(new GridLayout(1, false));
		
		StatsTable = new Table(TableLayer, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		StatsTable.setLinesVisible(true);
		StatsTable.setHeaderVisible(true);
		StatsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		tblclmnSource = new TableColumn(StatsTable, SWT.NONE);
		tblclmnSource.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Comparator<Vocab> c = (Vocab v1, Vocab v2) -> {
					return v1.source.toLowerCase().compareTo(v2.source.toLowerCase());
				};
				
				if(stats_sorted_column == tblclmnSource) {
					stats_sorted_column = null;
					refreshStats(user.getVocabList(), c, true);
				}else {
					stats_sorted_column = tblclmnSource;
					refreshStats(user.getVocabList(), c, false);
				}
			}
		});
		tblclmnSource.setWidth(112);
		tblclmnSource.setText("Source");
		
		tblclmnTarget = new TableColumn(StatsTable, SWT.NONE);
		tblclmnTarget.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Comparator<Vocab> c = (Vocab v1, Vocab v2) -> {
					return v1.target.toLowerCase().compareTo(v2.target.toLowerCase());
				};
				
				if(stats_sorted_column == tblclmnTarget) {
					stats_sorted_column = null;
					refreshStats(user.getVocabList(), c, true);
				}else {
					stats_sorted_column = tblclmnTarget;
					refreshStats(user.getVocabList(), c, false);
				}
			}
		});
		tblclmnTarget.setWidth(103);
		tblclmnTarget.setText("Target");
		
		tblclmnLastPracticed = new TableColumn(StatsTable, SWT.NONE);
		tblclmnLastPracticed.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Comparator<Vocab> c = (Vocab v1, Vocab v2) -> {
					if(v1.last_practiced == null)
						return -1;
					if(v2.last_practiced == null)
						return 1;
					
					return v1.last_practiced.compareTo(v2.last_practiced);
				};
				
				if(stats_sorted_column == tblclmnLastPracticed) {
					stats_sorted_column = null;
					refreshStats(user.getVocabList(), c, true);
				}else {
					stats_sorted_column = tblclmnLastPracticed;
					refreshStats(user.getVocabList(), c, false);
				}
			}
		});
		tblclmnLastPracticed.setWidth(65);
		tblclmnLastPracticed.setText("Practiced");
		
		tblclmnAttempts = new TableColumn(StatsTable, SWT.NONE);
		tblclmnAttempts.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Comparator<Vocab> c = (Vocab v1, Vocab v2) -> {
					if(practice_set.isLearnTarget())
						return v1.stats_tgt.getAttempts() - v2.stats_tgt.getAttempts();
					return v1.stats_src.getAttempts() - v2.stats_src.getAttempts();
				};
				
				if(stats_sorted_column == tblclmnAttempts) {
					stats_sorted_column = null;
					refreshStats(user.getVocabList(), c, true);
				}else {
					stats_sorted_column = tblclmnAttempts;
					refreshStats(user.getVocabList(), c, false);
				}
			}
		});
		tblclmnAttempts.setWidth(65);
		tblclmnAttempts.setText("Attempts");
		
		tblclmnScores = new TableColumn(StatsTable, SWT.NONE);
		tblclmnScores.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Comparator<Vocab> c = (Vocab v1, Vocab v2) -> {
					if(practice_set.isLearnTarget())
						return (int)(v1.stats_tgt.getScore() - v2.stats_tgt.getScore());
					return (int)(v1.stats_src.getScore() - v2.stats_src.getScore());
				};
				
				if(stats_sorted_column == tblclmnScores) {
					stats_sorted_column = null;
					refreshStats(user.getVocabList(), c, true);
				}else {
					stats_sorted_column = tblclmnScores;
					refreshStats(user.getVocabList(), c, false);
				}
			}
		});
		tblclmnScores.setWidth(100);
		tblclmnScores.setText("Scores");
		
		Menu menu = new Menu(StatsTable);
		StatsTable.setMenu(menu);
		
		MenuItem mntmNewItem = new MenuItem(menu, SWT.NONE);
		mntmNewItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = StatsTable.getSelection();
				
				if(items.length == 0) return;
				
				for(TableItem item : items) {
					Vocab vocab = stats_vocab_map.get(item);
					vocab.swapSrcTgt();
					item.setText(0, vocab.source);
					item.setText(1, vocab.target);
					if(stats_swap_src_tgt) {
						item.setText(3, Integer.toString(vocab.stats_src.getAttempts()));
						item.setText(4, String.format("%.0f", vocab.stats_src.getScore()));
					}else {
						item.setText(3, Integer.toString(vocab.stats_tgt.getAttempts()));
						item.setText(4, String.format("%.0f", vocab.stats_tgt.getScore()));
					}
				}
				
			}
		});
		mntmNewItem.setText("Src <--> Tgt");
		
		mntmRemove = new MenuItem(menu, SWT.NONE);
		mntmRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = StatsTable.getSelection();
				
				if(items.length == 0) return;
				
				List<Vocab> selected_vocab = new ArrayList<Vocab>(items.length);
				
				for(TableItem item : items) {
					Vocab vocab = stats_vocab_map.get(item);
					selected_vocab.add(vocab);
				}
				user.getVocabList().removeAll(selected_vocab);
				deleted_vocab = selected_vocab;
				undo_list.add(() -> {
					user.getVocabList().addAll(deleted_vocab);
					refreshStats(user.getVocabList());
					});
				mntmUndo.setEnabled(true);
				refreshStats(user.getVocabList());
			}
		});
		mntmRemove.setText("Remove");
		
		mntmUndo = new MenuItem(menu, SWT.NONE);
		mntmUndo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				undo_list.remove(0).run();
				mntmUndo.setEnabled(false);
			}
		});
		mntmUndo.setEnabled(false);
		mntmUndo.setText("Undo");
		
		btnSwapStats = new Button(StatsLayer, SWT.NONE);
		btnSwapStats.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				stats_swap_src_tgt = !stats_swap_src_tgt;
				refreshStats(user.getVocabList());
			}
		});
		GridData gd_btnSwapStats = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnSwapStats.widthHint = 57;
		btnSwapStats.setLayoutData(gd_btnSwapStats);
		btnSwapStats.setText("<-->");
		
		tbtmSettings = new TabItem(tabFolder, SWT.NONE);
		tbtmSettings.setText("Settings");
		
		SettingsTopLayer = new Composite(tabFolder, SWT.NONE);
		SettingsTopLayer.setLayout(new FillLayout(SWT.HORIZONTAL));
		tbtmSettings.setControl(SettingsTopLayer);
		
		SettingsLeftLayer = new Composite(SettingsTopLayer, SWT.NONE);
		
		SettingsLeftLayer.setLayout(new GridLayout(2, false));
		
		Settings_InkCanvas_Label = new Label(SettingsLeftLayer, SWT.NONE);
		Settings_InkCanvas_Label.setToolTipText("Use canvas to draw/write input instead of keyboard input");
		Settings_InkCanvas_Label.setText("Ink Canvas");
		
		ToggleInkCanvas = new Button(SettingsLeftLayer, SWT.CHECK);
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
		
		Settings_PenSize_Label = new Label(SettingsLeftLayer, SWT.NONE);
		Settings_PenSize_Label.setToolTipText("Change thickness of pen stroke");
		Settings_PenSize_Label.setText("Pen Size");
		
		PenFontCombo = new CCombo(SettingsLeftLayer, SWT.BORDER);
		PenFontCombo.setEditable(false);
		PenFontCombo.setEnabled(false);
		PenFontCombo.setToolTipText("Select pencil thickness");
		PenFontCombo.setLayoutDeferred(true);
		PenFontCombo.setItems(new String[] {"4", "8", "10", "12", "14", "16"});
		PenFontCombo.select(0);
		
		Settings_Ignore_Case_Label = new Label(SettingsLeftLayer, SWT.NONE);
		Settings_Ignore_Case_Label.setToolTipText("Ignore Case, like 'a' and 'A'  are treated the same");
		Settings_Ignore_Case_Label.setText("Ignore Case");
		
		ToggleCase = new Button(SettingsLeftLayer, SWT.CHECK);
		ToggleCase.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				practice_set.setIgnoreCase(ToggleCase.getSelection());
			}
		});
		
		Settings_Ignore_Accents_Label = new Label(SettingsLeftLayer, SWT.NONE);
		Settings_Ignore_Accents_Label.setToolTipText("Ignore Accents, like 'o' and '\u00F6' are treated the same");
		Settings_Ignore_Accents_Label.setText("Ignore Accents");
		
		ToggleAccents = new Button(SettingsLeftLayer, SWT.CHECK);
		ToggleAccents.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				practice_set.setIgnoreAccents(ToggleAccents.getSelection());
			}
		});
		
		Settings_Ignore_Special_Label = new Label(SettingsLeftLayer, SWT.NONE);
		Settings_Ignore_Special_Label.setToolTipText("Ignore non-aplha chars, like '!@#$%^&*()-_~'");
		Settings_Ignore_Special_Label.setText("Ignore Special");
		
		ToggleSpecial = new Button(SettingsLeftLayer, SWT.CHECK);
		ToggleSpecial.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				practice_set.setIgnoreSpecial(ToggleSpecial.getSelection());
			}
		});
		
		lblShuffleOrder = new Label(SettingsLeftLayer, SWT.NONE);
		lblShuffleOrder.setToolTipText("Shuffle the loaded vocab set each round");
		lblShuffleOrder.setText("Shuffle");
		
		ToggleShuffle = new Button(SettingsLeftLayer, SWT.CHECK);
		ToggleShuffle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shuffle_vocab = ToggleShuffle.getSelection();
			}
		});
		
		Settings_Hints_Label = new Label(SettingsLeftLayer, SWT.NONE);
		Settings_Hints_Label.setToolTipText("Give progreesively more hints with every failed attempt");
		Settings_Hints_Label.setText("Hints");
		
		ToggleHints = new CCombo(SettingsLeftLayer, SWT.BORDER);
		ToggleHints.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				practice_set.setHintFormat(ToggleHints.getSelectionIndex());
				
				if(!practice_set.isLoaded()) return;
				
				String hint = practice_set.getHint();

				if(hint == null)
					hint = "";
				HintLabel.setText(hint);
				
				HintLabel.getParent().layout(true);
			}
		});
		ToggleHints.setEditable(false);
		ToggleHints.setItems(new String[] {"None", "LeftToRight", "RightToLeft", "InToOut", "OutToIn", "Random"});
		ToggleHints.select(0);
		
		SettingsDelimitersLabel = new Label(SettingsLeftLayer, SWT.NONE);
		SettingsDelimitersLabel.setToolTipText("Delimiters are characters which separate words. \r\nDelimiters will allow multiple answers to a vocab submission\r\nEx: Source=hola, Target=\"hello, hi\", Delimiters=,\r\n      Both hello and hi can be submitted as correct asnwers");
		SettingsDelimitersLabel.setText("Delimiters");
		
		SettingsDelimitersField = new Text(SettingsLeftLayer, SWT.BORDER);

		GridData gd_SettingsDelimitersField = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_SettingsDelimitersField.widthHint = 80;
		SettingsDelimitersField.setLayoutData(gd_SettingsDelimitersField);
		SettingsDelimitersField.addVerifyListener(new VerifyListener() {
			
			@Override
			public void verifyText(VerifyEvent e) {
				for(char c  : e.text.toCharArray()) {
					if(c == ']' || c == '[')
						e.doit = false;
						return;
				}
					
				
			}
			
		});
		
		btnMacros = new Button(SettingsLeftLayer, SWT.TOGGLE);
		
		btnMacros.setText("Open Macros");
		new Label(SettingsLeftLayer, SWT.NONE);
		
		SettingsUserGroup = new Group(SettingsLeftLayer, SWT.NONE);
		SettingsUserGroup.setLayout(new GridLayout(3, false));
		SettingsUserGroup.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		SettingsUserGroup.setText("username");
		
		btnClearVocab = new Button(SettingsUserGroup, SWT.NONE);
		btnClearVocab.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox dialog = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES| SWT.NO);
				dialog.setText("Clear Vocab");
				dialog.setMessage("Do you really want to clear all your progress?");

				if(dialog.open() == SWT.YES) {
					dialog.setMessage("Clearing Vocab cannot be undone.\n Are you Sure?");
					if(dialog.open() == SWT.YES) {
						user.getVocabList().clear();
						try {
							Util.saveUser(user);
						} catch (IOException e1) {
							System.err.println("Failed to save user vocab changes.");
						}
					}
				}
			}
		});
		btnClearVocab.setText("Clear Vocab");
		
		Button btnResetStats = new Button(SettingsUserGroup, SWT.NONE);
		btnResetStats.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox dialog = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
					dialog.setText("Reset Stats");
					dialog.setMessage("Do you really want to reset stats?");

				if(dialog.open() == SWT.YES) {
					dialog.setMessage("Resetting stats cannot be undone.\n Are you Sure?");
					if(dialog.open() == SWT.YES) {
						user.getVocabList().forEach((v)->{ v.clearStats(); });
						try {
							Util.saveUser(user);
						} catch (IOException e1) {
							System.err.println("Failed to save user stat changes.");
						}
					}
				}
			}
		});
		btnResetStats.setText("Reset Stats");
		
		btnLogout = new Button(SettingsUserGroup, SWT.NONE);
		btnLogout.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox dialog = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				dialog.setText("Logout");
				dialog.setMessage(String.format("Do you really want to log out of %s?", user.getUsername()));
				if(dialog.open() == SWT.YES) {
					try {
						saveSession(String.format("resources/users/%s/session", user.getUsername()));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					user = null;
					((StackLayout)shell.getLayout()).topControl = LoginLayer;
					LoginLayer.getParent().layout(true);
				}
			}
		});
		btnLogout.setText("Logout");
		
		SettingsRightLayer = new Composite(SettingsTopLayer, SWT.NONE);
		SettingsRightLayer.setLayout(new StackLayout());
		
		Composite MacroBindingLayer = new Composite(SettingsRightLayer, SWT.NONE);
		MacroBindingLayer.setLayout(new GridLayout(1, false));
		
		btnMacros.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StackLayout sl = ((StackLayout)SettingsRightLayer.getLayout());
				if(sl.topControl != MacroBindingLayer) {
					sl.topControl = MacroBindingLayer;
					btnMacros.setText("Close Macros");
				}else {
					sl.topControl = null;
					btnMacros.setText("Open Macros");
				}
				SettingsRightLayer.layout(true);
			}
		});
		
		MacroScrollLayer = new ScrolledComposite(MacroBindingLayer, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gd_MacroScrollLayer = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_MacroScrollLayer.widthHint = 203;
		MacroScrollLayer.setLayoutData(gd_MacroScrollLayer);
		MacroScrollLayer.setExpandHorizontal(true);
		MacroScrollLayer.setExpandVertical(true);
		
		MacroListLayer = new Composite(MacroScrollLayer, SWT.NONE);
		MacroListLayer.setLayout(new GridLayout(1, false));
		MacroScrollLayer.setContent(MacroListLayer);
		MacroScrollLayer.setMinSize(MacroListLayer.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		MacroButtonsLayer = new Composite(MacroBindingLayer, SWT.NONE);
		MacroButtonsLayer.setLayout(new GridLayout(1, false));
		
		btnAdd = new Button(MacroButtonsLayer, SWT.NONE);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addMacroEntry(null ,null, false);
			}
		});
		GridData gd_btnAdd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnAdd.widthHint = 60;
		btnAdd.setLayoutData(gd_btnAdd);
		btnAdd.setText("Add");
		
		Composite QuickKeyboardBindingLayer = new Composite(SettingsRightLayer, SWT.NONE);
		QuickKeyboardBindingLayer.setLayout(new FillLayout(SWT.HORIZONTAL));
		((StackLayout)SettingsRightLayer.getLayout()).topControl = QuickKeyboardBindingLayer;
		QuickKeyboardBindingLayer.layout(true);
		QuickKeyboardBindingLayer.getParent().layout(true);
		QuickKeyboardBindingLayer.getParent().getParent().layout(true);

		
		LoginLayer = new Composite(shell, SWT.NONE);
		LoginLayer.setLayout(new GridLayout(3, false));
		
		LoginFill1 = new Label(LoginLayer, SWT.NONE);
		LoginFill1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		
		lblVocabPractice = new Label(LoginLayer, SWT.NONE);
		lblVocabPractice.setFont(SWTResourceManager.getFont("Segoe UI", 26, SWT.NORMAL));
		lblVocabPractice.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		lblVocabPractice.setText("Vocab Practice");
		
		LoginFill2 = new Label(LoginLayer, SWT.NONE);
		LoginFill2.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		new Label(LoginLayer, SWT.NONE);
		
		LoginForumStack = new Composite(LoginLayer, SWT.NONE);
		LoginForumStack.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		LoginForumStack.setLayout(new StackLayout());
		
		LoginFieldsLayer = new Composite(LoginForumStack, SWT.NONE);
		GridLayout gl_LoginFieldsLayer = new GridLayout(1, false);
		gl_LoginFieldsLayer.verticalSpacing = 0;
		LoginFieldsLayer.setLayout(gl_LoginFieldsLayer);
		
		UsernameLayer = new Composite(LoginFieldsLayer, SWT.NONE);
		UsernameLayer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_UsernameLayer = new GridLayout(1, false);
		gl_UsernameLayer.verticalSpacing = 0;
		UsernameLayer.setLayout(gl_UsernameLayer);
		
		LUsernameLabel = new Label(UsernameLayer, SWT.NONE);
		LUsernameLabel.setText("Username:");
		LUsernameLabel.setAlignment(SWT.RIGHT);
		
		LUsernameField = new Text(UsernameLayer, SWT.BORDER);
		LUsernameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.keyCode == SWT.Selection) {
					btnLogin.notifyListeners(SWT.Selection, new Event());
				}
			}
		});
		LUsernameField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		PasswordLayer = new Composite(LoginFieldsLayer, SWT.NONE);
		PasswordLayer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		GridLayout gl_PasswordLayer = new GridLayout(1, false);
		gl_PasswordLayer.marginHeight = 0;
		gl_PasswordLayer.verticalSpacing = 0;
		PasswordLayer.setLayout(gl_PasswordLayer);
		
		LPasswordLabel = new Label(PasswordLayer, SWT.NONE);
		LPasswordLabel.setEnabled(false);
		LPasswordLabel.setText("Password:");
		LPasswordLabel.setAlignment(SWT.RIGHT);
		
		LPasswordField = new Text(PasswordLayer, SWT.BORDER | SWT.PASSWORD);
		LPasswordField.setEnabled(false);
		LPasswordField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnLogin = new Button(LoginFieldsLayer, SWT.NONE);
		btnLogin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String username = LUsernameField.getText();
				if(username.length() == 0)
					return;
				
				File users_dir = new File("resources/users");
				if(!users_dir.exists()) {
					LErrorLabel.setText("Invalid Username.");
					LErrorLabel.getParent().layout(true);
					return;
				}
				
				for(String user_dir_name : users_dir.list()) {
					if(user_dir_name.equals(username)) {
						
						try {
							user = Util.loadUser(username);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						try {
							loadSession(String.format("resources/users/%s/session", username));
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						LUsernameField.setText("");
						SettingsUserGroup.setText(username);
						
						((StackLayout)shell.getLayout()).topControl = tabFolder;
						tabFolder.getParent().layout(true);
						return;
					}
				}
				
				LErrorLabel.setText("Invalid Username.");
				LErrorLabel.getParent().layout(true);
				
			}
		});
		GridData gd_btnLogin = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_btnLogin.verticalIndent = 10;
		btnLogin.setLayoutData(gd_btnLogin);
		btnLogin.setText("Login");
		
		SignupLinkLayer = new Composite(LoginFieldsLayer, SWT.NONE);
		SignupLinkLayer.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		GridLayout gl_SignupLinkLayer = new GridLayout(1, false);
		gl_SignupLinkLayer.marginWidth = 0;
		gl_SignupLinkLayer.marginHeight = 0;
		gl_SignupLinkLayer.horizontalSpacing = 0;
		SignupLinkLayer.setLayout(gl_SignupLinkLayer);
		
		CALink = new Link(SignupLinkLayer, 0);
		CALink.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CAUsernameField.setText("");
				CAPasswordField.setText("");
				CAConfPasswordField.setText("");
				((StackLayout)LoginForumStack.getLayout()).topControl = CreateAccountFieldsLayer;
				LoginForumStack.layout(true);
			}
		});
		CALink.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		CALink.setText("<a>Create account</a>");
		
		CreateAccountFieldsLayer = new Composite(LoginForumStack, SWT.NONE);
		GridLayout gl_CreateAccountFieldsLayer = new GridLayout(1, false);
		gl_CreateAccountFieldsLayer.verticalSpacing = 0;
		CreateAccountFieldsLayer.setLayout(gl_CreateAccountFieldsLayer);
		
		UsernameLayer_1 = new Composite(CreateAccountFieldsLayer, SWT.NONE);
		UsernameLayer_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_UsernameLayer_1 = new GridLayout(1, false);
		gl_UsernameLayer_1.verticalSpacing = 0;
		UsernameLayer_1.setLayout(gl_UsernameLayer_1);
		
		CAUsernameField_1 = new Label(UsernameLayer_1, SWT.NONE);
		CAUsernameField_1.setText("Username:");
		CAUsernameField_1.setAlignment(SWT.RIGHT);
		
		CAUsernameField = new Text(UsernameLayer_1, SWT.BORDER);
		CAUsernameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.keyCode == SWT.Selection) {
					btnCreateAccount.notifyListeners(SWT.Selection, new Event());
				}
			}
		});
		CAUsernameField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		PasswordLayer_1 = new Composite(CreateAccountFieldsLayer, SWT.NONE);
		PasswordLayer_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		GridLayout gl_PasswordLayer_1 = new GridLayout(1, false);
		gl_PasswordLayer_1.marginBottom = 5;
		gl_PasswordLayer_1.marginHeight = 0;
		gl_PasswordLayer_1.verticalSpacing = 0;
		PasswordLayer_1.setLayout(gl_PasswordLayer_1);
		
		CAPasswordLabel = new Label(PasswordLayer_1, SWT.NONE);
		CAPasswordLabel.setEnabled(false);
		CAPasswordLabel.setText("Password:");
		CAPasswordLabel.setAlignment(SWT.RIGHT);
		
		CAPasswordField = new Text(PasswordLayer_1, SWT.BORDER | SWT.PASSWORD);
		CAPasswordField.setEnabled(false);
		CAPasswordField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		ConfPasswordLayer = new Composite(CreateAccountFieldsLayer, SWT.NONE);
		ConfPasswordLayer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		GridLayout gl_ConfPasswordLayer = new GridLayout(1, false);
		gl_ConfPasswordLayer.marginHeight = 0;
		gl_ConfPasswordLayer.verticalSpacing = 0;
		ConfPasswordLayer.setLayout(gl_ConfPasswordLayer);
		
		CAConfPasswordLabel = new Label(ConfPasswordLayer, SWT.NONE);
		CAConfPasswordLabel.setEnabled(false);
		CAConfPasswordLabel.setText("Confirm Password:");
		CAConfPasswordLabel.setAlignment(SWT.RIGHT);
		
		CAConfPasswordField = new Text(ConfPasswordLayer, SWT.BORDER | SWT.PASSWORD);
		CAConfPasswordField.setEnabled(false);
		CAConfPasswordField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnCreateAccount = new Button(CreateAccountFieldsLayer, SWT.NONE);
		btnCreateAccount.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				File users_dir = new File("resources/users");
				String username = CAUsernameField.getText();
				if(username.length() == 0) {
					System.err.println("Username length must be grate than 0");
					return;
				}
				
				if(!users_dir.exists()) {
					System.out.println(users_dir.mkdirs());
				}
				
				for(String file_name : users_dir.list()) {
					if(file_name.equals(username)) {
						MessageBox dialog = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
						dialog.setText("Username taken");
						dialog.setMessage(String.format("Username %s is already taken.", username));

						dialog.open();
						return;
					}
				}
				File new_user = new File("resources/users/" + username);
				new_user.mkdirs();
				MessageBox dialog = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
				dialog.setText("Account Created");
				dialog.setMessage(String.format("Account Created for %s", username));

				dialog.open();
				
				LoginLink.notifyListeners(SWT.Selection, new Event());
			}
		});
		GridData gd_btnCreateAccount = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_btnCreateAccount.verticalIndent = 10;
		btnCreateAccount.setLayoutData(gd_btnCreateAccount);
		btnCreateAccount.setText("Create Account");
		
		LoginLink = new Link(CreateAccountFieldsLayer, SWT.NONE);
		LoginLink.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				LPasswordField.setText("");
				LErrorLabel.setText("");
				((StackLayout)LoginForumStack.getLayout()).topControl = LoginFieldsLayer;
				LoginForumStack.layout(true);
			}
		});
		LoginLink.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		LoginLink.setText("<a>Back to login</a>");
		
		SignupLinkLayer_1 = new Composite(CreateAccountFieldsLayer, SWT.NONE);
		GridLayout gl_SignupLinkLayer_1 = new GridLayout(1, false);
		gl_SignupLinkLayer_1.marginWidth = 0;
		gl_SignupLinkLayer_1.marginHeight = 0;
		gl_SignupLinkLayer_1.horizontalSpacing = 0;
		SignupLinkLayer_1.setLayout(gl_SignupLinkLayer_1);
		LoginForumStack.setTabList(new Control[]{LoginFieldsLayer, CreateAccountFieldsLayer});
		new Label(LoginLayer, SWT.NONE);
		
		((StackLayout)shell.getLayout()).topControl = LoginLayer;
		((StackLayout)LoginForumStack.getLayout()).topControl = LoginFieldsLayer;
		((StackLayout)LoadSelectVocabStack.getLayout()).topControl = LoadCountLayer;
		
		LoadBottomLayer = new Composite(LoadLayer, SWT.NONE);
		LoadBottomLayer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		GridLayout gl_LoadBottomLayer = new GridLayout(2, false);
		gl_LoadBottomLayer.marginWidth = 0;
		gl_LoadBottomLayer.verticalSpacing = 0;
		LoadBottomLayer.setLayout(gl_LoadBottomLayer);
		
		btnNewButton = new Button(LoadBottomLayer, SWT.NONE);
		GridData gd_btnNewButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnNewButton.widthHint = 57;
		btnNewButton.setLayoutData(gd_btnNewButton);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				load_swap_src_tgt = !load_swap_src_tgt;
				refreshLoadTable(loaded_vocab_list);
			}
		});
		btnNewButton.setText("<-->");
		
		btnApply = new Button(LoadBottomLayer, SWT.NONE);
		GridData gd_btnApply = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnApply.widthHint = 64;
		btnApply.setLayoutData(gd_btnApply);
		btnApply.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = LoadTable.getSelection();
				
				if(items.length == 0) return;
				
				List<Vocab> selected_vocab = new ArrayList<Vocab>(items.length);
				
				for(TableItem item : items) {
					selected_vocab.add(loaded_vocab_map.get(item));
				}
				
				user.addVocab(selected_vocab);
				
				if(shuffle_vocab)
					Collections.shuffle(selected_vocab);
				
				loadVocab(selected_vocab);
				practice_set.setLearnTarget(!load_swap_src_tgt);
				
				btnApply.setText("Applied");
				++applied_count;
				shell.getDisplay().timerExec(1000, () -> {
					if(applied_count == 1)
						btnApply.setText("Apply");
					--applied_count;
				});
				
			}
		});
		btnApply.setText("Apply");
		//LoadSelectVocabStack.getParent().layout(true);
		
		LErrorLabel = new Label(LoginFieldsLayer, SWT.NONE);
		LErrorLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		//LoginForumStack.layout(true);
		
		try {
			File session_file = new File("resources/users/session");
			if(!session_file.exists()) return;
			BufferedReader br = new BufferedReader(new FileReader(session_file));
			String username = br.readLine();
			if(username != null) {
				LUsernameField.setText(username);
				btnLogin.notifyListeners(SWT.Selection, new Event());
			}
			br.close();
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		m_bindingContext = initDataBindings();
		
		/*
		Thread auto_save_thread = new Thread(() -> {
			try {
				
				Thread.sleep(auto_save_interval);
				Util.saveUser(user);
				System.out.println("auto-saved.");
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		auto_save_thread.start();*/
		
	}
	public int getGd_CorrectLabelWidthHint() {
		return gd_CorrectLabel.widthHint;
	}
	public void setGd_CorrectLabelWidthHint(int widthHint) {
		gd_CorrectLabel.widthHint = 32;
	}
	
	public void addMacroEntry(String str1, String str2, boolean selected) {
		TextPair tp = new TextPair(MacroListLayer, SWT.NONE);
		macro_widgets.add(tp);
		
		if(selected)
			macros.put(str1, str2);
		
		tp.getCheckBox().setSelection(selected);
		
		tp.getDeleteLabel().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if(tp.isMouseOver()) {
					tp.dispose();
					MacroListLayer.layout(true);
					macro_widgets.remove(tp);
				}
			}
		});
		
		GridData gridData = new GridData();
	    gridData.grabExcessHorizontalSpace = true;
	    gridData.horizontalAlignment = GridData.FILL;
	    tp.setLayoutData(gridData);
	    
	    if(str1 == null && str2 == null) {
	    	tp.getText1().setText("/command");
	    	tp.getText2().setText("result");
	    }else {
	    	tp.getText1().setText(str1);
	    	tp.getText2().setText(str2);
	    }
		
		MacroListLayer.layout(true);
		MacroScrollLayer.setMinSize(MacroListLayer.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		
		
	}
	
	public void loadSession(String session_file_name) throws IOException
	{
		
		File session_file = new File(session_file_name);
		
		BufferedReader br = new BufferedReader(new FileReader(session_file));
		
		for(String line = null; (line = br.readLine()) != null;) {
			String[] split = Util.parseCSV(line);
			switch(split[0])
			{
				case "username":
					SettingsUserGroup.setText(split[1]);
					LUsernameField.setText(split[1]);
					break;
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
				case "shuffle":
					b = Boolean.parseBoolean(split[1]);
					ToggleShuffle.setSelection(b);
					ToggleShuffle.notifyListeners(SWT.Selection, new Event());
					break;
					
				case "hints":
					b = Boolean.parseBoolean(split[1]);
					i = Integer.parseInt(split[1]);
					ToggleHints.select(i);
					ToggleHints.notifyListeners(SWT.Selection, new Event());
					break;
				case "delimiters":
					if(split.length > 1)
						SettingsDelimitersField.setText(split[1]);
						practice_set.setDelimiters(split[1]
								);
					break;
				case "vocab":
					File file = new File(split[1]);
					if(file.exists()) {
						List<Vocab> vocab_list = Util.loadFromCSV(split[1]);
						loadVocab(vocab_list);
					}
					else
						System.err.println("Couldn't load previous sessions vocab.");
					break;
					
				case "load_from":
					int idx = Integer.parseInt(split[1]);
					
					loaded_file_name = split[2];
					LoadOptionsCombo.select(idx);
					LoadOptionsCombo.notifyListeners(SWT.Selection, new Event());
					
					break;
					
				case "load_selection":
					idx = Integer.parseInt(split[1]);
					LoadSelectVocabCombo.select(idx);
					LoadSelectVocabCombo.notifyListeners(SWT.Selection, new Event());
					if(split.length > 2) {
						LoadCountField.setText(split[2]);
					}
					if(split.length > 3) {
						LoadFromField.setText(split[3]);
						LoadToField.setText(split[4]);
					}
					break;
					
				case "macro":
					boolean selected = Boolean.parseBoolean(split[3]);
					addMacroEntry(split[1], split[2], selected);
					break;
					
				default:
					System.err.println("Unrecognized config in session file:" + split[0]);
					break;
			}
		}
		
		br.close();
	}
	
	public void saveSession(String session_file_name) throws IOException
	{
		File session_file = new File(session_file_name);
		PrintWriter pw = new PrintWriter(session_file);
		
		pw.println("username,"+user.getUsername());
		pw.println("ink_canvas,"+ToggleInkCanvas.getSelection());
		pw.println("pen_size,"+PenFontCombo.getSelectionIndex());
		pw.println("ignore_case,"+ToggleCase.getSelection());
		pw.println("ignore_accents,"+ToggleAccents.getSelection());
		pw.println("ignore_special,"+ToggleSpecial.getSelection());
		pw.println("shuffle,"+ToggleShuffle.getSelection());
		pw.println("hints,"+ToggleHints.getSelectionIndex());
		String delimiters = practice_set.getDelimiters();
		pw.printf("delimiters,\"%s\"\n", delimiters == null ? "" : delimiters);
		pw.println(String.format("load_from,%s,%s",LoadOptionsCombo.getSelectionIndex(),loaded_file_name));
		pw.println(String.format("load_selection,%s,%s,%s,%s",LoadSelectVocabCombo.getSelectionIndex(), LoadCountField.getText(),
				LoadFromField.getText(), LoadToField.getText()));
		
		for(TextPair tp : macro_widgets) {
			pw.println(String.format("macro,%s,%s,%b", tp.getText1().getText(),tp.getText2().getText(), tp.getCheckBox().getSelection()));
		}

		if(vocab_file_name != null)
			pw.println("vocab,"+vocab_file_name);
		
		pw.close();
	}
	
	private void loadVocab(List<Vocab> vocab_list)
	{
		practice_set.setVocabList(vocab_list);
		practice_set.nextVocab();
	}
	
	private void refreshLearnTab()
	{
		Vocab current_vocab = practice_set.getVocab();
		if(current_vocab != null)
			VocabLabel.setText(practice_set.isLearnTarget() ? current_vocab.source : current_vocab.target);
		HintLabel.setText(practice_set.getHint());
		LearnLayer.layout(true);
	}
	
	private void refreshLoadTable(List<Vocab> vocab_list, Comparator<Vocab> comp, boolean reverse) {
		if(reverse) 
			vocab_list.sort(comp.reversed());
		else
			vocab_list.sort(comp);
		refreshLoadTable(vocab_list);
	}
	
	private void refreshLoadTable(List<Vocab> vocab_list)
	{
		LoadTable.removeAll();
		loaded_vocab_map = new LinkedHashMap<>(vocab_list.size() * 2);
		
		LocalDateTime time_threshold = LocalDateTime.now();
		
		for(int i = 0; i < vocab_list.size(); ++i) {
			Vocab vocab = vocab_list.get(i);
			
			TableItem item = new TableItem(LoadTable, SWT.NONE);
			
			loaded_vocab_map.put(item, vocab);
			
			if(load_swap_src_tgt)
			{
				item.setText(1, vocab.target);
				item.setText(2, vocab.source);
				item.setText(4, Integer.toString(vocab.stats_src.getAttempts()));
				
				double score = vocab.stats_src.getScore();
				item.setText(5, String.format("%.0f", score));
				item.setBackground(5, getColor(score));
			}else {
				item.setText(1, vocab.source);
				item.setText(2, vocab.target);
				item.setText(4, Integer.toString(vocab.stats_tgt.getAttempts()));
				
				double  score = vocab.stats_tgt.getScore();
				item.setText(5, String.format("%.0f", score));
				item.setBackground(5, getColor(score));
			}
			
			item.setText(0, Integer.toString(i + 1));
			
			DateTimeFormatter dateFormater = DateTimeFormatter.ISO_LOCAL_DATE;
			if(vocab.last_practiced != null && vocab.last_practiced.toLocalDate().equals(time_threshold.toLocalDate()))
				dateFormater = DateTimeFormatter.ISO_LOCAL_TIME;

			item.setText(3, vocab.last_practiced == null ? "N/A" : vocab.last_practiced.truncatedTo(ChronoUnit.SECONDS).format(dateFormater));
		}
	}
	
	private void refreshStats(List<Vocab> vocab_list, Comparator<Vocab> comp, boolean reverse) {
		if(reverse) 
			vocab_list.sort(comp.reversed());
		else
			vocab_list.sort(comp);
		refreshStats(vocab_list);
	}
	
	private void refreshStats(List<Vocab> vocab_list)
	{
		StatsTable.removeAll();
		stats_vocab_map = new LinkedHashMap<>(vocab_list.size() * 2);
		
		LocalDateTime time_threshold = LocalDateTime.now();
		
		for(int i = 0; i < vocab_list.size(); ++i) {
			Vocab vocab = vocab_list.get(i);
			
			TableItem item = new TableItem(StatsTable, SWT.NONE);
			
			stats_vocab_map.put(item, vocab);

			if(stats_swap_src_tgt)
			{
				item.setText(0, vocab.target);
				item.setText(1, vocab.source);
				item.setText(3, Integer.toString(vocab.stats_src.getAttempts()));
				
				double score = vocab.stats_src.getScore();
				item.setText(4, String.format("%.0f", score));
				item.setBackground(4, getColor(score));
			}else {
				item.setText(0, vocab.source);
				item.setText(1, vocab.target);
				item.setText(3, Integer.toString(vocab.stats_tgt.getAttempts()));
				
				double score = vocab.stats_tgt.getScore();
				item.setText(4, String.format("%.0f", score));
				item.setBackground(4, getColor(score));
			}
			
			DateTimeFormatter dateFormater = DateTimeFormatter.ISO_LOCAL_DATE;
			if(vocab.last_practiced != null && vocab.last_practiced.toLocalDate().equals(time_threshold.toLocalDate()))
				dateFormater = DateTimeFormatter.ISO_LOCAL_TIME;

			item.setText(2, vocab.last_practiced == null ? "N/A" : vocab.last_practiced.truncatedTo(ChronoUnit.SECONDS).format(dateFormater));
		}
	}
	
	public Color getColor(double score) {
		Color begin, end;
		double denom;
		
		if(score < Stats.MAX_SCORE*.25) {
			begin = DARK_RED;
			end = RED;
			denom = Stats.MAX_SCORE*.25;
		}else if(score < Stats.MAX_SCORE*.5) {
			begin = RED;
			end = ORANGE;
			denom = Stats.MAX_SCORE*.5;
		}else if(score < Stats.MAX_SCORE*.75) {
			begin = ORANGE;
			end = YELLOW;
			denom = Stats.MAX_SCORE*.75;
		}else {
			begin = YELLOW;
			end = GREEN;
			denom = Stats.MAX_SCORE;
		}
		
		double blending = score / denom;

		double inverse_blending = 1 - blending;

		int red = (int)Math.round(end.getRed() * blending + begin.getRed() * inverse_blending);
		int green = (int)Math.round(end.getGreen() * blending + begin.getGreen() * inverse_blending);
		int blue =  (int)Math.round(end.getBlue()  * blending + begin.getBlue()  * inverse_blending);

		//note that if i pass double values they have to be in the range of 0.0-1.0 
		//and not in 0-255 like the ones i get returned by the getters.
		Color blended = new Color (red, green, blue);
		
		return blended;
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		return bindingContext;
	}
}
