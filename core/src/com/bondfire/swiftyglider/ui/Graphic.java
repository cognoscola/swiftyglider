package com.bondfire.swiftyglider.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Graphic extends Box{

    private TextureRegion image;

    public Graphic(TextureRegion image, float x, float y, float width, float height){

        this.image = image;
        this.x =x;
        this.y =y;
        this.width = width;
        this.height = height;
    }

    public Graphic(TextureRegion image, float x, float y){

        this.image = image;
        this.x =x;
        this.y =y;
        this.width = image.getRegionWidth();
        this.height = image.getRegionHeight();
    }


    public void render(SpriteBatch sb){
        sb.draw(image, x- width /2, y-height/2,width,height);
    }
}
