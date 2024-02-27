package com.vodovoz.app.common.account.data

import com.vodovoz.app.common.datastore.DataStoreRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginManager @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
) {

    fun updateLastAuthPhone(phone: String) {
        dataStoreRepository.putString(PHONE, phone)
    }

    fun fetchLastAuthPhone() = when (dataStoreRepository.contains(PHONE)) {
        true -> dataStoreRepository.getString(PHONE) ?: ""
        false -> ""
    }

    fun updateLastRequestCodeDate(time: Long) {
        dataStoreRepository.putLong(LAST_REQUEST_CODE_DATE, time)
    }

    fun fetchLastRequestCodeDate() = when (dataStoreRepository.contains(LAST_REQUEST_CODE_DATE)) {
        true -> dataStoreRepository.getLong(LAST_REQUEST_CODE_DATE) ?: 0L
        false -> 0L
    }

    fun updateLastRequestCodeTimeOut(time: Int) {
        dataStoreRepository.putInt(LAST_REQUEST_CODE_TIME_OUT, time)
    }

    fun fetchLastRequestCodeTimeOut() =
        when (dataStoreRepository.contains(LAST_REQUEST_CODE_TIME_OUT)) {
            true -> dataStoreRepository.getInt(LAST_REQUEST_CODE_TIME_OUT) ?: 0
            false -> 0
        }

    companion object {
        private const val PHONE = "PHONE"
        private const val LAST_REQUEST_CODE_DATE = "LAST_REQUEST_CODE_DATE"
        private const val LAST_REQUEST_CODE_TIME_OUT = "LAST_REQUEST_CODE_TIME_OUT"
    }
}