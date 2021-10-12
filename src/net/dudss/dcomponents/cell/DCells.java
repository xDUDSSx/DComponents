package net.dudss.dcomponents.cell;

import net.dudss.dcomponents.misc.HSLColor;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class DCells {
	private static final Border SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
    private static final Border DEFAULT_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
    protected static Border noFocusBorder = DEFAULT_NO_FOCUS_BORDER;
    
    public static final String INVALID = "<invalid>";
    
	public static void configureTable(JComponent comp, JTable table, boolean isSelected, boolean hasFocus, int row, int column) {
		configureTableColors(comp, table, isSelected, hasFocus, row, column);
		
		comp.setFont(table.getFont());

        if (hasFocus) {
            Border border = null;
            if (isSelected) {
                border = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");
            }
            if (border == null) {
                border = UIManager.getBorder("Table.focusCellHighlightBorder");
            }
            comp.setBorder(border);

            if (!isSelected && table.isCellEditable(row, column)) {
                Color col;
                col = UIManager.getColor("Table.focusCellForeground");
                if (col != null) {
                	comp.setForeground(col);
                }
                col = UIManager.getColor("Table.focusCellBackground");
                if (col != null) {
                	comp.setBackground(col);
                }
            }
        } else {
        	comp.setBorder(getNoFocusBorder());
        }
	}

    public static void configureTableColors(JComponent comp, JTable table, boolean isSelected, boolean hasFocus, int row, int column) {
        configureTableColors(comp, table, isSelected, hasFocus, row, column, false);
    }

    public static void configureTableColors(JComponent comp, JTable table, boolean isSelected, boolean hasFocus, int row, int column, boolean muted) {
	    if (isSelected) {
            comp.setBackground(table.getSelectionBackground());
            comp.setForeground(table.getSelectionForeground());
	    } else {
            comp.setBackground(table.getBackground());
            comp.setForeground(table.getForeground());
        }

        if (muted) {
            HSLColor c = new HSLColor(comp.getForeground());
            if (c.getLuminance() > 0.5f) {
                comp.setForeground(c.adjustLuminance(85));
            } else {
                comp.setForeground(c.adjustLuminance(50));
            }
        }
    }
	
	protected static Border getNoFocusBorder() {
        Border border = UIManager.getBorder("Table.cellNoFocusBorder");
        if (System.getSecurityManager() != null) {
            if (border != null) return border;
            return SAFE_NO_FOCUS_BORDER;
        } else if (border != null) {
            if (noFocusBorder == null || noFocusBorder == DEFAULT_NO_FOCUS_BORDER) {
                return border;
            }
        }
        return noFocusBorder;
    }
}
