package com.example.yandexclussterization.glide;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

import java.nio.IntBuffer;

@GlideModule
public class ApplicationGlideModule extends AppGlideModule {

    @Override
    public void registerComponents(final Context context, final Glide glide, final Registry registry) {
        registry
                .append(Registry.BUCKET_BITMAP, IntBuffer.class, Bitmap.class, new IntBufferBitmapDecoder(glide.getBitmapPool()));
    }

}
