package com.rettermobile.rio.cloud

import com.rettermobile.rio.util.RioHttpMethod
import java.lang.reflect.Type

/**
 * Created by semihozkoroglu on 13.12.2021.
 */
class RioCloudObjectOptions constructor(
    var classId: String? = null,
    // Key ve instanceId birlikte olamaz!
    var instanceId: String? = null,
    var key: Pair<String, String>? = null,
    var method: String? = null,
    var httpMethod: RioHttpMethod = RioHttpMethod.POST,
    var body: Any? = null,
    var headers: Map<String, String> = mapOf(),
    var queries: Map<String, Any> = mapOf(),
    /**
     * Controls how [com.rettermobile.rio.Rio.getCloudObject] produces the object.
     *
     * `false` (default) sends an INSTANCE request: a network round trip that
     * resolves the instance remotely. `true` constructs the object handle
     * in memory with no network call, which requires [instanceId] to be set -
     * a [key] cannot be resolved without contacting the server.
     *
     * When `true` cannot be satisfied the SDK logs a warning and falls back to
     * the remote path. Check [com.rettermobile.rio.cloud.RioCloudObject.isLocal]
     * on the returned object to see which branch ran.
     *
     * Only honoured by `getCloudObject`; it has no effect on
     * [com.rettermobile.rio.Rio.makeStaticCall] or `RioCloudObject.call`.
     */
    var useLocal: Boolean = false,
    var path: String? = null,
    var culture: String? = null,
    var type: Type? = null
)