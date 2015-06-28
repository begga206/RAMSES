package ramses;

/**
 * Inputklasse entspricht einer Datenspeicherallokation im Datenspeicher der
 * RAM. Es ist möglich das einem allokierten Speicher sofort ein Wert zugewiesen
 * wird (Bsp.: "Input: s[0]=1" im geschriebenen RAM-Programm).
 * @author Lukas Becker
 * @author Andreas Paul
 *
 */
public class Input {
	//---------------------------Attribute-------------------------------------
	/** 
	 * Index des allokierten Speicherbereichs innerhalb des Datenspeichers 
	 * einer RAM.
	 */
	private int index;
	/** Zugewiesener Wert für den Speicherbereich */
	private int value;
	/** Gibt Information, ob Speicherbereich einen gültigen Wert besitzt */
	private boolean hasValue;
	
	//--------------------------Konstruktoren----------------------------------
	/**
	 * Konstruktor ohne mitgegebenen Wert bei der Allokation
	 * @param index
	 */
	public Input(int index){
		this.index = index;
		hasValue = false;
	}
	
	/**
	 * Konstruktor mit Wert bei der Allokation
	 * @param index
	 * @param value
	 */
	public Input(int index, int value){
		this.index = index;
		this.value = value;
		hasValue = true;
	}

	//----------------------------Getter und Setter----------------------------
	public int getIndex() {
		return index;
	}

	public void setValue(int value) {
		this.value = value;
		hasValue = true;
	}

	public int getValue() {
		return value;
	}

	public boolean hasValue() {
		return hasValue;
	}
}
