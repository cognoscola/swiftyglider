package com.bondfire.swiftyglider.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bondfire.app.bfUtils.BlurrableTextureAtlas;
import com.bondfire.swiftyglider.SwiftyGlider;
import com.bondfire.swiftyglider.ui.Graphic;
import com.bondfire.swiftyglider.ui.WhiteButton;

public class ScoreState extends State {

    public final static String KEY_BEST ="BEST";

    private final static float INSTRUCTION_HEIGHT_R = 0.35f;
    private final static float INSTRUCTION_WIDTH_R = 0.6f;
    private final static float START_WIDTH_R = 0.25f;
    private final static float START_HEIGH_R = 0.1f;

    private Graphic instruction;
    private Graphic start;
    private Graphic back;

    /** text ***/
    private WhiteButton scoreText;
    private WhiteButton hiScoreText;
    private BitmapFont bitmapFont;

    private int lastSavePoint;

    private int level;
    private int bestLevel = 20;

    BlurrableTextureAtlas atlas;

    public ScoreState(GSM gsm, int lastSavePoint, int level) {
        super(gsm);

        atlas = (BlurrableTextureAtlas)SwiftyGlider.res.getAtlas("sprites");

        bestLevel = SwiftyGlider.preferences.getInteger(KEY_BEST,0);

        if(level > bestLevel){
            bestLevel = level;
            SwiftyGlider.preferences.putInteger(KEY_BEST,level).flush();
        }

        this.level = level;
        bitmapFont = SwiftyGlider.res.getBmpFont();

        back = new Graphic(atlas,
                atlas.findRegion("back_icon"),
                 50,
                SwiftyGlider.HEIGHT - 100,
                60,
                60);

        instruction = new Graphic(
                atlas,
                atlas.findRegion("instructions"),
                SwiftyGlider.WIDTH / 2,
                SwiftyGlider.HEIGHT * 3 / 4,
                INSTRUCTION_WIDTH_R * SwiftyGlider.WIDTH,
                INSTRUCTION_HEIGHT_R * SwiftyGlider.HEIGHT);
        start = new Graphic(
                atlas,
                atlas.findRegion("start"),
                SwiftyGlider.WIDTH / 2,
                SwiftyGlider.HEIGHT / 2,
                START_WIDTH_R * SwiftyGlider.WIDTH,
                START_HEIGH_R * SwiftyGlider.HEIGHT);

        scoreText = new WhiteButton(
                bitmapFont,
                "Score:" + level,
                SwiftyGlider.WIDTH /2 ,
                SwiftyGlider.HEIGHT /4 + 20
        );

        hiScoreText = new WhiteButton(
                bitmapFont,
                "Best:" + bestLevel,
                SwiftyGlider.WIDTH /2 ,
                SwiftyGlider.HEIGHT /4 - 50 + 20
        );

        this.lastSavePoint = lastSavePoint;
//        scoreRegion = SwiftyGlider.res.getAtlas("sprites").findRegion("button");

        /** check achievemen*/
        if(SwiftyGlider.playServices != null){

            if(bestLevel > 75){
                SwiftyGlider.playServices.Unlock(SwiftyGlider.ACHIEVE_PRO_GLIDER);
            }else if(bestLevel > PlayState.LV_GOINGFAST){
                SwiftyGlider.playServices.Unlock(SwiftyGlider.ACHIEVE_SKY_GLIDER);
            }else if(bestLevel > PlayState.LV_WINDSLOW - 1){
                SwiftyGlider.playServices.Unlock(SwiftyGlider.ACHIEVE_PERSISTEN_GLIDER);
            }else if(bestLevel > PlayState.LV_EYEOFNEEDLE){
                SwiftyGlider.playServices.Unlock(SwiftyGlider.ACHIEVE_MASTER_GLIDER);
            }else if(bestLevel >499){
                SwiftyGlider.playServices.Unlock(SwiftyGlider.ACHIEVE_GRAND_MASTER_GLIDER);
            }

            SwiftyGlider.playServices.Score(bestLevel);
        }

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
        back.render(sb);
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

                if( SwiftyGlider.adController != null){
                    SwiftyGlider.adController.newRequest();
                    SwiftyGlider.adController.setAdVisibility(false);
                }
//                gsm.set(new PlayState(gsm,PlayState.LV_WINDSLOW));
//                gsm.set(new PlayState(gsm,PlayState.LV_SUPERSLOW));
            }

            if(back.contains(mouse.x,mouse.y)){
                gsm.set(new DifficultyState(gsm));
            }
        }
    }
}
