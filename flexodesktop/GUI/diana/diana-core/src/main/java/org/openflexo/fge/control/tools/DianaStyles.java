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
package org.openflexo.fge.control.tools;

import java.util.logging.Logger;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.control.DianaInteractiveEditor;
import org.openflexo.fge.view.DianaViewFactory;
import org.openflexo.fge.view.widget.FIBBackgroundStyleSelector;
import org.openflexo.fge.view.widget.FIBForegroundStyleSelector;
import org.openflexo.fge.view.widget.FIBShadowStyleSelector;
import org.openflexo.fge.view.widget.FIBShapeSelector;
import org.openflexo.fge.view.widget.FIBTextStyleSelector;
import org.openflexo.swing.CustomPopup.ApplyCancelListener;

/**
 * Represents a toolbar allowing to edit some style properties
 * 
 * @author sylvain
 * 
 * @param <C>
 * @param <F>
 * @param <ME>
 */
public abstract class DianaStyles<C, F extends DianaViewFactory<F, ? super C>, ME> extends DianaToolImpl<C, F, ME> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DianaStyles.class.getPackage().getName());

	private FIBForegroundStyleSelector<?> foregroundSelector;
	private FIBBackgroundStyleSelector<?> backgroundSelector;
	private FIBTextStyleSelector<?> textStyleSelector;
	private FIBShadowStyleSelector<?> shadowStyleSelector;
	private FIBShapeSelector<?> shapeSelector;

	/**
	 * Return the technology-specific component representing the toolbar
	 * 
	 * @return
	 */
	public abstract C getComponent();

	@Override
	public DianaInteractiveEditor<?, F, ?> getEditor() {
		return (DianaInteractiveEditor<?, F, ?>) super.getEditor();
	}

	public void delete() {

		if (backgroundSelector != null) {
			backgroundSelector.delete();
			backgroundSelector = null;
		}
		if (foregroundSelector != null) {
			foregroundSelector.delete();
			foregroundSelector = null;
		}
		if (shadowStyleSelector != null) {
			shadowStyleSelector.delete();
			shadowStyleSelector = null;
		}
		if (shapeSelector != null) {
			shapeSelector.delete();
			shapeSelector = null;
		}
		if (textStyleSelector != null) {
			textStyleSelector.delete();
			textStyleSelector = null;
		}
	}

	public FIBForegroundStyleSelector<?> getForegroundSelector() {
		if (foregroundSelector == null) {
			foregroundSelector = getEditor().getDianaFactory().makeFIBForegroundStyleSelector(getEditor().getCurrentForegroundStyle());
			foregroundSelector.addApplyCancelListener(new ApplyCancelListener() {
				@Override
				public void fireApplyPerformed() {
					if (getSelection().size() > 0) {
						for (ShapeNode<?> shape : getSelectedShapes()) {
							shape.getGraphicalRepresentation().setForeground(foregroundSelector.getEditedObject().clone());
						}
						for (ConnectorNode<?> connector : getSelectedConnectors()) {
							connector.getGraphicalRepresentation().setForeground(foregroundSelector.getEditedObject().clone());
						}
					} else {
						getEditor().setCurrentForegroundStyle(foregroundSelector.getEditedObject().clone());
					}
				}

				@Override
				public void fireCancelPerformed() {
				}
			});
		}
		return foregroundSelector;
	}

	public FIBBackgroundStyleSelector<?> getBackgroundSelector() {
		if (backgroundSelector == null) {
			backgroundSelector = getEditor().getDianaFactory().makeFIBBackgroundStyleSelector(getEditor().getCurrentBackgroundStyle());
			backgroundSelector.addApplyCancelListener(new ApplyCancelListener() {
				@Override
				public void fireApplyPerformed() {
					if (getSelectedShapes().size() > 0) {
						for (ShapeNode<?> shape : getSelectedShapes()) {
							shape.getGraphicalRepresentation().setBackground(backgroundSelector.getEditedObject().clone());
						}
					} else {
						getEditor().setCurrentBackgroundStyle(backgroundSelector.getEditedObject().clone());
					}
				}

				@Override
				public void fireCancelPerformed() {
				}
			});
		}
		return backgroundSelector;
	}

	public FIBTextStyleSelector<?> getTextStyleSelector() {
		if (textStyleSelector == null) {
			textStyleSelector = getEditor().getDianaFactory().makeFIBTextStyleSelector(getEditor().getCurrentTextStyle());
			textStyleSelector.addApplyCancelListener(new ApplyCancelListener() {
				@Override
				public void fireApplyPerformed() {
					if (getSelection().size() > 0) {
						for (DrawingTreeNode<?, ?> gr : getSelection()) {
							gr.getGraphicalRepresentation().setTextStyle(textStyleSelector.getEditedObject().clone());
						}
					} else {
						getEditor().setCurrentTextStyle(textStyleSelector.getEditedObject().clone());
					}
				}

				@Override
				public void fireCancelPerformed() {
				}
			});
		}
		return textStyleSelector;
	}

	public FIBShadowStyleSelector<?> getShadowStyleSelector() {
		if (shadowStyleSelector == null) {
			shadowStyleSelector = getEditor().getDianaFactory().makeFIBShadowStyleSelector(getEditor().getCurrentShadowStyle());
			shadowStyleSelector.addApplyCancelListener(new ApplyCancelListener() {
				@Override
				public void fireApplyPerformed() {
					if (getSelectedShapes().size() > 0) {
						for (ShapeNode<?> shape : getSelectedShapes()) {
							shape.getGraphicalRepresentation().setShadowStyle(shadowStyleSelector.getEditedObject().clone());
						}
					} else {
						getEditor().setCurrentShadowStyle(shadowStyleSelector.getEditedObject().clone());
					}
				}

				@Override
				public void fireCancelPerformed() {
				}
			});
		}
		return shadowStyleSelector;
	}

	public FIBShapeSelector<?> getShapeSelector() {
		if (shapeSelector == null) {
			shapeSelector = getEditor().getDianaFactory().makeFIBShapeSelector(getEditor().getCurrentShape());
			shapeSelector.addApplyCancelListener(new ApplyCancelListener() {
				@Override
				public void fireApplyPerformed() {
					if (getSelectedShapes().size() > 0) {
						for (ShapeNode<?> shape : getSelectedShapes()) {
							shape.getGraphicalRepresentation().setShapeSpecification(shapeSelector.getEditedObject().clone());
						}

					} else {
						getEditor().setCurrentShape(shapeSelector.getEditedObject().clone());
					}
				}

				@Override
				public void fireCancelPerformed() {
				}
			});
		}
		return shapeSelector;
	}
}
