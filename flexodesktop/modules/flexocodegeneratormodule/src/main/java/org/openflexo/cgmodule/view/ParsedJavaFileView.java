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

import java.awt.Color;
import java.awt.Dimension;
import java.util.logging.Logger;

import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.openflexo.cgmodule.GeneratorCst;
import org.openflexo.cgmodule.controller.GeneratorController;
import org.openflexo.cgmodule.controller.browser.fjp.JavaParserBrowser;
import org.openflexo.cgmodule.view.CodeDisplayer.ASCIIFileCodePanel;
import org.openflexo.components.browser.view.BrowserView;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGFile.FileContentEditor;
import org.openflexo.foundation.rm.cg.ContentSource;
import org.openflexo.foundation.rm.cg.JavaFileResource;
import org.openflexo.generator.cg.CGJavaFile;
import org.openflexo.generator.rm.GenerationAvailableFileResource;
import org.openflexo.javaparser.FJPJavaElement;
import org.openflexo.javaparser.FJPJavaEntity;
import org.openflexo.javaparser.FJPJavaParseException;
import org.openflexo.jedit.LinesHighlight;
import org.openflexo.logging.FlexoLogger;

public class ParsedJavaFileView extends JSplitPane implements FileContentEditor {

	private Logger logger = FlexoLogger.getLogger(ParsedJavaFileView.class.getPackage().getName());

	private CodeDisplayer javaCodeDisplayer;
	private GeneratorController _controller;
	private JavaFileResource _javaFileResource;
	private JavaParserBrowser _browser;
	private JavaParserBrowserView _browserView;

	public ParsedJavaFileView(JavaFileResource javaFileResource, GeneratorController controller, boolean editable) {
		super(JSplitPane.HORIZONTAL_SPLIT);
		_controller = controller;
		_javaFileResource = javaFileResource;
		if (editable) {
			javaCodeDisplayer = new CodeEditor((GenerationAvailableFileResource) javaFileResource, controller);
			javaCodeDisplayer.getComponent().requestFocus();
		} else {
			javaCodeDisplayer = new CodeDisplayer((GenerationAvailableFileResource) javaFileResource, ContentSource.CONTENT_ON_DISK,
					controller);
		}
		_browser = new JavaParserBrowser((CGJavaFile) javaFileResource.getCGFile(), _controller);
		_browserRootObject = _browser.getRootObject();
		_browserView = new JavaParserBrowserView(_browser);
		_codePanel = (ASCIIFileCodePanel) javaCodeDisplayer.getComponent();
		setLeftComponent(_codePanel);
		setRightComponent(_browserView);

		Dimension preferredDim = javaCodeDisplayer.getComponent().getMinimumSize();
		preferredDim.width = 100;
		javaCodeDisplayer.getComponent().setMinimumSize(preferredDim);
		setResizeWeight(1);
		resetToPreferredSizes();
		validate();
		if (_browserRootObject instanceof FJPJavaParseException) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if (_browser != null && _browserRootObject != null) {
						_browser.addToSelected(_browserRootObject);
						_browserView.treeSingleClick(_browserRootObject);
					}
				}
			});
		}
	}

	private FJPJavaElement _browserRootObject;

	public void update() {
		javaCodeDisplayer.update();
		if (_browserRootObject != _browser.getRootObject()) {
			_browser.update();
			_browserRootObject = _browser.getRootObject();
			if (_browserRootObject instanceof FJPJavaParseException) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if (_browser != null && _browserRootObject != null) {
							_browser.addToSelected(_browserRootObject);
							_browserView.treeSingleClick(_browserRootObject);
						}
					}
				});
			}
		}
	}

	private LinesHighlight _currentHighlight;
	private ASCIIFileCodePanel _codePanel;

	private Color HIGHLIGHT_BG_COLOR = new Color(232, 242, 254);
	private Color HIGHLIGHT_FG_COLOR = new Color(181, 213, 255);

	public class JavaParserBrowserView extends BrowserView {

		public JavaParserBrowserView(JavaParserBrowser browser) {
			super(browser, _controller);
			setMinimumSize(new Dimension(GeneratorCst.MINIMUM_BROWSER_VIEW_WIDTH, GeneratorCst.MINIMUM_BROWSER_VIEW_HEIGHT));
			validate();
		}

		@Override
		public void treeSingleClick(FlexoModelObject object) {
			if (_currentHighlight != null) {
				_codePanel.getPainter().removeCustomHighlight(_currentHighlight);
				_currentHighlight = null;
			}
			if (object instanceof FJPJavaEntity) {
				int firstLine = ((FJPJavaEntity) object).getLineNumber();
				int lastLine = firstLine + ((FJPJavaEntity) object).getLinesCount();
				_currentHighlight = new LinesHighlight(firstLine - 1, lastLine - 2, HIGHLIGHT_BG_COLOR, HIGHLIGHT_FG_COLOR);
				_codePanel.getPainter().addCustomHighlight(_currentHighlight);
				_codePanel.setFirstLine(firstLine > 5 ? firstLine - 5 : firstLine);
			} else if (object instanceof FJPJavaParseException) {
				int firstLine = ((FJPJavaParseException) object).getLine();
				_currentHighlight = new LinesHighlight(firstLine - 1, firstLine - 1, new Color(255, 214, 214), Color.RED);
				_codePanel.getPainter().addCustomHighlight(_currentHighlight);
				_codePanel.setFirstLine(firstLine > 5 ? firstLine - 5 : firstLine);
			}
		}

		@Override
		public void treeDoubleClick(FlexoModelObject object) {
		}

	}

	@Override
	public String getEditedContentForKey(String contentKey) {
		return ((CodeEditor) javaCodeDisplayer).getEditedContentForKey(contentKey);
	}

	@Override
	public void setEditedContent(CGFile file) {
		((CodeEditor) javaCodeDisplayer).setEditedContent(file);
	}

	public CodeDisplayer getJavaCodeDisplayer() {
		return javaCodeDisplayer;
	}

}
