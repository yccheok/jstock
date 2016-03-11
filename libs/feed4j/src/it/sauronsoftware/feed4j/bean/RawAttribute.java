package it.sauronsoftware.feed4j.bean;

/**
 * This class is used to represent the attributes of a XML element.
 * 
 * @author Carlo Pelliccia
 */
public class RawAttribute {

	/**
	 * The attribute namespace URI.
	 */
	private String namespaceURI = null;

	/**
	 * The attribute name.
	 */
	private String name = null;

	/**
	 * The attribute value.
	 */
	private String value = null;

	/**
	 * This method returns the attribute name.
	 * 
	 * @return The attribute name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * This method sets the attribute name.
	 * 
	 * @param name
	 *            The attribute name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * This method returns the attribute namespace URI.
	 * 
	 * @return The attribute namespace URI.
	 */
	public String getNamespaceURI() {
		return namespaceURI;
	}

	/**
	 * This method sets the attribute namespace URI.
	 * 
	 * @param namespaceURI
	 *            The attribute namespace URI.
	 */
	public void setNamespaceURI(String namespaceURI) {
		this.namespaceURI = namespaceURI;
	}

	/**
	 * This method returns the attribute value.
	 * 
	 * @return The attribute value.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * This method sets the attribute value.
	 * 
	 * @param value
	 *            The attribute value.
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
