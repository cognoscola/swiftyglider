package com.bondfire.swiftyglider.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bondfire.app.bfUtils.BlurrableTextureAtlas;
import com.bondfire.swiftyglider.SwiftyGlider;
import com.bondfire.swiftyglider.ui.Graphic;

/**
 * Render our main menu
 */
public class MenuState extends State {

    private final static float INSTRUCTION_HEIGHT_R = 0.35f;
    private final static float INSTRUCTION_WIDTH_R = 0.7f;
    private final static float START_WIDTH_R = 0.25f;
    private final static float START_HEIGH_R = 0.1f;

    private Graphic instruction;
    private Graphic start;

    public MenuState(GSM gsm){
        super(gsm);

        BlurrableTextureAtlas atlas = (BlurrableTextureAtlas)SwiftyGlider.res.getAtlas("sprites");

        instruction = new Graphic(
                atlas,
                atlas.findRegion("instructions"),
                SwiftyGlider.WIDTH/2,
                SwiftyGlider.HEIGHT*3/4,
                INSTRUCTION_WIDTH_R * SwiftyGlider.WIDTH,
                INSTRUCTION_HEIGHT_R * SwiftyGlider.HEIGHT);
        start = new Graphic(
                atlas,
                atlas.findRegion("start"),
                SwiftyGlider.WIDTH/2,
                SwiftyGlider.HEIGHT/2,
                START_WIDTH_R * SwiftyGlider.WIDTH,
                START_HEIGH_R * SwiftyGlider.HEIGHT);
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        SwiftyGlider.shader.setUniformf("bias", SwiftyGlider.MAX_BLUR*SwiftyGlider.blurAmount);
        instruction.render(sb);
        start.render(sb);
        sb.end();
    }

    @Override
    public void handleInput() {


        /** get our mouse */
        if(Gdx.input.justTouched()){
            mouse.x = Gdx.input.getX();
            mouse.y = Gdx.input.getY();
            cam.unproject(mouse);

            if(start.contains(mouse.x,mouse.y)){
                gsm.set(new DifficultyState(gsm));
            }
        }
    }

}
