package com.bondfire.swiftyglider.states;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.bondfire.swiftyglider.SwiftyGlider;
import com.bondfire.swiftyglider.sprites.Glider;
import com.bondfire.swiftyglider.sprites.Indicator;
import com.bondfire.swiftyglider.sprites.Wall;
import com.bondfire.swiftyglider.ui.WhiteButtons;

/** our play state. */
public class PlayState extends State {

    /** set the max number of fingers */
    private final int MAX_FINGERS = 0;

    /** save points */
    public final static int LV_BEGINNING   = 0;
    public final static int LV_FIRSTWIND   = 73;
    public final static int LV_GOINGFAST   = 149;
    public final static int LV_WINDFAST    = 253;
    public final static int LV_SUPERSLOW   = 275;
    public final static int LV_WINDSLOW    = 347;
    public final static int LV_LONGSTRETCH = 399;
    public final static int LV_EYEOFNEEDLE = 479;

    public final static float SCALE_GAPLENGTH_150   = 0.228659f; //scales gap length 130
    public final static float SCALE_GAPLENGTH_220   = 0.335366f; //scales gap length 220
    public final static float SCALE_GAPLENGTH_230   = 0.350619f; //scales gap length 230
    public final static float SCALE_GAPLENGTH_C_25  = 0.038110f; //scales gap length 25 + glider
    public final static float SCALE_GAPLENGTH_C_30  = 0.045731f;//scales gap length 30 + glider
    public final static float SCALE_GAPLENGTH_C_50  = 0.076220f;//scales gap length 50 + glider

    private final static float SCALE_WIND_OFFESET_10 = 0.010163f;
    private final static float SCALE_WIND_OFFESET_50 = 0.050813f;
    private final static float SCALE_WIND_OFFESET_65 = 0.066057f;

    private int i;

    /** our sprites **/
    private Glider glider;
    private Indicator line;
    private Array<Wall> wallQueueWaiting;
    private Array<Wall> wallQueueActive;

    /** text ***/
    private WhiteButtons scoreText;
    private BitmapFont bitmapFont;

    /** Game Logic */
    private int level;
    private int lastSavePoint;
    static boolean isRateChanging = false;
    static boolean isSpedChanging = false;
    static boolean isGoingUp      = true;
    static int levelSpeed         = 1;
    private final static float WALL_RATE_DEFAULT = 2.0f;
    private final static float LEVEL_AMPLIFICATION = 0.01f;
    private boolean isOnSaveLevels = false;

    private static float gapLength = 100f ;
    private static boolean colliding = false;

    /** timing logic */
    static float wallTimer = 3f;      //timer and also the initial start time
    static float wallFrequency = 3f;  //frequency of wall appearance
    static float indicatorTimer = 2f; //timer for indicator
    static float indicatorFrequency = 2f; //how frequently we should display the white line
    static float deathTimer = 0f; //keeps track of time passed since death;
    private static final float DEATH_TIME = 1f; //amount of time to pass before showing score screen


    /** wind logic */
    private float windHeightOffset          = 0f;//determines the amount of leeway room for the amount of wind to change
    private float windSafetyTimer           = 0f;
    private boolean windDistanceSafetyLatch = false;
    private float WIND_MAX_TIMER            = 2f;
    static boolean isWindChangeable         = false;        //keep track of wind changing while mid way through a log
    static int WIND_CHANCE                  = 2;            //Likely hood out of 100 that the wind will change
    static int windRoll                     = 0;                 //Variable that keeps track of time to switch
    static int windCounter                  = 0;             //keeps track of the amount of time
    static final int MIN_WIND_TIME          = 400;    //minimum amount of time before the wind can change
    static boolean isWindLongEnough         = false; //used to tell if we haven't changed wind for a while

    static boolean collidingLatch = false;


    public PlayState(GSM gsm, int level){
        super(gsm);

        reset();
        setLevel(level);

        /** load our sprites */
        glider = new Glider(SwiftyGlider.WIDTH/2,SwiftyGlider.HEIGHT/4);
        line   = new Indicator(SwiftyGlider.WIDTH/2, 0, SwiftyGlider.WIDTH, 50);
        wallQueueWaiting = new Array<Wall>();
        wallQueueActive =  new Array<Wall>();

        /**prepare out text*/
        bitmapFont = SwiftyGlider.res.GeneratorFont();
    }

    public void reset(){
        gapLength = 100f;
        colliding = false;
        wallTimer = 3f;
        deathTimer = 0f;
        wallFrequency = 3f;
        collidingLatch = false;
    }

    public void setLevel(int level){
        lastSavePoint = level;
        this.level = level;

        /** Update the environment when we start the game or reach a save point*/
        switch(level){
            case LV_BEGINNING:
                windHeightOffset = SwiftyGlider.HEIGHT * SCALE_WIND_OFFESET_10;
                gapLength = SwiftyGlider.WIDTH*SCALE_GAPLENGTH_150;
                break;

            case LV_FIRSTWIND:
                windHeightOffset = SwiftyGlider.HEIGHT * SCALE_WIND_OFFESET_50;
                gapLength =  SwiftyGlider.WIDTH*SCALE_GAPLENGTH_220;
                break;

            case LV_GOINGFAST:
                windHeightOffset = SwiftyGlider.HEIGHT * SCALE_WIND_OFFESET_50;
                gapLength =  SwiftyGlider.WIDTH*SCALE_GAPLENGTH_220;
                break;

            case LV_WINDFAST:
                windHeightOffset = SwiftyGlider.HEIGHT * SCALE_WIND_OFFESET_65;
                gapLength =   SwiftyGlider.WIDTH*SCALE_GAPLENGTH_230;
                break;
            case LV_SUPERSLOW:
                gapLength = SwiftyGlider.WIDTH*Glider.SCALE_GLIDER + SwiftyGlider.WIDTH*SCALE_GAPLENGTH_C_25;
                break;
            case LV_WINDSLOW:
                windHeightOffset = SwiftyGlider.HEIGHT * SCALE_WIND_OFFESET_10;
                gapLength = SwiftyGlider.WIDTH*Glider.SCALE_GLIDER +SwiftyGlider.WIDTH*SCALE_GAPLENGTH_C_30;
                break;
            case LV_LONGSTRETCH:
                gapLength = SwiftyGlider.WIDTH*Glider.SCALE_GLIDER +SwiftyGlider.WIDTH*SCALE_GAPLENGTH_C_30;
                break;

            case LV_EYEOFNEEDLE:
                gapLength = SwiftyGlider.WIDTH*Glider.SCALE_GLIDER +SwiftyGlider.WIDTH*SCALE_GAPLENGTH_C_50;
                break;
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

        wallTimer +=dt;
        indicatorTimer +=dt;

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
        for(int i = 0; i < wallQueueActive.size; i++){
            wallQueueActive.get(i).update(dt);
        }
    }

    private void calculateWeather(float dt){

        if(level > 399){
            /** at this point user knows and has competency of all level types
             * Now things get really difficult */

        }else{

            /** this first part is design to take the user through the different
             * types of walls
             */


            if (level > 349) {

            } else if (level > 274) {


            } else if (level > 255) { /** anoying fast wind */



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
        if(MathUtils.random(100) < WIND_CHANCE){
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
        line.render(sb);
        for(int i = 0; i < wallQueueActive.size; i++){
            wallQueueActive.get(i).render(sb);
        }
        glider.render(sb);

        /** render our score */
        if(scoreText != null && !windDistanceSafetyLatch)
        scoreText.render(sb);

        sb.end();
    }

    private void checkIndicatorRate(){

        if(indicatorTimer  >= indicatorFrequency -0.95f && !collidingLatch){
            line.reset();
        }

        if(indicatorTimer >= indicatorFrequency){
            indicatorTimer = 0f;
            updateScore(level++);
        }
    }

    private void updateScore(int level){
        scoreText = new WhiteButtons(
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
                gsm.set(new ScoreState(gsm,lastSavePoint, level - 1));
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
        if(wallTimer >= wallFrequency && !windDistanceSafetyLatch) {

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
            updateWallFrequency();
            /** Update Wall frequency ()*/
        }

    }

    private void updateWallFrequency(){
        if (level > 399) {
            /** At this point, switch back and forth between slow and fast with predictability */
            /** start going slowly */
            if(isRateChanging){
                switch (levelSpeed){
                    case 1:  wallFrequency = WALL_RATE_DEFAULT + 2000; break;
                    case 2:  wallFrequency = WALL_RATE_DEFAULT + 1000; break;
                    case 3:  wallFrequency = WALL_RATE_DEFAULT ; break;
                    case 4:  wallFrequency = WALL_RATE_DEFAULT - 500; break;
                    case 5:  wallFrequency = WALL_RATE_DEFAULT - 600; break;
                    case 6:  wallFrequency = WALL_RATE_DEFAULT - 700; break;
                    case 7:  wallFrequency = WALL_RATE_DEFAULT - 800; break;
                    case 8:  wallFrequency = WALL_RATE_DEFAULT - 900; break;
                    case 9:  wallFrequency = WALL_RATE_DEFAULT - 1000; break;
                    case 10: wallFrequency = WALL_RATE_DEFAULT - 1100; break;
                    case 11: wallFrequency = WALL_RATE_DEFAULT - 1200; break;
                    case 12: wallFrequency = WALL_RATE_DEFAULT - 1300; break;
                    case 13: wallFrequency = WALL_RATE_DEFAULT - 1400; break;
                    case 14: wallFrequency = WALL_RATE_DEFAULT - 1500; break;
                    case 15: wallFrequency = WALL_RATE_DEFAULT - 1600; break;
                }
                isRateChanging = false;
            }
            /** but eventually make it Too difficult */
        } else {

            if(level > 394){
                return;
            } else if (level > 349) {
                wallFrequency = WALL_RATE_DEFAULT - 2750 + 5600 + -level;
            } else if (level > 347) {
                return;
            } else if (level > 274) {
                wallFrequency = WALL_RATE_DEFAULT - 2750 + 5000 - level;
            } else if (level > 273) {
                return;
            } else if (level > 254) {
                wallFrequency = WALL_RATE_DEFAULT - level * 10 + 1500;
            } else if (level > 253) {
                return;
            } else if (level > 149) {
                wallFrequency = WALL_RATE_DEFAULT - level * 10 + 800;
            }
            /** we are approaching 150, prevent walls from arriving*/
            else if (level > 147) {

            } else if (level >= 74/* && level< 150*/) {
                isOnSaveLevels = false;
                wallFrequency = WALL_RATE_DEFAULT - (level - 72)*LEVEL_AMPLIFICATION;
            } else if (level >= 72){
                isOnSaveLevels = true;
                setLevel(level);
            } else if (level < 72) {
               isOnSaveLevels = false;
                wallFrequency = WALL_RATE_DEFAULT - level*LEVEL_AMPLIFICATION;
            }
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
                windDistanceSafetyLatch = wall.colliding(glider.getX(), glider.getY(), glider.getWidth() * 10, glider.getHeight() - windHeightOffset);
                i = wallQueueActive.size;
            }
        }

    }
}
