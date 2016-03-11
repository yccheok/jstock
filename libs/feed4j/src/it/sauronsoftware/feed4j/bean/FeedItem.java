package it.sauronsoftware.feed4j.bean;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class is used to represent the items in a feed.
 * 
 * @author Carlo Pelliccia
 */
public class FeedItem extends RawElement {

	/**
	 * The item GUID, used to identify it universally. It's very unusual that
	 * two non-equivalent items have the same GUID.
	 */
	private String GUID = null;

	/**
	 * The item title.
	 */
	private String title = null;

	/**
	 * The item link.
	 */
	private URL link = null;

	/**
	 * The URL for the comments to the item.
	 */
	private URL comments = null;

	/**
	 * The item description as HTML code.
	 */
	private String descriptionAsHTML = null;

	/**
	 * The item description as plain text.
	 */
	private String descriptionAsText = null;

	/**
	 * The item publication date.
	 */
	private Date pubDate = null;

	/**
	 * The item latest modification date.
	 */
	private Date modDate = null;

	/**
	 * The item enclosures list.
	 */
	private ArrayList enclosures = null;

	/**
	 * This method returns the URL for the comments to the item. It could be
	 * null.
	 * 
	 * @return The URL for the comments to the item (it could be null).
	 */
	public URL getComments() {
		return comments;
	}

	/**
	 * This method sets the URL for the comments to the item.
	 * 
	 * @param comments
	 *            The URL for the comments to the item.
	 */
	public void setComments(URL comments) {
		this.comments = comments;
	}

	/**
	 * This method returns the item GUID, used to identify it universally. It's
	 * very unusual that two non-equivalent items have the same GUID.
	 * 
	 * @return The item GUID.
	 */
	public String getGUID() {
		return GUID;
	}

	/**
	 * This method sets the item GUID.
	 * 
	 * @param guid
	 *            The item GUID. universalmente.
	 */
	public void setGUID(String guid) {
		GUID = guid;
	}

	/**
	 * This method returns the item link.
	 * 
	 * @return The item link.
	 */
	public URL getLink() {
		return link;
	}

	/**
	 * This method sets the item link.
	 * 
	 * @param link
	 *            The item link.
	 */
	public void setLink(URL link) {
		this.link = link;
	}

	/**
	 * This method returns the item publication date. It could be null.
	 * 
	 * @return The item publication date (it could be null).
	 */
	public Date getPubDate() {
		return pubDate;
	}

	/**
	 * This method sets the item publication date.
	 * 
	 * @param pubDate
	 *            The item publication date.
	 */
	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}

	/**
	 * This method returns the item title.
	 * 
	 * @return The item title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * This method sets the item title.
	 * 
	 * @param title
	 *            The item title.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * This method adds an enclosure to the item.
	 * 
	 * @param enclosure
	 *            The item enclosure.
	 */
	public void addEnclosure(FeedEnclosure enclosure) {
		if (enclosures == null) {
			enclosures = new ArrayList();
		}
		enclosures.add(enclosure);
	}

	/**
	 * This method returns one of the item enclosures.
	 * 
	 * @param index
	 *            The index of the wanted enclosure-
	 * @return The item enclosure.
	 * @throws IndexOutOfBoundsException
	 *             If the index supplied is not valid. It must be greater or
	 *             equal to 0 and less than the value returned by
	 *             getEnclosureCount().
	 */
	public FeedEnclosure getEnclosure(int index)
			throws IndexOutOfBoundsException {
		if (enclosures == null) {
			throw new IndexOutOfBoundsException();
		} else {
			return (FeedEnclosure) enclosures.get(index);
		}
	}

	/**
	 * This method returns the number of the feed enclosures.
	 * 
	 * @return The number of the feed enclosures.
	 */
	public int getEnclosureCount() {
		if (enclosures == null) {
			return 0;
		} else {
			return enclosures.size();
		}
	}

	/**
	 * This method returns the item latest modification date. It could be null.
	 * 
	 * @return The item latest modification date (it could be null).
	 */
	public Date getModDate() {
		return modDate;
	}

	/**
	 * This method sets the item latest modification date.
	 * 
	 * @param modDate
	 *            The item latest modification date.
	 */
	public void setModDate(Date modDate) {
		this.modDate = modDate;
	}

	/**
	 * This method returns the item description as HTML code. It could be null
	 * if no description is supplied. If no HTML description is supplied, but a
	 * plain text one is given, the HTML description is encoded automatically
	 * starting from the plain text one.
	 * 
	 * @return The item description as HTML code (it could be null).
	 */
	public String getDescriptionAsHTML() {
		return descriptionAsHTML;
	}

	/**
	 * This method sets the item description as HTML code.
	 * 
	 * @param descriptionAsHTML
	 *            The item description as HTML code.
	 */
	public void setDescriptionAsHTML(String descriptionAsHTML) {
		this.descriptionAsHTML = descriptionAsHTML;
	}

	/**
	 * This method returns the item description as plain text. It could be null
	 * if no description is supplied. If no plain text description is supplied,
	 * but a HTML encoded one is given, the plain text description is extracted
	 * automatically starting from the HTML one.
	 * 
	 * @return The item description as plain text (it could be null).
	 */
	public String getDescriptionAsText() {
		return descriptionAsText;
	}

	/**
	 * This method sets the item description as plain text.
	 * 
	 * @param descriptionAsText
	 *            The item description as plain text.
	 */
	public void setDescriptionAsText(String descriptionAsText) {
		this.descriptionAsText = descriptionAsText;
	}

}
