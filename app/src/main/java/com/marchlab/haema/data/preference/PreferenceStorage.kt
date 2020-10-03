package com.marchlab.haema.data.preference

interface PreferenceStorage {

    var purchased: Boolean

    var password: String?

    var appLock: Boolean

    /* var isAcknowledged: Boolean */

}