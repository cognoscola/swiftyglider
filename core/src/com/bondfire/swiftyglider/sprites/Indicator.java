package com.bondfire.swiftyglider.sprites;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Logger;
import com.bondfire.swiftyglider.SwiftyGlider;
import com.bondfire.swiftyglider.ui.Box;


public class Indicator extends Box {

    Logger log = new Logger("Indicator");

    private TextureRegion line;

    private float timer; //Time it takes for our glider to fly into the screen
    private float MAX_TIME = 1f;
    private float END_Y;

    public Indicator(float x, float y, float width, float height) {

        this.x = x;
        this.END_Y = y - height;
        this.y = SwiftyGlider.HEIGHT;
        this.width = width;
        this.height = height;

        line = SwiftyGlider.res.getAtlas("sprites").findRegion("indicator_line");
    }

    public void update(float dt){
        if(this.y > END_Y){
            timer += dt;
            this.y = SwiftyGlider.HEIGHT - timer/ MAX_TIME * SwiftyGlider.HEIGHT;
            if(this.y < END_Y) this.y = END_Y;
        }
    }

    public void render(SpriteBatch sb){
            sb.draw(line, x - width / 2, y - height / 2, width, height);
    }

    public void reset(){
        this.y = SwiftyGlider.HEIGHT;
        timer = 0;
    }
}
