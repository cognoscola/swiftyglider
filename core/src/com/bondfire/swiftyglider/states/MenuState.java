package com.bondfire.swiftyglider.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bondfire.swiftyglider.SwiftyGlider;
import com.bondfire.swiftyglider.ui.Graphic;

/**
 * Render our main menu
 */
public class MenuState extends State {

    private Graphic instruction;
    private Graphic start;

    public MenuState(GSM gsm){
        super(gsm);

        instruction = new Graphic(
                SwiftyGlider.res.getAtlas("sprites").findRegion("instructions"),
                SwiftyGlider.WIDTH/2,
                SwiftyGlider.HEIGHT*3/4 );
        start = new Graphic(
                SwiftyGlider.res.getAtlas("sprites").findRegion("start"),
                SwiftyGlider.WIDTH/2,
                SwiftyGlider.HEIGHT/2);
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
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
