package com.bondfire.swiftyglider.network;


/**
 * Created by alvaregd on 02/03/16.
 */
public class GameStateMessage {

    public final static String MESSAGE_TYPE = "STATE_MESSAGE";
    public static int TYPE_GAME_START= 0;
    public static int TYPE_GAME_STOP = 1;
    public static int TYPE_START_ACK = 2;

    public GameStateMessage() {
        this.messageType = MESSAGE_TYPE;
    }

    public String messageType;
    public int actionType;
}
