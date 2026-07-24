# ReleaseCanvas release rules (R8)

# Keep Compose / coroutines defaults from consumer rules in dependencies.

# Play Services Location
-keep class com.google.android.gms.location.** { *; }

# Models used via reflection-free code paths; keep Parcelables if added later.
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
