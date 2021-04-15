package net.dudss.dcomponents.demo.demo.panellist;

import javax.swing.JLabel;
import javax.swing.UIManager;

import net.dudss.dcomponents.components.panellist.DPanelListItem;
import net.miginfocom.swing.MigLayout;

public class SampleItemPanel extends DPanelListItem<SampleItem> {
	private JLabel lblNewLabel;

	public SampleItemPanel(SampleItem item) {
		super(item);
		
		initUI();
	}
	private void initUI() {
		setLayout(new MigLayout("", "[]", "[]"));
		
		lblNewLabel = new JLabel();
		add(lblNewLabel, "cell 0 0, grow");
	}
	
	@Override
	public void updateSelection(boolean selected) {
		super.updateSelection(selected);
		if (selected) {
			lblNewLabel.setForeground(getSelectionForegroundColor());
		} else {
			lblNewLabel.setForeground(UIManager.getColor("Label.foreground"));
		}
	}
	
	@Override
	public void updateComponents(boolean selected) {
		lblNewLabel.setText(this.object.name + " " + this.object.index);
	}

}
