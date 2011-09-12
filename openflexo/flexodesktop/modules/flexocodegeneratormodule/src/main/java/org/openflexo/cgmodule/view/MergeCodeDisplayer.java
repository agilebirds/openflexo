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
package org.openflexo.cgmodule.view;

import java.awt.event.FocusListener;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.openflexo.cgmodule.controller.GeneratorController;
import org.openflexo.diff.merge.DefaultMergedDocumentType;
import org.openflexo.diff.merge.Merge;
import org.openflexo.diff.merge.Merge.MergeRecomputed;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.rm.cg.ASCIIFile;
import org.openflexo.foundation.rm.cg.ASCIIFileResource;
import org.openflexo.foundation.rm.cg.ContentSource;
import org.openflexo.foundation.rm.cg.WOFile;
import org.openflexo.foundation.rm.cg.WOFileResource;
import org.openflexo.generator.rm.GenerationAvailableFileResource;
import org.openflexo.jedit.JEditTextArea.DisplayContext;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.TokenMarkerStyle;

public class MergeCodeDisplayer extends CodeDisplayer {

	private static final Logger logger = Logger.getLogger(MergeCodeDisplayer.class.getPackage().getName());

	//private MergeCodeDisplayerComponent _component;
	
	public MergeCodeDisplayer(GenerationAvailableFileResource resource, GeneratorController controller)
	{
		super(resource,controller);
	}
	
	private boolean editable = true;
	
	public boolean isEditable() 
	{
		return editable;
	}

	public void setEditable(boolean editable)
	{
		this.editable = editable;
		_component.setEditable(editable);
	}

	@Override
	public JComponent getComponent()
	{
		return (JComponent)_component;
	}

	protected interface MergeCodeDisplayerComponent extends CodeDisplayerComponent
	{
		@Override
		void update();
	}

	@Override
	protected MergeCodeDisplayerComponent buildComponent()
	{
		_component = null;
		
		if (getResource() instanceof ASCIIFileResource) {
			_component = new ASCIIFileMergeCodeDisplayer();
		}
		else if (getResource() instanceof WOFileResource) {
			_component = new WOFileMergeCodeDisplayer();
		}
		
		if (_component == null) {
			_component = new ErrorPanel();
		}
		
		return (MergeCodeDisplayerComponent)_component;
	}
	
	/*protected MergeCodeDisplayerComponent buildComponent()
	{
		_component = null;
		if (getResourceType() == ResourceType.JAVA_FILE) {
			if (getGeneratedCode() instanceof GeneratedComponent) {
				_component = new ComponentJavaMergeCodeDisplayer();
			}
			else if (getGeneratedCode() instanceof GeneratedJavaClass) {
				_component = new JavaClassMergeCodeDisplayer();
			}
			else if (getCGFile().isMarkedForDeletion()){
				_component = new JavaClassMergeCodeDisplayer();
			}
		}
		else if (getResourceType() == ResourceType.API_FILE) {
			_component = new ComponentAPIMergeCodeDisplayer();
		}
		else if (getResourceType() == ResourceType.WO_FILE) {
			_component = new WOFileMergeCodeDisplayer();
		}
		else {
			if (getGeneratedCode() instanceof GeneratedTextResource) {
				_component = new TextResourceMergeCodeDisplayer();
			}
			else if (getCGFile().isMarkedForDeletion()) {
				_component = new TextResourceMergeCodeDisplayer();
			}
		}
		if (_component == null) {
			_component = new ErrorPanel();
		}
		return _component;
	}*/
	
	private class ErrorPanel extends JTextArea implements MergeCodeDisplayerComponent
	{
		private ErrorPanel()
		{
			super(FlexoLocalization.localizedForKey("problem_accessing_file_view")+"\nResource: "+getResource()+"\nCode: "+getGeneratedCode()+"\n");
		}

		@Override
		public void update()
		{
		}
		
		@Override
		public String getEditedContentForKey(String contentKey) 
		{
			// Interface
			return null;
		}
		
		@Override
		public void setEditedContent(CGFile file) 
		{
			// Interface		
		}
		
		@Override
		public void setContentSource(ContentSource aContentSource) 
		{
			// Interface
		}
		@Override
		public void addToFocusListener(FocusListener aFocusListener)
		{
			// TODO Auto-generated method stub		
		}

		@Override
		public DisplayContext getDisplayContext() 
		{
			return new DisplayContext("error",0,0,0,0);
		}

		@Override
		public void setDisplayContext(DisplayContext context) 
		{
		}
		
	}
	
	@Override
	public void update()
	{
		if (_component != null) {
			_component.update();
		}
	}
	
	

	protected class ASCIIFileMergeCodeDisplayer extends JTabbedPane implements MergeCodeDisplayerComponent, Observer
	{
		private static final String GENERATION_MERGE = "generation_merge";
		private static final String RESULT_FILE_MERGE = "result_file_merge";

		private CGMergeEditor _generationMergeEditor;
		private CGMergeEditor _fileMergeEditor;
		private DisplayContext displayContext;
		
		protected ASCIIFileMergeCodeDisplayer()
		{
			super();
			update();
		}
		
		@Override
		public DisplayContext getDisplayContext()
		{
			DisplayContext context = null;
			if (getSelectedComponent() == _generationMergeEditor) {
				context = _generationMergeEditor.getMergeTextArea().getDisplayContext();
				context.setContent(GENERATION_MERGE);
			}
			if (getSelectedComponent() == _fileMergeEditor) {
				context = _fileMergeEditor.getMergeTextArea().getDisplayContext();
				context.setContent(RESULT_FILE_MERGE);
			}
			return context;
		}
		
		@Override
		public void setDisplayContext(DisplayContext context)
		{
			if (context != null) {
				if ((context.getContent() != null) && context.getContent().equals(GENERATION_MERGE)) {
					setSelectedComponent(_generationMergeEditor);
					if (_generationMergeEditor != null) {
						_generationMergeEditor.setFirstVisibleLine(context.getFirstVisibleLine());
					}
				}
				if ((context.getContent() != null) && context.getContent().equals(RESULT_FILE_MERGE)) {
					setSelectedComponent(_fileMergeEditor);
					if (_fileMergeEditor != null) {
						_fileMergeEditor.setFirstVisibleLine(context.getFirstVisibleLine());
					}
				}
			}
		}


		@Override
		public void setEditable(boolean isEditable)
		{
			if (_generationMergeEditor != null) {
				_generationMergeEditor.setEditable(isEditable);
			}
			if (_fileMergeEditor != null) {
				_fileMergeEditor.setEditable(isEditable);
			}
		}
		
		@Override
		public void setEditedContent(CGFile file) 
		{
			// Not editable anyway
		}
		
		@Override
		public String getEditedContentForKey(String contentKey) 
		{
			// Interface: this component is not supposed to be editable
			return null;
		}
		
		@Override
		public void update()
		{
			removeAll();
			_generationMergeEditor = new CGMergeEditor(
					getGenerationMerge(),
					getTokenMarkerStyle(),
					FlexoLocalization.localizedForKey("pure_generation"),
					FlexoLocalization.localizedForKey("last_accepted_version"),
					FlexoLocalization.localizedForKey("merged_generation"),
					FlexoLocalization.localizedForKey("no_structural_changes"));
			_fileMergeEditor = new CGMergeEditor(
					getResultFileMerge(),
					getTokenMarkerStyle(),
					FlexoLocalization.localizedForKey("merged_generation"),
					FlexoLocalization.localizedForKey("content_on_disk"),
					FlexoLocalization.localizedForKey("merged_file_(will_be_written_on_disk)"),
					FlexoLocalization.localizedForKey("no_structural_changes"));
			getResultFileMerge().addObserver(this);
			add(FlexoLocalization.localizedForKey("generation"),_generationMergeEditor);
			add(FlexoLocalization.localizedForKey("merged_file"),_fileMergeEditor);
			if (getGenerationMerge().isReallyConflicting()) {
				setSelectedIndex(0);
			}
			else {
				setSelectedIndex(1);
			}
			revalidate();
		}
		
		public void delete()
		{
			getGenerationMerge().deleteObserver(this);
		}
		
		@Override
		public void update(Observable o, Object arg) 
		{
			if ((o == getResultFileMerge()) && (arg instanceof MergeRecomputed)) {
				logger.info("update() received in ASCIIFileMergeCodeDisplayer for MergeRecomputed");
				//logger.info("left: "+getResultFileMerge().getLeftSource().getSourceString());
				int selectedIndex = getSelectedIndex();
				remove(_fileMergeEditor);
				_fileMergeEditor = new CGMergeEditor(
						getResultFileMerge(),
						getTokenMarkerStyle(),
						FlexoLocalization.localizedForKey("merged_generation"),
						FlexoLocalization.localizedForKey("content_on_disk"),
						FlexoLocalization.localizedForKey("merged_file_(will_be_written_on_disk)"),
						FlexoLocalization.localizedForKey("no_structural_changes"));
				add(FlexoLocalization.localizedForKey("merged_file"),_fileMergeEditor);
				setSelectedIndex(selectedIndex);
			}
		}
		
		protected Merge getGenerationMerge() 
		{
			return ((ASCIIFile)getResourceData()).getGenerationMerge();
		}
		
		protected Merge getResultFileMerge()
		{
			return ((ASCIIFile)getResourceData()).getResultFileMerge();
		}


		protected TokenMarkerStyle getTokenMarkerStyle()
		{
			return DefaultMergedDocumentType.getMergedDocumentType(getFileFormat()).getStyle();
		}

		@Override
		public void setContentSource(ContentSource aContentSource) 
		{
			// Interface
		}

		@Override
		public void addToFocusListener(FocusListener aFocusListener)
		{
			_generationMergeEditor.addToFocusListener(aFocusListener);
			_fileMergeEditor.addToFocusListener(aFocusListener);
		}


	}

	protected class WOFileMergeCodeDisplayer extends JTabbedPane implements MergeCodeDisplayerComponent
	{	
		private static final String HTML = "html";
		private static final String WOD = "wod";
		private static final String WOO = "woo";

		ASCIIFileMergeCodeDisplayer htmlDisplayer;
		ASCIIFileMergeCodeDisplayer wodDisplayer;
		ASCIIFileMergeCodeDisplayer wooDisplayer;

		protected WOFileMergeCodeDisplayer()
		{
			super();
			update();
		}
		
		@Override
		public DisplayContext getDisplayContext()
		{
			DisplayContext context = null;
			if (getSelectedComponent() == htmlDisplayer) {
				context = htmlDisplayer.getDisplayContext();
				context.setContent(context.getContent()+"_"+HTML);
			}
			if (getSelectedComponent() == wodDisplayer) {
				context = wodDisplayer.getDisplayContext();
				context.setContent(context.getContent()+"_"+WOD);
			}
			if (getSelectedComponent() == wooDisplayer) {
				context = wooDisplayer.getDisplayContext();
				context.setContent(context.getContent()+"_"+WOO);
			}
			return context;
		}
		
		@Override
		public void setDisplayContext(DisplayContext context)
		{
			String subContext = null;
			if (context.getContent().indexOf("_") > -1) {
				subContext = context.getContent().substring(0,context.getContent().lastIndexOf("_"));
			}
			if (context.getContent().endsWith(HTML)) {
				setSelectedComponent(htmlDisplayer);
				context = new DisplayContext(context);
				if (subContext!=null) {
					context.setContent(subContext);
				}
				htmlDisplayer.setDisplayContext(context);
			}
			if (context.getContent().endsWith(WOD)) {
				setSelectedComponent(wodDisplayer);
				context = new DisplayContext(context);
				if (subContext!=null) {
					context.setContent(subContext);
				}
				wodDisplayer.setDisplayContext(context);
			}
			if (context.getContent().endsWith(WOO)) {
				setSelectedComponent(wooDisplayer);
				context = new DisplayContext(context);
				if (subContext!=null) {
					context.setContent(subContext);
				}
				wooDisplayer.setDisplayContext(context);
			}
		}

		@Override
		public void setEditable(boolean isEditable)
		{
			// Interface, not editable anyway
		}
		
		@Override
		public String getEditedContentForKey(String contentKey) 
		{
			// Interface, not editable anyway
			return null;
		}

		@Override
		public void setEditedContent(CGFile file) 
		{
			// Interface, not editable anyway
		}


		@Override
		public void update()
		{
			removeAll();
			htmlDisplayer = new ASCIIFileMergeCodeDisplayer() {
				@Override
				protected TokenMarkerStyle getTokenMarkerStyle()
				{
					return TokenMarkerStyle.HTML;
				}
				@Override
				protected Merge getGenerationMerge() 
				{
					return ((WOFile)getResourceData()).getHTMLFile().getGenerationMerge();
				}	
				@Override
				protected Merge getResultFileMerge()
				{
					return ((WOFile)getResourceData()).getHTMLFile().getResultFileMerge();
				}
			};
			wodDisplayer = new ASCIIFileMergeCodeDisplayer() {
				@Override
				protected TokenMarkerStyle getTokenMarkerStyle()
				{
					return TokenMarkerStyle.WOD;
				}
				@Override
				protected Merge getGenerationMerge() 
				{
					return ((WOFile)getResourceData()).getWODFile().getGenerationMerge();
				}	
				@Override
				protected Merge getResultFileMerge()
				{
					return ((WOFile)getResourceData()).getWODFile().getResultFileMerge();
				}
			};
			wooDisplayer = new ASCIIFileMergeCodeDisplayer() {
				@Override
				protected TokenMarkerStyle getTokenMarkerStyle()
				{
					return TokenMarkerStyle.WOD;
				}
				@Override
				protected Merge getGenerationMerge() 
				{
					return ((WOFile)getResourceData()).getWOOFile().getGenerationMerge();
				}	
				@Override
				protected Merge getResultFileMerge()
				{
					return ((WOFile)getResourceData()).getWOOFile().getResultFileMerge();
				}
			};
			add(".html",htmlDisplayer);
			add(".wod",wodDisplayer);
			add(".woo",wooDisplayer);
			validate();
		}

		@Override
		public void setContentSource(ContentSource aContentSource) 
		{
			// Interface
		}

		@Override
		public void addToFocusListener(FocusListener aFocusListener)
		{
			htmlDisplayer.addToFocusListener(aFocusListener);
			wodDisplayer.addToFocusListener(aFocusListener);
			wooDisplayer.addToFocusListener(aFocusListener);
		}

	}


}
