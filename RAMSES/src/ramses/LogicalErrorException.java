package ramses;

public class LogicalErrorException extends Exception{

	private static final long serialVersionUID = 3522647953788823900L;

	/** Befehlscounter */
	int instPtr;
	/** Error Nachricht*/
    String errormsg;
    
    /**
     * Constructor
     * @param errormsg
     */
    public LogicalErrorException(int instPtr, String errormsg){
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
