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

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontoptimize
-dontpreverify
-ignorewarnings

-verbose
-dontpreverify
-repackageclasses ''
-allowaccessmodification
-optimizations !code/simplification/arithmetic

-keepparameternames
-renamesourcefileattribute SourceFile
-keepattributes Signature,Exceptions,*Annotation*,InnerClasses,PermittedSubclasses,EnclosingMethod,Deprecated,SourceFile,LineNumberTable

-dontwarn android.support.**
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-keep class androidx.fragment.** {*;}

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.content.Context {
    public void *(android.view.View);
    public void *(android.view.MenuItem);
}

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

-keepclassmembers,allowoptimization enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

-keepclassmembers,allowoptimization enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}

-dontnote junit.framework.**
-dontnote junit.runner.**
-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**

-dontwarn android.support.**
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**
-keep public class * extends android.view.**
-keep public class * extends com.soya.launcher.view.**

-dontwarn android.**
-keep class android.** { *; }
-keepclassmembers class android.** { *; }

-dontwarn com.soya.launcher.bean.**
-keepclassmembers class com.soya.launcher.bean.** { *; }

-dontwarn com.soya.launcher.http.**
-keepclassmembers class com.soya.launcher.http.** { *; }

#
# ----------------------------- 第三方 -----------------------------
#

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder


-dontwarn com.orhanobut.logger.**
-keep class com.orhanobut.logger.**{*;}
-keep interface com.orhanobut.logger.**{*;}

-dontwarn com.google.gson.**
-keep class com.google.gson.**{*;}
-keep interface com.google.gson.**{*;}

-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp.* { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

# Retrofit2
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions


# okhttp 3
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# Okio
-dontwarn com.squareup.**
-dontwarn okio.**
-keep public class org.codehaus.* { *; }
-keep public class java.nio.* { *; }
#----------okhttp end--------------

# The following are referenced but aren't required to run
-dontwarn com.fasterxml.jackson.**
-dontwarn org.apache.commons.logging.**
# Android 6.0 release removes support for the Apache HTTP client
-dontwarn org.apache.http.**

#---------- com.hs ---------------
-keep class com.hs.** {*;}


#------------- Aria --------------------
-dontwarn com.arialyy.aria.**
-keep class com.arialyy.aria.**{*;}
-keep class **$$DownloadListenerProxy{ *; }
-keep class **$$UploadListenerProxy{ *; }
-keep class **$$DownloadGroupListenerProxy{ *; }
-keep class **$$DGSubListenerProxy{ *; }
-keepclasseswithmembernames class * {
    @Download.* <methods>;
    @Upload.* <methods>;
    @DownloadGroup.* <methods>;
}




# Xpopup 混淆配置
-dontwarn com.lxj.xpopup.widget.**
-keep class com.lxj.xpopup.widget.**{*;}

# kotlinx.serialization混淆配置
# Keep `Companion` object fields of serializable classes.
# This avoids serializer lookup through `getDeclaredClasses` as done for named companion objects.
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}

# Keep `serializer()` on companion objects (both default and named) of serializable classes.
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep `INSTANCE.serializer()` of serializablLe objects.
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}
# @Serializable and @Polymorphic are used at runtime for polymorphic serialization.
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault


# UltimateBarX混淆配置
-keep class com.zackratos.ultimatebarx.ultimatebarx.** { *; }
-keep public class * extends androidx.fragment.app.Fragment { *; }

# 实体类混淆配置
-dontwarn  com.thumbsupec.lib_net.http.entity.**
-keep class com.thumbsupec.lib_net.http.entity.** { *; }
-dontwarn  com.thumbsupec.ispruz.lib_entity.**
-keep class com.thumbsupec.ispruz.lib_entity.** { *; }

-dontwarn  com.thumbsupec.lib_base.entity.**
-keep class com.thumbsupec.lib_base.entity.** { *; }

-dontwarn  com.thumbsupec.lib_base.db.**
-keep class com.thumbsupec.lib_base.db.** { *; }

# LiveEventBus混淆
-dontwarn com.jeremyliao.liveeventbus.**
-keep class com.jeremyliao.liveeventbus.** { *; }
-keep class androidx.lifecycle.** { *; }
-keep class androidx.arch.core.** { *; }

# Viewbinding 如果用的反射就会有混淆问题，需要加入混淆
-keep class * implements androidx.viewbinding.ViewBinding {
    *;
}

# PicSelector
-keep class com.luck.picture.lib.** { *; }

-keep class com.luck.lib.camerax.** { *; }

-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }



# Jpush
 -dontoptimize
    -dontpreverify

    -dontwarn cn.jpush.**
    -keep class cn.jpush.** { *; }

    -dontwarn cn.jiguang.**
    -keep class cn.jiguang.** { *; }


    #Umeng
    -keep class com.umeng.** {*;}

    -keep class org.repackage.** {*;}

    -keepclassmembers class * {
       public <init> (org.json.JSONObject);
    }

    -keepclassmembers enum * {
        public static **[] values();
        public static ** valueOf(java.lang.String);
    }


#unit
-dontwarn  com.thumbsupec.ispruz.lib_unity.**
-keep class com.thumbsupec.ispruz.lib_unity.** { *; }

-keep class com.unity3d.player.** { *; }
-keep class com.google.androidgamesdk.** { *; }
-keep class org.fmod.** { *; }
-keep class bitter.jnibridge.** { *; }
-keep class com.onevcat.uniwebview.* { *; }
-ignorewarnings


# DFU Library
-keep class no.nordicsemi.android.dfu.** { *; }


