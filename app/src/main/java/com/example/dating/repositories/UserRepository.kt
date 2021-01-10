package com.example.dating.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dating.api.RetrofitService
import com.example.dating.models.UserModel
import com.example.dating.utils.Constants
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream

object UserRepository {

    private val retrofitService = RetrofitService.getService()

    fun login(userModel: UserModel): LiveData<UserModel> {
        val data = MutableLiveData<UserModel>()
        retrofitService.userLogin(userModel)
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

    fun register(userModel: UserModel): LiveData<UserModel> {
        val data = MutableLiveData<UserModel>()
        retrofitService.userRegistration(userModel)
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

    fun changeInfo(userModel: UserModel, strToken: String): LiveData<UserModel> {
        val data = MutableLiveData<UserModel>()
        retrofitService.userChangeInfo(userModel, strToken)
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

    fun uploadImage(inputStream: InputStream, strToken: String): LiveData<UserModel> {
        val data = MutableLiveData<UserModel>()

        val filePart = MultipartBody.Part.createFormData(
            "file", "photo.png", RequestBody.create(
                "image/*".toMediaTypeOrNull(),
                inputStream.readBytes()
            )
        )

        retrofitService.uploadImage(filePart, strToken)
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

    fun getInfo(strToken: String): LiveData<UserModel> {
        val data = MutableLiveData<UserModel>()
        retrofitService.getInfo(strToken)
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