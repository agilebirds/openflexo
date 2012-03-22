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
package org.openflexo.ve.view;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.dnd.TreeDropTarget;
import org.openflexo.components.browser.ontology.ShemaDefinitionElement;
import org.openflexo.components.browser.ontology.ShemaFolderElement;
import org.openflexo.components.browser.ontology.ShemaLibraryElement;
import org.openflexo.components.browser.view.BrowserView;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.view.ViewDefinition;
import org.openflexo.foundation.view.ViewFolder;
import org.openflexo.foundation.view.ViewLibrary;
import org.openflexo.foundation.view.action.MoveView;
import org.openflexo.ve.controller.OEBrowser;
import org.openflexo.ve.controller.OEController;

/**
 * Represents the view for the browser of this module
 * 
 * @author yourname
 * 
 */
public class OEBrowserView extends BrowserView {

	private static final Logger logger = Logger.getLogger(OEBrowserView.class.getPackage().getName());

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================

	protected OEController _controller;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public OEBrowserView(OEBrowser browser, OEController controller, SelectionPolicy selectionPolicy) {
		super(browser, controller.getKeyEventListener(), controller.getEditor(), selectionPolicy);
		_controller = controller;
	}

	@Override
	protected TreeDropTarget createTreeDropTarget(FlexoJTree treeView2, ProjectBrowser _browser2) {
		return new OETreeDropTarget(treeView2, _browser2);
	}

	@Override
	public void treeSingleClick(FlexoModelObject object) {
	}

	@Override
	public void treeDoubleClick(FlexoModelObject object) {
		if (_controller.getCurrentPerspective().hasModuleViewForObject(object)) {
			// Try to display object in view
			_controller.selectAndFocusObject(object);
		}
	}

	public class OETreeDropTarget extends TreeDropTarget {

		private FlexoEditor editor;

		public OETreeDropTarget(FlexoJTree tree, ProjectBrowser browser) {
			super(tree, browser);
			editor = tree.getEditor();
		}

		@Override
		public boolean targetAcceptsSource(BrowserElement targ, BrowserElement source) {
			if (source instanceof ShemaDefinitionElement) {
				ViewDefinition dragged = ((ShemaDefinitionElement) source).getShema();
				if (targ instanceof ShemaFolderElement) {
					ViewFolder over = ((ShemaFolderElement) targ).getFolder();
					if (over == null || dragged == null) {
						return false;
					}
					Vector<ViewDefinition> v = new Vector<ViewDefinition>();
					v.add(dragged);
					return (dragged.getFolder() != over);
				} else if (targ instanceof ShemaLibraryElement) {
					ViewLibrary over = ((ShemaLibraryElement) targ).getShemaLibrary();
					if (over == null || dragged == null) {
						return false;
					}
					Vector<ViewDefinition> v = new Vector<ViewDefinition>();
					v.add(dragged);
					return (dragged.getFolder() != over.getRootFolder());
				}
			}
			return false;
		}

		@Override
		public boolean handleDrop(BrowserElement source, BrowserElement targ) {
			if (targetAcceptsSource(targ, source)) {
				ViewDefinition dragged = ((ShemaDefinitionElement) source).getShema();
				ViewFolder folder = null;
				if (targ instanceof ShemaFolderElement) {
					folder = ((ShemaFolderElement) targ).getFolder();
				} else if (targ instanceof ShemaLibraryElement) {
					folder = ((ShemaLibraryElement) targ).getShemaLibrary().getRootFolder();
				}
				if (folder == null) {
					return false;
				}
				Vector<ViewDefinition> v = new Vector<ViewDefinition>();
				v.add(dragged);
				MoveView move = MoveView.actionType.makeNewAction(dragged, v, editor);
				move.setFolder(folder);
				move.doAction();
				return move.hasActionExecutionSucceeded();
			} else {
				if (source == null || targ == null) {
					return false;
				}
				if (source instanceof ShemaDefinitionElement) {
					ViewDefinition dragged = ((ShemaDefinitionElement) source).getShema();
					if (targ instanceof ShemaFolderElement) {
						ViewFolder over = ((ShemaFolderElement) targ).getFolder();
						if (over == null || dragged == null) {
							return false;
						}
						/*if (over == dragged) {
							FlexoController.notify(FlexoLocalization.localizedForKey("cannot_drop_entry_within_itself"));
							return false;
						}
						if (over.isChildOf(dragged)) {
							FlexoController.notify(FlexoLocalization.localizedForKey("cannot_drop_father_within_son"));
							return false;
						}
						if (!over.canHaveChildrenWithDepth(dragged.getDepth())) {
							FlexoController
									.notify(FlexoLocalization.localizedForKey("maximum_toc_depth_is") + " " + TOCEntry.MAXIMUM_DEPTH);
							return false;
						}*/
					}
				}
				// FlexoController.notify(FlexoLocalization.localizedForKey("drop_cannot_be_performed"));
			}
			return false;
		}

	}

}
