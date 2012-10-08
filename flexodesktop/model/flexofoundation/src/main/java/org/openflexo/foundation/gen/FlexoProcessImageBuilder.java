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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.module.ModuleLoadingException;
import org.openflexo.module.external.ExternalWKFModule;
import org.openflexo.module.external.IModuleLoader;
import org.openflexo.swing.SwingUtils;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException;
import org.openflexo.ws.client.PPMWebService.PPMWebService_PortType;

public class FlexoProcessImageBuilder {

	private static final Logger logger = Logger.getLogger(FlexoProcessImageBuilder.class.getPackage().getName());

	private static void saveImageOfProcess(FlexoProcess process, File dest) {

		IModuleLoader moduleLoader = process.getProject().getModuleLoader();
		if (moduleLoader==null || !moduleLoader.isWKFLoaded()) {
			return;
		}
		ExternalWKFModule wkfModule = null;
		try {
			wkfModule = moduleLoader.getWKFModuleInstance();
		} catch (ModuleLoadingException e) {
			logger.warning("cannot load WKF module (and so can't create screenshot)." + e.getMessage());
			e.printStackTrace();
		}

		if (wkfModule == null) {
			return;
		}
		BufferedImage bi = ScreenshotGenerator.getImage(process).image;
		if (bi == null) {
			return;
		}
		try {
			if (!dest.exists()) {
				FileUtils.createNewFile(dest);
			}
			ImageIO.write(
					SwingUtils.scaleIt(bi, (int) (bi.getWidth() * wkfModule.getScreenshotQuality()),
							(int) (bi.getHeight() * wkfModule.getScreenshotQuality())), "jpg", dest);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getScreenshotFilename(FlexoProcess process) {
		return ToolBox.getJavaName(process.getURI()) + ".png";
	}

	private static File getImageFile(FlexoProcess process) {
		if (process == null) {
			return null;
		}
		if (process.isImported()) {
			return new File(process.getProject().getProcessSnapshotImportedDirectory(), getScreenshotFilename(process));
		}
		return new File(process.getProject().getProcessSnapshotLocalDirectory(), getScreenshotFilename(process));
	}

	public static void deleteSnapshot(FlexoProcess p) {
		File f = getImageFile(p);
		if (p == null || !f.exists()) {
			return;
		}
		f.delete();
	}

	public static void writeSnapshot(FlexoProcess process, byte[] img) throws IOException {
		if (img == null) {
			return;
		}
		if (process.isImported()) {
			File dest = getImageFile(process);
			if (dest.exists()) {
				dest.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(dest);
			try {
				fos.write(img);
				fos.flush();
			} finally {
				fos.close();
			}
		}
	}

	public static void writeSnapshot(final FlexoProcess process) {
		if (!process.getProject().isGenerateSnapshot()) {
			return;
		}
		if (!process.isImported()) {
			if (!SwingUtilities.isEventDispatchThread()) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						writeSnapshot(process);
					}
				});
				return;
			}
			File dest = getImageFile(process);
			saveImageOfProcess(process, dest);
		}
	}

	public static BufferedImage getSnapshot(FlexoProcess process) throws IOException {
		File src = getImageFile(process);
		if (src != null && src.exists()) {
			return ImageIO.read(src);
		}
		return null;
	}

	public static void startBackgroundDownloadOfSnapshots(Collection<FlexoProcess> processes, PPMWebService_PortType ws, String login,
			String pwd) {
		for (FlexoProcess p : processes) {
			try {
				byte[] imageBytes = ws.getScreenshoot(login, pwd, p.getURI());
				FlexoProcessImageBuilder.writeSnapshot(p, imageBytes);
			} catch (PPMWebServiceAuthentificationException e1) {
				e1.printStackTrace();
			} catch (RemoteException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void startBackgroundDownloadOfSnapshots(FlexoProcess fiProcess, PPMWebService_PortType webService, String login,
			String md5Password) {
		ArrayList<FlexoProcess> list = new ArrayList<FlexoProcess>();
		list.add(fiProcess);
		startBackgroundDownloadOfSnapshots(list, webService, login, md5Password);
	}
}
