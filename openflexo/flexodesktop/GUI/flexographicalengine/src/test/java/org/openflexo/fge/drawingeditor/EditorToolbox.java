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
package org.openflexo.fge.drawingeditor;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JPanel;

import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.ShadowStyle;
import org.openflexo.fge.graphics.TextStyle;
import org.openflexo.fge.view.widget.FIBBackgroundStyleSelector;
import org.openflexo.fge.view.widget.FIBForegroundStyleSelector;
import org.openflexo.fge.view.widget.FIBShadowStyleSelector;
import org.openflexo.fge.view.widget.FIBTextStyleSelector;


public class EditorToolbox {

	public ForegroundStyle currentForegroundStyle;
	public BackgroundStyle currentBackgroundStyle;
	public TextStyle currentTextStyle;
	public ShadowStyle currentShadowStyle;
	
	private JPanel toolboxPanel;
	private FIBForegroundStyleSelector foregroundSelector;
	private FIBBackgroundStyleSelector backgroundSelector;
	private FIBTextStyleSelector textStyleSelector;
	private FIBShadowStyleSelector shadowStyleSelector;
	
	public EditorToolbox() 
	{
		currentForegroundStyle = ForegroundStyle.makeDefault();
		currentBackgroundStyle = BackgroundStyle.makeColoredBackground(Color.RED);
		currentTextStyle = TextStyle.makeDefault();
		currentShadowStyle =  ShadowStyle.makeDefault();
	}

	public JPanel getToolboxPanel()
	{
		if (toolboxPanel == null) {
			toolboxPanel = new JPanel(new FlowLayout());
			foregroundSelector = new FIBForegroundStyleSelector(currentForegroundStyle) {
				@Override
				public void setEditedObject(ForegroundStyle object)
				{
					super.setEditedObject(object);
					currentForegroundStyle = object;
				}
			};
			backgroundSelector = new FIBBackgroundStyleSelector(currentBackgroundStyle) {
				@Override
				public void setEditedObject(BackgroundStyle object)
				{
					super.setEditedObject(object);
					currentBackgroundStyle = object;
				}
			};
			textStyleSelector = new FIBTextStyleSelector(currentTextStyle) {
				@Override
				public void setEditedObject(TextStyle object)
				{
					super.setEditedObject(object);
					currentTextStyle = object;
				}
			};
			shadowStyleSelector = new FIBShadowStyleSelector(currentShadowStyle) {
				@Override
				public void setEditedObject(ShadowStyle object)
				{
					super.setEditedObject(object);
					currentShadowStyle = object;
				}
			};
			toolboxPanel.add(foregroundSelector);
			toolboxPanel.add(backgroundSelector);
			toolboxPanel.add(shadowStyleSelector);
			toolboxPanel.add(textStyleSelector);
			toolboxPanel.validate();
		}
		return toolboxPanel;
	}
}
