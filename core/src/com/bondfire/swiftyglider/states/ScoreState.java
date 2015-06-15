package com.bondfire.swiftyglider.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bondfire.swiftyglider.SwiftyGlider;
import com.bondfire.swiftyglider.ui.Graphic;

/**
 * Created by alvaregd on 14/06/15.
 */
public class ScoreState extends State {

    private Graphic instruction;
    private Graphic start;
    private TextureRegion scoreRegion;

    private int lastSavePoint;


    public ScoreState(GSM gsm, int lastSavePoint) {
        super(gsm);

        instruction = new Graphic(
                SwiftyGlider.res.getAtlas("sprites").findRegion("instructions"),
                SwiftyGlider.WIDTH / 2,
                SwiftyGlider.HEIGHT * 3 / 4);
        start = new Graphic(
                SwiftyGlider.res.getAtlas("sprites").findRegion("start"),
                SwiftyGlider.WIDTH / 2,
                SwiftyGlider.HEIGHT / 2);

        scoreRegion = SwiftyGlider.res.getAtlas("sprites").findRegion("button");

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
        sb.draw(scoreRegion,
                SwiftyGlider.WIDTH /2 - 300/2,
                SwiftyGlider.HEIGHT /4 - 300/2,
                300,300);
        sb.end();
    }

    @Override
    public void handleInput() {

        /** get our mouse */
        if (Gdx.input.justTouched()) {
            mouse.x = Gdx.input.getX();
            mouse.y = Gdx.input.getY();
            cam.unproject(mouse);

            if (start.contains(mouse.x, mouse.y)) {
                gsm.set(new PlayState(gsm,lastSavePoint));
            }
        }
    }
}
