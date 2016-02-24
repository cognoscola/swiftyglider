package com.bondfire.swiftyglider.states;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bondfire.app.bfUtils.BlurrableTextureAtlas;
import com.bondfire.swiftyglider.SwiftyGlider;
import com.bondfire.swiftyglider.ui.Graphic;
import com.bondfire.swiftyglider.ui.WhiteButton;

/**
 * Created by alvaregd on 23/02/16.
 * Handles the multiplayer menu events
 */
public class MultiplayerMenuState extends State {

    private Graphic back;

    BlurrableTextureAtlas atlas;

    private WhiteButton instruction;
    private WhiteButton joinRoom;

    private BitmapFont bitmapFont;

    protected MultiplayerMenuState(GSM gsm) {
        super(gsm);

        bitmapFont = SwiftyGlider.res.getBmpFont();

        atlas = (BlurrableTextureAtlas)SwiftyGlider.res.getAtlas("sprites");
        back = new Graphic(atlas,
                atlas.findRegion("back_icon"),
                50,
                SwiftyGlider.HEIGHT - 100,
                60,
                60);




        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            if (SwiftyGlider.room.isConnected()) {

            }else{
                showDisconnectedView();
            }
        }else{
            showDisconnectedView();
        }
    }

    public void refreshRoomStatus(){

    }

    public void showDisconnectedView(){
        //Show text and a button that takes your to social room
        instruction = new WhiteButton(bitmapFont,"You must be connected to a Party to play in " +
                "Multiplayer mode.",SwiftyGlider.WIDTH/2, +  SwiftyGlider.HEIGHT/2 + 270 );
        instruction.setBackgroundVisibility(false);
        instruction.setWrap(true);
        instruction.setWidth(300f);

        joinRoom = new WhiteButton(bitmapFont, "Join a Party!",
                SwiftyGlider.WIDTH / 2, SwiftyGlider.HEIGHT / 2 -100);

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
        joinRoom.render(sb);
        sb.end();
    }

    @Override
    public void handleInput() {
        /** get our mouse */
        if (Gdx.input.justTouched()) {
            mouse.x = Gdx.input.getX();
            mouse.y = Gdx.input.getY();
            cam.unproject(mouse);

            if(back.contains(mouse.x,mouse.y)){
                gsm.set(new MenuState(gsm));
            }

            if (Gdx.app.getType() == Application.ApplicationType.Android) {
                if(back.contains(mouse.x,mouse.y)){
                    SwiftyGlider.paltformController.ShowMatches();
                }
            }
        }
    }
}
