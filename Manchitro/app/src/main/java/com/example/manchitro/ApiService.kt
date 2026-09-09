package com.example.manchitro.network

import com.example.manchitro.json.PlaceDataItem
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("api.php")
    suspend fun getEntities(): List<PlaceDataItem>

    @Multipart
    @POST("api.php")
    suspend fun createEntity(
        @Part("title") title: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<PlaceDataItem>

    @FormUrlEncoded
    @PUT("api.php")
    suspend fun updateEntity(
        @Field("id") id: Int,
        @Field("title") title: String,
        @Field("lat") lat: Double,
        @Field("lon") lon: Double
    ): Response<PlaceDataItem>

    @DELETE("api.php/{id}")
    suspend fun deleteEntity(@Path("id") id: Int): PlaceDataItem

    @Multipart
    @POST("upload.php")
    suspend fun uploadImage(@Part image: MultipartBody.Part): Response<UploadResponse>
}
