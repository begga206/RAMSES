package ramses;

public class Input {
	private int index;
	private int value;
	private boolean hasValue;
	
	
	public Input(int index){
		this.index = index;
		hasValue = false;
	}
	
	public Input(int index, int value){
		this.index = index;
		this.value = value;
		hasValue = true;
	}

	public int getIndex() {
		return index;
	}

	public int getValue() {
		return value;
	}

	public boolean hasValue() {
		return hasValue;
	}
}
