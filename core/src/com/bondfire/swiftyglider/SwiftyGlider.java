package com.bondfire.swiftyglider;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.bondfire.app.bfUtils.BlurrableSpriteBatch;
import com.bondfire.app.handler.Content;
import com.bondfire.app.services.AdController;
import com.bondfire.app.services.PlayServicesObject;


import com.bondfire.swiftyglider.states.BackgroundState;
import com.bondfire.swiftyglider.states.GSM;
import com.bondfire.swiftyglider.states.MenuState;

/** This covers LibGdx basics, expect lots of notes  */
public class SwiftyGlider extends ApplicationAdapter {

	final String VERT =
			"attribute vec4 "+ ShaderProgram.POSITION_ATTRIBUTE+";\n" +
					"attribute vec4 "+ShaderProgram.COLOR_ATTRIBUTE+";\n" +
					"attribute vec2 "+ShaderProgram.TEXCOORD_ATTRIBUTE+"0;\n" +

					"uniform mat4 u_projTrans;\n" +
					" \n" +
					"varying vec4 vColor;\n" +
					"varying vec2 vTexCoord;\n" +

					"void main() {\n" +
					"	vColor = "+ShaderProgram.COLOR_ATTRIBUTE+";\n" +
					"	vTexCoord = "+ShaderProgram.TEXCOORD_ATTRIBUTE+"0;\n" +
					"	gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
					"}";

	final String FRAG =
			//GL ES specific stuff
			"#ifdef GL_ES\n" //
					+ "#define LOWP lowp\n" //
					+ "precision mediump float;\n" //
					+ "#else\n" //
					+ "#define LOWP \n" //
					+ "#endif\n" + //
					"varying LOWP vec4 vColor;\n" +
					"varying vec2 vTexCoord;\n" +
					"uniform sampler2D u_texture;\n" +
					"uniform float bias;\n" +
					"void main() {\n" +
					"	vec4 texColor = texture2D(u_texture, vTexCoord, bias);\n" +
					"	\n" +
					"	gl_FragColor = texColor * vColor;\n" +
					"}";

	public final static float MAX_BLUR = 4f;

	/** actual game dimensions (not the screen size )*/
	public static final String TITLE= "Swifty Glider";
	public static final int WIDTH  = 480;
	public static final int HEIGHT = 800;

	public static final int ACHIEVE_PRO_GLIDER          = 0;
	public static final int ACHIEVE_SKY_GLIDER          = 1;
	public static final int ACHIEVE_PERSISTEN_GLIDER    = 2;
	public static final int ACHIEVE_MASTER_GLIDER       = 3;
	public static final int ACHIEVE_GRAND_MASTER_GLIDER = 4;

	/** our assets */
    public static Content res;

	/** tells us what kind of platform is running this game, */
	public static Application.ApplicationType appType;

	/** access to achievement group */
	public static PlayServicesObject playServices;

	/** access to the advertisement controleler serice **/
	public static AdController adController;

	/** we use it to draw */
	private BlurrableSpriteBatch sb;

	/** game state manager */
	private GSM gsm; //our gsm here

	/** so we can save the user's score */
	public static Preferences preferences;

	public static int timeInSeconds;

	public static ShaderProgram shader;

	public static float blurAmount;

	public SwiftyGlider(int time){
		timeInSeconds = time;
	}

	public void injectAchievementGroup(PlayServicesObject group){
		playServices = group;
	}

	public void injectAdController(AdController controller){
		adController = controller;
	}

	public void setBlur(float blurPercent){
		//No longer getting 4f
		this.blurAmount = blurPercent;
		if(sb != null){
			sb.setBlurAmount(blurAmount);
		}
	}

	@Override
	public void create () {
		/** first check the application type */
		appType = Gdx.app.getType();

		/** set the clear color (color that shows when everything on the screen is cleared  */
		Gdx.gl.glClearColor(0.2f,0.2f,0.2f,1);

		preferences = Gdx.app.getPreferences(TITLE);

		res = new Content();
		res.LoadAtlas("swifty.pack", "sprites"); //our path to the file and the key
		res.LoadFont("open_sans.ttf");
		res.LoadShaders("shaders/blurVertex.glsl","shaders/blurFragment.glsl","blurShader");

		/** load the blur shader */
		FileHandle[] blurfiles = res.getShaders("blurShader");
		shader = new ShaderProgram(blurfiles[0],blurfiles[1]);
		if (!shader.isCompiled()) {
			Gdx.app.log("ShaderLessons", "Could not compile shaders: "+shader.getLog());
			Gdx.app.exit();
		}

		sb = new BlurrableSpriteBatch();
		sb.setShader(shader);
		shader.begin();
		shader.setUniformf("bias", 0f);
		shader.end();

		gsm = new GSM();

		/** push our background */
		gsm.push(new BackgroundState(gsm, timeInSeconds));

		/** push the menu state*/
		gsm.push(new MenuState(gsm));
	}

	/** Game loop libgdx uses 60hz */

	@Override
	public void render () {

		if(appType == Application.ApplicationType.Desktop){
			float bias = (Gdx.input.getX() / (float)Gdx.graphics.getWidth());
			setBlur(bias);
		}
		/** CLear the screen */
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render(sb);
	}

	@Override
	public void dispose() {
		super.dispose();
		playServices = null;
	}
}
