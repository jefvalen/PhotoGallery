package com.jeffrey.android.photogallery;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiEndPointInterface  {

    @GET("services/rest/?method=flickr.photos.getRecent&extras=url_s&format=json&nojsoncallback=1")
    Call<GalleryApiResponse> getGalleryItems(@Query("api_key")String apikey,
                                             @Query("page") int page); //challenge paging

    @GET("services/rest/?method=flickr.photos.search&extras=url_s&format=json&nojsoncallback=1&")
    Call<GalleryApiResponse> getGallerySearchItems(@Query("api_key") String apikey, @Query("text") String search); //challenge paging

}
