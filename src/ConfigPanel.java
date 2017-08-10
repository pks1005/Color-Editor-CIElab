
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ConfigPanel extends JComponent
		implements ActionListener, ChangeListener, ListSelectionListener, PropertyChangeListener, FocusListener {
	ColorSequenceEditor _editor;
	
	JPanel _buttonPanel, _buttonPanel2, _buttonPanel3, _buttonPanel4;
	public static JButton _smoothing, _addBefore, _addAfter, _delete, _res, _open, _save, _smooth, _button10, _button11, _button12;
	double l = 0, r = 0, g = 0, b = 0;

	private Font buttonFont = new Font("Sans_Serif", Font.BOLD, 11);
	private Font pointListFont = new Font("Arial", Font.PLAIN, 11);
	
	/**
	 * Constructor
	 * 
	 * @param editor
	 */
	public ConfigPanel(ColorSequenceEditor editor) {
		// store a reference back to the main application
		_editor = editor;

		ColorSequenceEditor._pointList.setFont(pointListFont);
		_addBefore = new JButton("Add Bf");
		_addAfter = new JButton("Add Af");
		_delete = new JButton("Del");
		_res = new JButton("Reset");
		_open = new JButton("Open");
		_save = new JButton("Save");
		_smooth = new JButton("Smooth");
		_smoothing = new JButton("~");
		_button10 = new JButton("         ");
		_button11 = new JButton("         ");
		_button12 = new JButton("          ");
		
		_addBefore.setFont(buttonFont);
		_addAfter.setFont(buttonFont);
		_delete.setFont(buttonFont);
		_res.setFont(buttonFont);
		_open.setFont(buttonFont);
		_save.setFont(buttonFont);
		_smooth.setFont(buttonFont);
		_smoothing.setFont(buttonFont);
		_button10.setFont(buttonFont);
		_button11.setFont(buttonFont);
		_button12.setFont(buttonFont);
		
		_addBefore.addActionListener(this);
		_addAfter.addActionListener(this);
		_delete.addActionListener(this);
		_res.addActionListener(this);
		_open.addActionListener(this);
		_save.addActionListener(this);
		_smooth.addActionListener(this);
		_smoothing.addActionListener(this);
		_button10.addActionListener(this);
		_button11.addActionListener(this);
		_button12.addActionListener(this);

		_addAfter.setEnabled(false);
		_addBefore.setEnabled(false);
		_delete.setEnabled(false);
		_smoothing.setEnabled(false);
		// _save.setEnabled(false);
		// _res.setEnabled(false);

		_buttonPanel = new JPanel();
		_buttonPanel.setLayout(new BoxLayout(_buttonPanel, BoxLayout.X_AXIS));
		_buttonPanel.setAlignmentX(CENTER_ALIGNMENT);
		_buttonPanel.add(_addBefore);
		_buttonPanel.add(_addAfter);
		_buttonPanel.add(_delete);
		_buttonPanel.add(_res);

		_buttonPanel2 = new JPanel();
		_buttonPanel2.setLayout(new BoxLayout(_buttonPanel2, BoxLayout.X_AXIS));
		_buttonPanel2.setAlignmentX(CENTER_ALIGNMENT);
		_buttonPanel2.add(_open);
		_buttonPanel2.add(_save);
		_buttonPanel2.add(_smooth);
		_buttonPanel2.add(_smoothing);
		
		_buttonPanel3 = new JPanel();
		_buttonPanel3.setLayout(new BoxLayout(_buttonPanel3, BoxLayout.X_AXIS));
		_buttonPanel3.setAlignmentX(CENTER_ALIGNMENT);
		
		
		_buttonPanel4 = new JPanel();
		_buttonPanel4.setLayout(new BoxLayout(_buttonPanel4, BoxLayout.Y_AXIS));
		_buttonPanel4.setAlignmentX(CENTER_ALIGNMENT);
		_buttonPanel4.add(_buttonPanel2);// for version 1

		// make a layout for this Panel and add items
		// setLayout(new GridLayout(0, 1));
		setLayout(new BorderLayout());

		
		
		add(_buttonPanel, BorderLayout.NORTH);
		add(_buttonPanel4, BorderLayout.CENTER);

	}

	

	@Override
	public void actionPerformed(ActionEvent e) {
		// System.out.println("actionPerformed called");

		if (ColorSequenceEditor.uvPointList.size() > 0) {
			if (e.getSource() == _res) {
				ColorSequenceEditor._pointList.clearSelection();
				for (int i = 0; i < ColorSequenceEditor.uvPointList.size(); i++) {
					ColorSequenceEditor.displayList[i] = "";
				}
				ColorSequenceEditor.uvPointList.clear();
				ColorSequenceEditor.selectedPoint = -1;
				_editor.getUVSlice().repaint();
				ColorSequenceEditor.listScroller.updateUI();
				ColorSequenceEditor.pointCount = 0;
				System.out.println("reset button clicked");
				ColorSequenceEditor.cm.setTitle(ColorSequenceEditor.title);
				// disabling buttons if clicked outside
				_addAfter.setEnabled(false);
				_addBefore.setEnabled(false);
				_delete.setEnabled(false);
				//_save.setEnabled(false);
			}else if (e.getSource() == _save) {
				// ColorSequenceEditor.saveMap();
				ColorSequenceEditor.saveMap();
			} else if (e.getSource() == _smooth) {
				if (_smooth.getText() == "Smooth ") {
					_smooth.setText("Stepped");
					_smooth.setForeground(Color.RED);
					ColorSequenceEditor.smooth = true;
				} else {
					_smooth.setText("Smooth ");
					_smooth.setForeground(Color.BLACK);
					ColorSequenceEditor.smooth = false;
				}
				ColorSequenceEditor._colorSequenceDisplay.repaint();
			}else if (e.getSource() == _smoothing) {
				//test();
				//System.exit(1);
				System.out.println("Selected Points are :");
				double firstL = ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[0]).l;
				double lastL = ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[ColorSequenceEditor.selectedPoints.length-1]).l;
				double stepL = (lastL-firstL)/(ColorSequenceEditor.selectedPoints.length-1);
				
				for(int i=0;i<ColorSequenceEditor.selectedPoints.length;i++) {
					System.out.println("\n\t\tPoint "+i);
					// l value should be in the format XXX .X only 
					double n1,n2;
					double formatedL = firstL+stepL*i;
					n1 = (formatedL-(int)formatedL)*10.0;
					n2 = (int)((n1-(int)n1)*10);
					if(n2 > 4){	// rounding off
						n2 = (int)n1+1;
					}else{
						n2 = (int)n1;
					}
					formatedL=(double)((int)formatedL+n2/10.0);
					ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).l = formatedL;
					double lab[] = new double[3];
					lab[0] = formatedL;
					lab[1] = ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).u;
					lab[2] = ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).v;
					double rgb[] = ColorSequenceEditor.labTOrgb(lab);
					if(ColorSequenceEditor.isValidRGB(rgb)){
						ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).r = rgb[0];
						ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).g = rgb[1];
						ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).b = rgb[2];
						System.out.println("Already Inside Slice \nA/U=: "+ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).u + " X="+ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).x );
						System.out.println("B/V=: "+ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).v + " Y="+ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).y);
					}else{
						System.out.println("Outside Slice \nA/U =: "+ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).u+" X="+ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).x);
						System.out.println("B/V =: "+ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).v+" Y="+ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).y);
						if(i>0 && ColorSequenceEditor.selectedPoints.length>2){ // not required but just a check
							int dx=0,dy=0,x,y;
							x = ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).x;
							y = ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).y;
							
							while(true){
								//if(x>ColorSequenceEditor._uvSlice.getWidth()/2){
								if(x>187){ // run test method and check L=100 gives only this pixel (187,185) so this should be center
									dx = -1;
								}else {
									dx = 1;
								}
								x+=dx;
								lab[1] = ColorSequenceEditor._uvSlice.getU(x);
								rgb = ColorSequenceEditor.labTOrgb(lab);
								if(ColorSequenceEditor.isValidRGB(rgb)){
								    ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).x = x;
								    ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).y = y;
									ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).u = lab[1];
									ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).v = lab[2];
									ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).r = rgb[0];
									ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).g = rgb[1];
									ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).b = rgb[2];
									break;
								}else{
									System.out.println("Correcting \nA/U =: "+lab[1]+" X="+x);
									System.out.println("B/V =: "+lab[2]+" Y="+y);
								}
								
								//if(y>ColorSequenceEditor._uvSlice.getHeight()/2){
								if(y>185){	// run test method and check L=100 gives only this pixel (187,185) so this should be center
									dy = -1;
								}else {
									dy = 1;
								}
								y+=dy;
								lab[2] = ColorSequenceEditor._uvSlice.getV(y);
								rgb = ColorSequenceEditor.labTOrgb(lab);
								if(ColorSequenceEditor.isValidRGB(rgb)){
								    ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).x = x;
								    ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).y = y;
									ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).u = lab[1];
									ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).v = lab[2];
									ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).r = rgb[0];
									ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).g = rgb[1];
									ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).b = rgb[2];
									break;
								}else{
									System.out.println("Correcting \nA/U =: "+lab[1]+" X="+x);
									System.out.println("B/V =: "+lab[2]+" Y="+y);
								}
								
								
							}
						}
						System.out.println("Inside Slice \nA/U =: "+ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).u+" X="+ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).x);
						System.out.println("B/V =: "+ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).v+" Y="+ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.selectedPoints[i]).y);
					}
				}
				
				ColorSequenceEditor._uvSlice.repaint();
				ColorSequenceEditor._colorSequenceDisplay.repaint();
				ColorSequenceEditor._imgLoader.repaint();
				ColorSequenceEditor._pointList.repaint();
				
			}
			if (ColorSequenceEditor.selectedPoint != -1) {
				if (e.getSource() == _addBefore) {
					if (UVSlice.addBefore) {
						UVSlice.addBefore = false;
						_addBefore.setForeground(Color.BLACK);
					} else {
						UVSlice.addBefore = true;
						UVSlice.addAfter = false;
						_addBefore.setForeground(Color.RED);
						_addAfter.setForeground(Color.BLACK);
					}
					System.out.println("before button clicked");
				} else if (e.getSource() == _addAfter) {
					if (UVSlice.addAfter) {
						UVSlice.addAfter = false;
						_addAfter.setForeground(Color.BLACK);
					} else {
						UVSlice.addAfter = true;
						UVSlice.addBefore = false;
						_addAfter.setForeground(Color.RED);
						_addBefore.setForeground(Color.BLACK);
					}
					System.out.println("after button clicked");
				} else if (e.getSource() == _delete) {
					ColorSequenceEditor.uvPointList.remove(ColorSequenceEditor.selectedPoint);
					ColorSequenceEditor.selectedPoint = -1;
					// ColorSequenceEditor._pointList.clearSelection();
					ColorSequenceEditor._pointList.removeAll();
					repaint();
					ColorSequenceEditor.displayList[ColorSequenceEditor.uvPointList.size()] = "";
					ColorSequenceEditor.pointCount--;
					System.out.println("delete button clicked");
					_editor.getUVSlice().repaint();
					// disabling buttons if clicked outside
					_addAfter.setEnabled(false);
					_addBefore.setEnabled(false);
					_delete.setEnabled(false);

				} else {

				}
			}
			
			
			
			
		} else {
			if (e.getSource() == _open) {
				ColorSequenceEditor.showGraph = true;
				ColorSequenceEditor.openMap();
				ColorSequenceEditor._uvSlice.repaint();
			}
		}

	}




	@Override
	public void stateChanged(ChangeEvent arg) {
		
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		System.out.println("valueChanged called");

		repaint();
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		System.out.println("******* PIYUSH ******** propertyChange called");
		double gammaValue = ColorSequenceEditor.GAMMAVALUE; // put gamma slider otherwise

		if (e.getNewValue() instanceof Long)
			gammaValue = Double.parseDouble(((Long) e.getNewValue()).toString());
		else
			gammaValue = (Double) e.getNewValue();

		ColorSequenceEditor.GAMMAVALUE = gammaValue;
		_editor.getUVSlice().setLValue(_editor.getLValue());
		_editor.repaint();
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		System.out.println("focusGained called");

	}

	@Override
	public void focusLost(FocusEvent arg0) {
		System.out.println("focusLost called");
	}

}
