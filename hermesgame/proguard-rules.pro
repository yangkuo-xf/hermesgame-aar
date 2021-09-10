# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


-keeppackagenames com.hermesgamesdk.*
-dontpreverify


-keep public class * extends android.app.Activity


-keepclassmembers class * extends java.lang.Enum {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}


-keep class com.hermesgamesdk.QGManager {
    <fields>;
    <methods>;
}

-keep class com.hermesgamesdk.manager.SliderBarV2Manager$hermesgameJsInterface {
    <methods>;
}
-keep class com.hermesgamesdk.callback.QGCallBack {
    <fields>;
    <methods>;
}


-keep class com.hermesgamesdk.entity.InitData {*;}
-keep class com.hermesgamesdk.entity.InitData$Productconfig {*;}
-keep class com.hermesgamesdk.entity.InitData$Version {*;}
-keep class com.hermesgamesdk.entity.InitData$Paytypes {*;}
-keep class com.hermesgamesdk.entity.InitData$Paytypes$Rebate {*;}
-keep class com.hermesgamesdk.entity.InitData$Paytypes$Rebate$RateConfig {*;}




-keep class com.hermesgamesdk.entity.QGUserInfo {*;}

-keep class com.hermesgamesdk.entity.QGUserInfo$Userdata {*;}

-keep class com.hermesgamesdk.entity.QGUserInfo$ExtInfo {*;}

-keep class com.hermesgamesdk.entity.QGUserExtraInfo {*;}





-keep class com.hermesgamesdk.entity.QGRoleInfo {
    *** get*(...);
    *** set*(...);
}

-keep class com.hermesgamesdk.entity.QGOrderInfo {
    *** get*(...);
    *** set*(...);
}

-keep class com.hermesgamesdk.view.QGEditText {
    <fields>;
    <methods>;
}

-keep class com.hermesgamesdk.view.QGTitleBar {
    <fields>;
    <methods>;
}
-keep class com.hermesgamesdk.view.QGBar {
    <fields>;
    <methods>;
}
-keep class com.hermesgamesdk.QGConfig {
    <fields>;
    <methods>;
}

-keep class com.hermesgamesdk.view.QGPayListAdapter {
    <fields>;
    <methods>;
}

-keep class com.hermesgamesdk.utils.MiitHelper {
    <fields>;
    <methods>;
}
