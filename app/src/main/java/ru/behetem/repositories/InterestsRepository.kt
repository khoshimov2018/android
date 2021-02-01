package ru.behetem.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.behetem.api.RetrofitService
import ru.behetem.responses.BaseResponse
import ru.behetem.utils.getApiElseBaseResponse
import ru.behetem.utils.getFailureBaseResponse

object InterestsRepository {

    private val retrofitService = RetrofitService.getService()

    fun getInterests(strToken: String): LiveData<BaseResponse> {
        val data = MutableLiveData<BaseResponse>()
        retrofitService.getInterests(strToken)
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