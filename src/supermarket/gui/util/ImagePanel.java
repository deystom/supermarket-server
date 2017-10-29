package supermarket.gui.util;
import java.awt.*;
import javax.swing.*;
/**
 *
 * @author MUSTAFA
 */

public class ImagePanel extends JPanel {

  private Image img;
/*
  public ImagePanel(String img) {
    this(new ImageIcon(img).getImage());
  }

 * 
 */
  public ImagePanel(){

  }

  public ImagePanel(Image img) {
    this.img = img;
    Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
    setPreferredSize(size);
    //
    MyLookAndFeel.setLook();
    //
    setMinimumSize(size);
    setMaximumSize(size);
    setSize(size);
    setLayout(null);
  }

  public void setImage(Image img) {
    this.img = img;
    Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
    setPreferredSize(size);
    //
    MyLookAndFeel.setLook();
    //
    setMinimumSize(size);
    setMaximumSize(size);
    setSize(size);
    setLayout(null);
  }
  
    @Override
  public void paintComponent(Graphics g) {
    g.drawImage(img, 0, 0, null);
  }
}