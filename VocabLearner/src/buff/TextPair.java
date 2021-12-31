package buff;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.DragDetectEvent;

public class TextPair extends Composite {
	private Composite composite;
	private Button btnCheckButton;
	private Text text1;
	private Text text2;
	private Label lblDelete;
	private boolean mouseOver;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public TextPair(Composite parent, int style) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(1, false));
		
		composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		btnCheckButton = new Button(composite, SWT.CHECK);
		btnCheckButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		
		text1 = new Text(composite, SWT.BORDER);
		text1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		
		text2 = new Text(composite, SWT.BORDER);
		text2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		
		lblDelete = new Label(composite, SWT.NONE);
		lblDelete.addDragDetectListener(new DragDetectListener() {
			public void dragDetected(DragDetectEvent arg0) {
				mouseOver = false;
			}
		});
		lblDelete.setText("[X]");
		
		lblDelete.addMouseTrackListener(new MouseTrackListener() {

			@Override
			public void mouseEnter(MouseEvent arg0) {
				// TODO Auto-generated method stub
				mouseOver = true;
			}

			@Override
			public void mouseExit(MouseEvent arg0) {
				// TODO Auto-generated method stub
				mouseOver = false;
			}

			@Override
			public void mouseHover(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	public Text getText1() {
		return text1;
	}
	
	public Text getText2() {
		return text2;
	}
	
	public Composite getComposite()
	{
		return composite;
	}
	
	public Label getDeleteLabel() {
		return lblDelete;
	}
	
	public boolean isMouseOver() {
		return mouseOver;
	}
	
	public Button getCheckBox() {
		return btnCheckButton;
	}
}
