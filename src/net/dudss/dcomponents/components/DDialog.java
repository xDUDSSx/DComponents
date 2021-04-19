package net.dudss.dcomponents.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;

public abstract class DDialog extends JDialog {

    public DDialog() {
        this((Frame)null, false);
    }

    public DDialog(Frame owner) {
        this(owner, false);
    }

    public DDialog(Frame owner, boolean modal) {
        this(owner, "", modal);
    }

    public DDialog(Frame owner, String title) {
        this(owner, title, false);
    }

    public DDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        initUI();
    }

    public DDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        initUI();
    }
    
    public DDialog(Dialog owner) {
        this(owner, false);
    }

    public DDialog(Dialog owner, boolean modal) {
        this(owner, "", modal);
    }


    public DDialog(Dialog owner, String title) {
        this(owner, title, false);
    }

    public DDialog(Dialog owner, String title, boolean modal) {
        super(owner, title, modal);
        initUI();
    }

    public DDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        initUI();
    }

    public DDialog(Window owner) {
        this(owner, Dialog.ModalityType.MODELESS);
    }

    public DDialog(Window owner, ModalityType modalityType) {
        this(owner, "", modalityType);
    }

    public DDialog(Window owner, String title) {
        this(owner, title, Dialog.ModalityType.MODELESS);
    }

    public DDialog(Window owner, String title, Dialog.ModalityType modalityType) {
        super(owner, title, modalityType);
        initUI();
    }

    public DDialog(Window owner, String title, Dialog.ModalityType modalityType,
                   GraphicsConfiguration gc) {
        super(owner, title, modalityType, gc);
        initUI();
    }
    
    private void initUI() {
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
    		headerWrapper.setBorder(new MatteBorder(1, 0, 0, 0, UIManager.getColor("Separator.foreground")));
    		getContentPane().add(buttonWrapper, BorderLayout.SOUTH);
    	}
    }
    
    abstract public Component createHeaderPanel();
    
    abstract public Component createContentPanel();
    
    abstract public Component createButtonPanel();
    
}
