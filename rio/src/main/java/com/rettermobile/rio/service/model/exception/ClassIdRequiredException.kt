package com.rettermobile.rio.service.model.exception

/**
 * Thrown when [com.rettermobile.rio.Rio.getCloudObject] has to resolve the
 * instance remotely and no classId was supplied.
 *
 * A local object - [com.rettermobile.rio.cloud.RioCloudObjectOptions.useLocal]
 * with an instanceId - is still constructed without a classId, so that path
 * only logs a warning.
 */
class ClassIdRequiredException(s: String) : Throwable(s)
