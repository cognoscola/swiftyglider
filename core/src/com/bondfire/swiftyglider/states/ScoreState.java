package com.bondfire.swiftyglider.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bondfire.swiftyglider.SwiftyGlider;
import com.bondfire.swiftyglider.ui.Graphic;
import com.bondfire.swiftyglider.ui.WhiteButtons;

/**
 * Created by alvaregd on 14/06/15.
 */
public class ScoreState extends State {

    private Graphic instruction;
    private Graphic start;

    /** text ***/
    private WhiteButtons scoreText;
    private WhiteButtons hiScoreText;
    private BitmapFont bitmapFont;

    private int lastSavePoint;

    private int level;
    private int bestLevel = 20;


    public ScoreState(GSM gsm, int lastSavePoint, int level) {
        super(gsm);

        this.level = level;
        bitmapFont = SwiftyGlider.res.GeneratorFont();

        instruction = new Graphic(
                SwiftyGlider.res.getAtlas("sprites").findRegion("instructions"),
                SwiftyGlider.WIDTH / 2,
                SwiftyGlider.HEIGHT * 3 / 4);
        start = new Graphic(
                SwiftyGlider.res.getAtlas("sprites").findRegion("start"),
                SwiftyGlider.WIDTH / 2,
                SwiftyGlider.HEIGHT / 2);

        scoreText = new WhiteButtons(
                bitmapFont,
                "Score:" + level,
                SwiftyGlider.WIDTH /2 ,
                SwiftyGlider.HEIGHT /4
        );

        hiScoreText = new WhiteButtons(
                bitmapFont,
                "Best:" + bestLevel,
                SwiftyGlider.WIDTH /2 ,
                SwiftyGlider.HEIGHT /4 - 50
        );



//        scoreRegion = SwiftyGlider.res.getAtlas("sprites").findRegion("button");

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

        scoreText.render(sb);
        hiScoreText.render(sb);



     /*   sb.draw(scoreRegion,
                SwiftyGlider.WIDTH /2 - 300/2,
                SwiftyGlider.HEIGHT /4 - 300/2,
                300,300);*/




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
