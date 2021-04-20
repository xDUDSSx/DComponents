package net.dudss.dcomponents.components;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;

public abstract class DDialog extends JDialog {
	JComponent overlay = null;
	float overlayOpacity;
	Color overlayColor;
	
	public DDialog(Frame owner) {
		this(owner, true);
	}

	public DDialog(Frame owner, boolean modal) {
		this(owner, "", modal);
	}

	public DDialog(Frame owner, String title) {
		this(owner, title, true);
	}

	public DDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal ? Dialog.ModalityType.APPLICATION_MODAL : Dialog.ModalityType.MODELESS);
	}
	
	public DDialog(Dialog owner) {
		this(owner, true);
	}

	public DDialog(Dialog owner, boolean modal) {
		this(owner, "", modal);
	}

	public DDialog(Dialog owner, String title) {
		this(owner, title, true);
	}

	public DDialog(Dialog owner, String title, boolean modal) {
		super(owner, title, modal ? Dialog.ModalityType.APPLICATION_MODAL : Dialog.ModalityType.MODELESS);
	}
	
	public DDialog(Window owner) {
		this(owner, true);
	}

	public DDialog(Window owner, boolean modal) {
		this(owner, "", modal);
	}

	public DDialog(Window owner, String title) {
		this(owner, title, true);
	}

	public DDialog(Window owner, String title, boolean modal) {
		super(owner, title, modal ? Dialog.ModalityType.APPLICATION_MODAL : Dialog.ModalityType.MODELESS);
	}
	
	@Override
	protected void dialogInit() {
		super.dialogInit();
		installEscapeCloseOperation(this);
	}
	
	/**
	 * Makes the dialog replace its parents glass pane with a transparent overlay of a certain color.
	 * By default black with 25% opacity is used.
	 * The overlay appears when the dialog is set to visible and disappears when its closed or set invisible.
	 * 
	 * Note that the dialog changes the parents glass pane and discards the original.
	 * Currently only works with JFrames as parents and only with modal dialogs.
	 */
	public void installOverlay() {
		installOverlay(Color.BLACK, 0.25f);
	}
	
	/**
	 * Makes the dialog replace its parents glass pane with a transparent overlay of a certain color.
	 * The overlay appears when the dialog is set to visible and disappears when its closed or set invisible.
	 * 
	 * Note that the dialog changes the parents glass pane and discards the original.
	 * Currently only works with JFrames as parents and only with modal dialogs.
	 * 
	 * @param color Color of the overlay (default is Color.BLACK)
	 * @param opacity Transparency of the overlay (default is 0.25f)
	 */
	public void installOverlay(Color color, float opacity) {
		overlayOpacity = opacity;
		overlayColor = color;
		
		if (this.getModalityType() == ModalityType.MODELESS) {
			//System.err.println("DComponents - DDialog must be modal to install overlay!");
			//return;
		}
		
		if (this.getParent() instanceof JFrame) {
			JFrame parentFrame = (JFrame) this.getParent();
			if (!parentFrame.getGlassPane().isVisible()) {
				overlay = new JComponent() {
					AlphaComposite alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, overlayOpacity);
					
					@Override
					protected void paintComponent(Graphics g) {
						super.paintComponent(g);
						Graphics2D g2d = (Graphics2D) g;
						g2d.setColor(overlayColor);
						g2d.setComposite(alcom);
						g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
					}
				};
				
				this.addWindowListener(new WindowAdapter() {
		            @Override
		            public void windowClosing(WindowEvent e) {
		                hideOverlay();
		            }
		        });
			}
		}
	}
	
	@Override
	public void setVisible(boolean b) {
		if (b) {
			showOverlay();
		} else {
			hideOverlay();
		}
		super.setVisible(b);
	}
	
	protected void showOverlay() {
		if (overlay != null) {
			if (this.getParent() instanceof JFrame) {
				JFrame parentFrame = (JFrame) this.getParent();
				parentFrame.setGlassPane(overlay);
			}
			overlay.setVisible(true);
		}
	}
	
	protected void hideOverlay() {
		if (overlay != null) {
			overlay.setVisible(false);
		}
	}

	protected void initUI() {	
		JPanel headerWrapper = new JPanel(new BorderLayout());
		JPanel contentWrapper = new JPanel(new BorderLayout());
		JPanel buttonWrapper = new JPanel(new BorderLayout());

		Component headerPanel = createHeaderPanel();
		Component contentPanel = createContentPanel();
		Component buttonPanel = createButtonPanel();

		getContentPane().setLayout(new BorderLayout());

		if (headerPanel != null) {
			headerWrapper.add(headerPanel);
			headerWrapper.setBorder(new MatteBorder(0, 0, 1, 0, UIManager.getColor("Separator.foreground")));
			getContentPane().add(headerWrapper, BorderLayout.NORTH);
		}
		if (contentPanel != null) {
			contentWrapper.add(contentPanel);
			getContentPane().add(contentWrapper, BorderLayout.CENTER);
		}
		if (buttonPanel != null) {
			buttonWrapper.add(buttonPanel);
			buttonWrapper.setBorder(new MatteBorder(1, 0, 0, 0, UIManager.getColor("Separator.foreground")));
			getContentPane().add(buttonWrapper, BorderLayout.SOUTH);
		}
	}
	
	private static final KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
	public static final String dispatchWindowClosingActionMapKey = "net.dudss.dcomponents:WINDOW_CLOSING";

	protected static void installEscapeCloseOperation(final JDialog dialog) {
		Action dispatchClosing = new AbstractAction() {
			public void actionPerformed(ActionEvent event) {
				dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
			}
		};
		JRootPane root = dialog.getRootPane();
		root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, dispatchWindowClosingActionMapKey);
		root.getActionMap().put(dispatchWindowClosingActionMapKey, dispatchClosing);
	}

	abstract public Component createHeaderPanel();

	abstract public Component createContentPanel();

	abstract public Component createButtonPanel();

}
