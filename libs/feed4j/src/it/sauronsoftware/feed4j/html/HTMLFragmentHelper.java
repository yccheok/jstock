package it.sauronsoftware.feed4j.html;

import it.sauronsoftware.feed4j.bean.RawAttribute;
import it.sauronsoftware.feed4j.bean.RawElement;
import it.sauronsoftware.feed4j.bean.RawNode;
import it.sauronsoftware.feed4j.bean.RawText;

import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.html.dom.HTMLDocumentImpl;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.html.HTMLDocument;
import org.xml.sax.InputSource;

/**
 * HTML fragments parser and generator.
 * 
 * @author Carlo Pelliccia
 */
public class HTMLFragmentHelper {

	/**
	 * XML namespace.
	 */
	private static final String XML_NAMESPACE = "http://www.w3.org/XML/1998/namespace";

	/**
	 * XHTML namespace.
	 */
	private static final String XHTML_NAMESPACE = "http://www.w3.org/1999/xhtml";

	/**
	 * This method extracts a plain text from a HTML fragment.
	 * 
	 * @param str
	 *            The HTML fragment.
	 * @return The plain text extracted from the fragment.
	 */
	public static String fromHTMLtoTextPlain(String str) {
		DOMFragmentParser parser = new DOMFragmentParser();
		HTMLDocument document = new HTMLDocumentImpl();
		DocumentFragment fragment = document.createDocumentFragment();
		try {
			parser.parse(new InputSource(new StringReader(str)), fragment);
		} catch (Exception e) {
			return null;
		}
		return nodeToText(fragment);
	}

	private static String nodeToText(Node node) {
		StringBuffer buffer = new StringBuffer();
		if (node instanceof Text) {
			Text text = (Text) node;
			buffer.append(text.getData());
			buffer.append(' ');
		} else {
			NodeList list = node.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				buffer.append(nodeToText(list.item(i)));
				buffer.append(' ');
			}
		}
		String ret = buffer.toString();
		return ret.replaceAll("\\s+", " ").trim();
	}

	/**
	 * This method extracts a plain text from a XHTML fragment.
	 * 
	 * @param element
	 *            The XHTML fragment as an XML raw element.
	 * @return The plain text extracted from the fragment.
	 */
	public static String fromXHTMLtoTextPlain(RawElement element) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < element.getNodeCount(); i++) {
			RawNode node = element.getNode(i);
			if (node instanceof RawElement) {
				RawElement sub = (RawElement) node;
				buffer.append(fromXHTMLtoTextPlain(sub));
			} else if (node instanceof RawText) {
				RawText sub = (RawText) node;
				buffer.append(sub.getText());
			}
		}
		String ret = buffer.toString();
		return ret.replaceAll("\\s+", " ").trim();
	}

	/**
	 * This method encodes a plain text fragment in a HTML one.
	 * 
	 * @param str
	 *            The plain text fragment.
	 * @return The encoded HTML fragment.
	 */
	public static String fromTextPlainToHTML(String str) {
		return HTMLEntities.encode(str);
	}

	/**
	 * This method changes a XHTML fragment in a HTML one.
	 * 
	 * @param element
	 *            The XHTML fragment as an XML raw element.
	 * @return The HTML fragment as a string.
	 */
	public static String fromXHTMLtoHTML(RawElement element) {
		return fromXHTMLtoHTML(element, null);
	}

	/**
	 * This method changes a XHTML fragment in a HTML one.
	 * 
	 * @param element
	 *            The XHTML fragment as an XML raw element.
	 * @param base
	 *            The base URL for link href and image src absolute
	 *            reconstruction.
	 * @return The HTML fragment as a string.
	 */
	private static String fromXHTMLtoHTML(RawElement element, URL base) {
		String aux = element.getAttributeValue(XML_NAMESPACE, "base");
		if (aux != null) {
			try {
				base = new URL(aux);
			} catch (MalformedURLException e) {
				;
			}
		}
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < element.getNodeCount(); i++) {
			RawNode node = element.getNode(i);
			if (node instanceof RawText) {
				RawText rawText = (RawText) node;
				buffer.append(rawText.getText());
			} else if (node instanceof RawElement) {
				RawElement rawElement = (RawElement) node;
				if (rawElement.getNamespaceURI().equals(XHTML_NAMESPACE)) {
					buffer.append('<');
					buffer.append(rawElement.getName());
					for (int j = 0; j < rawElement.getAttributeCount(); j++) {
						RawAttribute rawAttribute = rawElement.getAttribute(j);
						if (rawAttribute.getNamespaceURI().equals(
								XHTML_NAMESPACE)) {
							String attrname = rawAttribute.getName();
							String attrvalue = rawAttribute.getValue();
							attrvalue = applBase(attrname, attrvalue, base);
							buffer.append(' ');
							buffer.append(attrname);
							buffer.append('=');
							buffer.append('"');
							buffer.append(HTMLEntities.encode(attrvalue));
							buffer.append('"');
						}
					}
					String value = rawElement.getValue();
					if (value != null) {
						buffer.append('>');
						buffer.append(HTMLEntities.encode(value));
						buffer.append('<');
						buffer.append('/');
						buffer.append(rawElement.getName());
						buffer.append('>');
					} else if (rawElement.getNodeCount() > 0) {
						buffer.append('>');
						buffer.append(fromXHTMLtoHTML(rawElement, base));
						buffer.append('<');
						buffer.append('/');
						buffer.append(rawElement.getName());
						buffer.append('>');
					} else {
						buffer.append(' ');
						buffer.append('/');
						buffer.append('>');
					}
				}
			}
		}
		String ret = buffer.toString();
		return ret.replaceAll("\\s+", " ").trim();
	}

	private static String applBase(String name, String value, URL base) {
		if (base != null && (name.equals("href") || name.equals("src"))) {
			if (value.indexOf(':') == -1) {
				try {
					URL aux = new URL(base, value);
					value = aux.toExternalForm();
				} catch (MalformedURLException e) {
					;
				}
			}
		}
		return value;
	}

}
