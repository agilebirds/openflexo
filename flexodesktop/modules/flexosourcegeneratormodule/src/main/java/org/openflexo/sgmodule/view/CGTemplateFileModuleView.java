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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.action.FlexoActionSource;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.cg.templates.CGTemplateFile;
import org.openflexo.foundation.cg.templates.CustomCGTemplateRepository;
import org.openflexo.foundation.cg.templates.TemplateFileChanged;
import org.openflexo.foundation.cg.templates.TemplateFileEdited;
import org.openflexo.foundation.cg.templates.TemplateFileEditionCancelled;
import org.openflexo.foundation.cg.templates.TemplateFileSaved;
import org.openflexo.foundation.cg.templates.CGTemplateFile.TemplateFileContentEditor;
import org.openflexo.foundation.cg.templates.action.CancelEditionOfCustomTemplateFile;
import org.openflexo.foundation.cg.templates.action.EditCustomTemplateFile;
import org.openflexo.foundation.cg.templates.action.RedefineCustomTemplateFile;
import org.openflexo.foundation.cg.templates.action.RefreshTemplates;
import org.openflexo.foundation.cg.templates.action.SaveCustomTemplateFile;
import org.openflexo.icon.FilesIconLibrary;
import org.openflexo.jedit.InputHandler;
import org.openflexo.jedit.JEditTextArea.DisplayContext;
import org.openflexo.jedit.cd.HTMLCodeDisplayer;
import org.openflexo.jedit.cd.JavaCodeDisplayer;
import org.openflexo.jedit.cd.TextCodeDisplayer;
import org.openflexo.jedit.cd.VTLHTMLCodeDisplayer;
import org.openflexo.jedit.cd.VTLJavaCodeDisplayer;
import org.openflexo.jedit.cd.WODCodeDisplayer;
import org.openflexo.jedit.cd.XMLCodeDisplayer;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.sgmodule.SGCst;
import org.openflexo.sgmodule.controller.SGController;
import org.openflexo.toolbox.FileFormat;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.toolbox.FileFormat.TextFileFormat;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;
import org.openflexo.view.listener.FlexoActionButton;

/**
 * @author sylvain
 */
public class CGTemplateFileModuleView extends JPanel implements ModuleView<CGTemplate>, FlexoActionSource, FlexoObserver, TemplateFileContentEditor
{
	private final Logger logger = FlexoLogger.getLogger(CGTemplateFileModuleView.class.getPackage().getName());

	protected CGTemplate _cgTemplateFile;
	private final SGController _controller;

	private CodeDisplayer _codeDisplayer;
	private final ViewHeader _header;

	private boolean openedInSeparateWindow = false;

	public CGTemplateFileModuleView(CGTemplate cgTemplateFile, SGController controller)
	{
		super(new BorderLayout());
		_controller = controller;
		_cgTemplateFile = cgTemplateFile;
		_cgTemplateFile.addObserver(this);

		_header = new ViewHeader();

		add(_header,BorderLayout.NORTH);

		FileFormat format = _cgTemplateFile.getFileFormat();

		if (format == FileFormat.JAVA) {
			_codeDisplayer = new VTLJavaCodePanel(_cgTemplateFile);
			add((JComponent)_codeDisplayer);
		}
		else if (format == FileFormat.XML) {
			_codeDisplayer = new XMLCodePanel(_cgTemplateFile);
			add((JComponent)_codeDisplayer);
		}
		else if (format == FileFormat.HTML) {
			_codeDisplayer = new VTLHTMLCodePanel(_cgTemplateFile);
			add((JComponent)_codeDisplayer);
		}
		else if (format == FileFormat.WOD) {
			_codeDisplayer = new WODCodePanel(_cgTemplateFile);
			add((JComponent)_codeDisplayer);
		}
		else if (format instanceof TextFileFormat) {
			_codeDisplayer = new TextCodePanel(_cgTemplateFile);
			add((JComponent)_codeDisplayer);
		}
		else {
			logger.warning("I should not come here !");
		}

		if ((_controller != null)&&(_codeDisplayer!=null)) {
			_codeDisplayer.addToFocusListener(_controller.getFooter());
			_codeDisplayer.getInputHandler().addKeyBinding((ToolBox.getPLATFORM()==ToolBox.MACOS?"M":"C")+"+S", new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					if ((_cgTemplateFile instanceof CGTemplateFile) && ((CGTemplateFile) _cgTemplateFile).isEdited()) {
						SaveCustomTemplateFile save = SaveCustomTemplateFile.actionType.makeNewAction((CGTemplateFile) _cgTemplateFile, null, _controller.getEditor());
						save.doAction();
					}
				}
			});
		}
		

	}

	private void updateView()
	{
		_header.update();
	}


	protected class ViewHeader extends JPanel
	{
		JLabel icon;
		JLabel title;
		JLabel subTitle;
		JPanel controlPanel;
		Vector<FlexoActionButton> actionButtons = new Vector<FlexoActionButton>();

		protected ViewHeader()
		{
			super(new BorderLayout());
			icon = new JLabel(FilesIconLibrary.mediumIconForFileFormat(_cgTemplateFile.getFileFormat()));
			icon.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
			add(icon,BorderLayout.WEST);
			title = new JLabel(_cgTemplateFile.getTemplateName(), SwingConstants.LEFT);
			//title.setVerticalAlignment(JLabel.BOTTOM);
			title.setFont(SGCst.HEADER_FONT);
			title.setForeground(Color.BLACK);
			title.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
			subTitle = new JLabel(subTitleForFile(),SwingConstants.LEFT);
			//title.setVerticalAlignment(JLabel.BOTTOM);
			subTitle.setFont(SGCst.SUB_TITLE_FONT);
			subTitle.setForeground(Color.GRAY);
			subTitle.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));

			JPanel labelsPanel = new JPanel(new GridLayout(2,1));
			labelsPanel.add(title);
			labelsPanel.add(subTitle);
			add(labelsPanel,BorderLayout.CENTER);			

			controlPanel = new JPanel(new FlowLayout());
			if (_cgTemplateFile.isCustomTemplate()) {	           
				FlexoActionButton editAction = new FlexoActionButton(EditCustomTemplateFile.actionType,"edit",CGTemplateFileModuleView.this,_controller.getEditor());
				FlexoActionButton saveAction = new FlexoActionButton(SaveCustomTemplateFile.actionType,"save",CGTemplateFileModuleView.this,_controller.getEditor());
				FlexoActionButton cancelAction = new FlexoActionButton(CancelEditionOfCustomTemplateFile.actionType,"cancel",CGTemplateFileModuleView.this,_controller.getEditor());
				FlexoActionButton refreshAction = new FlexoActionButton(RefreshTemplates.actionType,"reload",CGTemplateFileModuleView.this,_controller.getEditor());
				actionButtons.add(editAction);
				actionButtons.add(saveAction);
				actionButtons.add(cancelAction);
				actionButtons.add(refreshAction);
				controlPanel.add(editAction);
				controlPanel.add(saveAction);
				controlPanel.add(cancelAction);
				controlPanel.add(refreshAction);
				controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
			}
			else {
				FlexoActionButton redefineAction = new FlexoActionButton(RedefineCustomTemplateFile.actionType,CGTemplateFileModuleView.this,_controller.getEditor());
				actionButtons.add(redefineAction);
				controlPanel.add(redefineAction);				
			}
			add(controlPanel,BorderLayout.EAST);

			update();
		}

		private String subTitleForFile()
		{
			if (_cgTemplateFile.isCustomTemplate()) {
				return FlexoLocalization.localizedForKey("custom_template_defined_in_template_repository")+" "
				+((CustomCGTemplateRepository)_cgTemplateFile.getRepository()).getName();
			}
			else {
				return FlexoLocalization.localizedForKey("application_template_ro");
			}
		}

		protected void update()
		{
			title.setText(_cgTemplateFile.getTemplateName()
					+ ((_cgTemplateFile instanceof CGTemplateFile) && ((CGTemplateFile) _cgTemplateFile).isEdited() ? "[" + FlexoLocalization.localizedForKey("edited") + "]" : ""));
			for (FlexoActionButton button : actionButtons) {
				button.update();
			}
		}
	}

	public SGController getController()
	{
		return _controller;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification)
	{
		DisplayContext previousDisplayContext = null;
		if (_codeDisplayer != null) {
			previousDisplayContext 
			= _codeDisplayer.getDisplayContext();
			if (logger.isLoggable(Level.FINE)) {
				logger.fine ("Rebuild view, display context is: "+previousDisplayContext);
			}
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine ("RECEIVED "+dataModification+" for "+observable);
		}
		if (dataModification instanceof TemplateFileEdited) {
			_codeDisplayer.setEditable(true);
		}
		else if (dataModification instanceof TemplateFileChanged) {
			_codeDisplayer.refresh();
		}
		else if (dataModification instanceof TemplateFileSaved) {
			_codeDisplayer.setEditable(false);
		}
		else if (dataModification instanceof TemplateFileEditionCancelled) {
			_codeDisplayer.setEditable(false);
			_codeDisplayer.refresh();
		}
		
		if ((previousDisplayContext != null) && (_codeDisplayer != null)) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Restore display context: "+previousDisplayContext);
			}
			_codeDisplayer.applyDisplayContext(previousDisplayContext);
		}
		
		updateView();
	}

	@Override
	public void deleteModuleView() 
	{
		logger.info("CGTemplateFileModuleView view deleted");
		getController().removeModuleView(this);
		_cgTemplateFile.deleteObserver(this);
	}

	@Override
	public FlexoPerspective<FlexoModelObject> getPerspective() 
	{
		return _controller.CODE_GENERATION_PERSPECTIVE;
	}

	@Override
	public CGTemplate getRepresentedObject()
	{
		return _cgTemplateFile;
	}

	@Override
	public void willHide() 
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void willShow()
	{
		// TODO Auto-generated method stub
	}

	protected interface CodeDisplayer extends TemplateFileContentEditor
	{
		public void setEditable(boolean aBoolean);
		public InputHandler getInputHandler();
		public void refresh();
		public void addToFocusListener (FocusListener aFocusListener);
		public DisplayContext getDisplayContext();
		public void applyDisplayContext(DisplayContext context);
	}

	protected class JavaCodePanel extends JavaCodeDisplayer implements CodeDisplayer
	{
		private final CGTemplate _file;

		protected JavaCodePanel(CGTemplate file)
		{
			super(file.getContent());
			_file = file;
		}

		@Override
		public void refresh()
		{
			setTextKeepDisplayContext(_file.getContent());
		}

		@Override
		public String getEditedContent()
		{
			return getText();
		}

		@Override
		public void setEditedContent(String content)
		{
			setTextKeepDisplayContext(content);
		}

		@Override
		public void addToFocusListener (FocusListener aFocusListener)
		{
			addFocusListener(aFocusListener);
		}

	}

	protected class XMLCodePanel extends XMLCodeDisplayer implements CodeDisplayer
	{
		private final CGTemplate _file;

		protected XMLCodePanel(CGTemplate file)
		{
			super(file.getContent());
			_file = file;
		}

		@Override
		public void refresh()
		{
			setTextKeepDisplayContext(_file.getContent());
		}

		@Override
		public String getEditedContent()
		{
			return getText();
		}

		@Override
		public void setEditedContent(String content)
		{
			setTextKeepDisplayContext(content);
		}

		@Override
		public void addToFocusListener (FocusListener aFocusListener)
		{
			addFocusListener(aFocusListener);
		}

	}

	protected class HTMLCodePanel extends HTMLCodeDisplayer implements CodeDisplayer
	{
		private final CGTemplate _file;

		protected HTMLCodePanel(CGTemplate file)
		{
			super(file.getContent());
			_file = file;
		}

		@Override
		public void refresh()
		{
			setTextKeepDisplayContext(_file.getContent());
		}

		@Override
		public String getEditedContent()
		{
			return getText();
		}

		@Override
		public void setEditedContent(String content)
		{
			setTextKeepDisplayContext(content);
		}

		@Override
		public void addToFocusListener (FocusListener aFocusListener)
		{
			addFocusListener(aFocusListener);
		}

	}

	protected class TextCodePanel extends TextCodeDisplayer implements CodeDisplayer
	{
		private final CGTemplate _file;

		protected TextCodePanel(CGTemplate file)
		{
			super(file.getContent());
			_file = file;
		}

		@Override
		public void refresh()
		{
			setTextKeepDisplayContext(_file.getContent());
		}

		@Override
		public String getEditedContent()
		{
			return getText();
		}

		@Override
		public void setEditedContent(String content)
		{
			setTextKeepDisplayContext(content);
		}

		@Override
		public void addToFocusListener (FocusListener aFocusListener)
		{
			addFocusListener(aFocusListener);
		}

	}
	protected class VTLJavaCodePanel extends VTLJavaCodeDisplayer implements CodeDisplayer
	{
		private final CGTemplate _file;

		protected VTLJavaCodePanel(CGTemplate file)
		{
			super(file.getContent());
			_file = file;
		}

		@Override
		public void refresh()
		{
			setTextKeepDisplayContext(_file.getContent());
		}

		@Override
		public String getEditedContent()
		{
			return getText();
		}

		@Override
		public void setEditedContent(String content)
		{
			setTextKeepDisplayContext(content);
		}

		@Override
		public void addToFocusListener (FocusListener aFocusListener)
		{
			addFocusListener(aFocusListener);
		}

	}

	protected class VTLHTMLCodePanel extends VTLHTMLCodeDisplayer implements CodeDisplayer
	{
		private final CGTemplate _file;

		protected VTLHTMLCodePanel(CGTemplate file)
		{
			super(file.getContent());
			_file = file;
		}

		@Override
		public void refresh()
		{
			setTextKeepDisplayContext(_file.getContent());
		}

		@Override
		public String getEditedContent()
		{
			return getText();
		}

		@Override
		public void setEditedContent(String content)
		{
			setTextKeepDisplayContext(content);
		}

		@Override
		public void addToFocusListener (FocusListener aFocusListener)
		{
			addFocusListener(aFocusListener);
		}

	}

	protected class WODCodePanel extends WODCodeDisplayer implements CodeDisplayer
	{
		private final CGTemplate _file;

		protected WODCodePanel(CGTemplate file)
		{
			super(file.getContent());
			_file = file;
		}

		@Override
		public void refresh()
		{
			setTextKeepDisplayContext(_file.getContent());
		}

		@Override
		public String getEditedContent()
		{
			return getText();
		}

		@Override
		public void setEditedContent(String content)
		{
			setTextKeepDisplayContext(content);
		}

		@Override
		public void addToFocusListener (FocusListener aFocusListener)
		{
			addFocusListener(aFocusListener);
		}

	}


	/**
	 * Returns flag indicating if this view is itself responsible for scroll management
	 * When not, Flexo will manage it's own scrollbar for you
	 * 
	 * @return
	 */
	@Override
	public boolean isAutoscrolled() 
	{
		return true;
	}

	@Override
	public FlexoModelObject getFocusedObject()
	{
		return getRepresentedObject();
	}

	@Override
	public Vector getGlobalSelection() 
	{
		return null;
	}

	@Override
	public String getEditedContent()
	{
		if (_codeDisplayer != null) {
			return _codeDisplayer.getEditedContent();
		}
		return null;
	}

	@Override
	public void setEditedContent(String content)
	{
		if (_codeDisplayer != null) {
			_codeDisplayer.setEditedContent(content);
		}
	}

	public boolean isOpenedInSeparateWindow() {
		return openedInSeparateWindow;
	}

	public void setOpenedInSeparateWindow(boolean openedInSeparateWindow) {
		this.openedInSeparateWindow = openedInSeparateWindow;
	}

	@Override
	public FlexoEditor getEditor() 
	{
		return _controller.getEditor();
	}


}
