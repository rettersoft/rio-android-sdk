package com.rettermobile.rbs.cloud

/**
 * Created by semihozkoroglu on 13.12.2021.
 */
sealed class RBSCloudObjectState constructor(params: RBSCloudObjectParams) {

    private var successEvent: ((String?) -> Unit)? = null
    private var errorEvent: ((Throwable?) -> Unit)? = null

    fun subscribe(
        eventFired: ((String?) -> Unit)? = null,
        errorFired: ((Throwable?) -> Unit)? = null
    ) {
        successEvent = eventFired
        errorEvent = errorFired
    }
}

class RBSCloudUserObjectState constructor(p: RBSCloudObjectParams) : RBSCloudObjectState(p)

class RBSCloudRoleObjectState constructor(p: RBSCloudObjectParams) : RBSCloudObjectState(p)

class RBSCloudPublicObjectState constructor(p: RBSCloudObjectParams) : RBSCloudObjectState(p)