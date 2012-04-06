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

import salar.tagger.MaxentPOSTagger;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.List;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;


public class MainWindow {

	protected Shell shell;
	private Text txtDirectoryPath;
	private Text text;
	private Label lblDirectory;
	private Button btnProcess, btnBrowse, btnSave;
	private TabFolder tabFolder;
	private TabItem tbtmCopus;
	private Composite composite;
	private ProgressBar progressBar;
	private TabItem tbtmQuery;
	private Composite composite_1;
	private Label lblIds;
	private Text text_1;
	private Button btnNewButton;
	private Label lblNewLabel;
	private Label lblResults;
	private Table table;
	private TableColumn tblclmnId;

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
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		tabFolder = new TabFolder(shell, SWT.NONE);
		
		tbtmCopus = new TabItem(tabFolder, SWT.NONE);
		tbtmCopus.setText("Corpus");
		
		composite = new Composite(tabFolder, SWT.NONE);
		tbtmCopus.setControl(composite);
		composite.setLayout(new GridLayout(4, false));
		
		lblDirectory = new Label(composite, SWT.NONE);
		lblDirectory.setText("Corpus Directory:");
		
		txtDirectoryPath = new Text(composite, SWT.BORDER);
		txtDirectoryPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtDirectoryPath.setEditable(false);
						
						btnBrowse = new Button(composite, SWT.NONE);
						btnBrowse.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
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
						btnBrowse.setText("...");
						
								btnProcess = new Button(composite, SWT.NONE);
								btnProcess.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
								btnProcess.addMouseListener(new MouseAdapter() {
									@Override
									public void mouseUp(MouseEvent arg0) {
										try {
											ArrayList<String> posTags = new ArrayList<String>();
											String strSelectedDir = txtDirectoryPath.getText();
											posTags = MaxentPOSTagger.getInstance().extractTags(strSelectedDir);
											
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
								btnProcess.setText("Process");
										
										progressBar = new ProgressBar(composite, SWT.NONE);
										progressBar.setSelection(20);
										progressBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1));
										
												text = new Text(composite, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
												text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
												text.setEditable(false);
										new Label(composite, SWT.NONE);
										new Label(composite, SWT.NONE);
										new Label(composite, SWT.NONE);
										
												btnSave = new Button(composite, SWT.NONE);
												btnSave.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
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
												btnSave.setText("Save Output");
												
												tbtmQuery = new TabItem(tabFolder, SWT.NONE);
												tbtmQuery.setText("Query");
												
												composite_1 = new Composite(tabFolder, SWT.NONE);
												tbtmQuery.setControl(composite_1);
												composite_1.setLayout(new GridLayout(3, false));
												
												lblIds = new Label(composite_1, SWT.NONE);
												lblIds.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
												lblIds.setText("ID(s):");
												
												text_1 = new Text(composite_1, SWT.BORDER);
												text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
												
												btnNewButton = new Button(composite_1, SWT.NONE);
												btnNewButton.addSelectionListener(new SelectionAdapter() {
													@Override
													public void widgetSelected(SelectionEvent arg0) {
													}
												});
												btnNewButton.setText("Search");
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
												
												table = new Table(composite_1, SWT.BORDER | SWT.FULL_SELECTION);
												table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
												table.setHeaderVisible(true);
												table.setLinesVisible(true);
												
												tblclmnId = new TableColumn(table, SWT.CENTER);
												tblclmnId.setResizable(false);
												tblclmnId.setWidth(64);
												tblclmnId.setText("ID");
	}

}
