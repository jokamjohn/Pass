package johnkagga.me.pass;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseUser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    //RequestCodes
    public final static int TAKE_PHOTO_CODE = 0;
    public final static int TAKE_VIDEO_CODE = 1;
    public final static int CHOOSE_PHOTO_CODE = 2;
    public final static int CHOOSE_VIDEO_CODE = 3;

    public final static int MEDIA_TYPE_IMAGE = 4;
    public final static int MEDIA_TYPE_VIDEO = 5;

    private Uri mMediaUri;

    private DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which)
            {
                case 0://Take Picture
                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                    //check if SD is not null
                    if (mMediaUri != null) {
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        startActivityForResult(takePhotoIntent, TAKE_PHOTO_CODE);

                    }
                    else {
                        Toast.makeText(MainActivity.this, R.string.camera_toast_error_msg,Toast.LENGTH_LONG)
                                .show();
                    }
                    break;
                case 1: //Take Video
                    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
                    //check if SD is not null
                    if (mMediaUri != null) {
                        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,10);//10 seconds
                        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0);//lowest quality
                        startActivityForResult(takeVideoIntent, TAKE_VIDEO_CODE);

                    }
                    else {
                        Toast.makeText(MainActivity.this, R.string.camera_toast_error_msg,Toast.LENGTH_LONG)
                                .show();
                    }
                    break;
                case 2: //Choose image
                    Intent chooseImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    chooseImageIntent.setType("image/*");
                    startActivityForResult(chooseImageIntent,CHOOSE_PHOTO_CODE);
                    break;
                case 3: //Choose video
                    break;
            }
        }

        /** Create a File for saving an image or video */
        private File getOutputMediaFile(int mediaType) {
            // To be safe, you should check that the SDCard is mounted
            // using Environment.getExternalStorageState() before doing this.
            if (isExternalStorageAvailable())
            {
                //create the file path
                // 1. Get the external storage directory
                String appName = MainActivity.this.getString(R.string.app_name);
                File mediaStorageDir = new File(Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        appName);

                // 2.create our sub directory if it does not exist
                if (!mediaStorageDir.exists())
                {
                    if (!mediaStorageDir.mkdirs())
                    {
                        //fails to make a directory
                        Log.e(LOG_TAG,appName + "failed to make a directory");
                        return null;
                    }
                }

                // 3. create a file name
                Date now = new Date();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(now);
                File mediaFile;

                if (mediaType == MEDIA_TYPE_IMAGE)
                {
                    mediaFile = new File (mediaStorageDir.getPath() + File.separator
                            + "IMG_" + timeStamp + ".jpg");
                }
                else if (mediaType == MEDIA_TYPE_VIDEO)
                {
                    mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
                }
                else {
                    //media file not created
                    return null;
                }
                return mediaFile;
            }
            else {
                return null;
            }
        }

        /** Create a file Uri for saving an image or video */
        private Uri getOutputMediaFileUri(int mediaType) {
            Log.v(LOG_TAG,"File: " + Uri.fromFile(getOutputMediaFile(mediaType)));
            return Uri.fromFile(getOutputMediaFile(mediaType));
        }

        /**
         * Check whether there is external storage
         * @return Boolean
         */
        private boolean isExternalStorageAvailable()
        {
            String state = Environment.getExternalStorageState();
            return state.equals(Environment.MEDIA_MOUNTED);
        }
    };
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null)
        {
            startLoginActivity();
        }
        else {
            Log.i(LOG_TAG,currentUser.getUsername());
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this,getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
                if (requestCode == CHOOSE_PHOTO_CODE || requestCode == CHOOSE_VIDEO_CODE) {
                    if (data == null)
                    {
                        Toast.makeText(this,R.string.general_error,Toast.LENGTH_LONG)
                                .show();
                    }
                    else {
                        //get the uri of the media
                        mMediaUri = data.getData();
                    }
                } else {
                    //Add the image to the gallery
                    //Broadcast an intent for new media
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(mMediaUri);
                    sendBroadcast(mediaScanIntent);
                    Log.i(LOG_TAG, "saved image to gallery");
                }
        }
        else if (resultCode != RESULT_CANCELED)
        {
            Toast.makeText(this, R.string.general_error, Toast.LENGTH_LONG)
                        .show();
        }

    }

    /**
     * Start the login activity
     * The flags remove the MainActivity from the back stack
     */
    private void startLoginActivity() {
        Intent intent = new Intent(this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_logOut:
                ParseUser.logOut();
                startLoginActivity();
                break;
            case R.id.action_edit_friends:
                Intent intent = new Intent(this, EditFriendsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_camera:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(R.array.camera_choices,mDialogListener);
                AlertDialog dialog = builder.create();
                dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

}
