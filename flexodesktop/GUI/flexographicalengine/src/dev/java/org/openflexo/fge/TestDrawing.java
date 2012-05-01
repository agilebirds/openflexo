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
package org.openflexo.fge;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import org.openflexo.fge.connectors.Connector.ConnectorType;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.DecorationPainter;
import org.openflexo.fge.graphics.FGEShapeDecorationGraphics;
import org.openflexo.fge.shapes.Rectangle;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.fge.view.ConnectorView;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.fge.view.ShapeView;
import org.openflexo.fib.utils.FlexoLoggingViewer;
import org.openflexo.inspector.selection.EmptySelection;
import org.openflexo.inspector.selection.MultipleSelection;
import org.openflexo.inspector.selection.UniqueSelection;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.toolbox.FileResource;

public class TestDrawing {

	private static final Logger logger = FlexoLogger.getLogger(TestDrawing.class.getPackage().getName());

	public static void main(String[] args) {
		try {
			FlexoLoggingManager.initialize(-1, true, null, Level.INFO, null);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		showPanel();
	}

	public static class TestDrawingController extends DrawingController<MyDrawing> {
		private JPopupMenu contextualMenu;

		public TestDrawingController(MyDrawing aDrawing) {
			super(aDrawing);
			contextualMenu = new JPopupMenu();
			contextualMenu.add(new JMenuItem("Item"));
		}

		@Override
		public void addToSelectedObjects(GraphicalRepresentation anObject) {
			super.addToSelectedObjects(anObject);
			if (getSelectedObjects().size() == 1) {
				setChanged();
				notifyObservers(new UniqueSelection(getSelectedObjects().get(0), null));
			} else {
				setChanged();
				notifyObservers(new MultipleSelection());
			}
		}

		@Override
		public void removeFromSelectedObjects(GraphicalRepresentation anObject) {
			super.removeFromSelectedObjects(anObject);
			if (getSelectedObjects().size() == 1) {
				setChanged();
				notifyObservers(new UniqueSelection(getSelectedObjects().get(0), null));
			} else {
				setChanged();
				notifyObservers(new MultipleSelection());
			}
		}

		@Override
		public void clearSelection() {
			super.clearSelection();
			notifyObservers(new EmptySelection());
		}

		@Override
		public void selectDrawing() {
			super.selectDrawing();
			setChanged();
			notifyObservers(new UniqueSelection(getDrawingGraphicalRepresentation(), null));
		}

		@Override
		public DrawingView<MyDrawing> makeDrawingView(MyDrawing drawing) {
			DrawingView<MyDrawing> returned = super.makeDrawingView(drawing);
			returned.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					if (e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3) {
						logger.info("Display contextual menu");
					}
				}
			});
			return returned;
		}

	}

	public static void showPanel() {
		final JDialog dialog = new JDialog((Frame) null, false);

		JPanel panel = new JPanel(new BorderLayout());

		final TestInspector inspector = new TestInspector();

		final MyDrawing d = makeDrawing();
		final DrawingController<MyDrawing> dc = new TestDrawingController(d);
		dc.disablePaintingCache();
		dc.getDrawingView().setName("[NO_CACHE]");
		panel.add(new JScrollPane(dc.getDrawingView()), BorderLayout.CENTER);
		panel.add(dc.getScalePanel(), BorderLayout.NORTH);

		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
				System.exit(0);
			}
		});

		JButton inspectButton = new JButton("Inspect");
		inspectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				inspector.getWindow().setVisible(true);
			}
		});

		JButton logButton = new JButton("Logs");
		logButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FlexoLoggingViewer.showLoggingViewer(FlexoLoggingManager.instance());
			}
		});

		JButton screenshotButton = new JButton("Screenshot");
		screenshotButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final BufferedImage screenshot = dc.getPaintManager().getScreenshot(dc.getDrawingGraphicalRepresentation());
				JDialog screenshotDialog = new JDialog((Frame) null, false);
				screenshotDialog.getContentPane().add(new JPanel() {
					@Override
					public void paint(java.awt.Graphics g) {
						super.paint(g);
						g.drawImage(screenshot, 0, 0, screenshot.getWidth(), screenshot.getHeight(), null);
					}
				});
				screenshotDialog.setPreferredSize(new Dimension(400, 400));
				screenshotDialog.setLocation(500, 500);
				screenshotDialog.validate();
				screenshotDialog.pack();
				screenshotDialog.setVisible(true);
			}
		});

		JPanel controlPanel = new JPanel(new FlowLayout());
		controlPanel.add(closeButton);
		controlPanel.add(inspectButton);
		controlPanel.add(logButton);
		controlPanel.add(screenshotButton);

		panel.add(controlPanel, BorderLayout.SOUTH);

		dialog.setPreferredSize(new Dimension(550, 600));
		dialog.getContentPane().add(panel);
		dialog.validate();
		dialog.pack();

		dialog.setVisible(true);

		DrawingController<MyDrawing> dc2 = new DrawingController<MyDrawing>(d);
		final JDialog dialog2 = new JDialog((Frame) null, false);
		dialog2.getContentPane().add(new JScrollPane(dc2.getDrawingView()));
		dialog2.setPreferredSize(new Dimension(400, 400));
		dialog2.setLocation(800, 100);
		dialog2.validate();
		dialog2.pack();
		dialog2.setVisible(true);
		dc2.getDrawingView().setName("[CACHE]");
		dc2.enablePaintingCache();

		dc.addObserver(inspector);
		inspector.getWindow().setVisible(true);
	}

	public static MyDrawing makeDrawing() {
		return new MyDrawing();
	}

	public static class MyDrawing implements Drawing {
		private DrawingGraphicalRepresentation gr;
		private Vector<Object> list = new Vector<Object>();
		private Vector<Object> list2 = new Vector<Object>();

		@Override
		public Object getModel() {
			return this;
		}

		@Override
		public List getContainedObjects(Object aDrawable) {
			if (aDrawable == getModel())
				return list;
			if (aDrawable == rectangle2)
				return list2;
			return null;
		}

		@Override
		public Object getContainer(Object aDrawable) {
			if (aDrawable == getModel())
				return null;
			else if (aDrawable == rectangle1)
				return this;
			else if (aDrawable == rectangle2)
				return this;
			else if (aDrawable == pentagon)
				return this;
			else if (aDrawable == image)
				return this;
			else if (aDrawable == circle)
				return rectangle2;
			else if (aDrawable == line1)
				return this;
			else if (aDrawable == line2)
				return this;
			return null;
		}

		@Override
		public DrawingGraphicalRepresentation getDrawingGraphicalRepresentation() {
			return gr;
		}

		@Override
		public GraphicalRepresentation getGraphicalRepresentation(Object aDrawable) {
			if (aDrawable == this)
				return getDrawingGraphicalRepresentation();
			if (aDrawable instanceof MyShape)
				return ((MyShape) aDrawable).getGraphicalRepresentation();
			if (aDrawable instanceof MyConnector)
				return ((MyConnector) aDrawable).getGraphicalRepresentation();
			return null;
		}

		private MyRectangle rectangle1;
		private MyRoundedRectangle2 rectangle2;
		private MyPentagon pentagon;
		private MyCircle circle;
		private MyImage image;
		private MyLineConnector line1;
		private MyLineConnector line2;

		public MyDrawing() {
			gr = new DrawingGraphicalRepresentation<MyDrawing>(this);
			rectangle1 = new MyRectangle();
			rectangle2 = new MyRoundedRectangle2();
			circle = new MyCircle();
			line1 = new MyLineConnector(rectangle1, rectangle2);
			pentagon = new MyPentagon();
			image = new MyImage();
			line2 = new MyLineConnector(pentagon, circle);
			list.add(rectangle1);
			list.add(rectangle2);
			list.add(pentagon);
			list.add(image);
			list.add(line1);
			list.add(line2);
			list2.add(circle);
		}

		public class MyShapeGraphicalRepresentation<O extends MyShape> extends ShapeGraphicalRepresentation<O> {
			public MyShapeGraphicalRepresentation(ShapeType shapeType, O aDrawable, MyDrawing aDrawing) {
				super(shapeType, aDrawable, aDrawing);
			}

			@Override
			public ShapeView<O> makeShapeView(DrawingController<?> controller) {
				ShapeView<O> returned = super.makeShapeView(controller);
				returned.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent e) {
						if (e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3) {
							System.out.println("Affiche le menu contextuel depuis le composant " + getDrawable());
						}
					}
				});
				return returned;
			}
		}

		public abstract class MyShape {
			public abstract MyShapeGraphicalRepresentation<?> getGraphicalRepresentation();
		}

		public class MyConnectorGraphicalRepresentation<O extends MyConnector> extends ConnectorGraphicalRepresentation<O> {
			public MyConnectorGraphicalRepresentation(ConnectorType aConnectorType, MyShapeGraphicalRepresentation<?> aStartObject,
					MyShapeGraphicalRepresentation<?> anEndObject, O aDrawable, MyDrawing aDrawing) {
				super(aConnectorType, aStartObject, anEndObject, aDrawable, aDrawing);
			}

			@Override
			public ConnectorView<O> makeConnectorView(DrawingController<?> controller) {
				ConnectorView<O> returned = super.makeConnectorView(controller);
				returned.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent e) {
						if (e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3) {
							System.out.println("Affiche le menu contextuel depuis le connecteur " + getDrawable());
						}
					}
				});
				return returned;
			}
		}

		public abstract class MyConnector {
			public abstract MyConnectorGraphicalRepresentation getGraphicalRepresentation();
		}

		public class MyRectangle extends MyShape {
			private MyShapeGraphicalRepresentation<MyRectangle> gr;

			public MyRectangle() {
				gr = new MyShapeGraphicalRepresentation<MyRectangle>(ShapeType.RECTANGLE, this, MyDrawing.this);
				gr.setWidth(200);
				gr.setHeight(100);
				gr.setX(300);
				gr.setY(300);
				gr.setBackground(BackgroundStyle.makeColoredBackground(Color.LIGHT_GRAY));
			}

			@Override
			public MyShapeGraphicalRepresentation getGraphicalRepresentation() {
				return gr;
			}
		}

		public class MyRoundedRectangle2 extends MyShape {
			private MyShapeGraphicalRepresentation<MyRoundedRectangle2> gr;

			public MyRoundedRectangle2() {
				gr = new MyShapeGraphicalRepresentation<MyRoundedRectangle2>(ShapeType.RECTANGLE, this, MyDrawing.this);
				gr.setWidth(120);
				gr.setHeight(200);
				gr.setX(30);
				gr.setY(300);
				((Rectangle) gr.getShape()).setIsRounded(true);
				gr.setBackground(BackgroundStyle.makeColoredBackground(Color.ORANGE));
				gr.setBorder(new ShapeGraphicalRepresentation.ShapeBorder(20, 20, 20, 20));
				circle = new MyCircle();
			}

			@Override
			public MyShapeGraphicalRepresentation getGraphicalRepresentation() {
				return gr;
			}

		}

		public class MyCircle extends MyShape {
			private MyShapeGraphicalRepresentation<MyCircle> gr;

			public MyCircle() {
				gr = new MyShapeGraphicalRepresentation<MyCircle>(ShapeType.CIRCLE, this, MyDrawing.this);
				gr.setWidth(50);
				gr.setHeight(50);
				gr.setX(30);
				gr.setY(40);
				gr.getForeground().setColor(Color.BLUE);
				gr.setBackground(BackgroundStyle.makeColoredBackground(Color.PINK));
			}

			@Override
			public MyShapeGraphicalRepresentation getGraphicalRepresentation() {
				return gr;
			}

		}

		public class MyPentagon extends MyShape {
			private MyShapeGraphicalRepresentation<MyPentagon> gr;

			public MyPentagon() {
				gr = new MyShapeGraphicalRepresentation<MyPentagon>(ShapeType.POLYGON, this, MyDrawing.this);
				gr.setWidth(100);
				gr.setHeight(100);
				gr.setX(100);
				gr.setY(100);
				gr.getForeground().setColor(Color.BLUE);
				gr.setBackground(BackgroundStyle.makeColoredBackground(Color.YELLOW));
				gr.setBorder(new MyShapeGraphicalRepresentation.ShapeBorder(20, 10, 50, 0));
				gr.setLayer(2);
				gr.setDecorationPainter(new DecorationPainter() {
					@Override
					public void paintDecoration(FGEShapeDecorationGraphics g) {
						g.setDefaultBackground(BackgroundStyle.makeColoredBackground(Color.RED));
						g.useDefaultBackgroundStyle();
						g.drawRoundRect(0, 0, g.getWidth() - 1, g.getHeight() - 1, 20, 20);
					}

					@Override
					public boolean paintBeforeShape() {
						return true;
					}
				});
			}

			@Override
			public MyShapeGraphicalRepresentation getGraphicalRepresentation() {
				return gr;
			}

		}

		public class MyImage extends MyShape {
			private MyShapeGraphicalRepresentation<MyImage> gr;

			public MyImage() {
				gr = new MyShapeGraphicalRepresentation<MyImage>(ShapeType.RECTANGLE, this, MyDrawing.this);
				gr.setWidth(100);
				gr.setHeight(100);
				gr.setX(250);
				gr.setY(100);
				gr.setLayer(3);
				gr.getForeground().setColor(Color.BLUE);
				gr.setBackground(BackgroundStyle.makeImageBackground(new FileResource("Resources/WKF/IfOperator.gif")));
				gr.setBorder(new MyShapeGraphicalRepresentation.ShapeBorder(20, 10, 50, 0));
			}

			@Override
			public MyShapeGraphicalRepresentation getGraphicalRepresentation() {
				return gr;
			}

		}

		public class MyLineConnector extends MyConnector {
			private MyConnectorGraphicalRepresentation<MyLineConnector> gr;

			public MyLineConnector(MyShape d1, MyShape d2) {
				gr = new MyConnectorGraphicalRepresentation<MyLineConnector>(ConnectorType.RECT_POLYLIN, d1.getGraphicalRepresentation(),
						d2.getGraphicalRepresentation(), this, MyDrawing.this);
				gr.getForeground().setColor(Color.BLUE);
				gr.getForeground().setLineWidth(1.5);
				// gr.setBackground(BackgroundStyle.makeColoredBackground(Color.PINK));
				gr.setIsFocusable(true);
				gr.setText("label");
			}

			@Override
			public MyConnectorGraphicalRepresentation getGraphicalRepresentation() {
				return gr;
			}

		}

	}
}
