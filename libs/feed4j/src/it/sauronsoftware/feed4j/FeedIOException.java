package it.sauronsoftware.feed4j;

/**
 * This kind of exception is thrown by the parser every time a I/O error occurs
 * during the reading of a feed.
 * 
 * @author Carlo Pelliccia
 */
public class FeedIOException extends FeedException {

	private static final long serialVersionUID = 1L;

	public FeedIOException() {
		super();
	}

	public FeedIOException(String message, Throwable cause) {
		super(message, cause);
	}

	public FeedIOException(String message) {
		super(message);
	}

	public FeedIOException(Throwable cause) {
		super(cause);
	}

}
