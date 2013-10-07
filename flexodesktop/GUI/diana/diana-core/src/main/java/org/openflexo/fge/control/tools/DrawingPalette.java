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
package org.openflexo.fge.control.tools;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.GRProvider.DrawingGRProvider;
import org.openflexo.fge.GRProvider.ShapeGRProvider;
import org.openflexo.fge.GRStructureWalker;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.DianaInteractiveEditor;
import org.openflexo.fge.control.DianaViewer;
import org.openflexo.fge.control.tools.PaletteElement.PaletteElementTransferable;
import org.openflexo.fge.control.tools.PaletteElement.TransferedPaletteElement;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.swing.JDrawingView;
import org.openflexo.fge.swing.JPaletteElementView;
import org.openflexo.fge.swing.JShapeView;
import org.openflexo.fge.swing.SwingFactory;
import org.openflexo.fge.view.FGEView;
import org.openflexo.fge.view.listener.FocusRetriever;
import org.openflexo.fib.utils.FIBIconLibrary;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.toolbox.ToolBox;

/**
 * A DrawingPalette represents the graphical tool working with a Drawing Editor, which allows to drag-drop elements represented in a panel
 * into an edited drawing
 * 
 * @author sylvain
 * 
 */
public class DrawingPalette {

	private static final Logger logger = Logger.getLogger(DrawingPalette.class.getPackage().getName());

	private static Image DROP_OK_IMAGE = FIBIconLibrary.DROP_OK_CURSOR.getImage();
	private static Image DROP_KO_IMAGE = FIBIconLibrary.DROP_KO_CURSOR.getImage();

	public static final Cursor dropOK = ToolBox.getPLATFORM() == ToolBox.MACOS ? Toolkit.getDefaultToolkit().createCustomCursor(
			DROP_OK_IMAGE, new Point(16, 16), "Drop OK") : DragSource.DefaultMoveDrop;

	public static final Cursor dropKO = ToolBox.getPLATFORM() == ToolBox.MACOS ? Toolkit.getDefaultToolkit().createCustomCursor(
			DROP_KO_IMAGE, new Point(16, 16), "Drop KO") : DragSource.DefaultMoveNoDrop;

	private DianaInteractiveEditor<?, ?, ?> controller;

	private final PaletteDrawing paletteDrawing;
	// This controller is the local controller for displaying the palette, NOT the controller
	// Which this palette is associated to.
	private AbstractDianaEditor<DrawingPalette, ?, ?> paletteController;
	protected List<PaletteElement> elements;

	private DragSourceContext dragSourceContext;

	private final int width;
	private final int height;
	private final String title;

	/**
	 * This factory is the one of the palette itself, NOT THE ONE which is used in the related drawing editor
	 */
	private FGEModelFactory factory;

	public DrawingPalette(int width, int height, String title) {
		try {
			factory = new FGEModelFactoryImpl();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		this.width = width;
		this.height = height;
		this.title = title;
		elements = new ArrayList<PaletteElement>();
		paletteDrawing = new PaletteDrawing();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Build palette " + title + " " + Integer.toHexString(hashCode()) + " of " + getClass().getName());
		}
	}

	/**
	 * Return the factory of the palette, which is different of the one which is used in the related drawing editor
	 * 
	 * @return
	 */
	public FGEModelFactory getFactory() {
		return factory;
	}

	public void delete() {
		paletteController.delete();
		for (PaletteElement element : elements) {
			element.getGraphicalRepresentation().delete();
		}
		paletteDrawing.gr.delete();
		elements = null;
	}

	public String getTitle() {
		return title;
	}

	public List<PaletteElement> getElements() {
		return elements;
	}

	public void addElement(PaletteElement element) {
		elements.add(element);
		// Try to perform some checks and initialization of
		// expecting behaviour for a PaletteElement
		element.getGraphicalRepresentation().setIsFocusable(false);
		element.getGraphicalRepresentation().setIsSelectable(false);
		element.getGraphicalRepresentation().setIsReadOnly(true);
		element.getGraphicalRepresentation().setLocationConstraints(LocationConstraints.UNMOVABLE);
		// element.getGraphicalRepresentation().addToMouseDragControls(mouseDragControl)
	}

	public void removeElement(PaletteElement element) {
		elements.remove(element);
	}

	public JDrawingView<DrawingPalette> getPaletteView() {
		if (paletteController == null) {
			makePalettePanel();
		}
		return (JDrawingView<DrawingPalette>) paletteController.getDrawingView();
	}

	private JScrollPane scrollPane;

	public JScrollPane getPaletteViewInScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane(getPaletteView(), ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		}
		return scrollPane;
	}

	public PaletteDrawing getPaletteDrawing() {
		return paletteDrawing;
	}

	protected void makePalettePanel() {
		/*for (PaletteElement e : elements) {
			e.getGraphicalRepresentation().setValidated(true);
		}*/
		paletteDrawing.printGraphicalObjectHierarchy();
		paletteController = new DianaViewer<DrawingPalette, SwingFactory, JComponent>(paletteDrawing, factory, new SwingFactory() {
			@SuppressWarnings("unchecked")
			@Override
			public <O> JShapeView<O> makeShapeView(ShapeNode<O> shapeNode, AbstractDianaEditor<?, SwingFactory, JComponent> controller) {
				if (shapeNode.getDrawable() instanceof PaletteElement) {
					return (JShapeView<O>) (new JPaletteElementView((ShapeNode<PaletteElement>) shapeNode,
							(AbstractDianaEditor<DrawingPalette, SwingFactory, JComponent>) controller));
				}
				return super.makeShapeView(shapeNode, controller);
			}
		});
		/*for (PaletteElement e : elements) {
			e.getGraphicalRepresentation().notifyObjectHierarchyHasBeenUpdated();
		}*/
		System.out.println("J'obtiens: " + paletteController.getDrawingView());
		// paletteController.buildDrawingView();
		// System.out.println("J'obtiens 2 : " + paletteController.getDrawingView());
		// paletteController.getDrawingView().revalidate();
		// System.out.println("J'obtiens 3 : " + paletteController.getDrawingView());
	}

	public class PaletteDrawing extends DrawingImpl<DrawingPalette> implements Drawing<DrawingPalette> {

		private final DrawingGraphicalRepresentation gr;

		private PaletteDrawing() {
			super(DrawingPalette.this, factory, PersistenceMode.UniqueGraphicalRepresentations);
			gr = factory.makeDrawingGraphicalRepresentation(this, false);
			gr.setWidth(width);
			gr.setHeight(height);
			gr.setBackgroundColor(Color.WHITE);
			gr.setDrawWorkingArea(true);
			setEditable(true);
		}

		@Override
		public void init() {

			final DrawingGRBinding<DrawingPalette> paletteBinding = bindDrawing(DrawingPalette.class, "palette",
					new DrawingGRProvider<DrawingPalette>() {
						@Override
						public DrawingGraphicalRepresentation provideGR(DrawingPalette drawable, FGEModelFactory factory) {
							return gr;
						}
					});
			final ShapeGRBinding<PaletteElement> paletteElementBinding = bindShape(PaletteElement.class, "paletteElement",
					new ShapeGRProvider<PaletteElement>() {
						@Override
						public ShapeGraphicalRepresentation provideGR(PaletteElement drawable, FGEModelFactory factory) {
							return drawable.getGraphicalRepresentation();
						}
					});

			paletteBinding.addToWalkers(new GRStructureWalker<DrawingPalette>() {

				@Override
				public void walk(DrawingPalette palette) {
					for (PaletteElement element : palette.getElements()) {
						drawShape(paletteElementBinding, element, palette);
					}
				}
			});

		}

		/*@Override
		public List<?> getContainedObjects(Object aDrawable) {
			if (aDrawable == getModel()) {
				return elements;
			} else {
				return null;
			}
		}

		@Override
		public Object getContainer(Object aDrawable) {
			if (aDrawable instanceof PaletteElement) {
				return getModel();
			} else {
				return null;
			}
		}

		@Override
		public DrawingGraphicalRepresentation getDrawingGraphicalRepresentation() {
			return gr;
		}

		@Override
		@SuppressWarnings("unchecked")
		public GraphicalRepresentation getGraphicalRepresentation(Object aDrawable) {
			if (aDrawable == getModel()) {
				return getDrawingGraphicalRepresentation();
			}
			if (aDrawable instanceof PaletteElement) {
				return ((PaletteElement) aDrawable).getGraphicalRepresentation();
			}
			return null;
		}

		@Override
		public DrawingPalette getModel() {
			return DrawingPalette.this;
		}

		@Override
		public boolean isEditable() {
			return false;
		}*/

	}

	// Bout de code a rajouter dans les vues

	/*
	this.setDropTarget(new DropTarget(this, DnDConstants.ACTION_COPY, new WKFDTListener(this, controller), true){
		@Override
		public synchronized void dragOver(DropTargetDragEvent dtde) {
			super.dragOver(dtde);
			FlexoProcessView.this.getController().paintDraggedNode(FlexoProcessView.this, dtde);
		}
	});*/

	public PaletteDropListener buildPaletteDropListener(JComponent dropContainer, AbstractDianaEditor<?, ?, ?> controller) {
		return new PaletteDropListener(dropContainer, controller);
	}

	/**
	 * DTListener a listener that tracks the state of the operation
	 * 
	 * @see java.awt.dnd.DropTargetListener
	 * @see java.awt.dnd.DropTarget
	 */
	public class PaletteDropListener implements DropTargetListener {

		private final int acceptableActions = DnDConstants.ACTION_COPY;
		private final JComponent _dropContainer;
		private final AbstractDianaEditor<?, ?, ?> _controller;

		public PaletteDropListener(JComponent dropContainer, AbstractDianaEditor<?, ?, ?> controller) {
			super();
			_dropContainer = dropContainer;
			_controller = controller;
		}

		/**
		 * Called by isDragOk Checks to see if the flavor drag flavor is acceptable
		 * 
		 * @param e
		 *            the DropTargetDragEvent object
		 * @return whether the flavor is acceptable
		 */
		private boolean isDragFlavorSupported(DropTargetDragEvent e) {
			boolean ok = false;
			if (e.isDataFlavorSupported(PaletteElementTransferable.defaultFlavor())) {
				ok = true;
			}
			return ok;
		}

		/**
		 * Called by drop Checks the flavors and operations
		 * 
		 * @param e
		 *            the DropTargetDropEvent object
		 * @return the chosen DataFlavor or null if none match
		 */
		private DataFlavor chooseDropFlavor(DropTargetDropEvent e) {
			if (e.isLocalTransfer() == true && e.isDataFlavorSupported(PaletteElementTransferable.defaultFlavor())) {
				return PaletteElementTransferable.defaultFlavor();
			}
			return null;
		}

		/**
		 * Called by dragEnter and dragOver Checks the flavors and operations
		 * 
		 * @param e
		 *            the event object
		 * @return whether the flavor and operation is ok
		 */
		private boolean isDragOk(DropTargetDragEvent e) {
			if (isDragFlavorSupported(e) == false) {
				return false;
			}

			int da = e.getDropAction();
			// we're saying that these actions are necessary
			if ((da & acceptableActions) == 0) {
				return false;
			}

			try {
				PaletteElement element = ((TransferedPaletteElement) e.getTransferable().getTransferData(
						PaletteElementTransferable.defaultFlavor())).getPaletteElement();
				if (element == null) {
					return false;
				}
				DrawingTreeNode<?, ?> focused = getFocusedObject(e);
				if (focused == null) {
					return false;
				}
				return element.acceptDragging(focused);

			} catch (UnsupportedFlavorException e1) {
				logger.warning("Unexpected: " + e1);
				e1.printStackTrace();
				return false;
			} catch (IOException e1) {
				logger.warning("Unexpected: " + e1);
				e1.printStackTrace();
				return false;
			} catch (Exception e1) {
				logger.warning("Unexpected: " + e1);
				e1.printStackTrace();
				return false;
			}
		}

		/**
		 * start "drag under" feedback on component invoke acceptDrag or rejectDrag based on isDragOk
		 * 
		 * @param e
		 */
		@Override
		public void dragEnter(DropTargetDragEvent e) {
			if (!isDragOk(e)) {
				// DropLabel.this.borderColor=Color.red;
				// showBorder(true);
				e.rejectDrag();
				return;
			}
			e.acceptDrag(e.getDropAction());
		}

		/**
		 * continue "drag under" feedback on component invoke acceptDrag or rejectDrag based on isDragOk
		 * 
		 * @param e
		 */
		@Override
		public void dragOver(DropTargetDragEvent e) {
			if (isDragFlavorSupported(e)) {
				getDrawingView().updateCapturedDraggedNodeImagePosition(e, getDrawingView().getActivePalette().getPaletteView());
			}
			if (!isDragOk(e)) {
				if (getDragSourceContext() == null) {
					logger.warning("dragSourceContext should NOT be null for " + DrawingPalette.this.getTitle()
							+ Integer.toHexString(DrawingPalette.this.hashCode()) + " of " + DrawingPalette.this.getClass().getName());
				} else {
					getDragSourceContext().setCursor(dropKO);
				}
				e.rejectDrag();
				return;
			}
			if (getDragSourceContext() == null) {
				logger.warning("dragSourceContext should NOT be null");
			} else {
				getDragSourceContext().setCursor(dropOK);
			}
			e.acceptDrag(e.getDropAction());
		}

		@Override
		public void dropActionChanged(DropTargetDragEvent e) {
			if (!isDragOk(e)) {
				e.rejectDrag();
				return;
			}
			e.acceptDrag(e.getDropAction());
		}

		@Override
		public void dragExit(DropTargetEvent e) {
			// interface method
			getDrawingView().resetCapturedNode();
		}

		/**
		 * perform action from getSourceActions on the transferrable invoke acceptDrop or rejectDrop invoke dropComplete if its a local
		 * (same JVM) transfer, use StringTransferable.localStringFlavor find a match for the flavor check the operation get the
		 * transferable according to the chosen flavor do the transfer
		 * 
		 * @param e
		 */
		@Override
		public void drop(DropTargetDropEvent e) {
			try {
				DataFlavor chosen = chooseDropFlavor(e);
				if (chosen == null) {
					e.rejectDrop();
					return;
				}

				// the actions that the source has specified with DragGestureRecognizer
				int sa = e.getSourceActions();

				if ((sa & acceptableActions) == 0) {
					e.rejectDrop();
					return;
				}

				Object data = null;

				try {

					/*
					 * the source listener receives this action in dragDropEnd. if the
					 * action is DnDConstants.ACTION_COPY_OR_MOVE then the source
					 * receives MOVE!
					 */

					data = e.getTransferable().getTransferData(chosen);
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("data is a " + data.getClass().getName());
					}
					if (data == null) {
						throw new NullPointerException();
					}
				} catch (Throwable t) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Couldn't get transfer data: " + t.getMessage());
					}
					t.printStackTrace();
					e.dropComplete(false);
					return;
				}

				if (data instanceof TransferedPaletteElement) {

					try {
						PaletteElement element = ((TransferedPaletteElement) data).getPaletteElement();
						if (element == null) {
							e.rejectDrop();
							return;
						}
						DrawingTreeNode<?, ?> focused = getFocusedObject(e);
						if (focused == null) {
							e.rejectDrop();
							return;
						}
						// OK, let's got for the drop
						if (element.acceptDragging(focused)) {
							Component targetComponent = e.getDropTargetContext().getComponent();
							Point pt = e.getLocation();
							FGEPoint modelLocation = new FGEPoint();
							if (targetComponent instanceof FGEView) {
								pt = FGEUtils.convertPoint(((FGEView<?, ?>) targetComponent).getNode(), pt, focused,
										((FGEView<?, ?>) targetComponent).getScale());
								modelLocation.x = pt.x / ((FGEView<?, ?>) targetComponent).getScale();
								modelLocation.y = pt.y / ((FGEView<?, ?>) targetComponent).getScale();
								modelLocation.x -= ((TransferedPaletteElement) data).getOffset().x;
								modelLocation.y -= ((TransferedPaletteElement) data).getOffset().y;
							} else {
								modelLocation.x -= ((TransferedPaletteElement) data).getOffset().x;
								modelLocation.y -= ((TransferedPaletteElement) data).getOffset().y;
							}
							if (element.elementDragged(focused, modelLocation)) {
								e.acceptDrop(acceptableActions);
								e.dropComplete(true);
								logger.info("OK, valid drop, proceed");
								return;
							} else {
								e.rejectDrop();
								e.dropComplete(false);
								return;
							}
						}

					} catch (Exception e1) {
						logger.warning("Unexpected: " + e1);
						e1.printStackTrace();
						e.rejectDrop();
						e.dropComplete(false);
						return;
					}

				}

				e.rejectDrop();
				e.dropComplete(false);
				return;
			} finally {
				// Resets the screenshot stored by the editable drawing view (not the palette drawing view).
				getDrawingView().resetCapturedNode();
			}
		}

		private FocusRetriever getFocusRetriever() {
			if (_dropContainer instanceof FGEView) {
				return getDrawingView().getFocusRetriever();
			}
			return null;
		}

		private FGEView<?, ?> getFGEView() {
			if (_dropContainer instanceof FGEView) {
				return (FGEView<?, ?>) _dropContainer;
			}
			return null;
		}

		public DrawingTreeNode<?, ?> getFocusedObject(DropTargetDragEvent event) {
			if (getFocusRetriever() != null) {
				DrawingTreeNode<?, ?> returned = getFocusRetriever().getFocusedObject(event);
				if (returned == null) {
					// Since we are in a FGEView, a null value indicates that we are on the Drawing view
					return getFGEView().getDrawingView().getDrawing().getRoot();
				}
				return returned;
			}
			// No focus retriever: we are not in a FGEView....
			return null;
		}

		public DrawingTreeNode<?, ?> getFocusedObject(DropTargetDropEvent event) {
			if (getFocusRetriever() != null) {
				DrawingTreeNode<?, ?> returned = getFocusRetriever().getFocusedObject(event);
				if (returned == null) {
					// Since we are in a FGEView, a null value indicates that we are on the Drawing view
					return getFGEView().getDrawingView().getDrawing().getRoot();
				}
				return returned;
			}
			// No focus retriever: we are not in a FGEView....
			return null;
		}

	}

	/**
	 * Return the DrawingView of the controller this palette is associated to
	 * 
	 * @return
	 */
	public JDrawingView<?> getDrawingView() {
		if (getController() != null) {
			return (JDrawingView<?>) getController().getDrawingView();
		}
		return null;
	}

	public DianaInteractiveEditor<?, ?, ?> getController() {
		return controller;
	}

	public void registerController(DianaInteractiveEditor<?, ?, ?> controller) {
		this.controller = controller;
	}

	public void updatePalette() {
		paletteController.rebuildDrawingView();
		if (scrollPane != null) {
			scrollPane.setViewportView(getPaletteView());
		}
	}

	public DragSourceContext getDragSourceContext() {
		return dragSourceContext;
	}

	public void setDragSourceContext(DragSourceContext dragSourceContext) {
		this.dragSourceContext = dragSourceContext;
	}
}
