/*
 * $Id: MultiSplitLayout.java 3957 2011-03-15 18:27:26Z kschaefe $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openflexo.fib.model;

import java.util.List;

import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.swing.layout.MultiSplitLayout;
import org.openflexo.swing.layout.MultiSplitLayout.ColSplit;
import org.openflexo.swing.layout.MultiSplitLayout.Divider;
import org.openflexo.swing.layout.MultiSplitLayout.Leaf;
import org.openflexo.swing.layout.MultiSplitLayout.Node;
import org.openflexo.swing.layout.MultiSplitLayout.RowSplit;
import org.openflexo.swing.layout.MultiSplitLayout.Split;
import org.openflexo.swing.layout.MultiSplitLayoutFactory;

public class FIBMultiSplitLayoutFactory implements MultiSplitLayoutFactory {

	private final FIBModelFactory factory;

	public FIBMultiSplitLayoutFactory(FIBModelFactory factory) {
		this.factory = factory;
	}

	@Override
	public FIBDivider makeDivider() {
		return factory.newInstance(FIBDivider.class);
	}

	@Override
	public FIBLeaf makeLeaf() {
		return factory.newInstance(FIBLeaf.class);
	}

	@Override
	public FIBLeaf makeLeaf(String name) {
		return factory.newInstance(FIBLeaf.class);
	}

	@Override
	public FIBSplit makeSplit() {
		return factory.newInstance(FIBSplit.class);
	}

	@Override
	public FIBColSplit makeColSplit() {
		return factory.newInstance(FIBColSplit.class);
	}

	@Override
	public FIBRowSplit makeRowSplit() {
		return factory.newInstance(FIBRowSplit.class);
	}

	@Override
	public FIBRowSplit makeRowSplit(Leaf left, Divider divider, Leaf right) {
		return factory.newInstance(FIBRowSplit.class);
	}

	@Override
	public FIBColSplit makeColSplit(Leaf top, Divider divider, Leaf bottom) {
		return factory.newInstance(FIBColSplit.class);
	}

	/**
	 * API for the nodes that model a MultiSplitLayout.
	 */
	@ModelEntity(isAbstract = true)
	@ImplementationClass(MultiSplitLayout.DefaultNode.class)
	public static interface FIBNode extends Node {

	}

	/**
	 * Models a single vertical/horiztonal divider.
	 */
	@ModelEntity
	@ImplementationClass(MultiSplitLayout.DefaultDivider.class)
	@XMLElement(xmlTag = "Divider")
	public static interface FIBDivider extends FIBNode, Divider {

	}

	/**
	 * Models a java.awt Component child.
	 */
	@ModelEntity
	@ImplementationClass(MultiSplitLayout.DefaultLeaf.class)
	@XMLElement(xmlTag = "Divider")
	public static interface FIBLeaf extends FIBNode, Leaf {

		@PropertyIdentifier(type = String.class)
		public static final String NAME_KEY = "name";

		@PropertyIdentifier(type = double.class)
		public static final String WEIGHT_KEY = "weight";

		@Override
		@Getter(NAME_KEY)
		@XMLAttribute
		public String getName();

		@Override
		@Setter(NAME_KEY)
		public void setName(String name);

		@Override
		@Getter(WEIGHT_KEY)
		@XMLAttribute
		public double getWeight();

		@Override
		@Setter(WEIGHT_KEY)
		public void setWeight(double weight);

	}

	/**
	 * Defines a vertical or horizontal subdivision into two or more tiles.
	 */
	@ModelEntity(isAbstract = true)
	@ImplementationClass(MultiSplitLayout.DefaultSplit.class)
	public static interface FIBSplit extends FIBNode, Split<FIBNode> {

		@PropertyIdentifier(type = String.class)
		public static final String NAME_KEY = "name";

		@PropertyIdentifier(type = double.class)
		public static final String WEIGHT_KEY = "weight";

		@PropertyIdentifier(type = List.class)
		public static final String CHILDREN_KEY = "children";

		@Override
		@Getter(NAME_KEY)
		@XMLAttribute
		public String getName();

		@Override
		@Setter(NAME_KEY)
		public void setName(String name);

		@Override
		@Getter(WEIGHT_KEY)
		@XMLAttribute
		public double getWeight();

		@Override
		@Setter(WEIGHT_KEY)
		public void setWeight(double weight);

		@Override
		@Getter(value = CHILDREN_KEY, cardinality = Cardinality.LIST)
		public List<FIBNode> getChildren();

		@Override
		@Setter(CHILDREN_KEY)
		public void setChildren(List<FIBNode> children);

		@Override
		@Adder(CHILDREN_KEY)
		public void addToChildren(FIBNode child);

		@Override
		@Remover(CHILDREN_KEY)
		public void removeFromChildren(FIBNode child);
	}

	/**
	 * Defines a horizontal subdivision into two or more tiles.
	 */
	@ModelEntity
	@ImplementationClass(MultiSplitLayout.DefaultRowSplit.class)
	@XMLElement(xmlTag = "RowSplit")
	public static interface FIBRowSplit extends FIBSplit, RowSplit<FIBNode> {

	}

	/**
	 * Defines a vertical subdivision into two or more tiles.
	 */
	@ModelEntity
	@ImplementationClass(MultiSplitLayout.DefaultColSplit.class)
	@XMLElement(xmlTag = "ColSplit")
	public static interface FIBColSplit extends FIBSplit, ColSplit<FIBNode> {

	}

}
