# rbs-android

##### Latest Version: [![](https://jitpack.io/v/rettersoft/rio-android-sdk.svg)](https://jitpack.io/#rettersoft/rio-android-sdk)

##### Add it in your root build.gradle at the end of repositories:

```
allprojects {
  repositories {
  	...
  	maven { url 'https://jitpack.io' }
  }
}
```

##### Add the dependency

```
dependencies {
  implementation 'com.github.rettersoft:rio-android-sdk:{latest-version}'
}
```

##### Proguard config

```
-keep class com.rettermobile** { *; }
-keep class com.rettermobile.* { *; }
```
