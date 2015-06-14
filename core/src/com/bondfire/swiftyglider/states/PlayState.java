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

    private int i;

    /** our sprites **/
    private Glider glider;
    private Indicator line;
    private Array<Wall> wallQueueWaiting;
    private Array<Wall> wallQueueActive;



    /** Game Logic */
    private static float gapLength = 100f ;
    private static boolean colliding = false;

    /** timing logic */
    static float wallTimer = 1f;  //timer and also the initial start time
    static float walllInterval = 3f;



    public PlayState(GSM gsm){
        super(gsm);

        // W: 480 H: 800
        int tileSize = 50;

        /** load our sprites */
        glider = new Glider(SwiftyGlider.WIDTH/2,SwiftyGlider.HEIGHT/2,tileSize,tileSize);
        line   = new Indicator(SwiftyGlider.WIDTH/2, 0, SwiftyGlider.WIDTH, 50);
        wallQueueWaiting = new Array<Wall>();
        wallQueueActive =  new Array<Wall>();

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
        glider.render(sb);
        line.render(sb);
        for(int i = 0; i < wallQueueActive.size; i++){
            wallQueueActive.get(i).render(sb);
        }
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
        if(wallTimer >= walllInterval) {

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
                if(colliding) i = wallQueueActive.size;
            }
        }
    }
}
