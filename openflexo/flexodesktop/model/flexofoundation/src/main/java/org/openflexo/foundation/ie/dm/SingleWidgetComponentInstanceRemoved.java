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
package org.openflexo.foundation.ie.dm;

import org.openflexo.foundation.ie.SingleWidgetComponentInstance;

/**
 * @author bmangez
 * @version $Id: SingleWidgetComponentInstanceRemoved.java,v 1.1.2.2 2005/08/19
 *          16:45:38 sguerin Exp $ $Log:
 *          SingleWidgetComponentInstanceRemoved.java,v $ Revision 1.1.2.2
 *          2005/08/19 16:45:38 sguerin Commit on 19/08/2005, Sylvain GUERIN,
 *          version 7.1.10.alpha See committing documentation
 * 
 * Revision 1.1.2.1 2005/06/28 12:53:52 benoit ReusableComponents
 * 
 * 
 * <B>Class Description</B>
 */
public class SingleWidgetComponentInstanceRemoved extends IEDataModification
{

    public SingleWidgetComponentInstanceRemoved(SingleWidgetComponentInstance removed)
    {
        super(removed, null);
    }

}
