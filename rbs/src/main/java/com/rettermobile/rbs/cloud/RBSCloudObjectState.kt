package com.rettermobile.rbs.cloud

import com.google.firebase.firestore.ListenerRegistration
import com.rettermobile.rbs.RBSConfig
import com.rettermobile.rbs.RBSFirebaseManager
import com.rettermobile.rbs.exception.NoCloudSnapFoundException
import com.rettermobile.rbs.util.TokenManager

/**
 * Created by semihozkoroglu on 13.12.2021.
 */
sealed class RBSCloudObjectState constructor(val classId: String, val instanceId: String) {

    private var listener: ListenerRegistration? = null

    private var successEvent: ((String?) -> Unit)? = null
    private var errorEvent: ((Throwable?) -> Unit)? = null

    init {
        var path = "/projects/${RBSConfig.projectId}/classes/$classId/instances/$instanceId/"

        if (this is RBSCloudUserObjectState) {
            path += "userState/${TokenManager.userId}"
        } else if (this is RBSCloudRoleObjectState) {
            path += "roleState/${TokenManager.userIdentity}"
        }

        val document = RBSFirebaseManager.getDocument(path)
        listener = document.addSnapshotListener { value, error ->
            if (error != null) {
                errorEvent?.invoke(error)
            } else {
                if (value?.data == null) {
                    errorEvent?.invoke(NoCloudSnapFoundException())
                } else {
                    successEvent?.invoke(value.data?.toString())
                }
            }
        }
    }

    fun subscribe(
        eventFired: ((String?) -> Unit)? = null,
        errorFired: ((Throwable?) -> Unit)? = null
    ) {
        successEvent = eventFired
        errorEvent = errorFired
    }

    fun removeListener() {
        listener?.remove()
    }
}

class RBSCloudUserObjectState constructor(c: String, i: String) : RBSCloudObjectState(c, i)

class RBSCloudRoleObjectState constructor(c: String, i: String) : RBSCloudObjectState(c, i)

class RBSCloudPublicObjectState constructor(c: String, i: String) : RBSCloudObjectState(c, i)