package it.sauronsoftware.feed4j;

/**
 * The base kind of the exceptions thrown by the feed parser.
 * 
 * @author Carlo Pelliccia
 */
public abstract class FeedException extends Exception {

	public FeedException() {
		super();
	}

	public FeedException(String message, Throwable cause) {
		super(message, cause);
	}

	public FeedException(String message) {
		super(message);
	}

	public FeedException(Throwable cause) {
		super(cause);
	}

}
