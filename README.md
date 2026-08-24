# Rio Android SDK

Rio Android SDK helps Android applications connect to Rio Cloud services.
This document explains how to integrate the SDK and use its core features.

## Quick Start

Use this section if you want to get up and running quickly.

### 1. Add dependency

```gradle
dependencies {
  implementation 'com.github.rettersoft:rio-android-sdk:{latest-version}'
}
```

### 2. Initialize once in `Application`

```kotlin
import android.app.Application
import com.rettermobile.rio.Rio
import com.rettermobile.rio.service.RioNetworkConfig
import com.rettermobile.rio.util.RioRegion

class App : Application() {

    lateinit var rio: Rio

    override fun onCreate() {
        super.onCreate()

        rio = Rio(
            applicationContext = applicationContext,
            projectId = "<YOUR_PROJECT_ID>",
            culture = "en-us",
            config = RioNetworkConfig.build {
                region = RioRegion.EU_WEST_1
                sslPinningEnabled = true
            }
        )
    }
}
```

### 3. Authenticate and call a cloud method

```kotlin
rio.authenticateWithCustomToken("<CUSTOM_TOKEN>") { isSuccess, throwable ->
    if (!isSuccess) return@authenticateWithCustomToken

    rio.makeStaticCall<MyResponse>(
        options = RioCloudObjectOptions(
            classId = "<CLASS_ID>",
            method = "<METHOD_NAME>",
            body = MyRequest()
        ),
        onSuccess = { response ->
            val data = response.body
        },
        onError = { error ->
            // handle error
        }
    )
}
```

## Requirements

- Android `minSdk 23+`
- Kotlin-based Android project
- A valid Rio `projectId` from Rio Console

## Installation

### 1. Add JitPack repository

Add JitPack to your `settings.gradle` or root `build.gradle` repositories.

```gradle
allprojects {
  repositories {
    google()
    mavenCentral()
    maven { url 'https://jitpack.io' }
  }
}
```

### 2. Add SDK dependency

```gradle
dependencies {
  implementation 'com.github.rettersoft:rio-android-sdk:{latest-version}'
}
```

Latest release: [JitPack - rio-android-sdk](https://jitpack.io/#rettersoft/rio-android-sdk)

### 3. (Optional) Proguard rules

```proguard
-keep class com.rettermobile** { *; }
-keep class com.rettermobile.* { *; }
```

## SDK Initialization

Create the SDK once when your app starts (usually in `Application`).

```kotlin
import android.app.Application
import com.google.gson.GsonBuilder
import com.rettermobile.rio.Rio
import com.rettermobile.rio.service.RioNetworkConfig
import com.rettermobile.rio.util.RioRegion

class App : Application() {

    lateinit var rio: Rio

    override fun onCreate() {
        super.onCreate()

        rio = Rio(
            applicationContext = applicationContext,
            projectId = "<YOUR_PROJECT_ID>",
            culture = "en-us",
            config = RioNetworkConfig.build {
                // Region-based setup
                region = RioRegion.EU_WEST_1

                // Alternative: customDomain = "api.example.com"

                sslPinningEnabled = true
                gson = GsonBuilder().create()
            }
        )
    }
}
```

Notes:
- You must provide either `region` or `customDomain`.
- `customDomain` must not include `http://` or `https://`.

## Network and Security Configuration

Common `RioNetworkConfig` fields:

- `sslPinningEnabled`: Enables/disables public key pinning. Default is `true`.
- `sslPins`: Provide your own host+pin (`sha256/...`) pairs instead of default pins.
- `pinnedPemCertificate`: Optional PEM content string. If provided, interceptor-level certificate matching is also enforced.
- `interceptor`: Add an application interceptor.
- `networkInterceptor`: Add a network interceptor.
- `headerInterceptor`: Add custom headers to every request.
- `connectionSpec`: Customize TLS connection specs.
- `logLevel`: OkHttp logging level.

## Certificate Pinning

The SDK supports public key pinning by default. You can additionally enforce PEM certificate pinning.

Rules:
- If `pinnedPemCertificate` is null/empty, PEM validation is skipped.
- If `pinnedPemCertificate` is provided, SDK checks whether any certificate in TLS peer chain matches your PEM certificate.
- If there is no match, request fails with `SSLPeerUnverifiedException`.

### Provide PEM content from app (example)

```kotlin
val pemContent = applicationContext.assets
    .open("server_cert.pem")
    .bufferedReader()
    .use { it.readText() }

val config = RioNetworkConfig.build {
    region = RioRegion.EU_WEST_1
    sslPinningEnabled = true
    pinnedPemCertificate = pemContent
}
```

## Authentication

### Sign in with custom token

```kotlin
rio.authenticateWithCustomToken("<CUSTOM_TOKEN>") { isSuccess, throwable ->
    if (isSuccess) {
        // signed in
    } else {
        // handle error
    }
}
```

### Sign out

```kotlin
rio.signOut { isSuccess, throwable ->
    if (isSuccess) {
        // signed out
    } else {
        // handle error
    }
}
```

### Listen auth status

```kotlin
rio.setOnClientAuthStatusChangeListener { status, user ->
    // status: SIGNED_IN, SIGNED_OUT, AUTHENTICATING
}
```

## Cloud Object Usage

### 1. Get cloud object

```kotlin
import com.rettermobile.rio.cloud.RioCloudObjectOptions

rio.getCloudObject(
    RioCloudObjectOptions(
        classId = "<CLASS_ID>",
        instanceId = "<INSTANCE_ID>" // or key = "field" to "value"
    ),
    onSuccess = { cloudObject ->
        // cloudObject.call(...)
    },
    onError = { throwable ->
        // handle error
    }
)
```

`classId` is required for the remote path. Calling `getCloudObject` without one
fails with `ClassIdRequiredException` instead of sending a malformed request.
A local object (see `useLocal` below) is still constructed without a `classId`,
but it is logged as a warning, because `call(...)` and state subscriptions
cannot work without one.

#### Skipping the network round trip: `useLocal`

By default `getCloudObject` performs an INSTANCE request to resolve the instance
remotely. When you already know the `instanceId`, set `useLocal = true` to build
the object handle in memory instead, with no network call:

```kotlin
rio.getCloudObject(
    RioCloudObjectOptions(
        classId = "<CLASS_ID>",
        instanceId = "<INSTANCE_ID>",
        useLocal = true
    ),
    onSuccess = { cloudObject ->
        cloudObject.isLocal // true - no INSTANCE request was sent
    }
)
```

Notes:

- `useLocal = true` requires `instanceId`. A `key` cannot be resolved without
  contacting the server, so `useLocal = true` together with `key` still performs
  the remote request. Whenever `useLocal` cannot be satisfied the SDK logs a
  warning and falls back to the remote path; the returned object's `isLocal` is
  then `false`.
- `RioCloudObject.isLocal` tells you which branch produced the object. It is
  read-only for callers.
- `useLocal` is only honoured by `getCloudObject`. It has no effect on
  `makeStaticCall` or `cloudObject.call(...)`, which always go to the network.

### 2. Call cloud object method

```kotlin
import com.rettermobile.rio.cloud.RioCallMethodOptions

cloudObject.call<MyResponse>(
    RioCallMethodOptions(
        method = "<METHOD_NAME>",
        body = MyRequest(),
    ),
    onSuccess = { response ->
        val body = response.body
    },
    onError = { throwable ->
        // handle error
    }
)
```

### 3. Static call (without fetching instance)

```kotlin
rio.makeStaticCall<MyResponse>(
    options = RioCloudObjectOptions(
        classId = "<CLASS_ID>",
        method = "<METHOD_NAME>",
        body = MyRequest()
    ),
    onSuccess = { response ->
        // success
    },
    onError = { throwable ->
        // handle error
    }
)
```

### 4. List instances

```kotlin
cloudObject.listInstances(
    onSuccess = { listResponse ->
        // list data
    },
    onError = { throwable ->
        // handle error
    }
)
```

## Realtime State Subscribe

You can subscribe to `user`, `role`, or `public` state channels from a cloud object.

```kotlin
cloudObject.public.subscribe(
    eventFired = { json ->
        // latest state json
    },
    errorFired = { throwable ->
        // handle error
    }
)

// Clear listeners when no longer needed
cloudObject.unsubscribeStates()
```

## Retry Configuration

You can set global retry config while creating SDK:

```kotlin
import com.rettermobile.rio.service.RioRetryConfig

val retryConfig = RioRetryConfig(
    delay = 50,
    count = 3,
    rate = 1.5
)

val rio = Rio(
    applicationContext = applicationContext,
    projectId = "<YOUR_PROJECT_ID>",
    config = RioNetworkConfig.build { region = RioRegion.EU_WEST_1 },
    retryConfig = retryConfig
)
```

You can also override retry per call via `RioCallMethodOptions.retry`.

## Error Handling

Error callback may return `RioErrorResponse`:

```kotlin
import com.rettermobile.rio.cloud.RioErrorResponse

val onError: (Throwable?) -> Unit = { throwable ->
    if (throwable is RioErrorResponse) {
        val code = throwable.code
        val raw = throwable.rawBody
        val parsed = throwable.body<MyErrorModel>()
    }
}
```

## Logging

```kotlin
rio.logEnable(true)
rio.setLoggerListener { message ->
    // forward logs to your logging system
}
```

## Integration Recommendations

- Keep a single `Rio` instance (singleton-style).
- Always handle error callbacks for network/auth flows.
- Clean up realtime listeners when screen/lifecycle ends (`unsubscribeStates`).
- For pinning-enabled production setups, define a certificate rotation strategy.
