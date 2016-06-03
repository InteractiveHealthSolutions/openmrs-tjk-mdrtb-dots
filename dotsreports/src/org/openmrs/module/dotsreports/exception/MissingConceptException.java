package org.openmrs.module.dotsreports.exception;


public class MissingConceptException extends MdrtbAPIException {

    private static final long serialVersionUID = 1L;
	/**
	 * Default empty constructor. If at all possible, don't use this one, but use the
	 * {@link #MissingConceptException(String)} constructor to specify a helpful message to the end user
	 */
	public MissingConceptException() {
	}
	
	/**
	 * General constructor to give the end user a helpful message that relates to why this error
	 * occurred.
	 * 
	 * @param message helpful message string for the end user
	 */
	public MissingConceptException(String message) {
		super(message);
	}
	
	/**
	 * General constructor to give the end user a helpful message and to also propagate the parent
	 * error exception message.
	 * 
	 * @param message helpful message string for the end user
	 * @param cause the parent exception cause that this MissingConceptException is wrapping around
	 */
	public MissingConceptException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructor used to simply chain a parent exception cause to an MissingConceptException. Preference
	 * should be given to the {@link #MissingConceptException(String, Throwable)} constructor if at all
	 * possible instead of this one.
	 * 
	 * @param cause the parent exception cause that this MissingConceptException is wrapping around
	 */
	public MissingConceptException(Throwable cause) {
		super(cause);
	}
}
