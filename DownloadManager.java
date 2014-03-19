import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.swing.*;
import java.swing.event.*;

// Menedzer pobierania. 
public class DownloadManager extends JFrame
	implements Observer
{
	//Dodanie pola tekstowego pobierania.
	private JTextField addTextField;

	//Model danych tabeli pobierania.
	private DownloadsTableModel tableModel;

	//Tabela z pobraniami. 
	private JTable table;

	//Przyciski zarzadzania poszczegolnymi pobraniami. 
	private JButton pauseButton, resumeButton;
	private JButton cancelButton, clearButton;

	//Aktualnie zaznaczone pobieranie. 
	private Download selectedDownload;

	//Informacja o tym, czy usunieto zaznaczenie z elementu tabeli.
	private boolean clearing;

	//Konstruktor DownloadManager.
	public DownloadManager()
	{
		//Tytul aplikacji
		setTitle("Menedzer pobierania");
		//Ustaw rozmiar okna.
		setSize(640, 480);
		//Obsluga zdarzenia zamkniecia okna.
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				actionExit();
			}
		});
		
		//Ustawianie menu.
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("Plik");
		fileMenu.setMnemonic(SetEvent.VK_P);
		JMenuItem fileExitMenuItem = new JMenuItem("Wyjscie",
			KeyEvent.VK_W);
		fileExitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				actionExit();
			}
		});
		fileMenu.add(fileExitMenuItem);
		menuBar.add(fileMenu);
		setJMenuBar.add(fileMenu);
		
		//Wykonanie panelu pobierania. 
		JPanel addPanel = new JPanel();
		addTextField = new JTextField(30);
		addPanel.add(addTextField);
		JButton addButton = new JButton("Dodaj adres");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionAdd();
			}
		});
		addPanel.add(addButton);

		//Tabela pobran.
		tableModel = new DownloadsTableModel();
		table = new JTable(tableModel);
		table.getSelectionModel().addListSelectionListener(new
				ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) { 
	tableSelectionChanged();
				}
	});
	//Mozliwosc zaznaczenia w danym momencie tylko jednego wiersza.
	table.setSelectionModel(ListSelectionModel.SINGLE_SELECTION);

	//Ustawienie ProgressBar jako renderera kolumny postepu.
	ProgressRenderer renderer = new ProgressRenderer(0, 100);
	renderer.setStringPainted(true); //pokazanie tekstu postepu
	table.setDefaultRenderer(JProgressBar.class, renderer);

	//Ustawienie takiej wysokosci wiersza aby zmiescic JProgressBar.
	table.setRowHeight (
			(int) renderer.getPrefferedSize().getHeight());

	//Ustawienie panelu Pliki.
	JPanel downloadsPanel = JPanel(); 
	downloadsPanel.setBorder(
			BorderFactory.createTitleBorder("Pliki"));
			downloadsPanel.setLayout(new BorderLayout());
			downloadsPanel.add(new JScrollPane(table)),
				BorderLayout.CENTER);
	//Ustawienie panelu przyciskow.
	JPanel buttonsPanel = new JPanel();
	pauseButton = new JButton("Zatrzymaj.");
	pauseButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			actionPause();
		}
		)};
	pauseButton.setEnabled(false);
	buttonsPanel.add(pauseButton);
	resumeButton = new JButton("Wznow");
	resumeButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			actionResume();
		}
		});
	resumeButton.setEnabled(false);
	buttonPanel.add(resume.Button);
	cancelButton = new JButton("Anuluj");
	cancelButton.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			actionCancel();
	}
	});
	cancelButton.setEnabled(false);
	buttonsPanel.add(cancelButton);
	clearButton = new JButton("Wyczysc");
	clearButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			actionClear();
		}
		});
	clearButton.setEnabled(false);
	buttonsPanel.add(clearButton);

	//Dodanie paneli do wyswietlania.
	getContentPane().setLayout(new BorderLayout());
	getContentPane().add(addPanel, BorderLayout.NORTH);
	getContentPane().add(downloadsPanel, BorderLayout.CENTER);
	getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
}

//Wyjscie z programu.
private void actionExit() {
	System.exit(0);
}

//Dodanie nowego pliku.
private void actionAdd() {
	
}












