package org.fusion.x11.core.event;

import org.fusion.x11.core.XAtom;
import org.fusion.x11.core.XWindow;
import org.trinity.core.display.api.event.DisplayEventType;
import org.trinity.core.display.impl.event.BaseDisplayEvent;

//TODO documentation
/**
 * 
 * @author Erik De Rijcke
 * @since 1.0
 */
public class XSelectionClearNotifyEvent extends BaseDisplayEvent {

	public static final DisplayEventType TYPE = new DisplayEventType();

	private final XAtom selection;

	/**
	 * 
	 * @param eventSource
	 * @param selection
	 */
	public XSelectionClearNotifyEvent(final XWindow eventSource,
			final XAtom selection) {
		super(XSelectionClearNotifyEvent.TYPE, eventSource);
		this.selection = selection;
	}

	/**
	 * 
	 * @return
	 */
	public XAtom getSelection() {
		return this.selection;
	}
}