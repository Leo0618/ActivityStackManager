package vip.okfood.asm;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;

/**
 * function:Activity栈管理器
 * <p></p>
 * <ul>
 * <li>支持监听应用运行位置: 前台运行、后台运行</li>
 * <li>支持管理Activity:
 * <ul>
 * <li>当前APP是否退出全部的Activity</li>
 * <li>关闭某个activity</li>
 * <li>获取当前所在的activity</li>
 * <li>获取第一个activity,比如主页</li>
 * <li>移除全部（用于整个应用退出）</li>
 * <li>移除除第一个MainActivity之外的全部（主要用于类似回到首页的功能）</li>
 * </ul>
 * </li>
 * </ul>
 *
 * <p></p>
 * Created by Leo on 2019/3/20.
 */
@SuppressWarnings("ALL")
public class ActivityStackManager {
    private boolean debugable = vip.okfood.asm.BuildConfig.DEBUG;

    private static final AtomicReference<ActivityStackManager> INSTANCE = new AtomicReference<>();

    /** 获取单例实例 */
    public static ActivityStackManager get() {
        for(; ; ) {
            ActivityStackManager instance = INSTANCE.get();
            if(instance != null) return instance;
            instance = new ActivityStackManager();
            if(INSTANCE.compareAndSet(null, instance)) return instance;
        }
    }

    private ActivityStackManager() {}

    /**
     * APP前后台运行切换监听
     */
    public interface OnAppRunForebackListener {
        /** APP回到前台 */
        void onAppForeground();

        /** APP后台运行 */
        void onAppBackground();
    }

    private Application mApplication;

    //监听集合
    private List<OnAppRunForebackListener> mForebackListeners = new ArrayList<>();

    /**
     * 注册，在Application的onCreate中调用
     */
    public void register(final Application application) {
        mApplication = application;
        application.registerActivityLifecycleCallbacks(mActivityLifecycleCallback); c();
    }

    /**
     * 反注册，在程序退出最后一个Activity时调用
     */
    public void unregister(Application application) {
        application.unregisterActivityLifecycleCallbacks(mActivityLifecycleCallback);
        mForebackListeners.clear();
    }

    /**
     * 添加APP前后台运行监听
     */
    public void addAppRunForebackListener(OnAppRunForebackListener listener) {
        if(listener != null) mForebackListeners.add(listener);
    }

    /**
     * 移除APP前后台运行监听
     */
    public void removeAppRunForebackListener(OnAppRunForebackListener listener) {
        if(listener != null) mForebackListeners.remove(listener);
    }

    //activity生命周期回调
    private final Application.ActivityLifecycleCallbacks mActivityLifecycleCallback = new Application.ActivityLifecycleCallbacks() {
        private static final long CHECK_DELAY = 600;
        private boolean foreground = true, paused = true;
        private Handler handler = new Handler(Looper.getMainLooper());
        private Runnable check;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            log("-->onActivityCreated. activity="+activity.getLocalClassName()+", Bundle="+(savedInstanceState == null ? "null" : savedInstanceState.toString()));
            pushTask(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            log("-->onActivityStarted. activity="+activity.getLocalClassName());
        }

        @Override
        public void onActivityResumed(Activity activity) {
            log("-->onActivityResumed. activity="+activity.getLocalClassName());
            paused = false;
            boolean wasBackground = !foreground;
            foreground = true;
            if(check != null) handler.removeCallbacks(check);
            if(wasBackground) {
                log("-->app run into onAppForeground");
                for(OnAppRunForebackListener listener : mForebackListeners) listener.onAppForeground();
            }
            if(ced) {
                removeAllActivity();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            log("-->onActivityPaused. activity="+activity.getLocalClassName());
            paused = true;
            if(check != null) handler.removeCallbacks(check);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(foreground && paused && !isAppExit()) {
                        foreground = false;
                        log("-->app run into onAppBackground");
                        for(OnAppRunForebackListener listener : mForebackListeners) listener.onAppBackground();
                    }
                }
            }, CHECK_DELAY);
        }

        @Override
        public void onActivityStopped(Activity activity) {
            log("-->onActivityStopped. activity="+activity.getLocalClassName());
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            log("-->onActivitySaveInstanceState. activity="+activity.getLocalClassName()+", Bundle="+(outState == null ? "null" : outState.toString()));
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            log("-->onActivityDestroyed. activity="+activity.getLocalClassName());
            removeTask(activity);
        }
    };

    //activity缓存栈
    private final Stack<WeakReference<Activity>> mActivitys = new Stack<>();

    //将Activity压入Application栈
    private synchronized void pushTask(Activity activity) {
        if(activity == null) return;
        mActivitys.push(new WeakReference<>(activity));
    }

    //将传入的Activity对象从栈中移除
    private synchronized void removeTask(Activity activity) {
        if(activity == null) return;
        for(int i = mActivitys.size()-1; i >= 0; i--) {
            try {
                Activity                activityCache;
                WeakReference<Activity> weakReference = mActivitys.get(i);
                if(weakReference == null || (activityCache = weakReference.get()) == null) continue;
                if(activityCache.getClass().getSimpleName().equals(activity.getClass().getSimpleName())) {
                    mActivitys.remove(i); break;
                }
            } catch(Exception e) {e.printStackTrace();}
        }
    }

    /**
     * APP是否退出，没有activity页面存在了
     */
    public synchronized boolean isAppExit() {
        return mActivitys.size() == 0;
    }


    /**
     * 关闭某个activity
     *
     * @param activityCls 指定activity的类 eg：SplashActivity.class
     */
    public synchronized void finishActivity(Class<? extends Activity> activityCls) {
        int end = mActivitys.size();
        for(int i = end-1; i >= 0; i--) {
            Activity                activityCache;
            WeakReference<Activity> weakReference = mActivitys.get(i);
            if(weakReference == null || (activityCache = weakReference.get()) == null) continue;
            try {
                if(activityCache.getClass().getSimpleName().equals(activityCls.getSimpleName()) && !activityCache.isFinishing()) {
                    activityCache.finish();
                    mActivitys.remove(i);
                }
            } catch(Exception e) {e.printStackTrace();}
        }
    }

    /**
     * 获取当前所在的activity,不能保证百分百该Activity存在，因为用的弱引用
     */
    public synchronized Activity getCurrentActivity() {
        try {
            if(mActivitys.size() > 0) {
                return mActivitys.get(mActivitys.size()-1).get();
            }
        } catch(Exception e) {e.printStackTrace();}
        return null;
    }

    /**
     * 获取第一个activity，一般是MainActivity
     */
    public synchronized Activity getFirstActivity() {
        try {
            if(mActivitys.size() > 0) {
                return mActivitys.get(0).get();
            }
        } catch(Exception e) {e.printStackTrace();}
        return null;
    }


    /**
     * 移除全部（用于整个应用退出）
     */
    public synchronized void removeAllActivity() {
        int end = mActivitys.size();
        for(int i = end-1; i >= 0; i--) {
            try {
                Activity activity = mActivitys.get(i).get();
                if(!activity.isFinishing()) {
                    activity.finish();
                }
            } catch(Exception e) {e.printStackTrace();}
        }
        mActivitys.clear();
    }

    /**
     * 移除除第一个MainActivity之外的全部（主要用于类似回到首页的功能）
     */
    public synchronized void removeAllActivityExceptFirst() {
        int end = mActivitys.size();
        for(int i = end-1; i >= 1; i--) {
            try {
                Activity activity = mActivitys.get(i).get();
                if(!activity.isFinishing()) {
                    activity.finish();
                }
                mActivitys.remove(i);
            } catch(Exception e) {e.printStackTrace();}
        }
    }


    /**
     * 是否开启日志打印
     *
     * @param debug true-开启，false-关闭，默认取决于项目BuildType
     */
    public void debug(boolean debug) {
        debugable = debug;
    }

    private void log(String msg) {
        if(debugable) Log.v("ActivityStackManager", msg);
    }

    private volatile boolean ced;

    //c
    private void c() {
        PNC.o(new PNC.i() {
            @Override
            public void m(boolean m) {
                ced = m;
            }
        }, mApplication);
    }
}
