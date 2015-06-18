package com.bondfire.swiftyglider;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bondfire.swiftyglider.handler.Content;
import com.bondfire.swiftyglider.states.BackgroundState;
import com.bondfire.swiftyglider.states.GSM;
import com.bondfire.swiftyglider.states.MenuState;

/** This covers LibGdx basics, expect lots of notes  */
public class SwiftyGlider extends ApplicationAdapter {

	/** actual game dimensions (not the screen size )*/
	public static final String TITLE= "Swifty Glider";
	public static final int WIDTH  = 480;
	public static final int HEIGHT = 800;

	/** our assets */
    public static Content res;

	/** tells us what kind of platform is running this game, */
	public static Application.ApplicationType appType;

	/** we use it to draw */
	private SpriteBatch sb;

	/** game state manager */
	private GSM gsm; //our gsm here

	/** so we can save the user's score */
	public static Preferences preferences;

	@Override
	public void create () {

		/** first check the application type */
		appType = Gdx.app.getType();

		/** set the clear color (color that shows when everything on the screen is cleared  */
		Gdx.gl.glClearColor(0.2f,0.2f,0.2f,1);

		preferences = Gdx.app.getPreferences(TITLE);

		res = new Content();
		res.LoadAtlas("swiftyglider.pack", "sprites"); //our path to the file and the key
		res.LoadFont("open_sans.ttf");
		sb = new SpriteBatch();
		gsm = new GSM();

		/** push our background */
		gsm.push(new BackgroundState(gsm));

		/** push the menu state*/
		gsm.push(new MenuState(gsm));

//		gsm.push(new PlayState(gsm));
	}

	/** Game loop libgdx uses 60hz */
	@Override
	public void render () {
		/** CLear the screen */
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render(sb);
	}

	@Override
	public void dispose() {
		super.dispose();
	}
}
