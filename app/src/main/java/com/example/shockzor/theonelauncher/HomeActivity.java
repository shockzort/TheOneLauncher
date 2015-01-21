package com.example.shockzor.theonelauncher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Shockzor on 12.12.2014.
 */

public class HomeActivity extends Activity implements DialogInterface.OnClickListener{

    private static final int SELECT_PHOTO = 100;
    private BroadcastReceiver shortcutIntentReciever;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slideuppanel);

        IntentFilter filter = new IntentFilter("shockzor.theonelauncher.ADD_SHORTCUT");
        shortcutIntentReciever = new Receiver();
        this.registerReceiver(shortcutIntentReciever, filter);
    }


    public void showApps(View v){
        /** Call activity to show an app list */
        Intent i = new Intent(this, AppListActivity.class);

        startActivity(i);
    }

    public void showMessenger(View v){
        /** Creating an intent with default sms-mms app call  */
        Intent smsIntent = new Intent();
        smsIntent.setAction(Intent.ACTION_SEND);
        smsIntent.setType("text/plain");

        /** Verify that the intent will resolve to an activity */
        if (smsIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(smsIntent);
        }
    }

    public void showDialer(View v){
        /** Creating an intent with the dialer call */
        Intent intent = new Intent("android.intent.action.DIAL");

        /** Starting the Dialer activity */
        startActivity(intent);
    }

    public void showSkinDialog(View view)
    {
        // TODO add code for fetching available skins
        String choose[] = {"Basic","Alternative"};

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setTitle("Choose skin");
        dialogBuilder.setSingleChoiceItems(choose, 0, null);
        dialogBuilder.setPositiveButton("OK",this);
        dialogBuilder.setNegativeButton("Cancel", null);

        /** Creating the alert dialog window using the builder class */
        AlertDialog skinDialog = dialogBuilder.create();
        skinDialog.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        AlertDialog alert = (AlertDialog)dialog;
        int position = alert.getListView().getCheckedItemPosition();
        changeSkin(position);
    }

    public void changeWallpaper(View v){
        /** Creating an intent with image chooser intent */

        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");

        Intent chooser = Intent.createChooser(photoPickerIntent, "Choose program for image pick");

        /** Verify the original intent will resolve to at least one activity */
        if ( photoPickerIntent.resolveActivity( getPackageManager() ) != null) {
            startActivityForResult(chooser, SELECT_PHOTO);
        }
    }

    public void changeSkin(int skinId) {
        ImageButton callBtn = (ImageButton) findViewById(R.id.call_button);
        ImageButton smsBtn = (ImageButton) findViewById(R.id.sms_button);
        ImageButton homeBtn = (ImageButton) findViewById(R.id.apps_button);

        switch(skinId)
        {
            case 0:
                callBtn.setBackgroundResource(R.drawable.btn_call_new);
                smsBtn.setBackgroundResource(R.drawable.btn_sms_new);
                homeBtn.setBackgroundResource(R.drawable.btn_home_new);
                break;
            case 1:
                callBtn.setBackgroundResource(R.drawable.btn_call);
                smsBtn.setBackgroundResource(R.drawable.btn_sms);
                homeBtn.setBackgroundResource(R.drawable.btn_home);
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        /** If activity result is successful pick */
        if(requestCode == SELECT_PHOTO &&
           resultCode == RESULT_OK) {
            Uri selectedImage = imageReturnedIntent.getData();
            setWallpaper(selectedImage);
        }
    }

    private void setWallpaper(Uri newWallpaper) {
        Bitmap UserSelectedImage = null;

        /** Try to get a bitmap from Uri */
        UserSelectedImage = getDownsampledBitmap(newWallpaper);

        /** Try set up a bitmap as wallpaper */
        WallpaperManager myWallpaperManager
                = WallpaperManager.getInstance(getApplicationContext());
        try {
            myWallpaperManager.setBitmap(UserSelectedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Get device screen dimensions in pixels */
    private Point getScreenDims() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        Point screenDims = new Point();

        screenDims.y = metrics.heightPixels;
        screenDims.x = metrics.widthPixels;

        return screenDims;
    }

    private Bitmap getDownsampledBitmap(Uri uri) {
        Bitmap bitmap = null;
        Point screenDims = getScreenDims();
        try {
            BitmapFactory.Options outDimens = getBitmapDimensions(uri);
            /** Get the downsampling ratio */
            int sampleSize = calculateSampleSize(outDimens.outWidth, outDimens.outHeight, screenDims.x, screenDims.y);

            /** Load the downsampled bitmap */
            bitmap = downsampleBitmap(uri, sampleSize);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private BitmapFactory.Options getBitmapDimensions(Uri uri) throws FileNotFoundException, IOException {
        BitmapFactory.Options outDimens = new BitmapFactory.Options();
        outDimens.inJustDecodeBounds = true; // the decoder will return null (no bitmap)

        InputStream imageStream = getContentResolver().openInputStream(uri);
        BitmapFactory.decodeStream(imageStream, null, outDimens);
        imageStream.close();

        return outDimens;
    }

    private int calculateSampleSize(int width, int height, int targetWidth, int targetHeight) {
        int inSampleSize = 1;

        if (height > targetHeight || width > targetWidth) {

            /** Calculate downscale ratios */
            final int heightRatio = Math.round((float) height
                    / (float) targetHeight);
            final int widthRatio = Math.round((float) width / (float) targetWidth);

            /** Choose the proper scaling ratio */
            inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    private Bitmap downsampleBitmap(Uri uri, int sampleSize) throws FileNotFoundException, IOException {
        BitmapFactory.Options outBitmap = new BitmapFactory.Options();
        outBitmap.inJustDecodeBounds = false;
        outBitmap.inSampleSize = sampleSize;

        InputStream imageStream = getContentResolver().openInputStream(uri);
        Bitmap resizedBitmap = BitmapFactory.decodeStream(imageStream, null, outBitmap);
        imageStream.close();

        return resizedBitmap;
    }

    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {

           /* int appHash = arg1.getIntExtra("app", 0);
            List<ApplicationInfo> apps = getPackageManager().getInstalledApplications(0);
            ApplicationInfo app = apps.get(appHash);

            View headerView = View.inflate(getApplication(), R.layout.activity_home, null);
            RelativeLayout homeView = (RelativeLayout) headerView.findViewById(R.id.home_view);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(homeView.getWidth(),homeView.getHeight());
            lp.leftMargin = (int) 50;
            lp.topMargin = (int) 50;

            LayoutInflater li = (LayoutInflater) arg0.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout ll = (LinearLayout) li.inflate(R.layout.grid_entry, null);

            Drawable icon = app.loadIcon(getPackageManager());

            CharSequence label = app.loadLabel(getPackageManager());
            String mAppLabel = label != null ? label.toString() : app.packageName;

            ((TextView) ll.findViewById(R.id.text)).setText(mAppLabel);
            ((TextView) ll.findViewById(R.id.text)).setCompoundDrawables(null, icon, null, null);

            homeView.addView(ll, lp);

            headerView.bringToFront();
            homeView.bringToFront();*/
        }
    }

    @Override
    protected void onDestroy(){
        this.unregisterReceiver(shortcutIntentReciever);
    }
}