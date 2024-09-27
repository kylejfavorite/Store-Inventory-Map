import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;

// Printer implements the printable interface so that I can use print method
public class Printer implements Printable {
	
	// Field for buffered image necessary to be used by printer
	private BufferedImage bufferedImage;
	
	// Constructor for printer takes ImageIcon as argument
	// In program, ImageIcon would be the current store map image.
	public Printer(ImageIcon image) {
		
		// Create a BufferedImage from ImageIcon
		bufferedImage = new BufferedImage(image.getIconWidth(), image.getIconHeight(), BufferedImage.TYPE_INT_RGB);
		
		// Put graphics onto buffered image
        Graphics graphics = bufferedImage.createGraphics();
        image.paintIcon(null, graphics, 0, 0);
        graphics.dispose();

        // Open a print dialog
        PrinterJob printJob = PrinterJob.getPrinterJob();
        if (printJob.printDialog()) {
            printJob.setPrintable(this);
            try {
            	// Print buffered Image
                printJob.print();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
	}
	
	// Print method
	public int print(Graphics graphics, PageFormat format, int pageIndex) {
		
		// Check that page index is not zero
        if (pageIndex != 0)
            return NO_SUCH_PAGE;
        
        // place graphics
        Graphics2D g2 = (Graphics2D) graphics;
        
        // draw Image onto page using specifications
        g2.drawImage(bufferedImage, 0, 0, (int) format.getWidth(), (int) format.getHeight(), null);
        
        // Page is ready to be printed since it exists
        return PAGE_EXISTS;
    }

   
}
