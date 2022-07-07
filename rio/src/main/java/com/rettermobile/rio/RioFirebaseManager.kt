package com.rettermobile.rio

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.rettermobile.rio.service.model.RioFirebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Created by semihozkoroglu on 10.12.2021.
 */
object RioFirebaseManager {

    private var app: FirebaseApp? = null
    private var auth: FirebaseAuth? = null

    suspend fun authenticate(fireInfo: RioFirebase?) {
        if (!RioConfig.config.firebaseEnable) return

        if (fireInfo == null) return

        RioLogger.log("RIOFirebaseManager.authenticate STARTED")

        auth?.signOut()

        app?.let {
            deleteApp()
            initApp(fireInfo)
        } ?: run { initApp(fireInfo) }
    }

    private suspend fun deleteApp() = suspendCoroutine<Unit> { continuation ->
        RioLogger.log("RIOFirebaseManager.deleteApp called")
        app?.let {
            RioLogger.log("RIOFirebaseManager.deleteApp addLifecycleEventListener added")
            it.addLifecycleEventListener { appName, options ->
                RioLogger.log("RIOFirebaseManager.deleteApp $appName triggered")
                if (appName.equals("rio-sdk")) {
                    RioLogger.log("RIOFirebaseManager.deleteApp continuation.resume")
                    continuation.resume(Unit)
                }
            }
            RioLogger.log("RIOFirebaseManager.deleteApp app.delete() OK")
            it.delete()
        }
    }

    private suspend fun initApp(fireInfo: RioFirebase) {
        RioLogger.log("RIOFirebaseManager.initApp called")
        app = FirebaseApp.initializeApp(
            RioConfig.applicationContext, FirebaseOptions.Builder()
                .setProjectId(fireInfo.projectId!!)
                .setApplicationId(fireInfo.envs!!.androidAppId!!)
                .setGcmSenderId(fireInfo.envs.gcmSenderId!!)
                .setApiKey(fireInfo.apiKey!!)
                .build(), "rio-sdk"
        )

        auth = FirebaseAuth.getInstance(app!!)

        RioLogger.log("RIOFirebaseManager.initApp instance created")

        suspendCoroutine<Unit> { continuation ->
            if (auth?.currentUser == null) {
                RioLogger.log("RIOFirebaseManager.initApp currentUser is null")
                auth?.signInWithCustomToken(fireInfo.customToken!!)
                    ?.addOnCompleteListener { task ->
                        RioLogger.log("RIOFirebaseManager.initApp addOnCompleteListener isSuccessful: ${task.isSuccessful}")
                        if (task.isSuccessful) {
                            RioLogger.log("RIOFirebaseManager.authenticate signInWithCustomToken OK")
                        } else {
                            RioLogger.log("RIOFirebaseManager.authenticate addOnCompleteListener message: ${task.exception?.message}")
                        }

                        continuation.resume(Unit)
                    }
            } else {
                RioLogger.log("RIOFirebaseManager.initApp currentUser not null")
                continuation.resume(Unit)
            }
        }
    }

    fun signOut() {
        auth?.signOut()
        auth = null
    }

    fun getDocument(path: String): DocumentReference {
        val store = FirebaseFirestore.getInstance(app!!)

        return store.document(path)
    }

    fun isNotAuthenticated(): Boolean {
        return app == null || auth == null
    }
}