package com.example.shockzor.theonelauncher;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v4.content.AsyncTaskLoader;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;

/**
 * Created by Shockzor on 12.12.2014.
 */
public class AppAsyncLoader  extends AsyncTaskLoader<ArrayList<AppModel>> {
    ArrayList<AppModel> mInstalledApps;

    final PackageManager mPm;

    public AppAsyncLoader(Context context) {
        super(context);

        mPm = context.getPackageManager();
    }

    @Override
    public ArrayList<AppModel> loadInBackground() {
        // retrieve the list of installed applications
        List<ApplicationInfo> apps = mPm.getInstalledApplications(0);

        if (apps == null) {
            apps = new ArrayList<>();
        }

        final Context context = getContext();

        // create corresponding apps and load their labels
        ArrayList<AppModel> items = new ArrayList<>(apps.size());
        int i = 0;
        while (i < apps.size()) {
            String pkg = apps.get(i).packageName;

            // only apps which are launchable
            if (context.getPackageManager().getLaunchIntentForPackage(pkg) != null) {
                AppModel app = new AppModel(context, apps.get(i));
                app.loadLabel(context);
                items.add(app);
            }
            i++;
        }

        // sort the list
        Collections.sort(items, ALPHA_COMPARATOR);
        return items;
    }

    @Override
    public void deliverResult(ArrayList<AppModel> apps) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (apps != null) {
                onReleaseResources(apps);
            }
        }

        ArrayList<AppModel> oldApps = apps;
        mInstalledApps = apps;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(apps);
        }

        // At this point we can release the resources associated with
        // 'oldApps' if needed;

        if (oldApps != null) {
            onReleaseResources(oldApps);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mInstalledApps != null) {
            // If we currently have a result available, deliver it

            deliverResult(mInstalledApps);
        }

        if (takeContentChanged() || mInstalledApps == null ) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    public void onCanceled(ArrayList<AppModel> apps) {
        super.onCanceled(apps);

        // At this point we can release the resources associated with 'apps'

        onReleaseResources(apps);
    }

    @Override
    protected void onReset() {
        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mInstalledApps != null) {
            onReleaseResources(mInstalledApps);
            mInstalledApps = null;
        }
    }

    // Helper method to do the cleanup work if needed

    protected void onReleaseResources(ArrayList<AppModel> apps) {
        // do nothing
    }

    // Perform alphabetical comparison of application entry objects.

    public static final Comparator<AppModel> ALPHA_COMPARATOR = new Comparator<AppModel>() {
        private final Collator sCollator = Collator.getInstance();
        @Override
        public int compare(AppModel object1, AppModel object2) {
            return sCollator.compare(object1.getLabel(), object2.getLabel());
        }
    };
}