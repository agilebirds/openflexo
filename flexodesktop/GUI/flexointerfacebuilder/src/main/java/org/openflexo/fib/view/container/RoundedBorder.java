package org.openflexo.fib.view.container;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.UIManager;
import javax.swing.border.Border;

import org.openflexo.fib.utils.FIBIconLibrary;
import org.openflexo.toolbox.StringUtils;

public class RoundedBorder implements Border {
	private String title;
	private int top = 2;
	private int bottom = 2;
	private int left = 2;
	private int right = 2;

	private Font textFont;
	private Color textColor;
	private int darkLevel = 0;

	public RoundedBorder(String title, int top, int left, int bottom, int right, Font textFont, Color textColor, int darkLevel) {
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
		this.title = title;
		this.textColor = textColor;
		this.textFont = textFont;
		this.darkLevel = darkLevel;
	}

	public RoundedBorder(int top, int left, int bottom, int right) {
		this(null, top, left, bottom, right, null, null, 0);
	}

	public RoundedBorder() {
		this(2, 2, 2, 2);
	}

	@Override
	public Insets getBorderInsets(Component c) {
		Font f = textFont != null ? textFont : UIManager.getFont("TitledBorder.font");
		return new Insets(5 + top + (StringUtils.isNotEmpty(title) ? f.getSize() + 5 : 0), 5 + left, 5 + bottom, 5 + right);
	}

	@Override
	public boolean isBorderOpaque() {
		return true;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
		boolean titled = StringUtils.isNotEmpty(title);
		Font f = textFont != null ? textFont : UIManager.getFont("TitledBorder.font");
		int TOP = top + (titled ? f.getSize() + 5 : 0);
		int BOTTOM = bottom;
		int LEFT = left;
		int RIGHT = right;

		if (titled) {
			if (textFont != null) {
				g.setFont(textFont);
			} else {
				g.setFont(UIManager.getFont("TitledBorder.font"));
			}
			if (textColor != null) {
				g.setColor(textColor);
			}
			g.drawString(title, LEFT + 5, TOP - 3);
		}
		g.drawImage(FIBIconLibrary.ROUND_PANEL_BORDER_TOP_LEFT.getImage(), LEFT, TOP, null);
		g.drawImage(FIBIconLibrary.ROUND_PANEL_BORDER_TOP.getImage(), 2 + LEFT, TOP, c.getWidth() - 4 - LEFT - RIGHT, 4, null);
		g.drawImage(FIBIconLibrary.ROUND_PANEL_BORDER_TOP_RIGHT.getImage(), c.getWidth() - 3 - RIGHT, TOP, null);
		g.drawImage(FIBIconLibrary.ROUND_PANEL_BORDER_BOTTOM_LEFT.getImage(), LEFT, c.getHeight() - 2 - BOTTOM, null);
		g.drawImage(FIBIconLibrary.ROUND_PANEL_BORDER_BOTTOM.getImage(), 2 + LEFT, c.getHeight() - 2 - BOTTOM, c.getWidth() - 3 - LEFT
				- RIGHT, 2, null);
		g.drawImage(FIBIconLibrary.ROUND_PANEL_BORDER_BOTTOM_RIGHT.getImage(), c.getWidth() - 2 - RIGHT, c.getHeight() - 2 - BOTTOM, null);
		g.drawImage(FIBIconLibrary.ROUND_PANEL_BORDER_LEFT.getImage(), LEFT, 4 + TOP, 2, c.getHeight() - 6 - TOP - BOTTOM, null);
		g.drawImage(FIBIconLibrary.ROUND_PANEL_BORDER_RIGHT.getImage(), c.getWidth() - 2 - RIGHT, 4 + TOP, 2, c.getHeight() - 6 - TOP
				- BOTTOM, null);
		g.setColor(new Color(229 - darkLevel * 5, 229 - darkLevel * 5, 229 - darkLevel * 5));
		// g.setColor(Color.RED);
		g.fillRect(2 + LEFT, 5 + TOP, c.getWidth() - 4 - LEFT - RIGHT, c.getHeight() - 7 - TOP - BOTTOM);
		g.fillRect(3 + LEFT, 4 + TOP, c.getWidth() - 6 - LEFT - RIGHT, 1);
	}
}
