package com.bondfire.swiftyglider.handler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.HashMap;

/**
 * This class will handle all of the game's resources
 */
public class Content {

    HashMap<String, TextureAtlas> atlases ;

    public Content(){
        atlases = new HashMap<String, TextureAtlas>();
    }

    /** here we will fetch the assets and put them int memory*/
    public void LoadAtlas(String path, String key){
        atlases.put(key, new TextureAtlas(Gdx.files.internal(path)));
    }


    public TextureAtlas getAtlas(String key ){
       return atlases.get(key);
    }
}
