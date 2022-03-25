
## Requirements

You need to have a Rio projectId.

## Installation

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

Latest Version: [![](https://jitpack.io/v/rettersoft/rio-android-sdk.svg)](https://jitpack.io/#rettersoft/rio-android-sdk)

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

## Initialize SDK

Initialize the SDK with your project id created in Rio console.

```kt
rio = Rio(
    applicationContext = applicationContext,
    projectId = "<ProjectId>",
    culture= "en",
    config = RioNetworkConfig.build {
        region = RioRegion.EU_WEST_1
        sslPinningEnabled = false // default: true
    }
)
```

## Authenticate

Rio client's authenticateWithCustomToken method should be used to authenticate a user. If you don't call this method, client will send actions as an anonymous user.

```kt
rio.authenticateWithCustomToken("<CUSTOM_TOKEN>") { isSuccess, throwable ->
    if (isSuccess) {
        // do success
    } else {
        // use throwable
    }
}
```

You can sign out with .signout method.

```kt
rio.signOut() { isSuccess, throwable ->
    if (isSuccess) {
        // do success
    } else {
        // use throwable
    }
}
```

You can also receive auth status changes.

```kt
rio.setOnClientAuthStatusChangeListener { rbsClientAuthStatus, rbsUser ->
    //
}
```

## Get a cloud object

```kt
rio.getCloudObject(RioCloudObjectOptions(classId = "<ClassId>"), onSuccess = { cloudObj ->
    // cloudObj.call()
    // cloudObj.instanceId()
    // etc..
}, onError = { throwable ->
})
```

## Call a method on a cloud object

```kt
cloudObj.call<ParserClazz>(RioCallMethodOptions(
    method = "<method>",
    body = input,
), onSuccess = {
    onSuccess?.invoke(it.body)
}, onError = {
    onError?.invoke(it)
})
```

## Listen to realtime updates on cloud objects

```kt
cloudObj.public.subscribe( eventFired = { event ->
    // 
}, errorFired = { throwable ->
    // 
})
```
