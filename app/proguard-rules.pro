# Add project specific ProGuard rules here.
-keep public class * extends android.app.Activity
-keep class com.bookmanager.data.entity.** { *; }
-keep class com.bookmanager.data.dao.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**