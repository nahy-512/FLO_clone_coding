package com.example.flo

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlbumService {
    private lateinit var homeView: HomeView

    fun setHomeView(homeView: HomeView) {
        this.homeView = homeView
    }

    fun getAlbums() {
        val albumService = getRetrofit().create(AlbumRetrofitInterface::class.java)

        homeView.onGetAlbumsLoading()

        albumService.getAlbums().enqueue(object: Callback<AlbumResponse> {
            override fun onResponse(call: Call<AlbumResponse>, response: Response<AlbumResponse>) {
                if (response.isSuccessful && response.code() == 200) {
                    val resp: AlbumResponse = response.body()!!

                    Log.d("SONG-RESPONSE", resp.toString())

                    when (val code = resp.code) {
                        1000 -> {
                            homeView.onGetAlbumsSuccess(code, resp.result)
                        }
                        else -> homeView.onGetAlbumsFailure(code, resp.message)
                    }
                }
            }

            override fun onFailure(call: Call<AlbumResponse>, t: Throwable) {
                homeView.onGetAlbumsFailure(400, "네트워크 오류가 발생했습니다.")
            }
        })
    }

}