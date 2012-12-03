package org.openflexo.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class MultiSplitPane extends JPanel {

	private static final int INSETS_SIZE = 4;

	private class MouseDrag extends MouseAdapter {

		private boolean dragging = false;
		private Divider divider;
		private int available;
		private Node previous;
		private Node next;
		private Point last;

		@Override
		public void mousePressed(MouseEvent e) {
			last = new Point(e.getX(), e.getY());
			last = SwingUtilities.convertPoint((Component) e.getSource(), last, MultiSplitPane.this);
			divider = getDividerAt(last);
			if (divider != null) {
				previous = getLayout().getNodeForComponent(divider.previous);
				next = getLayout().getNodeForComponent(divider.next);
			}
			dragging = divider != null && previous != null && next != null;
			if (dragging) {
				setSize(previous, divider.previous);
				setSize(next, divider.next);
				if (getModel().isRowLayout()) {
					available = getWidth() - getLayout().getRequiredSize() /*- getComponentCount() * INSETS_SIZE*/;
				} else {
					available = getHeight() - getLayout().getRequiredSize() /*- getComponentCount() * INSETS_SIZE*/;
				}
			}
		}

		private void setSize(Node node, Component comp) {
			if (node.getPercentage() < 0) {
				if (getModel().isRowLayout()) {
					node.setSize(comp.getWidth());
				} else {
					node.setSize(comp.getHeight());
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			performDrag(e);
			dragging = false;
			divider = null;
			previous = null;
			next = null;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			performDrag(e);
		}

		private void performDrag(MouseEvent e) {
			if (dragging) {
				Point p = new Point(e.getX(), e.getY());
				int offset;
				if (divider.isVertical()) {
					offset = p.x - last.x;
				} else {
					offset = p.y - last.y;
				}
				updateNodeForComponent(offset, previous, divider.previous);
				updateNodeForComponent(-offset, next, divider.next);
			}
		}

		private void updateNodeForComponent(int offset, Node node, Component comp) {
			if (node.getPercentage() < 0) {
				node.setSize(node.getSize() + offset);
			} else {
				int size;
				if (getModel().isRowLayout()) {
					size = comp.getWidth();
				} else {
					size = comp.getHeight();
				}
				if (available > 0) {
					System.err.println(available + " " + size + " " + offset);
					double percentage = (double) (size + offset) / available;
					System.err.println(node.getName() + " " + node.getPercentage() + " " + percentage);
					node.setPercentage(Math.max(percentage, 0));
				}
			}
			getLayout().setConstraintForComponent(comp, node);
			revalidate();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			updateCursor(e);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			updateCursor(e);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			updateCursor(e);
		}

		private void updateCursor(MouseEvent e) {
			Point point = new Point(e.getX(), e.getY());
			point = SwingUtilities.convertPoint((Component) e.getSource(), point, MultiSplitPane.this);
			Divider d = getDividerAt(point);
			if (d != null) {
				if (getModel().isRowLayout()) {
					setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
				} else {
					setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
				}
			} else {
				setCursor(Cursor.getDefaultCursor());
			}

		}

	}

	@Override
	public void doLayout() {
		super.doLayout();
		for (Component c : getComponents()) {
			System.err.println(c.getSize());
		}
	}

	private class Divider {
		public final Component previous;
		public final Component next;

		public Divider(Component previous, Component next) {
			super();
			this.previous = previous;
			this.next = next;
		}

		public boolean isVertical() {
			return getModel().isRowLayout();
		}

		public boolean isHorizontal() {
			return !isVertical();
		}
	}

	private final MultiSplitLayout layout;

	public MultiSplitPane(Node model) {
		setLayout(new MultiSplitLayout(model));
		this.layout = (MultiSplitLayout) super.getLayout();
		MouseDrag drag = new MouseDrag();
		addMouseListener(drag);
		addMouseMotionListener(drag);
	}

	@Override
	protected void addImpl(Component comp, Object constraints, int index) {
		if (constraints instanceof String) {
			String name = (String) constraints;
			getLayout().addLayoutComponent(name, comp);
			Node node = getLayout().getNodeForName(name);
			if (node != null) {
				super.addImpl(comp, getLayout().getConstraintForComponent(comp, node), node.getParent().getChildren().indexOf(node));
			} else {
				throw new IllegalArgumentException("No such node with name " + name + " found in model");
			}
		} else {
			throw new IllegalArgumentException("Constraints cannot be null and must be a String");
		}
	}

	@Override
	public MultiSplitLayout getLayout() {
		return layout;
	}

	public Node getModel() {
		return layout.getModel();
	}

	public void setModel(Node model) {
		getLayout().setModel(model);
	}

	public Divider getDividerAt(Point p) {
		return getDividerAt(p.x, p.y);
	}

	public Divider getDividerAt(int x, int y) {
		if (getComponentAt(x, y) != MultiSplitPane.this) {
			return null;
		}
		Component previous = null;
		Component next = null;
		if (getModel().isRowLayout()) {
			for (int i = 0; i < INSETS_SIZE * 2; i++) {
				previous = getComponentAt(x - i, y);
				if (previous != null && previous != MultiSplitPane.this) {
					break;
				}
			}
			if (previous != null) {
				for (int i = 0; i < INSETS_SIZE * 2; i++) {
					next = getComponentAt(x + i, y);
					if (next != null && next != MultiSplitPane.this) {
						break;
					}
				}
			}
		} else {
			for (int i = 0; i < INSETS_SIZE * 2; i++) {
				previous = getComponentAt(x, y - i);
				if (previous != null && previous != MultiSplitPane.this) {
					break;
				}
			}
			if (previous != null) {
				for (int i = 0; i < INSETS_SIZE * 2; i++) {
					next = getComponentAt(x, y + i);
					if (next != null && next != MultiSplitPane.this) {
						break;
					}
				}
			}
		}
		if (next != null && next != MultiSplitPane.this && previous != null && previous != MultiSplitPane.this) {
			return new Divider(previous, next);
		}
		return null;
	}

	public class MultiSplitLayout extends GridBagLayout implements LayoutManager {

		private static final int PRECISION = 1000000; // 1e6

		private BiMap<String, Component> components = HashBiMap.create();

		private Node model;

		private int requiredSize = 0;

		public MultiSplitLayout(Node model) {
			if (model == null) {
				throw new NullPointerException();
			}
			setModel(model);
		}

		public Node getModel() {
			return model;
		}

		public void setModel(Node model) {
			if (model == null) {
				throw new NullPointerException("Model cannot be null");
			}
			this.model = model;
			updateConstraints();
		}

		public Node getNodeForComponent(Component comp) {
			String name = components.inverse().get(comp);
			if (name != null) {
				return getNodeForName(name);
			} else {
				return null;
			}
		}

		public Node getNodeForName(String name) {
			for (Node n : model.getChildren()) {
				if (n.getName().equals(name)) {
					return n;
				}
			}
			return null;
		}

		public final int getRequiredSize() {
			return requiredSize;
		}

		public void update() {
			updateRequiredSize();
			updateConstraints();
		}

		private void updateRequiredSize() {
			requiredSize = 0;
			for (Node n : model.getChildren()) {
				if (n.getPercentage() < 0) {
					if (n.getSize() > 0) {
						requiredSize += n.getSize();
					}
				}
			}
		}

		private void updateConstraints() {
			for (Node n : model.getChildren()) {
				Component component = components.get(n.getName());
				if (component != null) {
					setConstraintForComponent(component, n);
				}
			}
		}

		private void setConstraintForComponent(Component comp, Node n) {
			comp.setVisible(n.isVisible());
			if (n.isVisible()) {
				setConstraints(comp, getConstraintForComponent(comp, n));
			} else {
				super.removeLayoutComponent(comp);
			}
		}

		private GridBagConstraints getConstraintForComponent(Component comp, Node n) {
			GridBagConstraints gbc = new GridBagConstraints();
			boolean rowLayout = n.getParent().isRowLayout();
			if (rowLayout) {
				gbc.gridx = n.getParent().getChildren().indexOf(n);
				gbc.gridy = 0;
			} else {
				gbc.gridx = 0;
				gbc.gridy = n.getParent().getChildren().indexOf(n);
			}
			gbc.fill = GridBagConstraints.BOTH;
			if (n.getPercentage() >= 0) {
				comp.setPreferredSize(new Dimension(0, 0));
				if (rowLayout) {
					gbc.weightx = n.getPercentage() * PRECISION;
					gbc.weighty = 1.0;
				} else {
					gbc.weightx = 1.0;
					gbc.weighty = n.getPercentage() * PRECISION;
				}
			} else {
				if (n.getSize() > 0) {
					// Only width or height will be used in the layout process
					comp.setPreferredSize(new Dimension(n.getSize(), n.getSize()));
				}
			}
			if (n.getChildren().size() == 0) {
				gbc.insets = new Insets(INSETS_SIZE, INSETS_SIZE, INSETS_SIZE, INSETS_SIZE);
			}
			System.err.println(n.getName() + " " + gbc.weightx + " " + gbc.weighty);
			return gbc;
		}

		@Override
		public void addLayoutComponent(String name, Component comp) {
			if (name == null) {
				throw new IllegalArgumentException("Component name cannot be null");
			}
			Component c = components.get(name);
			if (c != null && c != comp) {
				throw new IllegalArgumentException("This container alread has a component for name " + name);
			}
			String old = components.inverse().remove(comp);

			components.put(name, comp);
			Node node = getNodeForName(name);
			if (node != null) {
				setConstraintForComponent(comp, node);
			}
		}

		@Override
		public void removeLayoutComponent(Component comp) {
			super.removeLayoutComponent(comp);
			for (Entry<String, Component> e : components.entrySet()) {
				if (e.getValue() == comp) {
					comp.setPreferredSize(null);
					components.remove(e.getKey());
					break;
				}
			}
		}

	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					initTestUI();
				} catch (ModelDefinitionException e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected static void initTestUI() throws ModelDefinitionException {
		JFrame frame = new JFrame("Test multi split");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ModelFactory factory = new ModelFactory();
		factory.importClass(Node.class);
		Node root = factory.newInstance(Node.class);
		root.setName("root");
		root.setRowLayout(true);
		Node left = getVerticalSplit(factory, "left");
		Node center = getVerticalSplit(factory, "center");
		Node right = getVerticalSplit(factory, "right");
		root.addChild(left);
		root.addChild(center);
		root.addChild(right);
		MultiSplitPane splitPane = new MultiSplitPane(root);
		splitPane.setBorder(BorderFactory.createLineBorder(Color.GREEN));
		MultiSplitPane leftPane = new MultiSplitPane(left);
		leftPane.setBorder(BorderFactory.createLineBorder(Color.RED));
		MultiSplitPane centerPane = new MultiSplitPane(center);
		centerPane.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		MultiSplitPane rightPane = new MultiSplitPane(right);
		rightPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		splitPane.add(left.getName(), leftPane);
		splitPane.add(center.getName(), centerPane);
		splitPane.add(right.getName(), rightPane);
		initSplit(left, leftPane);
		initSplit(center, centerPane);
		initSplit(right, rightPane);
		frame.add(splitPane);
		frame.setSize(1200, 600);
		frame.setVisible(true);
	}

	private static void initSplit(Node node, MultiSplitPane splitPane) {
		for (Node child : node.getChildren()) {
			JButton button = new JButton(child.getName());
			splitPane.add(child.getName(), button);
		}
	}

	private static Node getVerticalSplit(ModelFactory factory, String name) {
		Node node = factory.newInstance(Node.class);
		node.setName(name);
		node.setRowLayout(false);
		node.setPercentage(1.0);
		for (int i = 0; i < 3; i++) {
			Node child = factory.newInstance(Node.class);
			child.setName(name + "-" + i);
			child.setPercentage(1.0 / 3);
			node.addChild(child);
		}
		return node;
	}
}
