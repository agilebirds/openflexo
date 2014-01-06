/*
 * (c) Copyright 2010-2013 AgileBirds
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
package org.openflexo.fge.ppteditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.poi.hslf.model.Shape;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.swing.VerticalLayout;
import org.openflexo.toolbox.FileResource;

public class SlideShowEditor extends JPanel {

	private static final Logger logger = FlexoLogger.getLogger(SlideShowEditor.class.getPackage().getName());

	private SlideShow slideShow;
	private int index;
	private File file = null;
	private PPTEditorApplication application;

	public static SlideShowEditor loadSlideShowEditor(File file, PPTEditorApplication application) {
		logger.info("Loading " + file);

		SlideShowEditor returned = new SlideShowEditor(application);

		try {
			FileInputStream fis = new FileInputStream(file);
			returned.slideShow = new SlideShow(fis);
			returned.file = file;
			System.out.println("Loaded " + file.getAbsolutePath());
			returned.init();
			return returned;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	private SlideShowEditor(PPTEditorApplication application) {
		this.application = application;
		setLayout(new BorderLayout());
	}

	private void init() {
		JPanel miniatures = new JPanel();
		miniatures.setLayout(new VerticalLayout(10, 30, 10));
		for (Slide s : slideShow.getSlides()) {
			miniatures.add(getMiniature(s));
		}
		add(new JScrollPane(miniatures, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),
				BorderLayout.WEST);
		select(slideShow.getSlides()[0]);
	}

	public String getTitle() {
		if (file != null) {
			return file.getName();
		} else {
			return FlexoLocalization.localizedForKey(PPTEditorApplication.LOCALIZATION, "untitled") + "-" + index;
		}
	}

	public boolean save() {
		System.out.println("Saving " + file);

		try {
			slideShow.write(new FileOutputStream(file));
			System.out.println("Saved " + file.getAbsolutePath());
			return true;
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;

	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public String toString() {
		return "SlideShowEditor:" + getTitle();
	}

	public SlideEditor getController() {
		/*if (controller == null) {
			CompoundEdit edit = factory.getUndoManager().startRecording("Initialize diagram");
			controller = new SlideEditor(getDrawing(), factory, application.getToolFactory());
			factory.getUndoManager().stopRecording(edit);
		}
		return controller;*/
		return null;
	}

	public SlideShow getSlideShow() {
		return slideShow;
	}

	public static void main(String[] args) {
		FileInputStream fis;
		try {
			fis = new FileInputStream(new FileResource("TestPPT2.ppt"));
			SlideShow ssOpenned = new SlideShow(fis);
			System.out.println("Yes, j'ai ouvert le truc");
			System.out.println("Slides:" + ssOpenned.getSlides().length);
			Slide slide = ssOpenned.getSlides()[0];
			System.out.println("shapes=" + slide.getShapes().length);
			for (Shape s : slide.getShapes()) {
				System.out.println("Shape: " + s);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Slide currentSlide = null;

	public void select(Slide slide) {
		if (currentSlide != null) {
			remove(getEditor(currentSlide).getDrawingView());
		}
		System.out.println("selecting " + slide);
		currentSlide = slide;
		for (Slide s : slideShow.getSlides()) {
			getMiniature(s).setBorder(s == slide ? BorderFactory.createLineBorder(Color.BLUE, 2) : BorderFactory.createEmptyBorder());
		}
		add(getEditor(slide).getDrawingView(), BorderLayout.CENTER);
		revalidate();
		application.slideSwitched(getEditor(slide));
	}

	public Slide getCurrentSlide() {
		return currentSlide;
	}

	/*@Override
	protected void paintComponent(Graphics g) {
		slideShow.getSlides()[0].draw((Graphics2D) g);
	}*/

	private Map<Slide, MiniatureSlidePanel> miniatures = new HashMap<Slide, SlideShowEditor.MiniatureSlidePanel>();

	protected MiniatureSlidePanel getMiniature(Slide s) {
		MiniatureSlidePanel returned = miniatures.get(s);
		if (returned == null) {
			returned = new MiniatureSlidePanel(s);
			miniatures.put(s, returned);
		}
		return returned;
	}

	private Map<Slide, SlideEditor> editors = new HashMap<Slide, SlideEditor>();

	protected SlideEditor getEditor(Slide s) {
		SlideEditor returned = editors.get(s);
		if (returned == null) {
			SlideDrawing sDrawing = new SlideDrawing(s);
			// sDrawing.updateGraphicalObjectsHierarchy();
			returned = new SlideEditor(sDrawing);
			// sDrawing.updateGraphicalObjectsHierarchy();
			sDrawing.printGraphicalObjectHierarchy();
			editors.put(s, returned);
		}
		return returned;
	}

	public class MiniatureSlidePanel extends JLabel {

		private double WIDTH = 200;

		private MiniatureSlidePanel(final Slide s) {
			Dimension d = s.getSlideShow().getPageSize();
			// System.out.println("Slide in " + s.getSlideShow().getPageSize());

			BufferedImage i = new BufferedImage((int) WIDTH, (int) (WIDTH * d.height / d.width), BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = i.createGraphics();
			graphics.transform(AffineTransform.getScaleInstance(WIDTH / d.width, WIDTH / d.width));
			s.draw(graphics);
			setIcon(new ImageIcon(i));
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					select(s);
				}
			});
		}

	}

}
