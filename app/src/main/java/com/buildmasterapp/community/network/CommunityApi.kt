package com.buildmasterapp.community.network

import com.buildmasterapp.community.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface CommunityApi {
    @GET("posts/")
    suspend fun getPosts(): Response<List<NetworkPost>>

    @POST("posts/")
    suspend fun createPost(
        @Body post: CreatePostRequest
    ): Response<NetworkPost>

    @GET("posts/user/{user_id}")
    suspend fun getPostsByUser(
        @Path("user_id") userId: Int
    ): Response<List<NetworkPost>>

    @POST("posts/{post_id}/repost")
    suspend fun repost(
        @Path("post_id") postId: Int
    ): Response<NetworkPost>

    @POST("posts/{post_id}/comment")
    suspend fun commentPost(
        @Path("post_id") postId: Int,
        @Body comment: CreateCommentRequest
    ): Response<NetworkPost>

    @POST("posts/{post_id}/like")
    suspend fun likePost(
        @Path("post_id") postId: Int
    ): Response<NetworkPost>

    @POST("posts/{post_id}/dislike")
    suspend fun dislikePost(
        @Path("post_id") postId: Int
    ): Response<NetworkPost>

    @GET("users/{user_id}")
    suspend fun getUserById(
        @Path("user_id") userId: Int
    ): Response<User>
}
