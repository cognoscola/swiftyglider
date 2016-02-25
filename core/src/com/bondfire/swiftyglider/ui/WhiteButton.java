package com.bondfire.swiftyglider.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.bondfire.app.bfUtils.BlurrableTextureAtlas;
import com.bondfire.swiftyglider.SwiftyGlider;

public class WhiteButton extends Box {

    private String text;
    private BitmapFont bitmapFont;
    private GlyphLayout layout;
    private TextureRegion background;

    private final static float padding = 30f;

    private boolean whiteBackground = true;
    private boolean isWrapping = false;
    private float wrapWidth = 100;
    private float backWidth;
    private float backHeight;
    private float textAlpha;

    BlurrableTextureAtlas atlas;

    private OnItemSelectedListener listener;

    public WhiteButton(BitmapFont bitmapFont, String text, float x, float y){

        this.text = text;
        this.x = x;
        this.y = y;

        this.bitmapFont = bitmapFont;
        layout = new GlyphLayout();
        layout.setText(bitmapFont,text);

        this.width = layout.width;
        this.height = layout.height;

        atlas =(BlurrableTextureAtlas)SwiftyGlider.res.getAtlas("sprites");

        background = atlas.findRegion("button");
        backWidth = width;
        backHeight = height;

    }

    public void render(SpriteBatch sb){

        textAlpha = SwiftyGlider.blurAmount;

        if(whiteBackground){

            sb.draw(atlas.tex,
                    x - width / 2,
                    y - height / 2 ,
                    width / 2,
                    height / 2,
                    backWidth ,
                    backHeight ,
                    1,
                    1,
                    0,//rotation
                    background.getRegionX(),
                    background.getRegionY(),
                    background.getRegionWidth(),
                    background.getRegionHeight(),
                    false,
                    false);
//            sb.draw(background, x- width/2 - 10, y-height/2 -10,width + 20,height + 20);
        }
        bitmapFont.setColor(1.0f,1.0f,1.0f,  1 - (textAlpha) );

        if (isWrapping) {
            bitmapFont.draw(sb,text, x - 150 , y - 50, wrapWidth, Align.left, true);
        }else{
            bitmapFont.draw(sb,text, x - width/2, y + height/2);
        }
    }


    @Override
    public boolean contains(float x, float y) {

        boolean ret;
        this.width = backWidth + 100;
        this.height = backHeight + 20;

        ret =  super.contains(x, y);

        this.width = layout.width;
        this.height = layout.height;

        return ret;
    }

    public void setText(String text) {
        layout.setText(bitmapFont,text);
    }

    public void setWrap(boolean isWrap) {
        this.isWrapping = isWrap;
    }

    public void setWidth(float width) {
        this.wrapWidth =width;
    }

    public void setBackgroundVisibility(boolean show){
        whiteBackground = show;
    }

    public void setListener(OnItemSelectedListener listener) {this.listener = listener;}

    public interface OnItemSelectedListener {
        void OnItemSelected();
    }

}
