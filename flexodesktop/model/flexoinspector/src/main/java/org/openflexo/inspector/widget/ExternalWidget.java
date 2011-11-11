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
package org.openflexo.inspector.widget;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.ExternalizedInspectableObject;
import org.openflexo.inspector.model.PropertyModel;

/**
 * @author bmangez
 * @version $Id: ExternalWidget.java,v 1.3 2011/09/12 11:47:21 gpolet Exp $ $Log: ExternalWidget.java,v $ Revision 1.3 2011/09/12 11:47:21
 *          gpolet Converted v2 to v3
 * 
 *          Revision 1.2 2011/06/21 14:46:33 gpolet MEDIUM: Added all missing @Override
 * 
 *          Revision 1.1 2011/05/24 01:12:41 gpolet LOW: First import of OpenFlexo
 * 
 *          Revision 1.1.2.2 2011/05/20 14:16:32 gpolet LOW: Added GPL v2 file header
 * 
 *          Revision 1.1.2.1 2011/05/19 09:39:46 gpolet refactored package names
 * 
 *          Revision 1.7 2009/01/05 10:02:20 gpolet LOW: merge from b_temp_HEAD
 * 
 *          Revision 1.6.4.1 2008/12/11 18:43:18 sguerin MEDIUM / Refactored and clean many things / Also implement conditional display of
 *          tabs using inspecting context (string dictionary)
 * 
 *          Revision 1.6 2008/09/30 15:47:26 gpolet LOW: Organized imports
 * 
 *          Revision 1.5 2007/03/22 10:08:42 sguerin MEDIUM / Better management of AbstractController
 * 
 *          Revision 1.4 2006/07/28 10:47:01 gpolet LOW: Organize imports
 * 
 *          Revision 1.3 2006/04/20 09:24:26 bmangez IMPORTANT/Repetition and Conditionnal + some code cleaning
 * 
 *          Revision 1.2 2006/02/02 15:36:13 bmangez merge from bdev
 * 
 *          Revision 1.1.2.6 2005/10/07 07:22:21 benoit warnings removed
 * 
 *          Revision 1.1.2.5 2005/10/03 11:51:03 benoit organize import format code logger test Revision 1.1.2.4 2005/10/03 07:28:31 benoit
 *          *** empty log message ***
 * 
 *          Revision 1.1.2.3 2005/09/14 15:14:07 sguerin Commit on 14/09/2005, Sylvain GUERIN, version 7.1.12.alpha See committing
 *          documentation
 * 
 *          Revision 1.1.2.2 2005/07/22 16:32:02 sguerin Commit on 22/07/2005, Sylvain GUERIN, version 7.1.6.alpha See committing
 *          documentation
 * 
 *          Revision 1.1.2.1 2005/06/28 12:54:03 benoit ReusableComponents
 * 
 * 
 *          <B>Class Description</B>
 */
public class ExternalWidget extends DenaliWidget {

	JButton openButton;

	public ExternalWidget(PropertyModel model, AbstractController controller) {
		super(model, controller);
		if (getDynamicComponent() != null)
			getDynamicComponent().addFocusListener(new WidgetFocusListener(this));

	}

	@Override
	public synchronized void updateWidgetFromModel() {

	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized void updateModelFromWidget() {

	}

	@Override
	public JComponent getDynamicComponent() {
		openButton = new JButton("...");
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (getObjectValue() == null) {
					// System.out.println("getObjectValue() is null");
				} else {
					if (getModel() == null) {
						// System.out.println("getModel() is null");
					} else {
						((ExternalizedInspectableObject) getModel().objectForKey(getObservedPropertyName()))
								.showInspector((JComponent) openButton.getParent());

					}
				}
			}
		});
		return openButton;
	}

	@Override
	public Class getDefaultType() {
		return Object.class;
	}

}
