package salar.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

import salar.tagger.SimplePOSTagger;


public class MainWindow {

	protected Shell shell;
	private Text txtDirectoryPath;
	private Text text;
	private Label lblDirectory;
	private Button btnProcess, btnBrowse, btnSave;

	/**
	 * @wbp.parser.entryPoint
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

	protected void createContents() {
		shell = new Shell();
		shell.setSize(600, 400);
		shell.setText("Salar Word Tagger (Version 1.1)");
		shell.setLayout(new GridLayout(4, false));
		
		lblDirectory = new Label(shell, SWT.NONE);
		lblDirectory.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDirectory.setText("Corpus Directory:");
		
		txtDirectoryPath = new Text(shell, SWT.BORDER);
		txtDirectoryPath.setEditable(false);
		txtDirectoryPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,1 , 1));
						
		btnBrowse = new Button(shell, SWT.NONE);
		btnBrowse.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent arg0) {
				DirectoryDialog dialog = new DirectoryDialog (shell);
				String platform = SWT.getPlatform();
				dialog.setFilterPath (platform.equals("win32") || platform.equals("wpf") ? "c:\\" : "/");
				try{
					String strSelectedDir = dialog.open ();
					txtDirectoryPath.setText(strSelectedDir);
					text.setText("");
					btnProcess.setEnabled(true);
				} catch (Exception e) {
					text.setText(e.toString());
					btnProcess.setEnabled(false);
				}
			}
		});
		btnBrowse.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1));
		btnBrowse.setText("...");

		btnProcess = new Button(shell, SWT.NONE);
		btnProcess.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent arg0) {
				try {
					ArrayList<String> posTags = new ArrayList<String>();
					String strSelectedDir = txtDirectoryPath.getText();
					posTags = SimplePOSTagger.getInstance().extractTags(strSelectedDir);
					
					for (String str: posTags) {
						text.append(str + System.getProperty("line.separator"));
					}

					btnSave.setEnabled(true);
				} catch (Exception e) {
					text.setText(e.toString());
					btnSave.setEnabled(false);
				}
			}
		});
		btnProcess.setEnabled(false);
		btnProcess.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1));
		btnProcess.setText("Process");

		text = new Text(shell, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		text.setEditable(false);

		btnSave = new Button(shell, SWT.NONE);
		btnSave.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent arg0) {
				FileDialog dialog = new FileDialog(shell, SWT.SAVE);
				String platform = SWT.getPlatform();
				dialog.setFilterPath (platform.equals("win32") || platform.equals("wpf") ? "c:\\" : "/");
				String strSelectedFile = dialog.open();
				try {
					BufferedWriter bw = new BufferedWriter(new FileWriter(new File(strSelectedFile)));
					bw.write(text.getText());
					bw.close();
				} catch (Exception e) {
					text.setText(e.toString());
				}
			}
		});		
		btnSave.setEnabled(false);
		btnSave.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 4, 1));
		btnSave.setText("Save Output");
	}

}
