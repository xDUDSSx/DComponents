package net.dudss.dcomponents.cell.table;

import net.dudss.dcomponents.cell.DCells;

import java.awt.Component;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;

public abstract class BaseRenderer extends AbstractCellEditor implements TableCellRenderer {
	int row;
	int column;
	
	Object value; // Need to preserve value during edit events

	JLabel invalidLabel = new JLabel("<invalid>");
	boolean drawFocus;

	public BaseRenderer() {
		this(true);
	}

	public BaseRenderer(boolean drawFocus) {
		this.drawFocus = drawFocus;
		invalidLabel.setOpaque(true);
	}

	@Override
	abstract public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column);
	
	@Override
	public Object getCellEditorValue() {
		return null;
	}

	protected void configureComponentForTable(JComponent comp, JTable table, boolean isSelected, boolean hasFocus, int row, int column) {
		DCells.configureTable(comp, table, isSelected, drawFocus ? hasFocus : false, row, column);
	}

	protected void updateData(JTable table, Object value, boolean isSelected, int row, int column) {
		this.row = row;
		this.column = column;
		this.value = value;
	}

	public void setDrawFocus(boolean drawFocus) {
		this.drawFocus = drawFocus;
	}

	public boolean getDrawFocus() {
		return drawFocus;
	}
}