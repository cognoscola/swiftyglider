package com.bondfire.swiftyglider.states;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.bondfire.app.services.GameParticipant;
import com.bondfire.app.services.GameRoom;
import com.bondfire.swiftyglider.SwiftyGlider;
import com.bondfire.swiftyglider.network.CollisionMessage;
import com.bondfire.swiftyglider.network.EnvironmenMessage;
import com.bondfire.swiftyglider.network.GameStateMessage;
import com.bondfire.swiftyglider.network.LevelSyncMessage;
import com.bondfire.swiftyglider.network.PositionMessage;
import com.bondfire.swiftyglider.network.WallMessage;
import com.bondfire.swiftyglider.sprites.Glider;
import com.bondfire.swiftyglider.sprites.Indicator;
import com.bondfire.swiftyglider.sprites.Wall;
import com.bondfire.swiftyglider.ui.WhiteButton;

/** our play state. */
public class PlayState extends State {

    private final static String TAG = "PlayState";
    private final static boolean d_updateRoom = true;

    /** set the max number of fingers */
    private final int MAX_FINGERS = 0;

    /** save points */
    public final static int LV_BEGINNING   = 0;
    public final static int LV_FIRSTWIND   = 73;
    public final static int LV_GOINGFAST   = 149;
    public final static int LV_WINDFAST    = 250;
    public final static int LV_SUPERSLOW   = 275;
    public final static int LV_WINDSLOW    = 349;
    public final static int LV_EYEOFNEEDLE = 399;

    private final static float SCALE_GAPLENGTH_150   = 0.228659f; //scales gap length 130
    private final static float SCALE_GAPLENGTH_220   = 0.335366f; //scales gap length 220
    private final static float SCALE_GAPLENGTH_230   = 0.350619f; //scales gap length 230
    private final static float SCALE_GAPLENGTH_C_25  = 0.038110f; //scales gap length 25 + glider
    private final static float SCALE_GAPLENGTH_C_30  = 0.045731f;//scales gap length 30 + glider
    private final static float SCALE_GAPLENGTH_C_50  = 0.076220f;//scales gap length 50 + glider

    private final static float SCALE_WIND_OFFESET_10 = 0.010163f;
    private final static float SCALE_WIND_OFFESET_50 = 0.050813f;
    private final static float SCALE_WIND_OFFESET_65 = 0.16057f;
//    private final static float SCALE_WIND_OFFESET_65 = 0.066057f;

    private int i;

    /** our sprites **/
    private Glider glider;
    private Indicator line;
    private Array<Wall> wallQueueWaiting;
    private Array<Wall> wallQueueActive;

    /** text ***/
    private WhiteButton scoreText;
    private BitmapFont bitmapFont;

    /** Game Logic */
    private int level;
    private int lastSavePoint;
    private final static float WALL_RATE_DEFAULT = 2.0f;
    private final static float LEVEL_AMPLIFICATION = 0.01f;
    private boolean isOnSaveLevels = false;

    private static float gapLength = 100f ;
    private static boolean colliding = false;

    /** timing logic */
    static float wallTimer = 0f;      //timer and also the initial start time
    static float wallAppearanceFrequency = 3f;  //frequency of wall appearance
    static float indicatorTimer = 2f; //timer for indicator
    static float indicatorFrequency = 2f; //how frequently we should display the white line
    static float deathTimer = 0f; //keeps track of time passed since death;
    private static final float DEATH_TIME = 1f; //amount of time to pass before showing score screen

    /** wind logic */
    private float windHeightOffset          = 0f;//determines the amount of leeway room for the amount of wind to change
    private float windSafetyTimer           = 0f;
    private boolean windDistanceSafetyLatch = false;
    private float WIND_MAX_TIMER            = 3f;
    static int WIND_CHANCE                  = 2;            //Likely hood out of 100 that the wind will change

    static boolean collidingLatch = false;

    /** ONLINE STUFF */
    private Array<Glider> opponentGliders;
    private static boolean isEveryoneElseDead = false;
    private float positionUpdateTimer = 0.0f;
    private static float MAX_POSITION_UPDATE_TIMER = 0.03f;  //50 messages per second

    private boolean isMultiplayerMode;

    private static PositionMessage outPositionMessage; //outgoing position message
    private static PositionMessage inPositionMessage;  //incoming position message
    private static WallMessage inWallMessage;          //outgoing wall message
    private static WallMessage outWallMessage;         //incoming wall message
    private static EnvironmenMessage inEnvMessage;
    private static EnvironmenMessage outEnvMessage;
    private static CollisionMessage inCollisionMessage; //incoming collision message
    private static CollisionMessage outCollisionMessage; //outgoing collion message
    private static GameStateMessage inStateMessage;   //incoming game state message
    private static GameStateMessage outStateMessage;  //outgoing game state message
    private static LevelSyncMessage inLevelMessage;
    private static LevelSyncMessage outLevelMessage;


    public PlayState(GSM gsm, int level, GameRoom room, boolean multiPlayer) {

        super(gsm);

        this.isMultiplayerMode = multiPlayer;

        SwiftyGlider.keepScreenOn();

        /** load our sprites */
        glider = new Glider(SwiftyGlider.WIDTH / 2, SwiftyGlider.HEIGHT / 4);
        line = new Indicator(SwiftyGlider.WIDTH / 2, 0, SwiftyGlider.WIDTH, 50);
        wallQueueWaiting = new Array<Wall>();
        wallQueueActive = new Array<Wall>();

        /**prepare out text*/
        bitmapFont = SwiftyGlider.res.getBmpFont();
        opponentGliders = new Array<Glider>();
        reset();

        if (isMultiplayerMode) {
            if (roomExists() && SwiftyGlider.room.isConnected() && SwiftyGlider.room.getParticipants() != null) {

                outPositionMessage = new PositionMessage();
                inWallMessage = new WallMessage();
                outWallMessage = new WallMessage();
                inEnvMessage = new EnvironmenMessage();
                outEnvMessage = new EnvironmenMessage();
                inCollisionMessage = new CollisionMessage();
                outCollisionMessage = new CollisionMessage();
                inStateMessage = new GameStateMessage();
                outStateMessage = new GameStateMessage();
                inLevelMessage = new LevelSyncMessage();
                outLevelMessage = new LevelSyncMessage();

                //this is online mode


                for (GameParticipant participant : room.getParticipants()) {

                    //if this is us
                    if (participant.getParticipantId() == room.getClientId()) {
                        glider.setParticipantId(participant.getParticipantId());
                        glider.setDisplayName(participant.getParticipantName().substring(0, 2) + ".");
                        glider.setIsOpponent(false);
                        continue;
                    }

                    //this is other people, only add if they are not busy
                    if (participant.getPlayerStatus() != GameParticipant.STATUS_BUSY) {
                        Glider glider = new Glider(SwiftyGlider.WIDTH / 2, SwiftyGlider.HEIGHT / 4);
                        glider.setParticipantId(participant.getParticipantId());
                        glider.setDisplayName(participant.getParticipantName().substring(0, 2) + ".");
                        glider.setIsOpponent(true);
                        opponentGliders.add(glider);
                    }
                }
                if (room.isHost()) {
                    setLevel(level);
                }
            }
        } else {
            setLevel(level);
        }
    }

    public void reset(){
        gapLength = 100f;
        colliding = false;
        wallTimer = 0f;
        deathTimer = 0f;
        wallAppearanceFrequency = 3f;
        collidingLatch = false;
        windDistanceSafetyLatch = true;

        wallQueueActive.clear();
        wallQueueWaiting.clear();
    }

    public void setLevel(int level){
        System.out.println(TAG + " setLEvel(level):  " + level);
        glider.setWind(0);
        lastSavePoint = level;
        this.level = level;

        /** Update the environment when we start the game or reach a save point*/
        if(level >= LV_EYEOFNEEDLE){
            gapLength = SwiftyGlider.WIDTH*Glider.SCALE_GLIDER +SwiftyGlider.WIDTH*SCALE_GAPLENGTH_C_50;
            wallTimer =  (WALL_RATE_DEFAULT - (level - LV_SUPERSLOW - 200) * LEVEL_AMPLIFICATION) * 0.5f;
        }else if(level >=LV_WINDSLOW ) {
            Wall.setWallLifeTime(10f);
            windHeightOffset = SwiftyGlider.HEIGHT * SCALE_WIND_OFFESET_10;
            gapLength = SwiftyGlider.WIDTH * Glider.SCALE_GLIDER + SwiftyGlider.WIDTH * SCALE_GAPLENGTH_C_30;
            wallQueueActive.add(new Wall(SwiftyGlider.WIDTH, gapLength,0.25f));
            wallQueueActive.add(new Wall(SwiftyGlider.WIDTH, gapLength,0.01f));
        }else  if(level >= LV_SUPERSLOW) {
            gapLength = SwiftyGlider.WIDTH * Glider.SCALE_GLIDER + SwiftyGlider.WIDTH * SCALE_GAPLENGTH_C_25;
            Wall.setWallLifeTime(10f);
            /** because this level is super slow, the walls already part way down */
            wallQueueActive.add(new Wall(SwiftyGlider.WIDTH, gapLength,0.25f));
            wallQueueActive.add(new Wall(SwiftyGlider.WIDTH, gapLength,0.01f));

        } else if(level >= LV_WINDFAST) {
            Wall.setWallLifeTime(2f);
            windHeightOffset = SwiftyGlider.HEIGHT * SCALE_WIND_OFFESET_65;
            gapLength = SwiftyGlider.WIDTH * SCALE_GAPLENGTH_230;
            wallQueueActive.add(new Wall(SwiftyGlider.WIDTH, gapLength,0.0f));
        }else if(level >= LV_GOINGFAST) {
            Wall.setWallLifeTime(2f);
            windHeightOffset = SwiftyGlider.HEIGHT * SCALE_WIND_OFFESET_50;
            gapLength =  SwiftyGlider.WIDTH*SCALE_GAPLENGTH_220;
            wallQueueActive.add(new Wall(SwiftyGlider.WIDTH, gapLength,0.0f));
        }else if(level >=LV_FIRSTWIND ) {
            Wall.setWallLifeTime(5f);
            windHeightOffset = SwiftyGlider.HEIGHT * SCALE_WIND_OFFESET_50;
            gapLength =  SwiftyGlider.WIDTH*SCALE_GAPLENGTH_220;
            wallQueueActive.add(new Wall(SwiftyGlider.WIDTH, gapLength,0.0f));
        } else {
            Wall.setWallLifeTime(5f);
            windHeightOffset = SwiftyGlider.HEIGHT * SCALE_WIND_OFFESET_10;
            gapLength = SwiftyGlider.WIDTH*SCALE_GAPLENGTH_150;
            wallQueueActive.add(new Wall(SwiftyGlider.WIDTH, gapLength,0.0f));
        }

        // send out results if there
        if (isMultiplayerMode && roomExists()) {

            if (SwiftyGlider.room.isConnected() && SwiftyGlider.room.isHost()) {

                wallQueueActive.clear();

                outEnvMessage.wind = 0;
                outEnvMessage.windHeightOffset = windHeightOffset;
                outEnvMessage.messageType = SwiftyGlider.MESSAGE_TYPE_ENV;

                for (int i = 0; i < opponentGliders.size; i++) {

                    Gdx.app.log(TAG,"setLevel() SENT ENV MESSAGE ");
                    Glider glider = opponentGliders.get(i);
                    SwiftyGlider.realTimeService.getSender().OnRealTimeMessageSend(
                            glider.getParticipantId(),
                            SwiftyGlider.json.toJson(outEnvMessage),
                            true
                    );
                }
            }
        }
    }

    @Override
    public void handleInput() {
        /** we're going to try multitouch */
        /** use the vector3 to grab the mouse position */
        /** we only want max MAX_FINGERS inputs available */
        for(i = 0; i < MAX_FINGERS;i++){

            /** check if the pointer are pressed */
            if(Gdx.input.isTouched()){

                mouse.x = Gdx.input.getX(i);
                mouse.y = Gdx.input.getY(i);

                /** change the coordinates from screen coordinates to the world coordinate */
                cam.unproject(mouse);

                /** find out if our object was clicked */
                glider.setX(mouse.x);
                glider.setY(mouse.y);
            }
        }
    }

    @Override
    public void update(float dt) {

        wallTimer += dt;
        indicatorTimer += dt;

        if (isMultiplayerMode && roomExists() && SwiftyGlider.room.isConnected()) {

            /** Update this state*/
            checkDeath(dt);
            updateWallState();
            checkCollision();
            updatePlayers(dt);
            updateWallPositions(dt);
            if (SwiftyGlider.room.isHost()) {
                checkIndicatorRate();
            }

        } else {

            checkDeath(dt);
            updateWallState();
            checkIndicatorRate();
            checkCollision();
            calculateWeather(dt);
            handleInput();
            updatePlayers(dt);
            line.update(dt);
            updateWallPositions(dt);
        }
    }

    private void updateWallPositions(float dt){
        /** for each wall, update them */
        for (int i = 0; i < wallQueueActive.size; i++) {
            wallQueueActive.get(i).update(dt);
        }
    }


    private void calculateWeather(float dt){
        if(level > 399){
            /** at this point user knows and has competency of all level types
             * Now things get really difficult */
        }else{
            /** this first part is design to take the user through the different types of walls*/
            if (level > 349) {
                makeWind(2,dt);
                WIND_CHANCE = 5;
            } else if (level > 274) {

            } else if (level > 255) { /** anoying fast wind */
                makeWind(2,dt);
                WIND_CHANCE = 10;

            } else if (level > 149) {
                /** make walls go fucking fast */

            } else if (level > 74) {
                /**slowly increase the likelyhood of wind change of changing */
                makeWind(1,dt);

                if (level > 140) {
                    WIND_CHANCE = 10;
                } else if (level > 120) {
                    WIND_CHANCE = 5;
                } else {
                    WIND_CHANCE = 1;
                }
            }
        }
    }

    private void makeWind(int strength, float dt){

        /** make sure the wind doesn't change too quickly */
        windSafetyTimer +=dt;
        if(windSafetyTimer < WIND_MAX_TIMER ){
            return;
        }

        /** make sure the wind changes while the player's height is not the same as a wall's height, changes
         * must happen in between walls.*/
        windSafetyTimer = WIND_MAX_TIMER;
        if(MathUtils.random(100) < WIND_CHANCE && !windDistanceSafetyLatch){
            int wind = MathUtils.random(strength*2) - strength;
            glider.setWind(wind);
            ((BackgroundState)gsm.getBackground()).setWind(-wind);
            windSafetyTimer = 0;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        /** Before we draw anything, we always, always need to set the camera */
        sb.setProjectionMatrix(cam.combined);
        sb.begin();

        if (isMultiplayerMode && roomExists() && SwiftyGlider.room.isConnected()) {
            for(int i = 0; i < wallQueueActive.size; i++){
                wallQueueActive.get(i).render(sb);
            }

            for (int i = 0; i < opponentGliders.size; i++) {
                Glider glider = opponentGliders.get(i);
                glider.render(sb);
            }

            glider.render(sb);

        }else{

            line.render(sb);
            for(int i = 0; i < wallQueueActive.size; i++){
                wallQueueActive.get(i).render(sb);
            }
            glider.render(sb);

            /** render our score */
            if(scoreText != null )
                scoreText.render(sb);
        }
        sb.end();
    }

    private void checkIndicatorRate(){

        if(indicatorTimer  >= indicatorFrequency -0.95f && !collidingLatch){
            line.reset();
        }

        if(indicatorTimer >= indicatorFrequency){
            indicatorTimer = 0f;
            updateScore(level++);
            updateWallFrequency();

            if (isMultiplayerMode && roomExists()) {
                if (SwiftyGlider.room.isHost()) {
                    outLevelMessage.messageType = SwiftyGlider.MESSAGE_TYPE_LEVEL;
                    outLevelMessage.level = level;

                    for (int i = 0; i < opponentGliders.size; i++) {
                        Glider glider = opponentGliders.get(i);
                        SwiftyGlider.realTimeService.getSender().OnRealTimeMessageSend(
                                glider.getParticipantId(),
                                SwiftyGlider.json.toJson(outLevelMessage),
                                true
                        );
                    }
                }
            }
        }
    }

    private void updateScore(int level){

        scoreText = new WhiteButton(
                bitmapFont,
                "" + level,
                SwiftyGlider.WIDTH * 5/6,
                SwiftyGlider.HEIGHT /8
        );
    }

    private void checkDeath(float dt){
        if(collidingLatch){
            deathTimer += dt;
            if(deathTimer > DEATH_TIME){
                ((BackgroundState)gsm.getBackground()).setWind(0);

                Gdx.app.log(TAG, "checkDeath() isMultiplayer:" + isMultiplayerMode);
                Gdx.app.log(TAG, "checkDeath() roomExists: " + isMultiplayerMode);
                Gdx.app.log(TAG, "checkDeath() roomExists: " + SwiftyGlider.room.isHost());


                if (isMultiplayerMode && roomExists()) {
                    //if everyone is dead except one person, tell everyone to stop the game

                    if (SwiftyGlider.room.isHost()) {
                        isEveryoneElseDead = true;
                        for (int i = 0; i < opponentGliders.size; i++) {
                            //while someone is still alive
                            if (!(opponentGliders.get(i).isDead())) {
                                isEveryoneElseDead = false;
                                break;
                            }
                        }

                        //if everyone is dead
                        if (isEveryoneElseDead && collidingLatch) {
                            //send out stop message

                            outStateMessage.messageType = SwiftyGlider.MESSAGE_TYPE_ACTION;
                            outStateMessage.actionType = SwiftyGlider.TYPE_GAME_STOP;

                            for (int i = 0; i < opponentGliders.size; i++) {
                                Glider glider = opponentGliders.get(i);
                                SwiftyGlider.realTimeService.getSender().OnRealTimeMessageSend(
                                        glider.getParticipantId(),
                                        SwiftyGlider.json.toJson(outStateMessage),
                                        true
                                );
                            }
                            gsm.set(new MultiplayerMenuState(gsm, SwiftyGlider.room, true));
                            SwiftyGlider.stopKeepingScreenOn();
                        }
                    }

                } else{
                    gsm.set(new ScoreState(gsm,lastSavePoint, level - 1));
                    SwiftyGlider.stopKeepingScreenOn();
                }

                if( SwiftyGlider.adController != null){
                    SwiftyGlider.adController.setAdVisibility(true);
                }
            }
        }
    }

    private void updatePlayers(float dt){
        glider.update(dt);

        if (isMultiplayerMode && roomExists() && SwiftyGlider.room.isConnected()) {
            //update the opponent player animations
            for (int i = 0; i < opponentGliders.size; i++) {
                Glider glider = opponentGliders.get(i);
                glider.update(dt);
            }


            //if we're connected and we're not dead
            if (SwiftyGlider.room.isConnected() && !collidingLatch) {
                positionUpdateTimer +=dt;
                if (positionUpdateTimer > MAX_POSITION_UPDATE_TIMER) {

                    outPositionMessage.x = this.glider.getX();
                    outPositionMessage.y = this.glider.getY();
                    outPositionMessage.messageType = SwiftyGlider.MESSAGE_TYPE_POSITION;

                    for (int i = 0; i < opponentGliders.size; i++) {

                        Glider glider = opponentGliders.get(i);
                        SwiftyGlider.realTimeService.getSender().OnRealTimeMessageSend(
                                glider.getParticipantId(),
                                SwiftyGlider.json.toJson(outPositionMessage),
                                false
                        );
                    }
                    positionUpdateTimer = 0;
                }
            }
        }
    }

    private void updateWallState() {

        /** for each wall in the active queue, check if they are done.*/
        for (i = 0; i < wallQueueActive.size; i++) {
            Wall wall = wallQueueActive.get(i);

            /** if yes, put them in the waitQueue*/
            if (wall.isDone()) {
                wallQueueWaiting.add(wall);
                wallQueueActive.removeIndex(i);
            }
        }

        if (isMultiplayerMode) {
            if (roomExists() && SwiftyGlider.room.isHost()) {
                checkNextWallReady();
            }
        }else{
            checkNextWallReady();
        }
    }

    /**
     * Checks if we are ready to display the next wall
     */
    private void checkNextWallReady() {

        if (isMultiplayerMode) {
            if (roomExists() && SwiftyGlider.room.isConnected() && SwiftyGlider.room.isHost()) {
                /** Check if it is time to put a new wall on the screen */
                if (wallTimer >= wallAppearanceFrequency) {
                    Gdx.app.log(TAG, "checkNextWallReady() WallTimer in Multiplayer!");
                    /** if yes, fetch a wall from the waitQueue and reset it*/
                    Wall wall = getANonActiveWall();
                    wall.RecycleWall(SwiftyGlider.WIDTH, gapLength);

                    //pass it to other players first
                    outWallMessage.gapLength = wall.getGapLength();
                    outWallMessage.gapPosition = wall.getGapPosition();
                    outWallMessage.lifeTime = Wall.getMaxWallLifetime();
                    outWallMessage.messageType = SwiftyGlider.MESSAGE_TYPE_WALL;

                    Gdx.app.log(TAG, "checkNextWallReady() SENT WALL");

                    if (SwiftyGlider.room.isConnected()) {
                        for (int i = 0; i < opponentGliders.size; i++) {
                            Glider glider = opponentGliders.get(i);
                            SwiftyGlider.realTimeService.getSender().OnRealTimeMessageSend(
                                    glider.getParticipantId(),
                                    SwiftyGlider.json.toJson(outWallMessage),
                                    true
                            );
                        }
                    }

                    /**if we havent crashed yet */
                    wallQueueActive.add(wall);
                    wallTimer = 0;
                    /** Update Wall frequency ()*/
                }

            }
        } else {
            if (wallTimer >= wallAppearanceFrequency) {
                Gdx.app.log(TAG, "checkNextWallReady() WallTimer in Single player!");

                /** if yes, fetch a wall from the waitQueue and put it into the activeQueue if waitQueue
                 * is empty just make a new wall.*/
                if (!isOnSaveLevels) { /** don't make new walls while on safe levels */
                    Wall wall = getANonActiveWall();
                    /**if we havent crashed yet */
                    if (!collidingLatch) {
                        Gdx.app.log(TAG, "checkNextWallReady() Recyling wall!");
                        wall.RecycleWall(SwiftyGlider.WIDTH, gapLength);
                        wallQueueActive.add(wall);
                    }
                }
                wallTimer = 0;
                /** Update Wall frequency ()*/
            }
        }
    }


    /**
     * Returns a wall not currently active
     * @return a wall
     */
    private Wall getANonActiveWall(){
        Wall wall;
        if(wallQueueWaiting.size != 0)
            wall = wallQueueWaiting.pop();
        else{
            wall = new Wall(SwiftyGlider.WIDTH, gapLength);
        }
        return wall;
    }


    private void updateWallFrequency() {
        System.out.println(TAG + " updateFrequency() Level:" + level);
        if (level > LV_EYEOFNEEDLE) {
            isOnSaveLevels = false;
            wallAppearanceFrequency = WALL_RATE_DEFAULT - (level - LV_SUPERSLOW - 200) * LEVEL_AMPLIFICATION;
            Wall.setWallLifeTime(1.5f);
        } else if (level > LV_EYEOFNEEDLE - 1) {
            isOnSaveLevels = true;
            setLevel(level);
            Wall.setWallLifeTime(2);
        } else if (level > LV_WINDSLOW) {
            wallAppearanceFrequency = WALL_RATE_DEFAULT - (level - LV_SUPERSLOW - 210) * LEVEL_AMPLIFICATION;
            isOnSaveLevels = false;
        } else if (level > LV_WINDSLOW - 1) {
            isOnSaveLevels = true;
            setLevel(level);
        } else if (level >= LV_SUPERSLOW) {
            wallAppearanceFrequency = WALL_RATE_DEFAULT - (level - LV_SUPERSLOW - 110) * LEVEL_AMPLIFICATION;
            isOnSaveLevels = false;
        } else if (level > LV_SUPERSLOW - 1) {
            isOnSaveLevels = true;
            setLevel(level);
        } else if (level > LV_WINDFAST) {
            wallAppearanceFrequency = WALL_RATE_DEFAULT - (level - LV_WINDFAST + 40) * LEVEL_AMPLIFICATION;
            isOnSaveLevels = false;
        } else if (level > LV_WINDFAST - 1) {
            isOnSaveLevels = true;
            setLevel(level);
        } else if (level > LV_GOINGFAST) {
            wallAppearanceFrequency = WALL_RATE_DEFAULT - (level - LV_GOINGFAST + 40) * LEVEL_AMPLIFICATION;
            isOnSaveLevels = false;
        } else if (level > LV_GOINGFAST - 1) { //148
            setLevel(level);
        } else if (level > LV_GOINGFAST - 3) {
            isOnSaveLevels = true;
        } else if (level >= LV_FIRSTWIND) {
            isOnSaveLevels = false;
            wallAppearanceFrequency = WALL_RATE_DEFAULT - (level - LV_FIRSTWIND) * LEVEL_AMPLIFICATION;
        } else if (level >= LV_FIRSTWIND - 1) {
            setLevel(level + 1);
        } else if (level >= LV_FIRSTWIND - 2) {
            isOnSaveLevels = true;
        } else {
            isOnSaveLevels = false;
            wallAppearanceFrequency = WALL_RATE_DEFAULT - (level )* LEVEL_AMPLIFICATION;
        }
    }

    public void checkCollision(){

        /** check if we are colliding with any walls */
        for (i = 0; i < wallQueueActive.size; i++) {
            Wall wall = wallQueueActive.get(i);

            if (wall != null) {
                /** Are we colliding with any walls?*/
                colliding = wall.colliding(glider.getX(), glider.getY(), glider.getWidth(), glider.getHeight());
                glider.setColliding(colliding);


                /** We don't want the colliding value to change back to false by
                 * checking another wall it is not colliding with, so terminate the loop*/
                if (colliding) {
                    i = wallQueueActive.size;

                    if (colliding && !collidingLatch) {

                        //vibrate our client
                        if (SwiftyGlider.appType == Application.ApplicationType.Android) {
                            Gdx.input.vibrate(200);
                        }
                        collidingLatch = true;

                        //let everyone else know we crashed if needed
                        tellEveryoneElseIdied();
                    }
                }
            }
        }

        /** check if it is safe to switch wind */
        for (i = 0; i < wallQueueActive.size; i++) {
            Wall wall = wallQueueActive.get(i);
            if (wall != null) {
                /** Are we colliding with the left side? */
                windDistanceSafetyLatch = wall.colliding(glider.getX(), glider.getY(), glider.getWidth()*10, glider.getHeight() + windHeightOffset);
//                glider.setWindCollide(windDistanceSafetyLatch);
                if(windDistanceSafetyLatch){
                    i = wallQueueActive.size;
                }
            }
        }
    }

    //handle room stuff
    public boolean roomExists(){
        return SwiftyGlider.room != null;
    }

    /**
     * Receive game messages sent by other clients
     * @param message game message
     * @param senderId the sender participant ID
     */
    public void receiveMessage(String message, String senderId) {

        // POSITION UPDATE FROM A PLAYER
        if (message.contains(SwiftyGlider.MESSAGE_TYPE_POSITION)) {
            inPositionMessage = SwiftyGlider.json.fromJson( PositionMessage.class,message);
            for (int i = 0; i < opponentGliders.size; i++) {
                Glider glider = opponentGliders.get(i);
                if (glider.getParticipantId().equals(senderId)) {
                    glider.setX(inPositionMessage.x);
                    glider.setY(inPositionMessage.y);
                    break;
                }
            }
        }

        //WALL INCOMING FROM A HOST
        if (message.contains(SwiftyGlider.MESSAGE_TYPE_WALL)) {
            inWallMessage = SwiftyGlider.json.fromJson(WallMessage.class, message);
            /**if we havent crashed yet */

            Wall wall = getANonActiveWall();
            wall.RecycleWall(SwiftyGlider.WIDTH, inWallMessage.gapLength, inWallMessage.gapPosition);
            Wall.setMaxWallLifetime(inWallMessage.lifeTime);
            wallQueueActive.add(wall);
        }

        if (message.contains(SwiftyGlider.MESSAGE_TYPE_ENV)) {
            inEnvMessage = SwiftyGlider.json.fromJson(EnvironmenMessage.class, message);
            if(!collidingLatch) {
                this.windHeightOffset = inEnvMessage.windHeightOffset;
                this.glider.setWind(inEnvMessage.wind);
            }
        }

        if (message.contains((SwiftyGlider.MESSAGE_TYPE_COLLIDE))) {
            inCollisionMessage = SwiftyGlider.json.fromJson(CollisionMessage.class, message);
            for (int i = 0; i < opponentGliders.size; i++) {
                Glider glider = opponentGliders.get(i);
                if (glider.getParticipantId().equals(senderId)) {
                    glider.setColliding(true);
                }
            }
        }

        if (message.contains(SwiftyGlider.MESSAGE_TYPE_ACTION)) {
            Gdx.app.log(TAG, "onGameMessageReceived() RECEIVED  GameStateMessage");

            inStateMessage = SwiftyGlider.json.fromJson(GameStateMessage.class, message);
            if (inStateMessage.actionType == SwiftyGlider.TYPE_GAME_STOP) {
                gsm.set(new MultiplayerMenuState(gsm, SwiftyGlider.room,true));
                SwiftyGlider.stopKeepingScreenOn();
            }
        }

        if (message.contains(SwiftyGlider.MESSAGE_TYPE_LEVEL)) {

            inLevelMessage = SwiftyGlider.json.fromJson(LevelSyncMessage.class, message);
            Gdx.app.log(TAG,"receiveMessage() LEVEL + " + inLevelMessage.level);
            level = inLevelMessage.level;
        }

    }

    /**
     * Update the room because we have received a roomConfiguration changed signal.
     * Someone joined or left or changed their configuration
     * @param incomingGameRoom
     */
    public void updateRoom(GameRoom incomingGameRoom) {
        if(d_updateRoom) Gdx.app.log(TAG, "updateRoom() ");

        //compare this room to the incoming room, to see whats up happening
        //Has someone left, or joined?
        if (incomingGameRoom.getParticipants().size != opponentGliders.size + 1) {
            if(d_updateRoom) Gdx.app.log(TAG, "updateRoom() Room Sizes don't match!");

            //someone joined
            if (incomingGameRoom.getParticipants().size > SwiftyGlider.room.getParticipants().size) {
                //Do nothing right now
            }

            //someone left
            if (incomingGameRoom.getParticipants().size < (opponentGliders.size  + 1)) {
                if(d_updateRoom) Gdx.app.log(TAG, "updateRoom() Someone left");

                //find out who left
                boolean foundMatch = false;


                //we didn't leave so scan through through the opponents list
                for (int i = 0; i < opponentGliders.size; i++) {

                    Glider glider = opponentGliders.get(i);
                    foundMatch = false;

                    if (d_updateRoom)
                        Gdx.app.log(TAG, "updateRoom() Checking if " + glider.getDisplayName() + " left");

                    for (int j = 0; j < incomingGameRoom.getParticipants().size; j++) {

                        GameParticipant participant = incomingGameRoom.getParticipants().get(j);
                        if (participant.getParticipantId().equals(glider.getParticipantId())) {
                            Gdx.app.log(TAG, "updateRoom() "+glider.getDisplayName() + " is still here");
                            foundMatch = true;
                            break;
                        }
                    }

                    // Check if we found this person in the new room configuration
                    if (!foundMatch) {
                        if (d_updateRoom)
                            Gdx.app.log(TAG, "updateRoom() Setting " + glider.getDisplayName() + " dead");
                        //mark this glider as DEAD
                        glider.setColliding(true);
                        break;
                    }
                }
            }
        }

        //Check player statuses
        for (int i = 0; i < incomingGameRoom.getParticipants().size; i++) {
            GameParticipant gameParticipant = incomingGameRoom.getParticipants().get(i);

            //one of the participants became busy, kill him if he is in the round
            if (gameParticipant.getPlayerStatus() == GameParticipant.STATUS_BUSY) {
                for (int j = 0; j < opponentGliders.size; j++) {
                    Glider glider = opponentGliders.get(j);
                    glider.setColliding(true);
                    break;
                }
            }
        }
    }

    private void tellEveryoneElseIdied(){
        if (isMultiplayerMode && roomExists()) {
            if (SwiftyGlider.room.isConnected()) {
                outCollisionMessage.messageType = SwiftyGlider.MESSAGE_TYPE_COLLIDE;
                for (int i = 0; i < opponentGliders.size; i++) {
                    Glider glider = opponentGliders.get(i);
                    SwiftyGlider.realTimeService.getSender().OnRealTimeMessageSend(
                            glider.getParticipantId(),
                            SwiftyGlider.json.toJson(outCollisionMessage),
                            true
                    );
                }
            }
        }
    }

    public void killSelf(){
        collidingLatch = true;
        colliding = true;
        glider.setColliding(true);
        tellEveryoneElseIdied();
    }
}
