package com.example.dating.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dating.api.RetrofitService
import com.example.dating.models.UserModel
import com.example.dating.responses.BaseResponse
import com.example.dating.utils.Constants
import com.example.dating.utils.getApiElseBaseResponse
import com.example.dating.utils.getFailureBaseResponse
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object NationalitiesRepository {
    private val retrofitService = RetrofitService.getService()

    fun getNationalities(strToken: String): LiveData<BaseResponse> {
        val data = MutableLiveData<BaseResponse>()
        retrofitService.getNationalities(strToken)
            .enqueue(object : Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.isSuccessful) {
                        data.value = response.body()
                    } else {
                        data.value = getApiElseBaseResponse(response)
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    data.value = getFailureBaseResponse(t)
                }
            })
        return data
    }
}