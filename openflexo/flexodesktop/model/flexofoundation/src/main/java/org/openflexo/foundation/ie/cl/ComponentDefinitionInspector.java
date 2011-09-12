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
package org.openflexo.foundation.ie.cl;

import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * @author bmangez
 * @version $Id: ComponentDefinitionInspector.java,v 1.1.2.2 2005/08/19 16:45:38
 *          sguerin Exp $ $Log: ComponentDefinitionInspector.java,v $
 *          sguerin Exp $ Revision 1.1.2.3  2005/10/03 11:50:47  benoit
 *          sguerin Exp $ organize importformat codelogger test
 *          sguerin Exp $ Revision
 *          1.1.2.2 2005/08/19 16:45:38 sguerin Commit on 19/08/2005, Sylvain
 *          GUERIN, version 7.1.10.alpha See committing documentation
 * 
 * Revision 1.1.2.1 2005/08/04 16:20:20 sguerin Commit on 04/08/2005, Sylvain
 * GUERIN, version 7.1.6.alpha Temporary commit, see next commit
 * 
 * Revision 1.1.2.2 2005/07/20 13:24:40 sguerin Commit on 20/07/2005, Sylvain
 * GUERIN, version 7.1.5.alpha Temporary commit, see next commit
 * 
 * Revision 1.1.2.1 2005/06/28 12:53:53 benoit ReusableComponents
 * 
 * 
 * <B>Class Description</B>
 */
public class ComponentDefinitionInspector extends JComponent
{

    private ComponentDefinition _componentDefinition;

    /**
     * @param model
     */
    public ComponentDefinitionInspector(ComponentDefinition compDef)
    {
        super();
        _componentDefinition = compDef;
        add(new JLabel("ComponentDefinitionInspector"));
    }

}
