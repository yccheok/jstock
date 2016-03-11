package it.sauronsoftware.feed4j.bean;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class is used to represent a XML element.
 * 
 * @author Carlo Pelliccia
 * 
 */
public class RawElement implements RawNode {

	/**
	 * The element namespace URI.
	 */
	private String namespaceURI = null;

	/**
	 * The element name.
	 */
	private String name = null;

	/**
	 * The element value.
	 */
	private String value = null;

	/**
	 * The element attributes.
	 */
	private ArrayList attributes = null;

	/**
	 * The element nodes.
	 */
	private ArrayList nodes = null;

	/**
	 * This method returns the element name.
	 * 
	 * @return The element name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * This method sets the element name.
	 * 
	 * @param name
	 *            The element name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * This method returns the element namespace URI.
	 * 
	 * @return The element namespace URI.
	 */
	public String getNamespaceURI() {
		return namespaceURI;
	}

	/**
	 * This method sets the element namespace URI.
	 * 
	 * @param namespaceURI
	 *            The element namespace URI.
	 */
	public void setNamespaceURI(String namespaceURI) {
		this.namespaceURI = namespaceURI;
	}

	/**
	 * This method returns the element value.
	 * 
	 * @return The element value.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * This method sets the element value.
	 * 
	 * @param value
	 *            The element value.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * This method returns an element attribute.
	 * 
	 * @param index
	 *            The index of the wanted attribute.
	 * @return The element attribute.
	 * @throws IndexOutOfBoundsException
	 *             If the supplied index is not valid. It must be greater or
	 *             equal to 0 and less than the value returned by
	 *             getAttributeCount().
	 */
	public RawAttribute getAttribute(int index)
			throws IndexOutOfBoundsException {
		if (attributes == null) {
			throw new IndexOutOfBoundsException();
		} else {
			return (RawAttribute) attributes.get(index);
		}
	}

	/**
	 * This method adds an attribute to the element.
	 * 
	 * @param attribute
	 *            The attribute.
	 */
	public void addAttribute(RawAttribute attribute) {
		if (attributes == null) {
			attributes = new ArrayList();
		}
		attributes.add(attribute);
	}

	/**
	 * This method returns the number of the element attribute.
	 * 
	 * @return The number of the element attribute.
	 */
	public int getAttributeCount() {
		if (attributes == null) {
			return 0;
		} else {
			return attributes.size();
		}
	}

	/**
	 * This method returns the value of an element attribute.
	 * 
	 * @param namespaceURI
	 *            The attribute namespace URI.
	 * @param name
	 *            The attribute name.
	 * @return The value of the attribute, or null if it could not be found.
	 */
	public String getAttributeValue(String namespaceURI, String name) {
		if (attributes == null) {
			return null;
		} else {
			for (Iterator i = attributes.iterator(); i.hasNext();) {
				RawAttribute aux = (RawAttribute) i.next();
				if (aux.getNamespaceURI().equals(namespaceURI)
						&& aux.getName().equals(name)) {
					return aux.getValue();
				}
			}
			return null;
		}
	}

	/**
	 * This method returns an element node.
	 * 
	 * @param index
	 *            The index of the wanted node.
	 * @return The node.
	 * @throws IndexOutOfBoundsException
	 *             If the supplied index is not valid. It must be greater or
	 *             equal to 0 and less than the value returned by
	 *             getNodeCount().
	 */
	public RawNode getNode(int index) throws IndexOutOfBoundsException {
		if (nodes == null) {
			throw new IndexOutOfBoundsException();
		} else {
			return (RawNode) nodes.get(index);
		}
	}

	/**
	 * This method returns a set of sub-elements.
	 * 
	 * @param namespaceURI
	 *            The namespace URI of the wanted elements.
	 * @param name
	 *            The name of the wanted elements.
	 * @return An array with the wanted elements. If no element is found, then a
	 *         zero-size array is returned.
	 */
	public RawElement[] getElements(String namespaceURI, String name) {
		if (nodes == null) {
			return new RawElement[0];
		} else {
			ArrayList list = new ArrayList();
			for (Iterator i = nodes.iterator(); i.hasNext();) {
				RawNode node = (RawNode) i.next();
				if (node instanceof RawElement) {
					RawElement aux = (RawElement) node;
					if (aux.getNamespaceURI().equals(namespaceURI)
							&& aux.getName().equals(name)) {
						list.add(aux);
					}
				}
			}
			int size = list.size();
			RawElement[] ret = new RawElement[size];
			for (int i = 0; i < size; i++) {
				ret[i] = (RawElement) list.get(i);
			}
			return ret;
		}
	}

	/**
	 * This method returns the first occurrence of a wanted sub-element.
	 * 
	 * @param namespaceURI
	 *            The namespace URI of the wanted element.
	 * @param name
	 *            The name of the wanted element.
	 * @return The first occurrence of a wanted sub-element, or null if it could
	 *         not be found.
	 */
	public RawElement getElement(String namespaceURI, String name) {
		if (nodes == null) {
			return null;
		} else {
			for (Iterator i = nodes.iterator(); i.hasNext();) {
				RawNode node = (RawNode) i.next();
				if (node instanceof RawElement) {
					RawElement aux = (RawElement) node;
					if (aux.getNamespaceURI().equals(namespaceURI)
							&& aux.getName().equals(name)) {
						return aux;
					}
				}
			}
			return null;
		}
	}

	/**
	 * This method returns the value of first occurrence of a wanted
	 * sub-element.
	 * 
	 * @param namespaceURI
	 *            The namespace URI of the wanted element.
	 * @param name
	 *            The name of the wanted element.
	 * @return The value first occurrence of a wanted sub-element, or null if
	 *         the element could not be found or if it exists but its value is
	 *         not textual.
	 */
	public String getElementValue(String namespaceURI, String name) {
		if (nodes == null) {
			return null;
		} else {
			RawElement element = getElement(namespaceURI, name);
			if (element != null) {
				return element.getValue();
			} else {
				return null;
			}
		}
	}

	/**
	 * This method adds a node to the element.
	 * 
	 * @param node
	 *            The node.
	 */
	public void addNode(RawNode node) {
		if (nodes == null) {
			nodes = new ArrayList();
		}
		nodes.add(node);
	}

	/**
	 * This method returns the number of the nodes in the element.
	 * 
	 * @return The number of the nodes in the element.
	 */
	public int getNodeCount() {
		if (nodes == null) {
			return 0;
		} else {
			return nodes.size();
		}
	}

	/**
	 * This method removes a node from the element.
	 * 
	 * @param node
	 *            The node to remove.
	 */
	public void removeNode(RawNode node) {
		if (nodes != null) {
			nodes.remove(node);
			if (nodes.size() == 0) {
				nodes = null;
			}
		}
	}

}
