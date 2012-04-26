package fuschia.tagger.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Savepoint;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

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

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.wb.swt.SWTResourceManager;

import fuschia.tagger.common.Document;
import fuschia.tagger.common.DocumentRepository;
import fuschia.tagger.TaggerThread;

import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.swtchart.Chart;
import org.swtchart.IBarSeries;
import org.swtchart.ISeries.SeriesType;

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
	public Button btnSearch;
	private Label lblNewLabel;
	private Label lblResults;
	private StyledText styledText;
	TextStyle normalStyle = new TextStyle();
	TextStyle tagStyle = new TextStyle();
	private static MainWindow _instance = null;
	private Composite chartPlaceholder;
	private Chart chart;
	private TaggerThread taggerThread;
	public Button btnSave;

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

	public static MainWindow getInstance() {
		if (_instance == null)
			_instance = new MainWindow();
		return _instance;
	}

	public void setProgress(int percentage) {
		this.progressBar.setSelection(percentage);
	}

	public void log(String strLine) {
		text.append(strLine + System.getProperty("line.separator"));
	}

	public void showDocument(Document doc) {

		int size = -1;

		if (doc != null) {
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
		for (int i = 0; i < size; i++) {

			// TOKEN
			styledText.append(doc.tokens[i] + " ");
			styleRange = new StyleRange();
			styleRange.start = styledText.getText().length()
					- doc.tokens[i].length() - 1;
			styleRange.length = doc.tokens[i].length();
			styleRange.foreground = SWTResourceManager.getColor(0, 0, 0);
			styleRange.fontStyle = SWT.NORMAL;
			styledText.setStyleRange(styleRange);

			// TAG
			styledText.append(doc.tags[i] + " ");
			styleRange = new StyleRange();
			styleRange.start = styledText.getText().length()
					- doc.tags[i].length() - 1;
			styleRange.length = doc.tags[i].length();
			styleRange.foreground = SWTResourceManager.getColor(255, 0, 0);
			styleRange.fontStyle = SWT.ITALIC | SWT.BOLD;
			styledText.setStyleRange(styleRange);

		}

		styledText.append(System.getProperty("line.separator"));
		styledText.append("-------------------");
		styledText.append(System.getProperty("line.separator"));

		final double[] barChartValues = new double[doc.cumulativeTags.size()];
		final String[] tagCategories = new String[doc.cumulativeTags.size()];
		// TAG
		int index = 0;
		for (Iterator<String> i = doc.cumulativeTags.keySet().iterator(); i
				.hasNext();) {
			String key = i.next();
			Integer value = doc.cumulativeTags.get(key);

			tagCategories[index] = key;
			barChartValues[index++] = (double) value;
			String str = key + " = " + value
					+ System.getProperty("line.separator");
			styledText.append(str);
			styleRange = new StyleRange();
			styleRange.start = styledText.getText().length() - str.length() - 1;
			styleRange.length = str.length();
			styleRange.foreground = SWTResourceManager.getColor(0, 200, 0);
			styleRange.fontStyle = SWT.ITALIC | SWT.BOLD;
			styledText.setStyleRange(styleRange);

		}

		// create a chart

		if (chart != null)
			chart.dispose();
		chart = new Chart(chartPlaceholder, SWT.NONE);
		chart.getLegend().setVisible(false);

		// set titles
		chart.getTitle().setText("Cumulative Tag Chart");
		chart.getAxisSet().getXAxis(0).getTitle().setText("Tag");
		chart.getAxisSet().getYAxis(0).getTitle().setText("Count");

		chart.getAxisSet().getXAxis(0).setCategorySeries(tagCategories);
		chart.getAxisSet().getXAxis(0).enableCategory(true);
		// create bar series
		IBarSeries barSeries = (IBarSeries) chart.getSeriesSet().createSeries(
				SeriesType.BAR, "Count");
		barSeries.setYSeries(barChartValues);

		barSeries.setBarColor(SWTResourceManager.getColor(80, 200, 100));

		// adjust the axis range
		chart.getAxisSet().adjustRange();

	}

	protected void createContents() {
		shell = new Shell();
		shell.setSize(600, 552);
		shell.setText("Salar Word Tagger (Version 1.1)");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));

		tabFolder = new TabFolder(shell, SWT.NONE);

		tbtmCopus = new TabItem(tabFolder, SWT.NONE);
		tbtmCopus.setText("Corpus");

		composite = new Composite(tabFolder, SWT.NONE);
		tbtmCopus.setControl(composite);
		composite.setLayout(new GridLayout(5, false));

		lblDirectory = new Label(composite, SWT.NONE);
		lblDirectory.setText("Corpus Directory");

		txtDirectoryPath = new Text(composite, SWT.BORDER);
		txtDirectoryPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		txtDirectoryPath.setEditable(false);

		btnBrowse = new Button(composite, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				DirectoryDialog dialog = new DirectoryDialog(shell);
				String platform = SWT.getPlatform();
				dialog.setFilterPath(platform.equals("win32")
						|| platform.equals("wpf") ? "c:\\" : "/");
				try {
					String strSelectedDir = dialog.open();
					txtDirectoryPath.setText(strSelectedDir);
					text.setText("");
					btnProcess.setEnabled(true);
				} catch (Exception e) {
					text.setText(e.toString());
					btnProcess.setEnabled(false);
				}
			}
		});
		btnBrowse.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false,
				false, 1, 1));
		btnBrowse.setText("...");

		btnProcess = new Button(composite, SWT.NONE);
		btnProcess.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				taggerThread = new TaggerThread(txtDirectoryPath.getText()
						.trim());
				taggerThread.start();
			}
		});
		btnProcess.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnProcess.setEnabled(false);
		btnProcess.setText("Process");

		btnSave = new Button(composite, SWT.NONE);
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				try {
					if (taggerThread != null && taggerThread.results != null) {
						FileDialog dialog = new FileDialog(shell, SWT.SAVE);
						String platform = SWT.getPlatform();
						dialog.setFilterPath(platform.equals("win32") || platform.equals("wpf") ? "c:\\" : "/");
						String strSelectedFile = dialog.open();
						txtDirectoryPath.setText(strSelectedFile);
						text.append("Saving generated map to " + strSelectedFile + " ...");
						taggerThread.results.saveToFile(strSelectedFile);

					}
				} catch (Exception e) {
					e.printStackTrace();
					MainWindow.getInstance().log(e.toString());
					MainWindow.getInstance().btnSave.setEnabled(false);
				}
			}
		});
		btnSave.setEnabled(false);
		btnSave.setText("Save");

		progressBar = new ProgressBar(composite, SWT.NONE);
		progressBar.setSelection(20);
		progressBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				5, 1));

		text = new Text(composite, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		text.setFont(SWTResourceManager.getFont("Monaco", 11, SWT.NORMAL));
		GridData gd_text = new GridData(SWT.FILL, SWT.FILL, true, true, 5, 2);
		gd_text.heightHint = 415;
		gd_text.widthHint = 551;
		text.setLayoutData(gd_text);
		text.setEditable(false);

		tbtmQuery = new TabItem(tabFolder, SWT.NONE);
		tbtmQuery.setText("Query");

		composite_1 = new Composite(tabFolder, SWT.NONE);
		tbtmQuery.setControl(composite_1);
		composite_1.setLayout(new GridLayout(3, false));

		lblIds = new Label(composite_1, SWT.NONE);
		lblIds.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblIds.setText("ID:");

		txtQuery = new Text(composite_1, SWT.BORDER);
		txtQuery.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		btnSearch = new Button(composite_1, SWT.NONE);
		btnSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				try {
					String strQuery = txtQuery.getText().trim();
					if (taggerThread != null && taggerThread.results != null
							&& strQuery != null && strQuery.length() > 0) {
						Document doc = taggerThread.results.getDocument(strQuery);
						MainWindow.getInstance().showDocument(doc);
					} else {
						MainWindow.getInstance().showDocument(null);
					}
				} catch (Exception e) {
					e.printStackTrace();
					MainWindow.getInstance().log(e.toString());
					MainWindow.getInstance().btnSearch.setEnabled(false);
				}
			}
		});
		btnSearch.setText("Search");
		new Label(composite_1, SWT.NONE);

		lblNewLabel = new Label(composite_1, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false,
				1, 1));
		lblNewLabel.setForeground(SWTResourceManager
				.getColor(SWT.COLOR_DARK_GRAY));
		lblNewLabel.setFont(SWTResourceManager.getFont("Lucida Grande", 10,
				SWT.ITALIC));
		lblNewLabel.setText("Examples: 1, 10-12");
		new Label(composite_1, SWT.NONE);

		chartPlaceholder = new Composite(composite_1, SWT.NONE);
		chartPlaceholder.setLayout(new FillLayout(SWT.HORIZONTAL));
		GridData gd_chartPlaceholder = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 3, 1);
		gd_chartPlaceholder.heightHint = 200;
		chartPlaceholder.setLayoutData(gd_chartPlaceholder);

		lblResults = new Label(composite_1, SWT.NONE);
		lblResults.setText("Results:");
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);

		styledText = new StyledText(composite_1, SWT.BORDER | SWT.READ_ONLY
				| SWT.WRAP);
		styledText.setIndent(3);
		styledText.setLeftMargin(10);
		styledText.setEditable(false);
		styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				3, 1));

		/** TEST */
		// String[] toks = {"token1","tokenTwo", "TokenThree"};
		// String[] tgs = {"JJ","SS","QQ"};
		// Document doc = new Document("test", toks, tgs);
		// showDocument(doc);
	}

}
