package com.rettermobile.rbs.cloud

import com.google.firebase.firestore.ListenerRegistration
import com.rettermobile.rbs.RBSConfig
import com.rettermobile.rbs.RBSFirebaseManager
import com.rettermobile.rbs.util.TokenManager

/**
 * Created by semihozkoroglu on 13.12.2021.
 */
sealed class RBSCloudObjectState constructor(params: RBSCloudObjectParams) {

    val classId = params.classId
    val instanceId = params.instanceId

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
                successEvent?.invoke(value?.data?.toString())
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

class RBSCloudUserObjectState constructor(p: RBSCloudObjectParams) : RBSCloudObjectState(p)

class RBSCloudRoleObjectState constructor(p: RBSCloudObjectParams) : RBSCloudObjectState(p)

class RBSCloudPublicObjectState constructor(p: RBSCloudObjectParams) : RBSCloudObjectState(p)