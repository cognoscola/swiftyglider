package com.bondfire.swiftyglider.states;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import com.bondfire.app.services.GameParticipant;
import com.bondfire.app.services.GameRoom;
import com.bondfire.swiftyglider.SwiftyGlider;
import com.bondfire.swiftyglider.handler.Assets;
import com.bondfire.swiftyglider.network.DifficultySelectMessage;
import com.bondfire.swiftyglider.ui.BlurrableTextureAtlas;
import com.bondfire.swiftyglider.ui.Graphic;
import com.bondfire.swiftyglider.ui.RadioGroup;
import com.bondfire.swiftyglider.ui.WhiteButton;

/**
 * Created by alvaregd on 23/02/16.
 * Handles the multiplayer menu events
 */
public class MultiplayerMenuState extends State {

    private static final String TAG = MultiplayerMenuState.class.getName();

    private Graphic back;


    private static WhiteButton disconnectedinstruction;
    private static WhiteButton guestInstruction;
    private static WhiteButton hostInstruction;

    private Array<WhiteButton> modeButtons;
    //Increase frequency, increase descent speed, Windy, Tight
    private static String[] levelNames = {"Easy","Speedy","Windy","Nerve Wracking"};
    private static WhiteButton joinRoom;
//    private static WhiteButton survival;
//    private static WhiteButton second;
    private static BitmapFont bitmapFont;

    public static RadioGroup getRadioGroup() {
        return radioGroup;
    }

    private static RadioGroup radioGroup;
    private static WhiteButton readyStatement;
    private static WhiteButton begin;

    private static DifficultySelectMessage inMessage;
    private static DifficultySelectMessage outMessage;

    private boolean requestSent;
    private boolean disconnectLatch = false; //When we disconnect with the room while being in this state, we need logic that will
                                             //'reset' the conditions that will allow us to reconnect if the client decides to reconnect
                                             //to a new room. This variable is a "latch" that helps us send to ConnectionReady message
                                             //only once every time we reconnect

    private static int count;                //holds the total number of players participating in this game
    private boolean invitationSpamBlocker = false; // in place to prevent player from spamming this button

    public MultiplayerMenuState(GSM gsm, GameRoom room, boolean skipNetworkRequest) {
        super(gsm);

        inMessage = new DifficultySelectMessage();
        outMessage = new DifficultySelectMessage();

        bitmapFont = SwiftyGlider.res.getBmpFont();


        back = new Graphic(Assets.atlas,
                Assets.atlas.findRegion("back_icon"),
                50,
                SwiftyGlider.HEIGHT - 100,
                60,
                60);

        modeButtons = new Array<WhiteButton>();
        radioGroup = new RadioGroup();

        for(int i = 0; i <levelNames.length ; i++){
            WhiteButton button =new WhiteButton(
                    bitmapFont,
                    levelNames[i],
                    SwiftyGlider.WIDTH/2,
                    SwiftyGlider.HEIGHT/2 + 200 - 50 * i
            );
            if (i != 0) {
                button.hasBackground(false);
            }
            modeButtons.add(button);
            radioGroup.add(button);
        }

        joinRoom = new WhiteButton(bitmapFont, "Join a Party!",
                SwiftyGlider.WIDTH / 2, SwiftyGlider.HEIGHT / 2 -100);

        disconnectedinstruction = new WhiteButton(bitmapFont,"You must be connected to a Party to play in " +
                "Multiplayer mode.",SwiftyGlider.WIDTH/2, +  SwiftyGlider.HEIGHT/2 + 270 );
        disconnectedinstruction.hasBackground(false);
        disconnectedinstruction.setWrap(true);
        disconnectedinstruction.setWidth(300f);

        guestInstruction = new WhiteButton(bitmapFont,"Waiting for host to start",
                SwiftyGlider.WIDTH/2, +  SwiftyGlider.HEIGHT/2 -200);
        guestInstruction.hasBackground(false);
        begin = new WhiteButton(bitmapFont, "START ROUND", SwiftyGlider.WIDTH / 2, +SwiftyGlider.HEIGHT / 2 - 200);

        if (roomExists()) {
            readyStatement =new WhiteButton(bitmapFont,room.getParticipants().size + " Players Ready",SwiftyGlider.WIDTH/2, +  SwiftyGlider.HEIGHT/2 -150);
            if (SwiftyGlider.room.isHost()) {
                hostInstruction = new WhiteButton(bitmapFont,"Select Mode",SwiftyGlider.WIDTH/2, +  SwiftyGlider.HEIGHT/2 + 270);
                hostInstruction.hasBackground(false);
            }else{
                hostInstruction = new WhiteButton(bitmapFont,"Mode",SwiftyGlider.WIDTH/2, +  SwiftyGlider.HEIGHT/2 + 270);
                hostInstruction.hasBackground(false);
            }
            updateRoom();
        }else{
            readyStatement =new WhiteButton(bitmapFont,"0 Players Ready",SwiftyGlider.WIDTH/2, +  SwiftyGlider.HEIGHT/2);
        }

        readyStatement.hasBackground(false);
        requestSent = skipNetworkRequest;

        invitationSpamBlocker = false;
    }

    @Override
    public void update(float dt) {
        handleInput();

        //We put the invitations on the update block because it could be that we enter this
        //state while being disconnected, and then later connect to a roome while still
        //remaining in this game state. So invitations go out when we have polled a connected state
        if (roomExists()) {
            if (SwiftyGlider.room.isConnected()) {
                disconnectLatch = false;
                if (!requestSent) {
                    requestSent = true;
                    invitationSpamBlocker =false;
                    SwiftyGlider.realTimeService.getSender().setGameConnectionReady();
                }
            }else if (!disconnectLatch ) {
                requestSent = false;
                disconnectLatch = true;
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
            updateInstruction();
        }
    }

    private void updateInstruction(){

        if (SwiftyGlider.room.isHost()) {
            hostInstruction.setText("Select Mode");
        }else{
            hostInstruction.setText("Mode");
        }
    }

    private void updatePlayerCount(int count){
        Gdx.app.log(TAG, "updatePlayerCount() " + count);
        readyStatement.setText(count + " Players Ready");

        if (count == 1) {
            begin.setText("INVITE PLAYERS");
        } else if (count > 1) {
            begin.setText("START ROUND");
        }
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
                for(int i = 0; i < modeButtons.size; i++){
                    modeButtons.get(i).render(sb);
                }
                hostInstruction.render(sb);
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

            if (roomExists()) {
                if (SwiftyGlider.room.isHost()) {
                    radioGroup.justTouched(mouse.x,mouse.y);
                    if (radioGroup.getSelecteIndex() != -1) {

                        //send out mode change message
                        outMessage.mode = radioGroup.getSelecteIndex();
                        outMessage.messageType = SwiftyGlider.MESSAGE_TYPE_DIFFICULTY_SELECT;

                        //tell others of the mode change
                        if (SwiftyGlider.room.isConnected()) {
                            for (GameParticipant participant : SwiftyGlider.room.getParticipants()) {
                                if (participant.getParticipantId().equals(SwiftyGlider.room.getClientId())) continue;
                                SwiftyGlider.realTimeService.getSender().OnRealTimeMessageSend(
                                        participant.getParticipantId(),
                                        SwiftyGlider.json.toJson(outMessage),
                                        true
                                );
                            }
                        }
                    }
                }
            }

            if (begin.contains(mouse.x, mouse.y)) {

                if (count > 1) {

                    if (roomExists()) {
                        if (SwiftyGlider.room.isHost() && SwiftyGlider.room.isConnected()) {

                            SwiftyGlider.outStateMessage.actionType = SwiftyGlider.TYPE_GAME_START;
                            SwiftyGlider.outStateMessage.messageType = SwiftyGlider.MESSAGE_TYPE_ACTION;
                            SwiftyGlider.outStateMessage.difficulty = radioGroup.getSelecteIndex();

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

                            //note the mode selected
                            SwiftyGlider.multiplayerMode = radioGroup.getSelecteIndex();

                            switch (SwiftyGlider.multiplayerMode) {
                                case 0: SwiftyGlider.sessionRounds_multiplayer_normal++;break;
                                case 1: SwiftyGlider.sessionRounds_multiplayer_speedy++;break;
                                case 2: SwiftyGlider.sessionRounds_multiplayer_windy++;break;
                                case 3: SwiftyGlider.sessionRounds_multiplayer_wrecking++;break;
                            }


                            // start the round
                            gsm.set(new PlayState(gsm, radioGroup.getSelecteIndex(), SwiftyGlider.room,true));
                        }
                    }
                }else{
                    //Invite Players
                    if (roomExists()) {
                        if (SwiftyGlider.room.isConnected()) {
                            if (!invitationSpamBlocker) {
                                SwiftyGlider.realTimeService.getSender().CreateGameInvitations();
                                SwiftyGlider.paltformController.SendToast("Invitations Sent");
                                invitationSpamBlocker = true;
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

    /**
     * Receive game messages sent by other clients
     * @param message game message
     * @param senderId the sender participant ID
     */
    public void receiveMessage(String message, String senderId) {
        if (message.contains(SwiftyGlider.MESSAGE_TYPE_DIFFICULTY_SELECT)) {

            //double check that the message is coming from host
            inMessage = SwiftyGlider.json.fromJson( DifficultySelectMessage.class,message);
            radioGroup.setSelectedItem(inMessage.mode);
            SwiftyGlider.multiplayerMode = inMessage.mode;
        }
    }
}
