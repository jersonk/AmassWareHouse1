package com;

import android.app.Application;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by U11001548 on 2017/8/10.
 */

public class ApApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initImageLoadLib();
    }

    private void initImageLoadLib() {
        //UIL 初始化
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .threadPriority(Thread.NORM_PRIORITY - 1).threadPoolSize(2)
                .memoryCache(new WeakMemoryCache()).memoryCacheSize(100 * 100)
                .build();

        ImageLoader.getInstance().init(config);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        ImageLoader.getInstance().clearMemoryCache();
    }
}
