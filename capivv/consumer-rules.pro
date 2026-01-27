# Capivv SDK ProGuard Rules
# Keep all public API classes
-keep class com.capivv.sdk.** { *; }

# Keep Kotlin serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep Capivv model classes for serialization
-keep,includedescriptorclasses class com.capivv.sdk.models.**$$serializer { *; }
-keepclassmembers class com.capivv.sdk.models.** {
    *** Companion;
}
-keepclasseswithmembers class com.capivv.sdk.models.** {
    kotlinx.serialization.KSerializer serializer(...);
}
