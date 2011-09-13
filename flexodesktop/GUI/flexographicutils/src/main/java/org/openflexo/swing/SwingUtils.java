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
package org.openflexo.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;

public class SwingUtils {

	/**
	 * Move the specified window to the centre of the screen. It is the
	 * programmer's responsibility to ensure that the window fits on the screen.
	 * 
	 * @param win
	 *            The window to be moved
	 */
	public static void centerWindow(Window win)
	{
	    Toolkit tk = Toolkit.getDefaultToolkit();
	
	    // If the window is maximized then we can't center it
	    if (win instanceof Frame) {
	        Frame frame = (Frame) win;
	        if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) != 0) {
	            return;
	        }
	    }
	
	    Dimension screenDim = tk.getScreenSize();
	    Dimension windowDim = win.getSize();
	
	    // If the window is not maximized but is the entire size of the screen
	    // then we can't center it.
	    if (windowDim.equals(screenDim)) {
	        return;
	    }
	
	    // Center Frame, Dialogue or Window on screen
	    int x = (screenDim.width - win.getSize().width) / 2;
	    int y = (screenDim.height - win.getSize().height) / 2;
	    win.setLocation(x, y);
	}

	public static BufferedImage scaleIt(BufferedImage image,int width, int height){
		// Create new (blank) image of required (scaled) size

		BufferedImage scaledImage = new BufferedImage(
		   width, height, BufferedImage.TYPE_INT_RGB);

		// Paint scaled version of image to new image

		Graphics2D graphics2D = scaledImage.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.drawImage(image, 0, 0, width, height, null);

		// clean up

		graphics2D.dispose();
		return scaledImage;
	}
	
	public static String getComponentPath(Component c)
	{
		StringBuffer sb = new StringBuffer();
		Component current = c;
		boolean isFirst = true;
		while (current.getParent() != null) {
			sb.insert(0,(isFirst?"":".")+current.getClass().getSimpleName());
			isFirst = false;
			current = current.getParent();
		}
		return sb.toString();
	}
	
	public static boolean isComponentContainedInContainer(Component c, Container container)
	{
		Component current = c;
		while (current.getParent() != null) {
			if (current == container) return true;
			current = current.getParent();
		}
		return false;
	}
	

}
