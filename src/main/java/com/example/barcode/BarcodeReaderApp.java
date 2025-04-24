package com.example.barcode;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class BarcodeReaderApp {

  public static void main(String[] args) {
    SwingUtilities.invokeLater(BarcodeReaderApp::createAndShowGUI);
  }

  private static void createAndShowGUI() {
    JFrame frame = new JFrame("Barcode Reader");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setSize(600, 400);
    frame.setLayout(new BorderLayout());

    JLabel imageLabel = new JLabel("Paste an image from the clipboard", SwingConstants.CENTER);
    imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
    imageLabel.setVerticalAlignment(SwingConstants.CENTER);
    imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

    JTextField resultField = new JTextField("Decoded content will appear here");
    resultField.setHorizontalAlignment(SwingConstants.CENTER);
    resultField.setEditable(false); // Make it non-editable
    resultField.setBorder(BorderFactory.createLineBorder(Color.BLACK));

    // Create a popup menu for the resultField
    JPopupMenu popupMenu = new JPopupMenu();
    JMenuItem copyMenuItem = new JMenuItem("Copy");
    popupMenu.add(copyMenuItem);

    // Add action listener to the "Copy" menu item
    copyMenuItem.addActionListener(e -> {
      String selectedText = resultField.getSelectedText();
      if (selectedText != null && !selectedText.isEmpty()) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
            new StringSelection(selectedText), null);
      }
    });

    // Add mouse listener to show the popup menu on right-click
    resultField.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
          popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
          popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
      }
    });

    JButton pasteButton = new JButton("Paste Image from Clipboard");
    pasteButton.addActionListener(e -> pasteImageFromClipboard(imageLabel, resultField, frame));

    // Add KeyEventDispatcher for CMD+V or CTRL+V
    KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(event -> {
      if (event.getID() == KeyEvent.KEY_PRESSED) {
        boolean isPasteShortcut = (event.getKeyCode() == KeyEvent.VK_V) &&
            ((event.getModifiersEx() & InputEvent.META_DOWN_MASK) != 0 || // CMD on macOS
                (event.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0);  // CTRL on Windows/Linux
        if (isPasteShortcut) {
          pasteImageFromClipboard(imageLabel, resultField, frame);
          return true; // Consume the event
        }
      }
      return false; // Pass the event to other listeners
    });

    frame.add(imageLabel, BorderLayout.CENTER);
    frame.add(resultField, BorderLayout.SOUTH);
    frame.add(pasteButton, BorderLayout.NORTH);

    frame.setVisible(true);
  }

  // Helper method to handle pasting logic
  private static void pasteImageFromClipboard(JLabel imageLabel, JTextField resultField, JFrame frame) {
    BufferedImage image = getImageFromClipboard();
    if (image != null) {
      imageLabel.setIcon(new ImageIcon(image));
      String decodedText = decodeBarcode(image);
      resultField.setText(decodedText != null ? decodedText : "No barcode detected");
    } else {
      JOptionPane.showMessageDialog(frame, "No image found in clipboard", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private static BufferedImage getImageFromClipboard() {
    try {
      Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
      if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
        Object image = transferable.getTransferData(DataFlavor.imageFlavor);
        if (image instanceof BufferedImage bufferedImage) {
          return bufferedImage;
        } else if (image instanceof java.awt.image.MultiResolutionImage multiResolutionImage) {
          // Extract the default resolution image
          Image resolutionVariant = multiResolutionImage.getResolutionVariants().get(0);
          if (resolutionVariant instanceof BufferedImage bufferedImage) {
            return bufferedImage;
          }
        }
      }
    } catch (Exception e) {
      Logger.getLogger(BarcodeReaderApp.class.getName()).log(Level.SEVERE, "Error accessing clipboard", e);
    }
    return null;
  }

  private static String decodeBarcode(BufferedImage image) {
    try {
      LuminanceSource source = new BufferedImageLuminanceSource(image);
      BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
      Result result = new MultiFormatReader().decode(bitmap);
      return result.getText();
    } catch (NotFoundException e) {
      return null; // No barcode found
    }
  }
}