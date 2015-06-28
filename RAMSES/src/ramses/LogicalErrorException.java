package ramses;

/**
 * Einfache Exceptionklasse für logische Fehler innerhalb des RAM Kontext
 * @author Lukas Becker
 * @author Andreas Paul
 */
public class LogicalErrorException extends Exception{

	private static final long serialVersionUID = 1L;
	
	/** Wenn ein Fehler nicht im Zuge einer Instruktion auftrat*/
	public final static int NO_LINE = -3;
	/** Befehlscounter */
	private int instPtr;
	/** Error Nachricht*/
    private String errormsg;
    
    /**
     * Constructor
     * @param errormsg
     */
    public LogicalErrorException(int instPtr, String errormsg){
    	this.instPtr = instPtr;
        this.errormsg = errormsg;
    }
    
    public LogicalErrorException(String errormsg){
    	this(NO_LINE, errormsg);
    }
    
    /**
     * toString method
     * @return String mit einer spezifischen Error Nachricht
     */
    @Override
    public String toString(){
    	switch(instPtr){
    		case NO_LINE:
    			return("LogicalErrorException: " + errormsg);
    		default:
    			return("LogicalErrorException in Line "+ instPtr + ": " + errormsg);
    	}
    }
}
