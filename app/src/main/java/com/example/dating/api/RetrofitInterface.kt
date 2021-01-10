package com.example.dating.api

import com.example.dating.models.UserModel
import com.example.dating.utils.ApiConstants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {

    @POST(ApiConstants.LOGIN)
    fun userLogin(@Body userModel: UserModel): Call<UserModel>

    @POST(ApiConstants.REGISTRATION)
    fun userRegistration(@Body userModel: UserModel): Call<UserModel>

    @POST(ApiConstants.CHANGE_INFO)
    fun userChangeInfo(@Body userModel: UserModel, @Header("Authorization") token: String): Call<UserModel>

    @Multipart
    @POST(ApiConstants.UPLOAD_IMAGE)
    fun uploadImage(@Part filePart: MultipartBody.Part, @Header("Authorization") token: String): Call<UserModel>
}