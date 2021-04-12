package net.dudss.dcomponents.cell.table;

import java.awt.Component;
import java.time.LocalDateTime;

import javax.swing.JTable;

import net.dudss.dcomponents.Common;
import net.dudss.dcomponents.cell.DCells;

public class DateTimeCellRenderer extends LabelRenderer {
	public DateTimeCellRenderer() {
		super();
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		updateData(table, value, isSelected, row, column);
		label.setText(DCells.INVALID);
		if (value == null) {
			label.setText("");
		}
		if (value instanceof LocalDateTime) {
			label.setText(((LocalDateTime) value).format(Common.dateTimeFormatter));
		}
		return comp;
	}
	
	@Override
	protected void updateData(JTable table, Object value, boolean isSelected, int row, int column) {
		super.updateData(table, value, isSelected, row, column);
	}
}