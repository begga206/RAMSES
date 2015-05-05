package ramses;

/**
 * Einfache Exceptionklasse für Syntax-Errors beim Einlesen
 * @author Lukas
 *
 */
public class SyntaxErrorException extends Exception {
	private static final long serialVersionUID = -95523412616969117L;
	/** Befehlscounter */
	int instPtr;
	/** Error Nachricht*/
    String errormsg;
    
    /**
     * Constructor
     * @param errormsg
     */
    public SyntaxErrorException(int instPtr, String errormsg){
    	this.instPtr = instPtr;
        this.errormsg = errormsg;
    }
    
    /**
     * toString method
     * @return String mit einer spezifischen Error Nachricht
     */
    @Override
    public String toString(){
        return("SyntaxErrorException in Line "+ instPtr + ": " + errormsg);
    }
}
