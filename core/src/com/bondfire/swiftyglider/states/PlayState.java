package com.bondfire.swiftyglider.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bondfire.swiftyglider.SwiftyGlider;
import com.bondfire.swiftyglider.sprites.Glider;

/** our play state. */
public class PlayState extends State {

    private Glider glider;

    private float boardOffset; //used to position the glider in the middle of the screen

    public PlayState(GSM gsm){
        super(gsm);

        // W: 480 H: 800
        int tileSize = 50;
        glider = new Glider(SwiftyGlider.WIDTH/2,SwiftyGlider.HEIGHT/2,tileSize,tileSize);
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch sb) {

        /** Before we draw anything, we always, always need to set the camera */
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        glider.render(sb);
        sb.end();
    }

    @Override
    public void handeInput() {

    }
}
