/** SortableTreeTable.java                          PortfolioApp
 *
 * Created 23/12/2007 12:43:37 PM
 * 
 * @author Ray Turnbull
 */
package org.yccheok.jstock.gui.treetable;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.SortOrder;
import org.jdesktop.swingx.treetable.TreeTableModel;


/**
 * Subclassed to allow clicking on column headers to sort, and display sort
 * indicator in header.<br>
 * Also hacked to notify model of treetable(this) to allow expanded nodes to
 * be maintained, and sort indicators in header to be set if sort set or 
 * changed programmatically
 * <p>
 * Must be used with SortableTreeTableModel
 */
public class SortableTreeTable extends JXTreeTable {
	
	/**
     * 
     */
    private static final long serialVersionUID = 3670159528754017419L;
    private boolean started = false;
	private SortOrder order;
	private int sortColumn;
	private SortableTreeTableModel sortModel = null;
	private boolean suppress = false;
	
	/**
	 * 
	 */
	public SortableTreeTable() {
		super();
		started = true;
	}

	/**
	 * @param treeModel
	 */
	public SortableTreeTable(TreeTableModel treeModel) {
		super(treeModel);
		if (!(treeModel instanceof SortableTreeTableModel)) {
			throw new IllegalArgumentException("" +
					"Model must be a SortableTreeTableModel");
		}
		sortModel = (SortableTreeTableModel)treeModel;
		addTreeExpansionListener(sortModel);
		sortModel.setTreeTable(this);
		started = true;
	}
	
	// ==================================================== overridden methods
	
	@Override
	public boolean isSortable() {
		return true;
	}
	
	@Override
	public void resetSortOrder() {
		if (suppress) return;
		sortModel.setSortOrder(SortOrder.UNSORTED);
	}
	
	@Override
	public SortOrder getSortOrder(int colIndex) {
		// need this as method called from JXTable constructor and TreeTable
		// model not setup yet
		if (!started || sortModel == null) {			
			return SortOrder.UNSORTED;			
		}
		int x = convertColumnIndexToModel(colIndex);
		getSortParams();
		if (x != sortColumn) {
			return SortOrder.UNSORTED;
		} else {
			return order;
		}
	}
	
	@Override
    public void toggleSortOrder(int columnIndex) {
		int x = convertColumnIndexToModel(columnIndex);
		getSortParams();
		if (x == sortColumn) {
			sortModel.toggleSortOrder();
		} else {
			sortModel.setSortColumn(x);
		}
		
	}
	
	@Override
	public void setTreeTableModel(TreeTableModel treeModel) {
		TreeTableModel m = getTreeTableModel();
		SortableTreeTableModel old = null;
		if (m instanceof SortableTreeTableModel) {
			old = (SortableTreeTableModel) m;
		}
		super.setTreeTableModel(treeModel);
		if (!(treeModel instanceof SortableTreeTableModel)) {
			throw new IllegalArgumentException("" +
			"Model must be a SortableTreeTableModel");
		}
		sortModel = (SortableTreeTableModel)treeModel;
		if (old != null) {
			removeTreeExpansionListener(old);
		}
		addTreeExpansionListener(sortModel);
		sortModel.setTreeTable(this);
	}
	
    @Override
	protected boolean isSortable(int columnIndex) {
    	return true;
    }
    
    @Override
    public void setSortOrder(int columnIndex, SortOrder sortOrder) {
    	sortModel.setSortOptions(sortModel.getColumnName(columnIndex), order);
    }
    
	// ======================================================= private methods
	
	private void getSortParams() {
		if (sortModel == null) {
			throw new IllegalStateException("No TreeTable Model");
		}
		order = sortModel.getSortOrder();
		sortColumn = sortModel.getSortColumnIndex();
	}
	
}
