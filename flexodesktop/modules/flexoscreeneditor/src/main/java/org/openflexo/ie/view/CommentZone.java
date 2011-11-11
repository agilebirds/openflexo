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
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JLabel;

import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.view.FlexoDialog;
import org.openflexo.wysiwyg.FlexoWysiwyg;
import org.openflexo.wysiwyg.FlexoWysiwygLight;

public class CommentZone extends FlexoDialog {

	protected IEWOComponent _model;

	private JLabel helpLabel;

	FlexoWysiwyg editor;

	public CommentZone(Frame owner, ComponentDefinition model) {
		super(owner);
		setTitle(model.getWOComponent().getName());
		_model = model.getWOComponent();
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		helpLabel = new JLabel("Help (for end-user):");
		helpLabel.setFont(IEWOComponentView.LABEL_BOLD_FONT);
		helpLabel.setBackground(IEWOComponentView.DEFAULT_BG_COLOR);
		editor = new FlexoWysiwygLight(_model.getHelpText(), true) {
			@Override
			public void notifyTextChanged() {
				_model.setHelpText(editor.getBodyContent());
			}
		};
		getContentPane().add(helpLabel, BorderLayout.NORTH);
		getContentPane().add(editor, BorderLayout.CENTER);
		setSize(new Dimension(400, 250));
	}

	public CommentZone(Frame owner, IEWOComponent model) {
		super();
		setTitle(model.getName());
		_model = model;
		getContentPane().setLayout(new BorderLayout());
		helpLabel = new JLabel("Help (for end-user):");
		helpLabel.setFont(IEWOComponentView.LABEL_BOLD_FONT);
		helpLabel.setBackground(IEWOComponentView.DEFAULT_BG_COLOR);
		editor = new FlexoWysiwygLight(_model.getHelpText(), true) {
			@Override
			public void notifyTextChanged() {
				_model.setHelpText(editor.getBodyContent());
			}
		};
		getContentPane().add(helpLabel, BorderLayout.NORTH);
		getContentPane().add(editor, BorderLayout.CENTER);
		setSize(new Dimension(400, 250));
	}

}
