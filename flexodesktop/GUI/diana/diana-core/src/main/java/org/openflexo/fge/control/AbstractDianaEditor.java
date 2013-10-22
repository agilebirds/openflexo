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
package org.openflexo.fge.control;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.control.notifications.ControlNotification;
import org.openflexo.fge.control.notifications.ScaleChanged;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.view.ConnectorView;
import org.openflexo.fge.view.DianaViewFactory;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.fge.view.FGEView;
import org.openflexo.fge.view.ShapeView;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * This is the abstract core implementation of a {@link DianaEditor} representing and/or editing a {@link Drawing}
 * 
 * @author sylvain
 * 
 * @param <M>
 *            type of model beeing represented
 * @param <F>
 *            type of {@link DianaViewFactory} (the technology used in this editor)
 * @param <C>
 *            type of components beeing managed as view (DianaView)
 */
public abstract class AbstractDianaEditor<M, F extends DianaViewFactory<F, C>, C> implements DianaEditor<M>, PropertyChangeListener,
		HasPropertyChangeSupport {

	private static final Logger logger = Logger.getLogger(AbstractDianaEditor.class.getPackage().getName());

	private Drawing<M> drawing;
	protected DrawingView<M, ? extends C> drawingView;
	private DianaEditorDelegate delegate;

	private double scale = 1.0;

	/**
	 * This factory is the one which is used to creates and maintains object graph
	 */
	private FGEModelFactory factory;

	/**
	 * This is the view factory installed for this editor
	 */
	private F dianaFactory;

	/**
	 * This is the view factory installed for this editor
	 */
	private DianaToolFactory<C> toolFactory;

	protected Map<DrawingTreeNode<?, ?>, FGEView<?, ? extends C>> contents;

	private PropertyChangeSupport pcSupport;

	public AbstractDianaEditor(Drawing<M> aDrawing, FGEModelFactory factory, F dianaFactory, DianaToolFactory<C> toolFactory) {
		super();

		pcSupport = new PropertyChangeSupport(this);

		this.factory = factory;
		this.dianaFactory = dianaFactory;
		this.toolFactory = toolFactory;

		contents = new Hashtable<DrawingTreeNode<?, ?>, FGEView<?, ? extends C>>();

		drawing = aDrawing;
		if (drawing instanceof DrawingImpl<?>) {
			((DrawingImpl<?>) drawing).getPropertyChangeSupport().addPropertyChangeListener(this);
		}

		buildDrawingView();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Building AbstractDianaEditor: " + this);
		}
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		// TODO
		return null;
	}

	public FGEModelFactory getFactory() {
		return factory;
	}

	public F getDianaFactory() {
		return dianaFactory;
	}

	public DianaToolFactory<C> getToolFactory() {
		return toolFactory;
	}

	public DianaEditorDelegate getDelegate() {
		return delegate;
	}

	protected void setDelegate(DianaEditorDelegate delegate) {
		this.delegate = delegate;
	}

	public DrawingView<M, ? extends C> rebuildDrawingView() {
		if (drawingView != null) {
			drawingView.delete();
		}
		buildDrawingView();
		return drawingView;
	}

	private DrawingView<M, ?> buildDrawingView() {
		drawingView = makeDrawingView();
		contents.put(drawing.getRoot(), drawingView);
		logger.info("Controller " + this + " Register " + drawingView + " for " + drawing.getRoot());
		for (DrawingTreeNode<?, ?> dtn : drawing.getRoot().getChildNodes()) {
			if (dtn instanceof ShapeNode) {
				ShapeView<?, ?> v = recursivelyBuildShapeView((ShapeNode<?>) dtn);
				drawingView.addView(v);
			} else if (dtn instanceof ConnectorNode) {
				ConnectorView<?, ?> v = makeConnectorView((ConnectorNode<?>) dtn);
				drawingView.addView(v);
			}
		}
		System.out.println("JDrawingView: " + drawingView);
		return drawingView;
	}

	private <O> ShapeView<O, ? extends C> recursivelyBuildShapeView(ShapeNode<O> shapeNode) {
		ShapeView<O, ? extends C> returned = makeShapeView(shapeNode);
		for (DrawingTreeNode<?, ?> dtn : shapeNode.getChildNodes()) {
			if (dtn instanceof ShapeNode) {
				ShapeView<?, ?> v = recursivelyBuildShapeView((ShapeNode<?>) dtn);
				returned.addView(v);
			} else if (dtn instanceof ConnectorNode) {
				ConnectorView<?, ?> v = makeConnectorView((ConnectorNode<?>) dtn);
				returned.addView(v);
			}
		}
		return returned;
	}

	/**
	 * Instantiate a new JDrawingView<br>
	 * You might override this method for a custom view managing
	 * 
	 * @return
	 */
	public DrawingView<M, ? extends C> makeDrawingView() {
		return getDianaFactory().makeDrawingView(this);
	}

	/**
	 * Instantiate a new JShapeView for a shape node<br>
	 * You might override this method for a custom view managing
	 * 
	 * @param shapeNode
	 * @return
	 */
	public <O> ShapeView<O, ? extends C> makeShapeView(ShapeNode<O> shapeNode) {
		ShapeView<O, ? extends C> returned = getDianaFactory().makeShapeView(shapeNode, this);
		contents.put(shapeNode, returned);
		return returned;
	}

	/**
	 * Instantiate a new JConnectorView for a connector node<br>
	 * You might override this method for a custom view managing
	 * 
	 * @param shapeNode
	 * @return
	 */
	public <O> ConnectorView<O, ? extends C> makeConnectorView(ConnectorNode<O> connectorNode) {
		ConnectorView<O, ? extends C> returned = getDianaFactory().makeConnectorView(connectorNode, this);
		contents.put(connectorNode, returned);
		return returned;
	}

	/**
	 * Return a Map containing all the views declared for each DrawingTreeNode, where the key of the map are the {@link DrawingTreeNode}
	 * 
	 * @return
	 */
	public Map<DrawingTreeNode<?, ?>, FGEView<?, ? extends C>> getContents() {
		return contents;
	}

	/**
	 * Return view matching supplied node
	 * 
	 * @param node
	 * @return
	 */
	public <O> FGEView<?, ? extends C> viewForNode(DrawingTreeNode<?, ?> node) {
		return contents.get(node);
	}

	/**
	 * Return view matching supplied node, asserting supplied node is a {@link ShapeNode}
	 * 
	 * @param node
	 * @return
	 */
	public ShapeView<?, ? extends C> shapeViewForNode(ShapeNode<?> node) {
		return (ShapeView<?, ? extends C>) viewForNode(node);
	}

	/**
	 * Return view matching supplied node, asserting supplied node is a {@link ConnectorNode}
	 * 
	 * @param node
	 * @return
	 */
	public ConnectorView<?, ? extends C> connectorViewForNode(ConnectorNode<?> node) {
		return (ConnectorView<?, ? extends C>) viewForNode(node);
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double aScale) {
		if (aScale < 0) {
			return;
		}
		if (scale != aScale) {
			double oldValue = scale;
			scale = aScale;
			setChanged();
			notifyObservers(new ScaleChanged(oldValue, aScale));
			drawingView.rescale();
		}
	}

	public Drawing<M> getDrawing() {
		return drawing;
	}

	public DrawingView<M, ? extends C> getDrawingView() {
		return drawingView;
	}

	public void delete() {
		if (drawing instanceof DrawingImpl<?>) {
			((DrawingImpl<?>) drawing).getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		if (drawingView != null) {
			drawingView.delete();
		}
		drawingView = null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
	}

	@Deprecated
	public void setChanged() {
	}

	public void notifyObservers(ControlNotification notification) {
		getPropertyChangeSupport().firePropertyChange(notification.eventName(), notification.oldValue, notification.newValue);
	}

}
