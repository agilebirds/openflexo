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
package org.openflexo.components.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProject.ImageFile;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.TextFieldCustomPopup;
import org.openflexo.swing.layout.WrapLayout;

/**
 * Widget allowing to select a DocItem while browsing the DocResourceCenter
 * 
 * @author sguerin
 * 
 */
public class ImageFileSelector extends TextFieldCustomPopup<ImageFile> {

	private static final Color SELECTION_COLOR = new Color(184, 207, 229);

	public interface ImageImporter {
		public void importImage(ActionEvent e);
	}

	protected static final String EMPTY_STRING = "";

	private FlexoProject project;

	private ImageFile revertValue;

	protected ImageImporter importer;
	protected boolean importedImageOnly;

	public ImageFileSelector(FlexoProject project, ImageImporter importer, ImageFile docItem, boolean importedImageOnly) {
		super(docItem);
		this.project = project;
		this.importer = importer;
		this.importedImageOnly = importedImageOnly;
		this.revertValue = docItem;
	}

	public FlexoProject getProject() {
		return project;
	}

	public void setProject(FlexoProject project) {
		this.project = project;
	}

	protected class ImageSelectorPanel extends ResizablePanel {
		private JPanel imagePanel;

		private JButton applyButton;
		private JButton cancelButton;
		private JButton resetButton;
		private JButton importButton;

		protected ImageSelectorPanel() {
			super();
			setLayout(new BorderLayout());
			imagePanel = new JPanel(new WrapLayout());
			JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			applyButton = new JButton(FlexoLocalization.localizedForKey("ok"));
			applyButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					apply();
				}
			});
			cancelButton = new JButton(FlexoLocalization.localizedForKey("cancel"));
			cancelButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					cancel();
				}
			});
			resetButton = new JButton(FlexoLocalization.localizedForKey("reset"));
			resetButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setEditedObject(null);
					apply();
				}
			});
			if (importer != null) {
				importButton = new JButton(FlexoLocalization.localizedForKey("import"));
				importButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						importer.importImage(e);
						updateImagePanel();
						// Just to be extra sure, but the next lines should not be useful.
						if (!popupIsShown()) {
							openPopup();
						}
					}
				});
			}
			buttonPanel.add(applyButton);
			buttonPanel.add(cancelButton);
			buttonPanel.add(resetButton);
			if (importButton != null) {
				buttonPanel.add(importButton);
			}
			updateImagePanel();
			final JScrollPane scroll = new JScrollPane(imagePanel);
			scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scroll.setPreferredSize(new Dimension(280, 340));
			if (scroll.getVerticalScrollBar() != null) {
				scroll.getVerticalScrollBar().setUnitIncrement(10);
				scroll.getVerticalScrollBar().setBlockIncrement(30);
			}
			scroll.validate();
			add(scroll, BorderLayout.CENTER);
			add(buttonPanel, BorderLayout.SOUTH);
			validate();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					Component[] c = imagePanel.getComponents();
					for (int i = 0; i < c.length; i++) {
						ImageView component = (ImageView) c[i];
						if (component.file == getEditedObject()) {
							component.scrollRectToVisible(new Rectangle(component.getSize()));
							break;
						}
					}

				}
			});
		}

		private void updateImagePanel() {
			imagePanel.removeAll();
			for (ImageFile file : getProject().getAvailableImageFiles()) {
				if (importedImageOnly && !file.isImported()) {
					continue;
				}
				imagePanel.add(new ImageView(file));
			}
			imagePanel.validate();
		}

		@Override
		public Dimension getDefaultSize() {
			return new Dimension(340, 390);
		}

		private class ImageView extends JPanel implements MouseListener {

			private ImageIcon image;
			private int imageWidth;
			private int imageHeight;
			private double imageRatio;

			private ImageFile file;

			protected ImageView(ImageFile file) {
				this.file = file;
				image = new ImageIcon(file.getImageFile().getAbsolutePath());
				imageWidth = image.getIconWidth();
				imageHeight = image.getIconHeight();
				imageRatio = imageWidth / imageHeight;
				setToolTipText(file.getBeautifiedImageName());
				addMouseListener(this);
				setOpaque(true);
				setPreferredSize(new Dimension(80, 80));
			}

			public ImageFile getFile() {
				return file;
			}

			private boolean isSelected = false;

			@Override
			public void paint(Graphics g) {
				super.paint(g);
				Rectangle r = getBounds();
				int x, y, width, height;
				double ratio = r.width / r.height;
				if (r.width > imageWidth && r.height > imageHeight) {
					height = imageHeight;
					width = imageWidth;
					x = (r.width - width) / 2;
					y = (r.height - height) / 2;
				} else {
					if (imageRatio < ratio) {
						y = 0;
						height = r.height;
						width = imageWidth * r.height / imageHeight;
						x = (r.width - width) / 2;
					} else if (imageRatio > ratio) {
						x = 0;
						width = r.width;
						height = imageHeight * r.width / imageWidth;
						y = (r.height - height) / 2;
					} else {
						x = 1;
						y = 1;
						width = r.width - 1;
						height = r.height - 1;
					}
				}
				g.drawImage(image.getImage(), x, y, width, height, null);
			}

			@Override
			public Color getBackground() {
				if (isSelected || getEditedObject() == file) {
					Color c = UIManager.getDefaults().getColor("TextField.selectionBackground");
					if (c == null) {
						c = SELECTION_COLOR;
					}
					return c;
				} else {
					return super.getBackground();
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					setEditedObject(file);
					imagePanel.repaint();
				} else {
					setEditedObject(file);
					apply();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				isSelected = true;
				imagePanel.repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				isSelected = false;
				imagePanel.repaint();
			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}
		}

	}

	public ImageFile getRevertValue() {
		return revertValue;
	}

	@Override
	public void setRevertValue(ImageFile oldValue) {
		this.revertValue = oldValue;
	}

	@Override
	public void apply() {
		setRevertValue(getEditedObject());
		closePopup();
		super.apply();
	}

	@Override
	public void cancel() {
		setEditedObject(revertValue);
		closePopup();
		super.cancel();
	}

	@Override
	public void closePopup() {
		super.closePopup();
		deletePopup();
	}

	@Override
	public String renderedString(ImageFile editedObject) {
		if (editedObject == null) {
			return EMPTY_STRING;
		}
		return editedObject.getImageName();
	}

	@Override
	public void updateCustomPanel(ImageFile editedObject) {

	}

	@Override
	protected ResizablePanel createCustomPanel(ImageFile editedObject) {
		return new ImageSelectorPanel();
	}

}
