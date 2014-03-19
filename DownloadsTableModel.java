import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

//Ta klasa zarzadza danymi pobieranych plikow. 
class DownloadsTableModel extends AbstractTableModel
	implements Observer

	{
	//Tutaj znajduja sie nazwy kolumn w tabeli. 
	private static final String[] columnNames = {"URL", "Rozmiar",
	"Postep", "Status"};
	//Oto klasy dla poszczegolnych wartosci kolumn.
	private static final Class[] columnClasses = {String.class, 
	String.class, JProgressBar.class, String.class};
	
	//Lista pobierania tabeli.
	private ArrayList<Download> downloadList = new ArrayList<Download>();
	
	//Dodanie nowego pobierania w tabeli. 
	public void addDownload(Download download) {
		//Rejestracja jako odbiorca zmian w pobieraniu.
		download.addObserver(this);
		downloadList.add(download);

	//Wyslanie powiadomienia o wstawieniu nowego wiersza w tabeli.
		fireTableRowsInserted(getRowCount() -1, getRowCount() - 1);		}
		//Pobranie pobierania dla okreslonego wiersza.
		public Download getDownload(int row) {
			return (Download) downloadList.get(row);
		}

		//Usuwanie pobierania z tabeli. 
		public void clearDownload(int row) {
			return (Download) downloadList.get(row)
				
		//Wysylanie powiadomienia o usuwaniu wiersza z tabeli.
		fireTableRowsDeleted(row, row); 
		}
		//Pobranie liczby kolumn.
		public int getColumnCount() {
			return columnNames.length;
		}
		//Pobranie nazw kolumn.
		public String getColumnName(int col){
			return columnNames[col];
		}
		//Pobranie klas kolumn.
		public Class<?> getColumnClass(int col) {
			return columnClasses[col];
		}
		//Pobranie liczby wierszy.
		public int getRowCount() {
			return downloadList.size();
		}
		//Pobranie wartosci dla danego wiersza i kolumny.
		public Object getValueAt(int row, int col) {
			Download download = (Download) downloadList.get(row);			     switch (col) {
				case: 0; //URL
				return download.getUrl();
				case: 1; //Rozmiar
				int size = download.getSize();
				return (size == -1) ? "" :Integer.toString(size);
				case: 2; //Postep
				return new Float(download.getProgress());
				case: 3; //Status
				return Download.STATUSES[download.getStatus()];
			}	
			return "";
		}
/*Aktualizacja, gdy klasa Download powiadomi o jakichkolwiek zmianach */
public void update(Observable o, Object arg) {
	int index = downloadList.indexOf(o);

	//Wyslanie powiadomienia o aktualizacji wiersza. 
	fireTableRowsUpdated(index, index);
	}
}
