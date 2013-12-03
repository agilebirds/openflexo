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
package org.openflexo.fib.editor.view.container;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.fib.editor.controller.FIBEditorController;
import org.openflexo.fib.editor.view.FIBEditableView;
import org.openflexo.fib.editor.view.FIBEditableViewDelegate;
import org.openflexo.fib.editor.view.FIBEditableViewDelegate.FIBDropTarget;
import org.openflexo.fib.editor.view.PlaceHolder;
import org.openflexo.fib.model.BorderLayoutConstraints;
import org.openflexo.fib.model.BorderLayoutConstraints.BorderLayoutLocation;
import org.openflexo.fib.model.BoxLayoutConstraints;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBModelObject;
import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.fib.model.FIBTab;
import org.openflexo.fib.model.FlowLayoutConstraints;
import org.openflexo.fib.model.GridBagLayoutConstraints;
import org.openflexo.fib.model.GridLayoutConstraints;
import org.openflexo.fib.model.TwoColsLayoutConstraints;
import org.openflexo.fib.model.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.fib.view.container.FIBTabView;
import org.openflexo.logging.FlexoLogger;

public class FIBEditableTabView<T> extends FIBTabView<FIBTab, T> implements FIBEditableView<FIBTab, JPanel> {

	private static final Logger logger = FlexoLogger.getLogger(FIBEditableTabView.class.getPackage().getName());

	private final FIBEditableViewDelegate<FIBTab, JPanel> delegate;

	private Vector<PlaceHolder> placeholders;

	private final FIBEditorController editorController;

	@Override
	public FIBEditorController getEditorController() {
		return editorController;
	}

	public FIBEditableTabView(final FIBTab model, FIBEditorController editorController) {
		super(model, editorController.getController());
		this.editorController = editorController;

		delegate = new FIBEditableViewDelegate<FIBTab, JPanel>(this);

		model.getPropertyChangeSupport().addPropertyChangeListener(this);
	}

	@Override
	public void delete() {
		getComponent().getPropertyChangeSupport().removePropertyChangeListener(this);
		if (placeholders != null) {
			placeholders.clear();
		}
		placeholders = null;
		delegate.delete();
		super.delete();
	}

	@Override
	protected void retrieveContainedJComponentsAndConstraints() {
		if (placeholders == null) {
			placeholders = new Vector<PlaceHolder>();
		}
		placeholders.removeAllElements();

		super.retrieveContainedJComponentsAndConstraints();

		if (!getComponent().getProtectContent()) {

			// FlowLayout
			if (getComponent().getLayout() == Layout.flow || getComponent().getLayout() == Layout.buttons) {
				final FlowLayoutConstraints beginPlaceHolderConstraints = new FlowLayoutConstraints();
				PlaceHolder beginPlaceHolder = new PlaceHolder(this, "<begin>") {
					@Override
					public void insertComponent(FIBComponent newComponent) {
						FIBEditableTabView.this.getComponent().addToSubComponents(newComponent, beginPlaceHolderConstraints, 0);
					}
				};
				registerComponentWithConstraints(beginPlaceHolder, beginPlaceHolderConstraints, 0);
				placeholders.add(beginPlaceHolder);
				beginPlaceHolder.setVisible(false);
				final FlowLayoutConstraints endPlaceHolderConstraints = new FlowLayoutConstraints();
				PlaceHolder endPlaceHolder = new PlaceHolder(this, "<end>") {
					@Override
					public void insertComponent(FIBComponent newComponent) {
						FIBEditableTabView.this.getComponent().addToSubComponents(newComponent, endPlaceHolderConstraints);
					}
				};
				registerComponentWithConstraints(endPlaceHolder, endPlaceHolderConstraints);
				placeholders.add(endPlaceHolder);
				endPlaceHolder.setVisible(false);
			}

			// BoxLayout

			if (getComponent().getLayout() == Layout.box) {
				final BoxLayoutConstraints beginPlaceHolderConstraints = new BoxLayoutConstraints();
				PlaceHolder beginPlaceHolder = new PlaceHolder(this, "<begin>") {
					@Override
					public void insertComponent(FIBComponent newComponent) {
						FIBEditableTabView.this.getComponent().addToSubComponents(newComponent, beginPlaceHolderConstraints, 0);
					}
				};
				registerComponentWithConstraints(beginPlaceHolder, beginPlaceHolderConstraints, 0);
				placeholders.add(beginPlaceHolder);
				beginPlaceHolder.setVisible(false);
				final BoxLayoutConstraints endPlaceHolderConstraints = new BoxLayoutConstraints();
				PlaceHolder endPlaceHolder = new PlaceHolder(this, "<end>") {
					@Override
					public void insertComponent(FIBComponent newComponent) {
						FIBEditableTabView.this.getComponent().addToSubComponents(newComponent, endPlaceHolderConstraints);
					}
				};
				registerComponentWithConstraints(endPlaceHolder);
				placeholders.add(endPlaceHolder);
				endPlaceHolder.setVisible(false);
			}

			// BorderLayout
			if (getComponent().getLayout() == Layout.border) {
				BorderLayout bl = (BorderLayout) getJComponent().getLayout();
				BorderLayoutLocation[] placeholderLocations = { BorderLayoutLocation.north, BorderLayoutLocation.south,
						BorderLayoutLocation.center, BorderLayoutLocation.east, BorderLayoutLocation.west };
				for (final BorderLayoutLocation l : placeholderLocations) {
					boolean found = false;
					for (FIBComponent subComponent : getComponent().getSubComponents()) {
						BorderLayoutConstraints blc = (BorderLayoutConstraints) subComponent.getConstraints();
						if (blc.getLocation() == l) {
							found = true;
						}
					}
					if (!found) {
						PlaceHolder newPlaceHolder = new PlaceHolder(this, "<" + l.getConstraint() + ">") {
							@Override
							public void insertComponent(FIBComponent newComponent) {
								BorderLayoutConstraints blConstraints = new BorderLayoutConstraints(l);
								newComponent.setConstraints(blConstraints);
								FIBEditableTabView.this.getComponent().addToSubComponents(newComponent);
							}
						};
						registerComponentWithConstraints(newPlaceHolder, l.getConstraint());
						newPlaceHolder.setVisible(false);
						placeholders.add(newPlaceHolder);
						logger.fine("Made placeholder for " + l.getConstraint());
					}
				}
			}

			// TwoColsLayout

			if (getComponent().getLayout() == Layout.twocols) {
				final TwoColsLayoutConstraints beginCenterPlaceHolderConstraints = new TwoColsLayoutConstraints(
						TwoColsLayoutLocation.center, true, false);
				PlaceHolder beginCenterPlaceHolder = new PlaceHolder(this, "<center>") {
					@Override
					public void insertComponent(FIBComponent newComponent) {
						FIBEditableTabView.this.getComponent().addToSubComponents(newComponent, beginCenterPlaceHolderConstraints, 0);
					}
				};
				registerComponentWithConstraints(beginCenterPlaceHolder, beginCenterPlaceHolderConstraints, 0);
				placeholders.add(beginCenterPlaceHolder);
				beginCenterPlaceHolder.setVisible(false);

				final TwoColsLayoutConstraints beginLeftPlaceHolderConstraints = new TwoColsLayoutConstraints(TwoColsLayoutLocation.left,
						true, false);
				final TwoColsLayoutConstraints beginRightPlaceHolderConstraints = new TwoColsLayoutConstraints(TwoColsLayoutLocation.right,
						true, false);

				PlaceHolder beginRightPlaceHolder = new PlaceHolder(this, "<right>") {
					@Override
					public void insertComponent(FIBComponent newComponent) {
						FIBEditableTabView.this.getComponent().addToSubComponents(newComponent, beginRightPlaceHolderConstraints);
						FIBEditableTabView.this.getComponent().addToSubComponents(new FIBLabel("<left>"), beginLeftPlaceHolderConstraints);
					}
				};
				registerComponentWithConstraints(beginRightPlaceHolder, beginRightPlaceHolderConstraints, 0);
				placeholders.add(beginRightPlaceHolder);
				beginRightPlaceHolder.setVisible(false);

				PlaceHolder beginLeftPlaceHolder = new PlaceHolder(this, "<left>") {
					@Override
					public void insertComponent(FIBComponent newComponent) {
						FIBEditableTabView.this.getComponent().addToSubComponents(newComponent, beginLeftPlaceHolderConstraints, 0);
						FIBEditableTabView.this.getComponent().addToSubComponents(new FIBLabel("<right>"),
								beginRightPlaceHolderConstraints, 0);
					}
				};
				registerComponentWithConstraints(beginLeftPlaceHolder, beginLeftPlaceHolderConstraints, 0);
				placeholders.add(beginLeftPlaceHolder);
				beginLeftPlaceHolder.setVisible(false);

				final TwoColsLayoutConstraints endCenterPlaceHolderConstraints = new TwoColsLayoutConstraints(TwoColsLayoutLocation.center,
						true, false);
				PlaceHolder endCenterPlaceHolder = new PlaceHolder(this, "<center>") {
					@Override
					public void insertComponent(FIBComponent newComponent) {
						FIBEditableTabView.this.getComponent().addToSubComponents(newComponent, endCenterPlaceHolderConstraints);
					}
				};
				registerComponentWithConstraints(endCenterPlaceHolder, endCenterPlaceHolderConstraints);
				placeholders.add(endCenterPlaceHolder);
				endCenterPlaceHolder.setVisible(false);

				final TwoColsLayoutConstraints endLeftPlaceHolderConstraints = new TwoColsLayoutConstraints(TwoColsLayoutLocation.left,
						true, false);
				final TwoColsLayoutConstraints endRightPlaceHolderConstraints = new TwoColsLayoutConstraints(TwoColsLayoutLocation.right,
						true, false);
				PlaceHolder endLeftPlaceHolder = new PlaceHolder(this, "<left>") {
					@Override
					public void insertComponent(FIBComponent newComponent) {
						FIBEditableTabView.this.getComponent().addToSubComponents(newComponent, endLeftPlaceHolderConstraints);
						FIBEditableTabView.this.getComponent().addToSubComponents(new FIBLabel("<right>"), endRightPlaceHolderConstraints);
					}
				};
				registerComponentWithConstraints(endLeftPlaceHolder, endLeftPlaceHolderConstraints);
				placeholders.add(endLeftPlaceHolder);
				endLeftPlaceHolder.setVisible(false);

				PlaceHolder endRightPlaceHolder = new PlaceHolder(this, "<right>") {
					@Override
					public void insertComponent(FIBComponent newComponent) {
						FIBEditableTabView.this.getComponent().addToSubComponents(new FIBLabel("<left>"), endLeftPlaceHolderConstraints);
						FIBEditableTabView.this.getComponent().addToSubComponents(newComponent, endRightPlaceHolderConstraints);
					}
				};
				registerComponentWithConstraints(endRightPlaceHolder, endRightPlaceHolderConstraints);
				placeholders.add(endRightPlaceHolder);
				endRightPlaceHolder.setVisible(false);

			}

			// GridBagLayout

			if (getComponent().getLayout() == Layout.gridbag) {
				final GridBagLayoutConstraints beginPlaceHolderConstraints = new GridBagLayoutConstraints();
				PlaceHolder beginPlaceHolder = new PlaceHolder(this, "<begin>") {
					@Override
					public void insertComponent(FIBComponent newComponent) {
						FIBEditableTabView.this.getComponent().addToSubComponents(newComponent, beginPlaceHolderConstraints, 0);
					}
				};
				registerComponentWithConstraints(beginPlaceHolder, beginPlaceHolderConstraints, 0);
				placeholders.add(beginPlaceHolder);
				beginPlaceHolder.setVisible(false);
				final GridBagLayoutConstraints endPlaceHolderConstraints = new GridBagLayoutConstraints();
				PlaceHolder endPlaceHolder = new PlaceHolder(this, "<end>") {
					@Override
					public void insertComponent(FIBComponent newComponent) {
						FIBEditableTabView.this.getComponent().addToSubComponents(newComponent, endPlaceHolderConstraints);
					}
				};
				registerComponentWithConstraints(endPlaceHolder);
				placeholders.add(endPlaceHolder);
				endPlaceHolder.setVisible(false);
			}

			// logger.info("******** Set DropTargets");
			if (getEditorController() != null) {
				for (PlaceHolder ph : placeholders) {
					logger.fine("Set DropTarget for " + ph);
					// Put right drop target
					new FIBDropTarget(ph);
				}
			}
			/*else {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						updateLayout();
					}
				});
			}*/
		}
	}

	// Special case for GridLayout
	@Override
	protected JComponent _getJComponent(final int col, final int row) {
		for (FIBComponent subComponent : getComponent().getSubComponents()) {
			GridLayoutConstraints glc = (GridLayoutConstraints) subComponent.getConstraints();
			if (glc.getX() == col && glc.getY() == row) {
				return getController().viewForComponent(subComponent).getResultingJComponent();
			}
		}

		if (!getComponent().getProtectContent()) {
			// Otherwise, it's a PlaceHolder
			PlaceHolder newPlaceHolder = new PlaceHolder(this, "<" + col + "," + row + ">") {
				@Override
				public void insertComponent(FIBComponent newComponent) {
					GridLayoutConstraints glConstraints = new GridLayoutConstraints(col, row);
					newComponent.setConstraints(glConstraints);
					FIBEditableTabView.this.getComponent().addToSubComponents(newComponent);
				}
			};
			newPlaceHolder.setVisible(false);
			placeholders.add(newPlaceHolder);

			return newPlaceHolder;
		} else {
			// Otherwise, it's an empty cell
			return new JPanel();
		}
	}

	@Override
	public Vector<PlaceHolder> getPlaceHolders() {
		return placeholders;
	}

	@Override
	public FIBEditableViewDelegate<FIBTab, JPanel> getDelegate() {
		return delegate;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		delegate.receivedModelNotifications((FIBModelObject) evt.getSource(), evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
	}

}
