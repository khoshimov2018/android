package ru.behetem.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.behetem.api.RetrofitService
import ru.behetem.models.ChatMessageModel
import ru.behetem.models.ChatRoomModel
import ru.behetem.responses.BaseResponse
import ru.behetem.utils.getApiElseBaseResponse
import ru.behetem.utils.getFailureBaseResponse
import java.io.InputStream

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
        val chatId = chatRoomModel.chatId ?: ""
        retrofitService.deleteChatRoom(strToken, chatId)
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

    fun getMessages(strToken: String, otherUserId: Int, page: Int, pageSize: Int): LiveData<BaseResponse> {
        val data = MutableLiveData<BaseResponse>()
        retrofitService.getMessages(strToken, otherUserId, page, pageSize)
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

    fun sendMessage(strToken: String, chatMessageModel: ChatMessageModel): LiveData<BaseResponse> {
        val data = MutableLiveData<BaseResponse>()
        retrofitService.sendMessage(strToken, chatMessageModel)
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

    fun uploadImage(inputStream: InputStream, strToken: String): LiveData<BaseResponse> {
        val data = MutableLiveData<BaseResponse>()

        val extension: RequestBody? = "png".toRequestBody(MultipartBody.FORM)

        val map: MutableMap<String, RequestBody?> = HashMap()
        map["extension"] = extension

        val filePart = MultipartBody.Part.createFormData(
            "file", "photo.png", RequestBody.create(
                "image/*".toMediaTypeOrNull(),
                inputStream.readBytes()
            )
        )

        retrofitService.uploadImageChat(filePart, map, strToken)
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