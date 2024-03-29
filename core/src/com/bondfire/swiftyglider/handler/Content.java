package com.bondfire.swiftyglider.handler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.util.HashMap;

/**
 * This class will handle all of the game's resources
 */
public class Content {

    HashMap<String, TextureAtlas> atlases;
    FreeTypeFontGenerator textGenerator;
    FreeTypeFontGenerator.FreeTypeFontParameter parameter;

    String fontPath;

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

    public void LoadFont(String path){
        this.fontPath = path;
    }

    public BitmapFont GeneratorFont(){

        this.textGenerator = new FreeTypeFontGenerator((Gdx.files.internal(fontPath)));
        this.parameter =     new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 35;
        BitmapFont ret = textGenerator.generateFont(parameter);
        textGenerator.dispose();
        return ret;
    }
}
