package com.bondfire.swiftyglider.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bondfire.app.bfUtils.BlurUtils;

/**
 * Created by alvaregd on 10/05/16.
 */
public class BlurrableTextureAtlas extends TextureAtlas {
    private static final String TAG = BlurrableTextureAtlas.class.getName();

    Pixmap pixmap;
    public Texture tex;
    private boolean isBlurrable = false;
    private boolean isBound = false;
    private boolean isHorizontalStretched = false;
    private static float stretchRatio;

    public BlurrableTextureAtlas() {
    }

    public BlurrableTextureAtlas(FileHandle packFile) {
        super(packFile);
    }

    public void PrepareBlur(Pixmap pixmap){

        if(!isBlurrable){
            System.out.println("Blurrable, loading tex");
            checkDimensions(pixmap);
            tex = new Texture(this.pixmap, Pixmap.Format.RGBA8888, false);
            isBlurrable= true;
        }
    }

    public boolean isBlurrable(){
        return isBlurrable;
    }

    private void checkDimensions(Pixmap pixmap){

        int w = pixmap.getWidth(), h = pixmap.getHeight();
        if(w != h) {
            int max = w > h? w : h;
            stretchRatio = w > h ? (float)w/(float)h : (float)h/(float)w;
            isHorizontalStretched = w < h ;
            Pixmap pixmap2 = new Pixmap(max, max, Pixmap.Format.RGBA8888);
            pixmap2.drawPixmap(pixmap, 0, 0);
            this.pixmap = pixmap2;
        }else{
            this.pixmap = pixmap;
        }
    }

    public void bind(){
        tex.bind();
        if(!isBound){
            BlurUtils.generateBlurredMipmaps(pixmap, pixmap.getWidth(), pixmap.getHeight(), 1, 3, true);
            tex.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
            tex.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
            isBound = true;
            Gdx.app.log(TAG,"bind() Ratio:" + stretchRatio);

        }
    }

    @Override
    public AtlasRegion findRegion(String name) {
        AtlasRegion region = super.findRegion(name);
        AtlasRegion newRegion;

        if (isBlurrable) {

            //the original stretch vertically only
            newRegion = new AtlasRegion(
                    tex,
                    region.getRegionX(),
                    region.getRegionY(),
                    region.getRegionWidth(),
                    region.getRegionHeight()
            );
            //change the texture region details because the texture has stretched!
        } else {
            newRegion = region;
        }
        return newRegion;
    }
}
