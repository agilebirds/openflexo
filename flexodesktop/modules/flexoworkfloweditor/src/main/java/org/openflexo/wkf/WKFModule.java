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
package org.openflexo.wkf;

import java.awt.Dimension;
import java.io.File;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.ApplicationContext;
import org.openflexo.components.ProgressWindow;
import org.openflexo.components.browser.view.BrowserView.FlexoJTree;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.InspectorGroup;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.ActivityGroup;
import org.openflexo.foundation.wkf.ActivityPetriGraph;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.RoleList;
import org.openflexo.foundation.wkf.WKFArtefact;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.edge.MessageEdge;
import org.openflexo.foundation.wkf.edge.WKFEdge;
import org.openflexo.foundation.wkf.node.FatherNode;
import org.openflexo.foundation.wkf.node.LOOPOperator;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.foundation.wkf.node.SelfExecutableNode;
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.foundation.wkf.ws.FlexoPortMap;
import org.openflexo.foundation.wkf.ws.PortMapRegistery;
import org.openflexo.foundation.wkf.ws.PortRegistery;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.Module;
import org.openflexo.module.external.ExternalWKFModule;
import org.openflexo.swing.FlexoSwingUtils;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.wkf.controller.WKFController;
import org.openflexo.wkf.controller.WorkflowBrowser;
import org.openflexo.wkf.processeditor.ProcessEditorController;
import org.openflexo.wkf.processeditor.ProcessRepresentation.ProcessRepresentationDefaultVisibilityDelegate;
import org.openflexo.wkf.processeditor.ProcessView;
import org.openflexo.wkf.roleeditor.RoleEditorController;
import org.openflexo.wkf.swleditor.SwimmingLaneEditorController;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation.SwimmingLaneRepresentationDefaultVisibilityDelegate;
import org.openflexo.wkf.view.WorkflowBrowserView;

/**
 * Workflow Editor module
 * 
 * @author sguerin
 */
public class WKFModule extends FlexoModule implements ExternalWKFModule {
	private static final InspectorGroup[] inspectorGroups = new InspectorGroup[] { Inspectors.WKF };

	private Map<Drawing<? extends FlexoModelObject>, DrawingController<? extends Drawing<? extends FlexoModelObject>>> drawingControllers = new Hashtable<Drawing<? extends FlexoModelObject>, DrawingController<? extends Drawing<? extends FlexoModelObject>>>();

	public static enum ProcessRepresentation {
		BASIC_EDITOR, SWIMMING_LANE;
	}

	private static final Logger logger = FlexoLogger.getLogger(WKFModule.class.getPackage().getName());

	public WKFModule(ApplicationContext applicationContext) throws Exception {
		super(applicationContext);
		getWKFController().loadRelativeWindows();
		WKFPreferences.init();
		ProgressWindow.setProgressInstance(FlexoLocalization.localizedForKey("build_editor"));
	}

	@Override
	public Module getModule() {
		return Module.WKF_MODULE;
	}

	@Override
	protected FlexoController createControllerForModule() {
		return new WKFController(this);
	}

	@Override
	public InspectorGroup[] getInspectorGroups() {
		return inspectorGroups;
	}

	public File getPaletteDirectory() {
		return new FileResource(WKFCst.PALETTE_FOLDER_PATH);
	}

	public WKFController getWKFController() {
		return (WKFController) getFlexoController();
	}

	@Override
	public FlexoWorkflow getFlexoWorkflow() {
		return getWKFController().getFlexoWorkflow();
	}

	private static class BPEScreenshotProcessRepresentationObjectVisibilityDelegate extends ProcessRepresentationDefaultVisibilityDelegate {

		private WKFObject target;

		public BPEScreenshotProcessRepresentationObjectVisibilityDelegate(WKFObject target) {
			this.target = target;
		}

		@Override
		public boolean isVisible(WKFObject object) {
			if (object == null) {
				return false;
			}
			if (object instanceof FlexoProcess) {
				return false;
			}
			if (object == target) {
				return true;
			} else if (object instanceof FlexoPetriGraph) {
				return ((FlexoPetriGraph) object).getContainer() == target || object instanceof ActivityPetriGraph
						&& ((ActivityPetriGraph) object).getContainer() instanceof FlexoProcess || target instanceof PetriGraphNode
						&& ((PetriGraphNode) target).isEmbeddedInPetriGraph((FlexoPetriGraph) object) || target instanceof WKFArtefact
						&& ((WKFArtefact) target).isEmbeddedInPetriGraph((FlexoPetriGraph) object);
			} else if (object instanceof FlexoPort) {
				return isVisible(((FlexoPort) object).getPortRegistery());
			} else if (object instanceof FlexoPortMap) {
				return !((FlexoPortMap) object).getIsHidden() && isVisible(((FlexoPortMap) object).getPortMapRegistery());
			} else if (object instanceof PetriGraphNode) {
				if (isVisible(((PetriGraphNode) object).getParentPetriGraph())) {
					return true;
				}
				FlexoPetriGraph petriGraph = null;
				if (object instanceof FatherNode) {
					petriGraph = ((FatherNode) object).getContainedPetriGraph();
				} else if (object instanceof SelfExecutableNode) {
					petriGraph = ((SelfExecutableNode) object).getExecutionPetriGraph();
				} else if (object instanceof LOOPOperator) {
					petriGraph = ((LOOPOperator) object).getExecutionPetriGraph();
				}
				return petriGraph != null && isVisible(petriGraph);
			} else if (object instanceof WKFArtefact) {
				return isVisible(((WKFArtefact) object).getParentPetriGraph());
			} else if (object instanceof PortMapRegistery) {
				return !((PortMapRegistery) object).getIsHidden() && ((PortMapRegistery) object).getPortMaps().size() > 0;
			} else if (object instanceof PortRegistery) {
				return ((PortRegistery) object).getIsVisible();
			} else if (object instanceof WKFEdge<?, ?>) {
				WKFEdge<?, ?> post = (WKFEdge<?, ?>) object;
				WKFObject firstVisibleStartObject = getFirstVisibleObject(post.getStartNode());
				WKFObject firstVisibleEndObject = getFirstVisibleObject(post.getEndNode());
				if (post instanceof MessageEdge<?, ?>) {
					if (firstVisibleStartObject instanceof FlexoPortMap) {
						if (((FlexoPortMap) firstVisibleStartObject).getSubProcessNode() == firstVisibleEndObject) {
							return false;
						}
					} else if (firstVisibleEndObject instanceof FlexoPortMap) {
						if (((FlexoPortMap) firstVisibleEndObject).getSubProcessNode() == firstVisibleStartObject) {
							return false;
						}
					}
				}
				return !(firstVisibleStartObject != post.getStartNode() && firstVisibleEndObject != post.getEndNode() && firstVisibleStartObject == firstVisibleEndObject)
						&& firstVisibleStartObject != null && firstVisibleEndObject != null;
			} else if (object instanceof ActivityGroup) {
				if (target instanceof PetriGraphNode) {
					if (((PetriGraphNode) target).isGrouped()) {
						return ((PetriGraphNode) target).getContainerGroup() == object;
					}
				}
			}
			return true;
		}

	}

	private class SWLScreenshotProcessRepresentationObjectVisibilityDelegate extends SwimmingLaneRepresentationDefaultVisibilityDelegate {

		private WKFObject target;

		public SWLScreenshotProcessRepresentationObjectVisibilityDelegate(WKFObject target) {
			this.target = target;
		}

		@Override
		public boolean isVisible(WKFObject object) {
			if (object == null) {
				return false;
			}
			if (object instanceof FlexoProcess) {
				return false;
			}
			if (object == target) {
				return true;
			} else if (object instanceof PetriGraphNode
					&& ((PetriGraphNode) object).getParentPetriGraph() == object.getProcess().getActivityPetriGraph()) {
				return true;
			} else if (object instanceof FlexoPetriGraph) {
				return ((FlexoPetriGraph) object).getContainer() == target || object instanceof ActivityPetriGraph
						&& ((ActivityPetriGraph) object).getContainer() instanceof FlexoProcess || target instanceof PetriGraphNode
						&& ((PetriGraphNode) target).isEmbeddedInPetriGraph((FlexoPetriGraph) object) || target instanceof WKFArtefact
						&& ((WKFArtefact) target).isEmbeddedInPetriGraph((FlexoPetriGraph) object);
			} else if (object instanceof FlexoPort) {
				return isVisible(((FlexoPort) object).getPortRegistery());
			} else if (object instanceof FlexoPortMap) {
				return !((FlexoPortMap) object).getIsHidden() && isVisible(((FlexoPortMap) object).getPortMapRegistery());
			} else if (object instanceof PetriGraphNode) {
				if (isVisible(((PetriGraphNode) object).getParentPetriGraph())) {
					return true;
				}
				FlexoPetriGraph petriGraph = null;
				if (object instanceof FatherNode) {
					petriGraph = ((FatherNode) object).getContainedPetriGraph();
				} else if (object instanceof SelfExecutableNode) {
					petriGraph = ((SelfExecutableNode) object).getExecutionPetriGraph();
				} else if (object instanceof LOOPOperator) {
					petriGraph = ((LOOPOperator) object).getExecutionPetriGraph();
				}
				return petriGraph != null && isVisible(petriGraph);
			} else if (object instanceof WKFArtefact) {
				return isVisible(((WKFArtefact) object).getParentPetriGraph());
			} else if (object instanceof PortMapRegistery) {
				return !((PortMapRegistery) object).getIsHidden() && ((PortMapRegistery) object).getPortMaps().size() > 0;
			} else if (object instanceof PortRegistery) {
				return ((PortRegistery) object).getIsVisible();
			} else if (object instanceof WKFEdge<?, ?>) {
				WKFEdge<?, ?> post = (WKFEdge<?, ?>) object;
				WKFObject firstVisibleStartObject = getFirstVisibleObject(post.getStartNode());
				WKFObject firstVisibleEndObject = getFirstVisibleObject(post.getEndNode());
				if (post instanceof MessageEdge<?, ?>) {
					if (firstVisibleStartObject instanceof FlexoPortMap) {
						if (((FlexoPortMap) firstVisibleStartObject).getSubProcessNode() == firstVisibleEndObject) {
							return false;
						}
					} else if (firstVisibleEndObject instanceof FlexoPortMap) {
						if (((FlexoPortMap) firstVisibleEndObject).getSubProcessNode() == firstVisibleStartObject) {
							return false;
						}
					}
				}
				return !(firstVisibleStartObject != post.getStartNode() && firstVisibleEndObject != post.getEndNode() && firstVisibleStartObject == firstVisibleEndObject)
						&& firstVisibleStartObject != null && firstVisibleEndObject != null;
			} else if (object instanceof ActivityGroup) {
				if (target instanceof PetriGraphNode) {
					if (((PetriGraphNode) target).isGrouped()) {
						return ((PetriGraphNode) target).getContainerGroup() == object;
					}
				}
			}
			return true;
		}
	}

	private class ScreenshotRetriever implements Callable<DrawingController<? extends Drawing<? extends FlexoModelObject>>> {
		private FlexoModelObject target;
		private final boolean showAll;

		protected ScreenshotRetriever(FlexoModelObject target, boolean showAll) {
			super();
			this.target = target;
			this.showAll = showAll;
		}

		@Override
		public DrawingController<? extends Drawing<? extends FlexoModelObject>> call() {
			DrawingController<? extends Drawing<? extends FlexoModelObject>> screenshotController = null;
			if (target instanceof RoleList) {
				screenshotController = new RoleEditorController((RoleList) target, null, null);
			} else if (target instanceof WKFObject) {
				screenshotController = getProcessRepresentationController((WKFObject) target, showAll);
			} else {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("Cannot create drawing controller for " + target);
				}
			}
			if (screenshotController != null) {
				drawingControllers.put(screenshotController.getDrawing(), screenshotController);
			}
			return screenshotController;
		}

	}

	@Override
	public JComponent createScreenshotForObject(final FlexoModelObject target) {
		if (target == null) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Cannot create screenshot for null target!");
			}
			return null;
		}
		JComponent component;
		if (target instanceof FlexoWorkflow) {
			FlexoJTree treeView = new WorkflowBrowserView(new WorkflowBrowser(target.getProject()), null).getTreeView();
			component = treeView;
			// Expand all nodes in tree
			for (int i = 0; i < treeView.getRowCount(); i++) {
				treeView.expandRow(i);
			}
		} else {
			DrawingController<? extends Drawing<? extends FlexoModelObject>> screenshotController = getScreenshotControllerForObject(
					target, false);
			DrawingView<? extends Drawing<? extends FlexoModelObject>> screenshot;
			component = screenshot = screenshotController.getDrawingView();
			screenshot.getDrawingGraphicalRepresentation().setDrawWorkingArea(false);
			if (screenshot instanceof ProcessView) {
				((ProcessView) screenshot).getDrawingGraphicalRepresentation().setShowGrid(false);
			}
			screenshot.getPaintManager().disablePaintingCache();
			screenshot.validate();
			Dimension d = screenshot.getComputedMinimumSize();
			d.height += 20;
			d.width += 20;
			screenshot.setSize(d);
			screenshot.setPreferredSize(d);
		}
		return component;
	}

	@Override
	public float getScreenshotQuality() {
		float reply = Float.valueOf(WKFPreferences.getScreenshotQuality()) / 100f;
		if (reply > 1) {
			return 1f;
		}
		if (reply < 0.1f) {
			return 0.1f;
		}
		return reply;
	}

	private DrawingController<? extends Drawing<? extends FlexoModelObject>> getScreenshotControllerForObject(FlexoModelObject target,
			boolean showAll) {
		try {
			return FlexoSwingUtils.syncRunInEDT(new ScreenshotRetriever(target, showAll));
		} catch (Exception e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.log(Level.SEVERE, "Could not retrieve screenshot for " + target, e);
			}
			return null;
		}
	}

	/**
	 * @param target
	 */
	private DrawingController<? extends Drawing<FlexoProcess>> getProcessRepresentationController(WKFObject target, boolean showAll) {
		DrawingController<? extends Drawing<FlexoProcess>> controller = null;
		ProcessRepresentation pr = (ProcessRepresentation) target.getProcess()._graphicalPropertyForKey("preferredRepresentation");
		if (pr == null) {
			pr = ProcessRepresentation.BASIC_EDITOR;
		}
		switch (pr) {
		case BASIC_EDITOR:
			controller = new ProcessEditorController(target.getProcess(), null, null,
					showAll ? org.openflexo.wkf.processeditor.ProcessRepresentation.SHOW_ALL
							: new BPEScreenshotProcessRepresentationObjectVisibilityDelegate(target));
			break;
		case SWIMMING_LANE:
			controller = new SwimmingLaneEditorController(target.getProcess(), null, null, showAll ? SwimmingLaneRepresentation.SHOW_ALL
					: new SWLScreenshotProcessRepresentationObjectVisibilityDelegate(target));
			break;
		default:
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Unknown process representation value: " + pr);
			}
			break;
		}
		return controller;
	}

	@Override
	public void finalizeScreenshotGeneration(JComponent screenshot) {
		if (screenshot != null) {
			if (screenshot instanceof DrawingView) {
				DrawingController<?> controller = ((DrawingView<?>) screenshot).getController();
				drawingControllers.remove(controller.getDrawing());
				controller.delete();
			} else if (screenshot instanceof FlexoJTree) {
				((FlexoJTree) screenshot).getBrowserView().getBrowser().delete();
			}
			if (screenshot.getParent() != null) {
				screenshot.getParent().remove(screenshot);
			}
		}
	}

	/**
	 * Overrides getDefaultObjectToSelect
	 * 
	 * @see org.openflexo.module.FlexoModule#getDefaultObjectToSelect(FlexoProject)
	 */
	@Override
	public FlexoModelObject getDefaultObjectToSelect(FlexoProject project) {
		return project.getFlexoWorkflow().getRootProcess();
	}

	/**
	 * Overrides moduleWillClose
	 * 
	 * @see org.openflexo.module.FlexoModule#moduleWillClose()
	 */
	@Override
	public void moduleWillClose() {
		super.moduleWillClose();
		for (DrawingController<? extends Drawing<? extends FlexoModelObject>> drawingController : drawingControllers.values()) {
			drawingController.delete();
		}
		drawingControllers.clear();
	}

	@Override
	public Object getProcessRepresentation(FlexoProcess process, boolean showAll) {
		try {
			process.setIgnoreNotifications();
			DrawingController<? extends Drawing<? extends FlexoModelObject>> controller = getScreenshotControllerForObject(process, showAll);
			return controller.getDrawing();
		} finally {
			process.resetIgnoreNotifications();
		}
	}

	@Override
	public void disposeProcessRepresentation(Object processRepresentation) {
		DrawingController<? extends Drawing<? extends FlexoModelObject>> drawingController = drawingControllers
				.remove(processRepresentation);
		if (drawingController != null) {
			drawingController.delete();
		}
	}
}
