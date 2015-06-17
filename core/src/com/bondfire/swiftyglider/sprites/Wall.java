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


    private final static float SCALE_LEFTWALLHEIGHT  = 0.117886f;//used to scale Left wall height
    private final static float SCALE_RIGHTWALLHEIGHT = 0.109756f;//used to scale right wall height
    private final static float SCALE_LEFTWALLWIDTH   = 1.8445f;  //used to scale left wall width
    private final static float SCALE_RIGHTWALLWIDTH  = 1.439024f;//used to scale right wall width




    private TextureRegion leftWall;
    private TextureRegion rightWall;

    private float MAX_TIME = 5f;
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
    private boolean isDoneLatch;
    private float canvasWidth;


    public Wall( float canvasWidth, float gapLength) {
        this.gapLength = gapLength;
        this.canvasWidth = canvasWidth;

        this.y = SwiftyGlider.HEIGHT;
        leftWall = SwiftyGlider.res.getAtlas("sprites").findRegion("wall_left");
        rightWall = SwiftyGlider.res.getAtlas("sprites").findRegion("wall_right");

        rightWallWidth = SwiftyGlider.WIDTH;
//        rightWallHeight = rightWall.getRegionHeight();
        rightWallHeight = SwiftyGlider.HEIGHT*SCALE_LEFTWALLHEIGHT;

        leftWallHeight = SwiftyGlider.HEIGHT*SCALE_RIGHTWALLHEIGHT;
//        leftWallHeight = leftWall.getRegionHeight();
        leftWallWidth = SwiftyGlider.WIDTH;

        this.height = rightWallHeight;
        this.END_Y = - rightWallHeight;

    }

    public void update(float dt){

        if(this.y > END_Y){
            timer += dt;
            this.y = SwiftyGlider.HEIGHT - timer/ MAX_TIME * SwiftyGlider.HEIGHT;
            if(this.y < END_Y) this.y = END_Y;
//            System.out.print("\ndt:" + timer + " Distance travelled: " +timer/ MAX_TIME * SwiftyGlider.HEIGHT );
        }
    }

    public void render(SpriteBatch sb){

        sb.draw(leftWall,  leftWallPosition_X - leftWallWidth/2, y - leftWallHeight / 2, leftWallWidth, leftWallHeight);
        sb.draw(rightWall, rightWallPosition_X - rightWallWidth/2, y - rightWallHeight / 2, rightWallWidth, rightWallHeight);
    }

    public void RecycleWall(float gameWidth, float gapLength){

        /** reset the height and timer */
        this.y = SwiftyGlider.HEIGHT;
        timer = 0;

        /** decide how rotation should happen randomly */

        /** determine X position of the gap and the walls */
        this.gapLength = gapLength;
        this.gapPosition =MathUtils.random(gapLength/2,gameWidth - gapLength/2);
        this.leftWallPosition_X = gapPosition - gapLength/2 - leftWallWidth/2;
        this.rightWallPosition_X = gapPosition + gapLength/2 + rightWallWidth/2;
    }

    public void FirstWall(int CanvasWidth, float gapLength){

        this.gapLength = gapLength;
        this.gapPosition =MathUtils.random(gapLength/2,CanvasWidth - gapLength/2);
        this.leftWallPosition_X = gapPosition - gapPosition/2 - leftWallWidth/2;
        this.rightWallPosition_X = gapPosition + gapPosition/2 + rightWallWidth/2;
    }

    public boolean isDone(){
        return this.y == END_Y;
    }


    @Override
    public boolean colliding(float x, float y, float width, float height) {

//        System.out.println("Y:" + y + " X:" + x + " Wall.Y:" + this.y + " wall Height:" + height );

        // check collision with left wall first
        this.width = leftWallWidth - 10;
        this.height = leftWallHeight;
        this.x = leftWallPosition_X;
        if(super.colliding(x, y, width, height))return true;

        // check collision with the right wall
        this.width = rightWallWidth;
        this.height = rightWallHeight;
        this.x = rightWallPosition_X;
        if(super.colliding(x, y, width, height))return true;

        return false;
    }
}