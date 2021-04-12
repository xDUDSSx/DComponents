package net.dudss.dcomponents.cell.table;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;

import net.dudss.dcomponents.DUtils;
import net.dudss.dcomponents.cell.DCells;

public class PercentageRendererEditor extends LabelRenderer implements TableCellEditor {
	protected JTextField field;
	
	public PercentageRendererEditor() {
		super();
		
		field = new JTextField();
		field.setOpaque(true);
		field.setHorizontalAlignment(SwingConstants.CENTER);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		updateData(table, value, isSelected, row, column);
		if (value instanceof Integer) {
			DCells.configureTable(label, table, isSelected, hasFocus, row, column);
			label.setText(value.toString() + " %");
			return label;
		} else {
			return invalidLabel;
		}
	}
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		updateData(table, value, isSelected, row, column);
		if (value instanceof Integer) {
			//DTableCellUtils.configure(field, table, isSelected, false, row, column); //Looks better as just a regular textfield
			field.setText(value.toString());
			return field;
		} else {
			return invalidLabel;
		}
	}
	
	@Override
	public Object getCellEditorValue() {
		if (value instanceof Integer) {
			if (DUtils.isIntegerFast(field.getText())) {
				int editorValue = Integer.parseInt(field.getText());
				if (editorValue < 0) editorValue = 0;
				if (editorValue > 100) editorValue = 100;
				return editorValue;
			}
		}
		return value;
	}
	
	@Override
	protected void updateData(JTable table, Object value, boolean isSelected, int row, int column) {
		super.updateData(table, value, isSelected, row, column);
	}
}