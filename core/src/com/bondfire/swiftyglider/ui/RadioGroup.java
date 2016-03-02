package com.bondfire.swiftyglider.ui;

import com.badlogic.gdx.utils.Array;

/**
 * Created by alvaregd on 24/02/16.
 * Class to handle a "radio group" structure
 */
public class RadioGroup{

    private Array<WhiteButton> buttons;
    private int selectedItemIndex = 0;

    public RadioGroup() {
        buttons = new Array<WhiteButton>();
    }

    public void add(WhiteButton button) {
        buttons.add(button);
    }

    public void justTouched(float x, float y) {

        boolean shouldChange = false;

        for (int i =0; i < buttons.size; i++) {
            WhiteButton button = buttons.get(i);
            if (button.contains(x, y)) {
                shouldChange =true;
                button.hasBackground(true);
                selectedItemIndex = i;
            } else {
                button.hasBackground(false);
            }
        }

        if (!shouldChange) {
            buttons.get(selectedItemIndex).hasBackground(true);
        }
    }

    public WhiteButton getSelectedItem() {

        try {
            if (selectedItemIndex != -1) {
                return buttons.get(selectedItemIndex);
            } else {
                return null;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
}