package com.bondfire.swiftyglider.sprites;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.bondfire.swiftyglider.SwiftyGlider;
import com.bondfire.swiftyglider.ui.Box;

public class Leaf extends Box {

    private TextureRegion[] leafs = new TextureRegion[3];
    private TextureRegion currentLeaf;

    private final static float MAX_TIME =30f;
    private float timer;
    private float END_Y;

    public Leaf() {

        /** assign a random location */
        this.x = MathUtils.random(SwiftyGlider.WIDTH);
        timer = MathUtils.random(MAX_TIME);

        /** get our leafy assets */
        leafs[0] = SwiftyGlider.res.getAtlas("sprites").findRegion("leaf1");
        leafs[1] = SwiftyGlider.res.getAtlas("sprites").findRegion("leaf2");
        leafs[2] = SwiftyGlider.res.getAtlas("sprites").findRegion("leaf3");

        /** assign a random leaf to use */
        currentLeaf =  leafs[MathUtils.random(2)];

        this.width = currentLeaf.getRegionWidth();
        this.height = currentLeaf.getRegionHeight();

        this.END_Y = 0 - currentLeaf.getRegionHeight();
    }

    public void update(float dt){

        if(this.y > END_Y){
            timer += dt;
            this.y = SwiftyGlider.HEIGHT - timer/ MAX_TIME * SwiftyGlider.HEIGHT;
        }
        if(this.y < END_Y) {
            timer = 0;
            this.y = SwiftyGlider.HEIGHT + height;
            this.x = MathUtils.random(SwiftyGlider.WIDTH);
            currentLeaf =  leafs[MathUtils.random(2)];
        }
    }

    public void render(SpriteBatch sb){
        sb.draw(currentLeaf,  x - width/2 , y - height/ 2, width, height);
    }
}
