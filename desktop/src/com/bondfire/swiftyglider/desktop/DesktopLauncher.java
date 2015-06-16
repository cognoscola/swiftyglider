package com.bondfire.swiftyglider.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.bondfire.swiftyglider.SwiftyGlider;

public class DesktopLauncher {
	public static void main (String[] arg) {

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		/** set the screen size */
		config.width = SwiftyGlider.WIDTH;
		config.height = SwiftyGlider.HEIGHT;
		config.title = SwiftyGlider.TITLE;

		new LwjglApplication(new SwiftyGlider(), config);
	}
}
