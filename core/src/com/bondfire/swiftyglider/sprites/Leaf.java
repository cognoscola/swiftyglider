package com.bondfire.swiftyglider.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.bondfire.app.bfUtils.BlurrableTextureAtlas;
import com.bondfire.swiftyglider.SwiftyGlider;
import com.bondfire.swiftyglider.ui.Box;

public class Leaf extends Box {

    private final static float LEAF_R = 0.2f;

    private TextureRegion[] leafs = new TextureRegion[3];
    private TextureRegion currentLeaf;

    private final static float MAX_TIME =30f;
    private float timer;
    private float END_Y;

    private float wind;
    private float rotation;

    BlurrableTextureAtlas atlas;

    public Leaf() {

        /** assign a random location */
        this.x = MathUtils.random(SwiftyGlider.WIDTH);
        timer = MathUtils.random(MAX_TIME);
        atlas = (BlurrableTextureAtlas)SwiftyGlider.res.getAtlas("sprites");

        /** get our leafy assets */
        leafs[0] = atlas.findRegion("leaf1");
        leafs[1] = atlas.findRegion("leaf2");
        leafs[2] = atlas.findRegion("leaf3");

        /** assign a random leaf to use */
        currentLeaf =  leafs[MathUtils.random(2)];

        this.width = LEAF_R * SwiftyGlider.WIDTH;
        this.height =LEAF_R * SwiftyGlider.WIDTH;
        this.END_Y = 0 - height;
        rotation = MathUtils.random(360f);
    }

    public void update(float dt){

        /*** update the Y movement*/
        if(this.y > END_Y){
            timer += dt;
            this.y = SwiftyGlider.HEIGHT - timer/ MAX_TIME * SwiftyGlider.HEIGHT;
        }
        if(this.y < END_Y) {
            timer = 0;
            this.y = SwiftyGlider.HEIGHT + height;
            this.x = MathUtils.random(SwiftyGlider.WIDTH);
            currentLeaf =  leafs[MathUtils.random(2)];
            rotation = MathUtils.random(360f);
        }

        /** update the X movement */
        this.x += wind * dt * 30;
        /** if we reach the left side of the screen */
        if(this.x < (0 - currentLeaf.getRegionWidth())){
            this.x = SwiftyGlider.WIDTH;
            rotation = MathUtils.random(360f);
        }

        /** if we reach the right side of the screen */
        else if (this.x > SwiftyGlider.WIDTH + currentLeaf.getRegionWidth()) {
            this.x = 0 - currentLeaf.getRegionWidth();
            rotation = MathUtils.random(360f);
        }
    }

    public void render(SpriteBatch sb){

        if(!atlas.isBlurrable()){
            System.out.println("Blurring from Lighs");
            currentLeaf.getTexture().getTextureData().prepare();
            atlas.PrepareBlur(currentLeaf.getTexture().getTextureData().consumePixmap());
        }
        atlas.bind();

        sb.draw(atlas.tex,
                x - width / 2,
                y - height / 2,
                width / 2,
                height / 2,
                width ,
                height ,
                1,
                1,
                rotation,// scale
                currentLeaf.getRegionX(),
                currentLeaf.getRegionY(),
                currentLeaf.getRegionWidth(),
                currentLeaf.getRegionHeight(),
                false,
                false);

      /*  sb.draw(currentLeaf,
                x - width/2,
                y - height/ 2,
                width/2,
                height/2,
                width,
                height,
                1,
                1,
                rotation);*/
    }

    public void setWind(int wind){
        this.wind = (float)wind;
    }
}
