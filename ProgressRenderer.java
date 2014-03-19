import java.awt.*;
import java.swing.*;
import java.swing.table.*;

//Ta klasa renderuje JProgressBar w komorce tablicy.
class ProgressRenderer extends JProgressBar
	implements TableCelRenderer
{
	//Konstruktor ProgressRenderer
	public ProgressRenderer(int min, int max) {
		super(min, max);
	}

	/*Zwraca ten JProgressBar jako renderer dla danej komorki tabeli.*/
	public Component getTableCellRendererComponent (
			JTable table, Object value, boolean isSelected, 
			boolean hasFocus, int row, int column)
	{
	//Ustawianie procentowej wartosci dla JProgressBar
	setValue((int)((Float)value).floatValue());
	return this;
	}
}
