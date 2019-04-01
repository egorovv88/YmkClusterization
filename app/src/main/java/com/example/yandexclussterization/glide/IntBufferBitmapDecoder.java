package com.example.yandexclussterization.glide;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

import java.io.IOException;
import java.nio.IntBuffer;

public class IntBufferBitmapDecoder implements ResourceDecoder<IntBuffer, Bitmap> {

    private final BitmapPool pool;

    public IntBufferBitmapDecoder(BitmapPool pool) {
        this.pool = pool;
    }

    @Override
    public boolean handles(IntBuffer intBuffer, Options options) throws IOException {
        return true;
    }

    @Override
    public @Nullable
    Resource<Bitmap> decode(IntBuffer intBuffer, int width, int height, Options options) throws IOException {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(intBuffer.array(), 0, width, 0, 0, width, height);

        return BitmapResource.obtain(bitmap, pool);
    }

}
