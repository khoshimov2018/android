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
import ru.behetem.models.*
import ru.behetem.responses.BaseResponse
import ru.behetem.utils.ApiConstants
import ru.behetem.utils.getApiElseBaseResponse
import ru.behetem.utils.getFailureBaseResponse
import java.io.InputStream

object UserRepository {

    private val retrofitService = RetrofitService.getService()

    fun login(userModel: UserModel): LiveData<BaseResponse> {
        val data = MutableLiveData<BaseResponse>()
        retrofitService.userLogin(userModel)
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

    fun register(userModel: UserModel): LiveData<BaseResponse> {
        val data = MutableLiveData<BaseResponse>()
        retrofitService.userRegistration(userModel)
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

    fun changeInfo(userModel: UserModel, strToken: String): LiveData<BaseResponse> {
        val data = MutableLiveData<BaseResponse>()
        retrofitService.userChangeInfo(userModel, strToken)
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

        retrofitService.uploadImage(filePart, map, strToken)
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

    fun getInfo(strToken: String): LiveData<BaseResponse> {
        val data = MutableLiveData<BaseResponse>()
        retrofitService.getInfo(strToken)
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

    fun getCurrentUserImages(strToken: String): LiveData<BaseResponse> {
        val data = MutableLiveData<BaseResponse>()
        retrofitService.getCurrentUserImages(strToken)
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

    fun getUsers(strToken: String, filterModel: FilterModel): LiveData<BaseResponse> {
        val data = MutableLiveData<BaseResponse>()

        var params = "lang=RU&page=${filterModel.page}&size=${filterModel.size}"

        if(filterModel.gender != null) {
            params += "&gender=${filterModel.gender}"
        }

        if(filterModel.maxDistance != null) {
            params += "&maxDistance=${filterModel.maxDistance}"
        }

        if(filterModel.dateOfBirthFrom != null) {
            params += "&dateOfBirthFrom=${filterModel.dateOfBirthFrom}"
        }

        if(filterModel.dateOfBirthTo != null) {
            params += "&dateOfBirthTo=${filterModel.dateOfBirthTo}"
        }

        if(filterModel.growthFrom != null) {
            params += "&growthFrom=${filterModel.growthFrom}"
        }

        if(filterModel.growthTo != null) {
            params += "&growthTo=${filterModel.growthTo}"
        }

        if(filterModel.weightFrom != null) {
            params += "&weightFrom=${filterModel.weightFrom}"
        }

        if(filterModel.weightTo != null) {
            params += "&weightTo=${filterModel.weightTo}"
        }

        if(filterModel.childrenDesire != null) {
            params += "&childrenDesire=${filterModel.childrenDesire}"
        }

        if(filterModel.interest != null) {
            for(interest in filterModel.interest!!) {
                params += "&interest=${interest}"
            }
        }

        if(filterModel.nationality != null) {
            for(nationality in filterModel.nationality!!) {
                params += "&nationality=${nationality}"
            }
        }

        if(filterModel.bodyType != null) {
            for(bodyType in filterModel.bodyType!!) {
                params += "&bodyType=${bodyType}"
            }
        }

        if(filterModel.childrenPresence != null) {
            for(childrenPresence in filterModel.childrenPresence!!) {
                params += "&childrenPresence=${childrenPresence}"
            }
        }

        if(filterModel.educationLevel != null) {
            for(educationLevel in filterModel.educationLevel!!) {
                params += "&educationLevel=${educationLevel}"
            }
        }

        if(filterModel.languageKnowledge != null) {
            for(languageKnowledge in filterModel.languageKnowledge!!) {
                params += "&languageKnowledge=${languageKnowledge}"
            }
        }

        if(filterModel.religionRespect != null) {
            for(religionRespect in filterModel.religionRespect!!) {
                params += "&religionRespect=${religionRespect}"
            }
        }

        if(filterModel.status != null) {
            for(status in filterModel.status!!) {
                params += "&status=${status}"
            }
        }

        if(filterModel.traditionsRespect != null) {
            for(traditionsRespect in filterModel.traditionsRespect!!) {
                params += "&traditionsRespect=${traditionsRespect}"
            }
        }

        retrofitService.getUsers(strToken, "${ApiConstants.GET_USERS}?${params}")
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

    /*fun getUsersMultipart(strToken: String, filterModel: FilterModel): LiveData<BaseResponse> {
        val data = MutableLiveData<BaseResponse>()

        val map: MutableMap<String, RequestBody?> = HashMap()

        val page: RequestBody = filterModel.page.toRequestBody(MultipartBody.FORM)
        map["extension"] = extension

        retrofitService.getUsers(filterModel, strToken)
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
    }*/

    fun deleteImage(strToken: String, imageModel: ImageModel): LiveData<BaseResponse> {
        val data = MutableLiveData<BaseResponse>()
        retrofitService.deleteImage(imageModel, strToken)
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

    fun changePassword(strToken: String, changePasswordModel: ChangePasswordModel): LiveData<BaseResponse> {
        val data = MutableLiveData<BaseResponse>()
        retrofitService.changePassword(changePasswordModel, strToken)
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

    fun deleteAccount(strToken: String): LiveData<BaseResponse> {
        val data = MutableLiveData<BaseResponse>()
        retrofitService.deleteAccount(strToken)
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