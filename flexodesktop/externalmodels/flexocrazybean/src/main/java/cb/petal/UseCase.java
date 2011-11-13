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
package cb.petal;

/**
 * Represents UseCase object
 * 
 * @version $Id: UseCase.java,v 1.3 2011/09/12 11:46:47 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class UseCase extends Inheritable {
	static final long serialVersionUID = -6446937716836493799L;

	public UseCase(PetalNode parent, java.util.Collection params) {
		super(parent, "UseCase", params);
	}

	public UseCase() {
		super("UseCase");
	}

	/**
	 * Add super use case of this use case, i.e. adds InheritanceRelationship to "superclasses" list.
	 * 
	 * @return implicitly created relationship object
	 */
	public InheritanceRelationship addSuperUseCase(UseCase caze) {
		return addSuperClassifier(caze);
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
