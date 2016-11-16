package com.jeffrey.android.photogallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class PhotoGalleryFragment extends VisibleFragment {

    private RecyclerView mPhotoRecyclerView;
    private static final String API_KEY = "12e0edb0f21341a1adce3c8af18e73b9";
    public static final String BASE_URL = "https://api.flickr.com/";

    private static List<GalleryItem> mGalleryItems;
    private int pageNumber = 1; //Challenge Paging
    private String mSearch;

    private static final String TAG = "FlickrFetchr";

    public static Fragment newInstance() {
        return new PhotoGalleryFragment(); //aangepast
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        //Intent i = PollService.newIntent(getActivity());
        //getActivity().startService(i);
        //PollService.setServiceAlarm(getActivity(), true); //TRIGGERS ALARM
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_photo_gallery, menu);

        MenuItem searchitem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchitem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
                public boolean onQueryTextSubmit(String search){
                Log.d(TAG, "QueryTextSubmit: " + search);
                mSearch = search;
                QueryPreferences.setPrefSearchOn(getActivity(),mSearch);
                updateItems();
                return true;
            }

            @Override
                    public boolean onQueryTextChange(String search){
                Log.d(TAG, "QueryTextChange: " + search);
                return false;
            }
        });

        MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
        if (PollService.isServiceAlarmOn(getActivity())) {
            toggleItem.setTitle(R.string.stop_polling);
        }else{
            toggleItem.setTitle(R.string.start_polling);
        }

    }

    private void updateItems(){
        new FlickrFetch().getData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_photo_gallery_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3)); //handles the gridmanager, de 3 is het aantal pic op het scherm

        mPhotoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(dy)) {
                    if (pageNumber < 10) {
                        pageNumber++;
                        new FlickrFetch().getData();
                    }
                }
            }
        });

        mSearch = QueryPreferences.getPrefSearchOn(getActivity());

        new FlickrFetch().getData();
        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  //TOGGLE MENU ITEM IMPLEMENTATION, PAGE 479
        switch (item.getItemId()){
            case R.id.menu_item_toggle_polling:
                boolean shouldStartAlarm = !PollService.isServiceAlarmOn(getActivity());
                PollService.setServiceAlarm(getActivity(), shouldStartAlarm);
                getActivity().invalidateOptionsMenu();
                return true;
            case R.id.menu_item_clear:
                QueryPreferences.setPrefSearchOn(getActivity(), null);
                updateItems();
                return true;
            default:
                return super.onOptionsItemSelected(item);
            }
    }


    public static List<GalleryItem> getGalleryItems() {
        return mGalleryItems;
    }


    public class FlickrFetch {

        public void getData() {

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiEndPointInterface apiResponse = retrofit.create(ApiEndPointInterface.class);

            if (mSearch == null) {
                apiResponse.getGalleryItems(API_KEY, pageNumber).enqueue(new
                     Callback<GalleryApiResponse>() {
                         @Override
                         public void onResponse(Call<GalleryApiResponse> call, Response<GalleryApiResponse> response) {
                             GalleryApiResponse mGalleryApiResponse = response.body();
                             if (response.body() == null) {
                                 Log.e("Retrofit body null", String.valueOf(response.code()));
                             }
                             mGalleryItems = mGalleryApiResponse.getGalleryItems();
                             Log.v("mGalleryitems",
                                     String.valueOf(response.body().getGalleryItems().size()));

                             if (mPhotoRecyclerView != null) {
                                 mPhotoRecyclerView.setAdapter(new PhotoAdapter(mGalleryItems));
                             }
                         }

                         @Override
                         public void onFailure(Call<GalleryApiResponse> call, Throwable t) {
                             Log.e("Retrofit error", t.getMessage());
                         }
                     });
            } else {
                apiResponse.getGallerySearchItems(API_KEY, mSearch).enqueue(new
                      Callback<GalleryApiResponse>() {
                          @Override
                          public void onResponse(Call<GalleryApiResponse> call, Response<GalleryApiResponse> response) {
                              GalleryApiResponse mGalleryApiResponse = response.body();
                              if (response.body() == null) {
                                  Log.e("Retrofit body null", String.valueOf(response.code()));
                              }
                              mGalleryItems = mGalleryApiResponse.getGalleryItems();
                              Log.v("mGalleryitems",
                                      String.valueOf(response.body().getGalleryItems().size()));

                              if (mPhotoRecyclerView != null) {
                                  mPhotoRecyclerView.setAdapter(new PhotoAdapter(mGalleryItems));
                              }
                          }

                          @Override
                          public void onFailure(Call<GalleryApiResponse> call, Throwable t) {
                              Log.e("Retrofit error", t.getMessage());
                          }
                      });
            }
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mItemImageView;
        private GalleryItem mGalleryItem; //FIRING IMPLICIT INTENT WHEN ITEM IS PRESSED, PAGE 515

        public PhotoHolder(View itemView) {
            super(itemView);
            //mTitleTextView = (TextView) itemView; 1e aanpassing
            //mItemImageView = (ImageView) itemView; 2e aanpassing
            mItemImageView = (ImageView) itemView.findViewById(R.id.iv_photo_gallery_fragment); // zorgt dat photo in de gallery te zien is...
            mItemImageView.setOnClickListener(this);
        }

        public void bindGalleryItem(GalleryItem item) { //mTitleTextView.setText(item.getId());
            Glide.with(getActivity())
                    .load(item.getUrl())
                    .placeholder(R.mipmap.ic_launcher)
                    .into(mItemImageView);

            mGalleryItem = item;
        }

        @Override
        public void onClick(View v) {
            //Intent i = new Intent(Intent.ACTION_VIEW, mGalleryItem.getPhotoPageUri());
            Intent i = PhotoPageActivity.newIntent(getActivity(), mGalleryItem.getPhotoPageUri());
            startActivity(i);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }


        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //TextView textView = new TextView(getActivity());
            //return new PhotoHolder(textView); 1e aanpassing
            //ImageView imageView = new ImageView(getActivity()); //2e aanpassing
            //return new PhotoHolder(imageView); //2e aanpassing
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false);
            return new PhotoHolder(view);   //creert de view en 'inflate' de gallery item...
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem mGalleryItem = mGalleryItems.get(position);
            holder.bindGalleryItem(mGalleryItem);
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }


}
