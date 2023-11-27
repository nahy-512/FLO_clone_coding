package com.example.flo.data.remote

import android.util.Log
import com.example.flo.ui.main.home.AlbumView
import com.example.flo.ui.main.home.HomeView
import com.example.flo.utils.getRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlbumService {
    private lateinit var homeView: HomeView
    private lateinit var albumView: AlbumView

    fun setHomeView(homeView: HomeView) {
        this.homeView = homeView
    }

    fun setAlbumView(albumView: AlbumView) {
        this.albumView = albumView
    }

    fun getAlbums() {
        val albumService = getRetrofit().create(AlbumRetrofitInterface::class.java)

        homeView.onGetAlbumsLoading()

        albumService.getAlbums().enqueue(object: Callback<AlbumResponse> {
            override fun onResponse(call: Call<AlbumResponse>, response: Response<AlbumResponse>) {
                if (response.isSuccessful && response.code() == 200) {
                    val resp: AlbumResponse = response.body()!!

                    Log.d("ALBUM-RESPONSE", resp.toString())

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

    fun getAlbumTracks(albumIdx: Int) {
        val albumService = getRetrofit().create(AlbumRetrofitInterface::class.java)

        albumView.onGetAlbumTracksLoading()

        albumService.getAlbumTracks(albumIdx).enqueue(object: Callback<AlbumTrackResponse> {
            override fun onResponse(call: Call<AlbumTrackResponse>, response: Response<AlbumTrackResponse>) {
                if (response.isSuccessful && response.code() == 200) {
                    val resp: AlbumTrackResponse = response.body()!!

                    Log.d("ALBUM-RESPONSE", resp.toString())

                    when (val code = resp.code) {
                        1000 -> {
                            albumView.onGetAlbumTracksSuccess(code, resp.result)
                        }
                        else -> albumView.onGetAlbumTracksFailure(code, resp.message)
                    }
                }
            }

            override fun onFailure(call: Call<AlbumTrackResponse>, t: Throwable) {
                albumView.onGetAlbumTracksFailure(400, t.message.toString())
            }
        })
    }
}