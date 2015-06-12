package com.bondfire.swiftyglider.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Stack;

/**
 * Game State Manager
 * the purpose of the game state manager is to keep track of all of the game states that
 * we create on the game. */

public class GSM {

    /** Stacks are a good tool to handle game states. You push state and pop states as
     * you progress through the game. The first thing on the stack is usually the main menu.
     * Clicking start, will push the game play state into the stack. Going back will pop
     * the stack */
    private Stack<State> states; //Our states stacks

    public GSM(){
        states = new Stack<State>();
    }

    public void push(State s){
        states.push(s);
    }

    public void pop(){
        states.pop();
    }

    /** replace top of the stack */
    public void set(State s){
        states.pop();
        states.push(s);
    }

    /** our GSM is only going to be updating the TOP of the stack */
    public void update(float dt){
        /** get the top of the stack */
        states.peek().update(dt);
    }
    /** our GSM is only going to be rendering the TOP of the stack */
    public void render(SpriteBatch sp){
        states.peek().render(sp);
    }

}
