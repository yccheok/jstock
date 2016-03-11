package it.sauronsoftware.feed4j.bean;

/**
 * This class is used to represent the textual node in a XML document.
 * 
 * @author Carlo Pelliccia
 */
public class RawText implements RawNode {

	/**
	 * The text in the node.
	 */
	private String text;

	/**
	 * This method returns the text in the node.
	 * 
	 * @return The text in the node.
	 */
	public String getText() {
		return text;
	}

	/**
	 * This method sets the text in the node.
	 * 
	 * @param text
	 *            The text in the node.
	 */
	public void setText(String text) {
		this.text = text;
	}

}
