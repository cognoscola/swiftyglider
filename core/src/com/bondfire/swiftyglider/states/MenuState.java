package com.bondfire.swiftyglider.states;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bondfire.app.bfUtils.BlurrableTextureAtlas;
import com.bondfire.app.services.GameParticipant;
import com.bondfire.app.services.GameRoom;
import com.bondfire.swiftyglider.SwiftyGlider;
import com.bondfire.swiftyglider.ui.Graphic;
import com.bondfire.swiftyglider.ui.WhiteButton;

/**
 * Render our main menu
 */
public class MenuState extends State {

    private final static float INSTRUCTION_HEIGHT_R = 0.35f;
    private final static float INSTRUCTION_WIDTH_R = 0.7f;

    private Graphic instruction;

    private WhiteButton multiplayer;
    private WhiteButton singleplayer;

    public MenuState(GSM gsm){
        super(gsm);

        BlurrableTextureAtlas atlas = (BlurrableTextureAtlas)SwiftyGlider.res.getAtlas("sprites");
        BitmapFont bitmapFont = SwiftyGlider.res.getBmpFont();

        instruction = new Graphic(
                atlas,
                atlas.findRegion("instructions"),
                SwiftyGlider.WIDTH/2,
                SwiftyGlider.HEIGHT*3/4,
                INSTRUCTION_WIDTH_R * SwiftyGlider.WIDTH,
                INSTRUCTION_HEIGHT_R * SwiftyGlider.HEIGHT);

        singleplayer = new WhiteButton(bitmapFont, "Singleplayer",
                SwiftyGlider.WIDTH / 2,
                SwiftyGlider.HEIGHT / 2);

        multiplayer = new WhiteButton(bitmapFont, "Multiplayer",
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

//        SwiftyGlider.shader.setUniformf(SwiftyGlider.biasLocation, SwiftyGlider.MAX_BLUR*SwiftyGlider.blurAmount);
//        SwiftyGlider.shader.setUniformf("bias", SwiftyGlider.MAX_BLUR*SwiftyGlider.blurAmount);
        instruction.render(sb);
        singleplayer.render(sb);
        multiplayer.render(sb);
        sb.end();
    }

    @Override
    public void handleInput() {

        /** get our mouse */
        if(Gdx.input.justTouched()){
            mouse.x = Gdx.input.getX();
            mouse.y = Gdx.input.getY();
            cam.unproject(mouse);

            if(singleplayer.contains(mouse.x,mouse.y)){
                gsm.set(new DifficultyState(gsm));
            }

            if (multiplayer.contains(mouse.x, mouse.y)) {
                if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
                    GameRoom room = new GameRoom();
                    room.setClientId("ID");
                    room.setGameHostId("ID");
                    room.getParticipants().add(new GameParticipant());
                    gsm.set(new MultiplayerMenuState(gsm, room,true));
                }else{
                    gsm.set(new MultiplayerMenuState(gsm, SwiftyGlider.room,false));
                }
            }
        }
    }
}
