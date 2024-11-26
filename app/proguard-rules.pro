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
-keepattributes *Annotation*

-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>

# R8 full mode strips generic signatures from return types if not kept.
-keep,allowobfuscation,allowshrinking class retrofit2.Response

-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

# If a class is used in some way by the application, and has fields annotated with @SerializedName
# and a no-args constructor, keep those fields and the constructor
-if class *
-keepclasseswithmembers,allowobfuscation,allowoptimization class <1> {
  <init>();
  @com.google.gson.annotations.SerializedName <fields>;
}

-keep class com.test.core.domain.models.UserData { *; }