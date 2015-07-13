package com.bondfire.swiftyglider.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bondfire.swiftyglider.SwiftyGlider;

public class WhiteButtons extends Box {

    private String text;
    private BitmapFont bitmapFont;
    private GlyphLayout layout;
    private TextureRegion background;

    private boolean whiteBackground = true;

    private float backWidth;
    private float backHeight;

    public WhiteButtons(BitmapFont bitmapFont, String text, float x, float y){

        this.text = text;
        this.x = x;
        this.y = y;

        this.bitmapFont = bitmapFont;
        layout = new GlyphLayout();
        layout.setText(bitmapFont,text);

        this.width = layout.width;
        this.height = layout.height;

        background = SwiftyGlider.res.getAtlas("sprites").findRegion("button");
        backWidth = background.getRegionWidth();
        backHeight = background.getRegionHeight();
    }

    public void render(SpriteBatch sb){

        if(whiteBackground){
            sb.draw(background, x- width/2 - 10, y-height/2 -10,width + 20,height + 20);
        }
        bitmapFont.draw(sb,text, x - width/2, y + height/2);
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

    public void setBackgroundVisibility(boolean show){
        whiteBackground = show;
    }


}
