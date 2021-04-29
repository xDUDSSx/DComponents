package net.dudss.dcomponents;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class FrameCreator {
   private static int size = 200;

   public static void initLAF() throws UnsupportedLookAndFeelException {
      UIManager.setLookAndFeel(new FlatIntelliJLaf());
      FlatLaf.setUseNativeWindowDecorations(FlatLaf.supportsNativeWindowDecorations());

      UIManager.put("TitlePane.unifiedBackground", false);
   }
   public static void main(String[] args) throws InvocationTargetException, InterruptedException {
      SwingUtilities.invokeAndWait(() -> {
         try {
            initLAF();
         } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
         }
      });
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      double width = screenSize.getWidth();
      double height = screenSize.getHeight();
         for (int i = 0; i < width / size; i++) {
            for (int j = 0; j < height / size; j++) {
               showFrame(i,j);
            }
      }
   }

   public static void showFrame(int i, int j) {
      SwingUtilities.invokeLater(() -> {
         JFrame frame = new JFrame();
         frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
         frame.setSize(size - 10, size - 10);
         frame.setLocation(i * size + 10, j * size + 10);
         frame.setVisible(true);
      });
   }
}