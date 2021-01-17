package com.example.dating.api

import com.example.dating.models.UserModel
import com.example.dating.responses.BaseResponse
import com.example.dating.utils.ApiConstants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {

    @POST(ApiConstants.LOGIN)
    fun userLogin(@Body userModel: UserModel): Call<BaseResponse>

    @POST(ApiConstants.REGISTRATION)
    fun userRegistration(@Body userModel: UserModel): Call<BaseResponse>

    @POST(ApiConstants.CHANGE_INFO)
    fun userChangeInfo(@Body userModel: UserModel, @Header("Authorization") token: String): Call<BaseResponse>

    @Multipart
    @POST(ApiConstants.UPLOAD_IMAGE)
    fun uploadImage(@Part filePart: MultipartBody.Part, @Header("Authorization") token: String): Call<BaseResponse>

    @GET(ApiConstants.GET_INFO)
    fun getInfo(@Header("Authorization") token: String): Call<BaseResponse>

    @GET(ApiConstants.GET_INTERESTS)
    fun getInterests(@Header("Authorization") token: String): Call<BaseResponse>

    @GET(ApiConstants.GET_NATIONALITIES)
    fun getNationalities(@Header("Authorization") token: String): Call<BaseResponse>

    @POST(ApiConstants.GET_CURRENT_USER_IMAGES)
    fun getCurrentUserImages(@Header("Authorization") token: String): Call<BaseResponse>
}