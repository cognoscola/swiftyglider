package com.bondfire.swiftyglider.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.bondfire.swiftyglider.SwiftyGlider;
import com.bondfire.swiftyglider.sprites.Glider;
import com.bondfire.swiftyglider.sprites.Indicator;
import com.bondfire.swiftyglider.sprites.Wall;

/** our play state. */
public class PlayState extends State {

    /** set the max number of fingers */
    private final int MAX_FINGERS = 0;

    /** save points */
    public final static int LV_BEGINNING   = 0;
    public final static int LV_FIRSTWIND   = 73;
    public final static int LV_GOINGFAST   = 149;
    public final static int LV_WINDFAST    = 253;
    public final static int LV_SUPERSLOW   = 275;
    public final static int LV_WINDSLOW    = 347;
    public final static int LV_LONGSTRETCH = 399;
    public final static int LV_EYEOFNEEDLE = 479;

    public final static float SCALE_GAPLENGTH_150   = 0.228659f; //scales gap length 130
    public final static float SCALE_GAPLENGTH_220   = 0.335366f; //scales gap length 220
    public final static float SCALE_GAPLENGTH_230   = 0.350619f; //scales gap length 230
    public final static float SCALE_GAPLENGTH_C_25  = 0.038110f; //scales gap length 25 + glider
    public final static float SCALE_GAPLENGTH_C_30  = 0.045731f;//scales gap length 30 + glider
    public final static float SCALE_GAPLENGTH_C_50  = 0.076220f;//scales gap length 50 + glider

    private int i;

    /** our sprites **/
    private Glider glider;
    private Indicator line;
    private Array<Wall> wallQueueWaiting;
    private Array<Wall> wallQueueActive;

    /** Game Logic */
    private int level;
    private static float gapLength = 100f ;
    private static boolean colliding = false;

    /** timing logic */
    static float wallTimer = 3f;      //timer and also the initial start time
    static float wallFrequency = 3f;  //frequency of wall appearance
    static float indicatorTimer = 3f; //timer for indicator
    static float indicatorFrequency = 4f;

    static boolean collidingLatch = false;

    private int lastSavePoint;

    public PlayState(GSM gsm, int level){
        super(gsm);

        reset();
        setLevel(level);

        /** load our sprites */
        glider = new Glider(SwiftyGlider.WIDTH/2,SwiftyGlider.HEIGHT/4);
        line   = new Indicator(SwiftyGlider.WIDTH/2, 0, SwiftyGlider.WIDTH, 50);
        wallQueueWaiting = new Array<Wall>();
        wallQueueActive =  new Array<Wall>();
    }

    public void reset(){
        gapLength = 100f;
        colliding = false;
        wallTimer = 3f;
        wallFrequency = 3f;
        collidingLatch = false;
    }

    public void setLevel(int level){
        lastSavePoint = level;

        switch(level){
            case LV_BEGINNING:
//                scoreText = String.valueOf(level);
//                oldwind = 0;
//                wind = 0;
                gapLength = SwiftyGlider.WIDTH*SCALE_GAPLENGTH_150;
                break;

            case LV_FIRSTWIND:
//                scoreText = String.valueOf(level);
//                oldwind = 0;
//                wind = 0;
                gapLength =  SwiftyGlider.WIDTH*SCALE_GAPLENGTH_220;
                break;
            case LV_GOINGFAST:

//                oldwind = 0;
//                wind = 0;
                gapLength =  SwiftyGlider.WIDTH*SCALE_GAPLENGTH_220;
                break;

            case LV_WINDFAST:

//                oldwind = 0;
//                wind = 0;
                gapLength =   SwiftyGlider.WIDTH*SCALE_GAPLENGTH_230;
                break;
            case LV_SUPERSLOW:

//                oldwind = 0;
//                wind = 0;
                gapLength = SwiftyGlider.WIDTH*Glider.SCALE_GLIDER + SwiftyGlider.WIDTH*SCALE_GAPLENGTH_C_25;
                break;
            case LV_WINDSLOW:
//                oldwind = 0;
//                wind = 0;
                gapLength = SwiftyGlider.WIDTH*Glider.SCALE_GLIDER +SwiftyGlider.WIDTH*SCALE_GAPLENGTH_C_30;
                break;
            case LV_LONGSTRETCH:
//                levelSpeed = 1;
//                oldwind = 0;
//                wind = 0;
                gapLength = SwiftyGlider.WIDTH*Glider.SCALE_GLIDER +SwiftyGlider.WIDTH*SCALE_GAPLENGTH_C_30;
//                wallInterval = WALL_RATE_DEFAULT - 1100;
                break;

            case LV_EYEOFNEEDLE:
//                levelSpeed = 11;
//                oldwind = 0;
//                wind = 0;
                gapLength = SwiftyGlider.WIDTH*Glider.SCALE_GLIDER +SwiftyGlider.WIDTH*SCALE_GAPLENGTH_C_50;
//                wallInterval = WALL_RATE_DEFAULT - 1300;
                break;
        }

    }


    @Override
    public void handleInput() {

        /** we're going to try multitouch */
        /** use the vector3 to grab the mouse position */
        /** we only want max MAX_FINGERS inputs available */
        for(i = 0; i < MAX_FINGERS;i++){

            /** check if the pointer are pressed */
            if(Gdx.input.isTouched()){

                mouse.x = Gdx.input.getX(i);
                mouse.y = Gdx.input.getY(i);

                /** change the coordinates from screen coordinates to the world coordinate */
                cam.unproject(mouse);

                /** find out if our object was clicked */
                glider.setX(mouse.x);
                glider.setY(mouse.y);

               /* if(mouse.x >= 0 && mouse.x < SwiftyGlider.WIDTH &&
                        mouse.y >= 0 && mouse.y < SwiftyGlider.HEIGHT){
                    if(glider.contains(mouse.x, mouse.y)){
                        glider.setColliding(true);
                        line.reset();
                        for(int i = 0; i < wallQueueWaiting.size; i++){
                            //TODO when calling this, make sure you subtract the sprites's width
                            wallQueueWaiting.get(i).RecycleWall(SwiftyGlider.WIDTH, 50f);
                        }
                    }
                }*/
            }
        }
    }

    @Override
    public void update(float dt) {

        wallTimer +=dt;

        /** Update this state*/
        checkWallRate();

        /** checkCollision */
        checkCollision();

        /** update everything inside this state */
        handleInput();
        glider.update(dt);
        line.update(dt);

        /** for each wall, update them */
        for(int i = 0; i < wallQueueActive.size; i++){
            wallQueueActive.get(i).update(dt);
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        /** Before we draw anything, we always, always need to set the camera */
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        line.render(sb);
        for(int i = 0; i < wallQueueActive.size; i++){
            wallQueueActive.get(i).render(sb);
        }
        glider.render(sb);
        sb.end();
    }

    private void checkWallRate(){

        /** for each in the active queue, check if they are done.*/
       for( i = 0; i < wallQueueActive.size; i++ ){
            Wall wall = wallQueueActive.get(i);

           /** if yes, put them in the waitQueue*/
           if(wall.isDone()){
              wallQueueWaiting.add(wall);
               wallQueueActive.removeIndex(i);
           }
       }

        /** Check if it is time to put a new wall on the screen */
        if(wallTimer >= wallFrequency) {

            /** if yes, fetch a wall from the waitQueue and put it into the activeQueue if waitQueue
             * is empty just make a new wall.*/
            Wall wall;
            if(wallQueueWaiting.size != 0)
                wall = wallQueueWaiting.pop();
            else{
                wall = new Wall(SwiftyGlider.WIDTH, gapLength);
//                System.out.println("New wall");
            }

            wall.RecycleWall(SwiftyGlider.WIDTH, gapLength);
            wallQueueActive.add(wall);
            wallTimer = 0;
        }
    }

    public void checkCollision(){

        /** check if we are colliding with any walls */
        for (i = 0; i < wallQueueActive.size; i++) {
            Wall wall = wallQueueActive.get(i);

            if (wall != null) {
                /** Are we colliding with the left side? */
                colliding = wall.colliding(glider.getX(), glider.getY(), glider.getWidth(), glider.getHeight());
                glider.setColliding(colliding);
                /** We don't want the colliding value to change back to false by
                 * checking another wall it is not colliding with, so terminate the loop*/
                if(colliding) {
                    i = wallQueueActive.size;

                    if(colliding && !collidingLatch){
                        gsm.set(new ScoreState(gsm,lastSavePoint));
                        collidingLatch = true;
                    }
                }
            }
        }
    }
}
