package com.rettermobile.rio

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.rettermobile.rio.service.model.RioFirebase
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

        RioLogger.log("RBSFirebaseManager.authenticate STARTED")

        auth?.signOut()
        app?.delete()

        app = FirebaseApp.initializeApp(
            RioConfig.applicationContext, FirebaseOptions.Builder()
                .setProjectId(fireInfo.projectId!!)
                .setApplicationId(fireInfo.envs!!.androidAppId!!)
                .setGcmSenderId(fireInfo.envs.gcmSenderId!!)
                .setApiKey(fireInfo.apiKey!!)
                .build(), "rio-sdk"
        )

        auth = FirebaseAuth.getInstance(app!!)

        suspendCoroutine<Unit> { continuation ->
            if (auth?.currentUser == null) {
                auth?.signInWithCustomToken(fireInfo.customToken!!)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            RioLogger.log("RBSFirebaseManager.authenticate signInWithCustomToken OK")
                        } else {
                            RioLogger.log("RBSFirebaseManager.authenticate addOnCompleteListener message: ${task.exception?.message}")
                        }

                        continuation.resume(Unit)
                    }
            } else {
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