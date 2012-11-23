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
package org.openflexo.ie.view;

import java.awt.BorderLayout;

import javax.swing.JTabbedPane;

import org.openflexo.ie.IECst;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.FlexoRelativeWindow;

/**
 * @author bmangez
 * @version $Id: ComponentLibraryBrowserWindow.java,v 1.1.2.3 2005/08/04 16:22:05 sguerin Exp $ $Log: ComponentLibraryBrowserWindow.java,v $
 *          16:22:05 sguerin Exp $ Revision 1.8.4.1 2008/12/23 13:53:59 gpolet 16:22:05 sguerin Exp $ LOW: Merge from HEAD 16:22:05 sguerin
 *          Exp $ 16:22:05 sguerin Exp $ Revision 1.9 2008/12/22 13:02:40 gpolet 16:22:05 sguerin Exp $ LOW: nullified some references when
 *          disposing browser window 16:22:05 sguerin Exp $ 16:22:05 sguerin Exp $ Revision 1.8 2008/09/30 15:45:49 gpolet 16:22:05 sguerin
 *          Exp $ LOW: Organized imports 16:22:05 sguerin Exp $ 16:22:05 sguerin Exp $ Revision 1.7 2007/09/17 15:00:53 gpolet 16:22:05
 *          sguerin Exp $ IMPORTANT: First merge of branch b_1_1_0 from Root_b_1_1_0 until t_first_merge (after t_1_1_0RC10) 16:22:05
 *          sguerin Exp $ 16:22:05 sguerin Exp $ Revision 1.6.2.1 2007/05/31 10:49:12 bmangez 16:22:05 sguerin Exp $ LOW/organize import
 *          16:22:05 sguerin Exp $ 16:22:05 sguerin Exp $ Revision 1.6 2007/05/02 11:31:44 bmangez 16:22:05 sguerin Exp $ MEDIUM/synchronize
 *          module view with selected tab in browser. (switch to last dkv view when you change the displayed tab in ie browser, ...)
 *          16:22:05 sguerin Exp $ 16:22:05 sguerin Exp $ Revision 1.5 2007/03/12 13:18:14 bmangez 16:22:05 sguerin Exp $ IMPORTANT/remove
 *          IETable deprecated code 16:22:05 sguerin Exp $ 16:22:05 sguerin Exp $ Revision 1.4 2006/07/28 10:46:48 gpolet 16:22:05 sguerin
 *          Exp $ LOW: Organize imports 16:22:05 sguerin Exp $ 16:22:05 sguerin Exp $ Revision 1.3 2006/03/23 09:47:37 sguerin 16:22:05
 *          sguerin Exp $ FTS-ERG-DEV-003 - Palette restructuration - Module windows restructuration - 'Windows" menu restructuration
 *          16:22:05 sguerin Exp $ 16:22:05 sguerin Exp $ Revision 1.2 2006/02/02 15:51:13 bmangez 16:22:05 sguerin Exp $ merge from bdev
 *          16:22:05 sguerin Exp $ 16:22:05 sguerin Exp $ Revision 1.1.2.4 2005/10/03 11:50:58 benoit 16:22:05 sguerin Exp $ organize import
 *          format code logger test 16:22:05 sguerin Exp $ Revision 1.1.2.3 2005/08/04 16:22:05 sguerin Commit on 04/08/2005, Sylvain
 *          GUERIN, version 7.1.6.alpha Temporary commit, see next commit
 * 
 *          Revision 1.1.2.2 2005/07/14 13:12:31 benoit *** empty log message ***
 * 
 *          Revision 1.1.2.1 2005/06/28 12:54:02 benoit ReusableComponents
 * 
 * 
 *          <B>Class Description</B>
 */
public class ComponentLibraryBrowserWindow extends FlexoRelativeWindow {

	private ComponentLibraryBrowserView _wkfBrowserView;

	private MenuEditorBrowserView _menuEditorBrowserView;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public ComponentLibraryBrowserWindow(FlexoFrame mainFrame) {
		super(mainFrame);
		getContentPane().setLayout(new BorderLayout());

		_wkfBrowserView = new ComponentLibraryBrowserView((IEController) getController());
		_wkfBrowserView.setName("Library");
		_menuEditorBrowserView = new MenuEditorBrowserView((IEController) getController());
		_menuEditorBrowserView.setName("Menu");
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add(_wkfBrowserView);
		tabbedPane.add(_menuEditorBrowserView);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		setTitle(FlexoLocalization.localizedForKey(getName()));
		setSize(IECst.DEFAULT_COMPONENT_LIBRARY_BROWSER_WINDOW_WIDTH, IECst.DEFAULT_COMPONENT_LIBRARY_BROWSER_WINDOW_HEIGHT);
		setLocation(IECst.IE_WINDOW_WIDTH + 2, IECst.DEFAULT_PALETTE_HEIGHT + 25);

		validate();
		pack();
	}

	@Override
	public void dispose() {
		_wkfBrowserView = null;
		_menuEditorBrowserView = null;
		super.dispose();
	}

	@Override
	public String getName() {
		return IECst.DEFAULT_COMPONENT_LIBRARY_BROWSER_WINDOW_TITLE;
	}

}
