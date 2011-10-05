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

import java.util.Hashtable;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import org.openflexo.FlexoCst;
import org.openflexo.components.browser.view.BrowserView.SelectionPolicy;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ontology.ImportedOntology;
import org.openflexo.foundation.viewpoint.ExampleDrawingShema;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.viewpoint.ViewPointPalette;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.icon.VPMIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.selection.SelectionManagingDrawingController;
import org.openflexo.utils.FlexoSplitPaneLocationSaver;
import org.openflexo.view.EmptyPanel;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.vpm.CEDCst;
import org.openflexo.vpm.drawingshema.CalcDrawingShemaController;
import org.openflexo.vpm.drawingshema.CalcDrawingShemaModuleView;
import org.openflexo.vpm.palette.CalcPaletteController;
import org.openflexo.vpm.palette.CalcPaletteModuleView;
import org.openflexo.vpm.view.CEDBrowserView;
import org.openflexo.vpm.view.CalcLibraryView;
import org.openflexo.vpm.view.CalcView;
import org.openflexo.vpm.view.EditionPatternView;
import org.openflexo.vpm.view.OntologyView;

public class ViewPointPerspective extends FlexoPerspective<FlexoModelObject>
{

	protected static final Logger logger = Logger.getLogger(ViewPointPerspective.class.getPackage().getName());

	private final CEDController _controller;
	
	private final CalcLibraryBrowser _browser;
	private final CalcBrowser calcBrowser;
	private final CalcPaletteBrowser calcPaletteBrowser;
	private final OntologyBrowser ontologyBrowser;
	private final CalcDrawingShemaBrowser calcDrawingShemaBrowser;
	
	private final CEDBrowserView _browserView;
	private final CEDBrowserView calcBrowserView;
	private final CEDBrowserView calcPaletteBrowserView;
	private final CEDBrowserView ontologyBrowserView;
	private final CEDBrowserView calcDrawingShemaBrowserView;
	
	private final JSplitPane splitPane;
	private final JSplitPane splitPane2;
	
	private final Hashtable<ViewPointPalette,CalcPaletteController> _paletteControllers;
	private final Hashtable<ExampleDrawingShema,CalcDrawingShemaController> _shemaControllers;
	private final Hashtable<SelectionManagingDrawingController,JSplitPane> _splitPaneForProcess;
	
	private final JLabel infoLabel;
	
	private static final JPanel EMPTY_RIGHT_VIEW = new JPanel();

	/**
	 * @param controller TODO
	 * @param name
	 */
	public ViewPointPerspective(CEDController controller)
	{
		super("calc_perspective");
		_controller = controller;
		_paletteControllers = new Hashtable<ViewPointPalette, CalcPaletteController>();
		_shemaControllers = new Hashtable<ExampleDrawingShema, CalcDrawingShemaController>();
		_splitPaneForProcess = new Hashtable<SelectionManagingDrawingController, JSplitPane>();
		_browser = new CalcLibraryBrowser(controller);
		_browserView = new CEDBrowserView(_browser, _controller, SelectionPolicy.ParticipateToSelection) {
			@Override
			public void treeDoubleClick(FlexoModelObject object) {
				super.treeDoubleClick(object);
				if (object instanceof ViewPoint) {
					focusOnCalc((ViewPoint)object);
					//System.out.println(((OntologyCalc)object).getXMLRepresentation());
				}
			}
		};
		calcBrowser = new CalcBrowser(controller);
		calcBrowserView = new CEDBrowserView(calcBrowser, controller, SelectionPolicy.ForceSelection) {
			@Override
			public void treeDoubleClick(FlexoModelObject object) {
				super.treeDoubleClick(object);
				if (object instanceof ViewPointPalette) {
					focusOnPalette((ViewPointPalette)object);
				}
				else if (object instanceof ImportedOntology) {
					focusOnOntology((ImportedOntology)object);
				}
				else if (object instanceof EditionPattern) {
					hideBottomBrowser();
				}
			}			
			
			@Override
			public void treeSingleClick(FlexoModelObject object)
			{
				super.treeSingleClick(object);
				if (! (object instanceof ViewPointPalette) && !(object instanceof ImportedOntology)) {
					hideBottomBrowser();
				}
			}
		};
		
		calcPaletteBrowser = new CalcPaletteBrowser(controller);
		calcPaletteBrowserView = new CEDBrowserView(calcPaletteBrowser, controller, SelectionPolicy.ForceSelection);
		
		calcDrawingShemaBrowser = new CalcDrawingShemaBrowser(controller);
		calcDrawingShemaBrowserView = new CEDBrowserView(calcDrawingShemaBrowser, controller, SelectionPolicy.ForceSelection);
		
		ontologyBrowser = new OntologyBrowser(controller);
		ontologyBrowserView = new CEDBrowserView(ontologyBrowser, controller, SelectionPolicy.ForceSelection);
		
		
		splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,calcBrowserView,calcPaletteBrowserView);
		splitPane2.setDividerLocation(0.5);
		splitPane2.setResizeWeight(0.5);
		splitPane2.remove(calcPaletteBrowserView);
		
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,_browserView,splitPane2);
		splitPane.setDividerLocation(0.5);
		splitPane.setResizeWeight(0.5);
		infoLabel = new JLabel("Calc perspective");
		infoLabel.setFont(FlexoCst.SMALL_FONT);
	}

	public void focusOnCalc(ViewPoint calc)
	{
		splitPane2.setBottomComponent(null);
		calcBrowser.deleteBrowserListener(_browserView); 		            
		calcBrowser.setRepresentedObject(calc);
		calcBrowser.update();
		calcBrowser.addBrowserListener(_browserView); 		            
	}
	
	public void focusOnPalette(ViewPointPalette palette)
	{
		splitPane2.setBottomComponent(calcPaletteBrowserView);
		calcPaletteBrowser.deleteBrowserListener(_browserView); 		            
		calcPaletteBrowser.setRepresentedPalette(palette);
		calcPaletteBrowser.update();
		calcPaletteBrowser.addBrowserListener(_browserView); 		            
	}
	
	public void focusOnShema(ExampleDrawingShema shema)
	{
		splitPane2.setBottomComponent(calcDrawingShemaBrowserView);
		calcDrawingShemaBrowser.deleteBrowserListener(_browserView); 		            
		calcDrawingShemaBrowser.setRepresentedShema(shema);
		calcDrawingShemaBrowser.update();
		calcDrawingShemaBrowser.addBrowserListener(_browserView); 		            
	}
	
	public void focusOnOntology(ImportedOntology ontology)
	{
		splitPane2.setBottomComponent(ontologyBrowserView);
		ontologyBrowser.deleteBrowserListener(_browserView); 		            
		ontologyBrowser.setRepresentedOntology(ontology);
		ontologyBrowser.update();
		ontologyBrowser.addBrowserListener(_browserView); 		            
	}
	
	public void hideBottomBrowser()
	{
		splitPane2.setBottomComponent(null);
	}
	
	/**
	 * Overrides getIcon
	 *
	 * @see org.openflexo.view.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon()
	{
		return VPMIconLibrary.VPM_VPE_ACTIVE_ICON;
	}

	/**
	 * Overrides getSelectedIcon
	 *
	 * @see org.openflexo.view.FlexoPerspective#getSelectedIcon()
	 */
	@Override
	public ImageIcon getSelectedIcon()
	{
		return VPMIconLibrary.VPM_VPE_SELECTED_ICON;
	}

	@Override
	public FlexoModelObject getDefaultObject(FlexoModelObject proposedObject) 
	{
		if (hasModuleViewForObject(proposedObject)) {
			return proposedObject;
		}
		return _controller.getCalcLibrary();
	}

	@Override
	public boolean hasModuleViewForObject(FlexoModelObject object)
	{
		return (object instanceof ViewPointLibrary) 
		|| (object instanceof ImportedOntology)
		|| (object instanceof ViewPointPalette)
		|| (object instanceof ExampleDrawingShema)
		|| (object instanceof ViewPoint)
		|| (object instanceof EditionPattern);
	}


	@Override
	public ModuleView<? extends FlexoModelObject> createModuleViewForObject(FlexoModelObject object, FlexoController controller)
	{
		if (object instanceof ViewPointLibrary) {
			return new CalcLibraryView((ViewPointLibrary)object,(CEDController)controller);
		}
		if (object instanceof ImportedOntology) {
			((ImportedOntology)object).loadWhenUnloaded();
			return new OntologyView((ImportedOntology)object,(CEDController)controller,this);
		}
		if (object instanceof ViewPoint) {
			((ViewPoint)object).loadWhenUnloaded();
			return new CalcView((ViewPoint)object,(CEDController)controller);
		}
		if (object instanceof EditionPattern) {
			((EditionPattern)object).getCalc().loadWhenUnloaded();
			return new EditionPatternView((EditionPattern)object,(CEDController)controller);
		}
		if (object instanceof ViewPointPalette) {
			return getControllerForPalette((ViewPointPalette)object).getModuleView();
		}
		if (object instanceof ExampleDrawingShema) {
			return getControllerForShema((ExampleDrawingShema)object).getModuleView();
		}
		return new EmptyPanel<FlexoModelObject>(controller,this,object);
	}

	public ModuleView getCurrentModuleView()
	{
		if (_controller != null) {
			return _controller.getCurrentModuleView();
		}
		return null;
	}


	@Override
	public boolean doesPerspectiveControlLeftView() 
	{
		return true;
	}
	
	@Override
	public JComponent getLeftView() 
	{
		return splitPane;
	}

	@Override
	public JComponent getFooter() 
	{
		return infoLabel;
	}


	   @Override
	    public boolean doesPerspectiveControlRightView()
	    {
	    	return true;
	    }

		@Override
		public JComponent getRightView()
		{
			if (getCurrentModuleView() == null) {
				return EMPTY_RIGHT_VIEW;
			}
			return getSplitPaneWithPalettesAndDocInspectorPanel();
		}

		/**
		 * Return Split pane with Role palette and doc inspector panel
		 * Disconnect doc inspector panel from its actual parent
		 * @return
		 */
		protected JComponent getSplitPaneWithPalettesAndDocInspectorPanel()
		{
			SelectionManagingDrawingController controller = null;
			JTabbedPane paletteView = null;
			if (getCurrentModuleView() instanceof CalcPaletteModuleView) {
				controller =  ((CalcPaletteModuleView)getCurrentModuleView()).getController();
				paletteView = (((CalcPaletteModuleView)getCurrentModuleView()).getController()).getPaletteView();
			}
			if (getCurrentModuleView() instanceof CalcDrawingShemaModuleView) {
				controller =  ((CalcDrawingShemaModuleView)getCurrentModuleView()).getController();
				paletteView = (((CalcDrawingShemaModuleView)getCurrentModuleView()).getController()).getPaletteView();
			}

			
			if ((controller != null) && (paletteView != null)) {
				JSplitPane splitPaneWithPalettesAndDocInspectorPanel = _splitPaneForProcess.get(controller);
				if (splitPaneWithPalettesAndDocInspectorPanel == null) {
					splitPaneWithPalettesAndDocInspectorPanel = new JSplitPane(
							JSplitPane.VERTICAL_SPLIT,
							paletteView,
							_controller.getDisconnectedDocInspectorPanel());
					splitPaneWithPalettesAndDocInspectorPanel.setResizeWeight(0);
					splitPaneWithPalettesAndDocInspectorPanel.setDividerLocation(CEDCst.PALETTE_DOC_SPLIT_LOCATION);
					_splitPaneForProcess.put(controller,splitPaneWithPalettesAndDocInspectorPanel);
				}
				if (splitPaneWithPalettesAndDocInspectorPanel.getBottomComponent() == null) {
					splitPaneWithPalettesAndDocInspectorPanel.setBottomComponent(_controller.getDisconnectedDocInspectorPanel());
				}
				new FlexoSplitPaneLocationSaver(splitPaneWithPalettesAndDocInspectorPanel,"CEDPaletteAndDocInspectorPanel");
				return splitPaneWithPalettesAndDocInspectorPanel;
			}
		
			//logger.warning("Unexpected view: "+getCurrentModuleView());
			return EMPTY_RIGHT_VIEW;
		}

	
	@Override
	public boolean isAlwaysVisible() 
	{
		return true;
	}
	
	public String getWindowTitleforObject(FlexoModelObject object) 
	{
		if (object instanceof ViewPointLibrary) {
			return FlexoLocalization.localizedForKey("view_point_library");
		}
		if (object instanceof ViewPoint) {
			return ((ViewPoint)object).getName();
		}
		if (object instanceof ViewPointPalette) {
			return ((ViewPointPalette)object).getName()+" ("+FlexoLocalization.localizedForKey("palette")+")";
		}
		if (object instanceof ExampleDrawingShema) {
			return ((ExampleDrawingShema)object).getName()+" ("+FlexoLocalization.localizedForKey("example_drawing")+")";
		}
		if (object instanceof ViewPoint) {
			return ((ViewPoint)object).getName();
		}
		if (object instanceof EditionPattern) {
			return ((EditionPattern)object).getName();
		}
		return object.getFullyQualifiedName();
	}
	
	public CalcPaletteController getControllerForPalette(ViewPointPalette object)
	{
		CalcPaletteController returned = _paletteControllers.get(object);
		if (returned == null) {
				returned = new CalcPaletteController(_controller,object,false);
				_paletteControllers.put(object, returned);
		}
		return returned;
	}


	public void removeFromControllers(CalcPaletteController controller)
	{
		_paletteControllers.remove(controller.getDrawing().getModel());
		_splitPaneForProcess.remove(controller);
	}

	public CalcDrawingShemaController getControllerForShema(ExampleDrawingShema object)
	{
		CalcDrawingShemaController returned = _shemaControllers.get(object);
		if (returned == null) {
				returned = new CalcDrawingShemaController(_controller,object,false);
				_shemaControllers.put(object, returned);
		}
		return returned;
	}


	public void removeFromControllers(CalcDrawingShemaController controller)
	{
		_shemaControllers.remove(controller.getDrawing().getModel());
		_splitPaneForProcess.remove(controller);
	}


}