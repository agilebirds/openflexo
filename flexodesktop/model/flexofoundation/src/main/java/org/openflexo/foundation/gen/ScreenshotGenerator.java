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
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.RoleList;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.LOOPOperator;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.module.external.ExternalDMModule;
import org.openflexo.module.external.ExternalIEModule;
import org.openflexo.module.external.ExternalModule;
import org.openflexo.module.external.ExternalModuleDelegater;
import org.openflexo.module.external.ExternalOEModule;
import org.openflexo.module.external.ExternalWKFModule;
import org.openflexo.swing.ImageUtils;
import org.openflexo.toolbox.FileResource;


//TODO : rename it into ScreenshotBuilder
public class ScreenshotGenerator
{
	public static class ScreenshotImage {
		public BufferedImage image;
		public Rectangle trimInfo;
	}

	private static final Logger logger = FlexoLogger.getLogger(ScreenshotGenerator.class.getPackage().getName());

	protected static final String BAD_FILE_NAME_CHARACTERS_REG_EXP = "[^-A-Za-z0-9]";

	protected static final Pattern BAD_FILE_NAME_CHARACTERS_PATTERN= Pattern.compile(BAD_FILE_NAME_CHARACTERS_REG_EXP);

	private static final String REPLACEMENT = "-";

	public static void main(String[] args)
	{
		String s = "ABSTRA\tCTACTIVITY\n-TestDM.BE\r\n\rGIN1062";
		System.out.println(s+"="+formatImageName(s));
	}

	public static String getScreenshotName(Object o)
	{
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
			return getImageNameForLoopOperator((LOOPOperator)o);
		} else if (o instanceof ERDiagram) {
			return getImageNameForERDiagram((ERDiagram)o);
		} else if (o instanceof RoleList) {
			return getImageNameForRoleList((RoleList)o);
		} else if (o instanceof FlexoWorkflow) {
			return getImageNameForWorkflow((FlexoWorkflow)o);
		} else if (o instanceof View) {
			return getImageNameForShema((View)o);
		}
		return null;
	}

	private static String getImageNameForWorkflow(FlexoWorkflow o) {
		return "Workflow";
	}

	private static String getImageNameForRoleList(RoleList o) {
		return "RoleList";
	}

	/**
	 * @param definition
	 * @return
	 */
	private static String getImageNameForComponent(ComponentDefinition definition)
	{
		return getImageNameForComponent(definition.getComponentName());
	}

	/**
	 * @param definition
	 * @return
	 */
	private static String getImageNameForComponent(String componentName)
	{
		return IEWOComponent.getTypeName() + "-" + formatImageName(componentName);
	}

	/**
	 * @param node
	 * @return
	 */
	private static String getImageNameForActivity(AbstractActivityNode node)
	{
		return getImageNameForActivity(node, node.getName() + node.getFlexoID());
	}

	/**
	 * @param node
	 * @return
	 */
	private static String getImageNameForActivity(AbstractActivityNode node, String activityName)
	{
		String imageName = formatImageName(node.getProcess().getName() + "-" + activityName);
		return AbstractActivityNode.getTypeName() + "-" + imageName;
	}

	private static String getImageNameForLoopOperator(LOOPOperator node)
	{
		String imageName = formatImageName(node.getProcess().getName() + "-" + node.getName() + node.getFlexoID());
		return LOOPOperator.getTypeName() + "-" + imageName;
	}

	private static String getImageNameForERDiagram(ERDiagram node)
	{
		String imageName = formatImageName(node.getName() + "-"  + node.getFlexoID());
		return "ERDiagram-" + imageName;
	}
	/**
	 * @param node
	 * @return
	 */
	private static String getImageNameForOperation(OperationNode node)
	{
		return getImageNameForOperation(node.getProcess().getName() + "-" + node.getName() + node.getFlexoID());
	}

	/**
	 * @param node
	 * @return
	 */
	private static String getImageNameForOperation(String operationName)
	{
		return OperationNode.getTypeName() + "-" + formatImageName(operationName);
	}

	/**
	 * @param process
	 * @return
	 */
	private static String getImageNameForProcess(FlexoProcess process)
	{
		return getImageNameForProcess(process.getName() + process.getFlexoID());
	}

	/**
	 * @param process
	 * @return
	 */
	private static String getImageNameForProcess(String processName)
	{
		return FlexoProcess.getTypeName() + "-" + formatImageName(processName);
	}

	/**
	 * @param process
	 * @return
	 */
	private static String getImageNameForShema(View shema)
	{
		return getImageNameForShema(shema.getName() + shema.getFlexoID());
	}

	/**
	 * @param process
	 * @return
	 */
	private static String getImageNameForShema(String shemaName)
	{
		return View.getTypeName() + "-" + formatImageName(shemaName);
	}

	private static String formatImageName(String imageName)
	{
		if (imageName == null) {
			return null;
		}
		return BAD_FILE_NAME_CHARACTERS_PATTERN.matcher(imageName).replaceAll(REPLACEMENT);
	}

	public static ScreenshotImage getImage(FlexoModelObject object)
	{
		if (object==null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Object is null: cannot generate screenshot");
			}
			return getEmptyScreenshot();
		}
		ScreenshotImage i = null;
		JFrame frame = null;
		ExternalWKFModule wkfModule = null;
		ExternalIEModule ieModule = null;
		ExternalDMModule dmModule = null;
		ExternalOEModule oeModule = null;
		if (object.getXMLResourceData() instanceof FlexoXMLSerializableObject) {
			((FlexoXMLSerializableObject)object.getXMLResourceData()).setIgnoreNotifications();
		}
		try {
			BufferedImage bi = null;
			JComponent c = null;
			if (object instanceof AbstractActivityNode || object instanceof FlexoProcess
					|| object instanceof LOOPOperator || object instanceof RoleList
					|| object instanceof FlexoWorkflow) {
				wkfModule = ExternalModuleDelegater.getModuleLoader() != null ? ExternalModuleDelegater.getModuleLoader()
						.getWKFModuleInstance() : null;
			}
			else if (object instanceof IEWOComponent
					|| object instanceof ComponentDefinition
					|| object instanceof OperationNode) {
				ieModule = ExternalModuleDelegater.getModuleLoader() != null ? ExternalModuleDelegater.getModuleLoader()
						.getIEModuleInstance() : null;
			}
			else if (object instanceof ERDiagram) {
				dmModule = ExternalModuleDelegater.getModuleLoader() != null ? ExternalModuleDelegater.getModuleLoader()
						.getDMModuleInstance() : null;
			}
			else if (object instanceof View) {
				oeModule = ExternalModuleDelegater.getModuleLoader() != null ? ExternalModuleDelegater.getModuleLoader()
						.getOEModuleInstance() : null;
			}
			if (wkfModule != null || ieModule != null || dmModule!=null || oeModule!=null) {
				ExternalModule module = ieModule != null ? ieModule : wkfModule!=null ? wkfModule : dmModule!=null ? dmModule : oeModule;
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
						c = dmModule.createScreenshotForObject((ERDiagram)object);
					} else if (object instanceof View) {
						c = oeModule.createScreenshotForShema((View)object);
					}
					if (c == null) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Could not retrieve printable component for " + object.getFullyQualifiedName());
						}
						return getEmptyScreenshot();
					}
					c.setOpaque(true);
					c.setBackground(Color.WHITE);
					frame = new JFrame();
					frame.setBackground(Color.WHITE);
					frame.setUndecorated(true);
					frame.getContentPane().add(c);
					frame.pack();
					bi = ImageUtils.createImageFromComponent(c);
					if (wkfModule != null) {
						i = trimImage(bi);
					} else if (ieModule != null) {
						i = new ScreenshotImage();
						i.image = bi;
						i.trimInfo = new Rectangle(0,0,bi.getWidth(),bi.getHeight());
					} else if (dmModule != null) {
						i = new ScreenshotImage();
						i.image = bi;
						i.trimInfo = new Rectangle(0,0,bi.getWidth(),bi.getHeight());
					} else if (oeModule != null) {
						i = new ScreenshotImage();
						i.image = bi;
						i.trimInfo = new Rectangle(0,0,bi.getWidth(),bi.getHeight());
					}
				}
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError mem) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("I ran out of memory: I'm dead and cannot generate in memory screenshots!!!");
			}
		} finally {
			if (frame!=null) {
				if (frame.getContentPane()!=null) {
					frame.getContentPane().removeAll();
				}
				frame.dispose();
			}
			if (wkfModule!=null) {
				wkfModule.finalizeScreenshotGeneration();
			}
			if (ieModule!=null) {
				ieModule.finalizeScreenshot();
			}
			if (oeModule!=null) {
				oeModule.finalizeScreenshotGeneration();
			}
			if (object.getXMLResourceData() instanceof FlexoXMLSerializableObject) {
				((FlexoXMLSerializableObject)object.getXMLResourceData()).resetIgnoreNotifications();
			}
		}
		if (i == null) {
			return getEmptyScreenshot();
		}
		return i;
	}

	public static ScreenshotImage makeImage(BufferedImage bi)
	{
		ScreenshotImage i = new ScreenshotImage();
		i.image = bi;
		i.trimInfo = new Rectangle(0,0,bi.getWidth(),bi.getHeight());
		return i;
	}

	public static ScreenshotImage trimImage(BufferedImage bi)
	{
		// Trim operation to remove white borders.
		int border = 10;
		int top = -1;
		int bottom =-1;
		int left = -1;
		int right = -1;
		for (int i=bi.getWidth()-1;i>0 && right==-1;i--) {
			for (int j=0;j<bi.getHeight() && right==-1;j++) {
				int color = bi.getRGB(i, j);
				if (color!=-1) {
					right = i;
				}
			}
		}

		for (int i=0;i<bi.getWidth() && left==-1;i++) {
			for (int j=0;j<bi.getHeight() && left==-1;j++) {
				int color = bi.getRGB(i, j);
				if (color!=-1) {
					left = i;
				}
			}
		}
		for (int j=0;j<bi.getHeight() && top==-1;j++) {
			for (int i=0;i<bi.getWidth() && top==-1;i++) {

				int color = bi.getRGB(i, j);
				if (color!=-1) {
					top = j;
				}
			}
		}

		for (int j=bi.getHeight()-1;j>0 && bottom==-1;j--) {
			for (int i=bi.getWidth()-1;i>0 && bottom==-1;i--) {

				int color = bi.getRGB(i, j);
				if (color!=-1) {
					bottom = j;
				}
			}
		}
		left = Math.max(left-border,0);
		top = Math.max(top-border,0);
		right = Math.min(right-left+border,bi.getWidth()-1-left);
		bottom = Math.min(bottom-top+border,bi.getHeight()-1-top);
		ScreenshotImage i = new ScreenshotImage();
		i.image = bi.getSubimage(left, top, right,bottom);
		i.trimInfo = new Rectangle(left, top, right,bottom);
		return i;
	}

	/**
	 * @return
	 */
	private static ScreenshotImage getEmptyScreenshot()
	{
		File f = new FileResource("LatexExtras/EmptyScreenshot.jpg");
		if (f.exists()) {
			try {
				FileInputStream fis = new FileInputStream(f);
				BufferedImage bi = ImageIO.read(fis);
				ScreenshotImage i = new ScreenshotImage();
				i.image = bi;
				i.trimInfo = new Rectangle(0,0,bi.getWidth(),bi.getHeight());
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
			logger.severe("Cannot find "+f.getAbsolutePath()+" returning null");
		}
		return null;
	}

}
