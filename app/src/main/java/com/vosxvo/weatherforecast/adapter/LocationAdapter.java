package com.vosxvo.weatherforecast.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.vosxvo.weatherforecast.MainActivity;
import com.vosxvo.weatherforecast.R;
import com.vosxvo.weatherforecast.database.LocationDbHelper;
import com.vosxvo.weatherforecast.model.LocationItem;
import com.vosxvo.weatherforecast.preferences.PreferencesHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private List<LocationItem> list;
    private LocationDbHelper dbHelper;
    private PreferencesHelper preferencesHelper;
    private Context context;

    public LocationAdapter(Context context) {
        dbHelper = new LocationDbHelper(context);
        preferencesHelper = new PreferencesHelper(context, Context.MODE_PRIVATE);
        this.context = context;
        putData();
    }

    public void putData() {
        list = new ArrayList<>();
        list.add(new LocationItem("Your location", 0.0, 0.0));
        Cursor data = dbHelper.selectRecord();
        if (data == null) return;
        while (data.moveToNext()) {
            list.add(new LocationItem(
                    data.getString(data.getColumnIndexOrThrow(dbHelper.getColumnNameAddress())),
                    data.getDouble(data.getColumnIndexOrThrow(dbHelper.getColumnNameLatitude())),
                    data.getDouble(data.getColumnIndexOrThrow(dbHelper.getColumnNameLongitude()))
            ));
        }
    }

    public List<LocationItem> getList() {
        return list;
    }

    @NonNull
    @NotNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull LocationAdapter.LocationViewHolder holder, int position) {
        holder.locationItem.setText(list.get(position).getName());

        holder.locationItem.setOnClickListener(v -> {
            onItemClicked(position);
            ((MainActivity) context).updateWeather();
//            Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT).show();
        });
    }

    private void onItemClicked(int position) {
        if (list == null && list.size() <= position) return;
        LocationItem item = list.get(position);
        Bundle bundle = new Bundle();
        if (position != 0) {
            bundle.putBoolean("uses_location", false);
            bundle.putDouble("coord.lat", item.getLat());
            bundle.putDouble("coord.lon", item.getLon());
        } else {
            bundle.putBoolean("uses_location", true);
        }
        preferencesHelper.savePreferences(bundle);
        ((MainActivity) context).backToMainScreen();
    }

    public void close() {
        context = null;
        dbHelper.close();
        preferencesHelper.close();
    }

    @Override
    public int getItemCount() {
        if (list != null) return list.size();
        return 0;
    }

    public void insertItem(LocationItem item) {
        if (dbHelper == null || list == null) return;
        dbHelper.insertRecord(item.getLat(), item.getLon(), item.getName());
        list.add(item);
        notifyItemInserted(list.size() - 1);
    }

    public void removeItem(int position) {
        if (dbHelper == null || list == null) return;
        dbHelper.deleteRecord(String.valueOf(position));
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(int position, LocationItem deleted) {
        if (dbHelper == null || list == null) return;
        dbHelper.insertRecord(deleted.getLat(), deleted.getLon(), deleted.getName());
        list.add(position, deleted);
        notifyItemInserted(position);
    }

    public static class LocationViewHolder extends RecyclerView.ViewHolder {
        public FrameLayout itemContainer;
        public ImageView itemDelete;
        public LinearLayout itemBackground;
        public TextView locationItem;

        public LocationViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            itemContainer = itemView.findViewById(R.id.item_container);
            itemDelete = itemView.findViewById(R.id.item_delete);
            itemBackground = itemView.findViewById(R.id.item_background);
            locationItem = itemView.findViewById(R.id.location_item);
        }
    }
}
