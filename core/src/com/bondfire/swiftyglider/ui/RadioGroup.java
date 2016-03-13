package com.bondfire.swiftyglider.ui;

import com.badlogic.gdx.utils.Array;

/**
 * Created by alvaregd on 24/02/16.
 * Class to handle a "radio group" structure
 */
public class RadioGroup{

    private Array<WhiteButton> views;
    private int selectedItemIndex = 0;

    public RadioGroup() {
        views = new Array<WhiteButton>();
    }

    public void add(WhiteButton button) {
        views.add(button);
    }

    /**
     * When an item is Selected, we handle the highlight of each item to indicate the touched item
     * @param x the coordinate of the user's touch (left to right)
     * @param y the coordinate of the user's touch in the Y axis (up and down)
     * @return  the currently selected item
     */
    public int justTouched(float x, float y) {

        boolean shouldChange = false;

        for (int i = 0; i < views.size; i++) {
            WhiteButton button = views.get(i);
            if (button.contains(x, y)) {
                shouldChange =true;
                button.hasBackground(true);
                selectedItemIndex = i;
            } else {
                button.hasBackground(false);
            }
        }

        /** Did we notice any change in selected item? **/
        if (!shouldChange) {
            //no, so return no new item touched
            views.get(selectedItemIndex).hasBackground(true);
            return -1;
        }else{
            //return new item touched index
            return selectedItemIndex;
        }
    }

    /**
     * Gets the selected item in the radio group
     * @return the instance of the view
     */
    public WhiteButton getSelectedItem() {
        try {
            if (selectedItemIndex != -1) {
                return views.get(selectedItemIndex);
            } else {
                return null;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    /** set the selected item index */
    public void setSelectedItem(int item){

        //safety
        if (item > views.size - 1 || item < 0) {
            return;
        }
        selectedItemIndex = item;
        for(int i = 0 ; i< views.size; i++) {

            WhiteButton button = views.get(i);
            if (i == selectedItemIndex) {
                button.hasBackground(true);
            }else{
                button.hasBackground(false);
            }
        }
    }
}