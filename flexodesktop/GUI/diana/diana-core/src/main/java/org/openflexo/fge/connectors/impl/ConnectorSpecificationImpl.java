package org.openflexo.fge.connectors.impl;

import java.util.logging.Logger;

import org.openflexo.fge.connectors.ConnectorSpecification;
import org.openflexo.fge.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.MiddleSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.StartSymbolType;
import org.openflexo.fge.impl.FGEObjectImpl;
import org.openflexo.fge.notifications.FGEAttributeNotification;

public abstract class ConnectorSpecificationImpl extends FGEObjectImpl implements ConnectorSpecification {

	private StartSymbolType startSymbol = StartSymbolType.NONE;
	private EndSymbolType endSymbol = EndSymbolType.NONE;
	private MiddleSymbolType middleSymbol = MiddleSymbolType.NONE;

	private double startSymbolSize = 10.0;
	private double endSymbolSize = 10.0;
	private double middleSymbolSize = 10.0;

	private double relativeMiddleSymbolLocation = 0.5; // default is in the middle !

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ConnectorSpecification.class.getPackage().getName());

	public ConnectorSpecificationImpl() {
		super();
	}

	@Override
	public abstract ConnectorType getConnectorType();

	@Override
	public EndSymbolType getEndSymbol() {
		return endSymbol;
	}

	@Override
	public void setEndSymbol(EndSymbolType endSymbol) {
		FGEAttributeNotification notification = requireChange(END_SYMBOL, endSymbol);
		if (notification != null) {
			this.endSymbol = endSymbol;
			hasChanged(notification);
		}
	}

	@Override
	public double getEndSymbolSize() {
		return endSymbolSize;
	}

	@Override
	public void setEndSymbolSize(double endSymbolSize) {
		FGEAttributeNotification notification = requireChange(END_SYMBOL_SIZE, endSymbolSize);
		if (notification != null) {
			this.endSymbolSize = endSymbolSize;
			hasChanged(notification);
		}
	}

	@Override
	public MiddleSymbolType getMiddleSymbol() {
		return middleSymbol;
	}

	@Override
	public void setMiddleSymbol(MiddleSymbolType middleSymbol) {
		FGEAttributeNotification notification = requireChange(MIDDLE_SYMBOL, middleSymbol);
		if (notification != null) {
			this.middleSymbol = middleSymbol;
			hasChanged(notification);
		}
	}

	@Override
	public double getMiddleSymbolSize() {
		return middleSymbolSize;
	}

	@Override
	public void setMiddleSymbolSize(double middleSymbolSize) {
		FGEAttributeNotification notification = requireChange(MIDDLE_SYMBOL_SIZE, middleSymbolSize);
		if (notification != null) {
			this.middleSymbolSize = middleSymbolSize;
			hasChanged(notification);
		}
	}

	@Override
	public StartSymbolType getStartSymbol() {
		return startSymbol;
	}

	@Override
	public void setStartSymbol(StartSymbolType startSymbol) {
		FGEAttributeNotification notification = requireChange(START_SYMBOL, startSymbol);
		if (notification != null) {
			this.startSymbol = startSymbol;
			hasChanged(notification);
		}
	}

	@Override
	public double getStartSymbolSize() {
		return startSymbolSize;
	}

	@Override
	public void setStartSymbolSize(double startSymbolSize) {
		FGEAttributeNotification notification = requireChange(START_SYMBOL_SIZE, startSymbolSize);
		if (notification != null) {
			this.startSymbolSize = startSymbolSize;
			hasChanged(notification);
		}
	}

	@Override
	public double getRelativeMiddleSymbolLocation() {
		return relativeMiddleSymbolLocation;
	}

	@Override
	public void setRelativeMiddleSymbolLocation(double relativeMiddleSymbolLocation) {
		FGEAttributeNotification notification = requireChange(RELATIVE_MIDDLE_SYMBOL_LOCATION, relativeMiddleSymbolLocation);
		if (notification != null) {
			this.relativeMiddleSymbolLocation = relativeMiddleSymbolLocation;
			hasChanged(notification);
		}
	}

}
