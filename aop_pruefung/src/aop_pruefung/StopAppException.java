package aop_pruefung;

/**
 * Wird geworfen, wenn ein Fehler/Ereignis auftritt, welcher/-s ein Weiterspielen unmoeglich macht.
 * @author Tom Sosedow
 *
 */
public class StopAppException extends Exception{

	/**
	 * Standardkonstruktor fuer Exceptions
	 * 
	 * @param a Nachricht
	 * @see Exception
	 */
	public StopAppException(String a) {
		super(a);
	}

}
