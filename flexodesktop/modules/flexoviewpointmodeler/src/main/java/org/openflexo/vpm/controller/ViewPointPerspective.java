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
package org.openflexo.vpm.controller;

import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openflexo.FlexoCst;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPalette;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPaletteObject;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagram;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramObject;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.icon.VPMIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;
import org.openflexo.vpm.drawingshema.CalcDrawingShemaController;
import org.openflexo.vpm.drawingshema.CalcDrawingShemaModuleView;
import org.openflexo.vpm.palette.CalcPaletteController;
import org.openflexo.vpm.palette.CalcPaletteModuleView;
import org.openflexo.vpm.view.CalcLibraryView;
import org.openflexo.vpm.view.EditionPatternView;
import org.openflexo.vpm.view.ViewPointView;
import org.openflexo.vpm.widget.FIBDiagramPaletteBrowser;
import org.openflexo.vpm.widget.FIBExampleDiagramBrowser;
import org.openflexo.vpm.widget.FIBViewPointBrowser;
import org.openflexo.vpm.widget.FIBViewPointLibraryBrowser;
import org.openflexo.vpm.widget.FIBVirtualModelBrowser;

public class ViewPointPerspective extends FlexoPerspective {

	protected static final Logger logger = Logger.getLogger(ViewPointPerspective.class.getPackage().getName());

	private final VPMController _controller;

	private final JLabel infoLabel;

	private final JPanel EMPTY_RIGHT_VIEW = new JPanel();

	private FIBViewPointLibraryBrowser viewPointLibraryBrowser = null;
	private FIBViewPointBrowser viewPointBrowser = null;
	private FIBVirtualModelBrowser virtualModelBrowser = null;
	private FIBExampleDiagramBrowser exampleDiagramBrowser = null;
	private FIBDiagramPaletteBrowser diagramPaletteBrowser = null;

	/**
	 * @param controller
	 *            TODO
	 * @param name
	 */
	public ViewPointPerspective(VPMController controller) {
		super("viewpoint_perspective");
		_controller = controller;

		viewPointLibraryBrowser = new FIBViewPointLibraryBrowser(controller.getViewPointLibrary(), controller);

		viewPointBrowser = new FIBViewPointBrowser(null, controller);
		virtualModelBrowser = new FIBVirtualModelBrowser(null, controller);
		exampleDiagramBrowser = new FIBExampleDiagramBrowser(null, controller);
		diagramPaletteBrowser = new FIBDiagramPaletteBrowser(null, controller);

		setTopLeftView(viewPointLibraryBrowser);

		infoLabel = new JLabel("ViewPoint perspective");
		infoLabel.setFont(FlexoCst.SMALL_FONT);
		setFooter(infoLabel);
	}

	public ModuleView<?> getCurrentModuleView() {
		return _controller.getCurrentModuleView();
	}

	public void focusOnViewPoint(ViewPoint viewPoint) {
		logger.info("focusOnViewPoint " + viewPoint);

		viewPointBrowser.setRootObject(viewPoint);
		setBottomLeftView(viewPointBrowser);
	}

	public void focusOnVirtualModel(VirtualModel<?> virtualModel) {
		logger.info("focusOnVirtualModel " + virtualModel);

		virtualModelBrowser.setRootObject(virtualModel);
		setBottomLeftView(virtualModelBrowser);
	}

	public void focusOnPalette(DiagramPalette palette) {
		logger.info("focusOnPalette " + palette);
		diagramPaletteBrowser.setRootObject(palette);
		setBottomLeftView(diagramPaletteBrowser);
	}

	public void focusOnExampleDiagram(ExampleDiagram exampleDiagram) {
		logger.info("focusOnExampleDiagram " + exampleDiagram);
		exampleDiagramBrowser.setRootObject(exampleDiagram);
		setBottomLeftView(exampleDiagramBrowser);
	}

	public void hideBottomBrowser() {
		setBottomLeftView(null);
	}

	/**
	 * Overrides getIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon() {
		return VPMIconLibrary.VPM_VPE_ACTIVE_ICON;
	}

	/**
	 * Overrides getSelectedIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getSelectedIcon()
	 */
	@Override
	public ImageIcon getSelectedIcon() {
		return VPMIconLibrary.VPM_VPE_SELECTED_ICON;
	}

	@Override
	public FlexoObject getDefaultObject(FlexoObject proposedObject) {
		if (hasModuleViewForObject(proposedObject)) {
			return proposedObject;
		}
		return _controller.getCalcLibrary();
	}

	@Override
	public boolean hasModuleViewForObject(FlexoObject object) {
		return object instanceof ViewPointLibrary || object instanceof DiagramPalette || object instanceof ExampleDiagram
				|| object instanceof ViewPoint || object instanceof EditionPattern;
	}

	@Override
	public ModuleView<? extends FlexoObject> createModuleViewForObject(FlexoObject object, FlexoController controller) {
		if (object instanceof ViewPointLibrary) {
			return new CalcLibraryView((ViewPointLibrary) object, (VPMController) controller);
		}
		if (object instanceof ViewPoint) {
			return new ViewPointView((ViewPoint) object, (VPMController) controller);
		}
		if (object instanceof EditionPattern) {
			return new EditionPatternView((EditionPattern) object, (VPMController) controller);
		}
		if (object instanceof DiagramPalette) {
			return new CalcPaletteController(_controller, (DiagramPalette) object, false).getModuleView();
		}
		if (object instanceof ExampleDiagram) {
			return new CalcDrawingShemaController(_controller, (ExampleDiagram) object, false).getModuleView();
		}
		return null;
	}

	@Override
	public JComponent getTopRightView() {
		if (getCurrentModuleView() instanceof CalcPaletteModuleView) {
			return ((CalcPaletteModuleView) getCurrentModuleView()).getController().getPaletteView();
		} else if (getCurrentModuleView() instanceof CalcDrawingShemaModuleView) {
			return ((CalcDrawingShemaModuleView) getCurrentModuleView()).getController().getPaletteView();
		}
		return EMPTY_RIGHT_VIEW;
	}

	public String getWindowTitleforObject(FlexoObject object) {
		if (object instanceof ViewPointLibrary) {
			return FlexoLocalization.localizedForKey("view_point_library");
		}
		if (object instanceof ViewPoint) {
			return ((ViewPoint) object).getName();
		}
		if (object instanceof DiagramPalette) {
			return ((DiagramPalette) object).getName() + " (" + FlexoLocalization.localizedForKey("palette") + ")";
		}
		if (object instanceof ExampleDiagram) {
			return ((ExampleDiagram) object).getName() + " (" + FlexoLocalization.localizedForKey("example_diagram") + ")";
		}
		if (object instanceof ViewPoint) {
			return ((ViewPoint) object).getName();
		}
		if (object instanceof EditionPattern) {
			return ((EditionPattern) object).getName();
		}
		if (object != null) {
			return object.getFullyQualifiedName();
		}
		return "null";
	}

	@Override
	public void objectWasClicked(Object object) {
		logger.info("ViewPointPerspective: object was clicked: " + object);
		if (object == null) {
			return;
		}

		if (getBottomLeftView() == viewPointBrowser) {
			if (!(object instanceof ViewPointObject) || (object instanceof DiagramPaletteObject)
					|| (object instanceof ExampleDiagramObject)) {
				setBottomLeftView(null);
			} else {
				ViewPointObject o = (ViewPointObject) object;
				if (o.getViewPoint() != viewPointBrowser.getRootObject()) {
					setBottomLeftView(null);
				}
			}
		}

		else if (getBottomLeftView() == exampleDiagramBrowser) {
			if (!(object instanceof ViewPointObject)) {
				setBottomLeftView(null);
			} else {
				ViewPointObject o = (ViewPointObject) object;
				if (o instanceof ExampleDiagramObject) {
					if (((ExampleDiagramObject) o).getExampleDiagram() != exampleDiagramBrowser.getRootObject()) {
						setBottomLeftView(null);
					}
				} else {
					setBottomLeftView(null);
				}
			}
		}

		else if (getBottomLeftView() == diagramPaletteBrowser) {

			if (!(object instanceof ViewPointObject)) {
				setBottomLeftView(null);
			} else {
				ViewPointObject o = (ViewPointObject) object;
				if (o instanceof DiagramPaletteObject) {
					if (((DiagramPaletteObject) o).getPalette() != diagramPaletteBrowser.getRootObject()) {
						setBottomLeftView(null);
					}
				} else {
					setBottomLeftView(null);
				}
			}
		}

		if (getBottomLeftView() == null) {
			if (object instanceof ViewPointObject) {
				if (object instanceof DiagramPaletteObject) {
					diagramPaletteBrowser.setRootObject(((DiagramPaletteObject) object).getPalette());
					setBottomLeftView(diagramPaletteBrowser);
				} else if (object instanceof ExampleDiagramObject) {
					exampleDiagramBrowser.setRootObject(((ExampleDiagramObject) object).getExampleDiagram());
					setBottomLeftView(exampleDiagramBrowser);
				} else {
					viewPointBrowser.setRootObject(((ViewPointObject) object).getViewPoint());
					setBottomLeftView(viewPointBrowser);
				}
			}
		}
	}

	@Override
	public void objectWasRightClicked(Object object) {
		logger.info("ViewPointPerspective: object was right-clicked: " + object);
	}

	@Override
	public void objectWasDoubleClicked(Object object) {
		logger.info("ViewPointPerspective: object was double-clicked: " + object);
	}

}
