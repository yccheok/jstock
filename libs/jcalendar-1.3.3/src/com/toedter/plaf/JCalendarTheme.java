/*
 *  Copyright (C) 2004 Kai Toedter
 *  kai@toedter.com
 *  www.toedter.com
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package com.toedter.plaf;

import java.awt.Font;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

/**
 * The JCalendar theme. The colors are based on the theme "Experience Bue" of
 * the great Plastic3D Look and feel by JGoddies (www.jgoodies.com)
 * 
 * @author Kai Toedter
 * @version $LastChangedRevision: 85 $
 * @version $LastChangedDate: 2006-04-28 13:50:52 +0200 (Fr, 28 Apr 2006) $
 */
public class JCalendarTheme extends DefaultMetalTheme {
	static final ColorUIResource secondary1 = new ColorUIResource(128, 128, 128);
	static final ColorUIResource secondary2 = new ColorUIResource(189, 190, 176);
	static final ColorUIResource secondary3 = new ColorUIResource(236, 233, 216);
	static final ColorUIResource BLUE_LOW_MEDIUM = new ColorUIResource(166, 202, 240);
	static final ColorUIResource BLUE_LOW_LIGHTEST = new ColorUIResource(195, 212, 232);
	static final ColorUIResource BLUE_MEDIUM_DARKEST = new ColorUIResource(44, 73, 135);
	static final ColorUIResource BLUE_MEDIUM_DARK = new ColorUIResource(49, 106, 196);
	static final ColorUIResource BLUE_MEDIUM_MEDIUM = new ColorUIResource(85, 115, 170);
	static final ColorUIResource ORANGE_FOCUS = new ColorUIResource(245, 165, 16);
	static final ColorUIResource GREEN_CHECK = new ColorUIResource(33, 161, 33);
	static final ColorUIResource WHITE = new ColorUIResource(255, 255, 255);
	static final FontUIResource standardFont = new FontUIResource(
			new Font("Tahoma", Font.PLAIN, 11));

	/**
	 * Creates a new JCalendarTheme object.
	 */
	public JCalendarTheme() {
	}

	public String getName() {
		return "JCalendar Theme";
	}

	protected Font getFont() {
		return new Font("Dialog", Font.PLAIN, 12);
	}

	public FontUIResource getSystemTextFont() {
		return standardFont;
	}

	public FontUIResource getUserTextFont() {
		return standardFont;
	}

	public FontUIResource getControlTextFont() {
		return standardFont;
	}

	public FontUIResource getMenuTextFont() {
		return standardFont;
	}

	protected ColorUIResource getPrimary1() {
		return BLUE_MEDIUM_DARK;
	}

	protected ColorUIResource getPrimary2() {
		return BLUE_LOW_MEDIUM;
	}

	protected ColorUIResource getPrimary3() {
		return BLUE_LOW_LIGHTEST;
	}

	protected ColorUIResource getSecondary1() {
		return secondary1;
	}

	protected ColorUIResource getSecondary2() {
		return secondary2;
	}

	protected ColorUIResource getSecondary3() {
		return secondary3;
	}

	public ColorUIResource getFocusColor() {
		return ORANGE_FOCUS;
	}

	public ColorUIResource getPrimaryControlShadow() {
		return getPrimary3();
	}

	public ColorUIResource getMenuSelectedBackground() {
		return getPrimary1();
	}

	public ColorUIResource getMenuSelectedForeground() {
		return WHITE;
	}

	public ColorUIResource getMenuItemBackground() {
		return WHITE;
	}

	public ColorUIResource getToggleButtonCheckColor() {
		return GREEN_CHECK;
	}
}