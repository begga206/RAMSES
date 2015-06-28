package ramses;

/**
 * Einfache Exceptionklasse für Syntax-Errors beim Einlesen
 * @author Lukas Becker
 * @author Andreas Paul
 *
 */
public class SyntaxErrorException extends Exception {
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
    public SyntaxErrorException(int instPtr, String errormsg){
    	this.instPtr = instPtr;
        this.errormsg = errormsg;
    }
   
    public SyntaxErrorException(String errormsg){
    	this(NO_LINE, errormsg);
    }
    
    /**
     * toString method
     * @return String mit einer spezifischen Error Nachricht
     */
    @Override
    public String toString(){
    	if(instPtr == NO_LINE)
    		return("SyntaxErrorException: " + errormsg);
    	else
    		return("SyntaxErrorException in Line "+ instPtr + ": " + errormsg);
    }
}
