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
    private TextureRegion tail;

    private boolean isDead;

    public Glider(float x, float y, float width, float height) {

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        body = SwiftyGlider.res.getAtlas("sprites").findRegion("glider");
        tail = SwiftyGlider.res.getAtlas("sprites").findRegion("tail_left");
    }

    public void update(float dt){}
    public void render(SpriteBatch sb){


        sb.draw(body, x - width / 2, y - height / 2, width, height);


    }
}
