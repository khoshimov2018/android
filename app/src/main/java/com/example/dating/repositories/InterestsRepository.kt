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

    fun getInterests(strToken: String): LiveData<UserModel> {
        val data = MutableLiveData<UserModel>()
        retrofitService.getInterests(strToken)
            .enqueue(object : Callback<UserModel> {
                override fun onResponse(
                    call: Call<UserModel>,
                    response: Response<UserModel>
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

                override fun onFailure(call: Call<UserModel>, t: Throwable) {
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