# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/liuzhaoxiang/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#对于一些基本指令的添加 代码混淆压缩比，在0~7之间，默认为5，一般不做修改
-optimizationpasses 5
# 混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames
# 指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses
# 这句话能够使我们的项目混淆后产生映射文件# 包含有类名->混淆后类名的映射关系
-verbose
# 指定不去忽略非公共库的类成员
-dontskipnonpubliclibraryclassmembers
# 不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
-dontpreverify
# 保留注解与内部类
-keepattributes *Annotation*, InnerClasses
# 避免混淆泛型 如果混淆报错建议关掉 //TODO
-keepattributes Signature
# 避免混淆反射 如果混淆报错建议关掉 //TODO
-keepattributes EnclosingMethod
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
# 指定混淆是采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不做更改
-optimizations !code/simplification/cast,!field/*,!class/merging/*
#忽略警告
-ignorewarning

#Android开发中一些需要保留的公共部分,保留我们使用的四大组件，自定义的Application等等这些类不被混淆# 因为这些子类都有可能被外部调用
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService

# 出现如下错误使用下面语句
#java.lang.NoSuchMethodError
# No static method newPullParser()Lorg/xmlpull/v1/a; in class Landroid/util/Xml; or its super classes (declaration of ‘android.util.Xml’ appears in /system/framework/framework.jar)
# com.ta.utdid2.c.a.e.void a(java.util.Map,java.io.OutputStream)(Unknown Source)
-dontwarn org.xmlpull.v1.XmlPullParser
-dontwarn org.xmlpull.v1.XmlSerializer
-keep class org.xmlpull.v1.* {*;}

# 保留support下的所有类及其内部类
-keep class android.support.** {*;}

#如果引用了v4或者v7包
-dontwarn android.support.**
# 保留继承的
-keep public class * extends android.support.v4.**
#如果有引用v4包可以添加下面这行
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**

#如果有使用keep注解可以加入下面的话
#-keep class android.support.annotation.Keep
#-keep @android.support.annotation.Keep class * {*;}
#-keepclasseswithmembers class * {
#    @android.support.annotation.Keep <methods>;
#}
#-keepclasseswithmembers class * {
#    @android.support.annotation.Keep <fields>;
#}
#-keepclasseswithmembers class * {
#    @android.support.annotation.Keep <init>(...);
#}

# 保留R下面的资源
-keep class **.R$* {*;}

# 保留本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
# 保留在Activity中的方法参数是view的方法，
# 这样以来我们在layout中写的onClick就不会被影响-keepclassmembers class * extends android.app.Activity{    public void *(android.view.View);}
#保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# 保留我们自定义控件（继承自View）不被混淆
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保留Parcelable序列化类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable
# 保留Serializable序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}

# webView处理
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
    public *;
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.webView, jav.lang.String);
}

#JS支持
-keepattributes SetJavaScriptEnabled
-keepattributes JavascriptInterface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

#保持枚举 enum 类不被混淆
-keepclassmembers enum * {
  public static **[] values();
  public static ** valueOf(java.lang.String);
}

#保持点击事件
-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
}

#不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}

# 移除Log类打印各个等级日志的代码，打正式包的时候可以做为禁log使用，这里可以作为禁止log打印的功能使用
# 记得proguard-android.txt中一定不要加-dontoptimize才起作用
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

#如果有JNI机制,需要加上下面这句
#-keep class com.your.jnicallback.class { *; }

#使用第三方jar包注意 studio在引用了libs下的jar包时已经自动加入了打包脚本，不需要再次添加，如下语句可以不需要
#-libraryjars libs/......

# Gson specific classes
# Application classes that will be serialized/deserialized over Gson
# gson
-keep class sun.misc.Unsafe { *; }
-keep class com.idea.fifaalarmclock.entity.***
-keep class org.json.* {*;}
-dontwarn com.google.gson.**
-keep class com.google.gson.** { *; }
# 如果使用了Gson之类的工具要使被它解析的JavaBean类即实体类不被混淆。
-keep class com.google.gson.examples.android.model.* {*;}
-keep class com.matrix.app.entity.json.** { *; }
-keep class com.matrix.appsdk.network.model.** { *; }

#友盟相关混淆
-keep class com.umeng.commonsdk.** {*;}
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keep public class com.rongyu.enterprisehouse100.R$*{
    public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclasseswithmembers class * {
    native <methods>;
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#腾讯地图定位
-keepclassmembers class ** {
    public void on*Event(...);
}
-keep class c.t.**{*;}
-keep class com.tencent.map.geolocation.**{*;}
-keep class com.tencent.tencentmap.lbssdk.service.**{*;}
-dontwarn  org.eclipse.jdt.annotation.**
-dontwarn  c.t.**

#腾讯地图 3D sdk
-keep class com.tencent.tencentmap.**{*;}
-keep class com.tencent.map.**{*;}
-keep class com.tencent.beacontmap.**{*;}
-keep class navsns.cnst.**{*;}
-dontwarn com.qq.**
-dontwarn com.tencent.beacon.**

#腾讯地图检索sdk
-keep class com.tencent.lbssearch.**{*;}
-keep class com.google.gson.examples.android.model.** { *; }

#bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

#微信支付
-keep class com.tencent.** { *;}
#-keep class com.tencent.mm.opensdk.** { *;}
#-keep class com.tencent.wxop.** { *;}
#-keep class com.tencent.mm.sdk.** { *;}
#如果项目中引用了dom4j.jar
#-keep class org.dom4j.** {*;}

#支付宝相关混淆
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
#-dontwarn android.net.**
#-keep class android.net.SSLCertificateSocketFactory{*;}
#-keep class org.apache.http.{*;}

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}
#okio
-dontwarn okio.**
-keep class okio.**{*;}
#rx2.*
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Exceptions

#netease dun
-keep public class com.netease.nis.captcha.**{*;}
-keep public class android.webkit.**

#sqlcipher
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }