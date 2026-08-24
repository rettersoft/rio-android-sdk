package com.rettermobile.rio.service.model.exception

/**
 * Thrown when [com.rettermobile.rio.Rio.getCloudObject] is called without a classId.
 */
class ClassIdRequiredException(s: String) : Throwable(s)
