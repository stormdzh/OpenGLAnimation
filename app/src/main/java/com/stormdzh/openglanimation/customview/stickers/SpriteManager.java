package com.stormdzh.openglanimation.customview.stickers;

import android.content.Context;
import android.text.TextUtils;

import com.stormdzh.openglanimation.util.LogUtil;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class SpriteManager {
    private SpriteManager() {

    }
    float scaleForParent=1.0f;
    int width, height;
    float scale = 1.5f;
    int dis_slot=20;
    int dis_area=7000;
    public void setCoordinateConvert(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public static SpriteManager getInstance() {
        return InnerLoader.spriteManager;
    }
    public void setScaleFitParentView(float scale){
        this.scaleForParent=scale;
    }
    static final String TAG = "SpriteManager";
    ConcurrentHashMap<String, Sprite> hashMap = new ConcurrentHashMap<>();

    public String addSprite(SpriteMsg spriteMsg, String objid, Context ctx) {
        LogUtil.d(TAG, "addSprite");
        String temp = objid;
//        if (spriteMsg.type.equals(Sprite.TYPE_BIRD)) {
//            BirdSprite birdSprite = new BirdSprite(ctx);
//            birdSprite.curX = spriteMsg.location_x;
//            birdSprite.curY = spriteMsg.location_y;
//            birdSprite.setUp();
//            birdSprite.setDisplayParam(width, height);
//            birdSprite.rotate(spriteMsg.rotation_degree);
//            hashMap.put(temp, birdSprite);
//
//        } else
        if (spriteMsg.type.equals(Sprite.TYPE_STUDENT)) {
            StudentSprite birdSprite = new StudentSprite(ctx);
            birdSprite.curX = spriteMsg.location_x;
            birdSprite.curY = spriteMsg.location_y;

            birdSprite.setUp();
            birdSprite.setDisplayParam(width, height);
            birdSprite.rotate(spriteMsg.rotation_degree);
            hashMap.put(temp, birdSprite);
        } else if (spriteMsg.type.equals(Sprite.TYPE_SMILE)) {
            SmileSprite birdSprite = new SmileSprite(ctx);
            birdSprite.curX = spriteMsg.location_x;
            birdSprite.curY = spriteMsg.location_y;

            birdSprite.setUp();
            birdSprite.setDisplayParam(width, height);
            birdSprite.rotate(spriteMsg.rotation_degree);
            hashMap.put(temp, birdSprite);
        }
//        LogUtils.d(TAG,"key=="+temp);
        return temp;
    }

    public void removeSprite(String key) {
        if (!TextUtils.isEmpty(key)) {
            hashMap.remove(key);
        } else {
            LogUtil.d(TAG, "removeSprite error key is null");
        }
    }

    public void resume() {
        Iterator<String> it = hashMap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            Sprite sprite = hashMap.get(key);
            if (sprite != null) {
                sprite.setUp();
//                LogUtils.d(TAG,"drawEach+"+hashMap.size());
            }
        }
    }



    public void updateSprite(String key, SpriteMsg spriteMsg) {
        if (hashMap == null) {
            LogUtil.d(TAG, "updateSprite hashMap is null ");
            return;
        }
        if (TextUtils.isEmpty(key)) {
            LogUtil.d(TAG, "updateSprite key is null ");
            return;
        }
        Sprite sprite = hashMap.get(key);
        if (sprite != null) {
            if (spriteMsg.openFaceOffset) {
                if (spriteMsg.sprite_height > 0 && spriteMsg.sprite_width > 0) {
                    int area=sprite.spriteHeight*sprite.spriteWidth;
                    double fit;

                    if(area!=0){
                         fit=Math.sqrt(((double)(spriteMsg.sprite_height*spriteMsg.sprite_width))/(double)(area));
//                        LogUtils.e("smile","fit="+fit);
                    }else{
                        fit=1;
                    }
                    int tempHeight=(int) ( sprite.spriteHeight *fit * scale*scaleForParent);
                    int tempWidth=(int) ( sprite.spriteWidth*fit * scale*scaleForParent);
                    int temp_area=tempHeight*tempWidth;
                    int old_area= sprite.spriteHeight*sprite.spriteWidth;
                    LogUtil.d(TAG,"area_distance="+Math.abs(temp_area-old_area));
                    if(Math.abs(temp_area-old_area)>dis_area){
                        sprite.spriteHeight = tempHeight;
                        sprite.spriteWidth= tempWidth;
                    }


                }
                int center_disx = (int) ((sprite.spriteWidth - spriteMsg.sprite_height*scaleForParent) / 2);
                int center_disy = (int) ((sprite.spriteHeight - spriteMsg.sprite_width*scaleForParent) / 2);
                int  temp_x=(int) (spriteMsg.location_x *scaleForParent- center_disx);
                int temp_y=(int) (spriteMsg.location_y*scaleForParent - center_disy);
//                LogUtils.e("smile","center_disx="+center_disx+",center_disy="+center_disy);

                if(Math.abs(sprite.curX-temp_x)>dis_slot){
                    sprite.curX = temp_x;
                }
                if( Math.abs(sprite.curY-temp_y)>dis_slot){
                    sprite.curY = temp_y;
                }


            } else {
                sprite.spriteHeight = (int) (spriteMsg.sprite_height*scaleForParent);
                sprite.spriteWidth = (int) (spriteMsg.sprite_width*scaleForParent);
                sprite.curX = (int) (spriteMsg.location_x*scaleForParent);
                sprite.curY = (int) (spriteMsg.location_y*scaleForParent);
            }


        } else {
            LogUtil.d(TAG, "updateSprite error sprite not find ");
        }
    }

    public void drawEach() {
        Iterator<String> it = hashMap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            Sprite sprite = hashMap.get(key);
            if (sprite != null) {
                sprite.draw();
//                LogUtils.d(TAG,"drawEach+"+hashMap.size());
            }
        }
    }

    public void clearAll() {
        hashMap.clear();
    }

    static class InnerLoader {
        static SpriteManager spriteManager = new SpriteManager();
    }
}
