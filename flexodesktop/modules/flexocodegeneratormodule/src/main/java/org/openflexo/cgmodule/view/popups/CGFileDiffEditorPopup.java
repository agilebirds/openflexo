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
package org.openflexo.cgmodule.view.popups;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.swing.VerticalLayout;
import org.openflexo.view.FlexoDialog;
import org.openflexo.view.listener.FlexoActionButton;


import org.openflexo.cgmodule.GeneratorCst;
import org.openflexo.cgmodule.controller.GeneratorController;
import org.openflexo.cgmodule.view.CustomDiffCodeDisplayer;
import org.openflexo.components.AskParametersPanel;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.version.AbstractCGFileVersion;
import org.openflexo.foundation.param.CGFileVersionParameter;
import org.openflexo.foundation.param.EnumDropDownParameter;
import org.openflexo.foundation.rm.cg.AbstractGeneratedFile;
import org.openflexo.foundation.rm.cg.ContentSource;
import org.openflexo.foundation.rm.cg.ContentSource.ContentSourceType;
import org.openflexo.generator.action.ShowFileVersion;
import org.openflexo.generator.rm.GenerationAvailableFileResource;


/**
 * @author sylvain
 */
public class CGFileDiffEditorPopup extends FlexoDialog
{
	private Logger logger = FlexoLogger.getLogger(CGFileDiffEditorPopup.class.getPackage().getName());

	private CGFile _cgFile;
	private GeneratorController _controller;
	private ContentSource _leftSource;
	private ContentSource _rightSource;
	private CustomDiffCodeDisplayer customDiffCodeDisplayer;
	private ContentSourceEditor leftSourceEditor;
	private ContentSourceEditor rightSourceEditor;

	public CGFileDiffEditorPopup (CGFile file, ContentSource leftSource, ContentSource rightSource, GeneratorController controller)
	{
		super(controller.getFlexoFrame(),file.getFileName()+" - "+FlexoLocalization.localizedForKey("diff_editor"),false);
		_cgFile = file;
		_leftSource = leftSource;
		_rightSource = rightSource;
		_controller = controller;
		
		_header = new ViewHeader();
		
		customDiffCodeDisplayer = new CustomDiffCodeDisplayer(
				(GenerationAvailableFileResource)file.getResource(),
				_leftSource, _rightSource,controller);
		
		getContentPane().setLayout(new BorderLayout());
	   	getContentPane().add(_header,BorderLayout.NORTH);
	   	getContentPane().add(customDiffCodeDisplayer.getComponent(),BorderLayout.CENTER);
    	JPanel controlPanel = new JPanel(new FlowLayout());
    	JButton button = new JButton();
    	button.setText(FlexoLocalization.localizedForKey("close",button));
    	button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}            		
    	});
    	controlPanel.add(button);
    	getContentPane().add(controlPanel,BorderLayout.SOUTH);
    	setPreferredSize(new Dimension(1000,800));
    	validate();
    	pack();
 	}
	
	private ViewHeader _header;

	protected class ViewHeader extends JPanel
	{
		JLabel icon;
		JLabel title;
		JLabel subTitle1;
		JLabel subTitle2;
		JPanel controlPanel;
		Vector<FlexoActionButton> actionButtons = new Vector<FlexoActionButton>();
		
		protected ViewHeader()
		{
			super(new BorderLayout());
			icon = new JLabel(GeneratorIconLibrary.DIFF_EDITOR_ICON);
			icon.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
			add(icon,BorderLayout.WEST);
			title = new JLabel(_cgFile.getFileName(),SwingConstants.LEFT);
			//title.setVerticalAlignment(JLabel.BOTTOM);
			title.setFont(GeneratorCst.HEADER_FONT);
			title.setForeground(Color.BLACK);
			title.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
			subTitle1 = new JLabel(subTitleForLeftSource(),SwingConstants.LEFT);
			subTitle1.setFont(GeneratorCst.SUB_TITLE_FONT);
			subTitle1.setForeground(Color.GRAY);
			subTitle1.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
			subTitle1.setVerticalAlignment(SwingConstants.BOTTOM);
			subTitle2 = new JLabel(subTitleForRightSource(),SwingConstants.LEFT);
			subTitle2.setFont(GeneratorCst.SUB_TITLE_FONT);
			subTitle2.setForeground(Color.GRAY);
			subTitle2.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));
			subTitle2.setVerticalAlignment(SwingConstants.BOTTOM);

			JPanel labelsPanel = new JPanel(/*new GridLayout(3,1)*/new VerticalLayout());
			labelsPanel.add(title);
			labelsPanel.add(subTitle1);
			labelsPanel.add(subTitle2);
			add(labelsPanel,BorderLayout.CENTER);			

			JPanel sourceEditorPanel = new JPanel(new FlowLayout());
			leftSourceEditor = new ContentSourceEditor(_leftSource,FlexoLocalization.localizedForKey("left_source"));
			rightSourceEditor = new ContentSourceEditor(_rightSource,FlexoLocalization.localizedForKey("right_source"));
			sourceEditorPanel.add(leftSourceEditor.getComponent());
			sourceEditorPanel.add(rightSourceEditor.getComponent());
			add(sourceEditorPanel,BorderLayout.EAST);			
			
			update();
		}


		private String subTitleForLeftSource()
		{
			String returned = FlexoLocalization.localizedForKey("left_source")+" : "+_leftSource.getStringRepresentation();
			if (_leftSource.getType() == ContentSourceType.HistoryVersion && _cgFile.getResource().getGeneratedResourceData() instanceof AbstractGeneratedFile) {
				AbstractCGFileVersion fileVersion = ((AbstractGeneratedFile)_cgFile.getResource().getGeneratedResourceData()).getHistory().versionWithId(_leftSource.getVersion());
				if (fileVersion != null) {
					returned += ", "+fileVersion.getDateAsString()+", "+fileVersion.getUserIdentifier(); 
				}
			}
			return returned;
		}

		private String subTitleForRightSource()
		{
			String returned = FlexoLocalization.localizedForKey("right_source")+" : "+_rightSource.getStringRepresentation();
			if (_rightSource.getType() == ContentSourceType.HistoryVersion && _cgFile.getResource().getGeneratedResourceData() instanceof AbstractGeneratedFile) {
				AbstractCGFileVersion fileVersion = ((AbstractGeneratedFile)_cgFile.getResource().getGeneratedResourceData()).getHistory().versionWithId(_rightSource.getVersion());
				if (fileVersion != null) {
					returned += ", "+fileVersion.getDateAsString()+", "+fileVersion.getUserIdentifier(); 
				}
			}
			return returned;
		}


		protected void update()
		{
			title.setText(_cgFile.getFileName());
			subTitle1.setText(subTitleForLeftSource());
			subTitle2.setText(subTitleForRightSource());
			for (FlexoActionButton button : actionButtons) {
				button.update();
			}
		}


	}
	
	protected class ContentSourceEditor
	{
		private AskParametersPanel component;
		private EnumDropDownParameter<ContentSourceType> sourceParam;
		private CGFileVersionParameter versionParam;
		
		protected ContentSourceEditor(ContentSource source, String label)
		{
			sourceParam = new EnumDropDownParameter<ContentSourceType> (
					"source",
					label,
					source.getType(),
					ContentSourceType.values()) {
				@Override
				public boolean accept(ContentSourceType value) 
				{
					return ShowFileVersion.getActionTypeFor(value).isEnabled(_cgFile,null,_controller.getEditor());
				}
				@Override
				public void setValue(ContentSourceType type) {
					super.setValue(type);
					selectedSourcesChanged();
				}
			};
			sourceParam.addParameter("showReset", "false");
			AbstractCGFileVersion fileVersion = (source.getVersion()!=null && _cgFile.getResource().getGeneratedResourceData() instanceof AbstractGeneratedFile?((AbstractGeneratedFile)_cgFile.getResource().getGeneratedResourceData()).getHistory().versionWithId(source.getVersion()):null);
		    versionParam = new CGFileVersionParameter("version","version",_cgFile,fileVersion) {
				@Override
				public void setValue(AbstractCGFileVersion fileVersion) {
					super.setValue(fileVersion);
					selectedSourcesChanged();
				}
			};
			versionParam.setDepends("source");
			versionParam.setConditional("source="+'"'+ContentSourceType.HistoryVersion.getStringRepresentation()+'"');
			component = new AskParametersPanel(
					_cgFile.getProject(),sourceParam,versionParam);
		}
		
		protected AskParametersPanel getComponent()
		{
			return component;
		}
		
		protected ContentSource getUpdatedContentSource()
		{
			if ((sourceParam.getValue() == ContentSourceType.HistoryVersion)
					&& (versionParam.getValue()==null) && _cgFile.getResource().getGeneratedResourceData() instanceof AbstractGeneratedFile)
				versionParam.setValue(((AbstractGeneratedFile)_cgFile.getResource().getGeneratedResourceData()).getHistory().versionWithId(_cgFile.getRepository().getLastReleaseVersionIdentifier()));
				return ContentSource.getContentSource(
						sourceParam.getValue(),
						(versionParam.getValue()!=null?versionParam.getValue().getVersionId():null));
			}

		
	}

	protected void selectedSourcesChanged()
	{
		_rightSource = rightSourceEditor.getUpdatedContentSource();
		_leftSource = leftSourceEditor.getUpdatedContentSource();
		getContentPane().remove(customDiffCodeDisplayer.getComponent());
		customDiffCodeDisplayer = new CustomDiffCodeDisplayer(
				(GenerationAvailableFileResource)_cgFile.getResource(),
				_leftSource, _rightSource,_controller);
	   	getContentPane().add(customDiffCodeDisplayer.getComponent(),BorderLayout.CENTER);
	   	_header.update();
	   	repaint();
	}

}
