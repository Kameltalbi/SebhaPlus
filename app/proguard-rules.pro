# Sebha — keep Compose / DataStore safe for release builds.
-keepclassmembers class * extends android.view.View {
    void set*(***);
    *** get*();
}
