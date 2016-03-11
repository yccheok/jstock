package it.sauronsoftware.feed4j;

import it.sauronsoftware.feed4j.bean.Feed;
import it.sauronsoftware.feed4j.bean.FeedHeader;
import it.sauronsoftware.feed4j.bean.FeedItem;
import it.sauronsoftware.feed4j.bean.RawElement;
import it.sauronsoftware.feed4j.bean.RawNode;
import it.sauronsoftware.feed4j.html.HTMLFragmentHelper;
import it.sauronsoftware.feed4j.html.HTMLOptimizer;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * Atom 0.3 feed parser.
 * 
 * @author Carlo Pelliccia
 */
class TypeAtom_0_3 extends TypeAbstract {

	/**
	 * This method parses a dom4j Document representation assuming it is an Atom
	 * 0.3 feed.
	 * 
	 * @param source
	 *            The source URL for the feed.
	 * @param document
	 *            The dom4j Document representation of the XML representing the
	 *            feed.
	 * @return The Feed object representing the feed parsed contents.
	 */
	public static Feed feed(URL source, Document document) {
		// Get the root element.
		Element root = document.getRootElement();
		// Root element namespace URI.
		String nsuri = root.getNamespaceURI();
		// Create the return value.
		Feed feed = new Feed();
		// Build the Header object.
		FeedHeader header = new FeedHeader();
		header.setURL(source);
		// Populate the header with raw elements.
		populateRawElement(header, root);
		// The feed language.
		String language = header.getAttributeValue(Constants.XML_NAMESPACE,
				"lang");
		if (language != null && isValidLanguageCode(language)) {
			header.setLanguage(language);
		}
		// Other interesting informations...
		for (int i = 0; i < header.getNodeCount(); i++) {
			RawNode node = header.getNode(i);
			if (node instanceof RawElement) {
				RawElement element = (RawElement) node;
				String ensuri = element.getNamespaceURI();
				String ename = element.getName();
				if (ensuri.equals(nsuri)) {
					// Atom namespace.
					if (ename.equals("title")) {
						String title = getValueAsTextPlain(element);
						if (title != null) {
							header.setTitle(title);
						}
					} else if (ename.equals("link")) {
						URL link = handleLink(element);
						if (link != null) {
							header.setLink(link);
						}
					} else if (ename.equals("modified")) {
						String modified = element.getValue();
						if (modified != null) {
							try {
								header
										.setPubDate(Constants.ISO_8601_DATE_FORMAT
												.parse(modified));
							} catch (ParseException e) {
								;
							}
						}
					} else if (ename.equals("entry")) {
						FeedItem item = handleEntry(source, element);
						if (item != null) {
							feed.addItem(item);
						}
					}

				}
			}
		}
		// Removes from the header every "entry" raw element.
		RawElement[] rawentries = header.getElements(nsuri, "entry");
		for (int i = 0; i < rawentries.length; i++) {
			header.removeNode(rawentries[i]);
		}
		// Add the header.
		feed.setHeader(header);
		// Well done!
		return feed;
	}

	/**
	 * From an Atom entry to a FeedItem object.
	 */
	private static FeedItem handleEntry(URL source, RawElement entryElement) {
		// Atom "entry" element namespace URI.
		String nsuri = entryElement.getNamespaceURI();
		// Return value.
		FeedItem item = new FeedItem();
		// Raw population.
		populateRawElement(item, entryElement);
		// Explore every node of the entry.
		String summary = null;
		String content = null;
		String id = null;
		for (int i = 0; i < item.getNodeCount(); i++) {
			RawNode node = item.getNode(i);
			if (node instanceof RawElement) {
				RawElement element = (RawElement) node;
				String ensuri = element.getNamespaceURI();
				String ename = element.getName();
				if (ensuri.equals(nsuri)) {
					if (ename.equals("title")) {
						String title = getValueAsTextPlain(element);
						if (title != null) {
							item.setTitle(title);
						}
					} else if (ename.equals("link")) {
						URL link = handleLink(element);
						if (link != null) {
							item.setLink(link);
						}
					} else if (ename.equals("id")) {
						String aux = element.getValue();
						if (aux != null) {
							id = aux;
						}
					} else if (ename.equals("summary")) {
						String aux = getValueAsHTML(element);
						if (aux != null) {
							summary = aux;
						}
					} else if (ename.equals("content")) {
						String aux = getValueAsHTML(element);
						if (aux != null) {
							content = aux;
						}
					} else if (ename.equals("issued")) {
						String modified = element.getValue();
						if (modified != null) {
							try {
								item.setPubDate(Constants.ISO_8601_DATE_FORMAT
										.parse(modified));
							} catch (ParseException e) {
								;
							}
						}
					} else if (ename.equals("modified")) {
						String modified = element.getValue();
						if (modified != null) {
							try {
								item.setPubDate(Constants.ISO_8601_DATE_FORMAT
										.parse(modified));
							} catch (ParseException e) {
								;
							}
						}
					}
				}
			}
		}
		// Is this item solid?
		if (item.getTitle() == null || item.getLink() == null) {
			// No, return null.
			return null;
		}
		// Work on the item description.
		if (summary == null) {
			summary = "";
		}
		if (content == null) {
			content = "";
		}
		String description = (content.length() > summary.length()) ? content
				: summary;
		if (description.length() > 0) {
			item.setDescriptionAsHTML(description);
			item.setDescriptionAsText(HTMLFragmentHelper
					.fromHTMLtoTextPlain(description));
		}
		// GUID generation.
		if (id == null) {
			id = item.getLink().toExternalForm();
		}
		item.setGUID(buildGUID(source.hashCode(), id.hashCode()));
		// Well done!
		return item;
	}

	/**
	 * Atom link analyzer.
	 */
	private static URL handleLink(RawElement linkElement) {
		String nsuri = linkElement.getNamespaceURI();
		// 1. Attribute rel="alternate" required.
		String rel = linkElement.getAttributeValue(nsuri, "rel");
		if (rel != null && !rel.equalsIgnoreCase("alternate")) {
			return null;
		}
		// 2. Attribute href="..." required.
		String href = linkElement.getAttributeValue(nsuri, "href");
		if (href == null || href.length() == 0) {
			return null;
		}
		// 3. Valid URL?.
		try {
			return new URL(href);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	/**
	 * Gets an element value as HTML. The element must contain a "type" Atom
	 * attribute.
	 */
	private static String getValueAsHTML(RawElement element) {
		String type = element.getAttributeValue(element.getNamespaceURI(),
				"type");
		if (type == null || type.length() == 0) {
			type = "text/plain";
		}
		if (type.equals("text/plain")) {
			String aux = element.getValue();
			if (aux != null) {
				aux = aux.trim();
				if (aux.length() > 0) {
					return HTMLFragmentHelper.fromTextPlainToHTML(aux);
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else if (type.equals("text/html")) {
			String aux = element.getValue();
			if (aux != null) {
				aux = aux.trim();
				if (aux.length() > 0) {
					aux = HTMLOptimizer.optimize(aux);
					if (aux.length() > 0) {
						return aux;
					} else {
						return null;
					}
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else if (type.equals("application/xhtml+xml")) {
			String aux = HTMLFragmentHelper.fromXHTMLtoHTML(element);
			if (aux.length() > 0) {
				aux = HTMLOptimizer.optimize(aux);
				if (aux.length() > 0) {
					return aux;
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Gets an element value as plain text. The element must contain a "type"
	 * Atom attribute.
	 */
	private static String getValueAsTextPlain(RawElement element) {
		String type = element.getAttributeValue(element.getNamespaceURI(),
				"type");
		if (type == null || type.length() == 0) {
			type = "text/plain";
		}
		if (type.equals("text/plain")) {
			String aux = element.getValue();
			if (aux != null) {
				aux = aux.trim();
				if (aux.length() > 0) {
					return aux;
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else if (type.equals("text/html")) {
			String value = element.getValue();
			if (value != null && value.length() > 0) {
				String aux = HTMLFragmentHelper.fromHTMLtoTextPlain(value);
				if (aux.length() > 0) {
					return aux;
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else if (type.equals("application/xhtml+xml")) {
			String aux = HTMLFragmentHelper.fromXHTMLtoTextPlain(element);
			if (aux.length() > 0) {
				return aux;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

}
