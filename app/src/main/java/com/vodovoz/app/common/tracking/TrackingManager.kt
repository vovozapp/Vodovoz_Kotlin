package com.vodovoz.app.common.tracking

import com.vodovoz.app.common.datastore.DataStoreRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingManager @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
) {

    private var sessionId: String? = null
    private var userGUID: String? = null

    private var viewGUID: String? = null

    private var sessionIdTime = 0

    private var enableTracking = false
    private fun GenerateUIDs() {

        //sessionId - 30 минут
        val timeInMillis: Long = sessionIdTime.toLong() * 60 * 1000
        val currentTime = System.currentTimeMillis()
        val last: Long = dataStoreRepository.getLong("LastSessionIDTime") ?: 0L
        if (currentTime - last > timeInMillis) {
            val uuid = UUID.randomUUID()
            dataStoreRepository.putString("sessionId", uuid.toString())
            sessionId = uuid.toString()
            dataStoreRepository.putLong("LastSessionIDTime", currentTime)
        } else {
            sessionId = dataStoreRepository.getString("sessionId") ?: UUID.randomUUID().toString()
        }

        //userGUID - 0:<TIMESTAMP>:<UUID>
        val yearInMillis = 365L * 24 * 60 * 60 * 1000
        val lastUserGUIDTime = dataStoreRepository.getLong("LastUserGUIDTime") ?: 0L
        if (currentTime - lastUserGUIDTime > yearInMillis) {
            val uuid = UUID.randomUUID()
            val userGUIDBuilder = createGuidUUID(uuid.toString(), currentTime)
            dataStoreRepository.putString("userGUID", userGUIDBuilder)
            userGUID = userGUIDBuilder
            dataStoreRepository.putLong("LastUserGUIDTime", currentTime)
        } else {
            userGUID = dataStoreRepository.getString(
                "userGUID"
            ) ?: createGuidUUID(UUID.randomUUID().toString(), currentTime)

        }

        //viewGUID - Генерируется на каждой странице и сквозной для всех трекинг запросов со страницы при ее посещении в данный момент времени.
        val uuid = UUID.randomUUID()
        viewGUID = uuid.toString()
    }

    private fun createGuidUUID(uuid: String, currentTime: Long): String {
        val timestampInBase36 = currentTime.toString(36)
        return "0:$timestampInBase36:$uuid"
    }

    fun getTracking(): Map<String, String> {
        if (!enableTracking) return emptyMap()
        GenerateUIDs()
        val trackingMap = HashMap<String, String>()
        trackingMap["sessionId"] = sessionId ?: ""
        trackingMap["userGUID"] = userGUID ?: ""
        trackingMap["viewGUID"] = viewGUID ?: ""
        return trackingMap
    }

    fun setEnableTracking(enableTracking: Boolean) {
        this.enableTracking = enableTracking
    }

    fun setSessionIdTime(sessionIdTime: Int) {
        this.sessionIdTime = sessionIdTime
    }
}