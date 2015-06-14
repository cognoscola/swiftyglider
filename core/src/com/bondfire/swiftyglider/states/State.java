package com.bondfire.swiftyglider.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.bondfire.swiftyglider.SwiftyGlider;

/** A little note On states. A game is basically a loop. It will update and then a render. Over
 * and over and over again. Games are composed of a lot of things. ORganize the different screens
 * by creating game states. Each game state is a seperate screen, with its own update and its
 * own render methods. For instance a basic game can have two states, the menu screen and the
 * game play screen.*/

public abstract  class State {

    /** Every state is going to have a reference to the GSM. That way the state
     * can tell the game state manager to switch to another game state by using
     * push, pop, set etc..  */
    protected GSM gsm;

    /** every play state needs to have a camera */
    protected OrthographicCamera cam;

    /** every state should have its own mouse coordinate */
    protected Vector3 mouse;

    protected State(GSM gsm){
        this.gsm = gsm;

        /**configure the camera, to be same size as game screen */
        cam = new OrthographicCamera();
        cam.setToOrtho(false, SwiftyGlider.WIDTH, SwiftyGlider.HEIGHT);
        mouse = new Vector3();

    }

    /** every game state has to have their own render/update methods */
    public abstract void update(float dt);
    public abstract void render(SpriteBatch sb);
    /** a method for handling input */
    public abstract void handleInput();

}
