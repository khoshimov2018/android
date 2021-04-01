package ru.behetem.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import ru.behetem.models.*
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

    @GET(ApiConstants.GET_INFO + "?lang=RU")
    fun getInfo(@Header("Authorization") token: String): Call<BaseResponse>

    @GET(ApiConstants.GET_INTERESTS + "?lang=RU")
    fun getInterests(@Header("Authorization") token: String): Call<BaseResponse>

    @GET(ApiConstants.GET_NATIONALITIES + "?lang=RU")
    fun getNationalities(@Header("Authorization") token: String): Call<BaseResponse>

    @GET(ApiConstants.GET_CURRENT_USER_IMAGES)
    fun getCurrentUserImages(@Header("Authorization") token: String): Call<BaseResponse>

    @POST(ApiConstants.SAVE_FILTERS)
    fun saveFilters(@Body filterModel: FilterModel, @Header("Authorization") token: String): Call<BaseResponse>

    @GET(ApiConstants.GET_FILTERS)
    fun getFilters(@Header("Authorization") token: String): Call<BaseResponse>

//    (ApiConstants.GET_USERS + "{params}")
    @GET
    fun getUsers(@Header("Authorization") token: String, @Url url: String): Call<BaseResponse>

    @PUT(ApiConstants.UPDATE_LOCATION)
    fun updateLocation(@Body locationModel: LocationModel, @Header("Authorization") token: String): Call<BaseResponse>

    @POST(ApiConstants.DELETE_IMAGE)
    fun deleteImage(@Body imageModel: ImageModel, @Header("Authorization") token: String): Call<BaseResponse>

    @POST(ApiConstants.SEND_REACTION)
    fun sendReaction(@Body reactionModel: ReactionModel, @Header("Authorization") token: String): Call<BaseResponse>

    @GET(ApiConstants.GET_REACTIONS)
    fun getReactions(@Header("Authorization") token: String): Call<BaseResponse>

    @POST(ApiConstants.CHANGE_EMAIL)
    fun changeEmail(@Body userModel: UserModel, @Header("Authorization") token: String): Call<BaseResponse>

    @POST(ApiConstants.CHANGE_PASSWORD)
    fun changePassword(@Body changePasswordModel: ChangePasswordModel, @Header("Authorization") token: String): Call<BaseResponse>

    @POST(ApiConstants.DELETE_ACCOUNT)
    fun deleteAccount(@Header("Authorization") token: String): Call<BaseResponse>

    @GET(ApiConstants.GET_USER_DETAIL)
    fun getUserDetail(@Header("Authorization") token: String, @Query("lang") lang: String, @Query("id") userId: Int): Call<BaseResponse>

    @PUT
    fun changeReaction(@Header("Authorization") token: String, @Url url: String): Call<BaseResponse>

    @GET(ApiConstants.GET_CHAT_ROOM)
    fun getChatRooms(@Header("Authorization") token: String): Call<BaseResponse>

    /*@POST(ApiConstants.DELETE_CHAT_ROOM)
    fun deleteChatRoom(@Header("Authorization") token: String, @Body chatRoomModel: ChatRoomModel): Call<BaseResponse>*/

    @GET(ApiConstants.DELETE_CHAT_ROOM)
    fun deleteChatRoom(@Header("Authorization") token: String, @Query("chatId") chatId: String): Call<BaseResponse>

    @GET(ApiConstants.GET_COMMERCIAL)
    fun getCommercial(@Header("Authorization") token: String): Call<BaseResponse>

    @GET(ApiConstants.ACTIVITY_CHECK)
    fun activityCheck(@Header("Authorization") token: String, @Query("userId") userId: Int): Call<BaseResponse>

    @GET(ApiConstants.GET_MESSAGES + "/{otherUserId}")
    fun getMessages(@Header("Authorization") token: String, @Path("otherUserId") otherUserId: Int, @Query("page") page: Int, @Query("pageSize") pageSize: Int): Call<BaseResponse>

    @POST(ApiConstants.SEND_MESSAGE)
    fun sendMessage(@Header("Authorization") token: String, @Body chatMessageModel: ChatMessageModel): Call<BaseResponse>
}