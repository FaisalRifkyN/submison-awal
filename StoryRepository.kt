package com.dicoding.appstory.data

import android.util.Log
import androidx.lifecycle.liveData
import com.dicoding.appstory.data.prefence.Preference
import com.dicoding.appstory.data.prefence.UserModel
import com.dicoding.appstory.data.response.StoryAddResponse
import com.dicoding.appstory.data.response.StoryRegisterResponse
import com.dicoding.appstory.data.response.StoryResponse
import com.dicoding.appstory.data.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class StoryRepository(
    private val apiService: ApiService,
    private val userPreference: Preference,
) {

    fun registerUser(name: String, email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(name, email, password)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonString = e.response()?.errorBody()?.string()
            Log.d(TAG, "StoryRepository: $jsonString")
            val errorBody = Gson().fromJson(jsonString, StoryRegisterResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage))
        } catch (e: Exception) {
            emit(Result.Error("Lost Connection"))
        }
    }

    fun loginUser(email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonString = e.response()?.errorBody()?.string()
            Log.d(TAG, "StoryRepository: $jsonString")
            val errorBody = Gson().fromJson(jsonString, StoryRegisterResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage))
        } catch (e: Exception) {
            emit(Result.Error("Lost Connection"))
        }
    }

    fun getAllStory() = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStories()
            emit(Result.Success(response.listStory))
        } catch (e: HttpException) {
            val jsonString = e.response()?.errorBody()?.string()
            Log.d(TAG, "StoryRepository: $jsonString")
            val errorBody = Gson().fromJson(jsonString, StoryResponse::class.java)
            emit(Result.Error(errorBody.message))
        } catch (e: Exception) {
            emit(Result.Error("Lost Connection"))
        }
    }



    fun uploadImage(imageFile: File, description: String) = liveData {
        emit(Result.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())

        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val successResponse = apiService.uploadImage(multipartBody, requestBody)
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val jsonString = e.response()?.errorBody()?.string()
            Log.d(TAG, "StoryRepository: $jsonString")
            val errorResponse = Gson().fromJson(jsonString, StoryAddResponse::class.java)
            emit(Result.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(Result.Error("Lost Connection"))
        }
    }

    suspend fun saveSession(userModel: UserModel) = userPreference.saveSession(userModel)

    suspend fun logout() = userPreference.logout()

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    companion object {

        private const val TAG = "StoryRepository"

        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: Preference,
        ): StoryRepository? {
            synchronized(this) {
                instance = StoryRepository(apiService, userPreference)
            }
            return instance
        }
    }
}