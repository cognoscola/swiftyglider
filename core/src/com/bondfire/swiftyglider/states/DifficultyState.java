package com.bondfire.swiftyglider.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.bondfire.swiftyglider.SwiftyGlider;
import com.bondfire.swiftyglider.ui.WhiteButtons;


public class DifficultyState extends State{

    private final static String LV1_TEXT = "Very Beginning";
    private final static String LV2_TEXT = "First Wind";
    private final static String LV3_TEXT = "Feeling Courageous";
    private final static String LV4_TEXT =  "Soaring Winds";
    private final static String LV5_TEXT = "Thick Brushes";
    private final static String LV6_TEXT = "Tricky Wind";
    private final static String LV7_TEXT = "Threading the Needle";

    String[] texts;

    private Array<WhiteButtons> buttons;
    private BitmapFont bitmapFont;

    public DifficultyState(GSM gsm){
        super(gsm);

        //TODO
        SwiftyGlider.preferences.putInteger(ScoreState.KEY_BEST, 399).flush();

       texts = getNames(SwiftyGlider.preferences.getInteger(ScoreState.KEY_BEST,0));

        buttons = new Array<WhiteButtons>();
        bitmapFont = SwiftyGlider.res.GeneratorFont();

        for(int i = 0; i <texts.length ; i++){
            buttons.add(
                    new WhiteButtons(
                            bitmapFont,
                            texts[i],
                            SwiftyGlider.WIDTH/2,
                            SwiftyGlider.HEIGHT/2 + 200 - 70 * i
                            )
            );
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
        for(int i = 0; i <buttons.size; i++){
            buttons.get(i).render(sb);
        }
        sb.end();
    }

    @Override
    public void handleInput() {

        /** get our mouse */
        if(Gdx.input.justTouched()){
            mouse.x = Gdx.input.getX();
            mouse.y = Gdx.input.getY();
            cam.unproject(mouse);

            for(int i = 0; i <buttons.size; i++){
                if(buttons.get(i).contains(mouse.x, mouse.y)){
//                    System.out.println("Clicked: " + i);
                    gsm.set(new PlayState(gsm, getLevel(i)));
                }
            }
        }
    }

    public int getLevel(int i){
        switch(i){
            case 0: return PlayState.LV_BEGINNING;
            case 1: return PlayState.LV_FIRSTWIND;
            case 2: return PlayState.LV_GOINGFAST;
            case 3: return PlayState.LV_WINDFAST;
            case 4: return PlayState.LV_SUPERSLOW;
            case 5: return PlayState.LV_WINDSLOW;
            case 6: return PlayState.LV_EYEOFNEEDLE;
            default: return PlayState.LV_BEGINNING;
        }
    }

    public String[] getNames(int level) {

        if (level >= PlayState.LV_EYEOFNEEDLE) {
            return new String[]  {
                    LV1_TEXT,
                    LV2_TEXT,
                    LV3_TEXT,
                    LV4_TEXT,
                    LV5_TEXT,
                    LV6_TEXT,
                    LV7_TEXT};
        }
        else if (level >= PlayState.LV_WINDSLOW) {
            return new String[]  {
                    LV1_TEXT,
                    LV2_TEXT,
                    LV3_TEXT,
                    LV4_TEXT,
                    LV5_TEXT,
                    LV6_TEXT,
                   };
        }
        else if (level >= PlayState.LV_SUPERSLOW) {
            return new String[]  {
                    LV1_TEXT,
                    LV2_TEXT,
                    LV3_TEXT,
                    LV4_TEXT,
                    LV5_TEXT
            };
        }

        else if (level >= PlayState.LV_WINDFAST) {
            return new String[]  {
                    LV1_TEXT,
                    LV2_TEXT,
                    LV3_TEXT,
                    LV4_TEXT
            };
        }

        else if (level >= PlayState.LV_GOINGFAST) {
            return new String[]  {
                    LV1_TEXT,
                    LV2_TEXT,
                    LV3_TEXT
            };
        }

        else if (level >= PlayState.LV_FIRSTWIND) {
            return new String[]  {
                    LV1_TEXT,
                    LV2_TEXT,
            };
        }

        else  {
            return new String[]  {
                    LV1_TEXT,
            };
        }
    }
}
