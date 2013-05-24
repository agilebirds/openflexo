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
package org.openflexo.print;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.foundation.gen.ScreenshotGenerator;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.view.FlexoDialog;
import org.openflexo.view.controller.FlexoController;

public class PrintPreviewDialog extends FlexoDialog {

	protected static final Logger logger = Logger.getLogger(PrintPreviewDialog.class.getPackage().getName());

	public enum ReturnedStatus {
		CONTINUE_PRINTING, CANCELLED
	};

	protected PrintManagingController _controller;
	protected ReturnedStatus status = ReturnedStatus.CONTINUE_PRINTING;
	protected FlexoPrintableComponent _printableComponent;

	private JTextField scaleTF;
	private JLabel pagesLabel;
	private JScrollPane scrollPane;

	public PrintPreviewDialog(PrintManagingController controller, FlexoPrintableComponent printableProcessView) {
		super(controller.getFlexoFrame(), true);
		_controller = controller;
		_printableComponent = printableProcessView;

		setTitle(FlexoLocalization.localizedForKey("print_preview"));

		JPanel topPanel = new JPanel(new FlowLayout());
		JButton plusScale = new JButton("+");
		JButton minusScale = new JButton("-");

		final JSlider slider = new JSlider(SwingConstants.HORIZONTAL, 0, 500, 100);
		slider.setMajorTickSpacing(100);
		slider.setMinorTickSpacing(20);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);

		pagesLabel = new JLabel("?? x ?? pages");
		scaleTF = new JTextField("" + (int) (printableProcessView.getPrintableDelegate().getScale() * 100) + "%");

		topPanel.add(minusScale);
		topPanel.add(slider);
		topPanel.add(scaleTF);
		topPanel.add(plusScale);
		topPanel.add(pagesLabel);

		final ChangeListener sliderChangeListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (slider.getValue() > 0) {
					_printableComponent.getPrintableDelegate().setScale((double) slider.getValue() / 100);
					update();
					_printableComponent.getPrintableDelegate().refresh();
				}
			}
		};

		plusScale.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_printableComponent.getPrintableDelegate().setScale(_printableComponent.getPrintableDelegate().getScale() * 1.1);
				update();
				slider.removeChangeListener(sliderChangeListener);
				slider.setValue((int) (_printableComponent.getPrintableDelegate().getScale() * 100));
				slider.addChangeListener(sliderChangeListener);
				_printableComponent.getPrintableDelegate().refresh();
			}
		});
		minusScale.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_printableComponent.getPrintableDelegate().setScale(_printableComponent.getPrintableDelegate().getScale() * 0.9);
				update();
				slider.removeChangeListener(sliderChangeListener);
				slider.setValue((int) (_printableComponent.getPrintableDelegate().getScale() * 100));
				slider.addChangeListener(sliderChangeListener);
				_printableComponent.getPrintableDelegate().refresh();
			}
		});

		slider.addChangeListener(sliderChangeListener);

		printableProcessView.getPrintableDelegate().preview(controller.getPrintManager().getPageFormat());
		scrollPane = new JScrollPane((Component) printableProcessView);
		scrollPane.getViewport().setPreferredSize(new Dimension(700, 500));

		final JSlider previewScaleSlider = new JSlider(SwingConstants.VERTICAL, 0, 200, 100);
		previewScaleSlider.setMajorTickSpacing(50);
		previewScaleSlider.setMinorTickSpacing(10);
		previewScaleSlider.setPaintTicks(true);
		previewScaleSlider.setPaintLabels(true);

		final ChangeListener previewScaleChangeListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (previewScaleSlider.getValue() > 0) {
					_printableComponent.getPrintableDelegate().setPreviewScale((double) previewScaleSlider.getValue() / 100);
					update();
					_printableComponent.getPrintableDelegate().refresh();
				}
			}
		};
		previewScaleSlider.addChangeListener(previewScaleChangeListener);

		final JCheckBox showPages = new JCheckBox(FlexoLocalization.localizedForKey("show_pages"), _printableComponent
				.getPrintableDelegate().showPages());
		showPages.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_printableComponent.getPrintableDelegate().setShowPages(showPages.isSelected());
				update();
				_printableComponent.getPrintableDelegate().refresh();
			}
		});
		final JCheckBox showTitle = new JCheckBox(FlexoLocalization.localizedForKey("show_title"), _printableComponent
				.getPrintableDelegate().showTitles());
		showTitle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_printableComponent.getPrintableDelegate().setShowTitles(showTitle.isSelected());
				update();
				_printableComponent.getPrintableDelegate().refresh();
			}
		});
		final JTextField titleTF = new JTextField(30);
		titleTF.setText(_printableComponent.getPrintableDelegate().getPrintTitle());
		titleTF.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_printableComponent.getPrintableDelegate().setPrintTitle(titleTF.getText());
				update();
				_printableComponent.getPrintableDelegate().refresh();
			}
		});

		JPanel paramsPanel = new JPanel(new FlowLayout());
		paramsPanel.add(showPages);
		paramsPanel.add(showTitle);
		paramsPanel.add(titleTF);

		JPanel centerPane = new JPanel(new BorderLayout());
		centerPane.add(previewScaleSlider, BorderLayout.WEST);
		centerPane.add(scrollPane, BorderLayout.CENTER);
		centerPane.add(paramsPanel, BorderLayout.SOUTH);

		JPanel bottomPanel = new JPanel(new FlowLayout());
		JButton printButton = new JButton();
		printButton.setText(FlexoLocalization.localizedForKey("print", printButton));
		printButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				status = ReturnedStatus.CONTINUE_PRINTING;
				print();
				dispose();
			}
		});

		JButton saveAsJPGButton = new JButton();
		saveAsJPGButton.setText(FlexoLocalization.localizedForKey("save_as_image", saveAsJPGButton));
		saveAsJPGButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				status = ReturnedStatus.CANCELLED;
				saveAsJpeg();
				dispose();
			}
		});

		JButton cancelButton = new JButton();
		cancelButton.setText(FlexoLocalization.localizedForKey("cancel", cancelButton));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				status = ReturnedStatus.CANCELLED;
				dispose();
			}
		});

		JButton pageSetupButton = new JButton();
		pageSetupButton.setText(FlexoLocalization.localizedForKey("page_setup", pageSetupButton));
		pageSetupButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_printableComponent.getPrintableDelegate().setPageFormat(_controller.getPrintManager().pageSetup());
				update();
				slider.removeChangeListener(sliderChangeListener);
				slider.setValue((int) (_printableComponent.getPrintableDelegate().getScale() * 100));
				slider.addChangeListener(sliderChangeListener);
				_printableComponent.getPrintableDelegate().refresh();
			}
		});

		JButton fitToPageButton = new JButton();
		fitToPageButton.setText(FlexoLocalization.localizedForKey("fit_to_page", fitToPageButton));
		fitToPageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_printableComponent.getPrintableDelegate().fitToPage();
				update();
				slider.removeChangeListener(sliderChangeListener);
				slider.setValue((int) (_printableComponent.getPrintableDelegate().getScale() * 100));
				slider.addChangeListener(sliderChangeListener);
				_printableComponent.getPrintableDelegate().refresh();
			}
		});

		bottomPanel.add(printButton);
		bottomPanel.add(saveAsJPGButton);
		bottomPanel.add(cancelButton);
		bottomPanel.add(pageSetupButton);
		bottomPanel.add(fitToPageButton);

		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(topPanel, BorderLayout.NORTH);
		contentPane.add(centerPane, BorderLayout.CENTER);
		contentPane.add(bottomPanel, BorderLayout.SOUTH);
		getRootPane().setDefaultButton(printButton);
		getContentPane().add(contentPane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		validate();
		pack();
		update();
		setVisible(true);
	}

	protected void update() {
		scaleTF.setText("" + (int) (_printableComponent.getPrintableDelegate().getScale() * 100) + "%");
		pagesLabel.setText(_printableComponent.getPrintableDelegate().getWidthPageNb() + " x "
				+ _printableComponent.getPrintableDelegate().getHeightPageNb() + " " + FlexoLocalization.localizedForKey("pages"));
		scrollPane.getViewport().reshape(scrollPane.getViewport().getViewPosition().x, scrollPane.getViewport().getViewPosition().y,
				_printableComponent.getWidth(), _printableComponent.getHeight());
		scrollPane.revalidate();
		scrollPane.repaint();
	}

	public ReturnedStatus getStatus() {
		return status;
	}

	public void print() {
		_controller.getPrintManager().printPageable(_printableComponent.getPrintableDelegate());
	}

	public void saveAsJpeg() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		chooser.setDialogTitle(FlexoLocalization.localizedForKey("save_as_image", chooser));

		File dest = null;
		int returnVal = chooser.showSaveDialog(null);
		if (returnVal == JFileChooser.CANCEL_OPTION) {
			return;
		}
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			if (isValidProjectName(chooser.getSelectedFile().getName())) {
				dest = chooser.getSelectedFile();
				if (!dest.getName().toLowerCase().endsWith(".png")) {
					dest = new File(dest.getAbsolutePath() + ".png");
				}
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Invalid file name. The following characters are not allowed: "
							+ FileUtils.BAD_CHARACTERS_FOR_FILE_NAME_REG_EXP);
				}
				FlexoController.notify(FlexoLocalization.localizedForKey("file_name_cannot_contain_\\___&_#_{_}_[_]_%_~"));
			}
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("No project specified !");
			}
		}
		if (dest == null) {
			return;
		}

		ScreenshotGenerator.ScreenshotImage image = ScreenshotGenerator.getImage(getPrintableComponent().getFlexoModelObject());

		try {
			if (!dest.exists()) {
				FileUtils.createNewFile(dest);
			}
			ImageIO.write(image.image, "png", dest);
		} catch (Exception e) {
			e.printStackTrace();
			FlexoController.showError(e.getMessage());
		}
	}

	private static boolean isValidProjectName(String absolutePath) {
		return absolutePath != null && absolutePath.trim().length() > 0
				&& !FileUtils.BAD_CHARACTERS_FOR_FILE_NAME_PATTERN.matcher(absolutePath).find();
	}

	public FlexoPrintableComponent getPrintableComponent() {
		return _printableComponent;
	}
}
