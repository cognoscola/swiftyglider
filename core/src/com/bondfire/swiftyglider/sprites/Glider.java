package com.bondfire.swiftyglider.sprites;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
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

    private TextureRegion body;
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
    static float filterAverage = 0;
    static int filterIndex = 0;
    final static int FILTER_LENGTH = 10;

    static boolean death_latch = false;

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
    private String participantId;

    /**name of person **/
    public String getDispayName() {
        return dispayName;
    }
    public void setDispayName(String dispayName) {
        this.dispayName = dispayName;
    }
    public String dispayName;

    public Glider(float x, float y) {

        System.out.println("New Glider");
        reset();

        this.x = x;

        this.width = SCALE_GLIDER * SwiftyGlider.WIDTH;
        this.height = SCALE_GLIDER * SwiftyGlider.WIDTH;

        atlas =(BlurrableTextureAtlas)SwiftyGlider.res.getAtlas("sprites");

        body = atlas.findRegion("glider");
        explosion = atlas.findRegion("explosion");
        tail_left = atlas.findRegion("tail_left");
        tail_right = atlas.findRegion("tail_right");

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

    public void update(float dt){
        if(death_latch){
            deathTimer +=dt;
        }
            /** Calculate our glider movement based on accelerometer */
            if(!death_latch) {

                if (SwiftyGlider.appType == Application.ApplicationType.Android
                        || SwiftyGlider.appType == Application.ApplicationType.iOS) {

                    /** Calculate X movement */
                    this.velocity_X = this.velocity_X * Vconstant + Gdx.input.getAccelerometerX() * amplitude * Kconstant + amplitude * Gdx.input.getAccelerometerX() + wind*30;
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
                    this.y = (SwiftyGlider.HEIGHT / 2 -  getYAverage() * 30);
                    tailFlappingRate = 0.1f + 0.01f*Gdx.input.getAccelerometerY();
                }
            }
//        }

        tailFlappingCounter +=dt;
        if(tailFlappingCounter > tailFlappingRate){
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
            body.getTexture().getTextureData().prepare();
            atlas.PrepareBlur(body.getTexture().getTextureData().consumePixmap());
        }
        atlas.bind();

        if(!(deathTimer > TIME_DEATH)){
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
            }else{

                sb.draw(atlas.tex,
                        x - width / 2,
                        y - height / 2,
                        width / 2,
                        height / 2,
                        (width - width * Math.abs(velocity_X)/1200 ) ,
                        height ,
                        1,
                        1,
                        0,// scale
                        collidingWind ?  explosion.getRegionX():body.getRegionX(),
                        collidingWind ?  explosion.getRegionY():body.getRegionY(),
                        collidingWind ?  explosion.getRegionWidth():body.getRegionWidth(),
                        collidingWind ?  explosion.getRegionHeight():body.getRegionHeight(),
                        false,
                        false);

                sb.draw(atlas.tex,
                        x - width / 2,
                        y - height - height / 2,
                        width / 2,
                        height / 2,
                        (width - width * Math.abs(velocity_X)/1200 ) ,
                        height ,
                        1,
                        1,
                        0,// scale
                        tailFlapping ?  tail_left.getRegionX():tail_right.getRegionX(),
                        tailFlapping ?  tail_left.getRegionY():tail_right.getRegionY(),
                        tailFlapping ?  tail_left.getRegionWidth():tail_right.getRegionWidth(),
                        tailFlapping ?  tail_left.getRegionHeight():tail_right.getRegionHeight(),
                        false,
                        false);

//                sb.draw(collidingWind ? explosion:body, x - width / 2, y - height / 2, (width - width * Math.abs(velocity_X)/1200 ), height);
//                sb.draw(tailFlapping ? tail_left:tail_right, x - width / 2, y - height - height / 2, (width - width * Math.abs(velocity_X)/1200 ), height);
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
