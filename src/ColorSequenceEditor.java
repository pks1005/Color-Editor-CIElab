
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JFileChooser;


public class ColorSequenceEditor extends JPanel implements ActionListener, ChangeListener, 
ListSelectionListener, PropertyChangeListener
{  
   public static double GAMMAVALUE = 1/2.2d;// should be close to .5 
   
   public static ColorModel cm;
   public static String title = "CIElab-Editor  ";
   
   public static JPanel               _colorPanel, _pointListPanel; // main panel
   
   ConfigPanel          _configPanel;
   
   public static ColorSequenceDisplay _colorSequenceDisplay;
   public static ColorSequenceDisplayForGraph _csd2;
   
   public static UVSlice              _uvSlice;
   //ImageLoader for Loading Image or Showing Graph
   public static ImageLoader          _imgLoader;
   //DefaultListModel     _pointList;
   JMenuBar             _menuBar;
   //PointManager		_pointManager;
   UVPoint p[];
   public static JScrollPane listScroller;
   public static JList  _pointList;
   public static final int MAX_POINTS = 150;
   public static double td = 0; // total discrimination
   public static JSlider _currentL;
   JLabel _lValuePanelLabel;
	JPanel _lValuePanel;

   
   public static double currentL=50;
   public static int pointCount=0;
   public static ArrayList<UVPoint> uvPointList = new ArrayList<UVPoint>();
   public static String displayList[] = new String[MAX_POINTS];
   public static int selectedPoint = -1; // default -1 i.e. nothing is selected
   public static int selectedPoints[]; // only used for smoothing function

private boolean smoothingEnabled = false;
   
   public static final boolean CIElab = true;  // switch between CIEluv & CIElab
   public static boolean indexSorted = true;
   public static boolean lValueSorted = false;
   public static boolean changeNeeded = true;
   public static double temp[] = new double[3];
   public static boolean  smooth = false;
   public static boolean showGraph = true;

	public static int lPad = 60;
	public static int rPad = 50;
	//public static int tPad = 94;// for version 1
	//public static int bPad = 64;// for version 1
	public static int tPad = 64;
	public static int bPad = 36;

   
      
   /**
    * Constructor
    * @param d
    */
   ColorSequenceEditor( Dimension d, ColorModel cm1 )
   {
	   cm = cm1;
	   cm1.setTitle(title);
      setDoubleBuffered( true );
      setPreferredSize( d );
      setMinimumSize( d );
      setLayout(new GridBagLayout());

      _uvSlice = new UVSlice( this );
      _uvSlice.setPreferredSize(new Dimension( 400, 400 ));
      _uvSlice.setVisible(true);
      
      _pointListPanel = new JPanel();
      _pointListPanel.setLayout(new BorderLayout());
      _pointListPanel.setPreferredSize(new Dimension( 250, 400 ));
      _pointListPanel.setVisible(true);
      // add a JList for a list of L U V points chosen
      _pointList = new JList(displayList);
      //DefaultListModel lm = new DefaultListModel();
      //_pointList.setModel(lm);
      //_pointList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      //_pointList.setLayoutOrientation(JList.VERTICAL);
      //_pointList.setVisibleRowCount(3);
      _pointList.addListSelectionListener(this);

      // create a scroller for the list
      listScroller = new JScrollPane(_pointList);
      
   // add a JSlider to select the L value
   		_currentL = new JSlider(JSlider.HORIZONTAL, 1, 1000, 500);
   		_currentL.addChangeListener(this);
   	// create a label to go with Lvalue slider note label is
   			// instance var so it can be updated with slider value
   			_lValuePanelLabel = new JLabel(" L: " + getLValue());
   		// create a panel that will display a selection for the seq display type
   			JPanel seqModePanel = new JPanel(new BorderLayout());
   			// create a panel to hold the label and slider
   			_lValuePanel = new JPanel();
   			_lValuePanel.setLayout(new BoxLayout(_lValuePanel, BoxLayout.X_AXIS));
   			_lValuePanelLabel.setAlignmentX(CENTER_ALIGNMENT);
   			_lValuePanel.add(_lValuePanelLabel);
   			_lValuePanel.add(_currentL);
   			seqModePanel.add(_lValuePanel, BorderLayout.CENTER);
   			
   			
      _pointListPanel.add( new JLabel(" Points list:"),BorderLayout.NORTH );
      _pointListPanel.add( listScroller, BorderLayout.CENTER );
      _pointListPanel.add(seqModePanel, BorderLayout.SOUTH);
      //_pointListPanel.add(_pointList,  BorderLayout.CENTER);
      
      _imgLoader = new ImageLoader();
      _imgLoader.setPreferredSize(new Dimension( 400, 400 ));
      _imgLoader.setVisible(true);
        
   // create a config panel for the sequence
      _configPanel = new ConfigPanel( this );
      //setLayout( new GridBagLayout() );
      //_configPanel.setPreferredSize(new Dimension( 250, 78 ));// for version 1
      _configPanel.setPreferredSize(new Dimension( 250, 50 ));
      _configPanel.setVisible(true);
      
      _colorSequenceDisplay = new ColorSequenceDisplay();
      //_colorSequenceDisplay.setPreferredSize(new Dimension( 400, 78 ));// for version 1
      _colorSequenceDisplay.setPreferredSize(new Dimension( 400, 50 ));
      _csd2 = new ColorSequenceDisplayForGraph();
      //_csd2.setPreferredSize(new Dimension( 400, 78 ));// for version 1
      _csd2.setPreferredSize(new Dimension( 400, 50 ));
      
      GridBagConstraints cs = new GridBagConstraints();
      //cs.fill = GridBagConstraints.BOTH;
      cs.gridx = 0;
      cs.gridy = 0;
      cs.gridwidth = 8;
      cs.gridheight = 8;
      
      cs.weighty = 2;
      cs.weightx = 2;
      add(_uvSlice,cs);
      
      GridBagConstraints cpl = new GridBagConstraints();
      cpl.fill = GridBagConstraints.BOTH;
      cpl.gridx = 8;
      cpl.gridy = 0;
      cpl.gridwidth = 4;
      cpl.gridheight = 8;
      
      cpl.weighty = 2;
      cpl.weightx = 2;
      add(_pointListPanel,cpl);
      
      GridBagConstraints cs1 = new GridBagConstraints();
      //cs1.fill = GridBagConstraints.BOTH;
      cs1.gridx = 12;
      cs1.gridy = 0;
      cs1.gridwidth = 8;
      cs1.gridheight = 8;
      
      cs1.weighty = 2;
      cs1.weightx = 2;
      
     //add(_imgLoader,cs1);
     add(_imgLoader,cs1);
     
     GridBagConstraints cs2 = new GridBagConstraints();
     cs2.fill = GridBagConstraints.BOTH;
     cs2.gridx = 0;
     cs2.gridy = 8;
     cs2.gridwidth = 8;
     cs2.gridheight = 2;
     
     cs2.weighty = 2;
     cs2.weightx = 2;
     add(_colorSequenceDisplay,cs2);
     
     GridBagConstraints cs3 = new GridBagConstraints();
     cs3.fill = GridBagConstraints.BOTH;
     cs3.gridx = 12;
     cs3.gridy = 8;
     cs3.gridwidth = 8;
     cs3.gridheight = 2;
     
     cs3.weighty = 2;
     cs3.weightx = 2;
     add(_csd2,cs3);
     
     GridBagConstraints cs4 = new GridBagConstraints();
     cs4.fill = GridBagConstraints.BOTH;
     cs4.gridx = 8;
     cs4.gridy = 8;
     cs4.gridwidth = 4;
     cs4.gridheight = 2;
     
     cs4.weighty = 2;
     cs4.weightx = 2;
     add(_configPanel,cs4);
     setVisible(true);
      
   }
   
   /**
	 * get the current L value 0 < L < 1
	 */
	public double getLValue() {
		// System.out.println("getLValue called");
		return (double) _currentL.getValue() / 10;
	}
   
   public ConfigPanel getConfigPanel()
   {
      return _configPanel;
   }
   
  
   /**
    * get the UV slice for the current L value
    * @return
    */
   public UVSlice getUVSlice()
   {
      return _uvSlice;
   }
   
 
   /**
    * Convert from RGB to XYZ
    * @param rgb
    * @return
    */
   public static double[] rgbTOxyz( double[] rgb )
   {
      double xyz[] = new double[3];
      //double rgbXYZ[][] = { { 2.76888, 1.75175, 1.13016 },
      //      					{ 1, 4.59070, 0.06010 }, 
      //						{ 0, 0.05651, 5.59427 } };
      // as per wikipedia checked inverse is correct
      
      // remove gamma
      rgb = ColorSequenceEditor.removeGamma(rgb);
      
      double rgbXYZ[][] = { { 0.412453, 0.357580, 0.180423 },
              				{ 0.212671, 0.715160, 0.072169 }, 
              				{ 0.019334, 0.119193, 0.950227 } };
      
      xyz = ColorSequenceEditor.matrixMultip( rgbXYZ, rgb );
      
      return xyz;
   }
   
   /**
    * Convert from XYZ to LAB
    * @param
    * @return
    */
   private static double ft(double t){
	   if(t > 6.0d*6*6/29/29/29)
		   return Math.cbrt(t);
	   else
		   return (( t*29.0d*29/6/6/3 ) + ( 4.0d/29 ));
   }
   public static double[] xyzTOlab( double[] xyz )
   {
      double lab[] = new double[3];
      lab[0] = 116.0d * ft(xyz[1]) - 16.0;
      lab[1] = 500.0d * (ft(xyz[0]/.95047d) - ft(xyz[1]));
      lab[2] = 200.0d * (ft(xyz[1]) - ft(xyz[2]/1.08883d));
      return lab;
   }
   
   /**
    * Convert from XYZ to LUV
    * @param xyz
    * @return
    */
   public static double[] xyzTOluv( double[] xyz )
   {
      double luv[] = new double[3];
      
      double uprime = 4.0d * xyz[0] / ( xyz[0] + 15.0d * xyz[1] + 3.0d * xyz[2] );
      double vprime = 9.0d * xyz[1] / ( xyz[0] + 15.0d * xyz[1] + 3.0d * xyz[2] );
      
      double tmp = xyz[1]/1.0d; // assuming Yn=1    luminance
      if(tmp <= Math.pow((6.0d/29.0d),3.0d)){
    	  luv[0] = Math.pow( ( 29.0d / 3.0d ), 3.0d )*tmp;
      }else{
    	  luv[0] = ( (Math.pow( tmp, 1.0d/3.0d )) * 116.0d )-16.0d;
      }
      
      luv[1] = 13.0d * luv[0] * ( uprime - .2009d );
      luv[2] = 13.0d * luv[0] * ( vprime - .4610d );
      
      return luv;
   }
   public static double[] xyzTOluv_BKP( double[] xyz )
   {
      double luv[] = new double[3];
      
      double uprime = 4 * xyz[0] / ( xyz[0] + 15 * xyz[1] + 3 * xyz[2] );
      double vprime = 9 * xyz[1] / ( xyz[0] + 15 * xyz[1] + 3 * xyz[2] );
      
      luv[0] = Math.pow( ( 29.0 / 3.0 ), 3 );
      luv[1] = 13 * luv[0] * ( uprime - .2009 );
      luv[2] = 13 * luv[0] * ( vprime - .4610 );
      
      return luv;
   }
   
   /**
    * Convert from LAB to XYZ
    * @param lab
    * @return
    */
   public static double[] labTOxyz( double[] lab )
   {
	   //lab[0] = 50;
	   //lab[1] = 0;
	   //lab[2] = 0;
	   // **************** remove code if doesn't work
	   if(lab[0]==100){
			lab[0]=99.9823599999944d;
	      }
	   // **************** 
      double xyz[] = new double[3];
      
      double f_inverse, t;
      
      t = ( lab[0] + 16 )/116;
      
      if(t > (6.0d/29)){
    	  f_inverse = t * t * t;
      }else{
    	  f_inverse = (3.0d*6*6/29/29)*(t - (4.0d/29));
      }
      xyz[1] = f_inverse * 1.0;
      
      t = t + ( lab[1] / 500 );
      if(t > (6.0d/29)){
    	  f_inverse = t * t * t;
      }else{
    	  f_inverse = (3.0d*6*6/29/29)*(t - (4.0d/29));
      }
      xyz[0] = f_inverse * .95047;
      
      t = ( lab[0] + 16 )/116 - ( lab[2] / 200 );
      if(t > (6.0d/29)){
    	  f_inverse = t * t * t;
      }else{
    	  f_inverse = (3.0d*6*6/29/29)*(t - (4.0d/29));
      }
      xyz[2] = f_inverse * 1.08883;
   
      //System.out.println("X:" +xyz[0]+"   Y:" +xyz[1]+"    Z:" +xyz[2]);
      
      return xyz;
   }
   
   /**
    * Convert from LUV to XYZ
    * @param luv
    * @return
    */
   public static double[] luvTOxyz( double[] luv )
   {
	   //luv[0] = 50;
	   //luv[1] = 0;
	   //luv[2] = 0;
      double xyz[] = new double[3];
      
      double uprime = ( luv[1] / ( 13.0d * luv[0] ) ) + .2009d;
      double vprime = ( luv[2] / ( 13.0d * luv[0] ) ) + .4610d;
      
      if (luv[0] <= 8)
         xyz[1] = luv[0] * Math.pow( ( 3.0d / 29.0d ), 3.0d );
      else
         xyz[1] = Math.pow( ( luv[0] + 16.0d ) / 116.0d, 3.0d );
      
      xyz[0] = ( 9.0d * xyz[1] * uprime )
            / ( 4.0d  * vprime );
      xyz[2] = ( xyz[1] ) * ( (12) - (3.0d * uprime) - (20 * vprime) ) / ( 4 * vprime );
      
      //System.out.println("X:" +xyz[0]+"   Y:" +xyz[1]+"    Z:" +xyz[2]);

      return xyz;
   }
   public static double[] luvTOxyz_BKP( double[] luv )
   {
      double xyz[] = new double[3];
      
      double uprime = ( luv[1] / ( 13.0d * luv[0] ) ) + .2009d;
      double vprime = ( luv[2] / ( 13.0d * luv[0] ) ) + .4610d;
      
      if (luv[0] <= 8)
         xyz[1] = luv[0] * Math.pow( ( 3.0d / 29.0d ), 3.0d );
      else
         xyz[1] = Math.pow( ( luv[0] + 16.0d ) / 116.0d, 3.0d );
      
      xyz[0] = -( 9.0d * xyz[1] * uprime )
            / ( ( uprime - 4.0d ) * vprime - uprime * vprime );
      xyz[2] = ( 9.0d * xyz[1] - 15.0d * vprime * xyz[1] - vprime * xyz[0] )
            / ( 3.0d * vprime );

      return xyz;
   }
   
   /**
    * Convert from RGB to LAB
    * @param rgb
    * @return
    */
   public static double[] rgbTOlab( double[] rgb )
   {
      //if (rgb[0] == 0)
         //return new double[] { 127.5, 127.5, 127.5 };
      return xyzTOlab( rgbTOxyz( rgb ) );
   }
   /**
    * Convert from RGB to LUV
    * @param rgb
    * @return
    */
   public static double[] rgbTOluv( double[] rgb )
   {
      //if (rgb[0] == 0)
         //return new double[] { 127.5, 127.5, 127.5 };
      return xyzTOluv( rgbTOxyz( rgb ) );
   }
   
   /**
    * Convert from LAB to RGB
    * @param lab
    * @return
    */
   public static double[] labTOrgb( double[] lab )
   {
      if (lab[0] == 0)
         return new double[] { 127.5, 127.5, 127.5 };
      return xyzTOrgb( labTOxyz( lab ) );
   }
   /**
    * Convert from LUV to RGB
    * @param luv
    * @return
    */
   public static double[] luvTOrgb( double[] luv )
   {
      if (luv[0] == 0)
         return new double[] { 127.5, 127.5, 127.5 };
      return xyzTOrgb( luvTOxyz( luv ) );
   }
   
   /**
    * Convert from XYZ to RGB
    * @param xyz
    * @return
    */
   public static double[] xyzTOrgb( double[] xyz )
   {// wikipedia sRGB
      double rgb[] = new double[3];
      //double xyzRGB[][] = { { 3.063, -1.393, -0.467 },
      //{ -0.969, 1.876, 0.042 }, { 0.068, -0.229, 1.069 } };
      // as per wikipedia
      double xyzRGB[][] = { { 3.240479, -1.537150, -0.498535 },
              				{ -0.969256, 1.875992, 0.041556 }, 
              				{ 0.055648, -0.204043, 1.057311 } };
      
      rgb = ColorSequenceEditor.matrixMultip( xyzRGB, xyz );
      //System.out.println("R:" +rgb[0]+"   G:" +rgb[1]+"    B:" +rgb[2]);
      
      //applying gamma
      rgb = ColorSequenceEditor.applyGamma(rgb);
      
      return rgb;
   }
   
   /**
    * multiply matrix
    * @param m1
    * @param m2
    * @return
    */
   static double[] matrixMultip( double[][] m1, double[] m2 )
   {
      final int rows = 3;
      final int cols = 3;
      
      double ret[] = new double[3];
      
      for( int i = 0; i < rows; ++i )
      {
         for( int j = 0; j < cols; ++j )
         {
            ret[i] += m1[i][j] * m2[j];
         }
      }
      
      return ret;
   }
   
   /**
    * Print out matrix used with multiplyMatrix
    * @param m1
    * @param m2
    */
   static void printMatrix( double m1[][], double m2[] )
   {
      final int size = 3;
      for( int i = 0; i < size; ++i )
      {
         for( int j = 0; j < size; ++j )
         {
            System.out.print( m1[i][j] + " " );
         }
         System.out.println();
      }
      
      System.out.println();
      
      for( int i = 0; i < size; ++i )
      {
         System.out.println( m2[i] );
      }
   }
   
   /**
    * Check if values are a valid RGB color
    * @param rgb
    * @return
    */
   public static boolean isValidRGB( double rgb[] ) // checking if value lies between 0 and 1
   {
      return ( rgb[0] >= 0.0d && rgb[0] <= 1.0d ) && ( rgb[1] >= 0.0d && rgb[1] <= 1.0d )
            && ( rgb[2] >= 0.0d && rgb[2] <= 1.0d );
   }
   
   /**
    * Convert RGB values [0,1] into RGB [0,255] and apply gamma correction
    * @param rgb
    * @return
    */
   public static double[] monitorRGB( double rgb[] )
   {
      rgb[0] = Math.pow( rgb[0], GAMMAVALUE );
      rgb[1] = Math.pow( rgb[1], GAMMAVALUE );
      rgb[2] = Math.pow( rgb[2], GAMMAVALUE );
      
      rgb[0]*=255;
      rgb[1]*=255;
      rgb[2]*=255;

      return rgb;
   }
   /**
    * Convert RGB values [0,1] into RGB [0,255]
    * @param rgb
    * @return
    */
   public static double[] convertToIntRGB( double rgb[] )
   {
      rgb[0]*=255;
      rgb[1]*=255;
      rgb[2]*=255;

      return rgb;
   }
   /**
    * apply gamma correction
    * @param rgb
    * @return
    */
   public static double[] applyGamma( double rgb[] )
   {
      rgb[0] = Math.pow( rgb[0], GAMMAVALUE );
      rgb[1] = Math.pow( rgb[1], GAMMAVALUE );
      rgb[2] = Math.pow( rgb[2], GAMMAVALUE );
      return rgb;
   }
   /**
    * remove gamma correction
    * @param rgb
    * @return
    */
   public static double[] removeGamma( double rgb[] )
   {
      rgb[0] = Math.pow( rgb[0], 1.0d/GAMMAVALUE );
      rgb[1] = Math.pow( rgb[1], 1.0d/GAMMAVALUE );
      rgb[2] = Math.pow( rgb[2], 1.0d/GAMMAVALUE );
      return rgb;
   }


   //sorting the points by L value
   public void sortByL(){
	   UVPoint tmpPoint = new UVPoint();
	   double tmpL=110;
	   int ind = -1;
	   for(int i=0,j;i<uvPointList.size();i++){// max L value is 100
		   tmpL = uvPointList.get(i).l;
		   for(j=i;j<uvPointList.size();j++){
			   if(uvPointList.get(j).l<tmpL){
				   tmpL = uvPointList.get(j).l;
				   ind = j;
			   }
		   }
		   if(tmpL < uvPointList.get(i).l){
			   tmpPoint = uvPointList.get(i);
			   uvPointList.set(i, uvPointList.get(ind));
			   uvPointList.set(ind, tmpPoint);
		   }
		   
	   }
	   changeNeeded=false;
   }
   //sorting the points by index
   public void sortById(){
	   UVPoint tmpPoint = new UVPoint();
	   int tmpId=MAX_POINTS+1;
	   int ind = -1;
	   for(int i=0,j;i<uvPointList.size();i++){// max L value is 100
		   tmpId = uvPointList.get(i).id;
		   for(j=i;j<uvPointList.size();j++){
			   if(uvPointList.get(j).id<tmpId){
				   tmpId = uvPointList.get(j).id;
				   ind = j;
			   }
		   }
		   if(tmpId < uvPointList.get(i).id){
			   tmpPoint = uvPointList.get(i);
			   uvPointList.set(i, uvPointList.get(ind));
			   uvPointList.set(ind, tmpPoint);
		   }
		   
	   }
	   changeNeeded=false;
   }


  
   
   public static void openMap(){
	   //test();
	   //System.exit(0);
		try{
			String s[];
			double rgb[] = new double[3];
			double luv[] = new double[3];

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(Paths.get(".").toAbsolutePath().normalize().toString()));
            int result = fileChooser.showOpenDialog(ColorSequenceEditor._pointListPanel);
            if (result == JFileChooser.APPROVE_OPTION) {
                // user selects a file
            	File selectedFile = fileChooser.getSelectedFile();
                FileReader f = new FileReader(selectedFile);
    			BufferedReader br = new BufferedReader(f);
    			cm.setTitle(title+selectedFile.getPath().toString());
    			String s1 = br.readLine();
                if(selectedFile.getPath().contains("json") || selectedFile.getPath().contains("JSON")) {
                	
        			while(!s1.contains("RGBPoints")){
        			    s1 = br.readLine();
                    }
                    while(!s1.contains("[")){
                        s1 = br.readLine();
                    }
                    while(!s1.contains(",")){
                        s1 = br.readLine();
                    }

        			uvPointList.clear();
        			UVPoint p;
        			double n1;
        			int n2;

        			for (int i=0; i<ColorSequenceEditor.MAX_POINTS; i++){  // break condition is in bottom
                        s = s1.split(",");
                        if(s.length<4)  // length=4 means only 3 commas i.e. last line
        				    break;
        				System.out.println("  R: "+s[1] +"  G: "+s[2]+"  B: "+s[3]);
        				rgb[0] = Double.parseDouble(s[1]);
        				rgb[1] = Double.parseDouble(s[2]);
        				rgb[2] = Double.parseDouble(s[3]);
        				p = new UVPoint();
        				p.r = rgb[0];
        				p.g = rgb[1];
        				p.b = rgb[2];
        				//rgb = ColorSequenceEditor.removeGamma( rgb ); // Gamma correction applied
        				
        				if(ColorSequenceEditor.CIElab){
        					luv = rgbTOlab(rgb);
        				}else{
        					luv = rgbTOluv(rgb);
        				}
        				p.gr = rgb[0];
        				p.gg = rgb[1];
        				p.gb = rgb[2];
        				// l value should be in the format XXX .X only 
        				n1 = (luv[0]-(int)luv[0])*10.0;
        				n2 = (int)((n1-(int)n1)*10);
        				if(n2 > 4){	// rounding off
        					n2 = (int)n1+1;
        				}else{
        					n2 = (int)n1;
        				}
        				p.l=(double)((int)luv[0]+n2/10.0);
        				p.u = luv[1];
        				p.v = luv[2];
        				p.id = i;
        				p.status = 0;
        				p.x = _uvSlice.getX(luv[1]);
        				p.y = _uvSlice.getY(luv[2]);
        				uvPointList.add(p);
        				s1 = br.readLine();
        			}
                }else {
        			uvPointList.clear();
        			UVPoint p;
        			double n1;
        			int n2;

        			for (int i=0; s1 != null ; i++){  // break condition is in bottom
                        s = s1.split("\t");
        				System.out.println("  R: "+s[1] +"  G: "+s[2]+"  B: "+s[3]);
        				rgb[0] = Double.parseDouble(s[1]);
        				rgb[1] = Double.parseDouble(s[2]);
        				rgb[2] = Double.parseDouble(s[3]);
        				p = new UVPoint();
        				p.r = rgb[0];
        				p.g = rgb[1];
        				p.b = rgb[2];
        				//rgb = ColorSequenceEditor.removeGamma( rgb ); // Gamma correction applied
        				
        				if(ColorSequenceEditor.CIElab){
        					luv = rgbTOlab(rgb);
        				}else{
        					luv = rgbTOluv(rgb);
        				}
        				p.gr = rgb[0];
        				p.gg = rgb[1];
        				p.gb = rgb[2];
        				// l value should be in the format XXX .X only 
        				n1 = (luv[0]-(int)luv[0])*10.0;
        				n2 = (int)((n1-(int)n1)*10);
        				if(n2 > 4){	// rounding off
        					n2 = (int)n1+1;
        				}else{
        					n2 = (int)n1;
        				}
        				p.l=(double)((int)luv[0]+n2/10.0);
        				p.u = luv[1];
        				p.v = luv[2];
        				p.id = i;
        				p.status = 0;
        				p.x = _uvSlice.getX(luv[1]);
        				p.y = _uvSlice.getY(luv[2]);
        				uvPointList.add(p);
        				s1 = br.readLine();
        			}
                }
                
    			// additional things
    			selectedPoint = -1;
    			
    			br.close();
    			f.close();
            }else{
            	System.out.println("file not selected");
            }
            
			
		}catch(Exception ex){
			System.out.println("Unable to write the file ..."+ex);
		}
   }
   

public static void saveMap(){
	   
	   //openMap();
	   //System.exit(0);

		try{
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(Paths.get(".").toAbsolutePath().normalize().toString()));
            int result = fileChooser.showSaveDialog(ColorSequenceEditor._pointListPanel);
            if (result == JFileChooser.APPROVE_OPTION) {
                // user selects a file
            }
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("path: "+selectedFile.getPath());
            
            // for files with json extn
            FileWriter fw = new FileWriter(selectedFile);
            double rgb[] = new double[3];
            if((selectedFile.getPath().length()>5) && (selectedFile.getPath().substring(selectedFile.getPath().length()-5).equals(".json") || selectedFile.getPath().substring(selectedFile.getPath().length()-5).equals(".JSON"))) {
            	System.out.println("Doing json: ");
    			fw.write("[\n\t{\n\t\t\"ColorSpace\" : \"RGB\",\n\t\t\"Name\" : \"PointsMap\",\n\t\t\"NanColor\" : [ 1, 0, 0 ], \n\t\t\"RGBPoints\" : [\n");
    			int i=0;
    			for (; i<uvPointList.size()-1;i++){
    				rgb[0] = uvPointList.get(i).r;
    				rgb[1] = uvPointList.get(i).g;
    				rgb[2] = uvPointList.get(i).b;
    				//rgb = ColorSequenceEditor.removeGamma(rgb);
    				fw.write("\t\t\t"+((double)i/(uvPointList.size()-1.0))+","+rgb[0]+","+rgb[1]+","+rgb[2]+",\n");
    			}
    			rgb[0] = uvPointList.get(i).r;
				rgb[1] = uvPointList.get(i).g;
				rgb[2] = uvPointList.get(i).b;
				//rgb = ColorSequenceEditor.removeGamma(rgb); // removing gamma correction before saving the file
                fw.write("\t\t\t"+((double)i/(uvPointList.size()-1.0))+","+rgb[0]+","+rgb[1]+","+rgb[2]+"\n");
                fw.write("\t\t]\n\t}\n]");
                fw.close();
            }else {
            	System.out.println("Doing txt: ");
            	// for files with any other extn
            	for (int i=0; i<uvPointList.size();i++){
            		double r,g,b,step;
            		step = (double)i/(uvPointList.size()-1.0);
            		rgb[0] = uvPointList.get(i).r;
            		rgb[1] = uvPointList.get(i).g;
            		rgb[2] = uvPointList.get(i).b;
            		//rgb = ColorSequenceEditor.removeGamma(rgb); // removing gamma correction before saving the file
            		System.out.println("Before format:\tR:"+rgb[0]+"G:"+rgb[1]+"B:"+rgb[2]);
            		//System.out.println("steps:\t"+    ((int)((r-(int)r)*10000))/10000.0d   );
            		
            		// preventing E in numbers
            		if(step<.001) step=0;
            		if(rgb[0]<.001) rgb[0]=0;
            		if(rgb[1]<.001) rgb[1]=0;
            		if(rgb[2]<.001) rgb[2]=0;
            		step = (int)step+(double)((int)((step-(int)step)*10000)/10000.0d);
            		rgb[0] = (int)rgb[0]+(double)((int)((rgb[0]-(int)rgb[0])*10000)/10000.0d);
            		rgb[1] = (int)rgb[1]+(double)((int)((rgb[1]-(int)rgb[1])*10000)/10000.0d);
            		rgb[2] = (int)rgb[2]+(double)((int)((rgb[2]-(int)rgb[2])*10000)/10000.0d);
            		System.out.println("After format:\\tR:"+rgb[0]+"G:"+rgb[1]+"B:"+rgb[2]);
    				fw.write(step+"\t"+rgb[0]+"\t"+rgb[1]+"\t"+rgb[2]+"\n");
    			}
            	fw.close();
            	if(!(selectedFile.getPath().substring(selectedFile.getPath().length()-4).equals(".txt") || selectedFile.getPath().substring(selectedFile.getPath().length()-4).equals(".TXT")))
            		selectedFile.renameTo(new File(selectedFile.getAbsoluteFile()+".txt"));
            }
			
			
		}catch(Exception ex){
			System.out.println("Unable to write the file ..."+ex);
		}
   }

@Override
public void propertyChange(PropertyChangeEvent arg0) {
	// TODO Auto-generated method stub
	System.out.println("*********************************");
	System.out.println("Property Changed of CSE called");
	System.out.println("*********************************");
}

@Override
public void valueChanged(ListSelectionEvent e) {
	try {
		//temp code need to be moved to button action method for saving the file
		//***********************************************************************
		//***********************************************************************
		
		// TODO Auto-generated method stub
		selectedPoint = _pointList.getSelectedIndex();
		selectedPoints = _pointList.getSelectedIndices(); // for smoothing function
		if(selectedPoints.length>1) {
			smoothingEnabled  = true;
			ConfigPanel._smoothing.setEnabled(true);
		}else {
			smoothingEnabled  = false;
			ConfigPanel._smoothing.setEnabled(false);		
		}
		selectPoint(selectedPoint);
		_currentL.setValue((int)(ColorSequenceEditor.uvPointList.get(selectedPoint).l*10));// change the slider position as per selected point's L Value
		if(changeNeeded){
			if(indexSorted){ //default is index sorted
				sortById();
			}else if(lValueSorted){
				sortByL();
			}
			repaint();
			changeNeeded = false;
		}
	}catch(Exception ex) {
		//System.err.println("dont click here...");
	}
	
}

private void selectPoint(int selectedIndex) {
	// TODO Auto-generated method stub
	for(int i=0;i<uvPointList.size();i++){
		uvPointList.get(i).status=0;
	}
	uvPointList.get(selectedIndex).status=1;
	changeNeeded = true;
}

@Override
public void stateChanged(ChangeEvent e) {
	// TODO Auto-generated method stub
	// changeListener for the JSlider
	if (e.getSource() == _currentL) {
		if (ColorSequenceEditor.selectedPoint != -1) { // point is selected
			// update sequence display color
			ColorSequenceEditor.temp[0] = getLValue();
			ColorSequenceEditor.temp[1] = ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoint).u;
			ColorSequenceEditor.temp[2] = ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoint).v;
			if (ColorSequenceEditor.CIElab) {
				ColorSequenceEditor.temp = ColorSequenceEditor.labTOrgb(ColorSequenceEditor.temp);
			} else {
				ColorSequenceEditor.temp = ColorSequenceEditor.luvTOrgb(ColorSequenceEditor.temp);
			}

			if (ColorSequenceEditor.isValidRGB(ColorSequenceEditor.temp)) {
				_lValuePanelLabel.setText(" L: " + getLValue());
				getUVSlice().setLValue(getLValue());
				ColorSequenceEditor.currentL = getLValue();
				ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoint).l = getLValue();
				ColorSequenceEditor.calculateRGB(ColorSequenceEditor.selectedPoint);
			} else {
				_lValuePanelLabel.setText(" L: " + getLValue());
				getUVSlice().setLValue(getLValue());
				ColorSequenceEditor.currentL = getLValue();
				_currentL.setValue(
						(int) (ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoint).l) * 10);
			}
			ColorSequenceEditor.changeNeeded = true;
		} else {
			_lValuePanelLabel.setText(" L: " + getLValue());
			getUVSlice().setLValue(getLValue());
			ColorSequenceEditor.currentL = getLValue();
			ColorSequenceEditor.changeNeeded = true;
		}
		repaint();
	}
	
	
}

@Override
public void actionPerformed(ActionEvent e) {
	// TODO Auto-generated method stub
	System.out.println("*********************************");
	System.out.println("Action Performed called of CSE called");
	System.out.println("*********************************");
}

public static void resetSelection() {
	// TODO Auto-generated method stub
	for(int i=0;i<uvPointList.size();i++){
		uvPointList.get(i).status=0;
	}
	selectedPoint = -1;
}

public static void calculateRGB(int index) {
	// TODO Auto-generated method stub
	if(ColorSequenceEditor.CIElab){
		temp = labTOrgb(new double[] {uvPointList.get(index).l,uvPointList.get(index).u,uvPointList.get(index).v});
	}else{
		temp = luvTOrgb(new double[] {uvPointList.get(index).l,uvPointList.get(index).u,uvPointList.get(index).v});
	}
	//temp = ColorSequenceEditor.applyGamma(temp);
	uvPointList.get(index).r = temp[0];
	uvPointList.get(index).g = temp[1];
	uvPointList.get(index).b = temp[2];
}

public static boolean validCoordinates() {
	// TODO Auto-generated method stub
	return isValidRGB(new double[] {uvPointList.get(selectedPoint).r,uvPointList.get(selectedPoint).g,uvPointList.get(selectedPoint).b});
}
}
