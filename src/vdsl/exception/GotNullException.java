package vdsl.exception;

public class GotNullException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//Parameterless Constructor
    public GotNullException() {}

    //Constructor that accepts a message
    public GotNullException(String message)
    {
       super(message);
    }
}
