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
package org.openflexo.doceditor.view;

import java.awt.BorderLayout;
import java.io.File;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import org.openflexo.doceditor.controller.DEController;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.toc.TOCRepository;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;

/**
 * 
 * @author gpolet
 */
public class DERepositoryModuleView extends JPanel implements ModuleView<TOCRepository>, FlexoObserver {

	protected static final Logger logger = Logger.getLogger(DERepositoryModuleView.class.getPackage().getName());

	private static final File STYLESHEET_FILE = new FileResource("Resources/FlexoDocumentationMasterStyle.css");

	protected DEController controller;

	private TOCRepository codeRepository;

	private JEditorPane htmlComponent;

	private JScrollPane scrollPane;

	private boolean isShown = false;

	private FlexoPerspective perspective;

	/**
	 * @param _process
	 * 
	 */
	public DERepositoryModuleView(TOCRepository repository, DEController ctrl, FlexoPerspective perspective) {
		super(new BorderLayout());
		codeRepository = repository;
		repository.addObserver(this);
		this.controller = ctrl;
		this.perspective = perspective;
		htmlComponent = new JEditorPane("text/html", "");
		htmlComponent.setEditable(false);
		StyleSheet styleSheet = new StyleSheet();
		try {
			styleSheet.setBase(STYLESHEET_FILE.toURL());
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Documentation Editor Stylesheet not found");
			}
		}
		((HTMLEditorKit) htmlComponent.getEditorKit()).setStyleSheet(styleSheet);
		try {
			((HTMLDocument) htmlComponent.getDocument()).setBase(ctrl.getProject().getImportedImagesDir().getParentFile().toURI().toURL()); /* set the base to the parent of the imported images dir */
		} catch (MalformedURLException e) {
			e.printStackTrace();
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Hum, this sucks,..., oh well, images won't be displayed");
			}
		}
		/*
		 * Note for later. If we want to entirely control image resolvation, for
		 * screenshots for example then we can set our own HTMLEditorKit by
		 * overriding the getViewFactory() method and return our own ViewFactory
		 * by extending HTMLViewFactory and override the public View
		 * create(Element elem) method
		 */
		// GPO: Don't call refresh() now because it is expensive, the willShow() method will!
		scrollPane = new JScrollPane(htmlComponent);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane);
		validate();
	}

	/**
	 * Overrides getPerspective
	 * 
	 * @see org.openflexo.view.ModuleView#getPerspective()
	 */
	@Override
	public FlexoPerspective getPerspective() {
		return perspective;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable instanceof TOCEntry && ((TOCEntry) observable).getRepository() == codeRepository) {
			requestRefresh();
		} else if (observable == codeRepository) {
			requestRefresh();
		}
	}

	// CPU-expensive
	protected void refresh() {
		if (isShown) {
			htmlComponent.setText(codeRepository.buildDocument(controller.getProject().getDocumentationCssResource().getFile()));
		}
	}

	protected boolean refreshRequested = false;

	private synchronized void requestRefresh() {
		if (refreshRequested) {
			return;
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				refresh();
				refreshRequested = false;
			}
		});
	}

	/**
	 * Overrides getRepresentedObject
	 * 
	 * @see org.openflexo.view.ModuleView#getRepresentedObject()
	 */
	@Override
	public TOCRepository getRepresentedObject() {
		return codeRepository;
	}

	/**
	 * Overrides delete
	 * 
	 * @see org.openflexo.view.ModuleView#deleteModuleView()
	 */
	@Override
	public void deleteModuleView() {
		controller.removeModuleView(this);
		codeRepository.deleteObserver(this);
		perspective = null;
		codeRepository = null;
	}

	@Override
	public boolean isAutoscrolled() {
		return true;
	}

	@Override
	public void willHide() {
		isShown = false;
	}

	@Override
	public void willShow() {
		isShown = true;
		refresh();
	}

}
