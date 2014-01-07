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
package org.openflexo.fib.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.tree.TreeNode;

import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Finder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;

@ModelEntity(isAbstract = true)
@ImplementationClass(FIBContainer.FIBContainerImpl.class)
public abstract interface FIBContainer extends FIBComponent {

	@PropertyIdentifier(type = List.class)
	public static final String SUB_COMPONENTS_KEY = "subComponents";

	@Getter(value = SUB_COMPONENTS_KEY, cardinality = Cardinality.LIST, inverse = FIBComponent.PARENT_KEY)
	public List<FIBComponent> getSubComponents();

	@Setter(SUB_COMPONENTS_KEY)
	public void setSubComponents(List<FIBComponent> subComponents);

	@Adder(SUB_COMPONENTS_KEY)
	public void addToSubComponents(FIBComponent aSubComponent);

	public void addToSubComponents(FIBComponent aComponent, ComponentConstraints someConstraints);

	public void addToSubComponents(FIBComponent aComponent, ComponentConstraints someConstraints, int subComponentIndex);

	@Remover(SUB_COMPONENTS_KEY)
	public void removeFromSubComponents(FIBComponent aSubComponent);

	@Finder(collection = SUB_COMPONENTS_KEY, attribute = FIBComponent.NAME_KEY)
	public FIBComponent getSubComponentNamed(String name);

	public void reorderComponents();

	public void append(FIBContainer container);

	public static abstract class FIBContainerImpl extends FIBComponentImpl implements FIBContainer {

		private static final Logger logger = Logger.getLogger(FIBContainer.class.getPackage().getName());

		// private Vector<FIBComponent> subComponents;

		public FIBContainerImpl() {
			// subComponents = new Vector<FIBComponent>();
		}

		/*public Class getDataClass()
		{
		// This was added to return a class when data is defined
		if (super.getDataClass() == null && getData() != null && getData().isValid()) {
			return TypeUtils.getBaseClass(getData().getBinding().getAccessedType());
		}
		return super.getDataClass();
		}*/

		/**
		 * Return a recursive list of all components beeing embedded in this container
		 * 
		 * @return
		 */
		public List<FIBComponent> getAllSubComponents() {
			List<FIBComponent> returned = new ArrayList<FIBComponent>();
			appendAllSubComponents(this, returned);
			return returned;
		}

		private void appendAllSubComponents(FIBContainer container, List<FIBComponent> returned) {
			for (FIBComponent child : container.getSubComponents()) {
				returned.add(child);
				if (child instanceof FIBContainer) {
					appendAllSubComponents((FIBContainer) child, returned);
				}
			}
		}

		@Override
		public void updateBindingModel() {
			super.updateBindingModel();
			if (deserializationPerformed) {
				for (FIBComponent child : getSubComponents()) {
					child.updateBindingModel();
				}
			}
		}

		/*@Override
		public Vector<FIBComponent> getSubComponents() {
			return subComponents;
		}*/

		/*public void setSubComponents(Vector<FIBComponent> someComponents) {
			FIBPropertyNotification<Vector<FIBComponent>> notification = requireChange(Parameters.subComponents, someComponents);
			if (notification != null) {
				subComponents = someComponents;
				hasChanged(notification);
			}
		}*/

		/*@Override
		public void addToSubComponents(FIBComponent aComponent) {
			addToSubComponents(aComponent, null);
		}*/

		@Override
		public void addToSubComponents(FIBComponent aComponent, ComponentConstraints someConstraints) {
			addToSubComponents(aComponent, someConstraints, getSubComponents().size());
		}

		@Override
		public void addToSubComponents(FIBComponent aComponent, ComponentConstraints someConstraints, int subComponentIndex) {
			getSubComponents().add(subComponentIndex, aComponent);
			if (someConstraints != null) {
				aComponent.getConstraints().ignoreNotif = true;
				aComponent.getConstraints().putAll(someConstraints);
				aComponent.getConstraints().ignoreNotif = false;
			}
			if (deserializationPerformed) {
				updateComponentIndexForInsertionIndex(aComponent, subComponentIndex);
			}

			performSuperAdder(SUB_COMPONENTS_KEY, aComponent);
			// Hack to put at right index
			// TODO: manage a performSuperAdder(index)
			getSubComponents().remove(aComponent);

			if (deserializationPerformed) {
				reorderComponents();
			}
			if (aComponent instanceof FIBWidget && ((FIBWidget) aComponent).getManageDynamicModel()) {
				if (deserializationPerformed) {
					updateBindingModel();
				}
			}
			getPropertyChangeSupport().firePropertyChange(SUB_COMPONENTS_KEY, null, getSubComponents());
		}

		private void updateComponentIndexForInsertionIndex(FIBComponent component, int insertionIndex) {
			if (getSubComponents().size() > 0) {
				FIBComponent previous = null;
				FIBComponent next = null;
				if (insertionIndex < getSubComponents().size()) {
					next = getSubComponents().get(insertionIndex);
				}
				if (insertionIndex > 0) {
					previous = getSubComponents().get(insertionIndex - 1);
				}

				if (previous != null) {
					if (previous.getIndex() == null) {
						if (component.getIndex() != null && component.getIndex() < 0) {
							component.setIndex(null);
						}
					} else if (previous.getIndex() < 0) {
						if (component.getIndex() != null && component.getIndex() < previous.getIndex()) {
							component.setIndex(previous.getIndex());
						}
					} else {
						if (component.getIndex() == null || component.getIndex() < previous.getIndex()) {
							component.setIndex(previous.getIndex());
						}
					}
				}
				if (next != null) {
					if (next.getIndex() == null) {
						if (component.getIndex() != null && component.getIndex() >= 0) {
							component.setIndex(null);
						}
					} else if (next.getIndex() < 0) {
						if (component.getIndex() == null || component.getIndex() > next.getIndex()) {
							component.setIndex(next.getIndex());
						}
					} else {
						if (component.getIndex() != null && component.getIndex() > next.getIndex()) {
							component.setIndex(next.getIndex());
						}
					}
				}
			}
		}

		/*@Override
		public FIBComponent getSubComponentNamed(String name) {
			for (FIBComponent c : getSubComponents()) {
				if (c.getName() != null && c.getName().equals(name)) {
					return c;
				}
			}
			return null;
		}*/

		/*@Override
		public void removeFromSubComponents(FIBComponent aComponent) {
			removeFromSubComponentsNoNotification(aComponent);
			getPropertyChangeSupport().firePropertyChange(Parameters.subComponents.name(), null, subComponents);
		}

		public void removeFromSubComponentsNoNotification(FIBComponent aComponent) {
			aComponent.setParent(null);
			subComponents.remove(aComponent);
		}*/

		/*public void notifyComponentMoved(FIBComponent aComponent) {
			getPropertyChangeSupport().firePropertyChange(Parameters.subComponents.name(), null, subComponents);
		}*/

		@Override
		public Enumeration<FIBComponent> children() {
			return (new Vector<FIBComponent>(getSubComponents())).elements();
		}

		@Override
		public boolean getAllowsChildren() {
			return true;
		}

		@Override
		public FIBComponent getChildAt(int childIndex) {
			return getSubComponents().get(childIndex);
		}

		@Override
		public int getChildCount() {
			return getSubComponents().size();
		}

		@Override
		public int getIndex(TreeNode node) {
			return getSubComponents().indexOf(node);
		}

		@Override
		public boolean isLeaf() {
			return getSubComponents().size() == 0;
		}

		// public static final String INHERITED = "Inherited";

		@Override
		public void append(FIBContainer container) {
			// logger.info(toString()+" append "+container);

			// if (this instanceof FIBTab && ())
			List<FIBComponent> mergedComponents = new ArrayList<FIBComponent>();
			for (int i = container.getSubComponents().size() - 1; i >= 0; i--) {
				FIBComponent c2 = container.getSubComponents().get(i);
				if (c2.getName() != null && c2 instanceof FIBContainer) {
					for (FIBComponent c1 : getSubComponents()) {
						if (c2.getName().equals(c1.getName()) && c1 instanceof FIBContainer) {
							((FIBContainer) c1).append((FIBContainer) c2);
							mergedComponents.add(c2);
							logger.fine("Merged " + c1 + " and " + c2);
							break;
						}
					}
				}
			}
			for (int i = 0; i < container.getSubComponents().size(); i++) {
				FIBComponent child = container.getSubComponents().get(i);
				if (mergedComponents.contains(child)) {
					continue;
				}
				// Is there a component already named same as the one to be added ?
				// (In this case, do NOT add it, the redefinition override parent behaviour)
				FIBComponent overridingComponent = getSubComponentNamed(child.getName());
				if (overridingComponent == null) {
					/**
					 * Merging Policy: We append the children of the container to this.subComponents 1. If child has no index, we insert it
					 * after all subcomponents with a negative index 2. If child has a negative index, we insert before any subcomponent
					 * with a null or positive index, or a negative index that is equal or greater than the child index 3. If the child has
					 * a positive index, we insert after all subcomponents with a negative or null index, or a positive index which is
					 * smaller or equal to the child index
					 * 
					 * Moreover, when inserting, we always verify that we are not inserting ourselves in a consecutive series of indexed
					 * components. Finally, when we insert the child, we also insert all the consecutive indexed components (two components
					 * with a null index are considered to be consecutive)
					 */

					int indexInsertion;
					if (child.getIndex() == null) {
						indexInsertion = getSubComponents().size();
						for (int j = 0; j < getSubComponents().size(); j++) {
							FIBComponent c = getSubComponents().get(j);
							if (c.getIndex() == null || c.getIndex() > -1) {
								indexInsertion = j;
								break;
							}
						}
					} else if (child.getIndex() < 0) {
						indexInsertion = 0;
						for (int j = 0; j < getSubComponents().size(); j++) {
							FIBComponent c = getSubComponents().get(j);
							if (c.getIndex() == null || c.getIndex() >= child.getIndex()) {
								// We have found where to insert
								indexInsertion = j;
								if (c.getIndex() != null && c.getIndex() < 0 && j > 0) {
									// This is a complex case
									FIBComponent previousComponent = getSubComponents().get(j - 1);
									// If the component that is just before the insertion point has an index which is right before the
									// current
									// component one, then we need to skip all the consecutives indexed component.
									if (previousComponent.getIndex() != null && previousComponent.getIndex() + 1 == c.getIndex()) {
										int previous = c.getIndex();
										j++;
										while (j < getSubComponents().size()) {
											c = getSubComponents().get(j);
											if (c.getIndex() != null && c.getIndex() == previous + 1) {
												previous = c.getIndex();
												j++;
											} else {
												break;
											}
										}
										indexInsertion = j;
										break;
									}
								}
								break;
							}
						}
					} else {
						indexInsertion = getSubComponents().size();
						for (int j = 0; j < getSubComponents().size(); j++) {
							FIBComponent c = getSubComponents().get(j);
							if (c.getIndex() != null && c.getIndex() > -1 && c.getIndex() >= child.getIndex()) {
								indexInsertion = j;
								if (j > 0) {
									// This is a complex case
									FIBComponent previousComponent = getSubComponents().get(j - 1);
									// If the component that is just before the insertion point has an index which is right before the
									// current
									// component one, then we need to skip all the consecutives indexed component.
									if (previousComponent.getIndex() != null && previousComponent.getIndex() + 1 == c.getIndex()) {
										int previous = c.getIndex();
										j++;
										while (j < getSubComponents().size()) {
											c = getSubComponents().get(j);
											if (c.getIndex() != null && c.getIndex() == previous + 1) {
												previous = c.getIndex();
												j++;
											} else {
												break;
											}
										}
										indexInsertion = j;
										break;
									}
								}
								break;
							}
						}
					}
					boolean insert = true;
					Integer startIndex = child.getIndex();
					while (insert) {
						child.setParent(this);
						getSubComponents().add(indexInsertion, child);
						indexInsertion++;
						if (i + 1 < container.getSubComponents().size()) {
							Integer previousInteger = child.getIndex();
							child = container.getSubComponents().get(i + 1);
							insert = previousInteger == null
									&& child.getIndex() == null
									|| previousInteger != null
									&& child.getIndex() != null
									&& previousInteger + 1 == child.getIndex()
									|| child.getIndex() != null
									&& (startIndex == null && child.getIndex() == startIndex || startIndex != null
											&& startIndex.equals(child.getIndex())) && !mergedComponents.contains(child);
							if (insert) {
								i++;
								overridingComponent = getSubComponentNamed(child.getName());
								if (overridingComponent != null) {
									insert = false;
									if (overridingComponent.getParameter("hidden") != null
											&& overridingComponent.getParameter("hidden").equalsIgnoreCase("true")) {
										removeFromSubComponents(overridingComponent);
									}
								}
							} else {
								break;
							}
						} else {
							break;
						}

					}
				} else {
					if (overridingComponent.getParameter("hidden") != null
							&& overridingComponent.getParameter("hidden").equalsIgnoreCase("true")) {
						// Super property must be shadowed
						removeFromSubComponents(overridingComponent);
					}
				}
			}

			if (container.getLocalizedDictionary() != null) {
				retrieveFIBLocalizedDictionary().append(container.getLocalizedDictionary());
			}

			updateBindingModel();
			for (FIBComponent c : getSubComponents()) {
				recursivelyFinalizeDeserialization(c);
			}
			finalizeDeserialization();
		}

		private void recursivelyFinalizeDeserialization(FIBComponent c) {
			c.finalizeDeserialization();
			if (c instanceof FIBContainer) {
				for (FIBComponent c2 : ((FIBContainer) c).getSubComponents()) {
					recursivelyFinalizeDeserialization(c2);
				}
			}
		}

		// Default layout is built-in: only FIBPanel manage a custom layout,
		// where this method is overriden
		public Layout getLayout() {
			return null;
		}

		// Not permitted since default layout is built-in: only FIBPanel
		// manage a custom layout, where this method is overriden
		public void setLayout(Layout layout) {
		}

		public void componentFirst(FIBComponent c) {
			if (c == null) {
				return;
			}
			getSubComponents().remove(c);
			updateComponentIndexForInsertionIndex(c, 0);
			getSubComponents().add(0, c);
			notifyComponentIndexChanged(c);
		}

		public void componentUp(FIBComponent c) {
			if (c == null) {
				return;
			}
			int index = getSubComponents().indexOf(c);
			if (index > 0) {
				getSubComponents().remove(c);
				updateComponentIndexForInsertionIndex(c, index - 1);
				getSubComponents().add(index - 1, c);
				notifyComponentIndexChanged(c);
			}
		}

		public void componentDown(FIBComponent c) {
			if (c == null) {
				return;
			}
			int index = getSubComponents().indexOf(c);
			if (index < getSubComponents().size() - 1) {
				getSubComponents().remove(c);
			}
			updateComponentIndexForInsertionIndex(c, index + 1);
			getSubComponents().add(index + 1, c);
			notifyComponentIndexChanged(c);
		}

		public void componentLast(FIBComponent c) {
			if (c == null) {
				return;
			}
			getSubComponents().remove(c);
			updateComponentIndexForInsertionIndex(c, getSubComponents().size());
			getSubComponents().add(c);
			notifyComponentIndexChanged(c);
		}

		private void notifyComponentIndexChanged(FIBComponent component) {
			FIBPropertyNotification<ComponentConstraints> notification = new FIBPropertyNotification<ComponentConstraints>(
					(FIBProperty<ComponentConstraints>) FIBProperty.getFIBProperty(getClass(), CONSTRAINTS_KEY),
					component.getConstraints(), component.getConstraints());
			component.notify(notification);
			getPropertyChangeSupport().firePropertyChange(SUB_COMPONENTS_KEY, null, getSubComponents());
		}

		private void notifySubcomponentsIndexChanged() {
			getPropertyChangeSupport().firePropertyChange(SUB_COMPONENTS_KEY, null, getSubComponents());
		}

		@Override
		public void notifiedBindingModelRecreated() {
			super.notifiedBindingModelRecreated();
			for (FIBComponent c : getSubComponents()) {
				c.notifiedBindingModelRecreated();
			}
		}

		@Override
		public void reorderComponents() {
			// Rules to sort sub components
			// 1. Smallest negative index is placed first
			// 2. All unindexed components (index==null) are then placed
			// 3. Eventually, all the other components are placed after according to their defined index (0 is considered as a positive
			// index)
			Collections.sort(getSubComponents(), new Comparator<FIBComponent>() {

				@Override
				public int compare(FIBComponent o1, FIBComponent o2) {
					if (o1.getIndex() == null) {
						if (o2.getIndex() == null) {
							return 0;
						}
						if (o2.getIndex() < 0) {
							return 1;
						} else {
							return -1;
						}
					} else {
						if (o2.getIndex() == null) {
							return o1.getIndex();
						} else {
							return o1.getIndex() - o2.getIndex();
						}
					}
				}
			});
			notifySubcomponentsIndexChanged();
		}

	}
}
