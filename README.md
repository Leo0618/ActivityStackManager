# ActivityStackManager
activity stack manager

- 支持监听应用运行位置: 前台运行、后台运行
- 支持管理Activity
	- 当前APP是否退出全部的Activity
	- 关闭某个activity
	- 获取当前所在的activity
	- 获取第一个activity,比如主页
	- 移除全部（用于整个应用退出）
	- 移除除第一个MainActivity之外的全部（主要用于类似回到首页的功能）

## Usage

**dependencies中添加：**

    implementation 'vip.okfood:asm:1.0.4'

**Application的onCreate中：**
    
    ActivityStackManager.get().init(this);
    //是否开启日志打印,日志tag为ActivityStackManager
    ActivityStackManager.get().debug(BuildConfig.DEBUG);
    
## API

	get()
	获取单例实例
	
	init(android.app.Application application)
	初始化，在程序入口处调用，Application的onCreate中
	
	debug(boolean debug)
	是否开启日志打印，默认关闭

	addAppRunForebackListener(ActivityStackManager.OnAppRunForebackListener listener)
	添加APP前后台运行监听
	
	removeAppRunForebackListener(ActivityStackManager.OnAppRunForebackListener listener)
	移除APP前后台运行监听

	getCurrentActivity()
	获取当前所在的activity,不能保证百分百该Activity存在，因为用的弱引用

	getFirstActivity()
	获取第一个activity，一般是MainActivity

	finishActivity(java.lang.Class<? extends android.app.Activity> activityCls)
	关闭某个activity

	removeAllActivityExceptFirst()
	移除除第一个MainActivity之外的全部（主要用于类似回到首页的功能）
	
	removeAllActivity()
	移除全部（用于整个应用退出）
	
	isAppExit()
	APP是否退出，没有activity页面存在了

