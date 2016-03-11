package it.sauronsoftware.feed4j.bean;

import java.net.URL;

/**
 * This class is used to represent the feed-related images.
 * 
 * @author Carlo Pelliccia
 */
public class FeedImage extends RawElement {

	/**
	 * The image URL.
	 */
	private URL url = null;

	/**
	 * The image description (it could be null).
	 */
	private String description = null;

	/**
	 * The image width (-1 if the information is not available).
	 */
	private int width = -1;

	/**
	 * The image height (-1 if the information is not available).
	 */
	private int height = -1;

	/**
	 * This method returns the image description. It could be null.
	 * 
	 * @return The image description (it could be null).
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * This method sets the image description.
	 * 
	 * @param description
	 *            The image description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * This method returns the image height, or -1 if the information is not
	 * available.
	 * 
	 * @return The image height, or -1 if the information is not available.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * This method sets the image height.
	 * 
	 * @param height
	 *            The image height.
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * This method returns the image URL.
	 * 
	 * @return The image URL.
	 */
	public URL getURL() {
		return url;
	}

	/**
	 * This method sets the image URL.
	 * 
	 * @param url
	 *            The image URL.
	 */
	public void setURL(URL url) {
		this.url = url;
	}

	/**
	 * This method returns the image width, or -1 if the information is not
	 * available.
	 * 
	 * @return The image width, or -1 if the information is not available.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * This method sets the image width.
	 * 
	 * @param width
	 *            The image width.
	 */
	public void setWidth(int width) {
		this.width = width;
	}

}
