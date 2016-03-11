package it.sauronsoftware.feed4j.html;

import java.io.StringReader;

import org.apache.html.dom.HTMLDocumentImpl;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.html.HTMLDocument;
import org.xml.sax.InputSource;

/**
 * HTML code optimizer. It analyzes a HTML fragment fixing common mistakes and
 * removing those attributes and tags usually useless for a feed reader.
 * 
 * @author Carlo Pelliccia
 */
public class HTMLOptimizer {

	/**
	 * An attribute structure.
	 * 
	 * @author Carlo Pelliccia
	 * 
	 */
	private static class Attribute {

		public String name;

		public boolean required;

		public Attribute(String name, boolean required) {
			this.name = name;
			this.required = required;
		}

	}

	/**
	 * A tag structure.
	 * 
	 * @author Carlo Pelliccia
	 * 
	 */
	private static class Tag {

		public String name;

		public Attribute[] attributes;

		public Tag(String name, Attribute[] attributes) {
			this.name = name;
			this.attributes = attributes;
		}

	}

	/**
	 * The approved tags list, each element with its approved attributes. The
	 * residue will be removed.
	 */
	private static final Tag[] TAGS = new Tag[] {
			new Tag("strong", new Attribute[0]),
			new Tag("cite", new Attribute[0]),
			new Tag("em", new Attribute[0]),
			new Tag("p", new Attribute[0]),
			new Tag("div", new Attribute[0]),
			new Tag("br", new Attribute[0]),
			new Tag("ul", new Attribute[0]),
			new Tag("ol", new Attribute[0]),
			new Tag("li", new Attribute[0]),
			new Tag("table", new Attribute[0]),
			new Tag("tr", new Attribute[0]),
			new Tag("td", new Attribute[0]),
			new Tag("th", new Attribute[0]),
			new Tag("img",
					new Attribute[] { new Attribute("src", true),
							new Attribute("width", false),
							new Attribute("height", false),
							new Attribute("alt", false),
							new Attribute("title", false) }),
			new Tag("a", new Attribute[] { new Attribute("href", true),
					new Attribute("title", false) }) };

	/**
	 * This method analyzes a HTML fragment fixing common mistakes and removing
	 * those attributes and tags usually useless for a feed reader.
	 * 
	 * @param html
	 *            The HTML fragment.
	 * @return The fixed HTML fragment.
	 */
	public static String optimize(String html) {
		// Parsa l'HTML.
		DOMFragmentParser parser = new DOMFragmentParser();
		HTMLDocument document = new HTMLDocumentImpl();
		DocumentFragment fragment = document.createDocumentFragment();
		try {
			parser.parse(new InputSource(new StringReader(html)), fragment);
		} catch (Exception e) {
			return null;
		}
		// Esegue le ottimizzazioni, ricodifica come stringa e restituisce.
		String ret = fromNodeToString(fragment).toString();
		return ret;
	}

	private static StringBuffer fromNodeToString(Node node) {
		StringBuffer buffer = new StringBuffer();
		if (node instanceof Element) {
			Element element = (Element) node;
			buffer.append(internal(element));
		} else if (node instanceof Text) {
			Text text = (Text) node;
			buffer.append(HTMLEntities.encode(text.getNodeValue()));
		} else {
			NodeList list = node.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				Node current = list.item(i);
				buffer.append(fromNodeToString(current));
			}
		}
		return buffer;
	}

	private static StringBuffer internal(Element el) {
		StringBuffer buffer = new StringBuffer();
		String tagname = el.getNodeName().toLowerCase();
		Tag tag = recognizeTag(tagname, el.getAttributes());
		if (tag != null) {
			buffer.append('<');
			buffer.append(tagname);
			buffer.append(recognizeAttributes(el, tag));
		}
		StringBuffer buffer2 = new StringBuffer();
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			buffer2.append(fromNodeToString(list.item(i)));
		}
		if (tag != null) {
			if (buffer2.length() == 0) {
				buffer.append(' ');
				buffer.append('/');
				buffer.append('>');
			} else {
				buffer.append('>');
				buffer.append(buffer2);
				buffer.append('<');
				buffer.append('/');
				buffer.append(tagname);
				buffer.append('>');
			}
		} else {
			if (buffer2.length() > 0) {
				buffer.append(buffer2);
			}
		}
		return buffer;
	}

	private static Tag recognizeTag(String tagName, NamedNodeMap attrs) {
		for (int i = 0; i < TAGS.length; i++) {
			if (TAGS[i].name.equals(tagName)) {
				Attribute[] aux = TAGS[i].attributes;
				for (int j = 0; j < aux.length; j++) {
					if (aux[j].required) {
						boolean found = false;
						for (int k = 0; k < attrs.getLength(); k++) {
							Node aux2 = attrs.item(k);
							String name = aux2.getNodeName().toLowerCase();
							String value = aux2.getNodeValue();
							if (name.equalsIgnoreCase(aux[j].name)
									&& value != null && value.length() > 0) {
								found = true;
								break;
							}
						}
						if (!found) {
							return null;
						}
					}
				}
				return TAGS[i];
			}
		}
		return null;
	}

	private static StringBuffer recognizeAttributes(Element element, Tag tag) {
		StringBuffer buffer = new StringBuffer();
		NamedNodeMap attrs = element.getAttributes();
		for (int k = 0; k < attrs.getLength(); k++) {
			Node attr = attrs.item(k);
			String attrName = attr.getNodeName().toLowerCase();
			String attrValue = attr.getNodeValue();
			boolean found = false;
			for (int w = 0; w < tag.attributes.length; w++) {
				if (attrName.equals(tag.attributes[w].name)) {
					found = true;
					break;
				}
			}
			if (found) {
				if (attrValue != null && attrValue.length() > 0) {
					buffer.append(' ');
					buffer.append(attrName);
					buffer.append('=');
					buffer.append('"');
					buffer.append(HTMLEntities.encode(attrValue));
					buffer.append('"');
				}
			}
		}
		return buffer;
	}

}
