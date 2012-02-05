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

package org.fusion.x11.icccm;

import org.fusion.x11.core.XWindow;
import org.hydrogen.displayinterface.event.BaseConfigureRequestEvent;

// TODO documentation
/**
 * 
 * @author Erik De Rijcke
 * @since 1.0
 */
public class WmNormalHintsManager implements
		IcccmPropertyManager<WmSizeHintsInstance> {

	@Override
	public void manageIcccmProperty(final XWindow window,
			final WmSizeHintsInstance sizeHintsReply) {

		final long normalHintFlags = sizeHintsReply.getFlags();
		int valueMask = 0;
		short x = 0;
		short y = 0;
		int width = 0;
		int height = 0;
		// USPosition 1 User-specified x, y
		// USSize 2 User-specified width, height
		// PPosition 4 Program-specified position
		// PSize 8 Program-specified size
		// PMinSize 16 Program-specified minimum size
		// PMaxSize 32 Program-specified maximum size
		// PResizeInc 64 Program-specified resize increments
		// PAspect 128 Program-specified min and max aspect ratios
		// PBaseSize 256 Program-specified base size
		// PWinGravity 512 Program-specified window gravity
		if ((normalHintFlags & 1) != 0) {
			x = (short) sizeHintsReply.getX();
			y = (short) sizeHintsReply.getY();
			valueMask |= 1;
			valueMask |= 2;
		}
		// if ((normalHintFlags & 2) != 0) {
		// // obsolete
		// // width = sizeHintsReply.getWidth();
		// // height = sizeHintsReply.getHeight();
		// // valueMask |= 4;
		// // valueMask |= 8;
		// }
		if ((normalHintFlags & 4) != 0) {
			if (x == 0) {
				valueMask |= 1;
				x = (short) sizeHintsReply.getX();
			}
			if (y == 0) {
				valueMask |= 2;
				y = (short) sizeHintsReply.getY();
			}
		}
		if ((normalHintFlags & 8) != 0) {
			if (width == 0) {
				width = sizeHintsReply.getWidth();
				valueMask |= 4;
			}
			if (height == 0) {
				height = sizeHintsReply.getHeight();
				valueMask |= 8;
			}
		}
		if ((normalHintFlags & 16) != 0) {
			// EWMH:
			// Windows can indicate that they are non-resizable by setting
			// minheight = maxheight and minwidth = maxwidth in the ICCCM
			// WM_NORMAL_HINTS property. The Window Manager MAY decorate such
			// windows differently.

			final int minWidth = sizeHintsReply.getMinWidth();
			final int minHeight = sizeHintsReply.getMinHeight();
			if (minWidth > 0) {
				window.getPreferences().getSizePlacePreferences()
						.setMinWidth(minWidth);
			}
			if (minHeight > 0) {
				window.getPreferences().getSizePlacePreferences()
						.setMinHeight(minHeight);
			}
		}
		if ((normalHintFlags & 32) != 0) {
			final int maxWidth = sizeHintsReply.getMaxWidth();
			final int maxHeight = sizeHintsReply.getMaxHeight();
			if (maxWidth > 0) {
				window.getPreferences().getSizePlacePreferences()
						.setMaxWidth(maxWidth);
			}
			if (maxHeight > 0) {
				window.getPreferences().getSizePlacePreferences()
						.setMaxHeight(maxHeight);
			}
		}
		if ((normalHintFlags & 64) != 0) {
			final int incWidth = sizeHintsReply.getWidthInc();
			final int incHeight = sizeHintsReply.getHeightInc();
			if (incWidth > 0) {
				window.getPreferences().getSizePlacePreferences()
						.setWidthInc(incWidth);
			}
			if (incHeight > 0) {
				window.getPreferences().getSizePlacePreferences()
						.setHeightInc(incHeight);
			}
		}
		// if ((normalHintFlags & 128) != 0) {
		// // not interested
		// }
		// if ((normalHintFlags & 256) != 0) {
		// // if (width == 0) {
		// // width = sizeHintsReply.getBaseWidth();
		// // valueMask |= 4;
		// // }
		// // if (height == 0) {
		// // height = sizeHintsReply.getBaseHeight();
		// // valueMask |= 8;
		// // }
		// }
		// if ((normalHintFlags & 512) != 0) {
		// // Window Managers MUST honor the win_gravity field of
		// // WM_NORMAL_HINTS for both MapRequest and ConfigureRequest events
		// // (ICCCM Version 2.0, §4.1.2.3 and §4.1.5)
		//
		// // TODO interpret win gravity (is this really needed?) and adapt x,
		// // y with & height
		// // accordingly
		//
		// // final int gravityCode = (int) sizeHintsReply.getWinGravity();
		// // final Gravity gravity = Gravity.findGravity(gravityCode);
		// //
		// window.getPreferences().getNormalHintGravity().setGravity(gravity);
		// }
		if (valueMask != 0) {
			// Some programs don't respect icccm (like firefox) and send us
			// bogus values (zero width and
			// height with an active flag).
			// We need to check for these bogus values and decide whether or not
			// to honor them.
			final boolean configureWidth = ((valueMask & 4) != 0)
					&& (width == 0);
			final boolean configureHeigt = ((valueMask & 8) != 0)
					&& (height == 0);

			final BaseConfigureRequestEvent configureRequest = new BaseConfigureRequestEvent(
					window, false, false, configureWidth, configureHeigt, x, y,
					width, height);

			// add a configure request event to the event queue
			window.getDisplayResourceHandle().getDisplay()
					.addEventToMasterQueue(configureRequest);

		}
		// window.fireEvent(new BasePlatformRenderAreaEvent(
		// BasePlatformRenderAreaEvent.GEO_PREFERENCES_UPDATE_TYPE, window));

	}
}
