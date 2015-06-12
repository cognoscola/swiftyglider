package com.bondfire.swiftyglider.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by alvaregd on 12/06/15.
 */
public class TestState extends State {

    public TestState(GSM gsm){
        super(gsm);
    }

    @Override
    public void update(float dt) {
        System.out.println("Test state updating");
    }

    @Override
    public void render(SpriteBatch sb) {
        System.out.println("Test state rendering");

    }

    @Override
    public void handeInput() {

    }
}
