package ru.behetem.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.behetem.api.RetrofitService
import ru.behetem.models.ChatRoomModel
import ru.behetem.responses.BaseResponse
import ru.behetem.utils.getApiElseBaseResponse
import ru.behetem.utils.getFailureBaseResponse

object ChatsRepository {

    private val retrofitService = RetrofitService.getService()

    fun getChatRooms(strToken: String): LiveData<BaseResponse> {
        val data = MutableLiveData<BaseResponse>()
        retrofitService.getChatRooms(strToken)
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

    fun deleteChatRoom(strToken: String, chatRoomModel: ChatRoomModel): LiveData<BaseResponse> {
        val data = MutableLiveData<BaseResponse>()
        retrofitService.deleteChatRoom(strToken, chatRoomModel)
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