package com.bondfire.swiftyglider.network;

/**
 * Created by alvaregd on 03/03/16.
 */
public class EnvironmenMessage {

    /** contains information about a round's environment, such as if
     * there is wind, and wall descent speed
     */
    public String messageType;
    public float windHeightOffset;
    public int wind;

}
