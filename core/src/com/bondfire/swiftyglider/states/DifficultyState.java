package com.bondfire.swiftyglider.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.bondfire.swiftyglider.SwiftyGlider;
import com.bondfire.swiftyglider.ui.WhiteButtons;



public class DifficultyState extends State{

    private Array<WhiteButtons> buttons;
    private BitmapFont bitmapFont;

    public DifficultyState(GSM gsm){
        super(gsm);

        String[] texts  = {"Very Beginning", "difficulty 2", "Difficulty 3","Difficulty 3","Difficulty 3","Difficulty 3"};
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
        handeInput();
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
    public void handeInput() {

        /** get our mouse */
        if(Gdx.input.justTouched()){
            mouse.x = Gdx.input.getX();
            mouse.y = Gdx.input.getY();
            cam.unproject(mouse);

            for(int i = 0; i <buttons.size; i++){
                if(buttons.get(i).contains(mouse.x, mouse.y)){

//                    System.out.println("Clicked: " + i);
                    gsm.set(new PlayState(gsm));
                }
            }
        }
    }
}
