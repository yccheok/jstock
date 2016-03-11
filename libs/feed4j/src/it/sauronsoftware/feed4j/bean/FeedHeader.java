package it.sauronsoftware.feed4j.bean;

import java.net.URL;
import java.util.Date;

/**
 * This class is used to represent the feed header informations.
 * 
 * @author Carlo Pelliccia
 */
public class FeedHeader extends RawElement {

	/**
	 * The feed URL.
	 */
	private URL url;

	/**
	 * The feed title.
	 */
	private String title;

	/**
	 * The feed link.
	 */
	private URL link;

	/**
	 * The feed description.
	 */
	private String description;

	/**
	 * The feed language code.
	 */
	private String language;

	/**
	 * The feed publication date.
	 */
	private Date pubDate;

	/**
	 * The feed image.
	 */
	private FeedImage image;

	/**
	 * This method returns the feed description. It could be null.
	 * 
	 * @return The feed description (it could be null).
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * This method sets the feed description.
	 * 
	 * @param description
	 *            The feed description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * This method returns the link in the feed, usually it links a feed-related
	 * web page. It could be null.
	 * 
	 * @return The link in the feed (it could be null).
	 */
	public URL getLink() {
		return link;
	}

	/**
	 * This method sets the link in the feed.
	 * 
	 * @param link
	 *            The link in the feed.
	 */
	public void setLink(URL link) {
		this.link = link;
	}

	/**
	 * This method return the feed title. It could be null.
	 * 
	 * @return The feed title (it could be null).
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * This method sets the feed title.
	 * 
	 * @param title
	 *            The feed title.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * This method returns the feed language code. It could be null.
	 * 
	 * @return The feed language code (it could be null).
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * This method sets the feed language code.
	 * 
	 * @param language
	 *            The feed language code.
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * This method returns the feed publication date. It could be null.
	 * 
	 * @return The feed publication date (it could be null).
	 */
	public Date getPubDate() {
		return pubDate;
	}

	/**
	 * This method sets the feed publication date.
	 * 
	 * @param pubDate
	 *            The feed publication date.
	 */
	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}

	/**
	 * This method return the feed image object representation. It could be
	 * null.
	 * 
	 * @return The feed image (it could be null).
	 */
	public FeedImage getImage() {
		return image;
	}

	/**
	 * This method sets the feed image.
	 * 
	 * @param image
	 *            The feed image.
	 */
	public void setImage(FeedImage image) {
		this.image = image;
	}

	/**
	 * This method returns the feed URL.
	 * 
	 * @return The feed URL.
	 */
	public URL getURL() {
		return url;
	}

	/**
	 * This method sets the feed URL.
	 * 
	 * @param url
	 *            he feed URL.
	 */
	public void setURL(URL url) {
		this.url = url;
	}

}
