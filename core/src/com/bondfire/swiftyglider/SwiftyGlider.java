package com.bondfire.swiftyglider;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Json;
import com.bondfire.app.bfUtils.BlurrableSpriteBatch;
import com.bondfire.app.callbacks.PlatformInterfaceController;
import com.bondfire.app.handler.Content;
import com.bondfire.app.services.AdController;
import com.bondfire.app.services.GameParticipant;
import com.bondfire.app.services.GameRoom;
import com.bondfire.app.services.PlayServicesObject;

import com.bondfire.app.services.RealTimeMultiplayerMessageReceiver;
import com.bondfire.app.services.RealTimeMultiplayerService;

import com.bondfire.app.services.ServiceUtils;
import com.bondfire.swiftyglider.network.GameStateMessage;
import com.bondfire.swiftyglider.states.BackgroundState;
import com.bondfire.swiftyglider.states.GSM;
import com.bondfire.swiftyglider.states.MenuState;
import com.bondfire.swiftyglider.states.MultiplayerMenuState;
import com.bondfire.swiftyglider.states.PlayState;
import com.bondfire.swiftyglider.states.State;

/** This covers LibGdx basics, expect lots of notes  */
public class SwiftyGlider extends ApplicationAdapter implements RealTimeMultiplayerMessageReceiver{

	private static final String TAG = SwiftyGlider.class.getName();

	public static Json json;

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

	public final static String MESSAGE_TYPE_ACTION   = "STATE_MESSAGE";
	public final static String MESSAGE_TYPE_POSITION = "POS_MESSAGE";
	public final static String MESSAGE_TYPE_WALL     = "WALL_MESSAGE"; //new wall incoming
	public final static String MESSAGE_TYPE_ENV      = "ENV_MESSAGE";
	public final static String MESSAGE_TYPE_COLLIDE  = "COLLIDE_MESSAGE"; //so that all clients know that they are collided
	public final static String MESSAGE_TYPE_LEVEL    = "LEVEL_MESSAGE"; //so that all clients know that they are collided
	public static int TYPE_GAME_START= 0;
	public static int TYPE_GAME_STOP = 1;
	public static int TYPE_START_ACK = 2;

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
	public static int biasLocation;

	/**networking stuff **/
	public static GameRoom room;
	public static GameStateMessage outStateMessage;
	public static GameStateMessage inStateMessage;

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

			//After we bind the game's services, find out if we should go to multiplayer room
			if(realTimeService.getSender().shouldGoToMultiplayerMenu()){
				/** push the multiplayer menu state **/
				gsm.push(new MultiplayerMenuState(gsm, room,false));
			}else{
				/** push the menu state*/
				gsm.push(new MenuState(gsm));
			}
		} catch (NullPointerException e) {
			Gdx.app.log(TAG,"injectRealTimeServices() ",e);
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
		/** first check the application actionType */
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
			Gdx.app.log("Shader Compile ERROR!!", "Could not compile shaders: "+shader.getLog());
			Gdx.app.exit();
		}

		sb = new BlurrableSpriteBatch();
		sb.setShader(shader);
		shader.begin();
		biasLocation = shader.getUniformLocation("u_bias");
		shader.setUniformf(biasLocation,0f);
//		shader.setUniformf("bias", 0f);
		shader.end();

		gsm = new GSM();

		/** push our background */
		gsm.push(new BackgroundState(gsm, timeInSeconds));

		if (Gdx.app.getType() == Application.ApplicationType.Android) {
			json = new Json();
			outStateMessage = new GameStateMessage();
			paltformController.getService(ServiceUtils.REAL_TIME_SERVICE);
			paltformController.setInformation("Swifty Glider", "Guide your character through the " +
					"obstacles by tilting your phone in various ways.", false);
		}
		//Temporarily create fake stuff
		if (Gdx.app.getType() == Application.ApplicationType.Desktop) {

			GameRoom room = new GameRoom();
			GameParticipant participant = new GameParticipant();
			participant.setParticipantName("Guillermo");
			room.getParticipants().add(participant);
			gsm.push(new PlayState(gsm, 0, room,false));
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
	public void onGameMessageReceived(String data, String senderId) {

		if (gsm.peek() instanceof PlayState) {
			((PlayState) gsm.peek()).receiveMessage(data, senderId);
		}

		if (gsm.peek() instanceof MultiplayerMenuState) {
			if (data.contains(MESSAGE_TYPE_ACTION)) {
				Gdx.app.log(TAG, "onGameMessageReceived() RECEIVED  GameStateMessage");

				inStateMessage = json.fromJson(GameStateMessage.class, data);
				if (inStateMessage.actionType == TYPE_GAME_START){
					setAddVisibiliyFalse();
					gsm.set(new PlayState(gsm,0,room,true));
				}
			}
		}
	}

	@Override
	public void onRoomConfigurationChanged(GameRoom inRoom) {
		Gdx.app.log(TAG,"onRoomConfigurationChanged() Size:" + inRoom.getParticipants().size);
		State state = gsm.peek();

		Gdx.app.log(TAG,"onRoomConfigurationChanged() I AM: " + (inRoom.isHost() ? "HOST":"GUEST"));

//before room changes
		if (state instanceof PlayState) {
			((PlayState) state).updateRoom(inRoom);
		}

//after room changes
		room = inRoom;

		if (state instanceof MultiplayerMenuState) {
			((MultiplayerMenuState) state).updateRoom();
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		if (appType == Application.ApplicationType.Android) {
			SwiftyGlider.realTimeService.getSender().DestroyGameConnection();
		}
		playServices = null;
	}

	public static void setAddVisibiliyFalse() {

		if (adController != null) {
			adController.newRequest();
			adController.setAdVisibility(false);
		}
	}

	public static void keepScreenOn() {
		if (paltformController != null) {
			paltformController.keepScreenOn();
		}
	}

	public static void stopKeepingScreenOn(){
		if (paltformController != null) {
			paltformController.stopKeepingScreenOn();
		}
	}

	@Override
	public void pause() {

		State state = gsm.peek();
		if (state instanceof PlayState) {
			((PlayState)state).killSelf();
		}

		super.pause();
	}
}
