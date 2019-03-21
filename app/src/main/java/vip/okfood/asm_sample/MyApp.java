package vip.okfood.asm_sample;

import android.app.Application;


import vip.okfood.asm.ActivityStackManager;

/**
 * function:
 *
 * <p></p>
 * Created by Leo on 2019/3/20.
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ActivityStackManager.get().register(this);
        ActivityStackManager.get().debug(BuildConfig.DEBUG);
    }
}
