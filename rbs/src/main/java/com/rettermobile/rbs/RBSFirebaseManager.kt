package com.rettermobile.rbs

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.rettermobile.rbs.service.model.RBSFirebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Created by semihozkoroglu on 10.12.2021.
 */
object RBSFirebaseManager {

    private var app: FirebaseApp? = null
    private var auth: FirebaseAuth? = null

    suspend fun authenticate(fireInfo: RBSFirebase?) {
        if (fireInfo == null) return

        RBSLogger.log("RBSFirebaseManager.authenticate STARTED")

        app?.delete()

        app = FirebaseApp.initializeApp(
            RBSConfig.applicationContext, FirebaseOptions.Builder()
                .setProjectId(fireInfo.projectId!!)
                .setApplicationId(fireInfo.envs!!.androidAppId!!)
                .setGcmSenderId(fireInfo.envs.gcmSenderId!!)
                .setApiKey(fireInfo.apiKey!!)
                .build(), "rbs-sdk"
        )

        auth = FirebaseAuth.getInstance(app!!)

        suspendCoroutine<Unit> { continuation ->
            if (auth?.currentUser == null) {
                auth?.signInWithCustomToken(fireInfo.customToken!!)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            RBSLogger.log("RBSFirebaseManager.authenticate signInWithCustomToken OK")
                        } else {
                            RBSLogger.log("RBSFirebaseManager.authenticate addOnCompleteListener message: ${task.exception?.message}")
                        }

                        continuation.resume(Unit)
                    }
            } else {
                continuation.resume(Unit)
            }
        }
    }

    fun signOut() {
    }

    fun getDocument(path: String): DocumentReference {
        val store = FirebaseFirestore.getInstance(app!!)

        return store.document(path)
    }

    fun isNotAuthenticated(): Boolean {
        return app == null || auth == null
    }
}