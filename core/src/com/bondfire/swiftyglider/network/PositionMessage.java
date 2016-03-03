package com.bondfire.swiftyglider.network;


/**
 * Created by alvaregd on 02/03/16.
 */
public class PositionMessage {

    public final static String MESSAGE_TYPE = "POS_MESSAGE";

    public PositionMessage() {
        this.messageType = MESSAGE_TYPE;
    }

    public String messageType;
    public float x;
    public float y;

}
