package salar.ui;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

import salar.tagger.SimplePOSTagger;


public class MainWindow {

	protected Shell shell;
	private Text txtDirectoryPath;
	private Text text;
	private Label lblDirectory;

	public static void main(String[] args) {
		try {
			MainWindow window = new MainWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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

	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 270);
		shell.setText("Salar Word Tagger (Version 2.0)");
		shell.setLayout(new GridLayout(3, false));
		
		lblDirectory = new Label(shell, SWT.NONE);
		lblDirectory.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDirectory.setText("Directory:");
		
		txtDirectoryPath = new Text(shell, SWT.BORDER);
		txtDirectoryPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		text = new Text(shell, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		gd_text.heightHint = 170;
		text.setLayoutData(gd_text);
		
		Button btnLoad = new Button(shell, SWT.NONE);
		btnLoad.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent arg0) {
				DirectoryDialog dialog = new DirectoryDialog (shell);
				String platform = SWT.getPlatform();
				dialog.setFilterPath (platform.equals("win32") || platform.equals("wpf") ? "c:\\" : "/");
				String strSelectedDir = dialog.open ();
				ArrayList<String> posTags;
				try {
					text.setText("");
					posTags = SimplePOSTagger.getInstance().extractTags(strSelectedDir);
					for (String str: posTags) {
						text.append(str + System.getProperty("line.separator"));
					}
				} catch (Exception e) {
					text.setText(e.toString());
				}
			}
		});
		btnLoad.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		btnLoad.setText("Load Data");
				new Label(shell, SWT.NONE);
				new Label(shell, SWT.NONE);
		
				Button btnProcess = new Button(shell, SWT.NONE);
				btnProcess.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
				btnProcess.setText("Process Data");
	}

}
