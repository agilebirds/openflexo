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
package org.openflexo.view;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JDialog;

import org.openflexo.swing.DialogFactory;

public class FlexoDialog extends JDialog {

	public FlexoDialog() throws HeadlessException {
		this(null);
	}

	public FlexoDialog(Window owner) throws HeadlessException {
		super(FlexoFrame.getOwner(owner));
		init();
	}

	public FlexoDialog(Window owner, boolean modal) throws HeadlessException {
		super(FlexoFrame.getOwner(owner));
		setModal(modal);
		init();
	}

	public FlexoDialog(Window owner, String title) throws HeadlessException {
		super(FlexoFrame.getOwner(owner), title);
		init();
	}

	public FlexoDialog(Window owner, String title, boolean modal) throws HeadlessException {
		super(FlexoFrame.getOwner(owner), title);
		setModal(modal);
		init();
	}

	private void init() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
					dispose();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {

			}
		});
		// (SGU) Very important !!!
		// Don't put this flag to true again, as it lead to very severe issues
		// while managing popup menu (see CustomPopup) which often appear below
		// JDialog
		// setAlwaysOnTop(true);
		setJMenuBar(null);
	}

	@Override
	public void show() {
		centerDialog();
		/*if (FlexoModule.getActiveModule()!=null)
			FlexoModule.getActiveModule().getFlexoController().dismountWindowsOnTop(getBounds());*/
		super.show();
	}

	public void centerDialog() {
		setLocationRelativeTo(getOwner());
	}

	public static final DialogFactory DIALOG_FACTORY = new DialogFactory() {

		@Override
		public Dialog getNewDialog() throws HeadlessException {
			return new FlexoDialog();
		}

		@Override
		public Dialog getNewDialog(Frame owner) throws HeadlessException {
			return new FlexoDialog(owner);
		}

		@Override
		public Dialog getNewDialog(Frame owner, boolean modal) throws HeadlessException {
			return new FlexoDialog(owner, modal);
		}

		@Override
		public Dialog getNewDialog(Frame owner, String title) throws HeadlessException {
			return new FlexoDialog(owner, title);
		}

		@Override
		public Dialog getNewDialog(Frame owner, String title, boolean modal) throws HeadlessException {
			return new FlexoDialog(owner, title, modal);
		}

		@Override
		public Dialog getNewDialog(Dialog owner) throws HeadlessException {
			return new FlexoDialog(owner);
		}

		@Override
		public Dialog getNewDialog(Dialog owner, boolean modal) throws HeadlessException {
			return new FlexoDialog(owner, modal);
		}

		@Override
		public Dialog getNewDialog(Dialog owner, String title) throws HeadlessException {
			return new FlexoDialog(owner, title);
		}

		@Override
		public Dialog getNewDialog(Dialog owner, String title, boolean modal) throws HeadlessException {
			return new FlexoDialog(owner, title, modal);
		}

	};

}