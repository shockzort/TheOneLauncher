package com.example.shockzor.theonelauncher;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Shockzor on 12.12.2014.
 */
public class AppListAdapter extends ArrayAdapter<AppModel> {
    private final LayoutInflater mInflater;
    private int scaleDownPix;
    public AppListAdapter (Context context) {
        super(context, android.R.layout.simple_list_item_1);

        DisplayMetrics DM = context.getResources().getDisplayMetrics();
        scaleDownPix = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, DM));
        mInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<AppModel> data) {
        clear();
        if (data != null) {
            addAll(data);
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void addAll(Collection<? extends AppModel> items) {
        //If the platform supports it, use addAll, otherwise add in loop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            super.addAll(items);
        }else{
            for(AppModel item: items){
                super.add(item);
            }
        }
    }

    //Populate new items in the list.
    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = mInflater.inflate(R.layout.grid_entry, parent, false);
        } else {
            view = convertView;
        }

        AppModel item = getItem(position);
        ((TextView)view.findViewById(R.id.text)).setText(item.getLabel());
        Drawable icon = item.getIcon();

        icon.setBounds(0, 0, scaleDownPix, scaleDownPix);

        ((TextView) view.findViewById(R.id.text)).setCompoundDrawables(null, icon, null, null);
        return view;
    }
}
