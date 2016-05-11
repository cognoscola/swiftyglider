package com.bondfire.swiftyglider.handler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.utils.Array;
import com.bondfire.swiftyglider.ui.BlurrableTextureAtlas;

/**
 * Created by alvaregd on 10/05/16.
 */
public class BlurrableTextureAtlasLoader extends AsynchronousAssetLoader<BlurrableTextureAtlas, BlurrableTextureAtlasLoader.BlurrableTextureAtlasParameter > {
    private static final String TAG = BlurrableTextureAtlasLoader.class.getName();

    public BlurrableTextureAtlasLoader(FileHandleResolver resolver) {
        super(resolver);
    }


    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, BlurrableTextureAtlasParameter parameter) {

        Gdx.app.log(TAG,"getDependencies() creating TextureAtlas");
        return null;
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, BlurrableTextureAtlasParameter parameter) {
        Gdx.app.log(TAG,"loadAsync() Applying Blurr");


    }

    @Override
    public BlurrableTextureAtlas loadSync(AssetManager manager, String fileName, FileHandle file, BlurrableTextureAtlasParameter parameter) {
        Gdx.app.log(TAG,"loadSync() ");
//        return atlas;

        BlurrableTextureAtlas atlas = new BlurrableTextureAtlas(file);
        TextureData texData = atlas.getRegions().first().getTexture().getTextureData();
        texData.prepare();
        atlas.PrepareBlur(texData.consumePixmap());
        atlas.bind();

        return atlas;
    }


    static public class BlurrableTextureAtlasParameter extends AssetLoaderParameters<BlurrableTextureAtlas> {
        /** whether to flip the texture atlas vertically **/
        public boolean flip = false;

        public BlurrableTextureAtlasParameter () {
        }

        public BlurrableTextureAtlasParameter (boolean flip) {
            this.flip = flip;
        }
    }
}
