-keep class com.squareup.okhttp3.** { *; }
-keep class com.reverie.voiceinput.** { *; }
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

-keepclassmembers class com.reverie.voiceinput.** {
   public *;
}