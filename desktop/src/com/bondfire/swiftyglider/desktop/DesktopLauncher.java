package com.bondfire.swiftyglider.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.bondfire.swiftyglider.SwiftyGlider;

import java.util.Calendar;

public class DesktopLauncher {
	public static void main (String[] arg) {

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		/** set the screen size */
		config.width = SwiftyGlider.WIDTH;
		config.height = SwiftyGlider.HEIGHT;
		config.title = SwiftyGlider.TITLE;

		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);

		new LwjglApplication(new SwiftyGlider(hour * 60 * 60 + minute * 60 + second), config);
	}
}
