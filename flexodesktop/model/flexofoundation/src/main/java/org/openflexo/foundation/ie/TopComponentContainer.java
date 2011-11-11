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
package org.openflexo.foundation.ie;

import java.util.Enumeration;
import java.util.Iterator;

import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.dm.TopComponentInserted;
import org.openflexo.foundation.ie.dm.TopComponentRemoved;
import org.openflexo.foundation.ie.widget.IESequenceTopComponent;
import org.openflexo.foundation.ie.widget.TopComponentReusableWidget;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.xmlcode.XMLSerializable;

/**
 * @author bmangez
 * @version $Id: TopComponentContainer.java,v 1.1.2.5 2005/10/03 07:31:30 benoit Exp $ $Log: TopComponentContainer.java,v $ Exp $ Revision
 *          1.2 2011/09/12 11:46:51 gpolet Exp $ Converted v2 to v3 Exp $ Exp $ Revision 1.1 2011/05/24 01:12:13 gpolet Exp $ LOW: First
 *          import of OpenFlexo Exp $ Exp $ Revision 1.1.2.2 2011/05/20 14:23:30 gpolet Exp $ LOW: Added GPL v2 file header Exp $ Exp $
 *          Revision 1.1.2.1 2011/05/20 08:26:32 gpolet Exp $ Package refactor of flexofoundation Exp $ Exp $ Revision 1.1.2.1 2011/05/19
 *          09:39:45 gpolet Exp $ refactored package names Exp $ Exp $ Revision 1.11 2008/07/16 11:13:07 bmangez Exp $ IMPORTANT/newWKF
 *          (merge from b_newwkf) Exp $ Exp $ Revision 1.9.6.1 2008/07/04 23:42:12 sguerin Exp $ IMPORTANT / Merging b_1_3_new_WKF with
 *          actual HEAD (05/07/2008) / Also contains some other work on new WKF model Exp $ Exp $ Revision 1.10 2008/06/30 14:40:17 gpolet
 *          Exp $ IMPORTANT: See commit mail on refactoring IE (26/06/2008) Exp $ Exp $ Revision 1.9 2007/09/17 14:38:55 gpolet Exp $
 *          IMPORTANT: First merge of branch b_1_1_0 from Root_b_1_1_0 until t_first_merge (after t_1_1_0RC10) Exp $ Exp $ Revision 1.8.10.1
 *          2007/05/31 11:56:38 bmangez Exp $ LOW/organize import Exp $ Exp $ Revision 1.8 2006/09/08 13:31:54 gpolet Exp $ LOW: Added
 *          notification so that IETabWidget in browser displays the correct subtree Exp $ Exp $ Revision 1.7 2006/09/06 13:00:17 gpolet Exp
 *          $ LOW: Fixed TabWidget in browser Exp $ Exp $ Revision 1.6 2006/08/31 16:10:43 gpolet Exp $ LOW: Adapted interface to meet
 *          typing Exp $ Exp $ Revision 1.5 2006/07/28 10:46:07 gpolet Exp $ LOW: Organize imports Exp $ Exp $ Revision 1.4 2006/06/26
 *          11:22:04 bmangez Exp $ IMPORTANT:Organize import + Sequence dans IE Exp $ Exp $ Revision 1.3 2006/05/22 10:21:08 gpolet Exp $
 *          LOW: moved topComponent insertion/removal to lowest method level Exp $ Exp $ Revision 1.2 2006/02/02 15:30:32 bmangez Exp $
 *          merge from bdev Exp $ Exp $ Revision 1.1.2.6 2005/10/03 11:50:46 benoit Exp $ organize import format code logger test Exp $
 *          Revision 1.1.2.5 2005/10/03 07:31:30 benoit *** empty log message ***
 * 
 *          Revision 1.1.2.4 2005/08/19 16:45:38 sguerin Commit on 19/08/2005, Sylvain GUERIN, version 7.1.10.alpha See committing
 *          documentation
 * 
 *          Revision 1.1.2.3 2005/08/04 16:20:19 sguerin Commit on 04/08/2005, Sylvain GUERIN, version 7.1.6.alpha Temporary commit, see
 *          next commit
 * 
 *          Revision 1.1.2.2 2005/07/08 13:58:32 benoit *** empty log message ***
 * 
 *          Revision 1.1.2.1.4.1 2005/07/08 13:31:26 benoit *** empty log message ***
 * 
 *          Revision 1.1.2.1 2005/06/28 12:53:52 benoit ReusableComponents
 * 
 * 
 *          <B>Class Description</B>
 */
@Deprecated
public interface TopComponentContainer extends XMLSerializable {
	public int indexOfTopComponent(IETopComponent topComponent);

	public void replaceWidgetByReusable(IETopComponent oldWidget, TopComponentReusableWidget newWidget);

	// ==========================================================================
	// ========================== Generic methods
	// ===============================
	// ==========================================================================
	public FlexoResource getFlexoResource();

	public FlexoProject getProject();

	public Enumeration topComponents();

	public Iterator<IETopComponent> iterator();

	public int length();

	public IESequenceTopComponent getTopComponentList();

	public void setTopComponentList(IESequenceTopComponent componentList);

	public void addToTopComponentList(IETopComponent component);

	public void removeFromTopComponentList(IETopComponent component);

	public void removeTopComponent(IETopComponent component);

	public void insertTopComponentAtIndex(IETopComponent component, int index);

	public ComponentDefinition getComponentDefinition();

	public void notifyTopComponentInserted(TopComponentInserted tci);

	public void notifyTopComponentRemoved(TopComponentRemoved tcr);
}
