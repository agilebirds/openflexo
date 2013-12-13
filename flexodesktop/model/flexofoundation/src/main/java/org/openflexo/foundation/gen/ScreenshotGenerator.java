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
package org.openflexo.foundation.gen;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoXMLSerializableObject;
import org.openflexo.foundation.dm.ERDiagram;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.RoleList;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.WorkflowModelObject;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.LOOPOperator;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.module.ModuleLoadingException;
import org.openflexo.module.external.ExternalDMModule;
import org.openflexo.module.external.ExternalIEModule;
import org.openflexo.module.external.ExternalModule;
import org.openflexo.module.external.ExternalVEModule;
import org.openflexo.module.external.ExternalWKFModule;
import org.openflexo.swing.FlexoSwingUtils;
import org.openflexo.swing.ImageUtils;
import org.openflexo.toolbox.FileResource;

//TODO : rename it into ScreenshotBuilder
public class ScreenshotGenerator {
	public static class ScreenshotImage {
		public BufferedImage image;
		public Rectangle trimInfo;
	}

	private static final Logger logger = FlexoLogger.getLogger(ScreenshotGenerator.class.getPackage().getName());

	protected static final String BAD_FILE_NAME_CHARACTERS_REG_EXP = "[^-A-Za-z0-9]";

	protected static final Pattern BAD_FILE_NAME_CHARACTERS_PATTERN = Pattern.compile(BAD_FILE_NAME_CHARACTERS_REG_EXP);

	private static final String REPLACEMENT = "-";

	public static String getScreenshotName(Object o) {
		if (o instanceof FlexoProcess) {
			return getImageNameForProcess((FlexoProcess) o);
		} else if (o instanceof OperationNode) {
			return getImageNameForOperation((OperationNode) o);
		} else if (o instanceof AbstractActivityNode) {
			return getImageNameForActivity((AbstractActivityNode) o);
		} else if (o instanceof ComponentDefinition) {
			return getImageNameForComponent((ComponentDefinition) o);
		} else if (o instanceof IEWOComponent) {
			return getImageNameForComponent(((IEWOComponent) o).getComponentDefinition());
		} else if (o instanceof LOOPOperator) {
			return getImageNameForLoopOperator((LOOPOperator) o);
		} else if (o instanceof ERDiagram) {
			return getImageNameForERDiagram((ERDiagram) o);
		} else if (o instanceof RoleList) {
			return getImageNameForRoleList((RoleList) o);
		} else if (o instanceof FlexoWorkflow) {
			return getImageNameForWorkflow((FlexoWorkflow) o);
		} /*else if (o instanceof Diagram) {
			return getImageNameForDiagram((Diagram) o);
			}*/
		return null;
	}

	private static String getImageNameForWorkflow(FlexoWorkflow o) {
		return getImageName("WORKFLOW", o.getName(), o.getFlexoID());
	}

	private static String getImageNameForRoleList(RoleList o) {
		return getImageName("ROLES", o.getWorkflow().getName(), o.getFlexoID());
	}

	/**
	 * @param definition
	 * @return
	 */
	private static String getImageNameForComponent(ComponentDefinition definition) {
		return getImageName(IEWOComponent.getTypeName(), definition.getComponentName(), definition.getFlexoID());
	}

	/**
	 * @param node
	 * @return
	 */
	private static String getImageNameForActivity(AbstractActivityNode node) {
		return getImageName(AbstractActivityNode.getTypeName(), node.getProcess().getName() + "-" + node.getName(), node.getFlexoID());
	}

	private static String getImageNameForLoopOperator(LOOPOperator node) {
		return getImageName(LOOPOperator.getTypeName(), node.getProcess().getName() + "-" + node.getName(), node.getFlexoID());
	}

	private static String getImageNameForERDiagram(ERDiagram node) {
		return getImageName("ERDiagram", node.getName(), node.getFlexoID());
	}

	/**
	 * @param node
	 * @return
	 */
	private static String getImageNameForOperation(OperationNode node) {
		return getImageName(OperationNode.getTypeName(), node.getProcess().getName() + "-" + node.getName(), node.getFlexoID());
	}

	/**
	 * @param process
	 * @return
	 */
	private static String getImageNameForProcess(FlexoProcess process) {
		return getImageName(FlexoProcess.getTypeName(), process.getName(), process.getFlexoID());
	}

	/**
	 * @param shema
	 * @return
	 */
	/*private static String getImageNameForDiagram(Diagram diagram) {
		return getImageName(Diagram.getTypeName(), diagram.getName(), diagram.getFlexoID());
	}*/

	private static String getImageName(String type, String name, long flexoID) {
		return trim(formatImageName(type + "-" + name)) + flexoID;
	}

	private static String trim(String name) {
		// Max-length is 255 chars
		// We need to remove 20 characters for the flexoID Long.MAX_VALUE is 20 digits
		// We need to remove 4 chars for the extension (.png)
		// Let's be cautious and add an extra 30 chars security
		if (name.length() > 200) {
			return name.substring(0, 200).trim();
		} else {
			return name;
		}
	}

	private static String formatImageName(String imageName) {
		if (imageName == null) {
			return null;
		}
		return BAD_FILE_NAME_CHARACTERS_PATTERN.matcher(imageName).replaceAll(REPLACEMENT);
	}

	public static ScreenshotImage getImage(FlexoModelObject object) {
		if (object == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Object is null: cannot generate screenshot");
			}
			return getEmptyScreenshot();
		}
		logger.info("Generating screenshot for " + object + " of " + object.getClass().getSimpleName());
		if (object.getXMLResourceData() instanceof FlexoXMLSerializableObject) {
			((FlexoXMLSerializableObject) object.getXMLResourceData()).setIgnoreNotifications();
		}
		try {
			ScreenshotComponentRunnable componentRunnable = new ScreenshotComponentRunnable(object);
			JComponent component = null;
			try {
				component = FlexoSwingUtils.syncRunInEDT(componentRunnable);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			if (component == null) {
				return getEmptyScreenshot();
			}
			ScreenshotImageRunnable runnable = new ScreenshotImageRunnable(component, object);
			ScreenshotImage i = null;
			try {
				i = FlexoSwingUtils.syncRunInEDT(runnable);
				FlexoSwingUtils.syncRunInEDT(new ScreenshotFinalizeRunnable(component, object));
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (i == null) {
				return getEmptyScreenshot();
			}
			return i;
		} finally {
			if (object.getXMLResourceData() instanceof FlexoXMLSerializableObject) {
				((FlexoXMLSerializableObject) object.getXMLResourceData()).resetIgnoreNotifications();
			}
		}
	}

	private static class ScreenshotComponentRunnable implements Callable<JComponent> {
		private final FlexoModelObject object;

		protected ScreenshotComponentRunnable(FlexoModelObject object) {
			this.object = object;
		}

		@Override
		public JComponent call() {
			return getScreenshotComponent(object);
		}

	}

	private static class ScreenshotImageRunnable implements Callable<ScreenshotImage> {
		private final FlexoModelObject object;
		private final JComponent component;

		protected ScreenshotImageRunnable(JComponent component, FlexoModelObject object) {
			this.component = component;
			this.object = object;
		}

		@Override
		public ScreenshotImage call() {
			return createImageForComponent(component, object instanceof WKFObject || object instanceof WorkflowModelObject);
		}

	}

	private static class ScreenshotFinalizeRunnable implements Callable<Void> {
		private final FlexoModelObject object;
		private final JComponent component;

		protected ScreenshotFinalizeRunnable(JComponent component, FlexoModelObject object) {
			this.component = component;
			this.object = object;
		}

		@Override
		public Void call() {
			finalizeScreenshotGeneration(component, object);
			return null;
		}

	}

	private static JComponent getScreenshotComponent(FlexoModelObject object) {
		ExternalWKFModule wkfModule = null;
		ExternalIEModule ieModule = null;
		ExternalDMModule dmModule = null;

		ExternalVEModule oeModule = null;

		FlexoProject project = object.getProject();
		JComponent c = null;
		try {

			try {
				if (object instanceof AbstractActivityNode || object instanceof FlexoProcess || object instanceof LOOPOperator
						|| object instanceof RoleList || object instanceof FlexoWorkflow) {
					wkfModule = project.getModuleLoader() != null ? project.getModuleLoader().getWKFModuleInstance() : null;
				} else if (object instanceof IEWOComponent || object instanceof ComponentDefinition || object instanceof OperationNode) {
					ieModule = project.getModuleLoader() != null ? project.getModuleLoader().getIEModuleInstance() : null;
				} else if (object instanceof ERDiagram) {
					dmModule = project.getModuleLoader() != null ? project.getModuleLoader().getDMModuleInstance() : null;
				} else if (object instanceof View) {
					oeModule = project.getModuleLoader() != null ? project.getModuleLoader().getVEModuleInstance() : null;
				}
			} catch (ModuleLoadingException e) {
				logger.warning("cannot load module (and so can't create screenshot)." + e.getMessage());
				e.printStackTrace();
			}
			if (wkfModule != null || ieModule != null || dmModule != null || oeModule != null) {
				ExternalModule module = ieModule != null ? ieModule : wkfModule != null ? wkfModule : dmModule != null ? dmModule
						: oeModule;
				synchronized (module) {// We synchronize on the module because modules cannot handle multiple screenshot generation
					if (object instanceof FlexoProcess) {
						c = wkfModule.createScreenshotForObject(object);
					} else if (object instanceof AbstractActivityNode) {
						c = wkfModule.createScreenshotForObject(object);
					} else if (object instanceof LOOPOperator) {
						c = wkfModule.createScreenshotForObject(object);
					} else if (object instanceof RoleList) {
						c = wkfModule.createScreenshotForObject(object);
					} else if (object instanceof FlexoWorkflow) {
						c = wkfModule.createScreenshotForObject(object);
					} else if (object instanceof OperationNode) {
						c = ieModule.createViewForOperation((OperationNode) object);
					} else if (object instanceof ComponentDefinition) {
						c = ieModule
								.getWOComponentView(ieModule.getIEExternalController(), ((ComponentDefinition) object).getWOComponent());
					} else if (object instanceof IEWOComponent) {
						c = ieModule.getWOComponentView(ieModule.getIEExternalController(), ((IEWOComponent) object)
								.getComponentDefinition().getWOComponent());
					} else if (object instanceof ERDiagram) {
						c = dmModule.createScreenshotForObject((ERDiagram) object);
					} /*else if (object instanceof Diagram) {
						c = oeModule.createScreenshotForDiagram(((Diagram) object).getResource());
						}*/
					return c;

				}
			} else {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("No module found to generate screenshot for object: " + object);
				}
			}

		} catch (Throwable e) {
			logger.severe("Failed to generate screenshot for " + object);
			e.printStackTrace();
		}
		return c;
	}

	private static ScreenshotImage createImageForComponent(JComponent c, boolean trim) {
		JFrame frame = new JFrame();
		try {
			BufferedImage bi = null;
			c.setOpaque(true);
			c.setBackground(Color.WHITE);
			frame.setBackground(Color.WHITE);
			frame.setUndecorated(true);
			frame.getContentPane().add(c);
			frame.pack();
			bi = ImageUtils.createImageFromComponent(c);
			ScreenshotImage i;
			if (trim) {
				i = trimImage(bi);
			} else {
				i = new ScreenshotImage();
				i.image = bi;
				i.trimInfo = new Rectangle(0, 0, bi.getWidth(), bi.getHeight());
			}
			return i;
		} finally {
			if (frame.getContentPane() != null) {
				frame.getContentPane().removeAll();
			}
			frame.dispose();
		}
	}

	private static void finalizeScreenshotGeneration(JComponent c, FlexoModelObject object) {
		ExternalWKFModule wkfModule = null;
		ExternalIEModule ieModule = null;
		ExternalDMModule dmModule = null;
		ExternalVEModule oeModule = null;
		if (object.getXMLResourceData() instanceof FlexoXMLSerializableObject) {
			((FlexoXMLSerializableObject) object.getXMLResourceData()).setIgnoreNotifications();
		}
		FlexoProject project = object.getProject();
		try {
			if (object instanceof AbstractActivityNode || object instanceof FlexoProcess || object instanceof LOOPOperator
					|| object instanceof RoleList || object instanceof FlexoWorkflow) {
				wkfModule = project.getModuleLoader() != null ? project.getModuleLoader().getWKFModuleInstance() : null;
			} else if (object instanceof IEWOComponent || object instanceof ComponentDefinition || object instanceof OperationNode) {
				ieModule = project.getModuleLoader() != null ? project.getModuleLoader().getIEModuleInstance() : null;
			} else if (object instanceof ERDiagram) {
				dmModule = project.getModuleLoader() != null ? project.getModuleLoader().getDMModuleInstance() : null;
			} /*else if (object instanceof Diagram) {
				oeModule = project.getModuleLoader() != null ? project.getModuleLoader().getVEModuleInstance() : null;
				}*/
		} catch (Throwable e) {
			logger.severe("Failed to generate screenshot for " + object);
			e.printStackTrace();
		} finally {
			if (wkfModule != null && c != null) {
				wkfModule.finalizeScreenshotGeneration(c);
			}
			if (ieModule != null) {
				ieModule.finalizeScreenshot();
			}
			if (dmModule != null) {
				dmModule.finalizeScreenshot();
			}
			if (oeModule != null) {
				oeModule.finalizeScreenshotGeneration();
			}
		}
	}

	public static ScreenshotImage makeImage(BufferedImage bi) {
		ScreenshotImage i = new ScreenshotImage();
		i.image = bi;
		i.trimInfo = new Rectangle(0, 0, bi.getWidth(), bi.getHeight());
		return i;
	}

	public static ScreenshotImage makeImage(BufferedImage bi, int left, int top, int width, int height) {
		ScreenshotImage i = new ScreenshotImage();
		i.image = bi.getSubimage(left, top, width, height);
		i.trimInfo = new Rectangle(left, top, width, height);
		return i;
	}

	public static ScreenshotImage trimImage(BufferedImage bi) {
		// Trim operation to remove white borders.
		int border = 10;
		int top = -1;
		int bottom = -1;
		int left = -1;
		int right = -1;
		for (int i = bi.getWidth() - 1; i > 0 && right == -1; i--) {
			for (int j = 0; j < bi.getHeight() && right == -1; j++) {
				int color = bi.getRGB(i, j);
				if (color != -1) {
					right = i;
				}
			}
		}

		for (int i = 0; i < bi.getWidth() && left == -1; i++) {
			for (int j = 0; j < bi.getHeight() && left == -1; j++) {
				int color = bi.getRGB(i, j);
				if (color != -1) {
					left = i;
				}
			}
		}
		for (int j = 0; j < bi.getHeight() && top == -1; j++) {
			for (int i = 0; i < bi.getWidth() && top == -1; i++) {

				int color = bi.getRGB(i, j);
				if (color != -1) {
					top = j;
				}
			}
		}

		for (int j = bi.getHeight() - 1; j > 0 && bottom == -1; j--) {
			for (int i = bi.getWidth() - 1; i > 0 && bottom == -1; i--) {

				int color = bi.getRGB(i, j);
				if (color != -1) {
					bottom = j;
				}
			}
		}
		left = Math.max(left - border, 0);
		top = Math.max(top - border, 0);
		right = Math.min(right - left + border, bi.getWidth() - 1 - left);
		bottom = Math.min(bottom - top + border, bi.getHeight() - 1 - top);
		ScreenshotImage i = new ScreenshotImage();
		i.image = bi.getSubimage(left, top, right, bottom);
		i.trimInfo = new Rectangle(left, top, right, bottom);
		return i;
	}

	/**
	 * @return
	 */
	private static ScreenshotImage getEmptyScreenshot() {
		File f = new FileResource("LatexExtras/EmptyScreenshot.jpg");
		if (f.exists()) {
			try {
				FileInputStream fis = new FileInputStream(f);
				BufferedImage bi = ImageIO.read(fis);
				ScreenshotImage i = new ScreenshotImage();
				i.image = bi;
				i.trimInfo = new Rectangle(0, 0, bi.getWidth(), bi.getHeight());
				return i;
			} catch (FileNotFoundException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("File " + f.getAbsolutePath() + " does not exist.");
				}
			} catch (IOException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Error trying to read file " + f.getAbsolutePath());
				}
			}
		}
		if (logger.isLoggable(Level.SEVERE)) {
			logger.severe("Cannot find " + f.getAbsolutePath() + " returning null");
		}
		return null;
	}

}
