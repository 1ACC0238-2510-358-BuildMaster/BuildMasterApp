package com.buildmasterapp.community.network

import com.buildmasterapp.community.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface CommunityApi {
    @GET("posts/")
    suspend fun getPosts(
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0
    ): Response<List<NetworkPost>>

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

    @DELETE("posts/{post_id}/repost")
    suspend fun removeRepost(
        @Path("post_id") postId: Int
    ): Response<Unit>

    @POST("posts/{post_id}/comment")
    suspend fun commentPost(
        @Path("post_id") postId: Int,
        @Body comment: CreateCommentRequest
    ): Response<NetworkPost>

    @POST("posts/{post_id}/like")
    suspend fun likePost(
        @Path("post_id") postId: Int
    ): Response<NetworkPost>

    @DELETE("posts/{post_id}/like")
    suspend fun removeLike(
        @Path("post_id") postId: Int
    ): Response<Unit>

    @POST("posts/{post_id}/dislike")
    suspend fun dislikePost(
        @Path("post_id") postId: Int
    ): Response<NetworkPost>

    @DELETE("posts/{post_id}/dislike")
    suspend fun removeDislike(
        @Path("post_id") postId: Int
    ): Response<Unit>

    @GET("users/{user_id}")
    suspend fun getUserById(
        @Path("user_id") userId: Int
    ): Response<User>

    @GET("posts/search")
    suspend fun searchPosts(
        @Query("query") query: String,
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0
    ): Response<List<NetworkPost>>

    @PUT("posts/{post_id}")
    suspend fun updatePost(
        @Path("post_id") postId: Int,
        @Body post: UpdatePostRequest
    ): Response<NetworkPost>

    @DELETE("posts/{post_id}")
    suspend fun deletePost(
        @Path("post_id") postId: Int
    ): Response<Unit>

    @PUT("posts/{post_id}/comment/{comment_id}")
    suspend fun updateComment(
        @Path("post_id") postId: Int,
        @Path("comment_id") commentId: Int,
        @Body comment: UpdateCommentRequest
    ): Response<CommentResponse>

    @DELETE("posts/{post_id}/comment/{comment_id}")
    suspend fun deleteComment(
        @Path("post_id") postId: Int,
        @Path("comment_id") commentId: Int
    ): Response<Unit>
}
