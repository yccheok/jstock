package it.sauronsoftware.feed4j;

/**
 * This means that the parser has encountered a feed whose type is unfortunately
 * unsupported.
 * 
 * @author Carlo Pelliccia
 * 
 */
public class UnsupportedFeedException extends FeedException {

	private static final long serialVersionUID = 1L;

	public UnsupportedFeedException() {
		super();
	}

}
