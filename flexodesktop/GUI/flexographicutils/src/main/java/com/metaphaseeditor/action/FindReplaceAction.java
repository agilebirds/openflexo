/**
 * Metaphase Editor - WYSIWYG HTML Editor Component
 * Copyright (C) 2010  Rudolf Visagie
 * Full text of license can be found in com/metaphaseeditor/LICENSE.txt
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * The author can be contacted at metaphase.editor@gmail.com.
 */

package com.metaphaseeditor.action;

import com.metaphaseeditor.FindReplaceDialog;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JTextPane;

/**
 * 
 * @author Rudolf Visagie
 */
public class FindReplaceAction extends AbstractAction {
	private JTextPane htmlTextPane;

	public FindReplaceAction(String actionName, JTextPane htmlTextPane) {
		super(actionName);
		this.htmlTextPane = htmlTextPane;
	}

	public void actionPerformed(ActionEvent ae) {
		FindReplaceDialog findReplaceDialog = new FindReplaceDialog(null, true, htmlTextPane);
		findReplaceDialog.setVisible(true);
	}

}
