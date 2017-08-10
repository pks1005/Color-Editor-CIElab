import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

public class ImageLoader extends JPanel{

	public static BufferedImage img;
	public static WritableRaster wRaster;
	int width, height;
	Color vertLinesColor = new Color(140,140,140);
	Color horiLinesColor = new Color(180,180,180);
	public ImageLoader(){
		setLayout(null);
	}
	
	//load graph
	
	public void updateGraph(Graphics g){
		int noOfObjects = ColorSequenceEditor.uvPointList.size();
		if( noOfObjects != 0){
			int dx = (width - ColorSequenceEditor.lPad - ColorSequenceEditor.rPad ) / noOfObjects;
			double dE, pre_dE = 0;
			
			g.setColor(Color.WHITE);
	  		g.drawRect(ColorSequenceEditor.lPad, ColorSequenceEditor.tPad, dx * noOfObjects, height-1);
	  		//g.drawRect(ColorSequenceEditor.lPad+1, ColorSequenceEditor.tPad, dx * noOfObjects - 1, height);

	  		// drawing Label for Graph
	  		g.setColor(Color.WHITE);
	  		g.drawString("Colormap Uniformity", width/2-55, 15);
	  		
	  		
	  		// drawing dE
	  		g.setColor(Color.WHITE);
	  		g.drawString("dE", ColorSequenceEditor.lPad-46, 316);
	  		
	  		// Drawing Y Axis
	  		g.setColor(horiLinesColor);
	  		for(int i = 0; i<10; i++){
	  			g.drawLine(ColorSequenceEditor.lPad+1, ColorSequenceEditor.tPad + (height-ColorSequenceEditor.tPad)/10 * i, ColorSequenceEditor.lPad - 1 + dx * noOfObjects, ColorSequenceEditor.tPad + (height-ColorSequenceEditor.tPad)/10 * i);
	  		}
	  		g.setColor(Color.BLACK);
	  		
	  		// Total discrimination is reset
	  		ColorSequenceEditor.td = 0;
	  		// delta E calculation
	  		if(noOfObjects > 1){
	  			for(int i = 1; i < noOfObjects; i++){		  		  
			  		  dE = Math.sqrt(
			  					Math.pow(ColorSequenceEditor.uvPointList.get(i).l-ColorSequenceEditor.uvPointList.get(i-1).l, 2) +
			  					Math.pow((ColorSequenceEditor.uvPointList.get(i).u-ColorSequenceEditor.uvPointList.get(i-1).u)*.15, 2) +
			  					Math.pow((ColorSequenceEditor.uvPointList.get(i).v-ColorSequenceEditor.uvPointList.get(i-1).v)*.15, 2)); 
			  		  if(dE > pre_dE)
			  			  pre_dE = dE; // finding highest dE value for scaling
			  		  ColorSequenceEditor.td += dE; // Total discrimination is calculated
		  		}
		  		int j=(int)(pre_dE/10)+1;
		  		//System.out.println("max dE: "+j);
		  		g.setColor(Color.WHITE);
		  		for(int i = 0; i<10; i++){
		  			//g.fillRect(ColorSequenceEditor.lPad, height/10 * i, dotLength, dotHeight);
		  			g.drawString(""+j*(10-i), ColorSequenceEditor.lPad-24, ColorSequenceEditor.tPad + (height-ColorSequenceEditor.tPad)/10 * i+5);
		  		}
		  		//manually drawing 0 on Y axis
		  		//g.fillRect(0, height-2, dotLength, dotHeight);
	  			g.drawString("0", ColorSequenceEditor.lPad-24, height);
				
	  			// drawing graph for delta E here
	  			double scalingFactor = (height-ColorSequenceEditor.tPad)/j/10;
	  			pre_dE = 0;
				for(int i = 1; i < noOfObjects; i++){
		  		  // generating graph
		  		  
		  		  dE = Math.sqrt(
		  					Math.pow(ColorSequenceEditor.uvPointList.get(i).l-ColorSequenceEditor.uvPointList.get(i-1).l, 2) +
		  					Math.pow((ColorSequenceEditor.uvPointList.get(i).u-ColorSequenceEditor.uvPointList.get(i-1).u)*.15, 2) +
		  					Math.pow((ColorSequenceEditor.uvPointList.get(i).v-ColorSequenceEditor.uvPointList.get(i-1).v)*.15, 2)); 
		  		 
		  		  //System.out.println("Piyush.. delta E"+i+": "+dE);
		  		  
		  		  g.setColor(vertLinesColor);
		  		  g.drawLine(dx*i+ColorSequenceEditor.lPad, ColorSequenceEditor.tPad, dx*i+ColorSequenceEditor.lPad, height);
		  		  g.setColor(Color.RED);
		  		  g.fillOval(dx*i-4+ColorSequenceEditor.lPad, ColorSequenceEditor.tPad + (int)((height-ColorSequenceEditor.tPad)-dE*scalingFactor-4), 8, 8);
		  		  
		  		  g.setColor(Color.WHITE);
		  		  if(i>1){
		  			g.drawLine(dx*(i-1)+ColorSequenceEditor.lPad, ColorSequenceEditor.tPad + (int)((height-ColorSequenceEditor.tPad)-pre_dE*scalingFactor), dx*i+ColorSequenceEditor.lPad, ColorSequenceEditor.tPad + (int)((height-ColorSequenceEditor.tPad)-dE*scalingFactor));
		  		  }
		  		  pre_dE = dE;
		  		  
		  	  	} 
	  		}
	  		
	  	// Formatting total discrimination and displaying if not zero
	  		if(ColorSequenceEditor.td<.0001) ColorSequenceEditor.td=0;
	  		else {
	  			ColorSequenceEditor.td = (int)ColorSequenceEditor.td+(double)((int)((ColorSequenceEditor.td-(int)ColorSequenceEditor.td)*10)/10.0d);
	  			g.setColor(Color.WHITE);
	  			g.drawString("Total Discrimination: "+ColorSequenceEditor.td, width/2-75, 30);
	  		}
		}
		
	}

	//load Image
	public static void loadImg(int w1, int h1){

		// TODO Auto-generated method stub
		img = new BufferedImage(w1, h1, BufferedImage.TYPE_3BYTE_BGR);
		wRaster = img.getRaster();
		try{
			String s[];
			double rgb[] = new double[3];
			int height = 400;
			int width = 400;
	        JFileChooser fileChooser = new JFileChooser();
	        fileChooser.setCurrentDirectory(new File(Paths.get(".").toAbsolutePath().normalize().toString()));
	        int result = fileChooser.showOpenDialog(ColorSequenceEditor._colorPanel);
	        if (result == JFileChooser.APPROVE_OPTION) {
	            // user selects a file
	        	File selectedFile = fileChooser.getSelectedFile();
	            FileReader f = new FileReader(selectedFile);
	    		BufferedReader br = new BufferedReader(f);
	    		ColorSequenceEditor.cm.setTitle(ColorSequenceEditor.title+selectedFile.getPath().toString());
	    		String s1 = br.readLine();
	            if(selectedFile.getPath().contains("TXT") || selectedFile.getPath().contains("txt")) {
	    			for (int i=0,j=0,k=0; i<height ; i++, s1 = br.readLine()){  // break condition is in bottom
	                    	s = s1.split(" ");
	                    	for(j = 0,k=0; j<width;k++,j+=3){
	                    		try{
	                    			rgb[0] = Double.parseDouble(s[j]) * 255;
		            				rgb[1] = Double.parseDouble(s[j+1]) * 255;
		            				rgb[2] = Double.parseDouble(s[j+2]) * 255;
		            				rgb[0] = 255;
		            				//rgb[1] = 0;
		            				//rgb[2] = 0;
		            				wRaster.setPixel(k, i, rgb);
	                    		}catch(Exception ex){
	                    			System.err.println("Insufficient data will be filled by black");
	                    			rgb[0] = 0;
		            				rgb[1] = 0;
		            				rgb[2] = 0;
		            				wRaster.setPixel(k, i, rgb);
	                    		}
	                    	}
	    			}
	            }else {
	            	System.err.println("Invalid File");
	            }
	    		// additional things
	    		br.close();
	    		f.close();
	        }else{
	        	System.out.println("file not selected");
	        }
		}catch(Exception ex){
			System.out.println("Unable to write the file ..."+ex);
		}

	}
	
	@Override
	   public void paintComponent(Graphics g) 
	   {
			g.setColor(new Color(.6f,.6f,.6f));
			g.fillRect(0, 0, width, height);
			g.setColor(Color.BLACK);
		    //System.out.println("Graph paintComponent called...");
		    width = getWidth();
		    height = getHeight();
		    if(ColorSequenceEditor.showGraph){
		    	updateGraph(g);
				//g.drawImage(img, 0, 0, null);
		    }else{
		    	//loadImg(width, height);
				g.drawImage(img, 0, 0, null);
		    }
		    
		    //g.setColor(Color.WHITE);
			//g.drawLine(20,20,90,90);
	   }
}
