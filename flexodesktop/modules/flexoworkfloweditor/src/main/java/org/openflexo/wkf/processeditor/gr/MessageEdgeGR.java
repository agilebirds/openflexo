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
package org.openflexo.wkf.processeditor.gr;

import java.awt.Color;

import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.ForegroundStyle.DashStyle;
import org.openflexo.fge.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.fge.connectors.rpc.RectPolylinConnector;
import org.openflexo.foundation.wkf.edge.MessageEdge;
import org.openflexo.wkf.processeditor.ProcessRepresentation;

public class MessageEdgeGR extends EdgeGR<MessageEdge<?, ?>> {

	public MessageEdgeGR(MessageEdge<?, ?> edge, ProcessRepresentation aDrawing) {
		super(edge, edge.getStartNode(), edge.getEndNode(), aDrawing);
	}

	public MessageEdge<?, ?> getMessageEdge() {
		return getDrawable();
	}

	@Override
	public String toString() {
		return "MessageEdgeGR of " + getMessageEdge();
	}

	@Override
	public void refreshConnector() {
		if (!isConnectorConsistent()) {
			// Dont' go further for connector that are inconsistent (this may happen
			// during big model restructurations (for example during a multiple delete)
			return;
		}
		if (getConnector() instanceof RectPolylinConnector) {
			if (getStartObject() instanceof PortmapGR) {
				startOrientationFixed = true;
				newStartOrientation = ((PortmapGR) getStartObject()).getOrientation();
			}
			if (getEndObject() instanceof PortmapGR) {
				endOrientationFixed = true;
				newEndOrientation = ((PortmapGR) getEndObject()).getOrientation();
			}
			/*
			if (getStartObject() instanceof PortmapGR && getEndObject() instanceof PortmapGR) {
				// Go to a portmap
				PortmapGR startPortmapGR = (PortmapGR)getStartObject();
				PortmapGR endPortmapGR = (PortmapGR)getEndObject();
				if (startPortmapGR != null 
						&& endPortmapGR != null
						&& startPortmapGR.getOrientation() != null
						&& endPortmapGR.getOrientation() != null) {
					newOrientationConstraints = RectPolylinConstraints.ORIENTATIONS_FIXED;
					newStartOrientation = startPortmapGR.getOrientation();
					newEndOrientation = endPortmapGR.getOrientation();
					//((RectPolylinConnector)getConnector()).setRectPolylinConstraints(RectPolylinConstraints.ORIENTATIONS_FIXED,startPortmapGR.getOrientation(),endPortmapGR.getOrientation());	
					//System.out.println("connector "+getMessageEdge()+" set start orientation to "+startPortmapGR.getOrientation());
					//System.out.println("connector "+getMessageEdge()+" set end orientation to "+endPortmapGR.getOrientation());
				}
			}
			else if (getEndObject() instanceof PortmapGR) {
				// Go to a portmap
				PortmapGR portmapGR = (PortmapGR)getEndObject();
				if (portmapGR != null
						&& portmapGR.getOrientation() != null) {
					newOrientationConstraints = RectPolylinConstraints.END_ORIENTATION_FIXED;
					newEndOrientation = portmapGR.getOrientation();
					//((RectPolylinConnector)getConnector()).setRectPolylinConstraints(RectPolylinConstraints.END_ORIENTATION_FIXED,null,portmapGR.getOrientation());		
					//System.out.println("connector "+getMessageEdge()+" set end orientation to "+portmapGR.getOrientation());
				}
			}
			else if (getStartObject() instanceof PortmapGR) {
				// Come from portmap
				PortmapGR portmapGR = (PortmapGR)getStartObject();
				if (portmapGR != null
						&& portmapGR.getOrientation() != null) {
					newOrientationConstraints = RectPolylinConstraints.START_ORIENTATION_FIXED;
					newStartOrientation = portmapGR.getOrientation();
					//((RectPolylinConnector)getConnector()).setRectPolylinConstraints(RectPolylinConstraints.START_ORIENTATION_FIXED,portmapGR.getOrientation(),null);			
					//System.out.println("connector "+getMessageEdge()+" set start orientation to "+portmapGR.getOrientation());
				}
			}*/
		}
		super.refreshConnector();
	}

	@Override
	public void updatePropertiesFromWKFPreferences() {
		super.updatePropertiesFromWKFPreferences();
		if (getStartObject() instanceof PortmapGR || getEndObject() instanceof PortmapGR || getStartObject() instanceof PortGR
				|| getEndObject() instanceof PortGR) {
			setForeground(ForegroundStyle.makeStyle(Color.DARK_GRAY, 1.6f, DashStyle.MEDIUM_DASHES));
			setEndSymbol(EndSymbolType.PLAIN_ARROW);
		} else {
			setForeground(ForegroundStyle.makeStyle(Color.DARK_GRAY, 1.6f));
			setEndSymbol(EndSymbolType.FILLED_ARROW);
		}
	}
}
