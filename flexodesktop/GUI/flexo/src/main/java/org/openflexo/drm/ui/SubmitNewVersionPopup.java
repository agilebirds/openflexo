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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.openflexo.drm.DocItem;
import org.openflexo.drm.DocItemVersion;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.view.FlexoDialog;

/**
 * Popup allowing to choose some objects of a given type from Flexo model objects hierarchy
 * 
 * @author sguerin
 * 
 */
public class SubmitNewVersionPopup extends FlexoDialog {

	DocItemVersion _versionToSubmit = null;

	protected SubmitNewVersionView _view;
	protected JButton hideShowDetailsButton;

	public SubmitNewVersionPopup(final DocItem docItem, Language language, Frame owner, FlexoEditor editor) {
		super(owner);

		String title = FlexoLocalization.localizedForKey("submit_documentation_for") + " " + docItem.getIdentifier();
		setTitle(title);
		getContentPane().setLayout(new BorderLayout());

		_view = new SubmitNewVersionView(docItem, language, editor);
		_view.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());

		JButton applyButton = new JButton(FlexoLocalization.localizedForKey("submit_documentation"));
		applyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_versionToSubmit = _view.getDocResourceManager().getEditedVersion(docItem);
				_view.getDocResourceManager().stopEditVersion(_view.getDocResourceManager().getEditedVersion(docItem));
				dispose();
			}
		});
		controlPanel.add(applyButton);
		applyButton.setSelected(true);

		JButton cancelButton = new JButton(FlexoLocalization.localizedForKey("cancel_submission"));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_view.getDocResourceManager().stopEditVersion(_view.getDocResourceManager().getEditedVersion(docItem));
				dispose();
			}
		});
		controlPanel.add(cancelButton);

		hideShowDetailsButton = new JButton(FlexoLocalization.localizedForKey("show_details"));
		hideShowDetailsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (_view.showDetails) {
					_view.hideDetails();
					hideShowDetailsButton.setText(FlexoLocalization.localizedForKey("show_details", hideShowDetailsButton));
				} else {
					setSize(1000, 900);
					Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
					setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2 - 100);
					_view.showDetails();
					hideShowDetailsButton.setText(FlexoLocalization.localizedForKey("hide_details", hideShowDetailsButton));
					pack();
					repaint();
				}
			}
		});
		controlPanel.add(hideShowDetailsButton);

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());

		contentPanel.add(_view, BorderLayout.CENTER);
		contentPanel.add(controlPanel, BorderLayout.SOUTH);

		getContentPane().add(contentPanel, BorderLayout.CENTER);

		setModal(true);
		validate();

		pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2 - 100);

		show();
	}

	public DocItemVersion getVersionToSubmit() {
		return _versionToSubmit;
	}

}
