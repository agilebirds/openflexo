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
package org.openflexo.jedit;

import java.awt.Dimension;

import javax.swing.JFrame;

public class JEditDemo extends JFrame {

	private JEditTextArea editorPane;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JEditDemo demo = new JEditDemo();
		demo.init();
		demo.setVisible(true);
	}

	private void init() {
		editorPane = new JEditTextArea();
		editorPane.setEditable(true);
		editorPane.setTokenMarker(new JavaTokenMarker());
		editorPane
				.setText("package org.openflexo.jedit;\nimport java.awt.Dimension;\nimport javax.swing.JFrame;\npublic class JEditDemo extends JFrame{\n	private JEditTextArea editorPane;\n\n	public static void main(String[] args) {\n// TODO Auto-generated method stub\nJEditDemo demo = new JEditDemo();\ndemo.init();\ndemo.setVisible(true);\n}\n\nprivate void init(){\neditorPane = new JEditTextArea();\neditorPane.setEditable(true);\neditorPane.setTokenMarker(new JavaTokenMarker());\ngetContentPane().add(editorPane);\nsetSize(new Dimension(400,400));\n}\n}\n");
		getContentPane().add(editorPane);
		setSize(new Dimension(400, 400));
	}

}
