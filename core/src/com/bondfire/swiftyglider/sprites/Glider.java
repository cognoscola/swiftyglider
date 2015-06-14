package com.bondfire.swiftyglider.sprites;

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

    private boolean isDead;
    private float timer; //Time it takes for our glider to fly into the screen
    private float MAX_TIME = 0.7f;
//    private float END_X;
    private float END_Y;

    public Glider(float x, float y, float width, float height) {

        this.x = x;
        this.END_Y = y;
        this.width = width;
        this.height = height;

        body = SwiftyGlider.res.getAtlas("sprites").findRegion("glider");
        explosion = SwiftyGlider.res.getAtlas("sprites").findRegion("explosion");

    }

    /** input method to change drawing of our sprite */
    public void setSelected(boolean b){
        isDead = true;
    }

    public void update(float dt){
        if(this.y < END_Y){
            timer += dt;
            this.y = timer/ MAX_TIME * END_Y ;

            if(this.y > END_Y) this.y = END_Y;
        }
    }


    public void render(SpriteBatch sb){
        if(isDead){
            sb.draw(explosion, x - width / 2, y - height / 2, width, height);
        }else{
            sb.draw(body, x - width / 2, y - height / 2, width, height);
        }
    }

    public float getHeight(){
        return this.height;
    }
}
