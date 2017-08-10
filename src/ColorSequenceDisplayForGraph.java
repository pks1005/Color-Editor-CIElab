import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import javax.swing.*;

public class ColorSequenceDisplayForGraph extends JPanel implements MouseListener{

	BufferedImage _img = null;
	WritableRaster wRaster = null;
	int dx=0, displayHeight=0, displayWidth=0;
	int x=-1,y=-1;

	public ColorSequenceDisplayForGraph(){
		//setPreferredSize(new Dimension(400,60));
		setPreferredSize(new Dimension(400,50));
		addMouseListener(this);
	}
	public void showColorSequenceDisplay(){
		displayHeight = getHeight();
	    displayWidth = getWidth();
		_img = new BufferedImage( displayWidth, displayHeight, BufferedImage.TYPE_3BYTE_BGR );
	    wRaster = _img.getRaster();
	    for(int i=0,j=0;i<wRaster.getHeight();i++)
	    	for(j=0;j<wRaster.getWidth();j++)
	    		wRaster.setPixel(j, i, new double[]{153d,153d,153d});
	    int objCount = ColorSequenceEditor.uvPointList.size();
	    if( objCount != 0){
	    	//System.out.println("Width: "+wRaster.getWidth());
	    	//System.out.println("Height: "+wRaster.getHeight());
	    	double c1[] = new double[3];
	    	double c2[] = new double[3];
	    	
	    		dx = (displayWidth - ColorSequenceEditor.lPad - ColorSequenceEditor.rPad ) / objCount;
		    	 for(int i=0,j=ColorSequenceEditor.lPad,k,l; i<objCount; i++,j+=dx){
		    		 for(k=j;k<j+dx;k++){
		    			 for(l=0;l<displayHeight-ColorSequenceEditor.bPad;l++){
		    				 //wRaster.setPixel(k, l, new double[]{ColorSequenceEditor.uvPointList.get(i).r*256,ColorSequenceEditor.uvPointList.get(i).g*256,ColorSequenceEditor.uvPointList.get(i).b*256});
		    				 wRaster.setPixel(k, l, ColorSequenceEditor.convertToIntRGB(new double[]{ColorSequenceEditor.uvPointList.get(i).r,ColorSequenceEditor.uvPointList.get(i).g,ColorSequenceEditor.uvPointList.get(i).b}));
		    				 //wRaster.setPixel(k, l, new double[]{0,0,255});
		    			 }
		    		 }
		    	 }
	    	//}
	    	
	    }
	    
	}
	public void paintComponent( Graphics g )
	   {
			showColorSequenceDisplay();
			g.drawImage( _img, 0, 0, null );
			if(ColorSequenceEditor.selectedPoint!=-1){
				g.setColor(Color.WHITE);
				//g.fillOval((ColorSequenceEditor.lPad+ColorSequenceEditor.selectedPoint*dx+dx/2)-5,30,10,10); // for version 1
				g.fillOval((ColorSequenceEditor.lPad+ColorSequenceEditor.selectedPoint*dx+dx/2)-4,20,8,8);
			}
	   }

	@Override
	public void mouseClicked(MouseEvent e) {
		//System.out.println("Graph clicked...");
		x= e.getX();
		y= e.getY();
		if(x >= ColorSequenceEditor.lPad && x<displayWidth-ColorSequenceEditor.rPad && y<displayHeight-ColorSequenceEditor.bPad ) {
			if(ColorSequenceEditor.selectedPoint != -1)
				ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoint).status=0;
			ColorSequenceEditor.selectedPoint = (x - ColorSequenceEditor.lPad)/dx;
			ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoint).status=1;
			ColorSequenceEditor._pointList.setSelectedIndex(ColorSequenceEditor.selectedPoint);
			ColorSequenceEditor._uvSlice.setLValue(ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoint).l);
			ColorSequenceEditor._currentL.setValue((int)(ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoint).l*10));// change the slider position as per selected point's L Value
			ColorSequenceEditor._uvSlice.repaint();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
}
