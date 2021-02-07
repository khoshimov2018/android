package ru.behetem.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import ru.behetem.models.FilterModel
import ru.behetem.models.ImageModel
import ru.behetem.models.LocationModel
import ru.behetem.models.UserModel
import ru.behetem.responses.BaseResponse
import ru.behetem.utils.ApiConstants

interface RetrofitInterface {

    @POST(ApiConstants.LOGIN)
    fun userLogin(@Body userModel: UserModel): Call<BaseResponse>

    @POST(ApiConstants.REGISTRATION)
    fun userRegistration(@Body userModel: UserModel): Call<BaseResponse>

    @POST(ApiConstants.CHANGE_INFO)
    fun userChangeInfo(@Body userModel: UserModel, @Header("Authorization") token: String): Call<BaseResponse>

    @Multipart
    @POST(ApiConstants.UPLOAD_IMAGE)
    fun uploadImage(@Part filePart: MultipartBody.Part, @PartMap partMap: @JvmSuppressWildcards Map<String, RequestBody?>,
                    @Header("Authorization") token: String): Call<BaseResponse>

    @GET(ApiConstants.GET_INFO)
    fun getInfo(@Header("Authorization") token: String): Call<BaseResponse>

    @GET(ApiConstants.GET_INTERESTS)
    fun getInterests(@Header("Authorization") token: String): Call<BaseResponse>

    @GET(ApiConstants.GET_NATIONALITIES)
    fun getNationalities(@Header("Authorization") token: String): Call<BaseResponse>

    @GET(ApiConstants.GET_CURRENT_USER_IMAGES)
    fun getCurrentUserImages(@Header("Authorization") token: String): Call<BaseResponse>

    @POST(ApiConstants.SAVE_FILTERS)
    fun saveFilters(@Body filterModel: FilterModel, @Header("Authorization") token: String): Call<BaseResponse>

    @GET(ApiConstants.GET_FILTERS)
    fun getFilters(@Header("Authorization") token: String): Call<BaseResponse>

    @POST(ApiConstants.GET_USERS)
    fun getUsers(@Body filterModel: FilterModel, @Header("Authorization") token: String): Call<BaseResponse>

    @PUT(ApiConstants.UPDATE_LOCATION)
    fun updateLocation(@Body locationModel: LocationModel, @Header("Authorization") token: String): Call<BaseResponse>

    @POST(ApiConstants.DELETE_IMAGE)
    fun deleteImage(@Body imageModel: ImageModel, @Header("Authorization") token: String): Call<BaseResponse>
}