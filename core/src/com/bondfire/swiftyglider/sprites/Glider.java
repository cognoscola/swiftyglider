package com.bondfire.swiftyglider.sprites;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bondfire.app.bfUtils.BlurrableTextureAtlas;
import com.bondfire.swiftyglider.SwiftyGlider;
import com.bondfire.swiftyglider.ui.Box;

import java.util.LinkedList;

/**
 * Here is our glider class. It will render/updat everything that has to do with
 * our glider. We extend the Box because we will want to use it to check for collisions
 */
public class Glider extends Box {

    private final static String Tag = "Glider";

    public final static float SCALE_GLIDER = 0.14634f;

    private TextureRegion opponentBody;
    private TextureRegion multiplayerBody;
    private TextureRegion singleplayerBody;
    private TextureRegion explosion;
    private TextureRegion tail_left;
    private TextureRegion tail_right;

    private boolean isDead = false;
    private float MAX_TIME = 0.7f; //max time it takes for the glider to slide into screen
    private float timer;

    private float deathTimer= 0;
    private final static float TIME_DEATH = 1.5f; // amount of time to show the death texture

    private float velocity_X;
    private final static float Kconstant = 0.2f;
    private final static float Vconstant = 0.5556f;
    private final static float amplitude = 55; //measure the strength of our accelerometer signal

    /** Y movement smoothing filter */
    private LinkedList<Float> filter;
    float filterAverage = 0;
    int filterIndex = 0;
    final static int FILTER_LENGTH = 10;

    public boolean isDead() {
        return death_latch;
    }

    private boolean death_latch = false;

    /** tail flapping */
    private boolean tailFlapping        = true;
    private float   tailFlappingCounter = 0;
    private float   tailFlappingRate    = 0.1f;

    private float wind;
    BlurrableTextureAtlas atlas;

    /*** MULTIPLAYER STUFF ***/
    /** ID OF THE GLIDER (when we're in multiplayer mode */
    public String getParticipantId() {
        return participantId;
    }
    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }
    private String participantId = "";
    public void setIsOpponent(boolean isOpponent) {
        this.isOpponent = isOpponent;
    }
    private boolean isOpponent = false;

    /**name of person **/
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public String displayName;
    public static BitmapFont bitmapFont;

    private static float NETWORK_WATCH_DOG_TIMER_RESET_VALUE = 7.0f;
    private float networkWatchDogTimer;


    public Glider(float x, float y) {

        System.out.println("New Glider");
        reset();

        if (bitmapFont == null) {
            bitmapFont = SwiftyGlider.res.getBmpFont();
        }

        this.x = x;

        this.width = SCALE_GLIDER * SwiftyGlider.WIDTH;
        this.height = SCALE_GLIDER * SwiftyGlider.WIDTH;

        atlas =(BlurrableTextureAtlas)SwiftyGlider.res.getAtlas("sprites");

        singleplayerBody = atlas.findRegion("glider");
        explosion = atlas.findRegion("explosion");
        tail_left = atlas.findRegion("tail_left");
        tail_right = atlas.findRegion("tail_right");
        opponentBody = atlas.findRegion("opponent_glider");
        multiplayerBody = atlas.findRegion("me_glider");

        filter = new LinkedList<Float>();

        for(int i= 0; i < FILTER_LENGTH; i++){
            filter.add(i,(0f));
        }
        this.y = (SwiftyGlider.HEIGHT / 2 -  getYAverage() * 30);
    }

    private void reset(){
        death_latch = false;
        isDead = false;
        filterAverage = 0f;
        filterIndex = 0;
        deathTimer = 0;
        timer  = 0;
    }

    /** input method to change drawing of our sprite */
    public void setColliding(boolean b){

        isDead = b;
        if(!death_latch && isDead){
            death_latch = true;
        }
    }



    public void update(float dt) {

        if (death_latch) {
            deathTimer += dt;
        }

        //if this instance of the glider is ours, get movement
        if (!isOpponent) {
            /** Calculate our glider movement based on accelerometer */
            if (!death_latch) {

                if (SwiftyGlider.appType == Application.ApplicationType.Android
                        || SwiftyGlider.appType == Application.ApplicationType.iOS) {

                    /** Calculate X movement */
                    this.velocity_X = this.velocity_X * Vconstant + Gdx.input.getAccelerometerX() * amplitude * Kconstant + amplitude * Gdx.input.getAccelerometerX() + wind * 30;
                    this.x = this.x - velocity_X * dt;

                    /** if the horizontal position reaches the right edge,  */
                    if (this.x > (SwiftyGlider.WIDTH - width / 2)) {
                        this.x = SwiftyGlider.WIDTH - width / 2;
                        velocity_X = 0;
                        /** if the horizontal position reaches the left edge,  */
                    } else if (this.x < 0 + width / 2) {
                        this.x = 0 + width / 2;
                        velocity_X = 0;
                    }
                    this.y = (SwiftyGlider.HEIGHT / 2 - getYAverage() * 30);
                    tailFlappingRate = 0.1f + 0.01f * Gdx.input.getAccelerometerY();
                }
            }
        }


        tailFlappingCounter += dt;
        if (tailFlappingCounter > tailFlappingRate) {
            tailFlapping = !tailFlapping;
            tailFlappingCounter = 0;
        }
    }


    public float getYAverage(){
        /** Calculate Y movement */
        filterAverage = 0;

        for (filterIndex = FILTER_LENGTH - 1; filterIndex > -1; filterIndex--) {

            filterAverage = filterAverage + filter.get(filterIndex);
            if (filterIndex == 0) {
                filter.set(filterIndex, Gdx.input.getAccelerometerY());
            } else {
                filter.set(filterIndex, filter.get(filterIndex - 1));
            }
        }
        return filterAverage / FILTER_LENGTH;
    }

    public void render(SpriteBatch sb){

        if(!atlas.isBlurrable()){
            System.out.println("Blurring from Lighs");
            singleplayerBody.getTexture().getTextureData().prepare();
            atlas.PrepareBlur(singleplayerBody.getTexture().getTextureData().consumePixmap());
        }
        atlas.bind();

        if(deathTimer < TIME_DEATH){
            if(death_latch){
                sb.draw(atlas.tex,
                        x - width / 2,
                        y - height / 2,
                        width / 2,
                        height / 2,
                        width ,
                        height ,
                        1,
                        1,
                        0,// scale
                        explosion.getRegionX(),
                        explosion.getRegionY(),
                        explosion.getRegionWidth(),
                        explosion.getRegionHeight(),
                        false,
                        false);

//                sb.draw(explosion, x - width / 2, y - height / 2, width, height);
            }else {

                //Draw the singleplayerBody
                if (participantId.isEmpty()) {

                    sb.draw(atlas.tex,
                            x - width / 2,
                            y - height / 2,
                            width / 2,
                            height / 2,
                            (width - width * Math.abs(velocity_X) / 1200),
                            height,
                            1,
                            1,
                            0,// scale
                            collidingWind ? explosion.getRegionX() :  singleplayerBody.getRegionX(),
                            collidingWind ? explosion.getRegionY() :  singleplayerBody.getRegionY(),
                            collidingWind ? explosion.getRegionWidth() : singleplayerBody.getRegionWidth(),
                            collidingWind ? explosion.getRegionHeight() : singleplayerBody.getRegionHeight(),
                            false,
                            false);

                }else{

                    sb.draw(atlas.tex,
                            x - width / 2,
                            y - height / 2,
                            width / 2,
                            height / 2,
                            (width - width * Math.abs(velocity_X) / 1200),
                            height,
                            1,
                            1,
                            0,// scale
                            collidingWind ? explosion.getRegionX() : isOpponent ? opponentBody.getRegionX() : multiplayerBody.getRegionX(),
                            collidingWind ? explosion.getRegionY() : isOpponent ? opponentBody.getRegionY() : multiplayerBody.getRegionY(),
                            collidingWind ? explosion.getRegionWidth() : isOpponent ? opponentBody.getRegionWidth() : multiplayerBody.getRegionWidth(),
                            collidingWind ? explosion.getRegionHeight() : isOpponent ? opponentBody.getRegionHeight() : multiplayerBody.getRegionHeight(),
                            false,
                            false);


                }




                //draw the tail
                sb.draw(atlas.tex,
                        x - width / 2,
                        y - height - height / 2,
                        width / 2,
                        height / 2,
                        (width - width * Math.abs(velocity_X) / 1200),
                        height,
                        1,
                        1,
                        0,// scale
                        tailFlapping ? tail_left.getRegionX() : tail_right.getRegionX(),
                        tailFlapping ? tail_left.getRegionY() : tail_right.getRegionY(),
                        tailFlapping ? tail_left.getRegionWidth() : tail_right.getRegionWidth(),
                        tailFlapping ? tail_left.getRegionHeight() : tail_right.getRegionHeight(),
                        false,
                        false);
                if (getDisplayName() != null) {
                    if (!getDisplayName().isEmpty()) {
                        bitmapFont.setColor(1.0f, 1.0f, 1.0f, 1 - (SwiftyGlider.blurAmount));
                        bitmapFont.draw(sb, getDisplayName(), x - width / 3, y + 10);
                    }
                }
            }
        }
    }

    public void setWind(int wind){
        System.out.println("Wind:" + wind);
        this.wind =(float)wind;
    }

    public void setWindCollide(boolean collidingWind){
        this.collidingWind = collidingWind;
    }

    private boolean collidingWind;


    public void setY(float y){
        this.y = y;
    }
    public void setX(float x){
        this.x = x;
    }

    public float getY(){
        return this.y;
    }
    public float getX(){
        return this.x;
    }

    public float getWidth(){
        return this.width;
    }
    public float getHeight(){
        return this.height;
    }



}
