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
package org.openflexo.fib.model;

import java.awt.Image;
import java.io.File;
import java.lang.reflect.Type;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class FIBImage extends FIBWidget {

	public static enum Parameters implements FIBModelAttribute
	{
		imageFile,
		align,
		imageWidth,
		imageHeight,
		sizeAdjustment
	}

	public static enum Align
	{
		left { @Override
		public int getAlign() { return SwingConstants.LEFT; }},
		right { @Override
		public int getAlign() { return SwingConstants.RIGHT; }},
		center { @Override
		public int getAlign() { return SwingConstants.CENTER; }};
		public abstract int getAlign();
	}
	
	public static enum SizeAdjustment
	{
		OriginalSize,
		FitToAvailableSize,
		FitToAvailableSizeRespectRatio,
		AdjustWidth,
		AdjustHeight,
		AdjustDimensions
	}
	
	private File imageFile;
	private Align align = Align.left;
	private Integer imageWidth;
	private Integer imageHeight;
	private SizeAdjustment sizeAdjustment = SizeAdjustment.OriginalSize;
	
	public FIBImage() 
	{
		super();
	}
	
	@Override
	public Type getDefaultDataClass() 
	{
		return Image.class;
	}

	public Align getAlign()
	{
		return align;
	}

	public void setAlign(Align align)
	{
		FIBAttributeNotification<Align> notification = requireChange(
				Parameters.align, align);
		if (notification != null) {
			this.align = align;
			hasChanged(notification);
		}
	}

	public File getImageFile()
	{
		return imageFile;
	}

	public void setImageFile(File imageFile)
	{
		FIBAttributeNotification<File> notification = requireChange(
				Parameters.imageFile, imageFile);
		if (notification != null) {
			this.imageFile = imageFile;
			hasChanged(notification);
		}
	}

	public SizeAdjustment getSizeAdjustment()
	{
		return sizeAdjustment;
	}

	public void setSizeAdjustment(SizeAdjustment sizeAdjustment)
	{
		FIBAttributeNotification<SizeAdjustment> notification = requireChange(
				Parameters.sizeAdjustment, sizeAdjustment);
		if (notification != null) {
			this.sizeAdjustment = sizeAdjustment;
			hasChanged(notification);
		}
	}

	public Integer getImageWidth()
	{
		return imageWidth;
	}

	public void setImageWidth(Integer imageWidth)
	{
		FIBAttributeNotification<Integer> notification = requireChange(
				Parameters.imageWidth, imageWidth);
		if (notification != null) {
			this.imageWidth = imageWidth;
			hasChanged(notification);
		}
	}

	public Integer getImageHeight()
	{
		return imageHeight;
	}

	public void setImageHeight(Integer imageHeight)
	{
		FIBAttributeNotification<Integer> notification = requireChange(
				Parameters.imageHeight, imageHeight);
		if (notification != null) {
			this.imageHeight = imageHeight;
			hasChanged(notification);
		}
	}

}
