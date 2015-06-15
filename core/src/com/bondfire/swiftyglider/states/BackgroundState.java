package com.bondfire.swiftyglider.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bondfire.swiftyglider.sprites.Leaf;


public class BackgroundState extends State  {

    private final static int numofLeafs = 8;

    private Leaf[] leafs = new Leaf[numofLeafs];

    public BackgroundState(GSM gsm){
        super(gsm);

        for(int i = 0; i < numofLeafs; i++){
            leafs[i] = new Leaf();
        }
    }

    @Override
    public void update(float dt) {
        for(int i = 0; i < numofLeafs; i++){
            leafs[i].update(dt);
        }
    }

    @Override
    public void render(SpriteBatch sb) {

        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        for(int i = 0; i < numofLeafs; i++){
            leafs[i].render(sb);
        }
        sb.end();
    }

    @Override
    public void handleInput() {

    }

}
