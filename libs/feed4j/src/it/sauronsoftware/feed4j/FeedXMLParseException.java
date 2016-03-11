package it.sauronsoftware.feed4j;

/**
 * This kind of exception is thrown every time a XML parsing problem occour
 * during the parse process.
 * 
 * @author Carlo Pelliccia
 */
public class FeedXMLParseException extends FeedException {

	private static final long serialVersionUID = 1L;

	public FeedXMLParseException() {
		super();
	}

	public FeedXMLParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public FeedXMLParseException(String message) {
		super(message);
	}

	public FeedXMLParseException(Throwable cause) {
		super(cause);
	}

}
