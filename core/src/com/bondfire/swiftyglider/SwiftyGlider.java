package com.bondfire.swiftyglider;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bondfire.swiftyglider.states.GSM;
import com.bondfire.swiftyglider.states.TestState;

/** This covers LibGdx basics, expect lots of notes  */
public class SwiftyGlider extends ApplicationAdapter {

	/** actual game dimensions (not the screen size )*/
	public static final String TITLE= "Swifty Glider";
	public static final int WIDTH  = 480;
	public static final int HEIGHT = 800;

	/** our assets */
	private SpriteBatch sb;
	private GSM gsm; //our gsm here

	@Override
	public void create () {

		/** set the clear color (color that shows when everything on the screen is cleared  */
		Gdx.gl.glClearColor(0.2f,0.2f,0.2f,1);

		sb = new SpriteBatch();
		gsm = new GSM();
		gsm.push(new TestState(gsm));
	}

	/** Game loop libgdx uses 60hz */
	@Override
	public void render () {
		/** CLear the screen */
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render(sb);

	}
}
