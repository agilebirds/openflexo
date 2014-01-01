package org.openflexo.fib.testutils;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.fib.editor.FIBEmbeddedEditor;

public class FIBDialogGraphicalContextDelegate {

	private final EventProcessor eventProcessor;
	private boolean dontDestroyMe = false;

	public FIBDialogGraphicalContextDelegate(final FIBDialog<?> dialog, final File componentFile) {
		eventProcessor = new EventProcessor();

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					JButton myButton;
					myButton = new JButton("Test component");
					myButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							dontDestroyMe = true;
						}
					});
					JButton editButton;
					editButton = new JButton("Edit component");
					editButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							dontDestroyMe = true;
							dialog.dispose();
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									new FIBEmbeddedEditor(componentFile, dialog.getData());
								}
							});

						}
					});
					JPanel buttons = new JPanel(new FlowLayout());
					buttons.add(myButton);
					buttons.add(editButton);
					dialog.getContentPane().add(buttons, BorderLayout.NORTH);

				}
			});

			(new Thread(new Runnable() {

				@Override
				public void run() {
					System.out.println("Juste avant d'ouvir la fenetre");
					waitGUI();
					dialog.dispose();
				}
			})).start();

			System.out.println("Hop, je l'affiche");

			dialog.pack();
			dialog.setVisible(true);

			System.out.println("Hop, je viens d'etre disposee, dontDestroyMe=" + dontDestroyMe);

			if (dontDestroyMe) {
				while (true) {
					try {
						synchronized (FIBDialogGraphicalContextDelegate.class) {
							FIBDialogGraphicalContextDelegate.class.wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	private void waitGUI() {
		System.out.println("J'attends");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.println("J'arrete d'attendre");

		if (dontDestroyMe) {
			while (true) {
				try {
					synchronized (FIBDialogGraphicalContextDelegate.class) {
						FIBDialogGraphicalContextDelegate.class.wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void setUp() {
		eventProcessor.reset();
	}

	public void tearDown() throws Exception {
		if (eventProcessor.getException() != null) {
			throw new InvocationTargetException(eventProcessor.getException());
		}
	}

	public static class EventProcessor extends java.awt.EventQueue {

		private Throwable exception = null;

		public EventProcessor() {
			Toolkit.getDefaultToolkit().getSystemEventQueue().push(this);
		}

		public void reset() {
			exception = null;
		}

		@Override
		protected void dispatchEvent(AWTEvent e) {
			try {
				super.dispatchEvent(e);
			} catch (Throwable exception) {
				this.exception = exception;
			}
		}

		public Throwable getException() {
			return exception;
		}
	}

}
