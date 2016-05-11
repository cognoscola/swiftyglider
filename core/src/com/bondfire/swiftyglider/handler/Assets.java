package com.bondfire.swiftyglider.handler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.bondfire.swiftyglider.ui.BlurrableTextureAtlas;


/**
 * Created by alvaregd on 10/05/16.
 * Improving asset loading techniques
 */
public class Assets {
    private static final String TAG = Assets.class.getName();

    private static final AssetManager assetManager = new AssetManager();

    //a place to store assets
    public static BlurrableTextureAtlas atlas;

    //load assets required for the loading or splash screen
    public static void loadBefore(){

        Gdx.app.log(TAG,"loadBefore() Loading the loading screen");
    }

    public static void loadFull(){

        FileHandleResolver resolve = new InternalFileHandleResolver();
        assetManager.setLoader(
                com.bondfire.swiftyglider.ui.BlurrableTextureAtlas.class,
                new BlurrableTextureAtlasLoader(resolve));

        assetManager.load("graphics/swifty.pack",BlurrableTextureAtlas.class);
    }

    public static float getProgress(){

        float progress = assetManager.getProgress();
        assetManager.update();
        if (progress == 1) {
            Gdx.app.log(TAG,"getProgress() finished loading pack");
            if (atlas == null) {
                atlas = assetManager.get("graphics/swifty.pack", BlurrableTextureAtlas.class);
            }
        }
        return progress;
    }

    public static void dispose(){
        atlas.dispose();
        assetManager.dispose();

    }
}
