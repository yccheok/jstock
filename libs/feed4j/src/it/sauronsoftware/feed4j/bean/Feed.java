package it.sauronsoftware.feed4j.bean;

import java.util.ArrayList;

/**
 * This class is used to represent a parsed feed in an object oriented way.
 * 
 * @author Carlo Pelliccia
 */
public class Feed {

	/**
	 * The feed header.
	 */
	private FeedHeader header = null;

	/**
	 * The feed items.
	 */
	private ArrayList items = null;

	/**
	 * This method returns the feed header.
	 * 
	 * @return The feed header.
	 */
	public FeedHeader getHeader() {
		return header;
	}

	/**
	 * This method sets the feed header.
	 * 
	 * @param header
	 *            The feed header.
	 */
	public void setHeader(FeedHeader header) {
		this.header = header;
	}

	/**
	 * This method adds an item to the feed.
	 * 
	 * @param item
	 *            The item.
	 */
	public void addItem(FeedItem item) {
		if (items == null) {
			items = new ArrayList();
		}
		items.add(item);
	}

	/**
	 * This method returns a feed item.
	 * 
	 * @param index
	 *            The item index.
	 * @return The feed item.
	 * @throws IndexOutOfBoundsException
	 *             Thrown if the index supplied is not valid (index is expected
	 *             to be greater or equal to 0 and less than the value returned
	 *             by getItemsCount()).
	 */
	public FeedItem getItem(int index) throws IndexOutOfBoundsException {
		if (items == null) {
			throw new IndexOutOfBoundsException();
		} else {
			return (FeedItem) items.get(index);
		}
	}

	/**
	 * This method returns the number of the items in the feed.
	 * 
	 * @return The number of the items in the feed.
	 */
	public int getItemCount() {
		if (items == null) {
			return 0;
		} else {
			return items.size();
		}
	}

}
