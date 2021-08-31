package com.vosxvo.weatherforecast.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.vosxvo.weatherforecast.BuildConfig;
import com.vosxvo.weatherforecast.MainActivity;
import com.vosxvo.weatherforecast.R;
import com.vosxvo.weatherforecast.adapter.LocationAdapter;
import com.vosxvo.weatherforecast.database.LocationDbHelper;
import com.vosxvo.weatherforecast.model.LocationItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SavedLocationFragment extends Fragment {
    public static final String FOCUSED_DELETE_COLOR = "#FFB71C1C";
    public static final String FOCUSED_DELETE_BACKGROUND = "#25FFFFFF"; // 25B71C1C
    public static final String DEFAULT_DELETE_COLOR = "#FFFFFF"; // FF707070

    private RecyclerView locationList;
    private FloatingActionButton actionButton;
    private LocationAdapter adapter;

    // Callback after add new location from Places Autocomplete Intent
    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data == null) return;

                    Place place = Autocomplete.getPlaceFromIntent(data);
                    if (place.getLatLng() == null) return;

                    Bundle bundle = new Bundle();
                    bundle.putDouble("lat", place.getLatLng().latitude);
                    bundle.putDouble("lon", place.getLatLng().longitude);

                    getParentFragmentManager().setFragmentResult(MainActivity.GET_WEATHER_REQUEST_KEY, bundle);
                    getParentFragmentManager().setFragmentResultListener(
                            MainActivity.SAVED_LOCATION_UPDATE,
                            this,
                            (requestKey, result1) -> {
                                adapter.insertItem(new LocationItem(
                                        result1.getString("name") + ", " + result1.getString("sys.country"),
                                        place.getLatLng().latitude,
                                        place.getLatLng().longitude));
                            }
                    );
                }
            });

    public SavedLocationFragment() {
        super(R.layout.fragment_saved_location);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater,
                             @Nullable @org.jetbrains.annotations.Nullable ViewGroup container,
                             @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_saved_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view,
                              @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        locationList = view.findViewById(R.id.location_list);
        actionButton = view.findViewById(R.id.floating_button);

        adapter = new LocationAdapter(getActivity());

        locationList.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayout.VERTICAL));
        locationList.setLayoutManager(new LinearLayoutManager(getActivity()));
        locationList.setAdapter(adapter);
        locationList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    actionButton.hide();
                } else {
                    actionButton.show();
                }
            }
        });


        // Setup Autocomplete Intent
        if (!Places.isInitialized()) {
            Places.initialize(getActivity(), BuildConfig.GOOGLE_PLACE_API_KEY);
        }

        List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG);

        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(getActivity());
        actionButton.setOnClickListener(v -> resultLauncher.launch(intent));

        // Setup swipe to delete
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.ACTION_STATE_IDLE,
                ItemTouchHelper.RIGHT
        ) {
            @Override
            public int getSwipeDirs(@NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder.getAdapterPosition() == 0) return 0;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView,
                                  @NonNull @NotNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull @NotNull RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (viewHolder instanceof LocationAdapter.LocationViewHolder) {
                    int position = viewHolder.getAdapterPosition();
                    LocationItem item = adapter.getList().get(position);
                    adapter.removeItem(position);
                    Snackbar.make(locationList, "Deleted!", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", v -> {
                                adapter.restoreItem(position, item);
                                if (position == adapter.getList().size() - 1) {
                                    locationList.scrollToPosition(position);
                                }
                            })
                            .setActionTextColor(Color.YELLOW)
                            .show();
                }
            }

            @Override
            public void onSelectedChanged(@Nullable @org.jetbrains.annotations.Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                if (viewHolder != null) {
                    View backgroundItem = ((LocationAdapter.LocationViewHolder) viewHolder).itemBackground;
                    getDefaultUIUtil().onSelected(backgroundItem);
                }
            }

            private void setAnimation(RecyclerView.ViewHolder viewHolder, float dX, int width) {
                if (viewHolder == null) return;
                FrameLayout frameLayout = ((LocationAdapter.LocationViewHolder) viewHolder).itemContainer;
                ImageView imageView = ((LocationAdapter.LocationViewHolder) viewHolder).itemDelete;

                if (dX == width) {
                    imageView.setVisibility(View.INVISIBLE);
                    imageView.setScaleX(0.8f);
                    imageView.setScaleY(0.8f);
                    frameLayout.setBackgroundColor(Color.TRANSPARENT);
                } else if (dX > width / 3.0) {
                    imageView.setVisibility(View.VISIBLE);
                    frameLayout.setBackgroundColor(Color.parseColor(FOCUSED_DELETE_BACKGROUND));
                    imageView.setColorFilter(Color.parseColor(FOCUSED_DELETE_COLOR));
                    imageView.setScaleX(1f);
                    imageView.setScaleY(1f);
                } else if (dX > width / 5.0) {
                    imageView.setVisibility(View.VISIBLE);
                    frameLayout.setBackgroundColor(Color.TRANSPARENT);
                    imageView.setColorFilter(Color.parseColor(DEFAULT_DELETE_COLOR));
                    imageView.setScaleX(0.8f);
                    imageView.setScaleY(0.8f);
                } else {
                    imageView.setVisibility(View.INVISIBLE);
                    imageView.setScaleX(0.8f);
                    imageView.setScaleY(0.8f);
                    frameLayout.setBackgroundColor(Color.TRANSPARENT);
                }
            }

            @Override
            public void onChildDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View backgroundItem = ((LocationAdapter.LocationViewHolder) viewHolder).itemBackground;
                setAnimation(viewHolder, dX, c.getWidth());
                getDefaultUIUtil().onDraw(c, recyclerView, backgroundItem, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onChildDrawOver(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View backgroundItem = ((LocationAdapter.LocationViewHolder) viewHolder).itemBackground;
                setAnimation(viewHolder, dX, c.getWidth());
                getDefaultUIUtil().onDraw(c, recyclerView, backgroundItem, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void clearView(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder) {
                View backgroundItem = ((LocationAdapter.LocationViewHolder) viewHolder).itemBackground;
                getDefaultUIUtil().clearView(backgroundItem);
            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(locationList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.close();
        }
    }
}
