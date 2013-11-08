/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.technologyadapter.powerpoint.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hslf.model.AutoShape;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.openflexo.swing.msct.CellSpan;
import org.openflexo.swing.msct.MultiSpanCellTable;
import org.openflexo.swing.msct.MultiSpanCellTableModel;
import org.openflexo.swing.msct.TableCellExtendedRenderer;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointAutoShape;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointShape;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointSlide;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointTextBox;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointTextShape;
import org.openflexo.view.controller.FlexoController;

/**
 * Widget allowing to edit/view a ExcelSheet.<br>
 * We use here an implementation of a MultiSpanCellTable to do it.
 * 
 * @author vincent, sguerin
 * 
 */
@SuppressWarnings("serial")
public class PowerpointSlideView extends JPanel {
	static final Logger logger = Logger.getLogger(PowerpointSlideView.class.getPackage().getName());

	private PowerpointSlide slide;
	private FlexoController controller;

	private PowerpointSlidePane slidePane;

	public PowerpointSlideView(PowerpointSlide slide, FlexoController controller) {
		super(new BorderLayout());
		this.slide = slide;
		this.controller = controller;
		slidePane = new PowerpointSlidePane(slide);
		Dimension dimension = slide.getSlideshow().getSlideShow().getPageSize();
		slidePane.setPreferredSize(dimension);


		
        BufferedImage img = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = img.createGraphics();
        //clear the drawing area
        graphics.setPaint(Color.white);
        graphics.fill(new Rectangle2D.Float(0, 0, dimension.width, dimension.height));

        try { //render
	        slide.getSlide().draw(graphics);
	        //save the output
	        FileOutputStream out;
		
			out = new FileOutputStream("slide-"  + slide.getName()  + ".png");
			javax.imageio.ImageIO.write(img, "png", out);
		    out.close();
		}catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        catch(Exception e2){
        	return;
        }
  
        JLabel lab = new JLabel(new ImageIcon(img)); 
        lab.setBounds(0, 0, dimension.width, dimension.height); 
        add(lab);
		/*for(PowerpointShape shape :slide.getPowerpointShapes()){
			if(shape instanceof PowerpointTextBox){
				JLabel label = new JLabel(shape.getShape().getShapeName());
				label.setVerticalAlignment(JLabel.TOP);
				label.setHorizontalAlignment(JLabel.CENTER);
				label.setOpaque(true);
				label.setBackground(shape.getShape().getFill().getBackgroundColor());
				label.setForeground(Color.black);
				label.setBorder(BorderFactory.createLineBorder(Color.black));
				label.setPreferredSize(new Dimension((int)shape.getWidth(), (int)shape.getHeight()));
				label.setLocation((int)shape.getShape().getAnchor().getCenterX(), (int)shape.getShape().getAnchor().getCenterY());
				add(label);
			}
			if(shape instanceof PowerpointAutoShape){
				AutoShape autoShape = (AutoShape)shape.getShape();
				autoShape.
				add(label);
			}
		}*/
		
		//add(new JScrollPane(slidePane), BorderLayout.CENTER);
		
	}

	public PowerpointSlide getSlide() {
		return slide;
	}

	/**
	 * @author vincent
	 * 
	 */
	class PowerpointSlidePane extends JLayeredPane {
		
		private PowerpointSlide slide;

		public PowerpointSlidePane(PowerpointSlide slide) {
			super();
			this.slide = slide;
		}
		
		public PowerpointSlide getSlide() {
			return slide;
		}
		
		public void setSlide(PowerpointSlide slide) {
			this.slide = slide;
		}
	}

}
