package it.sauronsoftware.feed4j;

import it.sauronsoftware.feed4j.bean.Feed;
import it.sauronsoftware.feed4j.bean.FeedHeader;
import it.sauronsoftware.feed4j.bean.FeedImage;
import it.sauronsoftware.feed4j.bean.FeedItem;
import it.sauronsoftware.feed4j.bean.RawElement;
import it.sauronsoftware.feed4j.bean.RawNode;
import it.sauronsoftware.feed4j.html.HTMLFragmentHelper;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * RSS 1.0 feed parser.
 * 
 * @author Carlo Pelliccia
 */
class TypeRSS_1_0 extends TypeAbstract {

	/**
	 * This method parses a dom4j Document representation assuming it is RSS 1.0
	 * feed.
	 * 
	 * @param source
	 *            The source URL for the feed.
	 * @param document
	 *            The dom4j Document representation of the XML representing the
	 *            feed.
	 * @return The Feed object representing the feed parsed contents.
	 */
	public static Feed feed(URL source, Document document) {
		// Root element.
		Element root = document.getRootElement();
		// Return value.
		Feed feed = new Feed();
		// Start from the header.
		FeedHeader header = new FeedHeader();
		header.setURL(source);
		// Search for the "channel" and the "item" elements.
		Element channel = null;
		for (Iterator i = root.elementIterator(); i.hasNext();) {
			Element aux = (Element) i.next();
			String nsuri = aux.getNamespaceURI();
			if (nsuri.equals(Constants.RSS_1_0_NS_URI)) {
				String name = aux.getName();
				if (name.equals("item")) {
					FeedItem item = handleItem(source, aux);
					if (item != null) {
						feed.addItem(item);
					}
				} else if (channel == null && name.equals("channel")) {
					channel = aux;
				}
			}
		}
		// Channel?
		if (channel != null) {
			// From channel to header, raw.
			populateRawElement(header, channel);
			// Parse every raw element and build non-raw ones.
			for (int i = 0; i < header.getNodeCount(); i++) {
				RawNode node = header.getNode(i);
				if (node instanceof RawElement) {
					RawElement element = (RawElement) node;
					String ensuri = element.getNamespaceURI();
					String ename = element.getName();
					String evalue = element.getValue();
					if (evalue != null) {
						// Textual element.
						if (ensuri.equals(Constants.RSS_1_0_NS_URI)) {
							if (ename.equals("title")) {
								header.setTitle(evalue);
							} else if (ename.equals("description")) {
								header.setDescription(evalue);
							} else if (ename.equals("link")) {
								try {
									header.setLink(new URL(evalue));
								} catch (MalformedURLException e) {
									;
								}
							}
						} else if (ensuri.equals(Constants.DC_NS_URI)) {
							if (evalue != null) {
								if (ename.equals("date")) {
									try {
										header
												.setPubDate(Constants.ISO_8601_DATE_FORMAT
														.parse(evalue));
									} catch (ParseException e) {
										;
									}
								} else if (ename.equals("language")) {
									if (isValidLanguageCode(evalue)) {
										header.setLanguage(evalue);
									}
								}
							}
						}
					} else {
						if (ensuri.equals(Constants.RSS_1_0_NS_URI)) {
							if (ename.equals("image")) {
								FeedImage image = handleImage(element);
								if (image != null) {
									header.setImage(image);
								}
							}
						}
					}
				}
			}
		}
		// Remove from the header raw elements the handled image.
		RawElement[] rawimages = header.getElements(Constants.RSS_1_0_NS_URI,
				"image");
		for (int i = 0; i < rawimages.length; i++) {
			header.removeNode(rawimages[i]);
		}
		// Add the header
		feed.setHeader(header);
		// Well done!
		return feed;
	}

	/**
	 * Items handler.
	 */
	private static FeedItem handleItem(URL source, Element el) {
		FeedItem item = new FeedItem();
		// Raw population.
		populateRawElement(item, el);
		// About -> GUID
		String rssGuid = item.getAttributeValue(Constants.RDF_NS_URI, "about");
		// Non-raw population.
		for (int i = 0; i < item.getNodeCount(); i++) {
			RawNode node = item.getNode(i);
			if (node instanceof RawElement) {
				RawElement element = (RawElement) node;
				String ensuri = element.getNamespaceURI();
				String ename = element.getName();
				String evalue = element.getValue();
				if (evalue != null) {
					// Textual element
					if (ensuri.equals(Constants.RSS_1_0_NS_URI)) {
						if (ename.equals("title")) {
							item.setTitle(evalue);
						} else if (ename.equals("link")) {
							try {
								item.setLink(new URL(evalue));
							} catch (MalformedURLException e) {
								;
							}
						} else if (ename.equals("description")) {
							item.setDescriptionAsText(evalue);
							item.setDescriptionAsHTML(HTMLFragmentHelper
									.fromTextPlainToHTML(evalue));
						}
					} else if (ensuri.equals(Constants.DC_NS_URI)) {
						if (evalue != null) {
							if (ename.equals("date")) {
								try {
									item
											.setPubDate(Constants.ISO_8601_DATE_FORMAT
													.parse(evalue));
								} catch (ParseException e) {
									;
								}
							}
						}
					}
				}
			}
		}
		// Valid?
		if (item.getTitle() == null || item.getLink() == null) {
			// No!
			return null;
		}
		// GUID for the item.
		if (rssGuid == null) {
			rssGuid = item.getLink().toExternalForm();
		}
		item.setGUID(buildGUID(source.hashCode(), rssGuid.hashCode()));
		// Well done!
		return item;
	}

	/**
	 * Channel image handler.
	 */
	private static FeedImage handleImage(RawElement rawImage) {
		FeedImage image = new FeedImage();
		// Raw population.
		populateRawElement(image, rawImage);
		// Non-raw population.
		String value = image.getElementValue(Constants.RSS_1_0_NS_URI, "url");
		if (value != null) {
			try {
				image.setURL(new URL(value));
			} catch (MalformedURLException e) {
				;
			}
		}
		value = image.getElementValue(Constants.RSS_1_0_NS_URI, "title");
		if (value != null) {
			image.setDescription(value);
		}
		// Valid?
		if (image.getURL() == null) {
			return null;
		}
		// Well done!
		return image;
	}

}
