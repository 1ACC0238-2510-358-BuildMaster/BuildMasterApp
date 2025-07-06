package com.buildmasterapp.community.data

import com.buildmasterapp.community.data.model.*
import com.buildmasterapp.community.network.CommunityApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Response
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor

class CommunityRepository(private val tokenProvider: TokenProvider) {
    // TokenProvider es una interfaz que deberás implementar en com.buildmasterapp.user
    // para obtener el token actual del usuario autenticado

    private val api: CommunityApi

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request: Request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${tokenProvider.getToken()}")
                    .build()
                chain.proceed(request)
            }
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://buildmaster-api-ddh3asdah2bsggfs.canadacentral-01.azurewebsites.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        api = retrofit.create(CommunityApi::class.java)
    }

    suspend fun getPosts(): Response<List<NetworkPost>> = api.getPosts()
    suspend fun createPost(request: CreatePostRequest): Response<NetworkPost> = api.createPost(request)
    suspend fun getPostsByUser(userId: Int): Response<List<NetworkPost>> = api.getPostsByUser(userId)
    suspend fun repost(postId: Int): Response<NetworkPost> = api.repost(postId)
    suspend fun commentPost(postId: Int, request: CreateCommentRequest): Response<NetworkPost> = api.commentPost(postId, request)
    suspend fun likePost(postId: Int): Response<NetworkPost> = api.likePost(postId)
    suspend fun dislikePost(postId: Int): Response<NetworkPost> = api.dislikePost(postId)

    // NUEVO: cache de user_id a username
    private val userIdToUsername = mutableMapOf<Int, String>()

    // NUEVO: función para obtener username por user_id
    suspend fun getUsernameByUserId(userId: Int): String? {
        userIdToUsername[userId]?.let { return it }
        // Llama al endpoint de usuario (ajusta la ruta y modelo según tu backend)
        // Ejemplo: /users/{user_id}
        return try {
            val response = api.getUserById(userId)
            if (response.isSuccessful) {
                val user = response.body()
                user?.username?.also { userIdToUsername[userId] = it }
            } else null
        } catch (e: Exception) {
            null
        }
    }
}

// Interfaz para obtener el token desde com.buildmasterapp.user
interface TokenProvider {
    fun getToken(): String
}
