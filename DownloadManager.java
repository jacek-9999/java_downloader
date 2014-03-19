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
	URL verifiedUrl = veryfyUrl(addTextField.getText());
	if (verifiedUrl != null) {
	tableModel.addDownload(new Download(verifiedUrl));
	addTextField.setText(""); //zresetowanie pola dodawania
	} else {
	JOptionPane.showMessageDialog(this,
			"Bledny adres URL", "Blad", 
			JOptionPane.ERROR_MESSAGE); 
	}
}

//Weryfilacja adresu URL. 
private URL verifyUrl(String url) {
	//Tylko adresy HTTP.
	if (!url.toLoweCase().startWith("http://"))
		retutn null;
	//Weryfikacja formatu URL.
	URL verifiedUrl = null; 
	try {
		verifiedUrl = new URL(url);
	}	catch (exception e) {
		return null; 

		return verifiedUrl; 
	}
	//Upewnienie sie ze adres URL wskazuje na plik. 
	if (verifiedUrl.getFile().length() < 2)
		return null;
	return verifiedUrl;
}
//Wywolywane przy zmianie zaznaczonego wiersza. 
private void tableSelectionChanged() {
	/*Usuniecie opcji powiadamiania z ostatnio zaznaczonego wiersza*/
	if (selectedDownload != null)
		selectedDownload.deleteObserwer(DownloadManager.this);

	/*Jesli nie jest to usuwanie wiersza,
	 ustaw otrzymywanie powiadomien z tego wiersza*/
	if (!clearing && table.getSelectedRow() > -1) {
		selectedDownload = 
			tableModel.getDownload(table.getSelectedRow());
		selectedDownload.addObserver(DownloadManager.this);
		updateButtons();
	}
}

//Wstrzymanie wybranego pliku. 
private void actionPause() {
	selectedDownload.pause();
	updateButtons();
}

//Wznowienie pobierania pliku. 
private void actionResume() {
	selectedDownload.resume();
	updateButtons();
}
//Anulowanie pobierania.
private void actionCancel() {
	selectedDownload.cancel();
	updateButtons();
}
//Usuniecie pobierania. 
private void actionClear() {
	clearing = true; 
	tableModel.clearDownload(table.getSelectedRow());
	clearing = false; 
	selectedDownload = null; 
	updateButtons(); 
}
/*Aktualizacja stanu przyciskow w zaleznosci od stanu zaznaczonego pobierania*/
private void updateButtons() {
if (selectedDownload != null) {
	int status = selectedDownload.getStatus(); 
	switch (status) {
		case Download.DOWNLOADING:
		pauseButton.setEnabled(true);
		resumeButton.setEnabled(false); 
		cancelButton.setEnabled(true);
		clearButton.setEnabled(false); 
		break; 
		case Download.PAUSED:
		pauseButton.setEnabled(false);
		resumeButton.setEnabled(true); 
		cancelButton.setEnabled(true);
		clearButton.setEnabled(false); 
		break; 
		case Download.ERROR:
		pauseButton.setEnabled(false);
		resumeButton.setEnabled(true); 
		cancelButton.setEnabled(false);
		clearButton.setEnabled(true); 
		break; 
		default: //CALY lub ANULOWANY
		pauseButton.setEnabled(false);
		resumeButton.setEnabled(false); 
		cancelButton.setEnabled(false);
		clearButton.setEnabled(true); 
	}
} else {
	//Nie jest zaznaczone zadne pobieranie. 
		pauseButton.setEnabled(false);
		resumeButton.setEnabled(false); 
		cancelButton.setEnabled(false);
		clearButton.setEnabled(false); 
	}
}
/*Aktualizacja po otrzymaniu informacji o dowolnych zmianach w klasie Download*/

public void update(Observable o, Object arg) {
//Aktualizacja przyciskow w przypadku zmiany zaznaczonego pobierania. 
	if (selectedDownload != null && selectedDownload.equals(0))
		SwingUtilities.invoke(new Runnable(){
		public void run() {
			updateButtons();
		}
		});
}
//Uruchomienie programu. 
public static void main(Stringp[] args) {
	SwingUtilities.invokeLater(new Runnable(){
		public void run() {
			DownloadManager manager = new DownloadManager();
			manager.setVisible(true);
	}
	});
}






