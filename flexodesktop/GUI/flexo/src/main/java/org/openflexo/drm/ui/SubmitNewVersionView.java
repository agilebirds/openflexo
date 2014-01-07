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
package org.openflexo.drm.ui;

import java.awt.BorderLayout;

import org.openflexo.drm.DocItem;
import org.openflexo.drm.DocItemAction;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.localization.Language;

public class SubmitNewVersionView extends AbstractDocItemView {

	public boolean showDetails = false;

	public SubmitNewVersionView(DocItem docItem, Language language, FlexoEditor editor) {
		super(docItem, null, editor);
		DocItemAction lastAction = docItem.getLastActionForLanguage(language);
		languageCB.setSelectedItem(language);
		if (lastAction != null) {
			getDocResourceManager().beginVersionReview(lastAction.getVersion());
			updateViewFromModel();
			setCurrentAction(lastAction);
		} else {
			getDocResourceManager().beginVersionSubmission(docItem, language);
			updateViewFromModel();
		}
		inheritanceChildsListView.setEnabled(false);
		embeddingChildsListView.setEnabled(false);
		relatedToListView.setEnabled(false);
		generalInfoPanel.parentItemRelatedToInheritanceDIS.setEnabled(false);
		generalInfoPanel.parentItemRelatedToInheritanceDIS.setEnabled(false);
		hideDetails();
	}

	protected void hideDetails() {
		showDetails = false;
		remove(bottomPanel);
		remove(rightPanel);
		revalidate();
		repaint();
	}

	protected void showDetails() {
		showDetails = true;
		add(bottomPanel, BorderLayout.SOUTH);
		add(rightPanel, BorderLayout.EAST);
		revalidate();
		repaint();
	}

	@Override
	protected HistoryPanel makeHistoryPanel() {
		return new SubmitNewVersionHistoryPanel();
	}

	protected class SubmitNewVersionHistoryPanel extends HistoryPanel {

		protected SubmitNewVersionHistoryPanel() {
			super();
			actionList.setEnabled(false);
			actionPanel.remove(editButton);
			actionPanel.remove(submitReviewButton);
			actionPanel.remove(approveButton);
			actionPanel.remove(refuseButton);
		}
	}

}
