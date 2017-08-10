
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.text.DecimalFormat;
import java.util.Collection;

import javax.swing.JList;
import javax.swing.JPanel;


public class UVSlice extends JPanel implements MouseListener, MouseMotionListener
{
   ColorSequenceEditor _editor;
   BufferedImage _img;
   double _lValue = 50;
   public static double uMax, uMin, vMax, vMin;
   int _width, _height;
   int width, height;
   public UVPoint p=null;
   //temp variables
   public int x,y,i,j;
   public static Graphics gr;
   DecimalFormat _nf= new DecimalFormat("#0.000");
   public static boolean dragged = false, addBefore = false, addAfter = false;
   double deltaU,deltaV,xCoord,yCoord;
   /**
    * Constructor
    * @param editor
    */
   public UVSlice(ColorSequenceEditor editor)
   { 
	   //System.out.println("UVSlice constructor called...");
      setLayout(null);
      addMouseListener(this);
      addMouseMotionListener(this);
      _editor = editor;
      _width = this.getWidth();
      _height = this.getHeight();
      
//      uMax = 322;
//      uMin = -200;
//      vMax = 150;
//      vMin = -170;
      
//      uMax = 176; //right
//      uMin = -88; //left
//      vMax = 118; //bottom
//      vMin = -132;//up
      
      uMax = 100; //right
      uMin = -88; //left
      vMax = 96; //bottom
      vMin = -110;//up
      
      setLValue(50);
      
      
   }
   
   public int getX(double u){
	  width = getWidth();      
      deltaU = (uMax-uMin)/(double)width;     
      int x = (int)((double)(u-uMin)/(double)deltaU);
      return x;
   }
   public int getY(double v){
      height = getHeight();
      deltaV = (vMax-vMin)/(double)height;
      int y = height - (int)((double)(v-vMin)/(double)deltaV)-1;
      return y;
   }
  
   public double getU(int x){
	  width = getWidth();      
      deltaU = (uMax-uMin)/(double)width;     
      double u = (x * deltaU)+uMin;
      return u;
   }
   public double getV(int y){
      height = getHeight();
      deltaV = (vMax-vMin)/(double)height;
      double v = (height-y-1)*deltaV+vMin;
      return v;
   }
	  
   /**
    * Update the L value for UV slice display
    * @param l
    */
   public void setLValue(double l)
   {
	   //ColorSequenceEditor._currentL.setValue((int)l);
      _lValue = l;
      if(ColorSequenceEditor.changeNeeded){
    	  repaint();
    	  ColorSequenceEditor.changeNeeded = false;
      }
   }
   
   /**
    * Update the raster to display the UV slice for the current L value
    */
   private void updateRaster()
   {
	   //System.out.println("updateRaster called...");
      width = getWidth();
      height = getHeight();
      
      
      deltaU = (uMax-uMin)/(double)width;
      deltaV = (vMax-vMin)/(double)height;
      
      _img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
      WritableRaster wRaster = _img.getRaster();
      
      //System.out.println("width...: "+width);
      //System.out.println("height...: "+height);
      //System.out.println("deltaU...: "+deltaU);
      //System.out.println("deltaV...: "+deltaV);
      //System.out.println("uMin...: "+uMin);
      //System.out.println("vMin...: "+vMin);
      //System.out.println("uMax...: "+uMax);
      //System.out.println("vMax...: "+vMax);
 
      for(int i=0; i<width; ++i)
      {
         for(int j=0; j<height; ++j)
         {
            xCoord = (double) i * deltaU + uMin;
            yCoord = (double) j * deltaV + vMin;
            if(ColorModel.first){
            	//System.out.println("i:"+i+", j:"+j+", xCoord:"+xCoord+", yCoord:"+yCoord);
            }
            double rgb[];
         // not getting correct results at L=100 so making it L=99.9823599999944
            if(_lValue==100){
            	_lValue=99.9823599999944d;
            }
            if(ColorSequenceEditor.CIElab)
            	rgb = ColorSequenceEditor.labTOrgb( new double[] {_lValue,xCoord,yCoord} );
            else
            	rgb = ColorSequenceEditor.luvTOrgb( new double[] {_lValue,xCoord,yCoord} );
            
            //if(i%8==0){
            //	System.out.println("luv:  "+", l:"+_lValue+",  a/u:"+xCoord+",  b/v:"+yCoord);
            //	System.out.println("rgb:  "+", r:"+rgb[0]+",  g:"+rgb[1]+",  b:"+rgb[2]);
            //}
            if(!ColorSequenceEditor.isValidRGB(rgb))
               rgb[0] = rgb[1] = rgb[2] = .4;
            
            rgb = ColorSequenceEditor.convertToIntRGB( rgb );
            wRaster.setPixel( i, height-j-1, rgb ); // inverted to make it correct
         }
      }
      ColorModel.first = false;
      _img.setData( wRaster );
      
   }
   
   @Override
   
   public void paintComponent(Graphics g) 
   {
	   gr=g;
		   //System.out.println("paintComponent called...");
		      width = getWidth();
		      height = getHeight();
		      if( _width != width || _height != height );
		      {
		         updateRaster();
		         _width = width;
		         _height = height;
		      }
		      g.drawImage(_img, 0, 0, null);
		      ColorSequenceEditor._colorSequenceDisplay.repaint(); // show selected Color Sequence as soon as a point is selected
		      ColorSequenceEditor._csd2.repaint();
		      ColorSequenceEditor._imgLoader.repaint();
		      if(ColorSequenceEditor.uvPointList != null){
		    	  if(dragged){
		    		  g.setColor(Color.BLACK);
		    		  g.fillOval( x-8, y-8, 15, 15 );
		    	  }else{
		    		  for(i=0; i<ColorSequenceEditor.uvPointList.size(); i++){
			    		  ColorSequenceEditor.displayList[i] = " L: "+ColorSequenceEditor.uvPointList.get(i).l +"   R: "+ (_nf.format(ColorSequenceEditor.uvPointList.get(i).r)) +"   G: "+ (_nf.format(ColorSequenceEditor.uvPointList.get(i).g))+"   B: "+ (_nf.format(ColorSequenceEditor.uvPointList.get(i).b));
			    		  //System.out.println( "point"+(i+1)+" Coordinates: x:"+ColorSequenceEditor.uvPointList.get(i).x +" y:"+ ColorSequenceEditor.uvPointList.get(i).y);
			    		  if(ColorSequenceEditor.uvPointList.get(i).status==1){
			    			  setLValue(ColorSequenceEditor.uvPointList.get(i).l);
			    			  g.setColor(Color.WHITE);
			    			  g.drawOval( ColorSequenceEditor.uvPointList.get(i).x-8, ColorSequenceEditor.uvPointList.get(i).y-8, 15, 15 );
			    			  g.drawOval( ColorSequenceEditor.uvPointList.get(i).x-8, ColorSequenceEditor.uvPointList.get(i).y-7, 15, 15 );
							  g.drawOval( ColorSequenceEditor.uvPointList.get(i).x-7, ColorSequenceEditor.uvPointList.get(i).y-8, 15, 15 );
							  g.drawOval( ColorSequenceEditor.uvPointList.get(i).x-7, ColorSequenceEditor.uvPointList.get(i).y-7, 15, 15 );
							  g.drawOval( ColorSequenceEditor.uvPointList.get(i).x-8, ColorSequenceEditor.uvPointList.get(i).y-9, 15, 15 );
			    			  g.drawOval( ColorSequenceEditor.uvPointList.get(i).x-9, ColorSequenceEditor.uvPointList.get(i).y-8, 15, 15 );
			    			  //ColorSequenceEditor._pointList.setSelectedIndex(ColorSequenceEditor.selectedPoint);
			    		  }else{
			    			  g.setColor(Color.YELLOW);
				    		  g.drawOval( ColorSequenceEditor.uvPointList.get(i).x-8, ColorSequenceEditor.uvPointList.get(i).y-8, 14, 14 );
			    		  }
			    	  }
			    	  ColorSequenceEditor._pointList.updateUI();
			    	  for(i=1; i<ColorSequenceEditor.uvPointList.size(); i++){
			    		  g.setColor(Color.WHITE);
			    		  g.drawLine(ColorSequenceEditor.uvPointList.get(i).x-1, ColorSequenceEditor.uvPointList.get(i).y-1, ColorSequenceEditor.uvPointList.get(i-1).x-1, ColorSequenceEditor.uvPointList.get(i-1).y-1);
			    		  
			    		// generating graph
			    		double dE = Math.sqrt(
			    					Math.pow(ColorSequenceEditor.uvPointList.get(i).l-ColorSequenceEditor.uvPointList.get(i-1).l, 2) +
			    					Math.pow((ColorSequenceEditor.uvPointList.get(i).u-ColorSequenceEditor.uvPointList.get(i-1).u)*.15, 2) +
			    					Math.pow((ColorSequenceEditor.uvPointList.get(i).v-ColorSequenceEditor.uvPointList.get(i-1).v)*.15, 2)); 
			    		 
			    		//System.out.println("delta E"+i+": "+dE);
			    		  
			    		  			    		  
			    	  } 
		    	  }
		    	  
		      }
		      
   }

   
   private int pointExists(int x, int y) {
		// TODO Auto-generated method stub
		j=-1; // if not found then return -1
		if(ColorSequenceEditor.uvPointList != null){
	  	  for(i=0; i<ColorSequenceEditor.uvPointList.size(); i++){
	  		ColorSequenceEditor.uvPointList.get(i).status=0;
	  		  if(x>ColorSequenceEditor.uvPointList.get(i).x-8 && x<ColorSequenceEditor.uvPointList.get(i).x+8 && 
	  				y>ColorSequenceEditor.uvPointList.get(i).y-8 && y<ColorSequenceEditor.uvPointList.get(i).y+8)
	  			  j = i;  		  
	  	  }
	    }
		return j;
	}
   

@Override
public void mousePressed(MouseEvent e) {
	// TODO Auto-generated method stub
	width = getWidth();
    height = getHeight();
    x=e.getPoint().x;
    y=e.getPoint().y;
    
    //System.out.println( "Mouse Coordinates: x:"+x +" y:"+ y);
    
    deltaU = (uMax-uMin)/(double)width;
    deltaV = (vMax-vMin)/(double)height;
	xCoord = (double) x * deltaU + uMin;
    yCoord = (double)(height-y-1) * deltaV + vMin; // because of the inversion

    i=pointExists(x,y);
    if(i!=-1){
    	ColorSequenceEditor.currentL = ColorSequenceEditor.uvPointList.get(i).l;
    }else{
    	ColorSequenceEditor.currentL = _editor.getLValue();
    }
    double luv[] = {ColorSequenceEditor.currentL, xCoord, yCoord};
    
    double rgb[];
    if(ColorSequenceEditor.CIElab)
    	rgb = ColorSequenceEditor.labTOrgb( luv );
    else
    	rgb = ColorSequenceEditor.luvTOrgb( luv);
    
    if(i!=-1){
    	System.out.println("\n*************************************************");
    	System.out.println("Point: "+i+" selected");
    	System.out.println("L: "+ColorSequenceEditor.uvPointList.get(i).l);
    	System.out.println("U: "+ColorSequenceEditor.uvPointList.get(i).u);
    	System.out.println("V: "+ColorSequenceEditor.uvPointList.get(i).v);
    	System.out.println("X: "+ColorSequenceEditor.uvPointList.get(i).x);
    	System.out.println("Y: "+ColorSequenceEditor.uvPointList.get(i).y);
    	System.out.println("R: "+ColorSequenceEditor.uvPointList.get(i).r);
    	System.out.println("G: "+ColorSequenceEditor.uvPointList.get(i).g);
    	System.out.println("B: "+ColorSequenceEditor.uvPointList.get(i).b);
    	System.out.println("*************************************************\n");
    	
    	    	
    	System.out.println( "Point "+(i+1)+" selected.");
    	ColorSequenceEditor._pointList.setSelectedIndex(i); // highlight the point in the list
    	ColorSequenceEditor.changeNeeded = true;
    	
    	
    	_editor._lValuePanelLabel.setText(" L: " + ColorSequenceEditor.uvPointList.get(i).l);
    	setLValue(ColorSequenceEditor.uvPointList.get(i).l);
    	ColorSequenceEditor.currentL = ColorSequenceEditor.uvPointList.get(i).l;
    	ColorSequenceEditor._currentL.setValue((int)(ColorSequenceEditor.uvPointList.get(i).l*10));// change the slider position as per selected point's L Value
    	ColorSequenceEditor.changeNeeded = true;
    	if(ColorSequenceEditor.selectedPoint != -1)
    		ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoint).status = 0; //reset previously selected point
    	ColorSequenceEditor.selectedPoint = i;
    	ColorSequenceEditor.uvPointList.get(i).status=1; //1=selected
    	// disabling buttons if clicked outside
    	ConfigPanel._addAfter.setEnabled(true);
    	ConfigPanel._addBefore.setEnabled(true);
    	ConfigPanel._delete.setEnabled(true);
    }else if(ColorSequenceEditor.isValidRGB(rgb)){
    	//setting attributes of clicked point in UV slice
    	//rgb = ColorSequenceEditor.applyGamma( rgb ); // Gamma correction applied
        p = new UVPoint();
       
        p.l = ColorSequenceEditor.currentL;
        p.u = xCoord;
        p.v = yCoord;
        p.x = x;
        p.y = y;
        p.r = rgb[0];
        p.g = rgb[1];
        p.b = rgb[2];
        p.status = 0; //1= unselected ; unselect the new point
        if(ColorSequenceEditor.uvPointList.size() != 0 && ColorSequenceEditor.selectedPoint != -1)
        	ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoint).status = 0;
        //ColorSequenceEditor._pointList.setSelectedIndex(p.id);
        if(addBefore){
        	ColorSequenceEditor.uvPointList.add(ColorSequenceEditor.selectedPoint, p);
        	p.id = ColorSequenceEditor.selectedPoint;
        	ColorSequenceEditor.pointCount++;
        	for(i=p.id+1; i<ColorSequenceEditor.uvPointList.size();i++){
        		ColorSequenceEditor.uvPointList.get(i).id = ColorSequenceEditor.uvPointList.get(i).id+1 ;
        	}
        }else if(addAfter){
        	//if(ColorSequenceEditor.selectedPoint>=ColorSequenceEditor.uvPointList.size())
        	ColorSequenceEditor.uvPointList.add(ColorSequenceEditor.selectedPoint+1, p);
        	p.id = ColorSequenceEditor.selectedPoint+1;
        	ColorSequenceEditor.pointCount++;
        	for(i=p.id+1; i<ColorSequenceEditor.uvPointList.size();i++){
        		ColorSequenceEditor.uvPointList.get(i).id = ColorSequenceEditor.uvPointList.get(i).id+1 ;
        	}
        }else{
        	p.id = ColorSequenceEditor.pointCount++;
        	ColorSequenceEditor.uvPointList.add(p);
        }
        //ColorSequenceEditor.selectedPoint = p.id;
      //ColorSequenceEditor.selectedPoint = -1;
            	
     // disabling buttons if clicked outside
    	ConfigPanel._addAfter.setEnabled(true);
    	ConfigPanel._addBefore.setEnabled(true);
    	ConfigPanel._delete.setEnabled(true);
    	    	
    	
    }else{		// check carefully ; it should always called if click outside the USlice
    	ColorSequenceEditor.resetSelection();
    	// disabling buttons if clicked outside
    	ConfigPanel._addAfter.setEnabled(false);
    	ConfigPanel._addBefore.setEnabled(false);
    	ConfigPanel._delete.setEnabled(false);
    }
    
    
    
    //ColorSequenceEditor._displayList = new JList(ColorSequenceEditor.displayList);
    
	//System.out.println("x: "+xCoord+"  y: "+yCoord);
	repaint();
}


@Override
public void mouseDragged(MouseEvent e) {
	// TODO Auto-generated method stub
	dragged = false;
	x=e.getPoint().x;
    y=e.getPoint().y;
    width = getWidth();
    height = getHeight();
    
	if(ColorSequenceEditor.selectedPoint != -1){
		
	    
	    //System.out.println( "Mouse Coordinates: x:"+x +" y:"+ y);
	    
	    deltaU = (uMax-uMin)/(double)width;
	    deltaV = (vMax-vMin)/(double)height;
		xCoord = (double) x * deltaU + uMin;
	    yCoord = (double)(height-y-1) * deltaV + vMin; // because of the inversion
	    double luv[] = {ColorSequenceEditor.currentL, xCoord, yCoord};
	    double rgb[];
	    if(ColorSequenceEditor.CIElab){
	    	rgb=ColorSequenceEditor.labTOrgb(luv);
	    }else{
	    	rgb=ColorSequenceEditor.luvTOrgb(luv);
	    }
	    
	    if(ColorSequenceEditor.isValidRGB(rgb)){
	    	//rgb = ColorSequenceEditor.applyGamma(rgb);
	    	ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoint).l=ColorSequenceEditor.currentL;
	    	ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoint).u=xCoord;
	    	ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoint).v=yCoord;
	    	ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoint).x=x;
	    	ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoint).y=y;
	    	ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoint).r=rgb[0];
	    	ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoint).g=rgb[1];
	    	ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoint).b=rgb[2];
	    	ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoint).status = 1; //selected
	    }
		
		repaint();
	}
	//System.out.println("UVSlice Mouse Dragging........");
}


@Override
public void mouseMoved(MouseEvent arg0) {
	// TODO Auto-generated method stub
	//System.out.println("UVSlice Mouse Moved........");
}


@Override
public void mouseClicked(MouseEvent e) {
	// TODO Auto-generated method stub
	//System.out.println("UVSlice Mouse Clicked........");
}


@Override
public void mouseEntered(MouseEvent e) {
	// TODO Auto-generated method stub
	//System.out.println("UVSlice Mouse Entered........");
}


@Override
public void mouseExited(MouseEvent e) {
	// TODO Auto-generated method stub
	//System.out.println("UVSlice Mouse Exited........");
	
}


@Override
public void mouseReleased(MouseEvent e) {
	// TODO Auto-generated method stub
	dragged = false;
	if(ColorSequenceEditor.selectedPoint != -1){

		repaint();
	}
	//System.out.println("UVSlice Mouse Released........");
}
 
   
 
}
