package net.dudss.dcomponents.demo.demo.panellist;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import net.dudss.dcomponents.components.DSeparator;
import net.dudss.dcomponents.components.DSeparator.Style;
import net.dudss.dcomponents.components.panellist.DPanelList;
import net.dudss.dcomponents.components.panellist.DPanelList.SelectionMode;
import net.dudss.dcomponents.components.panellist.DPanelListFactory;
import net.miginfocom.swing.MigLayout;

public class DPanelListDemo extends JPanel {
	List<SampleItem> sampleItems = new ArrayList<>();
	DPanelList<SampleItem, SampleItemPanel> samplePanelList;
	
	public DPanelListDemo() {
		initUI();
	}
	
	private void initUI() {
		setLayout(new MigLayout("", "[grow]", "[][grow]"));
		
		add(new DSeparator(Style.VERTICAL, "DPanelList"), "cell 0 0, grow");
		
		samplePanelList = DPanelListFactory.listDesign(null, SampleItemPanel.class, SelectionMode.SELECTION_FORCED, true, true, null);
		add(samplePanelList, "cell 0 1, grow");
		
		samplePanelList.setList(sampleItems);
		
		sampleItems.add(new SampleItem("Item"));
		sampleItems.add(new SampleItem("Item"));
		sampleItems.add(new SampleItem("Item"));
		sampleItems.add(new SampleItem("Item"));
		
		samplePanelList.refresh();
	}
	
}
