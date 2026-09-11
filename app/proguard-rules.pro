# Hilt
-keep public class * extends android.app.Service
-keep public class * extends android.app.Application
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Agora SDK
-keep class io.agora.** { *; }
-dontwarn io.agora.**

# SQLCipher
-keep class net.zetetic.database.** { *; }
-dontwarn net.zetetic.database.**

# Cloudinary
-keep class com.cloudinary.** { *; }
-dontwarn com.cloudinary.**

# Retrofit / OkHttp
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-keep class com.google.gson.** { *; }
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
-keepclassmembers class * {
    @retrofit2.http.* <methods>;
}

# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontwarn kotlinx.serialization.**
-keepclassmembers class com.example.model.** {
    *** Companion;
    *** $serializer;
}

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
