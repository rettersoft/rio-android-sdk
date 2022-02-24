package com.rettermobile.rio.cloud

import com.google.firebase.firestore.ListenerRegistration
import com.rettermobile.rio.RioConfig
import com.rettermobile.rio.RioFirebaseManager
import com.rettermobile.rio.util.TokenManager

/**
 * Created by semihozkoroglu on 13.12.2021.
 */
sealed class RioCloudObjectState constructor(params: RioCloudObjectParams) {

    val classId = params.classId
    val instanceId = params.instanceId

    private var listener: ListenerRegistration? = null

    private var successEvent: ((String?) -> Unit)? = null
    private var errorEvent: ((Throwable?) -> Unit)? = null

    init {
        var path = "/projects/${RioConfig.projectId}/classes/$classId/instances/$instanceId/"

        if (this is RioCloudUserObjectState) {
            path += "userState/${TokenManager.userId}"
        } else if (this is RioCloudRoleObjectState) {
            path += "roleState/${TokenManager.userIdentity}"
        }

        listener?.remove()

        val document = RioFirebaseManager.getDocument(path)
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

class RioCloudUserObjectState constructor(p: RioCloudObjectParams) : RioCloudObjectState(p)

class RioCloudRoleObjectState constructor(p: RioCloudObjectParams) : RioCloudObjectState(p)

class RioCloudPublicObjectState constructor(p: RioCloudObjectParams) : RioCloudObjectState(p)