package org.openflexo.swing.layout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.openflexo.swing.layout.MultiSplitLayout.Divider;
import org.openflexo.swing.layout.MultiSplitLayout.Leaf;
import org.openflexo.swing.layout.MultiSplitLayout.Node;
import org.openflexo.swing.layout.MultiSplitLayout.Split;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class TestMultiSplitPane {

	public static final String LEFT = "left";
	public static final String CENTER = "center";
	public static final String RIGHT = "right";

	public static final String TOP = "top";
	public static final String MIDDLE = "middle";
	public static final String BOTTOM = "bottom";

	protected void initUI() {

		Split root = getDefaultLayout();
		final MultiSplitLayout layout = new MultiSplitLayout();
		layout.setLayoutByWeight(false);
		layout.setFloatingDividers(false);
		JXMultiSplitPane splitPane = new JXMultiSplitPane(layout);
		splitPane.setDividerPainter(new KnobDividerPainter());
		addButton(LEFT + TOP, splitPane);
		addButton(CENTER + TOP, splitPane);
		addButton(RIGHT + TOP, splitPane);
		addButton(LEFT + BOTTOM, splitPane);
		addButton(CENTER + BOTTOM, splitPane);
		addButton(RIGHT + BOTTOM, splitPane);
		restoreLayout(layout, root);
		splitPane.setPreferredSize(layout.getModel().getBounds().getSize());
		JFrame frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				saveLayout(layout);
				System.exit(0);
			}
		});
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.add(splitPane);
		frame.pack();
		frame.setVisible(true);
	}

	protected Split getDefaultLayout() {
		Split root = new Split();
		root.setName("ROOT");
		Split left = getVerticalSplit(LEFT, 0.5, 0.5);
		left.setWeight(0);
		left.setName(LEFT);
		Split center = getVerticalSplit(CENTER, 0.8, 0.2);
		center.setWeight(1.0);
		center.setName(CENTER);
		Split right = getVerticalSplit(RIGHT, 0.5, 0.5);
		right.setWeight(0);
		right.setName(RIGHT);
		root.setChildren(left, new Divider(), center, new Divider(), right);
		return root;
	}

	protected void addButton(final String buttonName, final JXMultiSplitPane splitPane) {
		final JButton button = new JButton(buttonName);
		splitPane.add(buttonName, button);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				MultiSplitLayout layout = splitPane.getMultiSplitLayout();
				restoreLayout(layout, getDefaultLayout());
				splitPane.revalidate();
			}
		});
	}

	public Split getVerticalSplit(String name, double topWeight, double bottomWeight) {
		Split split = new Split();
		split.setRowLayout(false);
		Leaf top = new Leaf(name + TOP);
		top.setWeight(topWeight);
		Leaf bottom = new Leaf(name + BOTTOM);
		bottom.setWeight(bottomWeight);
		split.setChildren(top, new Divider(), bottom);
		return split;
	}

	protected void restoreLayout(MultiSplitLayout layout, Node defaultModel) {
		Node model = defaultModel;
		try {
			model = getGson().fromJson(new InputStreamReader(new FileInputStream(getLayoutFile()), "UTF-8"), Split.class);
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		layout.setModel(model);
	}

	protected void saveLayout(MultiSplitLayout layout) {
		Gson gson = getGson();
		String json = gson.toJson(layout.getModel());
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(getLayoutFile());
			fos.write(json.getBytes("UTF-8"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	protected Gson getGson() {
		GsonBuilder builder = new GsonBuilder().registerTypeAdapterFactory(new MultiSplitLayoutTypeAdapterFactory());
		Gson gson = builder.create();
		return gson;
	}

	protected File getLayoutFile() {
		return new File("testlayout");
	}

	public static void main(String[] args) {
		/*BeanInfo info = Introspector.getBeanInfo(JTextField.class);
		PropertyDescriptor[] propertyDescriptors = info.getPropertyDescriptors();
		for (int i = 0; i < propertyDescriptors.length; ++i) {
			PropertyDescriptor pd = propertyDescriptors[i];
			if (pd.getName().equals("text")) {
				pd.setValue("transient", Boolean.TRUE);
			}
		}*/

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new TestMultiSplitPane().initUI();
			}
		});
	}

}
