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
package org.openflexo.fib.view.widget.browser;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.openflexo.antar.binding.BindingValueChangeListener;
import org.openflexo.antar.binding.BindingValueListChangeListener;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBBrowser;
import org.openflexo.fib.model.FIBBrowserElement;
import org.openflexo.fib.model.FIBBrowserElement.FIBBrowserElementChildren;
import org.openflexo.fib.view.widget.FIBBrowserWidget;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class FIBBrowserModel extends DefaultTreeModel implements TreeModel {

	private static final Logger logger = Logger.getLogger(FIBBrowserModel.class.getPackage().getName());

	private Map<FIBBrowserElement, FIBBrowserElementType> _elementTypes;
	private FIBBrowser _fibBrowser;
	private final Multimap<Object, BrowserCell> contents;

	private final FIBBrowserWidget widget;

	/**
	 * Stores controls: key is the JButton and value the PropertyListActionListener
	 */
	// private Hashtable<JButton,PropertyListActionListener> _controls;

	public FIBBrowserModel(FIBBrowser fibBrowser, FIBBrowserWidget widget, FIBController controller) {
		super(null);
		contents = Multimaps.synchronizedMultimap(ArrayListMultimap.<Object, BrowserCell> create());
		_fibBrowser = fibBrowser;
		this.widget = widget;
		_elementTypes = new Hashtable<FIBBrowserElement, FIBBrowserElementType>();
		for (FIBBrowserElement browserElement : fibBrowser.getElements()) {
			addToElementTypes(browserElement, buildBrowserElementType(browserElement, controller));
		}

		//
	}

	public void delete() {
		for (FIBBrowserElement c : _elementTypes.keySet()) {
			_elementTypes.get(c).delete();
		}
		_elementTypes.clear();

		_elementTypes = null;
		_fibBrowser = null;
	}

	public FIBBrowserElement elementForClass(Class aClass) {
		return _fibBrowser.elementForClass(aClass);
	}

	public FIBBrowserElementType elementTypeForClass(Class aClass) {
		FIBBrowserElement element = elementForClass(aClass);
		if (element != null) {
			return _elementTypes.get(element);
		} else {
			logger.warning("Could not find element for class " + aClass);
			/*System.out.println("Available=");
			for (FIBBrowserElement e : _fibBrowser.getElements()) {
				System.out.println("> " + e.getName() + " for " + e.getDataClass());
			}*/
			return null;
		}
	}

	public FIBBrowser getBrowser() {
		return _fibBrowser;
	}

	/**
	 * @param root
	 * @return flag indicating if change was required
	 */
	public boolean updateRootObject(Object root) {
		if (root == null) {
			// TODO: check this
			logger.warning("Not implemented: please check this");
			setRoot(null);
			return false;
		}

		// nbOfBrowserCells = 0;

		BrowserCell rootCell = retrieveBrowserCell(root, null);
		if (getRoot() != rootCell) {
			if (getRoot() != null) {
				((BrowserCell) getRoot()).delete();
			}
			logger.fine("updateRootObject() with " + root + " rootCell=" + rootCell);
			setRoot(rootCell);
			// rootCell.loadChildren(this, null);

			return true;
		}
		return false;
	}

	public void fireTreeRestructured() {
		if (getRoot() instanceof BrowserCell) {
			((BrowserCell) getRoot()).update(true);
			// nodeStructureChanged((BrowserCell)getRoot());
		}
	}

	public void addToElementTypes(FIBBrowserElement element, FIBBrowserElementType elementType) {
		_elementTypes.put(element, elementType);
		elementType.setModel(this);
	}

	public void removeFromElementTypes(FIBBrowserElement element, FIBBrowserElementType elementType) {
		_elementTypes.remove(element);
		elementType.setModel(null);
	}

	public Map<FIBBrowserElement, FIBBrowserElementType> getElementTypes() {
		return _elementTypes;
	}

	private FIBBrowserElementType buildBrowserElementType(FIBBrowserElement browserElement, FIBController controller) {
		return new FIBBrowserElementType(browserElement, this, controller);
	}

	public TreePath[] getPaths(Object o) {
		Collection<BrowserCell> cells = contents.get(o);
		if (cells == null) {
			return new TreePath[0];
		}
		TreePath[] paths = new TreePath[cells.size()];
		int i = 0;
		for (BrowserCell cell : cells) {
			paths[i++] = getTreePath(cell);
		}
		return paths;
	}

	private TreePath getTreePath(BrowserCell cell) {
		Object[] path = getPathToRoot(cell);
		TreePath returned = new TreePath(path);
		return returned;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		BrowserCell cell = (BrowserCell) path.getLastPathComponent();
		if (cell.getBrowserElementType().isLabelEditable() && newValue instanceof String) {
			cell.getBrowserElementType().setEditableLabelFor(cell.getRepresentedObject(), (String) newValue);
		}
	}

	public Multimap<Object, BrowserCell> getContents() {
		return contents;
	}

	/**
	 * Obtain an iteration on all values which may be contained in this browser, by explicitely deeply-exploring all the tree.<br>
	 * Warning ! This method is dangerous for the perfs, and should never be called on a big "model"
	 * 
	 * @return
	 */
	public Iterator<Object> recursivelyExploreModelToRetrieveContents() {
		// We load all when not already up-t-date
		if (!exhaustiveContentsIsUpToDate) {
			computedExhaustiveContents.clear();
			if (getRoot() instanceof BrowserCell) {
				logger.info("!!!!! called recursivelyExploreModelToRetrieveContents() !!!!");
				((BrowserCell) getRoot()).update(true);
				exhaustiveContentsIsUpToDate = true;
			}
		}
		return contents.keys().iterator();
	}

	/**
	 * This set is used during exploration of all exhaustive contents, in order not no enter in an infinite loop
	 */
	private final Set<Object> computedExhaustiveContents = new HashSet<Object>();

	/**
	 * Flag indicating is exhaustive contents (obtained after deep-browsing) is up-to-date
	 */
	boolean exhaustiveContentsIsUpToDate = false;

	private BrowserCell retrieveBrowserCell(Object representedObject, BrowserCell parent) {
		/*System.out.println("retrieveBrowserCell for " + representedObject + " parent="
				+ (parent != null ? parent.getRepresentedObject() : "null"));*/
		ArrayList<BrowserCell> cells = new ArrayList<FIBBrowserModel.BrowserCell>(contents.get(representedObject));
		// Collection<BrowserCell> cells = contents.get(representedObject);
		if (cells != null) {
			for (BrowserCell cell : cells) {
				if (cell.getParent() == parent) {
					return cell;
				}
			}
		}
		BrowserCell returned = new BrowserCell(representedObject, parent);
		contents.put(representedObject, returned);
		// returned.update(false);
		return returned;
	}

	private void removeBrowserCell(BrowserCell cell) {
		contents.remove(cell.getRepresentedObject(), cell);
	}

	public boolean containsObject(Object representedObject) {
		return contents.get(representedObject) != null;
	}

	public Collection<BrowserCell> getBrowserCell(Object representedObject) {
		return contents.get(representedObject);
	}

	public class LoadingCell extends DefaultMutableTreeNode {
		public LoadingCell() {
			super("Loading...", false);
		}
	}

	public class BrowserCell extends DefaultMutableTreeNode /*implements Observer*/{

		private boolean loaded = false;

		private FIBBrowserElementType browserElementType;
		private boolean isDeleted = false;
		private boolean isVisible = true;

		public BrowserCell(Object representedObject, BrowserCell father) {
			setParent(father);
			setAllowsChildren(true);
			setUserObject(representedObject);
			browserElementType = elementTypeForClass(representedObject.getClass());

			if (browserElementType != null) {

				System.out.println("Build BrowserCell for " + representedObject);

				System.out.println("elementType=" + browserElementType.getBrowserElement().getName());

				final List<?> newChildrenObjects = browserElementType.getChildrenFor(getRepresentedObject());

				System.out.println("newChildrenObjects=" + newChildrenObjects);

				if (newChildrenObjects.size() > 0) {
					// System.out.println("For " + representedObject + " found " + newChildrenObjects.size() + " children: " +
					// newChildrenObjects);
					add(new LoadingCell());
				}

				/*if (getBrowserElement().getName() != null && getBrowserElement().getName().equals("diagramSpecification")) {
					logger.info("---------------> Created new DiagramSpecification browser element");
				}*/

				// dependingObjects = new DependingObjects(this);
				// dependingObjects.refreshObserving(browserElementType);

				listenLabelBindingValueChange();
				listenIconBindingValueChange();
				listenTooltipBindingValueChange();
				listenEnabledBindingValueChange();
				listenVisibleBindingValueChange();
				listenChildrenDataBindingValueChange();
				listenChildrenCastBindingValueChange();
				listenChildrenVisibleBindingValueChange();

			}

		}

		private BindingValueChangeListener<String> labelBindingValueChangeListener;
		private BindingValueChangeListener<Icon> iconBindingValueChangeListener;
		private BindingValueChangeListener<String> tooltipBindingValueChangeListener;
		private BindingValueChangeListener<Boolean> enabledBindingValueChangeListener;
		private BindingValueChangeListener<Boolean> visibleBindingValueChangeListener;
		private Map<FIBBrowserElementChildren, BindingValueChangeListener<?>> childrenDataBindingValueChangeListeners;
		private Map<FIBBrowserElementChildren, BindingValueChangeListener<?>> childrenCastBindingValueChangeListeners;
		private Map<FIBBrowserElementChildren, BindingValueChangeListener<Boolean>> childrenVisibleBindingValueChangeListeners;

		private void listenChildrenDataBindingValueChange() {
			if (childrenDataBindingValueChangeListeners != null) {
				for (BindingValueChangeListener<?> l : childrenDataBindingValueChangeListeners.values()) {
					l.stopObserving();
					l.delete();
				}
				childrenDataBindingValueChangeListeners.clear();
			} else {
				childrenDataBindingValueChangeListeners = new HashMap<FIBBrowserElement.FIBBrowserElementChildren, BindingValueChangeListener<?>>();
			}
			if (browserElementType.getBrowserElement() != null) {
				for (final FIBBrowserElementChildren children : browserElementType.getBrowserElement().getChildren()) {
					if (children.getData().isValid()) {
						BindingValueChangeListener<?> l;
						if (children.isMultipleAccess()) {
							l = new BindingValueListChangeListener<Object, List<Object>>(
									(DataBinding<List<Object>>) (DataBinding) children.getData(), browserElementType) {
								@Override
								public void bindingValueChanged(Object source, List<Object> newValue) {
									System.out.println(" bindingValueChanged() detected for data (as list) of children "
											+ children.getName() + " of " + browserElementType + " " + children.getData()
											+ " with newValue=" + newValue + " source=" + source);
									if (!isDeleted) {
										BrowserCell.this.update(false);
									}
								}
							};
						} else {
							l = new BindingValueChangeListener<Object>(children.getData(), browserElementType) {
								@Override
								public void bindingValueChanged(Object source, Object newValue) {
									System.out.println(" bindingValueChanged() detected for data of children " + children.getName()
											+ " of " + browserElementType + " " + children.getData() + " with newValue=" + newValue
											+ " source=" + source);
									if (!isDeleted) {
										BrowserCell.this.update(false);
									}
								}
							};
						}
						childrenDataBindingValueChangeListeners.put(children, l);
					}
				}
			}
		}

		private void listenChildrenCastBindingValueChange() {
			if (childrenCastBindingValueChangeListeners != null) {
				for (BindingValueChangeListener<?> l : childrenCastBindingValueChangeListeners.values()) {
					l.stopObserving();
					l.delete();
				}
				childrenCastBindingValueChangeListeners.clear();
			} else {
				childrenCastBindingValueChangeListeners = new HashMap<FIBBrowserElement.FIBBrowserElementChildren, BindingValueChangeListener<?>>();
			}
			if (browserElementType.getBrowserElement() != null) {
				for (final FIBBrowserElementChildren children : browserElementType.getBrowserElement().getChildren()) {
					if (children.getCast().isValid()) {
						BindingValueChangeListener<?> l = new BindingValueChangeListener<Object>(children.getCast(), browserElementType) {
							@Override
							public void bindingValueChanged(Object source, Object newValue) {
								System.out.println(" bindingValueChanged() detected for cast of children " + children.getName() + " of "
										+ browserElementType + " " + children.getCast() + " with newValue=" + newValue + " source="
										+ source);
								if (!isDeleted) {
									BrowserCell.this.update(false);
								}
							}
						};
						childrenCastBindingValueChangeListeners.put(children, l);
					}
				}
			}
		}

		private void listenChildrenVisibleBindingValueChange() {
			if (childrenVisibleBindingValueChangeListeners != null) {
				for (BindingValueChangeListener<?> l : childrenVisibleBindingValueChangeListeners.values()) {
					l.stopObserving();
					l.delete();
				}
				childrenVisibleBindingValueChangeListeners.clear();
			} else {
				childrenVisibleBindingValueChangeListeners = new HashMap<FIBBrowserElement.FIBBrowserElementChildren, BindingValueChangeListener<Boolean>>();
			}
			if (browserElementType.getBrowserElement() != null) {
				for (final FIBBrowserElementChildren children : browserElementType.getBrowserElement().getChildren()) {
					if (children.getVisible().isValid()) {
						BindingValueChangeListener<Boolean> l = new BindingValueChangeListener<Boolean>(children.getVisible(),
								browserElementType) {
							@Override
							public void bindingValueChanged(Object source, Boolean newValue) {
								System.out.println(" bindingValueChanged() detected for visble of children " + children.getName() + " of "
										+ browserElementType + " " + children.getVisible() + " with newValue=" + newValue + " source="
										+ source);
								if (!isDeleted) {
									BrowserCell.this.update(false);
								}
							}
						};
						childrenVisibleBindingValueChangeListeners.put(children, l);
					}
				}
			}
		}

		private void listenLabelBindingValueChange() {
			if (labelBindingValueChangeListener != null) {
				labelBindingValueChangeListener.stopObserving();
				labelBindingValueChangeListener.delete();
			}
			if (browserElementType.getBrowserElement() != null && browserElementType.getBrowserElement().getLabel().isValid()) {
				labelBindingValueChangeListener = new BindingValueChangeListener<String>(browserElementType.getBrowserElement().getLabel(),
						browserElementType) {
					@Override
					public void bindingValueChanged(Object source, String newValue) {
						System.out.println(" bindingValueChanged() detected for label of " + browserElementType + " "
								+ browserElementType.getBrowserElement().getLabel() + " with newValue=" + newValue + " source=" + source);
						if (!isDeleted) {
							BrowserCell.this.update(false);
						}
					}
				};
			}
		}

		private void listenIconBindingValueChange() {
			if (iconBindingValueChangeListener != null) {
				iconBindingValueChangeListener.stopObserving();
				iconBindingValueChangeListener.delete();
			}
			if (browserElementType.getBrowserElement() != null && browserElementType.getBrowserElement().getIcon().isValid()) {
				iconBindingValueChangeListener = new BindingValueChangeListener<Icon>(browserElementType.getBrowserElement().getIcon(),
						browserElementType) {
					@Override
					public void bindingValueChanged(Object source, Icon newValue) {
						System.out.println(" bindingValueChanged() detected for icon of " + browserElementType + " "
								+ browserElementType.getBrowserElement().getIcon() + " with newValue=" + newValue + " source=" + source);
						if (!isDeleted) {
							BrowserCell.this.update(false);
						}
					}
				};
			}
		}

		private void listenTooltipBindingValueChange() {
			if (tooltipBindingValueChangeListener != null) {
				tooltipBindingValueChangeListener.stopObserving();
				tooltipBindingValueChangeListener.delete();
			}
			if (browserElementType.getBrowserElement() != null && browserElementType.getBrowserElement().getTooltip().isValid()) {
				tooltipBindingValueChangeListener = new BindingValueChangeListener<String>(browserElementType.getBrowserElement()
						.getTooltip(), browserElementType) {
					@Override
					public void bindingValueChanged(Object source, String newValue) {
						System.out.println(" bindingValueChanged() detected for tooltip of " + browserElementType + " "
								+ browserElementType.getBrowserElement().getTooltip() + " with newValue=" + newValue + " source=" + source);
						if (!isDeleted) {
							BrowserCell.this.update(false);
						}
					}
				};
			}
		}

		private void listenEnabledBindingValueChange() {
			if (enabledBindingValueChangeListener != null) {
				enabledBindingValueChangeListener.stopObserving();
				enabledBindingValueChangeListener.delete();
			}
			if (browserElementType.getBrowserElement() != null && browserElementType.getBrowserElement().getEnabled().isValid()) {
				enabledBindingValueChangeListener = new BindingValueChangeListener<Boolean>(browserElementType.getBrowserElement()
						.getEnabled(), browserElementType) {
					@Override
					public void bindingValueChanged(Object source, Boolean newValue) {
						System.out.println(" bindingValueChanged() detected for enabled of " + browserElementType + " "
								+ browserElementType.getBrowserElement().getEnabled() + " with newValue=" + newValue + " source=" + source);
						if (!isDeleted) {
							BrowserCell.this.update(false);
						}
					}
				};
			}
		}

		private void listenVisibleBindingValueChange() {
			if (visibleBindingValueChangeListener != null) {
				visibleBindingValueChangeListener.stopObserving();
				visibleBindingValueChangeListener.delete();
			}
			if (browserElementType.getBrowserElement() != null && browserElementType.getBrowserElement().getVisible().isValid()) {
				visibleBindingValueChangeListener = new BindingValueChangeListener<Boolean>(browserElementType.getBrowserElement()
						.getVisible(), browserElementType) {
					@Override
					public void bindingValueChanged(Object source, Boolean newValue) {
						System.out.println(" bindingValueChanged() detected for visible of " + browserElementType + " "
								+ browserElementType.getBrowserElement().getVisible() + " with newValue=" + newValue + " source=" + source);
						if (!isDeleted) {
							BrowserCell.this.update(false);
						}
					}
				};
			}
		}

		@Override
		public BrowserCell getParent() {
			return (BrowserCell) super.getParent();
		}

		public Object getRepresentedObject() {
			return getUserObject();
		}

		public void loadChildren(final DefaultTreeModel model, final PropertyChangeListener progressListener) {
			if (loaded) {
				return;
			}

			loaded = true;

			update(false);

		}

		public boolean isLoaded() {
			return loaded;
		}

		/*@Override
		public List<DataBinding<?>> getDependencyBindings() {
			return getBrowserElementType().getDependencyBindings(getRepresentedObject());
		}*/

		// TODO: repair chained bindings, which is not more supported by this new implementation
		/*@Override
		public List<TargetObject> getChainedBindings(DataBinding binding, final TargetObject object) {
			for (FIBBrowserElementChildren child : browserElementType.getBrowserElement().getChildren()) {
				if (binding.equals(child.getData()) && child.getCast().isSet() && binding.toString().endsWith(object.propertyName)) {
					try {
						final Object bindingValue = child.getData().getBindingValue(browserElementType);
						List<?> list = ToolBox.getListFromIterable(bindingValue);
						if (list != null) {
							List<TargetObject> targetObjects = new ArrayList<TargetObject>();
							for (final Object o : list) {
								List<TargetObject> targetObjects2 = child.getCast().getTargetObjects(new BindingEvaluationContext() {

									@Override
									public Object getValue(BindingVariable variable) {
										if (variable.getVariableName().equals("child")) {
											return o;
										} else {
											return browserElementType.getValue(variable);
										}
									}
								});
								if (targetObjects2 != null) {
									targetObjects.addAll(targetObjects2);
								}
							}
							return targetObjects;
						} else {
							return child.getCast().getTargetObjects(new BindingEvaluationContext() {

								@Override
								public Object getValue(BindingVariable variable) {
									if (variable.getVariableName().equals("child")) {
										return bindingValue;
									} else {
										return browserElementType.getValue(variable);
									}
								}
							});

						}
					} catch (TypeMismatchException e) {
						continue;
					} catch (NullReferenceException e) {
						continue;
					} catch (InvocationTargetException e) {
						continue;
					}

				}
			}
			return null;
		}*/

		public void delete() {

			logger.fine("Delete BrowserCell for " + getRepresentedObject());

			if (childrenDataBindingValueChangeListeners != null) {
				for (BindingValueChangeListener<?> l : childrenDataBindingValueChangeListeners.values()) {
					l.stopObserving();
					l.delete();
				}
				childrenDataBindingValueChangeListeners.clear();
			}
			if (childrenCastBindingValueChangeListeners != null) {
				for (BindingValueChangeListener<?> l : childrenCastBindingValueChangeListeners.values()) {
					l.stopObserving();
					l.delete();
				}
				childrenCastBindingValueChangeListeners.clear();
			}
			if (childrenVisibleBindingValueChangeListeners != null) {
				for (BindingValueChangeListener<?> l : childrenVisibleBindingValueChangeListeners.values()) {
					l.stopObserving();
					l.delete();
				}
				childrenVisibleBindingValueChangeListeners.clear();
			}

			if (labelBindingValueChangeListener != null) {
				labelBindingValueChangeListener.stopObserving();
				labelBindingValueChangeListener.delete();
			}
			if (iconBindingValueChangeListener != null) {
				iconBindingValueChangeListener.stopObserving();
				iconBindingValueChangeListener.delete();
			}
			if (tooltipBindingValueChangeListener != null) {
				tooltipBindingValueChangeListener.stopObserving();
				tooltipBindingValueChangeListener.delete();
			}
			if (enabledBindingValueChangeListener != null) {
				enabledBindingValueChangeListener.stopObserving();
				enabledBindingValueChangeListener.delete();
			}
			if (visibleBindingValueChangeListener != null) {
				visibleBindingValueChangeListener.stopObserving();
				visibleBindingValueChangeListener.delete();
			}

			if (children != null) {
				for (Object c : children) {
					if (c instanceof BrowserCell) {
						((BrowserCell) c).delete();
					}
				}
			}

			if (getRepresentedObject() != null) {
				removeBrowserCell(this);
			}

			/*
			 * GPO: Commented next line. We should check why we drop this representedObject from the selection
			 * Why not also check if we are the current selected object?
			 * By all means, we should find another way to do this than by doing it in the TreeModel. We could do that in FIBBrowserWidget.treeNodesRemoved. 
			if (selection.contains(representedObject)) {
				selection.remove(representedObject);
			}
			 */
			setUserObject(null);
			browserElementType = null;
			setParent(null);
			if (children != null) {
				children.clear();
			}
			isDeleted = true;
		}

		public void update(boolean recursively) {

			List<BrowserCell> cellsToForceUpdate = new ArrayList<BrowserCell>();

			loaded = true;

			// During exploration of all exhaustive contents, in order not no enter in an infinite loop
			if (recursively) {
				computedExhaustiveContents.add(getRepresentedObject());
			}

			// logger.info("**************** update() " + this);
			if (browserElementType == null) {
				logger.warning("No element type registered for " + getRepresentedObject());
				return;
			}

			// Special case for cells that were declared as invisible
			// When becomes visible, must tells to parent to update
			if (!isVisible) {
				if (browserElementType.isVisible(getRepresentedObject())) {
					logger.fine("Cell " + this + " becomes visible");
					getParent().update(recursively);
				}
			}

			List<BrowserCell> oldChildren;
			List<BrowserCell> removedChildren;
			List<BrowserCell> newChildren = new ArrayList<BrowserCell>();

			if (children == null) {
				oldChildren = new ArrayList<BrowserCell>();
				removedChildren = new ArrayList<BrowserCell>();
			} else {
				if (children.size() == 1 && children.firstElement() instanceof LoadingCell) {
					removeAllChildren();
				}
				oldChildren = new ArrayList<BrowserCell>(children);
				removedChildren = new ArrayList<BrowserCell>(children);
			}

			final List<?> newChildrenObjects = /*(isEnabled ?*/browserElementType.getChildrenFor(getRepresentedObject()) /*: new Vector())*/;
			int index = 0;

			for (Object o : newChildrenObjects) {

				if (o != null && o != getRepresentedObject()) {
					BrowserCell cell = retrieveBrowserCell(o, this);
					FIBBrowserElementType childElementType = elementTypeForClass(o.getClass());
					if (childElementType != null && childElementType.isVisible(o)) {
						if (children != null && children.contains(cell)) {
							// OK, child still here
							removedChildren.remove(cell);
							if (recursively) {
								cell.update(true);
							}
							index = children.indexOf(cell) + 1;
						} else {
							newChildren.add(cell);
							if (children == null) {
								children = new Vector();
							}
							children.insertElementAt(cell, index);
							index++;
							// In order not to enter in possibly infinite loop, force update contents of this cell
							// only if cell not loaded and if represented object was not already registered
							if (recursively && !cell.isLoaded() && !computedExhaustiveContents.contains(cell.getRepresentedObject())) {
								// Do it at the end
								cellsToForceUpdate.add(cell);
								// cell.update(true);
							}
						}
					} else {
						cell.isVisible = false;
					}
				}
			}
			for (BrowserCell c : removedChildren) {
				if (children != null) {
					children.remove(c);
				}
				c.delete();
			}

			boolean requireSorting = false;
			if (children != null) {
				for (int i = 0; i < children.size() - 1; i++) {
					BrowserCell c1 = (BrowserCell) children.elementAt(i);
					BrowserCell c2 = (BrowserCell) children.elementAt(i + 1);
					if (newChildrenObjects.indexOf(c1.getRepresentedObject()) != newChildrenObjects.indexOf(c2.getRepresentedObject()) - 1) {
						requireSorting = true;
					}
				}
			}

			if (requireSorting) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Detected sorting required");
				}
				// Sort children according to supplied list
				Collections.sort(children, new Comparator<BrowserCell>() {
					@Override
					public int compare(BrowserCell o1, BrowserCell o2) {
						return newChildrenObjects.indexOf(o1.getRepresentedObject())
								- newChildrenObjects.indexOf(o2.getRepresentedObject());
					}
				});
			}

			// System.out.println("removedChildren ["+removedChildren.size()+"] "+removedChildren);
			// System.out.println("newChildren ["+newChildren.size()+"] "+newChildren);
			// System.out.println("children ["+children.size()+"] "+children);

			boolean structureChanged = false;

			if (removedChildren.size() > 0 || newChildren.size() > 0) {
				structureChanged = true;
				exhaustiveContentsIsUpToDate = false;
				if (oldChildren.size() == 0) {
					// Special case, i don't undertand why (SGU)
					// OK, issue seems to be MacOS only but workaround works on all platforms.
					// To observe the issue, load WKF module on a project that imports other projects
					// Imported workflow tree is not correctly initiated after reload of project.
					try {
						nodeStructureChanged(this);
					} catch (Exception e) {
						// Might happen when a structural modification will call parent's nodeChanged()
						// An Exception might be raised here
						// We should investigate further, but since no real consequences are raised here, we just ignore exception
						e.printStackTrace();
						logger.warning("Unexpected " + e.getClass().getSimpleName()
								+ " when refreshing browser, no severity but please investigate");
					}
				} else {
					if (removedChildren.size() > 0) {
						int[] childIndices = new int[removedChildren.size()];
						Object[] removedChildrenObjects = new Object[removedChildren.size()];
						for (int i = 0; i < removedChildren.size(); i++) {
							childIndices[i] = oldChildren.indexOf(removedChildren.get(i));
							removedChildrenObjects[i] = removedChildren.get(i);
						}
						nodesWereRemoved(this, childIndices, removedChildrenObjects);
					}
					if (newChildren.size() > 0) {
						int[] childIndices = new int[newChildren.size()];
						for (int i = 0; i < newChildren.size(); i++) {
							childIndices[i] = children.indexOf(newChildren.get(i));
						}
						nodesWereInserted(this, childIndices);
					}
				}
			}

			try {
				nodeChanged(this);
			} catch (ArrayIndexOutOfBoundsException e) {
				// Might happen when a structural modification will call parent's nodeChanged()
				// An ArrayIndexOutOfBoundsException might be raised here
				// We should investigate further, but since no real consequences are raised here, we just ignore exception
				// e.printStackTrace();
				logger.warning("Unexpected ArrayIndexOutOfBoundsException when refreshing browser, no severity but please investigate");
				nodeStructureChanged(this);
			} catch (NullPointerException e) {
				// Might happen when a structural modification will call parent's nodeChanged()
				// An NullPointerException might be raised here
				// We should investigate further, but since no real consequences are raised here, we just ignore exception
				// e.printStackTrace();
				logger.warning("Unexpected NullPointerException when refreshing browser, no severity but please investigate");
			}

			if (requireSorting) {

				/*Object wasSelected = widget.getSelectedObject();
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Will reselect " + wasSelected);
				}*/

				try {
					nodeStructureChanged(this);
				} catch (Exception e) {
					// Might happen when a structural modification will call parent's nodeChanged()
					// An Exception might be raised here
					// We should investigate further, but since no real consequences are raised here, we just ignore exception
					e.printStackTrace();
					logger.warning("Unexpected " + e.getClass().getSimpleName()
							+ " when refreshing browser, no severity but please investigate");
				}

				/*if (wasSelected != null) {
					widget.resetSelection();
					widget.addToSelection(wasSelected);
				}*/

			}

			/*if (wasSelected) {
				widget.addToSelection(representedObject);
			}*/

			// dependingObjects.refreshObserving(browserElementType);

			for (BrowserCell cell : cellsToForceUpdate) {
				cell.update(true);
			}
		}

		/*@Override
		public void update(Observable o, Object arg) {
			// logger.info("Object " + o + " received " + arg);

			if (!isDeleted && o == getRepresentedObject()) {
				update(false);
			}
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			// logger.info("Object " + representedObject + " received " + evt);

			if (!isDeleted) {
				// System.out.println("cell " + this + " propertyChanged " + evt.getPropertyName() + " for " + evt.getSource());
				update(false);
			}
		}*/

		public FIBBrowserElement getBrowserElement() {
			return browserElementType.getBrowserElement();
		}

		public FIBBrowserElementType getBrowserElementType() {
			return browserElementType;
		}

		public TreePath getTreePath() {
			return new TreePath(getPathToRoot(this, 0));
		}

	}

}
