import java.io.*
import java.net.*
import java.util.*

//Klasa pobiera plik z danego adresu URL.
class Download extends Observable implements Runnable {
	//Maksymalny rozmiar bufora pobierania.
	private static final int MAX_BUFFER_SIZE = 1024;

	//Nazwy statusow. 
	public static final String STATUSES[] = {"Pobieranie", 
	"Pauza", "Ukonczony", "Anulowany", "Blad"};
	//Kody stanow.
	public static final int DOWNLOADING = 0; 
	public static final int PAUSED = 1; 
	public static final int COMPLETE = 2; 
	public static final int CANCELLED = 3; 
	public static final int ERROR = 4; 
	private URL url; // adres URL
	private int size; // rozmiar pliku w bajtach
	private int downloaded; // liczba pobranych juz bajtow
	private int status; // aktualny status

	// Konstruktor Download.
	public Download(URL url) {
		this.url = url; 
		size = -1; 
		downloaded = 0;
		status = DOWNLOADING;

		//Rozpoczecie pobierania.
		download();
	}
	
	//Pobranie adresu URL.
	public String getURL() {
		return url.toString();
	}

	//Pobranie rozmiaru. 
	public int getSize() {
		return size;
	}
	
	//Pobranie postepu.
	public float getProgress() {
		return ((float) downloaded / size) * 100; 
	}
	//Pobranie statusu.
	public int getStatus() {
		return status;
	} 
	//Wstrzymanie 
	public void pause() {
		status = PAUSED;
		stateChanged();
	}
	//Wznowienie 
	public void resume() {
		status = DOWNLOADING;
		stateChanged();
		download();
	}
	//Anulowanie
	public void cancel() {
		status = CANCELLED;
		stateChanged();
	}
	//Oznaczenie pobierania jako blednego.
	private void error() {
		status = ERROR;
		stateChanged();
	}
	//Rozpoczecie lub wznowienie pobierania. 
	private void download() {
		Thread thread = new Thread(this);
		thread.start();
	}
	//Pobranie nazwy pliku z adresem URL.
	private String getFileName(URL url) {
		String fileName = url.getFile();
		return fileName.substring(fileName.lastIndexOf('/') +1);
	}
	//Pobieranie pliku.
	public void run() {
		RandomAccesFile file = null; 
		InputStream stream = null; 
		
		try {
			//Otwarcie polocznia URL.
			HttpURLConnection connection = 
				(HttpURLConnection) url.openConnection();

			//Okreslenie czesci pliku do pobierania.
			connection.setRequestProperty("Range",
				"bytes=" + downloaded + "-");
			//Poloczenie z serwerem. 
			connection.connect();
			//Upewnienie sie, iz kod odpowiedzi znajduje sie w zakresie 200
			if (connection.getResponseCode() / 100 != 2) {
				error();
		}
			//Sprawdzenie poprawnosci dlugosci.
			int contentLength = connection.getContentLength();
			if (contentLength < 1) {
				error();
			}
/*Ustawienie rozmiaru pobieranego pliku, jesli jeszcze tego nie zrobiono*/
			if (size == -1) {
				size = contentLength;
				stateChanged();
			}
			//Otwarcie pliku i przejscie na jego koniec.
			file = new RandomAccessFile(getFileName(url), "rw");
			file.seek(downloaded);

			stream = connection.getInputStream();
			while (status == DOWNLOADING) {
/*Rozmiar bufora w zaleznosci od tego ile jeszcze zostalo do pobrania.*/
			byte buffer[];
			if (size - downloaded > MAX_BUFFER_SIZE) {
				buffer = new byte[MAX_BUFFER_SIZE];
			} else {
				buffer = new byte[size - downloaded];
			}
			//Odczyt z serwera do bufora. 
			int read = stream.read(buffer);
			if (read == -1)
				break;
			//Zapis bufora do pliku.
			file.write(buffer, 0, read);
			downloaded += read;
			stateChanged();
			}
			
/*Zmiana statusu na ukonczony, gdy pobrano caly plik.*/
		if (status == DOWNLOADING) {
			status = COMPLETE;
			stateChanged();
		}					
	} catch (Exception e) {
		error();
	} finally {
	//Zamkniecie pliku.
	if (file != null) {
		try {
			file.close();
		} catch (Exception e) {}
	}
	//Zamkniecie poloczenia z serwerem.
	if (stream != null) {
		try {
			stram.close();
		} catch (Exception e) {}
		}
	}
}
	//Poinformowanie o zmianie statusu pobierania.
	private void stateChanged() {
		setChanged();
		notifyObservers();
	}
}
