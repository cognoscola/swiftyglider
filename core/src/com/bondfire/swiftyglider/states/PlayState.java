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
import com.bondfire.swiftyglider.network.PositionMessage;
import com.bondfire.swiftyglider.sprites.Glider;
import com.bondfire.swiftyglider.sprites.Indicator;
import com.bondfire.swiftyglider.sprites.Wall;
import com.bondfire.swiftyglider.ui.WhiteButton;

/** our play state. */
public class PlayState extends State {

    private final static String TAG = "PlayState";

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
    static float wallFrequency = 3f;  //frequency of wall appearance
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
    private GameRoom room;
    private Array<Glider> opponentGliders;
    private float positionUpdateTimer = 0.0f;
    private static float MAX_POSITION_UPDATE_TIMER = 0.1f;

    private static PositionMessage outPositionMessage;
    private static PositionMessage inPositionMessage;

    public PlayState(GSM gsm, int level, GameRoom room){
        super(gsm);

        this.room = room;

        /** load our sprites */
        glider = new Glider(SwiftyGlider.WIDTH/2,SwiftyGlider.HEIGHT/4);
        line   = new Indicator(SwiftyGlider.WIDTH/2, 0, SwiftyGlider.WIDTH, 50);
        wallQueueWaiting = new Array<Wall>();
        wallQueueActive =  new Array<Wall>();

        /**prepare out text*/
        bitmapFont = SwiftyGlider.res.getBmpFont();
        reset();


        if (roomExists()) {

            outPositionMessage = new PositionMessage();

            //this is online mode
            opponentGliders = new Array<Glider>();
            for (GameParticipant participant : room.getParticipants()) {

                //if this is us
                if (participant.getParticipantId() == room.getClientId()){
                    glider.setDispayName(participant.getParticipantName());
                    glider.setParticipantId(participant.getParticipantId());
                    continue;
                }
                //this is other people
                Glider glider = new Glider(SwiftyGlider.WIDTH / 2, SwiftyGlider.HEIGHT / 4);
                glider.setParticipantId(participant.getParticipantId());
                glider.setDispayName(participant.getParticipantName());
                opponentGliders.add(glider);
            }

        }else{
            //we are playing single player mode
            setLevel(level);
        }
    }

    public void reset(){
        gapLength = 100f;
        colliding = false;
        wallTimer = 0f;
        deathTimer = 0f;
        wallFrequency = 3f;
        collidingLatch = false;
        windDistanceSafetyLatch = true;

        wallQueueActive.clear();
        wallQueueWaiting.clear();
    }

    public void setLevel(int level){
        System.out.println(TAG + " setLEvel(level):  " +  level);
        glider.setWind(0);
        lastSavePoint = level;
        this.level = level;

        /** Update the environment when we start the game or reach a save point*/
        if(level >= LV_EYEOFNEEDLE){
            gapLength = SwiftyGlider.WIDTH*Glider.SCALE_GLIDER +SwiftyGlider.WIDTH*SCALE_GAPLENGTH_C_50;
            wallTimer =  (WALL_RATE_DEFAULT - (level - LV_SUPERSLOW - 200) * LEVEL_AMPLIFICATION) * 0.5f;
        }else if(level >=LV_WINDSLOW ) {
            Wall.setDescentSpeed(10f);
            windHeightOffset = SwiftyGlider.HEIGHT * SCALE_WIND_OFFESET_10;
            gapLength = SwiftyGlider.WIDTH * Glider.SCALE_GLIDER + SwiftyGlider.WIDTH * SCALE_GAPLENGTH_C_30;
            wallQueueActive.add(new Wall(SwiftyGlider.WIDTH, gapLength,0.25f));
            wallQueueActive.add(new Wall(SwiftyGlider.WIDTH, gapLength,0.01f));
        }else  if(level >= LV_SUPERSLOW) {
            gapLength = SwiftyGlider.WIDTH * Glider.SCALE_GLIDER + SwiftyGlider.WIDTH * SCALE_GAPLENGTH_C_25;
            Wall.setDescentSpeed(10f);
            /** because this level is super slow, the walls already part way down */
            wallQueueActive.add(new Wall(SwiftyGlider.WIDTH, gapLength,0.25f));
            wallQueueActive.add(new Wall(SwiftyGlider.WIDTH, gapLength,0.01f));

        } else if(level >= LV_WINDFAST) {
            Wall.setDescentSpeed(2f);
            windHeightOffset = SwiftyGlider.HEIGHT * SCALE_WIND_OFFESET_65;
            gapLength = SwiftyGlider.WIDTH * SCALE_GAPLENGTH_230;
            wallQueueActive.add(new Wall(SwiftyGlider.WIDTH, gapLength,0.0f));
        }else if(level >= LV_GOINGFAST) {
            Wall.setDescentSpeed(2f);
            windHeightOffset = SwiftyGlider.HEIGHT * SCALE_WIND_OFFESET_50;
            gapLength =  SwiftyGlider.WIDTH*SCALE_GAPLENGTH_220;
            wallQueueActive.add(new Wall(SwiftyGlider.WIDTH, gapLength,0.0f));
        }else if(level >=LV_FIRSTWIND ) {
            Wall.setDescentSpeed(5f);
            windHeightOffset = SwiftyGlider.HEIGHT * SCALE_WIND_OFFESET_50;
            gapLength =  SwiftyGlider.WIDTH*SCALE_GAPLENGTH_220;
            wallQueueActive.add(new Wall(SwiftyGlider.WIDTH, gapLength,0.0f));
        } else {
            Wall.setDescentSpeed(5f);
            windHeightOffset = SwiftyGlider.HEIGHT * SCALE_WIND_OFFESET_10;
            gapLength = SwiftyGlider.WIDTH*SCALE_GAPLENGTH_150;
            wallQueueActive.add(new Wall(SwiftyGlider.WIDTH, gapLength,0.0f));
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

        if (roomExists()) {
            glider.update(dt);

            if (room.isConnected()) {
                positionUpdateTimer +=dt;
                if (positionUpdateTimer > MAX_POSITION_UPDATE_TIMER) {
                    for (int i = 0; i < opponentGliders.size; i++) {

                        Glider glider = opponentGliders.get(i);
                        outPositionMessage.x = this.glider.getX();
                        outPositionMessage.y = this.glider.getY();
                        outPositionMessage.messageType = SwiftyGlider.MESSAGE_TYPE_POSITION;

                        SwiftyGlider.realTimeService.getSender().OnRealTimeMessageSend(
                                glider.getParticipantId(),
                                SwiftyGlider.json.toJson(outPositionMessage),
                                false
                        );
                    }
                    positionUpdateTimer = 0;
                }
            }

        } else {

            wallTimer += dt;
            indicatorTimer += dt;

            checkDeath(dt);

            /** Update this state*/
            checkWallRate();

            checkIndicatorRate();

            /** checkCollision */
            checkCollision();

            /** weather*/
            calculateWeather(dt);

            /** update everything inside this state */
            handleInput();
            glider.update(dt);
            line.update(dt);

            /** for each wall, update them */
            for (int i = 0; i < wallQueueActive.size; i++) {
                wallQueueActive.get(i).update(dt);
            }
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

        /** safe to switch */
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

        if (roomExists()) {

            for (Glider glider : opponentGliders) {
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

                System.out.println(TAG + "CheckDeath() Died, lastSavePoint:" + lastSavePoint);
                gsm.set(new ScoreState(gsm,lastSavePoint, level - 1));

                if( SwiftyGlider.adController != null){
                    SwiftyGlider.adController.setAdVisibility(true);
                }
            }
        }
    }


    private void checkWallRate(){

        /** for each in the active queue, check if they are done.*/
       for( i = 0; i < wallQueueActive.size; i++ ){
            Wall wall = wallQueueActive.get(i);

           /** if yes, put them in the waitQueue*/
           if(wall.isDone()){
              wallQueueWaiting.add(wall);
               wallQueueActive.removeIndex(i);
           }
       }

        /** Check if it is time to put a new wall on the screen */
        if(wallTimer >= wallFrequency ) {

            /** if yes, fetch a wall from the waitQueue and put it into the activeQueue if waitQueue
             * is empty just make a new wall.*/
            if(!isOnSaveLevels){ /** don't make new walls while on safe levels */
                Wall wall;
                if(wallQueueWaiting.size != 0)
                    wall = wallQueueWaiting.pop();
                else{
                    wall = new Wall(SwiftyGlider.WIDTH, gapLength);
                }

                if(!collidingLatch) {
                    wall.RecycleWall(SwiftyGlider.WIDTH, gapLength);
                    wallQueueActive.add(wall);
                }
            }
            wallTimer = 0;
            /** Update Wall frequency ()*/
        }
    }

    private void updateWallFrequency() {
        System.out.println(TAG + " updateFrequency() Level:" + level);
        if (level > LV_EYEOFNEEDLE) {
            isOnSaveLevels = false;
            wallFrequency = WALL_RATE_DEFAULT - (level - LV_SUPERSLOW - 200) * LEVEL_AMPLIFICATION;
            Wall.setDescentSpeed(1.5f);
        } else if (level > LV_EYEOFNEEDLE - 1) {
            isOnSaveLevels = true;
            setLevel(level);
            Wall.setDescentSpeed(2);
        } else if (level > LV_WINDSLOW) {
            wallFrequency = WALL_RATE_DEFAULT - (level - LV_SUPERSLOW - 210) * LEVEL_AMPLIFICATION;
            isOnSaveLevels = false;
        } else if (level > LV_WINDSLOW - 1) {
            isOnSaveLevels = true;
            setLevel(level);
        } else if (level >= LV_SUPERSLOW) {
            wallFrequency = WALL_RATE_DEFAULT - (level - LV_SUPERSLOW - 110) * LEVEL_AMPLIFICATION;
            isOnSaveLevels = false;
        } else if (level > LV_SUPERSLOW - 1) {
            isOnSaveLevels = true;
            setLevel(level);
        } else if (level > LV_WINDFAST) {
            wallFrequency = WALL_RATE_DEFAULT - (level - LV_WINDFAST + 40) * LEVEL_AMPLIFICATION;
            isOnSaveLevels = false;
        } else if (level > LV_WINDFAST - 1) {
            isOnSaveLevels = true;
            setLevel(level);
        } else if (level > LV_GOINGFAST) {
            wallFrequency = WALL_RATE_DEFAULT - (level - LV_GOINGFAST + 40) * LEVEL_AMPLIFICATION;
            isOnSaveLevels = false;
        } else if (level > LV_GOINGFAST - 1) { //148
            setLevel(level);
        } else if (level > LV_GOINGFAST - 3) {
            isOnSaveLevels = true;
        } else if (level >= LV_FIRSTWIND) {
            isOnSaveLevels = false;
            wallFrequency = WALL_RATE_DEFAULT - (level - LV_FIRSTWIND) * LEVEL_AMPLIFICATION;
        } else if (level >= LV_FIRSTWIND - 1) {
            setLevel(level + 1);
        } else if (level >= LV_FIRSTWIND - 2) {
            isOnSaveLevels = true;
        } else {
            isOnSaveLevels = false;
            wallFrequency = WALL_RATE_DEFAULT - (level )* LEVEL_AMPLIFICATION;
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

                if(colliding) {
                    i = wallQueueActive.size;

                    if(colliding && !collidingLatch){

                        if (SwiftyGlider.appType == Application.ApplicationType.Android){
                            Gdx.input.vibrate(200);
                        }
                        collidingLatch = true;
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
        return room != null;
    }

    public void receiveMessage(String message, String senderId) {

        //Check our message data actionType
        if (message.contains(SwiftyGlider.MESSAGE_TYPE_POSITION)) {
            Gdx.app.log(TAG, "receiveMessage() POSITION UPDATE ");

            inPositionMessage = SwiftyGlider.json.fromJson( PositionMessage.class,message);
            for (Glider glider : opponentGliders) {
                if (glider.getParticipantId().equals(senderId)) {
                    glider.setX(inPositionMessage.x);
                    glider.setY(inPositionMessage.y);
                    break;
                }
            }
        }

    }

}
