package com.bondfire.swiftyglider.sprites;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Logger;
import com.bondfire.swiftyglider.SwiftyGlider;
import com.bondfire.swiftyglider.ui.Box;

/**
 * This class defines the Walls (logs) that start from the top and come down. If our glider
 * collides with these walls the game ends. There are always two sides of a wall with a gap
 * in the middle. Whenever the wall reaches the bottom, it will "Reset" and the gap position
 * , width will change depending on the game difficulty
 */
public class Wall extends Box {

    Logger log = new Logger("Indicator");

    private TextureRegion leftWall;
    private TextureRegion rightWall;

    private float MAX_TIME = 3f;
    private float timer;
    private float END_Y;

    private float gapPosition;
    private float gapLength;
    private float leftWallPosition_X;
    private float rightWallPosition_X;
    private float rightWallWidth;
    private float leftWallWidth;
    private float rightWallHeight;
    private float leftWallHeight;

    private float canvasWidth;

    public Wall( float canvasWidth, float gapLength) {
        this.gapLength = gapLength;
        this.canvasWidth = canvasWidth;

        this.y = SwiftyGlider.HEIGHT;
        this.END_Y = y - height;

        leftWall = SwiftyGlider.res.getAtlas("sprites").findRegion("wall_left");
        rightWall = SwiftyGlider.res.getAtlas("sprites").findRegion("wall_right");

        rightWallWidth = rightWall.getRegionWidth();
        rightWallHeight = rightWall.getRegionHeight();

        leftWallHeight = leftWall.getRegionHeight();
        leftWallWidth = leftWall.getRegionWidth();

    }

    public void update(float dt){
        if(this.y > END_Y){
            timer += dt;
            this.y = SwiftyGlider.HEIGHT - timer/ MAX_TIME * SwiftyGlider.HEIGHT;
            if(this.y < END_Y) this.y = END_Y;
            System.out.print("\ndt:" + timer + " Distance travelled: " +timer/ MAX_TIME * SwiftyGlider.HEIGHT );
        }
    }

    public void render(SpriteBatch sb){
        sb.draw(leftWall,  0, y - height / 2, leftWallWidth, leftWallHeight);
        sb.draw(rightWall, 0, y - height / 2, rightWallWidth, rightWallHeight);
    }

    public void RecycleWall(float gameWidth, float gapLength){

        /** reset the height and timer */
        this.y = SwiftyGlider.HEIGHT;
        timer = 0;

        /** decide how rotation should happen randomly */

        /** determine X position of the gap and the walls */
        this.gapLength = gapLength;
        this.gapPosition = MathUtils.random(gameWidth);
        this.leftWallPosition_X = gapPosition - leftWall.getRegionWidth();
        this.rightWallPosition_X = gapPosition + gapLength;
    }

}