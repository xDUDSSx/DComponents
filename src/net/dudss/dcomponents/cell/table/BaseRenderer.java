package net.dudss.dcomponents.cell.table;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public abstract class BaseRenderer extends AbstractCellEditor implements TableCellRenderer {
	int row;
	int column;
	
	Object value; // Need to preserve value during edit events
	
	JLabel invalidLabel = new JLabel("<invalid>");
	
	public BaseRenderer() {
		invalidLabel.setOpaque(true);
	}

	@Override
	abstract public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column);
	
	@Override
	public Object getCellEditorValue() {
		return null;
	}
	
	protected void updateData(JTable table, Object value, boolean isSelected, int row, int column) {
		this.row = row;
		this.column = column;
		this.value = value;
	}
}