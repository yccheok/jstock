package it.sauronsoftware.feed4j;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Constants collection for the package.
 * 
 * @author Carlo Pelliccia
 */
interface Constants {

	/**
	 * RDF namespace.
	 */
	public String RDF_NS_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

	/**
	 * RSS 1.0 namespace.
	 */
	public String RSS_1_0_NS_URI = "http://purl.org/rss/1.0/";

	/**
	 * Atom (1.0) namespace.
	 */
	public String ATOM_NS_URI = "http://www.w3.org/2005/Atom";

	/**
	 * Atom 0.3 namespace.
	 */
	public String ATOM_0_3_NS_URI = "http://purl.org/atom/ns#";

	/**
	 * Dublin Core extensions namespace.
	 */
	public String DC_NS_URI = "http://purl.org/dc/elements/1.1/";

	/**
	 * XML base namespace.
	 */
	public String XML_NAMESPACE = "http://www.w3.org/XML/1998/namespace";

	/**
	 * RFC 822 compliant DateFormat.
	 */
	public DateFormat RFC_822_DATE_FORMAT = new SimpleDateFormat(
			"EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'z", Locale.US);

	/**
	 * ISO 8601 compliant DateFormat.
	 */
	public DateFormat ISO_8601_DATE_FORMAT = new ISO8601DateFormat();

}
