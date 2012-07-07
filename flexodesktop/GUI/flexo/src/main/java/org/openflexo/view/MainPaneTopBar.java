package org.openflexo.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openflexo.icon.IconLibrary;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.ModuleLoadingException;
import org.openflexo.toolbox.PropertyChangeListenerRegistrationManager;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;
import org.openflexo.view.controller.model.RootControllerModel;

public class MainPaneTopBar extends JPanel {

	private PropertyChangeListenerRegistrationManager registrationManager;

	private RootControllerModel model;

	private JPanel left;
	private JPanel center;
	private JPanel right;

	private JComponent header;

	private JButton leftViewToggle;

	private JButton rightViewToggle;

	public MainPaneTopBar(RootControllerModel model) {
		this.model = model;
		registrationManager = new PropertyChangeListenerRegistrationManager();
		setLayout(new BorderLayout());
		add(left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)), BorderLayout.WEST);
		add(center = new JPanel());
		add(right = new JPanel(), BorderLayout.EAST);
		initModules();
		initNavigationControls();
		initModuleViewTabHeaders();
		initPerspectives();
		initLeftRightViewVisibilityControls();
	}

	public void delete() {
		registrationManager.delete();
	}

	private void initModules() {
		for (final Module module : model.getModuleLoader().getAvailableModules()) {
			final JButton button = new JButton(module.getMediumIcon());
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						model.getModuleLoader().switchToModule(module);
					} catch (ModuleLoadingException e1) {
						e1.printStackTrace();
						FlexoController.notify(e1.getLocalizedMessage());
					}
				}
			});
			PropertyChangeListener listener = new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					button.setIcon(model.getModuleLoader().isLoaded(module) ? module.getMediumIconWithHover() : module.getMediumIcon());
					button.setEnabled(model.getModuleLoader().isActive(module));
				}
			};
			registrationManager.new PropertyChangeListenerRegistration(ModuleLoader.ACTIVE_MODULE, listener, model.getModuleLoader());
			registrationManager.new PropertyChangeListenerRegistration(ModuleLoader.MODULE_ACTIVATED, listener, model.getModuleLoader());
			registrationManager.new PropertyChangeListenerRegistration(ModuleLoader.MODULE_LOADED, listener, model.getModuleLoader());
			registrationManager.new PropertyChangeListenerRegistration(ModuleLoader.MODULE_UNLOADED, listener, model.getModuleLoader());
			left.add(button);
		}
	}

	private void initNavigationControls() {
		final JLabel backwardButton = new JLabel(IconLibrary.NAVIGATION_BACKWARD_ICON);
		final JLabel forwardButton = new JLabel(IconLibrary.NAVIGATION_FORWARD_ICON);
		final JLabel upButton = new JLabel(IconLibrary.NAVIGATION_UP_ICON);
		registrationManager.new PropertyChangeListenerRegistration(RootControllerModel.CURRENT_LOCATION, new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				updateNavigationControlState(backwardButton, forwardButton, upButton);
			}

		}, model);
		left.add(new JLabel(IconLibrary.NAVIGATION_CLOSE_LEFT));
		left.add(backwardButton);
		left.add(new JLabel(IconLibrary.NAVIGATION_SPACER));
		left.add(upButton);
		left.add(new JLabel(IconLibrary.NAVIGATION_SPACER));
		left.add(forwardButton);
		left.add(new JLabel(IconLibrary.NAVIGATION_CLOSE_RIGHT));
		updateNavigationControlState(backwardButton, forwardButton, upButton);
	}

	protected void updateNavigationControlState(final JLabel backwardButton, final JLabel forwardButton, final JLabel upButton) {
		backwardButton.setEnabled(model.canGoBackward());
		forwardButton.setEnabled(model.canGoForward());
		upButton.setEnabled(model.canGoUp());
	}

	private void initModuleViewTabHeaders() {

	}

	private void initPerspectives() {
		right.add(new JLabel(IconLibrary.NAVIGATION_CLOSE_LEFT));
		boolean needSpacer = false;
		for (final FlexoPerspective p : model.getPerspectives()) {
			final JLabel button = new JLabel(p.getActiveIcon());
			registrationManager.new PropertyChangeListenerRegistration(RootControllerModel.CURRENT_LOCATION, new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					updateIconForButtonPerspective(button, p);
				}
			}, model);
			updateIconForButtonPerspective(button, p);
			if (needSpacer) {
				right.add(new JLabel(IconLibrary.NAVIGATION_SPACER));
			}
			right.add(button);
			needSpacer = true;
		}
		right.add(new JLabel(IconLibrary.NAVIGATION_CLOSE_RIGHT));
	}

	protected void updateIconForButtonPerspective(final JLabel buttonPerspective, final FlexoPerspective p) {
		buttonPerspective.setIcon(model.getCurrentPerspective() == p ? p.getSelectedIcon() : p.getActiveIcon());
	}

	private void initLeftRightViewVisibilityControls() {
		leftViewToggle = getToggleVisibilityButton();
		left.add(leftViewToggle, 0);
		registrationManager.new PropertyChangeListenerRegistration(RootControllerModel.LEFT_VIEW_VISIBLE, new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				updateLeftViewToggleIcon();
			}
		}, model);
		updateLeftViewToggleIcon();
		rightViewToggle = getToggleVisibilityButton();
		right.add(rightViewToggle);
		registrationManager.new PropertyChangeListenerRegistration(RootControllerModel.RIGHT_VIEW_VISIBLE, new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				updateRightViewToggleIcon();
			}
		}, model);
		updateRightViewToggleIcon();

	}

	protected void updateLeftViewToggleIcon() {
		leftViewToggle.setIcon(model.isLeftViewVisible() ? IconLibrary.TOGGLE_ARROW_TOP_ICON : IconLibrary.TOGGLE_ARROW_BOTTOM_ICON);
	}

	protected void updateRightViewToggleIcon() {
		rightViewToggle.setIcon(model.isRightViewVisible() ? IconLibrary.TOGGLE_ARROW_TOP_ICON : IconLibrary.TOGGLE_ARROW_BOTTOM_ICON);
	}

	private JButton getToggleVisibilityButton() {
		final JButton button = new JButton(IconLibrary.TOGGLE_ARROW_TOP_ICON);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (button.getIcon() == IconLibrary.TOGGLE_ARROW_TOP_ICON) {
					button.setIcon(IconLibrary.TOGGLE_ARROW_TOP_SELECTED_ICON);
				} else {
					button.setIcon(IconLibrary.TOGGLE_ARROW_BOTTOM_SELECTED_ICON);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (button.getIcon() == IconLibrary.TOGGLE_ARROW_TOP_SELECTED_ICON) {
					button.setIcon(IconLibrary.TOGGLE_ARROW_TOP_ICON);
				} else {
					button.setIcon(IconLibrary.TOGGLE_ARROW_BOTTOM_ICON);
				}
			}
		});
		return button;
	}

	public void setHeader(JComponent header) {
		if (this.header != header) {
			if (this.header != null) {
				right.remove(this.header);
				right.revalidate();
			}
			this.header = header;
			if (header != null) {
				right.add(header, 0);
				right.revalidate();
			}
		}
	}

	public void setLeftViewToggle(boolean visible) {
		leftViewToggle.setVisible(visible);
		left.revalidate();
	}

	public void setRightViewToggle(boolean visible) {
		rightViewToggle.setVisible(visible);
		right.revalidate();
	}

}
