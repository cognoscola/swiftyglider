package com.bondfire.swiftyglider;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.bondfire.app.bfUtils.BlurrableSpriteBatch;
import com.bondfire.app.callbacks.PlatformInterfaceController;
import com.bondfire.app.handler.Content;
import com.bondfire.app.services.AdController;
import com.bondfire.app.services.PlayServicesObject;

import com.bondfire.app.services.RealTimeMultiplayerMessage;
import com.bondfire.app.services.RealTimeMultiplayerMessageReceiver;
import com.bondfire.app.services.RealTimeMultiplayerService;
import com.bondfire.app.services.RealTimeRoom;
import com.bondfire.app.services.ServiceUtils;
import com.bondfire.swiftyglider.states.BackgroundState;
import com.bondfire.swiftyglider.states.GSM;
import com.bondfire.swiftyglider.states.MenuState;

/** This covers LibGdx basics, expect lots of notes  */
public class SwiftyGlider extends ApplicationAdapter implements RealTimeMultiplayerMessageReceiver{

	private static final String TAG = SwiftyGlider.class.getName();

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

	/** access to the realtime network**/
	public static RealTimeMultiplayerService realTimeService;

	/** access to the console's user interface **/
	public static PlatformInterfaceController paltformController;

	/** we use it to draw */
	private BlurrableSpriteBatch sb;

	/** game state manager */
	private GSM gsm; //our gsm here

	/** so we can save the user's score */
	public static Preferences preferences;

	public static int timeInSeconds;
	public static ShaderProgram shader;
	public static float blurAmount;
	public static RealTimeRoom room;


	public SwiftyGlider(int time){
		timeInSeconds = time;
	}

	public void injectAchievementGroup(PlayServicesObject group){
		playServices = group;
	}

	public void injectAdController(AdController controller){
		adController = controller;
	}

	public void injectConsoleController(PlatformInterfaceController controller){paltformController = controller;}

	public void injectRealTimeServices(RealTimeMultiplayerService rtService){
		realTimeService = rtService;
		try {
			realTimeService.setReceiver(this);
			realTimeService.getSender().bindReceiver(this);
		} catch (NullPointerException e) {
			System.out.println("Error!");
		}
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
		res.LoadAtlas("graphics/swifty.pack", "sprites"); //our path to the file and the key
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

		if (Gdx.app.getType() == Application.ApplicationType.Android) {
			paltformController.getService(ServiceUtils.REAL_TIME_SERVICE);
		}
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
	public void onGamePacketReceived(RealTimeMultiplayerMessage realTimeMultiplayerMessage) {
		Gdx.app.log(TAG,"onGamePacketReceived() ");
	}

	@Override
	public void onRoomConfigurationChanged(RealTimeRoom inRoom) {
		Gdx.app.log(TAG,"onRoomConfigurationChanged() ");
		room = inRoom;
	}

	@Override
	public void dispose() {
		super.dispose();
		playServices = null;
	}
}
