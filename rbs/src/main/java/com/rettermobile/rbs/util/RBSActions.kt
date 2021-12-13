package com.rettermobile.rbs.util

/**
 * Created by semihozkoroglu on 15.03.2021.
 */
enum class RBSActions(val action: String) {
    LOGOUT("rbs.core.request.LOGOUT_USER"),
    INSTANCE("rbs.core.request.INSTANCE"),
    CALL("rbs.core.request.CALL"),
    STATE("rbs.core.request.STATE")
}