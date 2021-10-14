package net.dudss.dcomponents.renderers;


import net.dudss.dcomponents.data.Triplet;

import javax.swing.*;
import java.awt.*;

public class IconStringObjectPairListCellRenderer extends DefaultListCellRenderer {
	@SuppressWarnings("rawtypes")
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value instanceof Triplet) {
			Triplet pair = (Triplet) value;
			JLabel label = (JLabel) comp;
			Icon icon = (Icon) pair.getL();
			Object text = pair.getM();
			if (text != null) 
				label.setText(text.toString());
			else
				label.setText(pair.getR().toString());
			if (icon != null) label.setIcon(icon);
		}
		return comp;
	}
}
