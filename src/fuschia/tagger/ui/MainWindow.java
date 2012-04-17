package fuschia.tagger.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.List;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import fuschia.tagger.MaxentPOSTagger;
import fuschia.tagger.Document;

import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;

public class MainWindow {

	protected Shell shell;
	private Text txtDirectoryPath;
	private Text text;
	private Label lblDirectory;
	private Button btnProcess, btnBrowse;
	private TabFolder tabFolder;
	private TabItem tbtmCopus;
	private Composite composite;
	private ProgressBar progressBar;
	private TabItem tbtmQuery;
	private Composite composite_1;
	private Label lblIds;
	private Text txtQuery;
	private Button btnSearch;
	private Label lblNewLabel;
	private Label lblResults;
	private StyledText styledText;
	TextStyle normalStyle = new TextStyle();
	TextStyle tagStyle = new TextStyle();
	private Map<String, Document> taggerResults;
	
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

	public void showDocument(Document doc) {

		int size = -1;
		
		if (doc != null )
		{
			size = doc.size();
		}
				
		StyleRange styleRange = null;

		if (size == -1) {

			styledText.setText("Error!");

			styleRange = new StyleRange();
			styleRange.start = 0;
			styleRange.length = styledText.getText().length();
			styleRange.foreground = SWTResourceManager.getColor(255, 0, 0);
			styleRange.fontStyle = SWT.BOLD | SWT.ITALIC;
			styledText.setStyleRange(styleRange);
			
			return;
		}
		
		styledText.setText("");
		for (int i = 0 ; i < size ; i++) {

			// TOKEN
			styledText.append(doc.tokens[i] + " ");
			styleRange = new StyleRange();
			styleRange.start = styledText.getText().length() - doc.tokens[i].length() - 1;
			styleRange.length = doc.tokens[i].length();
			styleRange.foreground = SWTResourceManager.getColor(0, 0, 0);
			styleRange.fontStyle = SWT.NORMAL;
			styledText.setStyleRange(styleRange);
			
			// TAG
			styledText.append(doc.tags[i] + " ");
			styleRange = new StyleRange();
			styleRange.start = styledText.getText().length() - doc.tags[i].length() - 1;
			styleRange.length = doc.tags[i].length();
			styleRange.foreground = SWTResourceManager.getColor(255, 0, 0);
			styleRange.fontStyle = SWT.ITALIC | SWT.BOLD;
			styledText.setStyleRange(styleRange);

		}

		styledText.append(System.getProperty("line.separator"));
		styledText.append("-------------------");
		styledText.append(System.getProperty("line.separator"));
		
		// TAG
		for (Iterator<String> i = doc.cumulativeTags.keySet().iterator(); i.hasNext();) {
			String key = i.next();
			Integer value = doc.cumulativeTags.get(key);

			String str = key + " = " + value + System.getProperty("line.separator");
			styledText.append(str);
			styleRange = new StyleRange();
			styleRange.start = styledText.getText().length() - str.length() - 1;
			styleRange.length = str.length();
			styleRange.foreground = SWTResourceManager.getColor(0, 200, 0);
			styleRange.fontStyle = SWT.ITALIC | SWT.BOLD;
			styledText.setStyleRange(styleRange);			

		}

		
	}
	
	protected void createContents() {
		shell = new Shell();
		shell.setSize(600, 400);
		shell.setText("Salar Word Tagger (Version 1.1)");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));

		tabFolder = new TabFolder(shell, SWT.NONE);

		tbtmCopus = new TabItem(tabFolder, SWT.NONE);
		tbtmCopus.setText("Corpus");

		composite = new Composite(tabFolder, SWT.NONE);
		tbtmCopus.setControl(composite);
		composite.setLayout(new GridLayout(4, false));

		lblDirectory = new Label(composite, SWT.NONE);
		lblDirectory.setText("Corpus Directory");

		txtDirectoryPath = new Text(composite, SWT.BORDER);
		txtDirectoryPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtDirectoryPath.setEditable(false);

		btnBrowse = new Button(composite, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
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
		btnBrowse.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnBrowse.setText("...");

		btnProcess = new Button(composite, SWT.NONE);
		btnProcess.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				try {
					String strSelectedDir = txtDirectoryPath.getText();
					taggerResults = MaxentPOSTagger.INSTANCE.extractTags(strSelectedDir);
					text.append(strSelectedDir + System.getProperty("line.separator"));
					text.append("---------"  + System.getProperty("line.separator"));
					text.append(taggerResults.size()  + System.getProperty("line.separator"));


					btnSearch.setEnabled(true);
				} catch (Exception e) {
					text.setText(e.toString());
					btnSearch.setEnabled(false);
				}
			}
		});
		btnProcess.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnProcess.setEnabled(false);
		btnProcess.setText("Process");

		progressBar = new ProgressBar(composite, SWT.NONE);
		progressBar.setSelection(20);
		progressBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1));

		text = new Text(composite, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		text.setEditable(false);

		tbtmQuery = new TabItem(tabFolder, SWT.NONE);
		tbtmQuery.setText("Query");

		composite_1 = new Composite(tabFolder, SWT.NONE);
		tbtmQuery.setControl(composite_1);
		composite_1.setLayout(new GridLayout(3, false));

		lblIds = new Label(composite_1, SWT.NONE);
		lblIds.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblIds.setText("ID:");

		txtQuery = new Text(composite_1, SWT.BORDER);
		txtQuery.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		btnSearch = new Button(composite_1, SWT.NONE);
		btnSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				String key = txtQuery.getText().trim();
				if (key.length() > 0) {
					Document doc = taggerResults.get(key);
					showDocument(doc);
				} else {
					showDocument(null);
				}
			}
		});
		btnSearch.setText("Search");
		new Label(composite_1, SWT.NONE);

		lblNewLabel = new Label(composite_1, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblNewLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		lblNewLabel.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.ITALIC));
		lblNewLabel.setText("Examples: 1, 10-12");
		new Label(composite_1, SWT.NONE);

		lblResults = new Label(composite_1, SWT.NONE);
		lblResults.setText("Results:");
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		
		styledText = new StyledText(composite_1, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP);
		styledText.setIndent(3);
		styledText.setLeftMargin(10);
		styledText.setEditable(false);
		styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		
		/** TEST */
		// String[] toks = {"token1","tokenTwo", "TokenThree"};
		// String[] tgs = {"JJ","SS","QQ"};
		// Document doc = new Document("test", toks, tgs);
		// showDocument(doc);
	}

}
