package net.dudss.dcomponents.cell.table;

import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTable;

import net.dudss.dcomponents.cell.DCells;
import net.miginfocom.swing.MigLayout;

public class PanelRenderer extends BaseRenderer {
	boolean centered = false;
	
	protected JPanel panel;
	private JPanel topLevelPanel;
	
	public PanelRenderer() {
		this(false);
	}
	
	public PanelRenderer(boolean centered) {
		super();
		this.centered = centered;
		
		panel = new JPanel();
		
		if (centered) {
			topLevelPanel = new JPanel(new MigLayout("insets 0", "[grow]", "[grow]"));
			topLevelPanel.add(panel, "cell 0 0, center");	
		}
	}
	
	public JPanel getPanel() {
		return panel;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		super.updateData(table, value, isSelected, row, column);
		if (centered) {
			DCells.configureTable(topLevelPanel, table, isSelected, hasFocus, row, column);
			panel.setOpaque(false);
		} else { 
			DCells.configureTable(panel, table, isSelected, false, row, column);
		}
		return centered ? topLevelPanel : panel;
	}
}
