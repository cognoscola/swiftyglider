package com.bondfire.swiftyglider.sprites;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Logger;
import com.bondfire.swiftyglider.SwiftyGlider;
import com.bondfire.swiftyglider.ui.Box;

/**
 * Here is our glider class. It will render/updat everything that has to do with
 * our glider. We extend the Box because we will want to use it to check for collisions
 */
public class Glider extends Box {

    Logger log = new Logger("Glider");

    private TextureRegion body;
    private TextureRegion explosion;

    private boolean isDead = false;
    private float MAX_TIME = 0.7f; //max time it takes for the glider to slide into screen
    private float timer;
    private float END_Y;
    private boolean isEntering = true;

    private float velocity_X;

    private final static float Kconstant = 0.2f;
    private final static float Vconstant = 0.5556f;



    public Glider(float x, float y, float width, float height) {

        this.x = x;
        this.END_Y = y;
        this.width = width;
        this.height = height;

        body = SwiftyGlider.res.getAtlas("sprites").findRegion("glider");
        explosion = SwiftyGlider.res.getAtlas("sprites").findRegion("explosion");

    }

    /** input method to change drawing of our sprite */
    public void setColliding(boolean b){
        isDead = b;
    }

    public void update(float dt){

        if(isEntering){
            /** have the computer animate our character */

            if(this.y < END_Y){
                timer += dt;
                this.y = timer/ MAX_TIME * END_Y ;

                if(this.y > END_Y) this.y = END_Y;
            }else{
                isEntering = false;
            }
        }else{

            /** Calculate our glider movement based on accelerometer */
            if(SwiftyGlider.appType == Application.ApplicationType.Android
                    ||SwiftyGlider.appType == Application.ApplicationType.iOS){
//                this.x = Gdx.input.getAccelerometerX();
                this.x = this.x ;




            }
        }
    }

    public void render(SpriteBatch sb){
        if(isDead){
            sb.draw(explosion, x - width / 2, y - height / 2, width, height);
        }else{
            sb.draw(body, x - width / 2, y - height / 2, width, height);
        }
    }


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
