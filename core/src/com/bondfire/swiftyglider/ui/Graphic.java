package com.bondfire.swiftyglider.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Graphic extends Box{

    private TextureRegion image;
    private BlurrableTextureAtlas atlas;

    public Graphic(BlurrableTextureAtlas atlas,TextureRegion image, float x, float y, float width, float height){

        this.atlas = atlas;
        this.image = image;
        this.x =x;
        this.y =y;
        this.width = width;
        this.height = height;
    }

    public Graphic(BlurrableTextureAtlas atlas, TextureRegion image, float x, float y){

        this.image = image;
        this.x =x;
        this.y =y;
        this.width = image.getRegionWidth();
        this.height = image.getRegionHeight();

        /** apply blurring */
        this.atlas = atlas;

    }

    public void render(SpriteBatch sb){
        if(!atlas.isBlurrable()){
            System.out.println("Blurring from Lighs");
            image.getTexture().getTextureData().prepare();
            atlas.PrepareBlur(image.getTexture().getTextureData().consumePixmap());
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
                0,// scale
                image.getRegionX(),
                image.getRegionY(),
                image.getRegionWidth(),
                image.getRegionHeight(),
                false,
                false);

//        sb.draw(image, x- width /2, y-height/2,width,height);
    }
}
