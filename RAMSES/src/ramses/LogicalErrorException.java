package ramses;

public class LogicalErrorException extends Exception{

	private static final long serialVersionUID = 1L;

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
    
    public LogicalErrorException(String errormsg){
    	this(-3, errormsg);
    }
    
    /**
     * toString method
     * @return String mit einer spezifischen Error Nachricht
     */
    @Override
    public String toString(){
    	switch(instPtr){
    		case -3:
    			return("LogicalErrorException: " + errormsg);
    		default:
    			return("LogicalErrorException in Line "+ instPtr + ": " + errormsg);
    	}
    }
}
