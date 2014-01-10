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

package org.openflexo.swing.layout;

import org.openflexo.swing.layout.MultiSplitLayout.ColSplit;
import org.openflexo.swing.layout.MultiSplitLayout.DefaultColSplit;
import org.openflexo.swing.layout.MultiSplitLayout.DefaultDivider;
import org.openflexo.swing.layout.MultiSplitLayout.DefaultLeaf;
import org.openflexo.swing.layout.MultiSplitLayout.DefaultRowSplit;
import org.openflexo.swing.layout.MultiSplitLayout.DefaultSplit;
import org.openflexo.swing.layout.MultiSplitLayout.Divider;
import org.openflexo.swing.layout.MultiSplitLayout.Leaf;
import org.openflexo.swing.layout.MultiSplitLayout.RowSplit;
import org.openflexo.swing.layout.MultiSplitLayout.Split;

public interface MultiSplitLayoutFactory {

	public <N extends Divider<N>> Divider<N> makeDivider();

	public <N extends Leaf<N>> Leaf<N> makeLeaf();

	public <N extends Leaf<N>> Leaf<N> makeLeaf(String name);

	public <N extends Split<N>> Split<N> makeSplit();

	public <N extends ColSplit<N>> ColSplit<?> makeColSplit();

	public <N extends ColSplit<N>> ColSplit<?> makeColSplit(Leaf<?> top, Divider<?> divider, Leaf<?> bottom);

	public <N extends RowSplit<N>> RowSplit<?> makeRowSplit();

	public <N extends RowSplit<N>> RowSplit<?> makeRowSplit(Leaf<?> left, Divider<?> divider, Leaf<?> right);

	public static class DefaultMultiSplitLayoutFactory implements MultiSplitLayoutFactory {

		@Override
		public <N extends Divider<N>> Divider<N> makeDivider() {
			return new DefaultDivider<N>();
		}

		@Override
		public <N extends Leaf<N>> Leaf<N> makeLeaf() {
			return new DefaultLeaf<N>();
		}

		@Override
		public <N extends Leaf<N>> Leaf<N> makeLeaf(String name) {
			return new DefaultLeaf<N>(name);
		}

		@Override
		public <N extends Split<N>> Split<N> makeSplit() {
			return new DefaultSplit<N>();
		}

		@Override
		public <N extends ColSplit<N>> ColSplit<N> makeColSplit() {
			return new DefaultColSplit<N>();
		}

		@Override
		public <N extends ColSplit<N>> ColSplit<N> makeColSplit(Leaf<?> top, Divider<?> divider, Leaf<?> bottom) {
			return new DefaultColSplit((Leaf) top, (Divider) divider, (Leaf) bottom);
		}

		@Override
		public <N extends RowSplit<N>> RowSplit<N> makeRowSplit() {
			return new DefaultRowSplit<N>();
		}

		@Override
		public RowSplit<?> makeRowSplit(Leaf left, Divider divider, Leaf right) {
			return new DefaultRowSplit(left, divider, right);
		}

	}

}
