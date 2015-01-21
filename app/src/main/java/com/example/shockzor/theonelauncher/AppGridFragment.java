package com.example.shockzor.theonelauncher;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * Created by Shockzor on 12.12.2014.
 */
public class AppGridFragment extends GridFragment implements LoaderManager.LoaderCallbacks<ArrayList<AppModel>> {

    AppListAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText("No Applications");

        mAdapter = new AppListAdapter(getActivity());
        setGridAdapter(mAdapter);

        // till the data is loaded display a spinner
        setGridShown(false);

        // create the loader to load the apps list in background
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<ArrayList<AppModel>> onCreateLoader(int id, Bundle bundle) {
        return new AppAsyncLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<AppModel>> loader, ArrayList<AppModel> apps) {
        mAdapter.setData(apps);

        if (isResumed()) {
            setGridShown(true);
        } else {
            setGridShownNoAnimation(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<AppModel>> loader) {
        mAdapter.setData(null);
    }

    @Override
    public void onGridItemClick(GridView g, View v, int position, long id) {
        AppModel app = (AppModel) getGridAdapter().getItem(position);
        if (app != null) {
            Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(app.getApplicationPackageName());
            if (intent != null) {
                startActivity(intent);
            }
        }
    }

    @Override
    public void onGridItemLongClick(GridView g, View v, int position, long id) {
        //AppModel app = (AppModel) getGridAdapter().getItem(position);

        //final Context ctx = getActivity();

        //View headerView = View.inflate(this.getActivity(), R.layout.activity_home, null);
        //RelativeLayout homeView = (RelativeLayout) headerView.findViewById(R.id.home_view);
        //RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(v.getWidth(),v.getHeight());
        //lp.leftMargin = (int) v.getX();
        //lp.topMargin = (int) v.getY();

        //LayoutInflater li = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //LinearLayout ll = (LinearLayout) li.inflate(R.layout.grid_entry, null);

        //((TextView)ll.findViewById(R.id.text)).setText(app.getLabel());
        //Drawable icon = app.getIcon();

        //((TextView) ll.findViewById(R.id.text)).setCompoundDrawables(null, icon, null, null);


        //((TextView)ll.findViewById(R.id.text)).setText(((TextView)v.findViewById(R.id.text)).getText());

        //homeView.addView(ll, lp);

        Intent intent = new Intent();
        intent.setAction("shockzor.theonelauncher.ADD_SHORTCUT");
        intent.putExtra("app", position);

        getActivity().sendBroadcast(intent);

        startActivity(intent);
    }
}
