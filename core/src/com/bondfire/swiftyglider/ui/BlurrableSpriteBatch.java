package com.bondfire.swiftyglider.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

/**
 * Created by alvaregd on 11/05/16.
 */
public class BlurrableSpriteBatch extends SpriteBatch {

    private float blurAmount;
    private float R;
    private boolean usesZoomableAssets;
    private TextureAtlas.AtlasRegion blurrRegion;

    float C_H = 0f;  //target height
    float MAX_H = 0f; //max height
    float MIN_H = 0f; //mininum height

    float C_W = 0f; //target width
    float MAX_W = 0f; //max width
    float MIN_W = 0f; //min width

    float Y; //Y coordinates
    float X; //X coordinates

    public BlurrableSpriteBatch() {
        super();
        R = 2.4f;
        usesZoomableAssets = true;

    }

    public void setBlurAmount(float blurAmount){
        this.blurAmount = blurAmount;
    }

    public void setZoomRatio(float r) {
        R = r;
    }

    public void setUsesZoomableAssets(boolean zooms) {
        this.usesZoomableAssets = zooms;
    }


    @Override
    public void draw(
            Texture texture,
            float x, float y,
            float originX,
            float originY,
            float width,
            float height,
            float scaleX,
            float scaleY,
            float rotation,
            int srcX,
            int srcY,
            int srcWidth,
            int srcHeight,
            boolean flipX,
            boolean flipY)
    {
        if (usesZoomableAssets) {

            MAX_W = srcWidth;
            MAX_H = srcHeight;
            MIN_H = MAX_H / R;
            MIN_W = MAX_W / R;

            C_H = MIN_H + (MAX_H - MIN_H) *(( blurAmount/4 )/(1));
            C_W = MIN_W + (MAX_W - MIN_W) *(( blurAmount/4 )/(1));

            Y = srcY + (MAX_H/2 - C_H/2);
            X = srcX + (MAX_W/2 - C_W/2);

            //perform maths here
            super.draw(
                    texture,
                    x,
                    y,
                    originX,
                    originY,
                    width,
                    height,
                    scaleX,
                    scaleY,
                    rotation,
                    MathUtils.round(X),
                    MathUtils.round(Y),
                    MathUtils.round(C_W),
                    MathUtils.round(C_H),
                    flipX,
                    flipY);
        }else{
            super.draw(
                    texture,
                    x,
                    y,
                    originX,
                    originY,
                    width,
                    height,
                    scaleX,
                    scaleY,
                    rotation,
                    srcX,
                    srcY,
                    srcWidth,
                    srcHeight,
                    flipX,
                    flipY);
        }
    }

    @Override
    public void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {

        if (blurrRegion == null) {
            blurrRegion = new TextureAtlas.AtlasRegion(
                    region.getTexture(),
                    region.getRegionX(),
                    region.getRegionY(),
                    region.getRegionWidth(),
                    region.getRegionHeight());
        }

        if (usesZoomableAssets) {
            MAX_W = region.getRegionWidth();
            MAX_H = region.getRegionHeight();

            MIN_H = MAX_H / R;
            MIN_W = MAX_W / R;

            C_H = MIN_H + (MAX_H - MIN_H) *(( blurAmount/4 )/(1));
            C_W = MIN_W + (MAX_W - MIN_W) *(( blurAmount/4 )/(1));

            Y =  (MAX_H/2 - C_H/2);
            X =  (MAX_W/2 - C_W/2);

            blurrRegion.setRegion(region,
                    (int) X,(int)Y,(int)C_W,(int)C_H
            );

        }else{
            blurrRegion.setRegion(region);
        }
        super.draw(
                blurrRegion,
                x,
                y,
                originX,
                originY,
                width,
                height,
                scaleX,
                scaleY,
                rotation);
    }
}
