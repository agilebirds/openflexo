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
package cb.util;

import cb.petal.DescendingVisitor;
import cb.petal.EmptyVisitor;
import cb.petal.PetalObject;
import cb.petal.Visitor;

/**
 * Just take a visitor "piggy-backed" and apply it to all petal objects during traversal. This useful when translating petal files to other
 * structures, e.g., a converter to Java. This class is related to the Builder pattern; the piggy-backed visitor will usually be a subclass
 * of EmptyVisitor.
 * 
 * @see EmptyVisitor
 * 
 * @version $Id: PiggybackVisitor.java,v 1.3 2011/09/12 11:47:29 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class PiggybackVisitor extends DescendingVisitor {
	private Visitor v;

	public PiggybackVisitor(Visitor v) {
		this.v = v;
	}

	@Override
	public void visitObject(PetalObject obj) {
		obj.accept(v);
		super.visitObject(obj);
	}
}
