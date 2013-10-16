package org.openflexo.fge.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.util.logging.Logger;

import org.openflexo.fge.FGECoreUtils;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.localization.FlexoLocalization;

public abstract class ForegroundStyleImpl extends FGEStyleImpl implements ForegroundStyle {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ForegroundStyle.class.getPackage().getName());

	private boolean noStroke = false;

	private Color color;
	private double lineWidth;
	private JoinStyle joinStyle;
	private CapStyle capStyle;
	private DashStyle dashStyle;

	private boolean useTransparency = false;
	private float transparencyLevel = 0.5f; // Between 0.0 and 1.0

	private Stroke stroke;
	private double strokeScale;

	public ForegroundStyleImpl() {
		super();
		noStroke = false;
		color = Color.BLACK;
		lineWidth = 1.0;
		joinStyle = JoinStyle.JOIN_MITER;
		capStyle = CapStyle.CAP_SQUARE;
		dashStyle = DashStyle.PLAIN_STROKE;
	}

	@Deprecated
	private ForegroundStyleImpl(Color aColor) {
		this();
		color = aColor;
	}

	/*@Deprecated
	public static ForegroundStyleImpl makeDefault() {
		return new ForegroundStyleImpl();
	}

	@Deprecated
	public static ForegroundStyleImpl makeNone() {
		ForegroundStyleImpl returned = new ForegroundStyleImpl();
		returned.setNoStroke(true);
		return returned;
	}

	@Deprecated
	public static ForegroundStyleImpl makeStyle(Color aColor) {
		return new ForegroundStyleImpl(aColor);
	}

	@Deprecated
	public static ForegroundStyleImpl makeStyle(Color aColor, float aLineWidth) {
		ForegroundStyleImpl returned = new ForegroundStyleImpl(aColor);
		returned.setLineWidth(aLineWidth);
		return returned;
	}

	@Deprecated
	public static ForegroundStyleImpl makeStyle(Color aColor, float aLineWidth, JoinStyle joinStyle, CapStyle capStyle, DashStyle dashStyle) {
		ForegroundStyleImpl returned = new ForegroundStyleImpl(aColor);
		returned.setLineWidth(aLineWidth);
		returned.setJoinStyle(joinStyle);
		returned.setCapStyle(capStyle);
		returned.setDashStyle(dashStyle);
		return returned;
	}

	@Deprecated
	public static ForegroundStyleImpl makeStyle(Color aColor, float aLineWidth, DashStyle dashStyle) {
		ForegroundStyleImpl returned = new ForegroundStyleImpl(aColor);
		returned.setLineWidth(aLineWidth);
		returned.setDashStyle(dashStyle);
		return returned;
	}*/

	@Override
	public CapStyle getCapStyle() {
		return capStyle;
	}

	@Override
	public void setCapStyle(CapStyle aCapStyle) {
		if (requireChange(this.capStyle, aCapStyle)) {
			CapStyle oldCapStyle = capStyle;
			this.capStyle = aCapStyle;
			stroke = null;
			setChanged();
			notifyObservers(new FGENotification(CAP_STYLE, oldCapStyle, aCapStyle));
		}
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color aColor) {
		if (requireChange(this.color, aColor)) {
			java.awt.Color oldColor = color;
			this.color = aColor;
			setChanged();
			notifyObservers(new FGENotification(COLOR, oldColor, aColor));
		}
	}

	@Override
	public void setColorNoNotification(Color aColor) {
		this.color = aColor;
	}

	@Override
	public DashStyle getDashStyle() {
		return dashStyle;
	}

	@Override
	public void setDashStyle(DashStyle aDashStyle) {
		if (requireChange(this.color, aDashStyle)) {
			DashStyle oldDashStyle = dashStyle;
			this.dashStyle = aDashStyle;
			stroke = null;
			setChanged();
			notifyObservers(new FGENotification(DASH_STYLE, oldDashStyle, dashStyle));
		}
	}

	@Override
	public JoinStyle getJoinStyle() {
		return joinStyle;
	}

	@Override
	public void setJoinStyle(JoinStyle aJoinStyle) {
		if (requireChange(this.joinStyle, aJoinStyle)) {
			JoinStyle oldJoinStyle = joinStyle;
			this.joinStyle = aJoinStyle;
			stroke = null;
			setChanged();
			notifyObservers(new FGENotification(JOIN_STYLE, oldJoinStyle, aJoinStyle));
		}
	}

	@Override
	public double getLineWidth() {
		return lineWidth;
	}

	@Override
	public void setLineWidth(double aLineWidth) {
		if (requireChange(this.lineWidth, aLineWidth)) {
			double oldLineWidth = lineWidth;
			lineWidth = aLineWidth;
			stroke = null;
			setChanged();
			notifyObservers(new FGENotification(LINE_WIDTH, oldLineWidth, aLineWidth));
		}
	}

	@Override
	public boolean getNoStroke() {
		return noStroke;
	}

	@Override
	public void setNoStroke(boolean aFlag) {
		if (requireChange(this.noStroke, aFlag)) {
			boolean oldValue = noStroke;
			this.noStroke = aFlag;
			setChanged();
			notifyObservers(new FGENotification(NO_STROKE, oldValue, aFlag));
		}
	}

	@Override
	public Stroke getStroke(double scale) {
		if (stroke == null || scale != strokeScale) {
			if (dashStyle == DashStyle.PLAIN_STROKE) {
				stroke = new BasicStroke((float) (lineWidth * scale), capStyle.ordinal(), joinStyle.ordinal());
			} else {
				float[] scaledDashArray = new float[dashStyle.getDashArray().length];
				for (int i = 0; i < dashStyle.getDashArray().length; i++) {
					scaledDashArray[i] = (float) (dashStyle.getDashArray()[i] * scale * lineWidth);
				}
				float scaledDashedPhase = (float) (dashStyle.getDashPhase() * scale * lineWidth);
				stroke = new BasicStroke((float) (lineWidth * scale), capStyle.ordinal(), joinStyle.ordinal(), 10, scaledDashArray,
						scaledDashedPhase);
			}
			strokeScale = scale;
		}
		return stroke;
	}

	@Override
	public float getTransparencyLevel() {
		return transparencyLevel;
	}

	@Override
	public void setTransparencyLevel(float aLevel) {
		if (requireChange(this.transparencyLevel, aLevel)) {
			float oldValue = transparencyLevel;
			this.transparencyLevel = aLevel;
			setChanged();
			notifyObservers(new FGENotification(TRANSPARENCY_LEVEL, oldValue, aLevel));
		}
	}

	@Override
	public boolean getUseTransparency() {
		return useTransparency;
	}

	@Override
	public void setUseTransparency(boolean aFlag) {
		if (requireChange(this.useTransparency, aFlag)) {
			boolean oldValue = useTransparency;
			this.useTransparency = aFlag;
			setChanged();
			notifyObservers(new FGENotification(USE_TRANSPARENCY, oldValue, aFlag));
		}
	}

	/*@Override
	public ForegroundStyle clone() {
		try {
			ForegroundStyle returned = (ForegroundStyle) super.clone();
			return returned;
		} catch (CloneNotSupportedException e) {
			// cannot happen since we are clonable
			e.printStackTrace();
			return null;
		}
	}*/

	/*@Override
	public String toString() {
		return "ForegroundStyle " + Integer.toHexString(hashCode()) + " [noStroke=" + noStroke + ",lineWidth=" + lineWidth + ",color="
				+ color + ",joinStyle=" + joinStyle + ",capStyle=" + capStyle + ",dashStyle=" + dashStyle + ",useTransparency="
				+ useTransparency + ",transparencyLevel=" + transparencyLevel + "]";
	}*/

	@Override
	public String toNiceString() {
		if (getNoStroke()) {
			return FlexoLocalization.localizedForKey(FGECoreUtils.LOCALIZATION, "no_stroke");
		} else {
			return lineWidth + "pt, " + color;
		}
	}

	/*@Override
	public boolean equals(Object obj) {
		if (obj instanceof ForegroundStyle) {
			// logger.info("Equals called for ForegroundStyle !!!!!!!!!");
			ForegroundStyle fs = (ForegroundStyle) obj;
			return getNoStroke() == fs.getNoStroke() && getLineWidth() == fs.getLineWidth() && getColor() == fs.getColor()
					&& getJoinStyle() == fs.getJoinStyle() && getCapStyle() == fs.getCapStyle() && getDashStyle() == fs.getDashStyle()
					&& getUseTransparency() == fs.getUseTransparency() && getTransparencyLevel() == fs.getTransparencyLevel();
		}
		return super.equals(obj);
	}*/

	private boolean requireChange(Object oldObject, Object newObject) {
		if (oldObject == null) {
			if (newObject == null) {
				return false;
			} else {
				return true;
			}
		}
		return !oldObject.equals(newObject);
	}

}
