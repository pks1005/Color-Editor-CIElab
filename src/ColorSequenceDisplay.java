import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import javax.swing.*;

public class ColorSequenceDisplay extends JPanel{

	BufferedImage _img = null;
	
	public ColorSequenceDisplay(){
		//setPreferredSize(new Dimension(400,60));// for version 1
		setPreferredSize(new Dimension(400,50));
		
	}
	public void showColorSequenceDisplay(){
		int displayHeight = getHeight();
	      int displayWidth = getWidth();
		_img = new BufferedImage( displayWidth, displayHeight, BufferedImage.TYPE_3BYTE_BGR );
	    WritableRaster wRaster = _img.getRaster();
	    int objCount = ColorSequenceEditor.uvPointList.size();
	    if( objCount != 0){
	    	//System.out.println("Width: "+wRaster.getWidth());
	    	//System.out.println("Height: "+wRaster.getHeight());
	    	double c1[] = new double[3];
	    	double c2[] = new double[3];
	    	if(ColorSequenceEditor.smooth){
	    		int dx = displayWidth / (objCount-1);
		    	 for(int i=0,j=0,k,l,m; i<objCount-1; i++,j+=dx){
		    		 for(k=j,m=0;k<j+dx;k++,m++){
		    			 for(l=0;l<displayHeight;l++){
		    				 //wRaster.setPixel(k, l, new double[]{ColorSequenceEditor.uvPointList.get(i).r*256,ColorSequenceEditor.uvPointList.get(i).g*256,ColorSequenceEditor.uvPointList.get(i).b*256});
		    				 c1=ColorSequenceEditor.convertToIntRGB(new double[]{ColorSequenceEditor.uvPointList.get(i).r,ColorSequenceEditor.uvPointList.get(i).g,ColorSequenceEditor.uvPointList.get(i).b});
		    				 c2=ColorSequenceEditor.convertToIntRGB(new double[]{ColorSequenceEditor.uvPointList.get(i+1).r,ColorSequenceEditor.uvPointList.get(i+1).g,ColorSequenceEditor.uvPointList.get(i+1).b});
		    				 c1[0] = (c1[0]*(dx-m)+c2[0]*m)/dx;
		    				 c1[1] = (c1[1]*(dx-m)+c2[1]*m)/dx;
		    				 c1[2] = (c1[2]*(dx-m)+c2[2]*m)/dx;
		    				 wRaster.setPixel(k, l, c1);
		    				 //wRaster.setPixel(k, l, new double[]{0,0,255});
		    			 }
		    		 }
		    	 }
	    	}else{
	    		int dx = displayWidth / objCount;
		    	 for(int i=0,j=0,k,l; i<objCount; i++,j+=dx){
		    		 for(k=j;k<j+dx;k++){
		    			 for(l=0;l<displayHeight;l++){
		    				 //wRaster.setPixel(k, l, new double[]{ColorSequenceEditor.uvPointList.get(i).r*256,ColorSequenceEditor.uvPointList.get(i).g*256,ColorSequenceEditor.uvPointList.get(i).b*256});
		    				 wRaster.setPixel(k, l, ColorSequenceEditor.convertToIntRGB(new double[]{ColorSequenceEditor.uvPointList.get(i).r,ColorSequenceEditor.uvPointList.get(i).g,ColorSequenceEditor.uvPointList.get(i).b}));
		    				 //wRaster.setPixel(k, l, new double[]{0,0,255});
		    			 }
		    		 }
		    	 }
	    	}
	    	
	    }
	    
	}
	public void paintComponent( Graphics g )
	   {
		  showColorSequenceDisplay();
	      g.drawImage( _img, 0, 0, null );
	   }
}
