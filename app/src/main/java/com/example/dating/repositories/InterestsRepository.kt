package com.example.dating.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dating.api.RetrofitService
import com.example.dating.models.UserModel
import com.example.dating.utils.Constants
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object InterestsRepository {

    private val retrofitService = RetrofitService.getService()

    fun getInterests(strToken: String): LiveData<Any> {
        val data = MutableLiveData<Any>()
        retrofitService.getInterests(strToken)
            .enqueue(object : Callback<Any> {
                override fun onResponse(
                    call: Call<Any>,
                    response: Response<Any>
                ) {
                    if (response.isSuccessful) {
                        data.value = response.body()
                    } else {
                        val baseResponse = UserModel()
                        baseResponse.status = 401
                        baseResponse.error = Constants.ERROR
                        baseResponse.message = Constants.SOMETHING_WENT_WRONG
                        response.errorBody()?.let {
                            try {
                                val gson = Gson()
                                val user = gson.fromJson(response.errorBody()!!.string(), UserModel::class.java)
                                baseResponse.status = user.status
                                baseResponse.error = user.error
                                baseResponse.message = user.message
                            } catch (e: Exception) {
                                // ignore
                            }
                        }
                        data.value = baseResponse
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    val baseResponse = UserModel()
                    baseResponse.status = 401
                    baseResponse.error = Constants.ERROR
                    baseResponse.message = Constants.COULD_NOT_CONNECT_TO_SERVER
                    t.message?.let {
                        baseResponse.message = it
                    }
                    data.value = baseResponse
                }
            })
        return data
    }
}