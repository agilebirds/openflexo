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
package org.openflexo.sgmodule.view;

import java.awt.event.FocusListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.openflexo.diff.merge.DefaultMergedDocumentType;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.generator.COMPONENT_CODE_TYPE;
import org.openflexo.foundation.cg.generator.GeneratedCodeResult;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.ASCIIFile;
import org.openflexo.foundation.rm.cg.ContentSource;
import org.openflexo.foundation.rm.cg.WOFile;
import org.openflexo.generator.rm.GenerationAvailableFile;
import org.openflexo.generator.rm.GenerationAvailableFileResource;
import org.openflexo.jedit.JEditTextArea.DisplayContext;
import org.openflexo.jedit.cd.GenericCodeDisplayer;
import org.openflexo.sgmodule.controller.SGController;
import org.openflexo.toolbox.FileFormat;
import org.openflexo.toolbox.TokenMarkerStyle;


public class CodeDisplayer {

	private static final Logger logger = Logger.getLogger(CodeDisplayer.class.getPackage().getName());

	protected CodeDisplayerComponent _component;
	private final GenerationAvailableFileResource _resource;
	private final SGController _controller;
	
	private ContentSource _contentSource;
	
	public CodeDisplayer(GenerationAvailableFileResource resource, ContentSource contentSource, SGController controller)
	{
		super();
		_resource = resource;
		_contentSource = contentSource;
		buildComponent();
		_controller = controller;
		if (_controller != null) {
			addToFocusListener(_controller.getFooter());
		}
	}
	
	public CodeDisplayer(GenerationAvailableFileResource resource, SGController controller)
	{
		this(resource,ContentSource.GENERATED_MERGE,controller);
	}
	
	public GenerationAvailableFileResource getResource()
	{
		return _resource;
	}
	
	public IFlexoResourceGenerator getGenerator()
	{
		if (_resource != null) {
			return _resource.getGenerator();
		}
		return null;
	}

	public CGFile getCGFile()
	{
		if (_resource != null) {
			return _resource.getCGFile();
		}
		return null;
	}
	
	public GenerationAvailableFile getResourceData()
	{
		if (_resource != null) {
			return _resource.getGeneratedResourceData();
		}
		return null;
	}
	
	public GeneratedCodeResult getGeneratedCode()
	{
		if (getGenerator() != null) {
			return getGenerator().getGeneratedCode();
		}
		return null;
	}

	
	public ResourceType getResourceType()
	{
		return _resource.getResourceType();
	}
	
	public FileFormat getFileFormat()
	{
		return _resource.getResourceFormat();
	}
	
	public JComponent getComponent()
	{
		return (JComponent)_component;
	}

	protected CodeDisplayerComponent buildComponent()
	{
		_component = null;
		
		if (getResourceData() instanceof ASCIIFile) {
			_component = new ASCIIFileCodePanel(_contentSource);
		}
		else if (getResourceData() instanceof WOFile) {
			_component = new WOComponentCodePanel(_contentSource);
		}
		else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("This resource data type '"+getResourceData()+"' don't have a specific code panel. Use the default one.");
			}
			_component = new GenericCodePanel(_contentSource,"Use generic code panel for :"+(getResourceData()==null?"null resource data (ERROR)":getResourceData().getClass()));
		}
		return _component;
	}
	
	public void update()
	{
		if (_component != null) {
			_component.update();
		}
	}
	
	public DisplayContext getDisplayContext()
	{
		if (_component!=null) {
			return _component.getDisplayContext();
		}
		return null;
	}
	
	public void setDisplayContext(DisplayContext context)
	{
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Restore display context: "+context);
		}
		_component.setDisplayContext(context);
	}

	
	protected interface CodeDisplayerComponent
	{
		void update();
		public void setEditable(boolean isEditable);
		public String getEditedContentForKey(String contentKey);
		public void setEditedContent(CGFile file); 
		public void setContentSource(ContentSource aContentSource);
		public void addToFocusListener (FocusListener aFocusListener);
		public DisplayContext getDisplayContext();
		public void setDisplayContext(DisplayContext context);
	}

	protected String getASCIIContent (ContentSource contentSource)
	{
		return getASCIIContentForFile(getCGFile(), contentSource);
	}

	protected String getASCIIContentForFile (CGFile aFile, ContentSource contentSource)
	{
		if ((aFile != null)
				&& (aFile.getResource() != null)
				&& (aFile.getResource().getGeneratedResourceData() != null)
				&& (aFile.getResource().getGeneratedResourceData() instanceof ASCIIFile)) {
			return ((ASCIIFile)aFile.getResource().getGeneratedResourceData()).getContent(contentSource);
		}
		return null;
	}

	protected class ASCIIFileCodePanel extends GenericCodeDisplayer implements CodeDisplayerComponent
	{
		private ContentSource contentSource;
		private final DisplayContext displayContext;
		
		protected ASCIIFileCodePanel (ContentSource contentSource) 
		{
			super(getASCIIContent(contentSource),
					DefaultMergedDocumentType.getMergedDocumentType(getFileFormat()).getStyle());
			this.contentSource = contentSource;
			displayContext = super.getDisplayContext();
		}

		@Override
		public DisplayContext getDisplayContext()
		{
			return displayContext;
		}
		
		@Override
		public void setDisplayContext(DisplayContext context)
		{
			applyDisplayContext(context);
		}

		@Override
		public void update() 
		{
			setText(getASCIIContent(contentSource));
		}
		
		@Override
		public String getEditedContentForKey(String contentKey) 
		{
			return getText();
		}

		@Override
		public void setEditedContent(CGFile file) 
		{
			String newContent = getASCIIContentForFile(file,contentSource);
			if (newContent != null) {
				setTextKeepDisplayContext(newContent);
			}
		}

		@Override
		public void setContentSource(ContentSource aContentSource)
		{
			contentSource = aContentSource;
			update();
		}

		@Override
		public void addToFocusListener(FocusListener aFocusListener) 
		{
			addFocusListener(aFocusListener);
		}

	}


	protected String getHTMLContent (ContentSource contentSource)
	{
		return getHTMLContentForFile(getCGFile(),contentSource);
	}

	protected String getHTMLContentForFile (CGFile aFile, ContentSource contentSource)
	{
		if ((aFile != null)
				&& (aFile.getResource() != null)
				&& (aFile.getResource().getGeneratedResourceData() != null)
				&& (aFile.getResource().getGeneratedResourceData() instanceof WOFile)) {
			return ((WOFile)aFile.getResource().getGeneratedResourceData()).getHTMLFile().getContent(contentSource);
		}
		return null;
	}

	protected String getWODContent (ContentSource contentSource)
	{
		return getWODContentForFile(getCGFile(),contentSource);
	}

	protected String getWODContentForFile (CGFile aFile, ContentSource contentSource)
	{
		if ((aFile != null)
				&& (aFile.getResource() != null)
				&& (aFile.getResource().getGeneratedResourceData() != null)
				&& (aFile.getResource().getGeneratedResourceData() instanceof WOFile)) {
			return ((WOFile)aFile.getResource().getGeneratedResourceData()).getWODFile().getContent(contentSource);
		}
		return null;
	}

	protected String getWOOContent (ContentSource contentSource)
	{
		return getWOOContentForFile(getCGFile(),contentSource);
	}

	protected String getWOOContentForFile (CGFile aFile, ContentSource contentSource)
	{
		if ((aFile != null)
				&& (aFile.getResource() != null)
				&& (aFile.getResource().getGeneratedResourceData() != null)
				&& (aFile.getResource().getGeneratedResourceData() instanceof WOFile)) {
			return ((WOFile)aFile.getResource().getGeneratedResourceData()).getWOOFile().getContent(contentSource);
		}
		return null;
	}

	protected class GenericCodePanel extends JPanel implements CodeDisplayerComponent
	{

		private ContentSource contentSource;
		private DisplayContext displayContext;
		
		public GenericCodePanel(ContentSource contentSource, String info) {
			super();
			this.contentSource = contentSource;
			add(new JLabel(info));
		}

		@Override
		public void update() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setEditable(boolean isEditable) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String getEditedContentForKey(String contentKey) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setEditedContent(CGFile file) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setContentSource(ContentSource aContentSource) {
			this.contentSource = aContentSource;
		}

		@Override
		public void addToFocusListener(FocusListener aFocusListener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public DisplayContext getDisplayContext() {
			return displayContext;
		}

		@Override
		public void setDisplayContext(DisplayContext context) {
			displayContext = context;
		}
		
	}
	protected class WOComponentCodePanel extends JTabbedPane implements CodeDisplayerComponent
	{
		GenericCodeDisplayer htmlDisplayer;
		GenericCodeDisplayer wodDisplayer;
		GenericCodeDisplayer wooDisplayer;

		private ContentSource contentSource;
		
		private static final String HTML = "html";
		private static final String WOD = "wod";
		private static final String WOO = "woo";
		
		protected WOComponentCodePanel (ContentSource contentSource) 
		{
			super();
			this.contentSource = contentSource;
			htmlDisplayer = new GenericCodeDisplayer(getHTMLContent(contentSource),TokenMarkerStyle.HTML);
			wodDisplayer = new GenericCodeDisplayer(getWODContent(contentSource),TokenMarkerStyle.WOD);
			wooDisplayer = new GenericCodeDisplayer(getWOOContent(contentSource),TokenMarkerStyle.WOD);
			add(".html",htmlDisplayer);
			add(".wod",wodDisplayer);
			add(".woo",wooDisplayer);
			
		}

		@Override
		public DisplayContext getDisplayContext()
		{
			DisplayContext context = null;
			if (getSelectedComponent() == htmlDisplayer) {
				context = htmlDisplayer.getDisplayContext();
				context.setContent(HTML);
			}
			if (getSelectedComponent() == wodDisplayer) {
				context = wodDisplayer.getDisplayContext();
				context.setContent(WOD);
			}
			if (getSelectedComponent() == wooDisplayer) {
				context = wooDisplayer.getDisplayContext();
				context.setContent(WOO);
			}
			return context;
		}
		
		@Override
		public void setDisplayContext(DisplayContext context)
		{
			if (context.getContent().equals(HTML)) {
				setSelectedComponent(htmlDisplayer);
				htmlDisplayer.applyDisplayContext(context);
			}
			if (context.getContent().equals(WOD)) {
				setSelectedComponent(wodDisplayer);
				wodDisplayer.applyDisplayContext(context);
			}
			if (context.getContent().equals(WOO)) {
				setSelectedComponent(wooDisplayer);
				wooDisplayer.applyDisplayContext(context);
			}
		}


		@Override
		public void update() 
		{
			htmlDisplayer.setText(getHTMLContent(contentSource));
			wodDisplayer.setText(getWODContent(contentSource));
			wooDisplayer.setText(getWOOContent(contentSource));
		}

		@Override
		public void setEditable(boolean isEditable)
		{
			htmlDisplayer.setEditable(isEditable);
			wodDisplayer.setEditable(isEditable);
			wooDisplayer.setEditable(isEditable);
		}

		@Override
		public String getEditedContentForKey(String contentKey) 
		{
			if(contentKey == null) {
				return null;
			}
			
			try
			{
				COMPONENT_CODE_TYPE codeType = COMPONENT_CODE_TYPE.valueOf(contentKey);
				switch(codeType)
				{
					case HTML:
						return htmlDisplayer.getText();
					case WOD:
						return wodDisplayer.getText();
					case WOO:
						return wooDisplayer.getText();
				}
			}
			catch(IllegalArgumentException e){}
			
			return null;
		}

		@Override
		public void setEditedContent(CGFile file) 
		{
			htmlDisplayer.setTextKeepDisplayContext(getHTMLContentForFile(file,contentSource));
			wodDisplayer.setTextKeepDisplayContext(getWODContentForFile(file,contentSource));
			wooDisplayer.setTextKeepDisplayContext(getWOOContentForFile(file,contentSource));
		}

		@Override
		public void setContentSource(ContentSource aContentSource)
		{
			contentSource = aContentSource;
			update();
		}

		@Override
		public void addToFocusListener(FocusListener aFocusListener) 
		{
			htmlDisplayer.addFocusListener(aFocusListener);
			wodDisplayer.addFocusListener(aFocusListener);
			wooDisplayer.addFocusListener(aFocusListener);
		}

	}

	public ContentSource getContentSource() {
		return _contentSource;
	}

	public void setContentSource(ContentSource aContentSource) 
	{
		ContentSource old = _contentSource;
		
		if (!old.equals(aContentSource)) {
			_contentSource = aContentSource;
			if (_component != null) {
				_component.setContentSource(aContentSource);
			}
		}
	}

	public void addToFocusListener (FocusListener aFocusListener)
	{
		if(_component!=null) {
			_component.addToFocusListener(aFocusListener);
		}
	}
}
