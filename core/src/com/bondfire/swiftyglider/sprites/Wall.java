package com.bondfire.swiftyglider.sprites;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.bondfire.app.bfUtils.BlurrableTextureAtlas;
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

    private static float MAX_TIME = 5f;  //time it takes for the wall to descent
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

    /** for giving the wall a more random appearance **/
    private float leftWallRotate;
    private float rightWallRotate;
    private boolean swap;

    BlurrableTextureAtlas atlas;


    public Wall( float canvasWidth, float gapLength) {
        this.gapLength = gapLength;
        this.canvasWidth = canvasWidth;


        this.y = SwiftyGlider.HEIGHT;

        atlas =(BlurrableTextureAtlas) SwiftyGlider.res.getAtlas("sprites");
        leftWall =atlas.findRegion("wall_left");
        rightWall = atlas.findRegion("wall_right");

        rightWallWidth = SwiftyGlider.WIDTH;
//        rightWallHeight = rightWall.getRegionHeight();
        rightWallHeight = SwiftyGlider.HEIGHT*SCALE_LEFTWALLHEIGHT;

        leftWallHeight = SwiftyGlider.HEIGHT*SCALE_RIGHTWALLHEIGHT;
//        leftWallHeight = leftWall.getRegionHeight();
        leftWallWidth = SwiftyGlider.WIDTH;

        this.height = rightWallHeight;
        this.END_Y = - rightWallHeight;
    }

    public Wall( float canvasWidth, float gapLength, float startingHeight) {
        this.gapLength = gapLength;
        this.canvasWidth = canvasWidth;

        this.timer = MAX_TIME * startingHeight;

        atlas =(BlurrableTextureAtlas) SwiftyGlider.res.getAtlas("sprites");
        leftWall =atlas.findRegion("wall_left");
        rightWall = atlas.findRegion("wall_right");

        rightWallWidth = SwiftyGlider.WIDTH;
//        rightWallHeight = rightWall.getRegionHeight();
        rightWallHeight = SwiftyGlider.HEIGHT*SCALE_LEFTWALLHEIGHT;

        leftWallHeight = SwiftyGlider.HEIGHT*SCALE_RIGHTWALLHEIGHT;
//        leftWallHeight = leftWall.getRegionHeight();
        leftWallWidth = SwiftyGlider.WIDTH;

        this.height = rightWallHeight;
        this.END_Y = - rightWallHeight;

        FirstWall(canvasWidth, gapLength);
    }

    public void update(float dt){
        if(this.y > END_Y){
            timer += dt;
            this.y = SwiftyGlider.HEIGHT - timer/ MAX_TIME * SwiftyGlider.HEIGHT;
            if(this.y < END_Y) this.y = END_Y;
//          System.out.print("\ndt:" + timer + " Distance travelled: " +timer/ MAX_TIME * SwiftyGlider.HEIGHT );
        }
    }

    public void render(SpriteBatch sb){

        if(!atlas.isBlurrable()){
            System.out.println("Blurring from Lighs");
            leftWall.getTexture().getTextureData().prepare();
            atlas.PrepareBlur(leftWall.getTexture().getTextureData().consumePixmap());
        }
        atlas.bind();

        sb.draw(atlas.tex,
                leftWallPosition_X - leftWallWidth/2,
                y - leftWallHeight / 2,
                leftWallWidth/2,
                leftWallHeight/2,
                leftWallWidth ,
                leftWallHeight ,
                1,
                1,
                leftWallRotate,// scale
                swap ? rightWall.getRegionX():leftWall.getRegionX(),
                swap ? rightWall.getRegionY():leftWall.getRegionY(),
                swap ? rightWall.getRegionWidth():leftWall.getRegionWidth(),
                swap ? rightWall.getRegionHeight():leftWall.getRegionHeight(),
                false,
                false);

        sb.draw(atlas.tex,
                rightWallPosition_X - rightWallWidth/2,
                y - rightWallHeight / 2,
                rightWallWidth/2,
                rightWallHeight/2,
                rightWallWidth ,
                rightWallHeight ,
                1,
                1,
                rightWallRotate,// scale
                swap ? leftWall.getRegionX():rightWall.getRegionX(),
                swap ? leftWall.getRegionY():rightWall.getRegionY(),
                swap ? leftWall.getRegionWidth():rightWall.getRegionWidth(),
                swap ? leftWall.getRegionHeight():rightWall.getRegionHeight(),
                false,
                false);

       /* sb.draw(swap ? rightWall:leftWall,
                leftWallPosition_X - leftWallWidth/2,
                y - leftWallHeight / 2,
                leftWallWidth/2,
                leftWallHeight/2,
                leftWallWidth,
                leftWallHeight,
                1,
                1,
                leftWallRotate);

        sb.draw(swap ? leftWall:rightWall,
                rightWallPosition_X - rightWallWidth/2,
                y - rightWallHeight / 2,
                rightWallWidth/2,
                rightWallHeight/2,
                rightWallWidth,
                rightWallHeight,
                1,
                1,
                rightWallRotate);*/

//        sb.draw(leftWall,  leftWallPosition_X - leftWallWidth/2, y - leftWallHeight / 2, leftWallWidth, leftWallHeight);
//        sb.draw(rightWall, rightWallPosition_X - rightWallWidth/2, y - rightWallHeight / 2, rightWallWidth, rightWallHeight);
    }

    /**
     * Recycles the wall, giving the gap position a random length /
     * @param gameWidth
     * @param gapLength
     */
    public void RecycleWall(float gameWidth, float gapLength){

        /** reset the height and timer */
        this.y = SwiftyGlider.HEIGHT;
        timer = 0;

        /** decide how rotation should happen randomly */
        randomnizeWall();

        /** determine X position of the gap and the walls */
        this.gapLength = gapLength;
        this.gapPosition =MathUtils.random(gapLength/2,gameWidth - gapLength/2);
        this.leftWallPosition_X = gapPosition - gapLength/2 - leftWallWidth/2;
        this.rightWallPosition_X = gapPosition + gapLength/2 + rightWallWidth/2;
    }



    /** Recycle the wall with a given position **/
    public void RecycleWall(float gameWidth, float gapLength, float gapPosition) {
        this.y = SwiftyGlider.HEIGHT;
        timer = 0;

        /** decide how rotation should happen randomly */
        randomnizeWall();

        /** determine X position of the gap and the walls */
        this.gapLength = gapLength;
        this.gapPosition = gapPosition;
        this.leftWallPosition_X = gapPosition - gapLength/2 - leftWallWidth/2;
        this.rightWallPosition_X = gapPosition + gapLength/2 + rightWallWidth/2;
    }



    public void FirstWall(float CanvasWidth, float gapLength){

        randomnizeWall();
        this.gapLength = gapLength;
        this.gapPosition = canvasWidth/2;
        this.leftWallPosition_X = gapPosition - gapLength/2 - leftWallWidth/2;
        this.rightWallPosition_X = gapPosition + gapLength/2 + rightWallWidth/2;
    }

    public void randomnizeWall(){

        switch(MathUtils.random(7)){
            case 0: leftWallRotate = 180f; rightWallRotate = 0f;   swap = false; break;
            case 1: leftWallRotate = 0;    rightWallRotate = 180f; swap = false; break;
            case 2: leftWallRotate = 180;  rightWallRotate = 180f; swap = false; break;
            case 3: leftWallRotate = 0f;   rightWallRotate = 0f;   swap = false; break;
            case 4: leftWallRotate = 0f;   rightWallRotate = 0f;   swap = true; break;
            case 5: leftWallRotate = 180;  rightWallRotate = 0;    swap = true; break;
            case 6: leftWallRotate = 0f;   rightWallRotate = 180f; swap = true; break;
            case 7: leftWallRotate = 180;  rightWallRotate = 180f; swap = true; break;
        }
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

    public static void setDescentSpeed(float t){
        MAX_TIME = t;
    }
}