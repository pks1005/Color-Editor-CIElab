
import java.awt.Dimension;

import javax.swing.JFrame;

public class ColorModel extends JFrame
{
	public static boolean first = true;
	ColorModel()
   {
      this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      this.setResizable( false );
      
      //Dimension d = new Dimension(900,400);
      //Dimension d = new Dimension(1056,506);// for version 1
      Dimension d = new Dimension(1056,478);
      ColorSequenceEditor cse = new ColorSequenceEditor(d,this);
      this.setMinimumSize( d );
      this.setPreferredSize( d );
      this.add( cse );
      this.pack();
      this.setVisible( true );
   }
   
   /**
    * @param args
    */
   public static void main( String[] args )
   {
      new ColorModel();
   }
   
}
