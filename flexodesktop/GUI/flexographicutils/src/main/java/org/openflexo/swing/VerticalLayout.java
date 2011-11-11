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
/* Copyright (c) 1997 by Groupe Bull.  All Rights Reserved */
/* $Id: VerticalLayout.java,v 1.2 2011/09/12 11:47:24 gpolet Exp $ */
/* Author: Jean-Michel.Leon@sophia.inria.fr */

package org.openflexo.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 * A simple layout that arranges its components vertically all the components are given the same width and keep their own height.
 * 
 */

public class VerticalLayout implements LayoutManager {

	final static private int VGAP = 0;

	final static private int HMARGIN = 0;

	final static private int VMARGIN = 0;

	protected int vgap;

	protected int hmargin;

	protected int vmargin;

	/**
	 * Constructs a new VerticalLayout.
	 */
	public VerticalLayout() {
		this(VGAP, HMARGIN, VMARGIN);
	}

	/**
	 * Constructs a new VerticalLayout with specific gap and margin.
	 * 
	 * @param gap
	 * @param h
	 * @param v
	 */
	public VerticalLayout(int gap, int h, int v) {
		vgap = gap;
		hmargin = h;
		vmargin = v;
	}

	/**
	 * Adds the specified named component to the layout.
	 * 
	 * @param name
	 *            the String name
	 * @param comp
	 *            the component to be added
	 */
	@Override
	public void addLayoutComponent(String name, Component comp) {
		// interface method
	}

	/**
	 * Removes the specified component from the layout.
	 * 
	 * @param comp
	 *            the component to be removed
	 */
	@Override
	public void removeLayoutComponent(Component comp) {
		// interface method
	}

	@Override
	public Dimension minimumLayoutSize(Container target) {
		return preferredLayoutSize(target);
	}

	@Override
	public Dimension preferredLayoutSize(Container target) {
		Dimension dim = new Dimension(0, 0);
		int width = 0;
		int height = 0;
		Component children[] = target.getComponents();
		int length = 0;
		for (int i = 0; i < children.length; i++) {
			if (!children[i].isVisible())
				continue;
			Dimension d = children[i].getPreferredSize();
			width = Math.max(width, d.width);
			height += d.height;
			length++;
		}
		height += vgap * (length + 1) + vmargin * 2 * length;
		Insets insets = target.getInsets();
		dim.width = width + insets.left + insets.right + hmargin * 2;
		dim.height = height + insets.top + insets.bottom;
		return dim;
	}

	/**
	 * Lays out the specified container.
	 * 
	 * @param target
	 *            the component being laid out
	 * @see Container
	 */
	@Override
	public void layoutContainer(Container target) {
		Insets insets = target.getInsets();
		int top = insets.top, left = insets.left + hmargin;
		int width = target.getSize().width - left - insets.right - hmargin;
		Component children[] = target.getComponents();
		// available vertical space
		int vroom = target.getSize().height - top - insets.bottom - vmargin * 2;

		top += vgap;
		for (int i = 0; i < children.length; i++) {
			if (!children[i].isVisible())
				continue;
			int h = children[i].getPreferredSize().height + vmargin * 2;
			children[i].setBounds(left, top, width, h);
			top += h + vgap;
			vroom -= (h + vgap);
		}
	}

	/**
	 * Returns the String representation of this BorderLayout's values.
	 * 
	 * @return
	 */
	@Override
	public String toString() {
		return getClass().getName() + ",vgap=" + vgap + "]";
	}

}
