# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in F:\sdk/tools/proguard/proguard-android.txt
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
-dontwarn
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-keep public class com.onecm.power.R$*{
public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class **.R$* {*;}
-keep class **.R{*;}
-dontwarn **.R$*
-keep class com.nostra13.universalimageloader.** { *; }
-keepclassmembers class com.nostra13.universalimageloader.** {*;}
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-dontwarn com.google.gson.examples.android.model.*
-keep class com.google.gson.examples.android.model.** { *; }
-dontwarn com.gc.materialdesign.*
-keep class com.gc.materialdesign.** {*;}
-dontwarn com.mikepenz.materialdrawer.app.*
-keep class com.mikepenz.materialdrawer.app.** {*;}
-dontwarn com.github.ksoichiro.android.observablescrollview.*
-keep class com.github.ksoichiro.android.observablescrollview.** {*;}
-dontwarn in.srain.cube.views.ptr.*
-keep class in.srain.cube.views.ptr.** {*;}
-libraryjars libs/nineoldandroids-2.4.0.jar
-dontwarn com.nineoldandroids.*
-keep class com.nineoldandroids.** { *;}


-ignorewarnings
# 这里根据具体的SDK版本修改
-libraryjars libs/BmobPush_V0.6_20150403beta.jar
#-libraryjars libs/BmobSDK_V3.3.4_0310.jar
-keepattributes Signature
-keep class cn.bmob.v3.** {*;}

# 保证继承自BmobObject、BmobUser类的JavaBean不被混淆
-keep class com.onecm.bean.Discover{*;}

#sharedsdk
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-dontwarn cn.sharesdk.**
-keep class m.framework.**{*;}

