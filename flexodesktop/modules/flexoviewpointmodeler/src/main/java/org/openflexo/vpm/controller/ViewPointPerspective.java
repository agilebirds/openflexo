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
import org.openflexo.components.browser.view.BrowserView.SelectionPolicy;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.viewpoint.DiagramPalette;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.ExampleDiagram;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.icon.VPMIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;
import org.openflexo.vpm.drawingshema.CalcDrawingShemaController;
import org.openflexo.vpm.drawingshema.CalcDrawingShemaModuleView;
import org.openflexo.vpm.palette.CalcPaletteController;
import org.openflexo.vpm.palette.CalcPaletteModuleView;
import org.openflexo.vpm.view.CEDBrowserView;
import org.openflexo.vpm.view.CalcLibraryView;
import org.openflexo.vpm.view.CalcView;
import org.openflexo.vpm.view.EditionPatternView;
import org.openflexo.vpm.widget.FIBViewPointLibraryBrowser;

public class ViewPointPerspective extends FlexoPerspective {

	protected static final Logger logger = Logger.getLogger(ViewPointPerspective.class.getPackage().getName());

	private final VPMController _controller;

	// private final CalcLibraryBrowser _browser;
	private final CalcBrowser calcBrowser;
	private final CalcPaletteBrowser calcPaletteBrowser;
	// private final OntologyBrowser ontologyBrowser;
	private final CalcDrawingShemaBrowser calcDrawingShemaBrowser;

	// private final CEDBrowserView _browserView;
	private final CEDBrowserView calcBrowserView;
	private final CEDBrowserView calcPaletteBrowserView;
	// private final CEDBrowserView ontologyBrowserView;
	private final CEDBrowserView calcDrawingShemaBrowserView;

	private final JLabel infoLabel;

	private final JPanel EMPTY_RIGHT_VIEW = new JPanel();

	private FIBViewPointLibraryBrowser viewPointLibraryBrowser = null;

	/**
	 * @param controller
	 *            TODO
	 * @param name
	 */
	public ViewPointPerspective(VPMController controller) {
		super("calc_perspective");
		_controller = controller;

		viewPointLibraryBrowser = new FIBViewPointLibraryBrowser(controller.getViewPointLibrary(), controller);

		/*_browser = new CalcLibraryBrowser(controller);
		_browserView = new CEDBrowserView(_browser, _controller, SelectionPolicy.ParticipateToSelection) {
			@Override
			public void treeDoubleClick(FlexoModelObject object) {
				super.treeDoubleClick(object);
				if (object instanceof ViewPoint) {
					focusOnViewPoint((ViewPoint) object);
					// System.out.println(((OntologyCalc)object).getXMLRepresentation());
				}
			}
		};*/
		setTopLeftView(viewPointLibraryBrowser);

		/*System.out.println("Les rc:" + controller.getApplicationContext().getResourceCenterService());
		for (FlexoResourceCenter rc : controller.getApplicationContext().getResourceCenterService().getResourceCenters()) {
			System.out.println("> " + rc);
		}*/

		calcBrowser = new CalcBrowser(controller);
		calcBrowserView = new CEDBrowserView(calcBrowser, controller, SelectionPolicy.ForceSelection) {
			@Override
			public void treeDoubleClick(FlexoObject object) {
				super.treeDoubleClick(object);
				if (object instanceof DiagramPalette) {
					focusOnPalette((DiagramPalette) object);
				} /*else if (object instanceof OWLMetaModel) {
					focusOnOntology((OWLMetaModel) object);
					}*/else if (object instanceof EditionPattern) {
					hideBottomBrowser();
				}
			}

			@Override
			public void treeSingleClick(FlexoObject object) {
				super.treeSingleClick(object);
				if (!(object instanceof DiagramPalette) /*&& !(object instanceof OWLMetaModel)*/) {
					hideBottomBrowser();
				}
			}
		};

		calcPaletteBrowser = new CalcPaletteBrowser(controller);
		calcPaletteBrowserView = new CEDBrowserView(calcPaletteBrowser, controller, SelectionPolicy.ForceSelection);

		calcDrawingShemaBrowser = new CalcDrawingShemaBrowser(controller);
		calcDrawingShemaBrowserView = new CEDBrowserView(calcDrawingShemaBrowser, controller, SelectionPolicy.ForceSelection);

		/*ontologyBrowser = new OntologyBrowser(controller);
		ontologyBrowserView = new CEDBrowserView(ontologyBrowser, controller, SelectionPolicy.ForceSelection);
		setTopLeftView(_browserView);
		setMiddleLeftView(calcBrowserView);*/
		infoLabel = new JLabel("ViewPoint perspective");
		infoLabel.setFont(FlexoCst.SMALL_FONT);
		setFooter(infoLabel);
	}

	public ModuleView<?> getCurrentModuleView() {
		return _controller.getCurrentModuleView();
	}

	public void focusOnViewPoint(ViewPoint viewPoint) {
		logger.info("focusOnViewPoint " + viewPoint);
		setBottomLeftView(null);
		// calcBrowser.deleteBrowserListener(_browserView);
		calcBrowser.setRepresentedObject(viewPoint);
		calcBrowser.update();
		// calcBrowser.addBrowserListener(_browserView);
	}

	public void focusOnPalette(DiagramPalette palette) {
		setBottomLeftView(calcPaletteBrowserView);
		// calcPaletteBrowser.deleteBrowserListener(_browserView);
		calcPaletteBrowser.setRepresentedPalette(palette);
		calcPaletteBrowser.update();
		// calcPaletteBrowser.addBrowserListener(_browserView);
	}

	public void focusOnExampleDiagram(ExampleDiagram shema) {
		setBottomLeftView(calcDrawingShemaBrowserView);
		// calcDrawingShemaBrowser.deleteBrowserListener(_browserView);
		calcDrawingShemaBrowser.setRepresentedShema(shema);
		calcDrawingShemaBrowser.update();
		// calcDrawingShemaBrowser.addBrowserListener(_browserView);
	}

	/*public void focusOnOntology(OWLMetaModel ontology) {
		setBottomLeftView(ontologyBrowserView);
		ontologyBrowser.deleteBrowserListener(_browserView);
		ontologyBrowser.setRepresentedOntology(ontology);
		ontologyBrowser.update();
		ontologyBrowser.addBrowserListener(_browserView);
	}*/

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
		return object instanceof ViewPointLibrary /*|| object instanceof ImportedOWLOntology*/|| object instanceof DiagramPalette
				|| object instanceof ExampleDiagram || object instanceof ViewPoint || object instanceof EditionPattern;
	}

	@Override
	public ModuleView<? extends FlexoObject> createModuleViewForObject(FlexoObject object, FlexoController controller) {
		if (object instanceof ViewPointLibrary) {
			return new CalcLibraryView((ViewPointLibrary) object, (VPMController) controller);
		}
		/*if (object instanceof ImportedOWLOntology) {
			((ImportedOWLOntology) object).loadWhenUnloaded();
			return new OntologyView((ImportedOWLOntology) object, (VPMController) controller, this);
		}*/
		if (object instanceof ViewPoint) {
			return new CalcView((ViewPoint) object, (VPMController) controller);
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

}
