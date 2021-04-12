package net.dudss.dcomponents.cell.table;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import net.dudss.dcomponents.data.Pair;

/**
 * A Cell Renderer that renders the value as text.
 * Calls the .toString() method to get display text.
 * 
 * An Icon object value will be displayed as an image.
 *
 * 
 * The renderer can also display an icon next to the text.
 * There are two ways to pass the icon to the renderer.
 * 
 * Either the value can be a Pair of an Icon and an Object.
 * The expected object is Pair<Icon, Object>.
 * 
 * Or an icon can be passed to the renderers constructor.
 * 
 */
public class IconLabelRenderer extends LabelRenderer {
	protected  Icon icon;
	
	public IconLabelRenderer() {
		this(SwingConstants.LEADING);
	}
	
	public IconLabelRenderer(int alignment) {
		this(null, alignment);
	}
	
	public IconLabelRenderer(Icon icon) {
		this(icon, SwingConstants.LEADING);
	}
	
	public IconLabelRenderer(Icon icon, int alignment) {
		super(alignment);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component comp;
		if (value instanceof Pair && ((Pair) value).getL() instanceof Icon) {
			comp = super.getTableCellRendererComponent(table, ((Pair) value).getR(), isSelected, hasFocus, row, column);
			((JLabel) comp).setIcon((Icon) ((Pair) value).getL());
		} else if (value instanceof Icon) {
			comp = super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
			((JLabel) comp).setIcon((Icon) value);
		} else {
			comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
		return comp;
	}
}
