package com.bondfire.swiftyglider.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.bondfire.app.bfUtils.GradientBoard;
import com.bondfire.swiftyglider.SwiftyGlider;
import com.bondfire.swiftyglider.sprites.Leaf;

public class BackgroundState extends State  {

    private final static int numofLeafs = 8;

    private Leaf[] leafs = new Leaf[numofLeafs];
    private GradientBoard backWall;
    private ShapeRenderer sr;

    public BackgroundState(GSM gsm, int time){
        super(gsm);
        backWall = GradientBoard.newIntance(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),time);

         System.out.println("Width:" + SwiftyGlider.WIDTH + " HEIGHT:" + SwiftyGlider.HEIGHT);

        for(int i = 0; i < numofLeafs; i++){
            leafs[i] = new Leaf();
        }
        sr = new ShapeRenderer();
    }

    @Override
    public void update(float dt) {
        for(int i = 0; i < numofLeafs; i++){
            leafs[i].update(dt);
        }
    }

    @Override
    public void render(SpriteBatch sb) {

        backWall.render(sr);
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        SwiftyGlider.shader.setUniformf(SwiftyGlider.biasLocation, SwiftyGlider.MAX_BLUR * SwiftyGlider.blurAmount);
        for(int i = 0; i < numofLeafs; i++){
            leafs[i].render(sb);
        }
        sb.end();
    }

    @Override
    public void handleInput() {

    }

    public void setWind(int wind){

        for(int i = 0; i < numofLeafs; i++){
            leafs[i].setWind(wind);
        }
    }

}
