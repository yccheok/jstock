package it.sauronsoftware.feed4j;

import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;

/**
 * A feed recognizer. It can recognize RSS 1.0, RSS 2.0, Atom 0.3 and Atom 1.0.
 * 
 * @author Carlo Pelliccia
 */
class FeedRecognizer extends TypeAbstract {

	/**
	 * Unknown feed type.
	 */
	public static final int UNKNOWN = -1;

	/**
	 * RSS 1.0.
	 */
	public static final int RSS_1_0 = 0;

	/**
	 * RSS 2.0.
	 */
	public static final int RSS_2_0 = 1;

	/**
	 * Atom 0.3.
	 */
	public static final int ATOM_0_3 = 2;

	/**
	 * Atom 1.0.
	 */
	public static final int ATOM_1_0 = 3;

	/**
	 * It analyzes a XML document representation and return a costant suggesting
	 * the type of the feed in the document.
	 * 
	 * @param document
	 *            The XML document representation.
	 * @return UNKNOWN, RSS_1_0, RSS_2_0, ATOM_0_3 or ATOM_1_0, depending on the
	 *         type recognized.
	 */
	public static int recognizeFeed(Document document) {
		Element root = document.getRootElement();
		if (root == null) {
			return UNKNOWN;
		}
		Namespace namespace = root.getNamespace();
		String nsuri = namespace != null ? namespace.getURI() : null;
		String name = root.getName();
		if ("rss".equals(name)) {
			String version = root.attributeValue("version");
			if (version == null || version.equals("2.0")
					|| version.equals("0.91") || version.equals("0.92")) {
				return RSS_2_0;
			}
		} else if ("RDF".equals(name) && Constants.RDF_NS_URI.equals(nsuri)) {
			for (Iterator i = root.elementIterator(); i.hasNext();) {
				Element element = (Element) i.next();
				String elNsUri = element.getNamespaceURI();
				if (Constants.RSS_1_0_NS_URI.equals(elNsUri)) {
					return RSS_1_0;
				}
			}
		} else if (name.equals("feed")) {
			String version = root.attributeValue("version");
			if (version == null || version.equals("1.0")
					|| Constants.ATOM_NS_URI.equals(nsuri)) {
				return ATOM_1_0;
			} else if (version.equals("0.3")
					|| Constants.ATOM_0_3_NS_URI.equals(nsuri)) {
				return ATOM_0_3;
			}
		}
		return UNKNOWN;
	}

}
