package it.sauronsoftware.feed4j;

import it.sauronsoftware.feed4j.bean.RawAttribute;
import it.sauronsoftware.feed4j.bean.RawElement;
import it.sauronsoftware.feed4j.bean.RawText;

import java.util.Iterator;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Text;

/**
 * Base class for building specific feed type parser. It contains also some
 * static utilities.
 * 
 * @author Carlo Pelliccia
 * 
 */
class TypeAbstract {

	/**
	 * Valid local codes.
	 */
	private static final String[] VALID_LANGUAGES = new String[] { "af", "sq",
			"eu", "be", "bg", "ca", "zh-cn", "zh-tw", "hr", "cs", "da", "nl",
			"nl-be", "nl-nl", "en", "en-au", "en-bz", "en-ca", "en-ie",
			"en-jm", "en-nz", "en-ph", "en-za", "en-tt", "en-gb", "en-us",
			"en-zw", "et", "fo", "fi", "fr", "fr-be", "fr-ca", "fr-fr",
			"fr-lu", "fr-mc", "fr-ch", "gl", "gd", "de", "de-at", "de-de",
			"de-li", "de-lu", "de-ch", "el", "haw", "hu", "is", "in", "ga",
			"it", "it-it", "it-ch", "ja", "ko", "mk", "no", "pl", "pt",
			"pt-br", "pt-pt", "ro", "ro-mo", "ro-ro", "ru", "ru-mo", "ru-ru",
			"sr", "sk", "sl", "es", "es-ar", "es-bo", "es-cl", "es-co",
			"es-cr", "es-do", "es-ec", "es-sv", "es-gt", "es-hn", "es-mx",
			"es-ni", "es-pa", "es-py", "es-pe", "es-pr", "es-es", "es-uy",
			"es-ve", "sv", "sv-fi", "sv-se", "tr", "uk" };

	/**
	 * This method fills a RawElement object recursively, extracting
	 * informations from a dom4j Element object.
	 * 
	 * @param re
	 *            The RawElement (side-effect on this one).
	 * @param e
	 *            The dom4j Element object to explore in order to populate the
	 *            RawElement supplied.
	 */
	protected static void populateRawElement(RawElement re, Element e) {
		// Base informations.
		String ensuri = e.getNamespaceURI();
		re.setNamespaceURI(ensuri);
		re.setName(e.getName());
		// Attributes.
		for (Iterator i = e.attributeIterator(); i.hasNext();) {
			Attribute attr = (Attribute) i.next();
			RawAttribute rawAttribute = new RawAttribute();
			String attrnsuri = attr.getNamespaceURI();
			if (attrnsuri.equals("")) {
				attrnsuri = ensuri;
			}
			rawAttribute.setNamespaceURI(attrnsuri);
			rawAttribute.setName(attr.getName());
			String value = attr.getValue();
			if (value != null) {
				value = value.trim();
				rawAttribute.setValue(value);
				re.addAttribute(rawAttribute);
			}
		}
		// Contents.
		if (e.isTextOnly()) {
			String text = e.getTextTrim();
			if (text.length() > 0) {
				re.setValue(text);
			}
		}
		// Child elements.
		for (Iterator i = e.nodeIterator(); i.hasNext();) {
			Node current = (Node) i.next();
			if (current instanceof Text) {
				Text text = (Text) current;
				String aux = text.getText();
				if (aux != null && (aux = aux.trim()).length() > 0) {
					RawText rawText = new RawText();
					rawText.setText(aux);
					re.addNode(rawText);
				}
			} else if (current instanceof Element) {
				Element element = (Element) current;
				RawElement rawElement = new RawElement();
				populateRawElement(rawElement, element);
				re.addNode(rawElement);
			}
		}
	}

	/**
	 * XML nodes copier. This method populates r1 with the attributes and the
	 * nodes taken from re2.
	 */
	protected static void populateRawElement(RawElement re1, RawElement re2) {
		re1.setValue(re2.getValue());
		for (int i = 0; i < re2.getAttributeCount(); i++) {
			re1.addAttribute(re2.getAttribute(i));
		}
		for (int i = 0; i < re2.getNodeCount(); i++) {
			re1.addNode(re2.getNode(i));
		}
	}

	/**
	 * Is the given string a valid language code?
	 * 
	 * @param str
	 *            The string.
	 * @return true if the supplied string represents a valid language code,
	 *         false otherwise.
	 */
	protected static boolean isValidLanguageCode(String str) {
		for (int i = 0; i < VALID_LANGUAGES.length; i++) {
			if (str.equalsIgnoreCase(VALID_LANGUAGES[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * GUID maker helper routine.
	 */
	private static String toHexGUIDRapp(int a) {
		char firstChar;
		if (a < 0) {
			a = -a;
			firstChar = '-';
		} else {
			firstChar = '+';
		}
		String aux = Integer.toHexString(a);
		int size = aux.length();
		if (size < 8) {
			for (int i = size; i < 8; i++) {
				aux = '0' + aux;
			}
		}
		return firstChar + aux;
	}

	/**
	 * GUID maker for feed items. This method generates 18 chars GUIDs starting
	 * from two integer values. Usually the two values are hashcodes
	 * respectively for the whole feed (i.e. the hashcode of the feed URL) and
	 * the specific feed item (i.e. the hashcode of the link/permalink in the
	 * item, or the hashcode of the item GUID if one is given). It depends on
	 * the type of the feed and on the specific parser implementation.
	 * 
	 * @param a
	 *            Il primo intero del GUID.
	 * @param b
	 *            Il secondo intero del GUID.
	 * @return The GUID, as an 18 chars string.
	 */
	protected static String buildGUID(int a, int b) {
		return toHexGUIDRapp(a) + toHexGUIDRapp(b);
	}

}
