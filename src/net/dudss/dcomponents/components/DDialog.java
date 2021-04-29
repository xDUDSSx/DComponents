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
import java.awt.event.ActionListener;
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
import javax.swing.Timer;

/**
 * An extended {@linkplain JDialog} that separates content into 3 parts.
 * - Header
 * - Content
 * - Buttons
 * 
 * These parts can be constructed in their respective abstract methods.
 * In order to allow member variable initialization it is <b>required</b> to call the protected method {@link #initUI()}
 * in the subclass constructor to create the dialog contents.
 * 
 * Additionally:
 * - The dialog can be closed using the ESC key.
 * - The dialog has the ability to overwrite it's parents window glasspane to display a transparent overlay when the dialog is visible.
 *   This can create a sort of a internal dialog feeling without actually being internal. See the {@link #installOverlay()} method,
 *   it should be called after creating the dialog.
 *  
 * UIManager:
 * The following defaults can be changed
 * DDialog.overlayOpacity (Float)
 * DDialog.overlayColor¨  (Color)
 * 
 * @author DUDSS 2021
 *
 */
public abstract class DDialog extends JDialog {
	JComponent overlay = null;
	float overlayOpacity;
	Color overlayColor;
	
	boolean animationEnabled;
	int animationDelay;
	int animationLength;
	float realOverlayOpacity;
	Timer timer;
	
	public static final String overlayOpacityKey = "DDialog.overlayOpacity";
	public static final String overlayColorKey = "DDialog.overlayColor";
	public static final String overlayAnimationKey = "DDialog.overlayAnimation";
	public static final String overlayAnimationFpsKey = "DDialog.overlayAnimationFps";
	public static final String overlayAnimationLengthKey = "DDialog.overlayAnimationLength";
	
	
	public DDialog() {
		this((Frame) null, false);
	}
	
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
	
	abstract public Component createHeaderPanel();

	abstract public Component createContentPanel();

	abstract public Component createButtonPanel();
	
	/**
	 * Constructs the dialog's content. Necessary to call from the subclass constructor.
	 */
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
	
	@Override
	protected void dialogInit() {
		super.dialogInit();
		installDefaults();
		installEscapeCloseOperation(this);
	}
	
	private void installDefaults() {
		if (UIManager.get(overlayOpacityKey) == null) UIManager.put(overlayOpacityKey, 0.25f);
		if (UIManager.get(overlayColorKey) == null) UIManager.put(overlayColorKey, Color.BLACK);
		if (UIManager.get(overlayAnimationKey) == null) UIManager.put(overlayAnimationKey, true);
		if (UIManager.get(overlayAnimationFpsKey) == null) UIManager.put(overlayAnimationFpsKey, 30);
		if (UIManager.get(overlayAnimationLengthKey) == null) UIManager.put(overlayAnimationLengthKey, 8);
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
		installOverlay(	UIManager.getColor(overlayColorKey),
						(Float) UIManager.get(overlayOpacityKey),
						UIManager.getBoolean(overlayAnimationKey),
						UIManager.getInt(overlayAnimationFpsKey),
						UIManager.getInt(overlayAnimationLengthKey)
		);
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
	 * @param animationEnabled Enable / Disable animated overlay transition
	 * @param animationFps Animated overlay transition fps
	 * @param animationLength Animated overlay transition length in frames
	 */
	public void installOverlay(Color color, float opacity, boolean animationEnabled, int animationFps, int animationLength) {
		this.overlayOpacity = opacity;
		this.overlayColor = color;
		this.animationEnabled = animationEnabled;
		this.animationDelay = (int) (1000f/animationFps);
		this.animationLength = animationLength;
		
		if (this.getModalityType() == ModalityType.MODELESS) {
			System.err.println("DComponents - DDialog must be modal to install overlay!");
			return;
		}
		
		if (this.getParent() instanceof JFrame) {
			JFrame parentFrame = (JFrame) this.getParent();
			if (!parentFrame.getGlassPane().isVisible()) {
				overlay = new JComponent() {			
					@Override
					protected void paintComponent(Graphics g) {
						super.paintComponent(g);
						Graphics2D g2d = (Graphics2D) g;
						g2d.setColor(overlayColor);
						g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, animationEnabled ? realOverlayOpacity : overlayOpacity));
						g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
					}
				};
				
				this.addWindowListener(new WindowAdapter() {
		            @Override
		            public void windowClosing(WindowEvent e) {
		            	onCloseImpl();
		            }
		            
		            @Override
		            public void windowClosed(WindowEvent e) {
		            	onCloseImpl();
		            }
		        });
			}
		}
	}
	
	protected void onCloseImpl() {
		hideOverlay();
		onClose();
	}
	
	/**
	 * Called on dialog close.
	 */
	public abstract void onClose();
	
	@Override
	public void dispose() {
		super.dispose();
		onCloseImpl();
	}
	
	@Override
	public void setVisible(boolean b) {
		if (b) {
			showOverlay();
		} else {
			onCloseImpl();
		}
		super.setVisible(b);
	}
	
	protected void showOverlay() {
		if (overlay != null) {
			if (this.getParent() instanceof JFrame) {
				JFrame parentFrame = (JFrame) this.getParent();
				parentFrame.setGlassPane(overlay);
			}
			if (animationEnabled) fadeIn(overlay);
			overlay.setVisible(true);
		}
	}
	
	protected void hideOverlay() {
		if (overlay != null) {
			if (animationEnabled)
				fadeOut(overlay);
			else
				overlay.setVisible(false);
		}
	}

	/**
	 * Fade in the overlay opacity.
	 */
    public void fadeIn(JComponent overlay) {
    	if (timer != null) timer.stop();
		timer = new Timer(animationDelay, null);
		timer.setRepeats(true);
		timer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				realOverlayOpacity += overlayOpacity / animationLength;
				if (realOverlayOpacity < 0) {
					realOverlayOpacity = 0;
				}
				if (realOverlayOpacity > overlayOpacity) {
					realOverlayOpacity = overlayOpacity;
					timer.stop();
				}
				overlay.repaint();
			}
		});
		timer.start();
    }
    
    /**
	 * Fade out the overlay opacity.
	 */ 
    public void fadeOut(JComponent overlay) {
    	if (timer != null) timer.stop();
    	timer = new Timer(animationDelay, null);
		timer.setRepeats(true);
		timer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				realOverlayOpacity -= overlayOpacity / animationLength;
				if (realOverlayOpacity < 0) {
					realOverlayOpacity = 0;
					timer.stop();
					overlay.setVisible(false);
				}
				if (realOverlayOpacity > overlayOpacity) {
					realOverlayOpacity = overlayOpacity;
				}
				overlay.repaint();
			}
		});
		timer.start();
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

}
