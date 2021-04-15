package net.dudss.dcomponents.demo.demo.panellist;

public class SampleItem {
	private static int counter = 0;
	
	public String name;
	public int index;
	
	public SampleItem(String name) {
		this.name = name;
		index = counter++;
	}
}
