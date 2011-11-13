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
package org.openflexo.inspector;

import javax.swing.JComponent;

/**
 * @author bmangez
 * @version $Id: ExternalizedInspectableObject.java,v 1.1.2.3 2005/10/03 07:28:31 benoit Exp $ $Log: ExternalizedInspectableObject.java,v $
 *          07:28:31 benoit Exp $ Revision 1.1.2.4 2005/10/03 11:51:05 benoit 07:28:31 benoit Exp $ organize import format code logger test
 *          07:28:31 benoit Exp $ Revision 1.1.2.3 2005/10/03 07:28:31 benoit *** empty log message ***
 * 
 *          Revision 1.1.2.2 2005/07/22 16:32:02 sguerin Commit on 22/07/2005, Sylvain GUERIN, version 7.1.6.alpha See committing
 *          documentation
 * 
 *          Revision 1.1.2.1 2005/06/28 12:54:03 benoit ReusableComponents
 * 
 * 
 *          <B>Class Description</B>
 */
public interface ExternalizedInspectableObject extends InspectableObject {

	public void showInspector(JComponent parent);

}
