package com.example.dating.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dating.api.RetrofitService
import com.example.dating.models.FilterModel
import com.example.dating.responses.BaseResponse
import com.example.dating.utils.getApiElseBaseResponse
import com.example.dating.utils.getFailureBaseResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object FiltersRepository {

    private val retrofitService = RetrofitService.getService()

    fun saveFilters(filterModel: FilterModel, strToken: String): LiveData<BaseResponse> {
        val data = MutableLiveData<BaseResponse>()
        retrofitService.saveFilters(filterModel, strToken)
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

    fun getFilters(strToken: String): LiveData<BaseResponse> {
        val data = MutableLiveData<BaseResponse>()
        retrofitService.getFilters(strToken)
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