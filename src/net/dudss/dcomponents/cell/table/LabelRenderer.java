package net.dudss.dcomponents.cell.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import net.dudss.dcomponents.cell.DCells;

/**
 * A Cell Renderer that renders the value as a string.
 * Calls the .toString() method to get display text. 
 */
public class LabelRenderer extends BaseRenderer {
	protected JLabel label;
	
	public LabelRenderer() {
		this(SwingConstants.CENTER);
	}
	
	public LabelRenderer(int alignment) {
		super();
		
		label = new JLabel("");
		label.setOpaque(true);
		label.setHorizontalAlignment(alignment);
		label.setBackground(Color.red);
	}
	
	public JLabel getLabel() {
		return label;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		super.updateData(table, value, isSelected, row, column);
		super.configureComponentForTable(label, table, isSelected, hasFocus, row, column);
		if (value == null) {
			label.setText("");
		} else {
			label.setText(value.toString());
		}
		return label;
	}
}