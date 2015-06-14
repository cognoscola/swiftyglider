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
    private final int MAX_FINGERS = 1;
    private int i;
    private float boardOffset; //used to position the glider in the middle of the screen

    /** our sprites **/
    private Glider glider;
    private Indicator line;
    private Array<Wall> wallQueueWaiting;
    private Array<Wall> wallQueueActive;



    /** Game Logic */
    private static int level              = 0 ;
    private static int getLength          = 0 ;

    /** timing logic */
    static boolean isRateChanging = false;
    static boolean isSpedChanging = false;
    static boolean isGoingUp      = true;
    static int levelSpeed         = 1;

    private static boolean colliding = false;
    private static boolean firstWall = true;

    /** indexes*/
    int wallIndexer;

    /** game state */
    enum GameState{
        Running,
        Died
    }


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
    public void handeInput() {

        /** we're going to try multitouch */
        /** use the vector3 to grab the mouse position */
        /** we only want max MAX_FINGERS inputs available */
        for(i = 0; i < MAX_FINGERS;i++){

            /** check if the pointer are pressed */
            if(Gdx.input.justTouched()){

                mouse.x = Gdx.input.getX(i);
                mouse.y = Gdx.input.getY(i);

                /** change the coordinates from screen coordinates to the world coordinate */
                cam.unproject(mouse);

                /** find out if our object was clicked */
                if(mouse.x >= 0 && mouse.x < SwiftyGlider.WIDTH &&
                        mouse.y >= 0 && mouse.y < SwiftyGlider.HEIGHT){
                    if(glider.contains(mouse.x, mouse.y)){
                        glider.setSelected(true);
                        line.reset();
                        for(int i = 0; i < wallQueueWaiting.size; i++){

                            //TODO when calling this, make sure you subtract the sprites's width
                            wallQueueWaiting.get(i).RecycleWall(SwiftyGlider.WIDTH, 50f);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void update(float dt) {

        /** Update this state*/
        updateWall(dt);

        /** update everything inside this state */
        handeInput();
        glider.update(dt);
        line.update(dt);

        /** for each wall, update them */
        for(int i = 0; i < wallQueueWaiting.size; i++){
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
        for(int i = 0; i < wallQueueWaiting.size; i++){
            wallQueueWaiting.get(i).render(sb);
        }
        sb.end();
    }

    private void updateWall(float dt){

        /** for each each in the active queue, check if they are done.*/

        /** if yes, put them in the waitQueue*/

        /** Check if it is time to put a new wall on the screen */

        /** if yes, fetch a wall from the waitQueue and put it into the activeQueue if waitQueue
         * is empty just make a new wall.*/

    }

}
