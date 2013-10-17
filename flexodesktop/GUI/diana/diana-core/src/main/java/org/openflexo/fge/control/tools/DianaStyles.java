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

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.FGECoreUtils;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.control.DianaInteractiveEditor;
import org.openflexo.fge.shapes.ShapeSpecification;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.fge.view.DianaViewFactory;
import org.openflexo.fge.view.widget.FIBBackgroundStyleSelector;
import org.openflexo.fge.view.widget.FIBBackgroundStyleSelector.BackgroundStyleFactory;
import org.openflexo.fge.view.widget.FIBForegroundStyleSelector;
import org.openflexo.fge.view.widget.FIBShadowStyleSelector;
import org.openflexo.fge.view.widget.FIBShapeSelector;
import org.openflexo.fge.view.widget.FIBShapeSelector.ShapeFactory;
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
public abstract class DianaStyles<C, F extends DianaViewFactory<F, ? super C>> extends DianaToolImpl<C, F> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DianaStyles.class.getPackage().getName());

	private FIBForegroundStyleSelector<?> foregroundSelector;
	private FIBBackgroundStyleSelector<?> backgroundSelector;
	private FIBTextStyleSelector<?> textStyleSelector;
	private FIBShadowStyleSelector<?> shadowStyleSelector;
	private FIBShapeSelector<?> shapeSelector;

	private ForegroundStyle defaultForegroundStyle;
	private BackgroundStyle defaultBackgroundStyle;
	private TextStyle defaultTextStyle;
	private ShadowStyle defaultShadowStyle;
	private ShapeSpecification defaultShape;

	protected BackgroundStyleFactory bsFactory;
	protected ShapeFactory shapeFactory;

	public DianaStyles() {
		defaultForegroundStyle = FGECoreUtils.TOOLS_FACTORY.makeDefaultForegroundStyle();
		defaultBackgroundStyle = FGECoreUtils.TOOLS_FACTORY.makeColoredBackground(FGEConstants.DEFAULT_BACKGROUND_COLOR);
		defaultTextStyle = FGECoreUtils.TOOLS_FACTORY.makeDefaultTextStyle();
		defaultShadowStyle = FGECoreUtils.TOOLS_FACTORY.makeDefaultShadowStyle();
		defaultShape = FGECoreUtils.TOOLS_FACTORY.makeShape(ShapeType.RECTANGLE);
	}

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
			foregroundSelector = getDianaFactory().makeFIBForegroundStyleSelector(getCurrentForegroundStyle());
			foregroundSelector.addApplyCancelListener(new ApplyCancelListener() {
				@Override
				public void fireApplyPerformed() {
					if (getSelection().size() > 0) {
						for (ShapeNode<?> shape : getSelectedShapes()) {
							shape.getGraphicalRepresentation()
									.setForeground((ForegroundStyle) foregroundSelector.getEditedObject().clone());
						}
						for (ConnectorNode<?> connector : getSelectedConnectors()) {
							connector.getGraphicalRepresentation().setForeground(
									(ForegroundStyle) foregroundSelector.getEditedObject().clone());
						}
					} else {
						getEditor().setCurrentForegroundStyle((ForegroundStyle) foregroundSelector.getEditedObject().clone());
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
			bsFactory = new BackgroundStyleFactory(getCurrentBackgroundStyle());
			backgroundSelector = getDianaFactory().makeFIBBackgroundStyleSelector(bsFactory);
			backgroundSelector.addApplyCancelListener(new ApplyCancelListener() {
				@Override
				public void fireApplyPerformed() {
					if (getSelectedShapes().size() > 0) {
						for (ShapeNode<?> shape : getSelectedShapes()) {
							shape.getGraphicalRepresentation()
									.setBackground((BackgroundStyle) backgroundSelector.getEditedObject().clone());
						}
					} else {
						getEditor().setCurrentBackgroundStyle((BackgroundStyle) backgroundSelector.getEditedObject().clone());
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
			textStyleSelector = getDianaFactory().makeFIBTextStyleSelector(getCurrentTextStyle());
			textStyleSelector.addApplyCancelListener(new ApplyCancelListener() {
				@Override
				public void fireApplyPerformed() {
					if (getSelection().size() > 0) {
						for (DrawingTreeNode<?, ?> gr : getSelection()) {
							gr.getGraphicalRepresentation().setTextStyle((TextStyle) textStyleSelector.getEditedObject().clone());
						}
					} else {
						getEditor().setCurrentTextStyle((TextStyle) textStyleSelector.getEditedObject().clone());
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
			shadowStyleSelector = getDianaFactory().makeFIBShadowStyleSelector(getCurrentShadowStyle());
			shadowStyleSelector.addApplyCancelListener(new ApplyCancelListener() {
				@Override
				public void fireApplyPerformed() {
					if (getSelectedShapes().size() > 0) {
						for (ShapeNode<?> shape : getSelectedShapes()) {
							shape.getGraphicalRepresentation().setShadowStyle((ShadowStyle) shadowStyleSelector.getEditedObject().clone());
						}
					} else {
						getEditor().setCurrentShadowStyle((ShadowStyle) shadowStyleSelector.getEditedObject().clone());
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
			shapeFactory = new ShapeFactory(getCurrentShape());
			shapeSelector = getDianaFactory().makeFIBShapeSelector(shapeFactory);
			shapeSelector.addApplyCancelListener(new ApplyCancelListener() {
				@Override
				public void fireApplyPerformed() {
					if (getSelectedShapes().size() > 0) {
						for (ShapeNode<?> shape : getSelectedShapes()) {
							shape.getGraphicalRepresentation().setShapeSpecification(
									(ShapeSpecification) shapeSelector.getEditedObject().clone());
						}

					} else {
						getEditor().setCurrentShape((ShapeSpecification) shapeSelector.getEditedObject().clone());
					}
				}

				@Override
				public void fireCancelPerformed() {
				}
			});
		}
		return shapeSelector;
	}

	public ForegroundStyle getCurrentForegroundStyle() {
		if (getEditor() != null) {
			return getEditor().getCurrentForegroundStyle();
		}
		return defaultForegroundStyle;
	}

	public BackgroundStyle getCurrentBackgroundStyle() {
		if (getEditor() != null) {
			return getEditor().getCurrentBackgroundStyle();
		}
		return defaultBackgroundStyle;
	}

	public TextStyle getCurrentTextStyle() {
		if (getEditor() != null) {
			return getEditor().getCurrentTextStyle();
		}
		return defaultTextStyle;
	}

	public ShadowStyle getCurrentShadowStyle() {
		if (getEditor() != null) {
			return getEditor().getCurrentShadowStyle();
		}
		return defaultShadowStyle;
	}

	public ShapeSpecification getCurrentShape() {
		if (getEditor() != null) {
			return getEditor().getCurrentShape();
		}
		return defaultShape;
	}
}
