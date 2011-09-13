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
package org.openflexo.doceditor.controller.browser;

import java.util.Vector;


import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.dnd.TreeDropTarget;
import org.openflexo.components.browser.view.BrowserView.FlexoJTree;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.toc.TOCRepository;
import org.openflexo.foundation.toc.action.MoveTOCEntry;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.FlexoController;

public class TOCTreeDropTarget extends TreeDropTarget {

	private FlexoEditor editor;
	
	public TOCTreeDropTarget(FlexoJTree tree, ProjectBrowser browser) {
		super(tree, browser);
		editor = tree.getEditor();
	}
	
	@Override
	public boolean targetAcceptsSource(BrowserElement targ,
			BrowserElement source) {
		if (source instanceof TOCEntryElement) {
			TOCEntry dragged = ((TOCEntryElement)source).getEntry();
			if (targ instanceof TOCEntryElement) {
				TOCEntry over = ((TOCEntryElement)targ).getEntry();
				if (over==null || dragged==null)
					return false;
				Vector<TOCEntry> v = new Vector<TOCEntry>();
				v.add(dragged);
				return MoveTOCEntry.actionType.isEnabled(over,v , editor);
			} else if (targ instanceof TOCRepositoryElement){
				TOCRepository over = ((TOCRepositoryElement)targ).getRepository();
				if (over==null || dragged==null)
					return false;
				Vector<TOCEntry> v = new Vector<TOCEntry>();
				v.add(dragged);
				return MoveTOCEntry.actionType.isEnabled(over,v , editor);
			}
		}
		return false;
	}
	
	@Override
	public boolean handleDrop(BrowserElement source, BrowserElement targ) {
		if (targetAcceptsSource(targ, source)) {
			TOCEntry dragged = ((TOCEntryElement)source).getEntry();
			if (targ instanceof TOCEntryElement) {
				TOCEntry over = ((TOCEntryElement)targ).getEntry();
				Vector<TOCEntry> v = new Vector<TOCEntry>();
				v.add(dragged);
				MoveTOCEntry move = MoveTOCEntry.actionType.makeNewAction(over, v, editor);
				move.doAction();
				return move.hasActionExecutionSucceeded();
			} else if (targ instanceof TOCRepositoryElement){
				TOCRepository over = ((TOCRepositoryElement)targ).getRepository();
				Vector<TOCEntry> v = new Vector<TOCEntry>();
				v.add(dragged);
				MoveTOCEntry move = MoveTOCEntry.actionType.makeNewAction(over, v, editor);
				move.doAction();
				return move.hasActionExecutionSucceeded();
			}
		} else {
			if (source==null || targ==null)
				return false;
			if (source instanceof TOCEntryElement) {
				TOCEntry dragged = ((TOCEntryElement)source).getEntry();
				if (targ instanceof TOCEntryElement) {
					TOCEntry over = ((TOCEntryElement)targ).getEntry();
					if (over==null || dragged == null)
						return false;
					if (over==dragged) {
						FlexoController.notify(FlexoLocalization.localizedForKey("cannot_drop_entry_within_itself"));
						return false;
					}
					if (over.isChildOf(dragged)) {
						FlexoController.notify(FlexoLocalization.localizedForKey("cannot_drop_father_within_son"));
						return false;
					}
					if (!over.canHaveChildrenWithDepth(dragged.getDepth())) {
						FlexoController.notify(FlexoLocalization.localizedForKey("maximum_toc_depth_is")+" "+TOCEntry.MAXIMUM_DEPTH);
						return false;
					}
				}
			}
			//FlexoController.notify(FlexoLocalization.localizedForKey("drop_cannot_be_performed"));
		}
		return false;
	}

}
