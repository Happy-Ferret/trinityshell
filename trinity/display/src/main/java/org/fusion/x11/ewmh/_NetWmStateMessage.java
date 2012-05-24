/*
 * This file is part of Fusion-X11.
 * 
 * Fusion-X11 is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * Fusion-X11 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Fusion-X11. If not, see <http://www.gnu.org/licenses/>.
 */
package org.fusion.x11.ewmh;

import org.fusion.x11.core.IntDataContainer;
import org.fusion.x11.core.XAtom;
import org.fusion.x11.core.XDisplay;
import org.trinity.core.display.api.event.ClientMessageEvent;

//TODO documentation
/**
* 
* @author Erik De Rijcke
* @since 1.0
*/
public final class _NetWmStateMessage extends EwmhClientMessageEvent implements
		HasSourceIndication {

	private final StateAction action;
	private final XAtom firstProperty;
	private final XAtom secondProperty;

	private final SourceIndication sourceIndication;

	/**
	 * 
	 * @param display
	 * @param clientMessageEvent
	 */
	public _NetWmStateMessage(final XDisplay display,
			final ClientMessageEvent clientMessageEvent) {
		super(clientMessageEvent);

		final IntDataContainer intDataContainer = new IntDataContainer(
				clientMessageEvent.getData());

		final int actionValue = intDataContainer.readDataBlock().intValue();
		this.action = StateAction.values()[actionValue];

		final int firstPropertyValue = intDataContainer.readDataBlock()
				.intValue();
		this.firstProperty = display.getDisplayAtoms().getById(
				Long.valueOf(firstPropertyValue));

		final int secondPropertyValue = intDataContainer.readDataBlock()
				.intValue();
		this.secondProperty = display.getDisplayAtoms().getById(
				Long.valueOf(secondPropertyValue));

		final int source = intDataContainer.readDataBlock().intValue();

		this.sourceIndication = SourceIndication.values()[source];
	}

	/**
	 * 
	 * @return
	 */
	public StateAction getAction() {
		return this.action;
	}

	/**
	 * 
	 * @return
	 */
	public XAtom getFirstProperty() {
		return this.firstProperty;
	}

	/**
	 * 
	 * @return
	 */
	public XAtom getSecondProperty() {
		return this.secondProperty;
	}

	@Override
	public SourceIndication getSourceIndication() {
		return this.sourceIndication;
	}

	public static final EwmhClientMessageEventType TYPE = new EwmhClientMessageEventType();

	@Override
	public EwmhClientMessageEventType getType() {
		return _NetDesktopGeometryMessage.TYPE;
	}
}