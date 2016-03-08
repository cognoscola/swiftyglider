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
 * Created by alvaregd on 23/02/16.
 * Handles the multiplayer menu events
 */
public class MultiplayerMenuState extends State {

    private static final String TAG = MultiplayerMenuState.class.getName();

    private Graphic back;
    BlurrableTextureAtlas atlas;
    private static WhiteButton disconnectedinstruction;
    private static WhiteButton guestInstruction;
    private static WhiteButton joinRoom;
//    private static WhiteButton survival;
//    private static WhiteButton second;
    private static BitmapFont bitmapFont;
//    private static RadioGroup group;
    private static WhiteButton readyStatement;
    private static WhiteButton begin;

    private boolean requestSent;
    private static int count;

    public MultiplayerMenuState(GSM gsm, GameRoom room, boolean skipNetworkRequest) {
        super(gsm);

        bitmapFont = SwiftyGlider.res.getBmpFont();
        atlas = (BlurrableTextureAtlas)SwiftyGlider.res.getAtlas("sprites");

        back = new Graphic(atlas,
                atlas.findRegion("back_icon"),
                50,
                SwiftyGlider.HEIGHT - 100,
                60,
                60);

        /*group = new RadioGroup();
        survival = new WhiteButton(bitmapFont, "Survival",
                SwiftyGlider.WIDTH / 2, +SwiftyGlider.HEIGHT / 2 + 170);
        survival.setListener(new WhiteButton.OnItemSelectedListener() {
            @Override
            public void OnItemSelected() {
                Gdx.app.log(TAG, "OnItemSelected() Clicked Survial mode");
            }
        });
        group.add(survival);

        second = new WhiteButton(bitmapFont, "Second Mode",
                SwiftyGlider.WIDTH / 2, +SwiftyGlider.HEIGHT / 2 + 120);
        second.hasBackground(false);
        group.add(second);*/

        joinRoom = new WhiteButton(bitmapFont, "Join a Party!",
                SwiftyGlider.WIDTH / 2, SwiftyGlider.HEIGHT / 2 -100);

        disconnectedinstruction = new WhiteButton(bitmapFont,"You must be connected to a Party to play in " +
                "Multiplayer mode.",SwiftyGlider.WIDTH/2, +  SwiftyGlider.HEIGHT/2 + 270 );
        disconnectedinstruction.hasBackground(false);
        disconnectedinstruction.setWrap(true);
        disconnectedinstruction.setWidth(300f);

        guestInstruction = new WhiteButton(bitmapFont,"Waiting for host to start",
                SwiftyGlider.WIDTH/2, +  SwiftyGlider.HEIGHT/2 -100);
        guestInstruction.hasBackground(false);
        begin = new WhiteButton(bitmapFont, "START ROUND", SwiftyGlider.WIDTH / 2, +SwiftyGlider.HEIGHT / 2 - 100);

        if (roomExists()) {

            readyStatement =new WhiteButton(bitmapFont,room.getParticipants().size + " Players Ready",SwiftyGlider.WIDTH/2, +  SwiftyGlider.HEIGHT/2);
            updateRoom();
        }else{
            readyStatement =new WhiteButton(bitmapFont,"0 Players Ready",SwiftyGlider.WIDTH/2, +  SwiftyGlider.HEIGHT/2);
        }

        readyStatement.hasBackground(false);
        requestSent = skipNetworkRequest;
    }

    @Override
    public void update(float dt) {
        handleInput();

        //We put the invitations on the update block because it could be that we enter this
        //state while being disconnected, and then later connect to a roome while still
        //remaining in this game state. So invitations go out when we have polled a connected state
        if (roomExists()) {
            if (SwiftyGlider.room.isConnected()) {
                if (!requestSent) {
                    requestSent = true;
                    SwiftyGlider.realTimeService.getSender().CreateGameInvitations();
                    SwiftyGlider.realTimeService.getSender().setGameConnectionReady();
                }
            }
        }
    }

    public void updateRoom() {
        if (roomExists()) {

            count = 0;
            for (int i = 0; i < SwiftyGlider.room.getParticipants().size; i++) {
                if (SwiftyGlider.room.getParticipants().get(i).getPlayerStatus() != GameParticipant.STATUS_BUSY) {
                    count++;
                }
            }
            updatePlayerCount(count);
        }
    }

    private void updatePlayerCount(int count){
        Gdx.app.log(TAG, "updatePlayerCount() " + count);
        readyStatement.setText(count + " Players Ready");
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
//        SwiftyGlider.shader.setUniformf("bias", SwiftyGlider.MAX_BLUR * SwiftyGlider.blurAmount);
        back.render(sb);

        if (roomExists()) {
            if (SwiftyGlider.room.isConnected()) {
//            connectedInstruction.render(sb);
//            survival.render(sb);
//            second.render(sb);
                readyStatement.render(sb);
                if (SwiftyGlider.room.isHost()) {
                    begin.render(sb);
                } else {
                    guestInstruction.render(sb);
                }
            }else{
                joinRoom.render(sb);
                disconnectedinstruction.render(sb);
            }

        } else {
            joinRoom.render(sb);
            disconnectedinstruction.render(sb);
        }
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
                //Destroy any pending invitations
                if (Gdx.app.getType() == Application.ApplicationType.Android) {
                    SwiftyGlider.realTimeService.getSender().DestroyGameInvitations();
                    SwiftyGlider.realTimeService.getSender().DestroyGameConnection();
                }
                gsm.set(new MenuState(gsm));
            }

            if (Gdx.app.getType() == Application.ApplicationType.Android) {
                if(joinRoom.contains(mouse.x, mouse.y)) {

                    if (roomExists()) {
                        if (!SwiftyGlider.room.isConnected()) {
                            SwiftyGlider.paltformController.ShowMatches();
                        }
                    }else{
                        SwiftyGlider.paltformController.ShowMatches();
                    }
                }
            }
//            group.justTouched(mouse.x,mouse.y);

            if (begin.contains(mouse.x, mouse.y)) {
                if (roomExists()) {
                    if (SwiftyGlider.room.isHost() && SwiftyGlider.room.isConnected()) {

                        SwiftyGlider.outStateMessage.actionType = SwiftyGlider.TYPE_GAME_START;
                        SwiftyGlider.outStateMessage.messageType = SwiftyGlider.MESSAGE_TYPE_ACTION;

                        //turn off the add
                        SwiftyGlider.setAddVisibiliyFalse();

                        /** start the round */
                        gsm.set(new PlayState(gsm, 0, SwiftyGlider.room));

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
                    }
                }
            }


            if (readyStatement.contains(mouse.x, mouse.y)) {
                SwiftyGlider.paltformController.ShowMatches();
            }
        }
    }

    private boolean roomExists() {
        return SwiftyGlider.room != null;
    }


}
