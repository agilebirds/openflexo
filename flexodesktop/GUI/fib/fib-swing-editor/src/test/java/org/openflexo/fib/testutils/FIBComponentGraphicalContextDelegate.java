package org.openflexo.fib.testutils;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.editor.FIBEmbeddedEditor;

/**
 * Utility test class used to both interactively and automatically test FIBDialog components
 * 
 * @author sylvain
 * 
 */
public class FIBComponentGraphicalContextDelegate {

	private final EventProcessor eventProcessor;
	private JTabbedPane tabbedPane;
	private boolean dontDestroyMe = false;

	public FIBComponentGraphicalContextDelegate(final String frameTitle, final File componentFile, final Object data) {
		eventProcessor = new EventProcessor();

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					tabbedPane = new JTabbedPane();
					JFrame frame = new JFrame(frameTitle);
					frame.setLayout(new BorderLayout());
					frame.setSize(new Dimension(1024, 768));
					frame.setLocationRelativeTo(null);
					JButton testButton;
					testButton = new JButton("Test component");
					testButton.addActionListener(new ActionListener() {
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
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									new FIBEmbeddedEditor(componentFile, data);
								}
							});

						}
					});
					JPanel buttons = new JPanel(new FlowLayout());
					buttons.add(testButton);
					buttons.add(editButton);
					frame.getContentPane().add(buttons, BorderLayout.NORTH);
					frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
					frame.setVisible(true);
				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	public void addTab(String title, FIBController controller) {
		tabbedPane.add(title, controller.getRootView().getDynamicJComponent());
	}

	public void waitGUI() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (dontDestroyMe) {
			while (true) {
				try {
					synchronized (FIBComponentGraphicalContextDelegate.class) {
						FIBComponentGraphicalContextDelegate.class.wait();
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
