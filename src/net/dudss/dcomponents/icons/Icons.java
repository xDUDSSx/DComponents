package net.dudss.dcomponents.icons;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;

import com.formdev.flatlaf.extras.FlatSVGIcon;

public class Icons {
	public static FlatSVGIcon ok;
	
	public static FlatSVGIcon spinner;
	public static List<RotatedIcon> spinnerAnim;
	
	static {
		ok = new FlatSVGIcon("res/ok.svg", 16, 16);
		spinner = new FlatSVGIcon("res/spinner2.svg", 16, 16);
		spinnerAnim = generateSpinnerAnim();
	}
	
	public static Icon createSpinnerIcon(JComponent comp) {
		AnimatedIcon icon = new AnimatedIcon(comp, 1000/24);
		for (Icon f : spinnerAnim) {
			icon.addIcon(f);
		}
		icon.start();
		return icon;
	}
	
	private static List<RotatedIcon> generateSpinnerAnim() {
		List<RotatedIcon> list = new ArrayList<>();
		for (int angle = 15; angle < 360; angle += 15)
		{
			list.add(new RotatedIcon(Icons.spinner, angle, true));
		}
		return list;
	}
}
