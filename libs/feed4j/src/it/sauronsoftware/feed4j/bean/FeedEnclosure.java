package it.sauronsoftware.feed4j.bean;

import java.net.URL;

/**
 * This class is used to represent a feed enclosure.
 * 
 * @author Carlo Pelliccia
 */
public class FeedEnclosure extends RawElement {

	/**
	 * The enclosure URL.
	 */
	private URL url = null;

	/**
	 * The enclosure MIME type.
	 */
	private String mimeType = null;

	/**
	 * The enclosure size (-1 if unknown).
	 */
	private long length = -1;

	/**
	 * The enclosure title (null if not supplied).
	 */
	private String title;

	/**
	 * This method returns the enclosure size in bytes, or -1 if the information
	 * is not available.
	 * 
	 * @return The enclosure size in bytes, or -1 if the information is not
	 *         available.
	 */
	public long getLength() {
		return length;
	}

	/**
	 * This method sets the enclosure size in bytes.
	 * 
	 * @param length
	 *            The enclosure size in bytes.
	 */
	public void setLength(long length) {
		this.length = length;
	}

	/**
	 * This method returns the enclosure MIME type.
	 * 
	 * @return The enclosure MIME type.
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * This method sets the enclosure MIME type.
	 * 
	 * @param mimeType
	 *            The enclosure MIME type.
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * This method returns the URL for the enclosure retrieval.
	 * 
	 * @return The URL for the enclosure retrieval.
	 */
	public URL getURL() {
		return url;
	}

	/**
	 * This method sets the URL for the enclosure retrieval.
	 * 
	 * @param url
	 *            The URL for the enclosure retrieval.
	 */
	public void setURL(URL url) {
		this.url = url;
	}

	/**
	 * This method returns the enclosure title, or null if the information is
	 * not available.
	 * 
	 * @return The enclosure title, or null if the information is not available.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * This method sets the enclosure title.
	 * 
	 * @param title
	 *            The enclosure title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

}
