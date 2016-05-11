package com.bondfire.swiftyglider.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.bondfire.app.services.GameParticipant;
import com.bondfire.swiftyglider.SwiftyGlider;
import com.bondfire.swiftyglider.handler.Assets;
import com.bondfire.swiftyglider.ui.BlurrableTextureAtlas;
import com.bondfire.swiftyglider.ui.Graphic;
import com.bondfire.swiftyglider.ui.WhiteButton;

/**
 * Created by alvaregd on 13/03/16.
 * The screen which is shown after a multiplayer Play state
 */
public class MultiplayerWinState  extends State {

    private Graphic back;

    private static BitmapFont bitmapFont;

    private static WhiteButton winTitle;
    private static WhiteButton restart;
    private static WhiteButton guestInstruction;

    public MultiplayerWinState(GSM gsm, boolean isWinner) {
        super(gsm);

        bitmapFont = SwiftyGlider.res.getBmpFont();


        back = new Graphic(Assets.atlas,
                Assets.atlas.findRegion("back_icon"),
                50,
                SwiftyGlider.HEIGHT - 100,
                60,
                60);

        restart = new WhiteButton(bitmapFont, "RESTART", SwiftyGlider.WIDTH / 2, +SwiftyGlider.HEIGHT / 2 - 200);
        guestInstruction = new WhiteButton(bitmapFont,"Waiting for host to start",
                SwiftyGlider.WIDTH/2, +  SwiftyGlider.HEIGHT/2 -200);
        guestInstruction.hasBackground(false);

        if (isWinner) {
            winTitle = new WhiteButton(bitmapFont, "You win! :)", SwiftyGlider.WIDTH / 2, SwiftyGlider.HEIGHT / 2 + 40);

        }else{
            winTitle = new WhiteButton(bitmapFont, "You Lost! :(", SwiftyGlider.WIDTH / 2, SwiftyGlider.HEIGHT / 2 + 40);
        }
        winTitle.hasBackground(false);

        if (isWinner) {
            if (roomExists() && SwiftyGlider.room.isConnected()) {
                SwiftyGlider.realTimeService.getSender().BroadcastWonRound();
            }
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
        back.render(sb);

        if (roomExists()) {
            if (SwiftyGlider.room.isConnected() && SwiftyGlider.room.isHost()) {
                restart.render(sb);
            }else{
                guestInstruction.render(sb);
            }

            winTitle.render(sb);
        }

        sb.end();
    }

    @Override
    public void handleInput() {

        if (Gdx.input.justTouched()) {
            mouse.x = Gdx.input.getX();
            mouse.y = Gdx.input.getY();
            cam.unproject(mouse);

            if (back.contains(mouse.x, mouse.y)) {
                //Destroy any pending invitations
                gsm.set(new MultiplayerMenuState(gsm, SwiftyGlider.room,true));
            }

            if (restart.contains(mouse.x, mouse.y)) {
                if (roomExists()) {
                    if (SwiftyGlider.room.getParticipants().size > 1) {
                        if (SwiftyGlider.room.isHost() && SwiftyGlider.room.isConnected()) {

                            SwiftyGlider.outStateMessage.actionType = SwiftyGlider.TYPE_GAME_START;
                            SwiftyGlider.outStateMessage.messageType = SwiftyGlider.MESSAGE_TYPE_ACTION;
                            SwiftyGlider.outStateMessage.difficulty = SwiftyGlider.multiplayerMode;

                            Gdx.app.log("Test","handleInput() Sending out"+SwiftyGlider.json.toJson(SwiftyGlider.outStateMessage));

                            //tell others to start the room
                            if (SwiftyGlider.room.isConnected()) {
                                for (GameParticipant participant : SwiftyGlider.room.getParticipants()) {
                                    if (participant.getParticipantId().equals(SwiftyGlider.room.getClientId())) continue;
                                    SwiftyGlider.realTimeService.getSender().OnRealTimeMessageSend(
                                            participant.getParticipantId(),
                                            SwiftyGlider.json.toJson(SwiftyGlider.outStateMessage),
                                            true
                                    );
                                }
                            }

                            //turn off the add
                            SwiftyGlider.setAddVisibiliyFalse();

                            switch (SwiftyGlider.multiplayerMode) {
                                case 0: SwiftyGlider.sessionRounds_multiplayer_normal++;break;
                                case 1: SwiftyGlider.sessionRounds_multiplayer_speedy++;break;
                                case 2: SwiftyGlider.sessionRounds_multiplayer_windy++;break;
                                case 3: SwiftyGlider.sessionRounds_multiplayer_wrecking++;break;

                            }
                            // start the round
                            gsm.set(new PlayState(gsm, SwiftyGlider.multiplayerMode, SwiftyGlider.room,true));
                        }
                    }else{
                        gsm.set(new MultiplayerMenuState(gsm,SwiftyGlider.room,true));

                    }
                }
            }
        }
    }

    private boolean roomExists() {
        return SwiftyGlider.room != null;
    }

    public void updateRoom(){

        if (roomExists()) {
            if (!SwiftyGlider.room.isConnected()) {
                gsm.set(new MultiplayerMenuState(gsm, SwiftyGlider.room,true));
            }
        }
    }
}
