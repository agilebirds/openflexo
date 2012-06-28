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
package org.openflexo.ie.view.widget;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.SingleWidgetComponentInstance;
import org.openflexo.foundation.ie.widget.IEHTMLTableConstraints;
import org.openflexo.foundation.ie.widget.IEHTMLTableWidget;
import org.openflexo.foundation.ie.widget.IEReusableWidget;
import org.openflexo.foundation.ie.widget.IESequenceTR;
import org.openflexo.foundation.ie.widget.IESequenceWidget;
import org.openflexo.foundation.ie.widget.IETDWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.ie.widget.ITableRow;
import org.openflexo.foundation.ie.widget.ITableRowReusableWidget;
import org.openflexo.logging.FlexoLogger;

class GridBagLayoutInfo implements java.io.Serializable {
	int width, height; /* number of cells horizontally, vertically */

	int startx, starty; /* starting point for layout */

	int minWidth[]; /* largest minWidth in each column */

	int minHeight[]; /* largest minHeight in each row */

	double weightX[]; /* largest weight in each column */

	double weightY[]; /* largest weight in each row */

	GridBagLayoutInfo() {
		minWidth = new int[IEHTMLTableLayout.MAXGRIDSIZE];
		minHeight = new int[IEHTMLTableLayout.MAXGRIDSIZE];
		weightX = new double[IEHTMLTableLayout.MAXGRIDSIZE];
		weightY = new double[IEHTMLTableLayout.MAXGRIDSIZE];
	}
}

public class IEHTMLTableLayout implements LayoutManager2 {

	private IEHTMLTableWidget _table;

	private Container _parent;

	private static final Logger logger = FlexoLogger.getLogger(IEHTMLTableLayout.class.getPackage().getName());

	public static final int BORDER_SIZE = 3;

	public IEHTMLTableLayout(IEHTMLTableWidget htmlTable, JComponent parent) {
		this();
		_table = htmlTable;
		_parent = parent;
	}

	public IEObject getModel(Component comp) {
		IEObject w = null;

		if (comp instanceof IEWidgetView) {
			w = ((IEWidgetView) comp).getModel();
		}
		if (w instanceof IESequenceWidget) {
			IETDWidget td = ((IESequenceWidget) w).td();
			if (td == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("I could not find the parent TD of sequence. This is no good.");
				}
				return null;
			} else {
				return td;
			}

		} else if (w instanceof IESequenceTR) {
			return w;
		} else if (w instanceof ITableRowReusableWidget) {
			return ((ITableRowReusableWidget) w).getReusableComponentInstance();
		} else {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Model is nor an ITableRow nor a sequence." + comp.getClass());
			}
			return null;
		}
	}

	/**
	 * Returns the total width percentage from the cell <code>from</code> to the cell <code>to</code> (included). If you want to know the
	 * width percentage of a cell, <code>from</code> and <code>to</code> are equal to the xLocation of that cell.
	 * 
	 * @param from
	 *            - the starting cell
	 * @param to
	 *            - the ending cell
	 * @return the total width percentage between these two cells (both included)
	 */
	private double sum(int from, int to) {
		if (to < from) {
			return 0.0d;
		}
		return _table.getPourcentage(from, to - from + 1);
	}

	private int getParentWidth() {
		int parentWidth = _parent.getWidth() == 0 && _parent.getParent() != null ? _parent.getParent().getWidth() : _parent.getWidth();
		return parentWidth - 2 * BORDER_SIZE;
	}

	private int getWidth(IEObject widget) {
		int parentWidth = getParentWidth();
		if (widget instanceof IEWidget) {
			if (widget instanceof IESequenceTR) {
				int retval = new Double(sum(0, ((ITableRow) widget).getColCount() - 1) * parentWidth).intValue();
				if (((IESequenceTR) widget).isSubsequence()) {
					retval -= 2 * ((IESequenceTR) widget).getSequenceDepth();
				}
				return retval;
			} else if (widget instanceof IETDWidget) {
				IETDWidget td = (IETDWidget) widget;
				int retval = new Double(sum(td.getXLocation(), td.getXLocation() + td.getColSpan() - 1) * parentWidth).intValue();
				if (td.getXLocation() == 0 || td.getXLocation() + td.getColSpan() == td.tr().getColCount()) {
					retval -= td.tr().getSequenceTR().getSequenceDepth() * 2;
				}
				return retval;
			} else {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("Trying to layout something else than authorized : " + widget.getClass().getName());
				}
				return 0;
			}
		} else if (widget instanceof SingleWidgetComponentInstance) {
			SingleWidgetComponentInstance ci = (SingleWidgetComponentInstance) widget;
			IEReusableWidget reusable = ci.getReusableWidget();
			ITableRow iTableRow = (ITableRow) ((ITableRowReusableWidget) reusable).getRootObject();
			int retval = new Double(sum(0, iTableRow.getColCount() - 1) * parentWidth).intValue();
			retval -= 2 * iTableRow.getSequenceDepth();
			return retval;

		}
		if (logger.isLoggable(Level.SEVERE)) {
			logger.severe("Trying to layout something else than authorized : " + widget.getClass().getName());
		}
		return 0;
	}

	private int getX(IEObject model) {
		if (model instanceof IEWidget) {
			if (model instanceof IESequenceTR) {
				return ((IESequenceTR) model).getSequenceDepth() + BORDER_SIZE;// One
																				// pixel
																				// for
				// each border
			} else if (model instanceof IETDWidget) {
				Insets i = _parent.getInsets();
				int retval = new Double(sum(0, ((IETDWidget) model).getXLocation() - 1) * getParentWidth()).intValue();
				if (retval == 0 && i != null) {
					return i.left + BORDER_SIZE;
				}
				return retval + BORDER_SIZE;
			} else {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("Trying to layout something else than authorized");
				}
				return 0;
			}
		} else if (model instanceof SingleWidgetComponentInstance) {
			SingleWidgetComponentInstance ci = (SingleWidgetComponentInstance) model;
			IEReusableWidget reusable = ci.getReusableWidget();
			return ((IESequenceTR) reusable.getParent()).getSequenceDepth() + BORDER_SIZE;
		}

		if (logger.isLoggable(Level.SEVERE)) {
			logger.severe("Trying to layout something else than authorized : " + model.getClass());
		}
		return 0;
	}

	@Override
	public void layoutContainer(Container parent) {
		Component comp;
		int compindex;
		IEHTMLTableConstraints constraints;
		Insets insets = parent.getInsets();
		insets.top += BORDER_SIZE;
		insets.bottom += BORDER_SIZE;
		Component components[] = parent.getComponents();
		Dimension d;
		Rectangle r = new Rectangle();
		int i, diffw, diffh;
		double weight;
		GridBagLayoutInfo info;

		_parent = parent;
		/*
		 * If the parent has no slaves anymore, then don't do anything at all:
		 * just leave the parent's size as-is.
		 */
		if (components.length == 0 && (columnWidths == null || columnWidths.length == 0) && (rowHeights == null || rowHeights.length == 0)) {
			return;
		}

		/*
		 * Pass #1: scan all the slaves to figure out the total amount of space
		 * needed.
		 */

		info = getLayoutInfo(parent, PREFERREDSIZE);
		d = getMinSize(parent, info);

		if (parent.getWidth() < d.width || parent.getHeight() < d.height) {
			info = getLayoutInfo(parent, MINSIZE);
			d = getMinSize(parent, info);
		}

		layoutInfo = info;
		r.width = d.width;
		r.height = d.height;
		/*
		 * If the current dimensions of the window don't match the desired
		 * dimensions, then adjust the minWidth and minHeight arrays according
		 * to the weights.
		 */

		diffw = parent.getWidth() - r.width;
		if (diffw != 0) {
			weight = 0.0;
			for (i = 0; i < info.width; i++) {
				weight += info.weightX[i];
			}
			if (weight > 0.0) {
				for (i = 0; i < info.width; i++) {
					int dx = (int) (diffw * info.weightX[i] / weight);
					info.minWidth[i] += dx;
					r.width += dx;
					if (info.minWidth[i] < 0) {
						r.width -= info.minWidth[i];
						info.minWidth[i] = 0;
					}
				}
			}
			diffw = parent.getWidth() - r.width;
		} else {
			diffw = 0;
		}

		diffh = parent.getHeight() - r.height;
		if (diffh != 0) {
			weight = 0.0;
			for (i = 0; i < info.height; i++) {
				weight += info.weightY[i];
			}
			if (weight > 0.0) {
				for (i = 0; i < info.height; i++) {
					int dy = (int) (diffh * info.weightY[i] / weight);
					info.minHeight[i] += dy;
					r.height += dy;
					if (info.minHeight[i] < 0) {
						r.height -= info.minHeight[i];
						info.minHeight[i] = 0;
					}
				}
			}
			diffh = parent.getHeight() - r.height;
		} else {
			diffh = 0;
		}

		/*
		 * Now do the actual layout of the slaves using the layout information
		 * that has been collected.
		 */

		info.startx = diffw / 2 + insets.left;
		info.starty = diffh / 2 + insets.top;
		IEObject model = null;
		for (compindex = 0; compindex < components.length; compindex++) {
			comp = components[compindex];
			if (!comp.isVisible()) {
				continue;
			}
			constraints = lookupConstraints(comp);
			model = getModel(comp);
			r.x = getX(model);
			// for (i = 0; i < constraints.tempX; i++)
			// r.x += info.minWidth[i];

			r.y = info.starty;
			for (i = 0; i < constraints.tempY; i++) {
				r.y += info.minHeight[i];
			}

			r.width = getWidth(model);
			// for (i = constraints.tempX; i < (constraints.tempX +
			// constraints.tempWidth); i++) {
			// r.width += info.minWidth[i];
			// }
			r.height = 0;
			for (i = constraints.tempY; i < constraints.tempY + constraints.tempHeight; i++) {
				r.height += info.minHeight[i];
			}

			adjustForGravity(constraints, r);

			/*
			 * fix for 4408108 - components were being created outside of the
			 * container
			 */
			if (r.x < 0) {
				r.width -= r.x;
				r.x = 0;
			}

			if (r.y < 0) {
				r.height -= r.y;
				r.y = BORDER_SIZE;
			}

			/*
			 * If the window is too small to be interesting then unmap it.
			 * Otherwise configure it and then make sure it's mapped.
			 */

			if (r.width <= 0 || r.height <= 0) {
				comp.setBounds(0, 0, 0, 0);
			} else {
				if (comp.getX() != r.x || comp.getY() != r.y || comp.getWidth() != r.width || comp.getHeight() != r.height) {
					comp.setBounds(r.x, r.y, r.width, r.height);
				}
			}
		}
	}

	/**
	 * The maximum number of grid positions (both horizontally and vertically) that can be laid out by the grid bag layout.
	 */
	protected static final int MAXGRIDSIZE = 512;

	/**
	 * The smallest grid that can be laid out by the grid bag layout.
	 */
	protected static final int MINSIZE = 1;

	/**
	 * The preferred grid size that can be laid out by the grid bag layout.
	 */
	protected static final int PREFERREDSIZE = 2;

	/**
	 * This hashtable maintains the association between a component and its gridbag constraints. The Keys in <code>comptable</code> are the
	 * components and the values are the instances of <code>IEHTMLTableConstraints</code>.
	 * 
	 * @serial
	 * @see java.awt.IEHTMLTableConstraints
	 */
	protected Hashtable<Component, IEHTMLTableConstraints> comptable;

	/**
	 * This field holds a gridbag constraints instance containing the default values, so if a component does not have gridbag constraints
	 * associated with it, then the component will be assigned a copy of the <code>defaultConstraints</code>.
	 * 
	 * @serial
	 * @see #getConstraints(Component)
	 * @see #setConstraints(Component, IEHTMLTableConstraints)
	 * @see #lookupConstraints(Component)
	 */
	protected IEHTMLTableConstraints defaultConstraints;

	/**
	 * This field holds the layout information for the gridbag. The information in this field is based on the most recent validation of the
	 * gridbag. If <code>layoutInfo</code> is <code>null</code> this indicates that there are no components in the gridbag or if there are
	 * components, they have not yet been validated.
	 * 
	 * @serial
	 * @see #getLayoutInfo(Container, int)
	 */
	protected GridBagLayoutInfo layoutInfo;

	/**
	 * This field holds the overrides to the column minimum width. If this field is non-<code>null</code> the values are applied to the
	 * gridbag after all of the minimum columns widths have been calculated. If columnWidths has more elements than the number of columns,
	 * columns are added to the gridbag to match the number of elements in columnWidth.
	 * 
	 * @serial
	 * @see #getLayoutDimensions()
	 */
	public int columnWidths[];

	/**
	 * This field holds the overrides to the row minimum heights. If this field is non-</code>null</code> the values are applied to the
	 * gridbag after all of the minimum row heights have been calculated. If <code>rowHeights</code> has more elements than the number of
	 * rows, rowa are added to the gridbag to match the number of elements in <code>rowHeights</code>.
	 * 
	 * @serial
	 * @see #getLayoutDimensions()
	 */
	public int rowHeights[];

	/**
	 * This field holds the overrides to the column weights. If this field is non-<code>null</code> the values are applied to the gridbag
	 * after all of the columns weights have been calculated. If <code>columnWeights[i]</code> &gt; weight for column i, then column i is
	 * assigned the weight in <code>columnWeights[i]</code>. If <code>columnWeights</code> has more elements than the number of columns, the
	 * excess elements are ignored - they do not cause more columns to be created.
	 * 
	 * @serial
	 */
	public double columnWeights[];

	/**
	 * This field holds the overrides to the row weights. If this field is non-</code>null</code> the values are applied to the gridbag
	 * after all of the rows weights have been calculated. If <code>rowWeights[i]</code> &gt; weight for row i, then row i is assigned the
	 * weight in <code>rowWeights[i]</code>. If <code>rowWeights</code> has more elements than the number of rows, the excess elements are
	 * ignored - they do not cause more rows to be created.
	 * 
	 * @serial
	 */
	public double rowWeights[];

	/**
	 * Creates a grid bag layout manager.
	 */
	private IEHTMLTableLayout() {
		comptable = new Hashtable<Component, IEHTMLTableConstraints>();
		defaultConstraints = new IEHTMLTableConstraints();
	}

	/**
	 * Sets the constraints for the specified component in this layout.
	 * 
	 * @param comp
	 *            the component to be modified
	 * @param constraints
	 *            the constraints to be applied
	 */
	public void setConstraints(Component comp, IEHTMLTableConstraints constraints) {
		comptable.put(comp, constraints.clone());
	}

	/**
	 * Gets the constraints for the specified component. A copy of the actual <code>IEHTMLTableConstraints</code> object is returned.
	 * 
	 * @param comp
	 *            the component to be queried
	 * @return the constraint for the specified component in this grid bag layout; a copy of the actual constraint object is returned
	 */
	public IEHTMLTableConstraints getConstraints(Component comp) {
		IEHTMLTableConstraints constraints = comptable.get(comp);
		if (constraints == null) {
			constraints = defaultConstraints.clone();
			comptable.put(comp, constraints.clone());
		}
		return constraints.clone();
	}

	/**
	 * Retrieves the constraints for the specified component. The return value is not a copy, but is the actual
	 * <code>IEHTMLTableConstraints</code> object used by the layout mechanism.
	 * 
	 * @param comp
	 *            the component to be queried
	 * @return the contraints for the specified component
	 */
	protected IEHTMLTableConstraints lookupConstraints(Component comp) {
		IEHTMLTableConstraints constraints = comptable.get(comp);
		if (constraints == null) {
			constraints = defaultConstraints.clone();
			comptable.put(comp, constraints.clone());
		}
		return constraints;
	}

	/**
	 * Removes the constraints for the specified component in this layout
	 * 
	 * @param comp
	 *            the component to be modified
	 */
	private void removeConstraints(Component comp) {
		comptable.remove(comp);
	}

	/**
	 * Determines the origin of the layout area, in the graphics coordinate space of the target container. This value represents the pixel
	 * coordinates of the top-left corner of the layout area regardless of the <code>ComponentOrientation</code> value of the container.
	 * This is distinct from the grid origin given by the cell coordinates (0,0). Most applications do not call this method directly.
	 * 
	 * @return the graphics origin of the cell in the top-left corner of the layout grid
	 * @see java.awt.ComponentOrientation
	 * @since JDK1.1
	 */
	public Point getLayoutOrigin() {
		Point origin = new Point(0, 0);
		if (layoutInfo != null) {
			origin.x = layoutInfo.startx;
			origin.y = layoutInfo.starty;
		}
		return origin;
	}

	/**
	 * Determines column widths and row heights for the layout grid.
	 * <p>
	 * Most applications do not call this method directly.
	 * 
	 * @return an array of two arrays, containing the widths of the layout columns and the heights of the layout rows
	 * @since JDK1.1
	 */
	public int[][] getLayoutDimensions() {
		if (layoutInfo == null) {
			return new int[2][0];
		}

		int dim[][] = new int[2][];
		dim[0] = new int[layoutInfo.width];
		dim[1] = new int[layoutInfo.height];

		System.arraycopy(layoutInfo.minWidth, 0, dim[0], 0, layoutInfo.width);
		System.arraycopy(layoutInfo.minHeight, 0, dim[1], 0, layoutInfo.height);

		return dim;
	}

	/**
	 * Determines the weights of the layout grid's columns and rows. Weights are used to calculate how much a given column or row stretches
	 * beyond its preferred size, if the layout has extra room to fill.
	 * <p>
	 * Most applications do not call this method directly.
	 * 
	 * @return an array of two arrays, representing the horizontal weights of the layout columns and the vertical weights of the layout rows
	 * @since JDK1.1
	 */
	public double[][] getLayoutWeights() {
		if (layoutInfo == null) {
			return new double[2][0];
		}

		double weights[][] = new double[2][];
		weights[0] = new double[layoutInfo.width];
		weights[1] = new double[layoutInfo.height];

		System.arraycopy(layoutInfo.weightX, 0, weights[0], 0, layoutInfo.width);
		System.arraycopy(layoutInfo.weightY, 0, weights[1], 0, layoutInfo.height);

		return weights;
	}

	/**
	 * Determines which cell in the layout grid contains the point specified by <code>(x,&nbsp;y)</code>. Each cell is identified by its
	 * column index (ranging from 0 to the number of columns minus 1) and its row index (ranging from 0 to the number of rows minus 1).
	 * <p>
	 * If the <code>(x,&nbsp;y)</code> point lies outside the grid, the following rules are used. The column index is returned as zero if
	 * <code>x</code> lies to the left of the layout for a left-to-right container or to the right of the layout for a right-to-left
	 * container. The column index is returned as the number of columns if <code>x</code> lies to the right of the layout in a left-to-right
	 * container or to the left in a right-to-left container. The row index is returned as zero if <code>y</code> lies above the layout, and
	 * as the number of rows if <code>y</code> lies below the layout. The orientation of a container is determined by its
	 * <code>ComponentOrientation</code> property.
	 * 
	 * @param x
	 *            the <i>x</i> coordinate of a point
	 * @param y
	 *            the <i>y</i> coordinate of a point
	 * @return an ordered pair of indexes that indicate which cell in the layout grid contains the point (<i>x</i>,&nbsp;<i>y</i>).
	 * @see java.awt.ComponentOrientation
	 * @since JDK1.1
	 */
	public Point location(int x, int y) {
		Point loc = new Point(0, 0);
		int i, d;

		if (layoutInfo == null) {
			return loc;
		}

		d = layoutInfo.startx;
		if (!rightToLeft) {
			for (i = 0; i < layoutInfo.width; i++) {
				d += layoutInfo.minWidth[i];
				if (d > x) {
					break;
				}
			}
		} else {
			for (i = layoutInfo.width - 1; i >= 0; i--) {
				if (d > x) {
					break;
				}
				d += layoutInfo.minWidth[i];
			}
			i++;
		}
		loc.x = i;

		d = layoutInfo.starty;
		for (i = 0; i < layoutInfo.height; i++) {
			d += layoutInfo.minHeight[i];
			if (d > y) {
				break;
			}
		}
		loc.y = i;

		return loc;
	}

	/**
	 * Adds the specified component with the specified name to the layout.
	 * 
	 * @param name
	 *            the name of the component
	 * @param comp
	 *            the component to be added
	 */
	@Override
	public void addLayoutComponent(String name, Component comp) {
	}

	/**
	 * Adds the specified component to the layout, using the specified <code>constraints</code> object. Note that constraints are mutable
	 * and are, therefore, cloned when cached.
	 * 
	 * @param comp
	 *            the component to be added
	 * @param constraints
	 *            an object that determines how the component is added to the layout
	 * @exception IllegalArgumentException
	 *                if <code>constraints</code> is not a <code>GridBagConstraint</code>
	 */
	@Override
	public void addLayoutComponent(Component comp, Object constraints) {
		if (constraints instanceof IEHTMLTableConstraints) {
			setConstraints(comp, (IEHTMLTableConstraints) constraints);
		} else if (constraints != null) {
			throw new IllegalArgumentException("cannot add to layout: constraints must be a GridBagConstraint");
		}
	}

	/**
	 * Removes the specified component from this layout.
	 * <p>
	 * Most applications do not call this method directly.
	 * 
	 * @param comp
	 *            the component to be removed.
	 * @see java.awt.Container#remove(java.awt.Component)
	 * @see java.awt.Container#removeAll()
	 */
	@Override
	public void removeLayoutComponent(Component comp) {
		removeConstraints(comp);
	}

	/**
	 * Determines the preferred size of the <code>parent</code> container using this grid bag layout.
	 * <p>
	 * Most applications do not call this method directly.
	 * 
	 * @param parent
	 *            the container in which to do the layout
	 * @see java.awt.Container#getPreferredSize
	 * @return the preferred size of the <code>parent</code> container
	 */
	@Override
	public Dimension preferredLayoutSize(Container parent) {
		GridBagLayoutInfo info = getLayoutInfo(parent, PREFERREDSIZE);
		return getMinSize(parent, info);
	}

	/**
	 * Determines the minimum size of the <code>parent</code> container using this grid bag layout.
	 * <p>
	 * Most applications do not call this method directly.
	 * 
	 * @param parent
	 *            the container in which to do the layout
	 * @see java.awt.Container#doLayout
	 * @return the minimum size of the <code>parent</code> container
	 */
	@Override
	public Dimension minimumLayoutSize(Container parent) {
		GridBagLayoutInfo info = getLayoutInfo(parent, MINSIZE);
		return getMinSize(parent, info);
	}

	/**
	 * Returns the maximum dimensions for this layout given the components in the specified target container.
	 * 
	 * @param target
	 *            the container which needs to be laid out
	 * @see Container
	 * @see #minimumLayoutSize(Container)
	 * @see #preferredLayoutSize(Container)
	 * @return the maximum dimensions for this layout
	 */
	@Override
	public Dimension maximumLayoutSize(Container target) {
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	/**
	 * Returns the alignment along the x axis. This specifies how the component would like to be aligned relative to other components. The
	 * value should be a number between 0 and 1 where 0 represents alignment along the origin, 1 is aligned the furthest away from the
	 * origin, 0.5 is centered, etc.
	 * <p>
	 * 
	 * @return the value <code>0.5f</code> to indicate centered
	 */
	@Override
	public float getLayoutAlignmentX(Container parent) {
		return 0.5f;
	}

	/**
	 * Returns the alignment along the y axis. This specifies how the component would like to be aligned relative to other components. The
	 * value should be a number between 0 and 1 where 0 represents alignment along the origin, 1 is aligned the furthest away from the
	 * origin, 0.5 is centered, etc.
	 * <p>
	 * 
	 * @return the value <code>0.5f</code> to indicate centered
	 */
	@Override
	public float getLayoutAlignmentY(Container parent) {
		return 0.5f;
	}

	/**
	 * Invalidates the layout, indicating that if the layout manager has cached information it should be discarded.
	 */
	@Override
	public void invalidateLayout(Container target) {
	}

	/**
	 * Returns a string representation of this grid bag layout's values.
	 * 
	 * @return a string representation of this grid bag layout.
	 */
	@Override
	public String toString() {
		return getClass().getName();
	}

	/**
	 * Fills in an instance of <code>GridBagLayoutInfo</code> for the current set of managed children. This requires three passes through
	 * the set of children:
	 * 
	 * <ol>
	 * <li>Figure out the dimensions of the layout grid.
	 * <li>Determine which cells the components occupy.
	 * <li>Distribute the weights and min sizes amoung the rows/columns.
	 * </ol>
	 * 
	 * This also caches the minsizes for all the children when they are first encountered (so subsequent loops don't need to ask again).
	 * 
	 * @param parent
	 *            the layout container
	 * @param sizeflag
	 *            either <code>PREFERREDSIZE</code> or <code>MINSIZE</code>
	 * @return the <code>GridBagLayoutInfo</code> for the set of children
	 * @since 1.4
	 */
	protected GridBagLayoutInfo getLayoutInfo(Container parent, int sizeflag) {
		return GetLayoutInfo(parent, sizeflag);
	}

	/**
	 * This method is obsolete and supplied for backwards compatability only; new code should call {@link #getLayoutInfo(Container, int)
	 * getLayoutInfo} instead.
	 */
	protected GridBagLayoutInfo GetLayoutInfo(Container parent, int sizeflag) {
		synchronized (parent.getTreeLock()) {
			GridBagLayoutInfo r = new GridBagLayoutInfo();
			Component comp;
			IEHTMLTableConstraints constraints;
			Dimension d;
			Component components[] = parent.getComponents();

			int compindex, i, k, px, py, pixels_diff, nextSize;
			int curX, curY, curWidth, curHeight, curRow, curCol;
			double weight_diff, weight;
			int xMax[], yMax[];

			/*
			 * Pass #1
			 * 
			 * Figure out the dimensions of the layout grid (use a value of 1
			 * for zero or negative widths and heights).
			 */

			r.width = r.height = 0;
			curRow = curCol = -1;
			xMax = new int[MAXGRIDSIZE];
			yMax = new int[MAXGRIDSIZE];
			IEObject model = null;
			for (compindex = 0; compindex < components.length; compindex++) {
				comp = components[compindex];
				if (!comp.isVisible()) {
					continue;
				}
				constraints = lookupConstraints(comp);
				model = getModel(comp);
				curX = getGridX(model);
				curY = getGridY(model);
				curWidth = getColSpan(model);
				if (curWidth <= 0) {
					curWidth = 1;
				}
				curHeight = getRowSpan(model);
				if (curHeight <= 0) {
					curHeight = 1;
				}

				/* If x or y is negative, then use relative positioning: */
				if (curX < 0 && curY < 0) {
					if (curRow >= 0) {
						curY = curRow;
					} else if (curCol >= 0) {
						curX = curCol;
					} else {
						curY = 0;
					}
				}
				if (curX < 0) {
					px = 0;
					for (i = curY; i < curY + curHeight; i++) {
						if (xMax[i] > px) {
							px = xMax[i];
						}
					}

					curX = px - curX - 1;
					if (curX < 0) {
						curX = 0;
					}
				} else if (curY < 0) {
					py = 0;
					for (i = curX; i < curX + curWidth; i++) {
						if (yMax[i] > py) {
							py = yMax[i];
						}
					}

					curY = py - curY - 1;
					if (curY < 0) {
						curY = 0;
					}
				}

				/* Adjust the grid width and height */
				// for (px = curX + curWidth; r.width < px; r.width++);
				px = curX + curWidth;
				if (px > r.width) {
					r.width = px;
				}
				// for (py = curY + curHeight; r.height < py; r.height++);
				py = curY + curHeight;
				if (py > r.height) {
					r.height = py;
				}
				/* Adjust the xMax and yMax arrays */
				for (i = curX; i < curX + curWidth; i++) {
					yMax[i] = py;
				}
				for (i = curY; i < curY + curHeight; i++) {
					xMax[i] = px;
				}

				/* Cache the current slave's size. */
				if (sizeflag == PREFERREDSIZE) {
					d = comp.getPreferredSize();
				} else {
					d = comp.getMinimumSize();
				}
				constraints.minWidth = d.width;
				constraints.minHeight = d.height;

				/*
				 * Zero width and height must mean that this is the last item
				 * (or else something is wrong).
				 */
				if (constraints.gridheight == 0 && constraints.gridwidth == 0) {
					curRow = curCol = -1;
				}

				/* Zero width starts a new row */
				if (constraints.gridheight == 0 && curRow < 0) {
					curCol = curX + curWidth;
				} else if (constraints.gridwidth == 0 && curCol < 0) {
					curRow = curY + curHeight;
				}
			}

			/*
			 * Apply minimum row/column dimensions
			 */
			if (columnWidths != null && r.width < columnWidths.length) {
				r.width = columnWidths.length;
			}
			if (rowHeights != null && r.height < rowHeights.length) {
				r.height = rowHeights.length;
			}

			/*
			 * Pass #2
			 * 
			 * Negative values for gridX are filled in with the current x value.
			 * Negative values for gridY are filled in with the current y value.
			 * Negative or zero values for gridWidth and gridHeight end the
			 * current row or column, respectively.
			 */

			curRow = curCol = -1;
			xMax = new int[MAXGRIDSIZE];
			yMax = new int[MAXGRIDSIZE];
			for (compindex = 0; compindex < components.length; compindex++) {
				comp = components[compindex];
				if (!comp.isVisible()) {
					continue;
				}
				constraints = lookupConstraints(comp);

				model = getModel(comp);
				curX = getGridX(model);
				curY = getGridY(model);
				curWidth = getColSpan(model);
				curHeight = getRowSpan(model);

				/* If x or y is negative, then use relative positioning: */
				if (curX < 0 && curY < 0) {
					if (curRow >= 0) {
						curY = curRow;
					} else if (curCol >= 0) {
						curX = curCol;
					} else {
						curY = 0;
					}
				}

				if (curX < 0) {
					if (curHeight <= 0) {
						curHeight += r.height - curY;
						if (curHeight < 1) {
							curHeight = 1;
						}
					}

					px = 0;
					for (i = curY; i < curY + curHeight; i++) {
						if (xMax[i] > px) {
							px = xMax[i];
						}
					}

					curX = px - curX - 1;
					if (curX < 0) {
						curX = 0;
					}
				} else if (curY < 0) {
					if (curWidth <= 0) {
						curWidth += r.width - curX;
						if (curWidth < 1) {
							curWidth = 1;
						}
					}

					py = 0;
					for (i = curX; i < curX + curWidth; i++) {
						if (yMax[i] > py) {
							py = yMax[i];
						}
					}

					curY = py - curY - 1;
					if (curY < 0) {
						curY = 0;
					}
				}

				if (curWidth <= 0) {
					curWidth += r.width - curX;
					if (curWidth < 1) {
						curWidth = 1;
					}
				}

				if (curHeight <= 0) {
					curHeight += r.height - curY;
					if (curHeight < 1) {
						curHeight = 1;
					}
				}

				px = curX + curWidth;
				py = curY + curHeight;

				for (i = curX; i < curX + curWidth; i++) {
					yMax[i] = py;
				}
				for (i = curY; i < curY + curHeight; i++) {
					xMax[i] = px;
				}

				/* Make negative sizes start a new row/column */
				if (constraints.gridheight == 0 && constraints.gridwidth == 0) {
					curRow = curCol = -1;
				}
				if (constraints.gridheight == 0 && curRow < 0) {
					curCol = curX + curWidth;
				} else if (constraints.gridwidth == 0 && curCol < 0) {
					curRow = curY + curHeight;
				}

				/* Assign the new values to the gridbag slave */
				constraints.tempX = curX;
				constraints.tempY = curY;
				constraints.tempWidth = curWidth;
				constraints.tempHeight = curHeight;
			}

			/*
			 * Apply minimum row/column dimensions and weights
			 */
			if (columnWidths != null) {
				System.arraycopy(columnWidths, 0, r.minWidth, 0, columnWidths.length);
			}
			if (rowHeights != null) {
				System.arraycopy(rowHeights, 0, r.minHeight, 0, rowHeights.length);
			}
			if (columnWeights != null) {
				System.arraycopy(columnWeights, 0, r.weightX, 0, columnWeights.length);
			}
			if (rowWeights != null) {
				System.arraycopy(rowWeights, 0, r.weightY, 0, rowWeights.length);
			}

			/*
			 * Pass #3
			 * 
			 * Distribute the minimun widths and weights:
			 */

			nextSize = Integer.MAX_VALUE;

			for (i = 1; i != Integer.MAX_VALUE; i = nextSize, nextSize = Integer.MAX_VALUE) {
				for (compindex = 0; compindex < components.length; compindex++) {
					comp = components[compindex];
					if (!comp.isVisible()) {
						continue;
					}
					constraints = lookupConstraints(comp);

					if (constraints.tempWidth == i) {
						px = constraints.tempX + constraints.tempWidth; /*
																		 * right
																		 * column
																		 */

						/*
						 * Figure out if we should use this slave\'s weight. If
						 * the weight is less than the total weight spanned by
						 * the width of the cell, then discard the weight.
						 * Otherwise split the difference according to the
						 * existing weights.
						 */

						weight_diff = constraints.weightx;
						for (k = constraints.tempX; k < px; k++) {
							weight_diff -= r.weightX[k];
						}
						if (weight_diff > 0.0) {
							weight = 0.0;
							for (k = constraints.tempX; k < px; k++) {
								weight += r.weightX[k];
							}
							for (k = constraints.tempX; weight > 0.0 && k < px; k++) {
								double wt = r.weightX[k];
								double dx = wt * weight_diff / weight;
								r.weightX[k] += dx;
								weight_diff -= dx;
								weight -= wt;
							}
							/* Assign the remainder to the rightmost cell */
							r.weightX[px - 1] += weight_diff;
						}

						/*
						 * Calculate the minWidth array values. First, figure
						 * out how wide the current slave needs to be. Then, see
						 * if it will fit within the current minWidth values. If
						 * it will not fit, add the difference according to the
						 * weightX array.
						 */

						pixels_diff = constraints.minWidth + constraints.ipadx + constraints.insets.left + constraints.insets.right;

						for (k = constraints.tempX; k < px; k++) {
							pixels_diff -= r.minWidth[k];
						}
						if (pixels_diff > 0) {
							weight = 0.0;
							for (k = constraints.tempX; k < px; k++) {
								weight += r.weightX[k];
							}
							for (k = constraints.tempX; weight > 0.0 && k < px; k++) {
								double wt = r.weightX[k];
								int dx = (int) (wt * pixels_diff / weight);
								r.minWidth[k] += dx;
								pixels_diff -= dx;
								weight -= wt;
							}
							/* Any leftovers go into the rightmost cell */
							r.minWidth[px - 1] += pixels_diff;
						}
					} else if (constraints.tempWidth > i && constraints.tempWidth < nextSize) {
						nextSize = constraints.tempWidth;
					}

					if (constraints.tempHeight == i) {
						py = constraints.tempY + constraints.tempHeight; /*
																			    * bottom
																			    * row
																			    */

						/*
						 * Figure out if we should use this slave's weight. If
						 * the weight is less than the total weight spanned by
						 * the height of the cell, then discard the weight.
						 * Otherwise split it the difference according to the
						 * existing weights.
						 */

						weight_diff = constraints.weighty;
						for (k = constraints.tempY; k < py; k++) {
							weight_diff -= r.weightY[k];
						}
						if (weight_diff > 0.0) {
							weight = 0.0;
							for (k = constraints.tempY; k < py; k++) {
								weight += r.weightY[k];
							}
							for (k = constraints.tempY; weight > 0.0 && k < py; k++) {
								double wt = r.weightY[k];
								double dy = wt * weight_diff / weight;
								r.weightY[k] += dy;
								weight_diff -= dy;
								weight -= wt;
							}
							/* Assign the remainder to the bottom cell */
							r.weightY[py - 1] += weight_diff;
						}

						/*
						 * Calculate the minHeight array values. First, figure
						 * out how tall the current slave needs to be. Then, see
						 * if it will fit within the current minHeight values.
						 * If it will not fit, add the difference according to
						 * the weightY array.
						 */

						pixels_diff = constraints.minHeight + constraints.ipady + constraints.insets.top + constraints.insets.bottom;
						for (k = constraints.tempY; k < py; k++) {
							pixels_diff -= r.minHeight[k];
						}
						if (pixels_diff > 0) {
							weight = 0.0;
							for (k = constraints.tempY; k < py; k++) {
								weight += r.weightY[k];
							}
							for (k = constraints.tempY; weight > 0.0 && k < py; k++) {
								double wt = r.weightY[k];
								int dy = (int) (wt * pixels_diff / weight);
								r.minHeight[k] += dy;
								pixels_diff -= dy;
								weight -= wt;
							}
							/* Any leftovers go into the bottom cell */
							r.minHeight[py - 1] += pixels_diff;
						}
					} else if (constraints.tempHeight > i && constraints.tempHeight < nextSize) {
						nextSize = constraints.tempHeight;
					}
				}
			}

			return r;
		}
	}

	/**
	 * @param model
	 * @return
	 */
	private int getRowSpan(IEObject model) {
		if (model instanceof IESequenceTR) {
			return 1;
		} else if (model instanceof SingleWidgetComponentInstance) {
			return 1;
		} else if (model instanceof ITableRowReusableWidget) {
			return 1;
		} else if (model instanceof IETDWidget) {
			return ((IETDWidget) model).getRowSpan();
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Trying to layout an unknown component.");
			}
			return 0;
		}
	}

	/**
	 * @param model
	 * @return
	 */
	private int getColSpan(IEObject model) {
		if (model instanceof IESequenceTR) {
			return ((IESequenceTR) model).getColCount();
		} else if (model instanceof SingleWidgetComponentInstance) {
			return ((IESequenceTR) ((SingleWidgetComponentInstance) model).getReusableWidget().getRootObject()).getColCount();
		} else if (model instanceof ITableRowReusableWidget) {
			return ((ITableRowReusableWidget) model).getColCount();
		} else if (model instanceof IETDWidget) {
			return ((IETDWidget) model).getColSpan();
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Trying to layout an unknown component.");
			}
			return 0;
		}
	}

	/**
	 * @param model
	 * @return
	 */
	private int getGridY(IEObject model) {
		if (model instanceof IESequenceTR) {
			return ((IESequenceTR) model).getIndex();
		} else if (model instanceof IETDWidget) {
			return ((IETDWidget) model).tr().getIndex();
		} else if (model instanceof SingleWidgetComponentInstance) {
			return ((ITableRowReusableWidget) ((SingleWidgetComponentInstance) model).getReusableWidget()).getRowIndex();
		} else if (model instanceof ITableRowReusableWidget) {
			return ((ITableRowReusableWidget) model).getRowIndex();
		}
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Trying to layout an unknown component:" + model.getClass());
		}
		return 0;
	}

	/**
	 * @param model
	 * @return
	 */
	private int getGridX(IEObject model) {
		if (model instanceof IESequenceTR) {
			return 0;
		} else if (model instanceof SingleWidgetComponentInstance) {
			return 0;
		} else if (model instanceof IETDWidget) {
			return ((IETDWidget) model).getXLocation();
		} else if (model instanceof ITableRowReusableWidget) {
			return 0;
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Trying to layout an unknown component.");
			}
			return 0;
		}
	}

	/**
	 * Adjusts the x, y, width, and height fields to the correct values depending on the constraint geometry and pads.
	 * 
	 * @param constraints
	 *            the constraints to be applied
	 * @param r
	 *            the <code>Rectangle</code> to be adjusted
	 * @since 1.4
	 */
	protected void adjustForGravity(IEHTMLTableConstraints constraints, Rectangle r) {
		int diffx, diffy;

		if (!rightToLeft) {
			r.x += constraints.insets.left;
		} else {
			r.x -= r.width - constraints.insets.right;
		}
		r.width -= constraints.insets.left + constraints.insets.right;
		r.y += constraints.insets.top;
		r.height -= constraints.insets.top + constraints.insets.bottom;

		diffx = 0;
		if (constraints.fill != IEHTMLTableConstraints.HORIZONTAL && constraints.fill != IEHTMLTableConstraints.BOTH
				&& r.width > constraints.minWidth + constraints.ipadx) {
			diffx = r.width - (constraints.minWidth + constraints.ipadx);
			r.width = constraints.minWidth + constraints.ipadx;
		}

		diffy = 0;
		if (constraints.fill != IEHTMLTableConstraints.VERTICAL && constraints.fill != IEHTMLTableConstraints.BOTH
				&& r.height > constraints.minHeight + constraints.ipady) {
			diffy = r.height - (constraints.minHeight + constraints.ipady);
			r.height = constraints.minHeight + constraints.ipady;
		}

		switch (constraints.anchor) {
		case IEHTMLTableConstraints.CENTER:
			r.x += diffx / 2;
			r.y += diffy / 2;
			break;
		case IEHTMLTableConstraints.PAGE_START:
		case IEHTMLTableConstraints.NORTH:
			r.x += diffx / 2;
			break;
		case IEHTMLTableConstraints.NORTHEAST:
			r.x += diffx;
			break;
		case IEHTMLTableConstraints.EAST:
			r.x += diffx;
			r.y += diffy / 2;
			break;
		case IEHTMLTableConstraints.SOUTHEAST:
			r.x += diffx;
			r.y += diffy;
			break;
		case IEHTMLTableConstraints.PAGE_END:
		case IEHTMLTableConstraints.SOUTH:
			r.x += diffx / 2;
			r.y += diffy;
			break;
		case IEHTMLTableConstraints.SOUTHWEST:
			r.y += diffy;
			break;
		case IEHTMLTableConstraints.WEST:
			r.y += diffy / 2;
			break;
		case IEHTMLTableConstraints.NORTHWEST:
			break;
		case IEHTMLTableConstraints.LINE_START:
			if (rightToLeft) {
				r.x += diffx;
			}
			r.y += diffy / 2;
			break;
		case IEHTMLTableConstraints.LINE_END:
			if (!rightToLeft) {
				r.x += diffx;
			}
			r.y += diffy / 2;
			break;
		case IEHTMLTableConstraints.FIRST_LINE_START:
			if (rightToLeft) {
				r.x += diffx;
			}
			break;
		case IEHTMLTableConstraints.FIRST_LINE_END:
			if (!rightToLeft) {
				r.x += diffx;
			}
			break;
		case IEHTMLTableConstraints.LAST_LINE_START:
			if (rightToLeft) {
				r.x += diffx;
			}
			r.y += diffy;
			break;
		case IEHTMLTableConstraints.LAST_LINE_END:
			if (!rightToLeft) {
				r.x += diffx;
			}
			r.y += diffy;
			break;
		default:
			throw new IllegalArgumentException("illegal anchor value");
		}
	}

	/**
	 * Figures out the minimum size of the master based on the information from getLayoutInfo().
	 * 
	 * @param parent
	 *            the layout container
	 * @param info
	 *            the layout info for this parent
	 * @return a <code>Dimension</code> object containing the minimum size
	 * @since 1.4
	 */
	protected Dimension getMinSize(Container parent, GridBagLayoutInfo info) {
		return GetMinSize(parent, info);
	}

	/**
	 * This method is obsolete and supplied for backwards compatability only; new code should call
	 * {@link #getMinSize(Container, GridBagLayoutInfo) getMinSize} instead.
	 */
	protected Dimension GetMinSize(Container parent, GridBagLayoutInfo info) {
		Dimension d = new Dimension();
		int i, t;
		Insets insets = parent.getInsets();
		insets.bottom += BORDER_SIZE;
		insets.top += BORDER_SIZE;
		t = 0;
		for (i = 0; i < info.width; i++) {
			t += info.minWidth[i];
		}
		d.width = t + insets.left + insets.right;

		t = 0;
		for (i = 0; i < info.height; i++) {
			t += info.minHeight[i];
		}
		d.height = t + insets.top + insets.bottom;

		return d;
	}

	transient boolean rightToLeft = false;

}
