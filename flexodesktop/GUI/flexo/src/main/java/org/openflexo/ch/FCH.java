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
package org.openflexo.ch;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.GeneralPreferences;
import org.openflexo.drm.DocItem;
import org.openflexo.drm.DocItemFolder;
import org.openflexo.drm.DocResourceCenter;
import org.openflexo.drm.DocResourceManager;
import org.openflexo.drm.Language;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.FlexoModule;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.FlexoMainPane;
import org.openflexo.view.ModuleView;

/**
 * Utility class provinding facilities to handle contextual help Note: FCH stands for FlexoContextualHelp (ou bien
 * "Fuck cette Connerie de Help")
 * 
 * @author sguerin
 */
public class FCH {

	private static final Logger logger = Logger.getLogger(FCH.class.getPackage().getName());

	private static Hashtable<JComponent, DocItem> _docForComponent = new Hashtable<JComponent, DocItem>();

	private static synchronized Window getWindowForComponent(JComponent component) {
		JComponent current = component;
		Container parent;
		while (current.getParent() != null) {
			parent = current.getParent();
			if (parent instanceof JComponent) {
				current = (JComponent) parent;
			} else if (parent instanceof Window) {
				return (Window) parent;
			} else {
				return null;
			}
		}
		return null;
	}

	private static Hashtable<JComponent, String> _pendingComponents = new Hashtable<JComponent, String>();

	private static class SortComponents {
		private Vector<JComponent> initialVector;
		private Vector<Component> sortedVector;

		protected SortComponents(Vector<JComponent> sortingSet, Window window) {
			initialVector = sortingSet;
			sortedVector = new Vector<Component>();
			populateFrom(window);
		}

		private void populateFrom(Container component) {
			if (initialVector.contains(component)) {
				sortedVector.add(component);
			}
			for (int i = 0; i < component.getComponentCount(); i++) {
				Component comp = component.getComponent(i);
				if (comp instanceof Container) {
					populateFrom((Container) comp);
				} else if (initialVector.contains(comp)) {
					sortedVector.add(comp);
				}
			}
		}

		protected Vector<Component> getSortedVector() {
			return sortedVector;
		}

	}

	public static synchronized void validateWindow(Window window) {
		// logger.info("Validate window");
		Vector<JComponent> concernedComponents = new Vector<JComponent>();
		for (Enumeration<JComponent> en = _pendingComponents.keys(); en.hasMoreElements();) {
			JComponent next = en.nextElement();
			if (getWindowForComponent(next) == window) {
				concernedComponents.add(next);
			}
		}
		/*int i=0;
		for (Enumeration en=concernedComponents.elements(); en.hasMoreElements();i++) {
		    JComponent next = (JComponent)en.nextElement();
		    logger.info("Was "+i+" : "+(String)_pendingComponents.get(next));
		}*/
		/*  Collections.sort(concernedComponents,new Comparator() {
		    public int compare(Object o1, Object o2) {
		        if (!(o1 instanceof JComponent)) return 0;
		        if (!(o2 instanceof JComponent)) return 0;
		        JComponent c1 = (JComponent)o1;
		        JComponent c2 = (JComponent)o2;
		        if (c1.isAncestorOf(c2)) return -1;
		        if (c2.isAncestorOf(c1)) return 1;
		        return 0;
		    }
		});*/
		Vector<Component> sortedConcernedComponents = new SortComponents(concernedComponents, window).getSortedVector();

		/*  i=0;
		  for (Enumeration en=sortedConcernedComponents.elements(); en.hasMoreElements();i++) {
		      JComponent next = (JComponent)en.nextElement();
		      logger.info("Now "+i+" : "+(String)_pendingComponents.get(next));
		  }*/

		for (Enumeration<Component> en = sortedConcernedComponents.elements(); en.hasMoreElements();) {
			JComponent next = (JComponent) en.nextElement();
			validateHelpItem(next, _pendingComponents.get(next));
			_pendingComponents.remove(next);
		}
	}

	public static synchronized void setHelpItem(final JComponent component, final String anIdentifier) {
		// logger.info("setHelpItem for "+anIdentifier);
		if (component != null && anIdentifier != null) {
			_pendingComponents.put(component, anIdentifier);
		}
	}

	public static synchronized String getHelpItem(final JComponent component) {
		return _pendingComponents.get(component);
	}

	public static synchronized void validateHelpItem(final JComponent component, final String anIdentifier) {
		JComponent parent = getClosestDocumentedAncestorComponent(component);
		String identifier = anIdentifier;
		if (parent != null) {
			DocItem parentDocItem = getDocForComponent(parent);
			if (!anIdentifier.startsWith(parentDocItem.getIdentifier())) {
				identifier = parentDocItem.getIdentifier() + "-" + anIdentifier;
			}
			validateHelpItem(component, identifier, parentDocItem);
			return;
		}
		validateHelpItem(component, identifier, null);
	}

	public static synchronized void validateHelpItem(final JComponent component, final String anIdentifier, final DocItem parentDocItem) {
		// logger.info(">>>>>>> Validate for "+anIdentifier+" under "+parentDocItem);
		String identifier = anIdentifier;
		if (parentDocItem != null) {
			if (!anIdentifier.startsWith(parentDocItem.getIdentifier())) {
				identifier = parentDocItem.getIdentifier() + "-" + anIdentifier;
			}
		}
		DocItem item = DocResourceManager.getDocItem(identifier);
		if (item == null) {
			item = createCHEntry(component, identifier, parentDocItem);
		}
		_docForComponent.put(component, item);
		Language lang = DocResourceManager.instance().getLanguage(GeneralPreferences.getLanguage());
		String tooltipText = null;

		DocItem currentItem = item;

		while (tooltipText == null && currentItem != null) {
			if (currentItem.getLastApprovedActionForLanguage(lang) != null) {
				tooltipText = currentItem.getLastApprovedActionForLanguage(lang).getVersion().getShortHTMLDescription();
			}
			currentItem = currentItem.getInheritanceParentItem();
		}

		/*if (item.getLastApprovedActionForLanguage(lang) != null) {
		    tooltipText = item.getLastApprovedActionForLanguage(lang).getVersion().getShortHTMLDescription();
		}
		else {
		    tooltipText = FlexoLocalization.localizedForKeyAndLanguage("no_documentation",GeneralPreferences.getLanguage());
		}*/

		if (tooltipText == null) {
			tooltipText = FlexoLocalization.localizedForKeyAndLanguage("no_documentation", GeneralPreferences.getLanguage());
		}

		component.setToolTipText("<html>" + tooltipText + "</html>");
	}

	public static synchronized void setHelpItem(JComponent component, DocItem item) {
		setHelpItem(component, item.getIdentifier());
	}

	private static synchronized JComponent getClosestDocumentedAncestorComponent(JComponent component) {
		JComponent parent = null;
		DocItem parentItem = null;
		JComponent current = component;
		while (parentItem == null && current.getParent() != null && current.getParent() instanceof JComponent) {
			current = (JComponent) current.getParent();
			parentItem = getDocForComponent(current);
			if (parentItem != null) {
				parent = current;
			}
		}
		return parent;
	}

	private static synchronized DocItem createCHEntry(JComponent component, String identifier, DocItem parentItem) {
		if (parentItem == null) {
			parentItem = DocResourceManager.instance().getFlexoToolSetItem();
		}
		logger.info("Create entry for " + identifier + " under " + parentItem);
		DocItem newEntry = DocItem.createDocItem(identifier, "", parentItem.getFolder(), DocResourceManager.instance()
				.getDocResourceCenter(), false);
		parentItem.addToEmbeddingChildItems(newEntry);
		return newEntry;
	}

	public static DocItem getDocForComponent(JComponent component) {
		return _docForComponent.get(component);
	}

	private static final String MAIN_PANE_ID = "main-pane";
	private static final String CONTROL_PANEL_ID = "control-panel";
	private static final String LEFT_VIEW_ID = "left-view";
	private static final String RIGHT_VIEW_ID = "right-view";

	public static void ensureHelpEntryForModuleHaveBeenCreated(FlexoModule module) {
		DocItem newModuleItem = getDocItemFor(module);
		DocItem mainPaneItem = getMainPaneItemFor(module);
		DocItem controlPanelItem = getControlPanelItemFor(module);
		DocItem leftViewItem = getLeftViewItemFor(module);
		DocItem rightViewItem = getRightViewItemFor(module);

		FlexoFrame frame = module.getFlexoFrame();
		FlexoMainPane mainPane = module.getFlexoController().getMainPane();
		if (mainPane != null) {
			setHelpItem(mainPane.getControlPanel(), controlPanelItem);
		}
	}

	public static DocItem getDocItemFor(FlexoModule module) {
		DocResourceCenter drc = DocResourceManager.instance().getDocResourceCenter();
		String identifier = module.getModule().getHelpTopic();
		if (DocResourceManager.getDocItem(identifier) == null) {
			DocItemFolder newModuleFolder = DocItemFolder.createDocItemFolder(identifier, "", DocResourceManager.instance()
					.getFlexoToolSetItem().getFolder(), drc);
			DocResourceManager.instance().getAbstractModuleItem().addToInheritanceChildItems(newModuleFolder.getPrimaryDocItem());
		}
		return DocResourceManager.getDocItem(identifier);
	}

	public static DocItem getMainPaneItemFor(FlexoModule module) {
		DocResourceCenter drc = DocResourceManager.instance().getDocResourceCenter();
		String identifier = module.getModule().getHelpTopic();
		String mainPaneId = identifier + "-" + MAIN_PANE_ID;
		DocItem newModuleItem = getDocItemFor(module);
		if (DocResourceManager.getDocItem(mainPaneId) == null) {
			DocItemFolder mainPaneFolder = DocItemFolder.createDocItemFolder(mainPaneId, "", newModuleItem.getFolder(), drc);
			DocResourceManager.instance().getAbstractMainPaneItem().addToInheritanceChildItems(mainPaneFolder.getPrimaryDocItem());
			newModuleItem.addToEmbeddingChildItems(mainPaneFolder.getPrimaryDocItem());
		}
		return DocResourceManager.getDocItem(mainPaneId);

	}

	public static DocItem getModuleViewItemFor(FlexoModule module, ModuleView view) {
		DocResourceCenter drc = DocResourceManager.instance().getDocResourceCenter();
		DocItem mainPaneItem = getMainPaneItemFor(module);
		DocItemFolder folder;
		if (view.getPerspective() != null) {
			String perspectiveIdentifier = view.getPerspective().getName();
			if (DocResourceManager.getDocItem(perspectiveIdentifier) == null) {
				DocItemFolder perspectiveFolder = DocItemFolder.createDocItemFolder(perspectiveIdentifier,
						"Documentation on that perspective", mainPaneItem.getFolder(), drc);
				mainPaneItem.addToEmbeddingChildItems(perspectiveFolder.getPrimaryDocItem());
			}
			folder = DocResourceManager.getDocItem(perspectiveIdentifier).getFolder();
		} else {
			folder = mainPaneItem.getFolder();
		}
		String moduleViewIdentifier = view.getClass().getName();
		if (moduleViewIdentifier.lastIndexOf('.') > 0) {
			moduleViewIdentifier = moduleViewIdentifier.substring(moduleViewIdentifier.lastIndexOf('.') + 1);
		}
		if (DocResourceManager.getDocItem(moduleViewIdentifier) == null) {
			DocItemFolder moduleViewFolder = DocItemFolder.createDocItemFolder(moduleViewIdentifier, "Documentation on that view", folder,
					drc);
			mainPaneItem.addToInheritanceChildItems(moduleViewFolder.getPrimaryDocItem());
			if (view.getPerspective() != null && folder.getPrimaryDocItem() != null) {
				moduleViewFolder.getPrimaryDocItem().addToRelatedToItems(folder.getPrimaryDocItem());
			}
		}
		return DocResourceManager.getDocItem(moduleViewIdentifier);

	}

	public static DocItem getControlPanelItemFor(FlexoModule module) {
		DocResourceCenter drc = DocResourceManager.instance().getDocResourceCenter();
		String identifier = module.getModule().getHelpTopic();
		String controlPanelId = identifier + "-" + CONTROL_PANEL_ID;
		DocItem newModuleItem = getDocItemFor(module);
		if (DocResourceManager.getDocItem(controlPanelId) == null) {
			DocItemFolder controlPanelFolder = DocItemFolder.createDocItemFolder(controlPanelId, "", newModuleItem.getFolder(), drc);
			DocResourceManager.instance().getAbstractControlPanelItem().addToInheritanceChildItems(controlPanelFolder.getPrimaryDocItem());
			newModuleItem.addToEmbeddingChildItems(controlPanelFolder.getPrimaryDocItem());
		}
		return DocResourceManager.getDocItem(controlPanelId);
	}

	public static DocItem getLeftViewItemFor(FlexoModule module) {
		DocResourceCenter drc = DocResourceManager.instance().getDocResourceCenter();
		String identifier = module.getModule().getHelpTopic();
		String leftViewId = identifier + "-" + LEFT_VIEW_ID;
		DocItem newModuleItem = getDocItemFor(module);
		if (DocResourceManager.getDocItem(leftViewId) == null) {
			DocItemFolder leftViewFolder = DocItemFolder.createDocItemFolder(leftViewId, "", newModuleItem.getFolder(), drc);
			DocResourceManager.instance().getAbstractLeftViewItem().addToInheritanceChildItems(leftViewFolder.getPrimaryDocItem());
			newModuleItem.addToEmbeddingChildItems(leftViewFolder.getPrimaryDocItem());
		}
		return DocResourceManager.getDocItem(leftViewId);
	}

	public static DocItem getRightViewItemFor(FlexoModule module) {
		DocResourceCenter drc = DocResourceManager.instance().getDocResourceCenter();
		String identifier = module.getModule().getHelpTopic();
		String rightViewId = identifier + "-" + RIGHT_VIEW_ID;
		DocItem newModuleItem = getDocItemFor(module);
		if (DocResourceManager.getDocItem(rightViewId) == null) {
			DocItemFolder rightViewFolder = DocItemFolder.createDocItemFolder(rightViewId, "", newModuleItem.getFolder(), drc);
			DocResourceManager.instance().getAbstractRightViewItem().addToInheritanceChildItems(rightViewFolder.getPrimaryDocItem());
			newModuleItem.addToEmbeddingChildItems(rightViewFolder.getPrimaryDocItem());
		}
		return DocResourceManager.getDocItem(rightViewId);
	}

	public static void clearComponentsHashtable() {
		_docForComponent.clear();
		_pendingComponents.clear();
	}

}
