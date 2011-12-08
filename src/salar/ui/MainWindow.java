package salar.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ProgressBar;


public class MainWindow {

	protected Shell shell;
	private Text txtDirectoryPath;
	private Table table;

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
		shell.setSize(450, 300);
		shell.setText("Salar Word Tagger (Version 2.0)");
		shell.setLayout(new GridLayout(1, false));
		
		txtDirectoryPath = new Text(shell, SWT.BORDER);
		txtDirectoryPath.setText("Data Directory");
		txtDirectoryPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnLoad = new Button(shell, SWT.NONE);
		btnLoad.setText("Load Data");

		Button btnProcess = new Button(shell, SWT.NONE);
		btnProcess.setText("Process Data");		
		
		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		ProgressBar progressBar = new ProgressBar(shell, SWT.NONE);
		progressBar.setSelection(30);

	}

}
