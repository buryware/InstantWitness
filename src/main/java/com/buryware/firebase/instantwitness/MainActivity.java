/**
 * Copyright Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Instant Witnesses is written by Steve Stansbury  for Jason Nelson
 *
 * Created May 25, 2018 by the Buryware.
 */
package com.buryware.firebase.instantwitness;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.exoplayer2.*;
//import com.google.android.gms.ads.AdView;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.security.ProviderInstaller;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Indexables;
import com.google.firebase.appindexing.builders.PersonBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.firekast.FKCamera;
import io.firekast.FKCameraFragment;
import io.firekast.FKError;
import io.firekast.FKPlayerView;
import io.firekast.FKStream;
import io.firekast.FKStreamer;

import static android.os.SystemClock.sleep;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2CeJZ6zXKJ2z4teko8R2600eXILr8b3dKasWUXZDctuwH8f+HEyju56MJt/A/RekXc+x/+jLe3tfZM+vB6L7XXhjGIbmlHSy0TGmIa0RCQuncHfnYwVgPoBtASN/k+F3q/xcqhi2mJL60iQrP8rgl1bmjNRMmJy85owq8h8cjtCQWDCapW+MZLqjbtW/MFzaE8kL9x7wPs56sMFn0WbSlxgFsqkXHkPHKxQyG++RW1BACLOYEc+Bh5UZIyiR/+nbkanWMFgG3rZRqjmIxhGSH8Ok/7To7rSwzYQYcDphZ7+bluh6S/36ngk7qDg8A9GiXdxxUoPrl5KkY/uInRZmlwIDAQAB";

    private static final String TAG = "MainActivity";
    public static final String MESSAGES_CHILD = "messages";
    private static final int REQUEST_INVITE = 1;
    private static final int REQUEST_IMAGE = 2;

    private static final int REQUEST_PIN_SET = 3;
    private static final int REQUEST_PIN_CODE_RESET = 4;
    private static final int REQUEST_PIN_CODE_SOUND = 5;

    private static final int REQUEST_PIN_CODE = 8;

    public static final int DEFAULT_MSG_LENGTH_LIMIT = 80;
    private static final int TWENTY_MINUTES = 1000 * 60 * 20;
    public static final String ANONYMOUS = "anonymous";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private static final String MESSAGE_URL = "http://friendlychat.firebase.google.com/message/";
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PERMISSION_REQUEST_GPS = 2;

    private int friendlyMessageCount;
    private String mUsername;
    private String mPhotoUrl;
    private String mStreamID = null;
    private String mPlayStreamID = null;
    private String mUserPinCode = null;
    private String mCurrentHelpID = null;
    private String mCurrentRescueID = null;
    private String mLocationProvider;
    private LocationManager mLocationManager;
    private Location mLastKnownLocation;
    private Location mCurrentLocation;
    private LatLng debugLatLng = null;
    private LatLng mSelectedHelpGPs = null;
    private View mSeletectedHelpMsgView = null;
    private View mSeletectedRescueMsgView = null;

    private LocationListener mLocationListener;
    private SharedPreferences mSharedPreferences;

    private Button mHelpButton;
    private Button mRescueButton;
    private Button m911Button;
    private Button mResetButton;
    private Button mSoundButton;

    private ProgressBar mProgress;
    private GoogleMap mMap;
    private Boolean mEMSPlaying = false;

    private Menu mMenu = null;

    private Bundle savedMainInstanceState;

    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FrameLayout mFramelayout = null;
    private FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder> mFirebaseAdapter;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseAnalytics mFirebaseAnalytics;

    //private AdView mAdView;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private GoogleApiClient mGoogleApiClient;
    private Boolean bMute = false;
    private int mActivated = 0;

    private Boolean bHelp = false;
    private Boolean bRescue = false;
    private Bitmap mHelpCapture;

    FKCamera mCamera = null;
    FKStreamer mStreamer = null;
    FKStream mStream = null;
    FKCameraFragment mCameraFragment = null;
    FKPlayerView mPlayerView = null;
    FragmentManager fragmentManager = null;

    private static final int PIN_CODE_SET = 3;
    private static final int PIN_CODE_RESET = 4;
    private static final int PIN_CODE_SOUND = 5;

    private static final int PIN_CODE_OK = 6;
    private static final int PIN_CODE_WRONG = 7;

    AudioPlayer mAudioPlayer = null;
    Intent PinCodeViewIntent = null;

    private GoogleApiClient client;
    private List<Marker> IWmarkers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {

            mUserPinCode = savedInstanceState.getParcelable("pin_number");
        }

        setContentView(R.layout.activity_main);

        // Ensure an updated security provider is installed into the system when a new one is
        // available via Google Play services.
        try {
            ProviderInstaller.installIfNeededAsync(getApplicationContext(),
                    new ProviderInstaller.ProviderInstallListener() {
                        @Override
                        public void onProviderInstalled() {
                             //Toast("New security provider installed.");
                            installPlayServiceSecurityUpdates();
                        }

                        @Override
                        public void onProviderInstallFailed(int errorCode, Intent intent) {
                        //    ConsoleMessage.MessageLevel.LOG(TAG, "New security provider install failed.");
                            // No notification shown there is no user intervention needed.
                        }
                    });
        } catch (Exception ignorable) {
            // ConsoleMessage.MessageLevel.LOG(TAG, "Unknown issue trying to install a new security provider.", ignorable);
        }

        IWmarkers = new ArrayList<Marker>();

        savedMainInstanceState = savedInstanceState;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUsername = ANONYMOUS;

        mAudioPlayer = new AudioPlayer();
        mEMSPlaying = false;

        mLocationProvider = LocationManager.GPS_PROVIDER;
        if (mLocationProvider.isEmpty()) {
            mLocationProvider = LocationManager.NETWORK_PROVIDER;
        }

        mLocationManager = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestForGPSPermission();
        }

        // Define a listener that responds to location updates
        mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        mCurrentLocation = new Location (String.valueOf(getLocationLatLong()));

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */).addApi(Auth.GOOGLE_SIGN_IN_API).build();

        mMessageRecyclerView = findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        mFramelayout = findViewById(R.id.fragmentContainer);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        SnapshotParser<FriendlyMessage> parser = new SnapshotParser<FriendlyMessage>() {
            @Override
            public FriendlyMessage parseSnapshot(DataSnapshot dataSnapshot) {
                FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                if (friendlyMessage != null) {
                    friendlyMessage.setId(dataSnapshot.getKey());
                }
                return friendlyMessage;
            }
        };

        DatabaseReference messagesRef = mFirebaseDatabaseReference.child(MESSAGES_CHILD);

        FirebaseRecyclerOptions<FriendlyMessage> options = new FirebaseRecyclerOptions.Builder<FriendlyMessage>().setQuery(messagesRef, parser).build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>(options) {

            @Override
            public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new MessageViewHolder(inflater.inflate(R.layout.item_message, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(final MessageViewHolder viewHolder, int position, FriendlyMessage friendlyMessage) {

                if (friendlyMessage.getText() != null) {
                    viewHolder.messageTextView.setText(friendlyMessage.getText());

                    if (friendlyMessage.getmsgType().equals("HELP")) {
                        viewHolder.messageTextView.setTextColor(Color.RED);

                        if (mCurrentHelpID.equals(friendlyMessage.getStreamID()) && bHelp) {   // select help on help device

                            onMapAddHelpGPSSelected(friendlyMessage.getLatLng());

                        } else {

                            onMapAddHelpGPS(friendlyMessage.getLatLng());
                        }

                        if (mSeletectedHelpMsgView != null) {
                            mSeletectedHelpMsgView.setBackgroundColor(Color.WHITE);
                        }

                        if (!bHelp) {

                            mPlayStreamID = friendlyMessage.getStreamID();
                            mSelectedHelpGPs = friendlyMessage.getLatLng();
                            mSeletectedHelpMsgView = viewHolder.messageTextView;
                            mSeletectedHelpMsgView.setBackgroundColor(Color.YELLOW);
                        }
                    }
                    if (friendlyMessage.getmsgType().equals("RESCUE")) {
                        viewHolder.messageTextView.setTextColor(Color.BLUE);

                        if (mPlayStreamID.equals(friendlyMessage.getStreamID()) && bRescue) {   // select rescue on rescue device

                            onMapAddRescueGPSSelected(friendlyMessage.getLatLng());   // show close by possible rescuers
                        } else {

                            onMapAddRescueGPS(friendlyMessage.getLatLng());   // show close by possible rescuers
                        }

                        if (mCurrentHelpID.equals(friendlyMessage.getStreamID())) {   // select help on help device
                            if (!mEMSPlaying) {

                                mEMSPlaying = true;
                                mAudioPlayer.play(getApplicationContext(), R.raw.ems_alert);
                            }
                        }

                        if (mSeletectedHelpMsgView != null) {

                            mSeletectedHelpMsgView.setBackgroundColor(Color.WHITE);
                        }
                    }
                    viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
                    viewHolder.messageTextView.setGravity(Gravity.START);
                }

                if (friendlyMessage.getText() != null) {
                    // write this message to the on-device index
                    FirebaseAppIndex.getInstance().update(getMessageIndexable(friendlyMessage));
                }

                // log a view action on it
                FirebaseUserActions.getInstance().end(getMessageViewAction(friendlyMessage));
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 || (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                    mMessageRecyclerView.setBackgroundColor(Color.WHITE);
                }
            }
        });

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        // Initialize Firebase Measurement.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Initialize Firebase Remote Config.
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // Define Firebase Remote Config Settings.
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings = new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(true).build();

        // Define default config values. Defaults are used when fetched config values are not
        // available. Eg: if an error occurred fetching values from the server.
        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put("friendly_msg_length", 120L);

        // Apply config settings and default values.
        mFirebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);

        // Fetch remote config.
        fetchConfig();

        mProgress = findViewById(R.id.progressBar);

        mHelpButton = findViewById(R.id.HelpButton);
        mHelpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (bHelp) {

                    Toast toast = Toast.makeText(getApplication().getBaseContext(), R.string.HelpInProgress, Toast.LENGTH_LONG);
                    toast.show();

                } else if (bRescue) {

                    Toast toast = Toast.makeText(getApplication().getBaseContext(), R.string.RescueInProgress, Toast.LENGTH_LONG);
                    toast.show();

                } else {

                    mProgress.setVisibility(View.VISIBLE);
                    bHelp = true;

                    mCameraFragment = new FKCameraFragment.Builder().setCameraPosition(FKCamera.Position.FRONT).build();

                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().add(R.id.fragmentContainer, mCameraFragment).commit();
                    fragmentManager.beginTransaction().replace(R.id.fragmentContainer, mCameraFragment).commit();

                    mCameraFragment.getCameraAsync(new FKCameraFragment.OnCameraReadyCallback() {
                        @Override
                        public void onCameraReady(@Nullable FKCamera fkCamera, @Nullable FKStreamer fkStreamer, @Nullable FKError fkError) {
                            mCamera = fkCamera;
                            mStreamer = fkStreamer;
                            mCamera.setMicrophoneEnabled(true);
                            mCamera.capturePreview(new MyCameraPrevueCallback());

                            mMenu.getItem(4).setEnabled(true);   // Camera front
                            mMenu.getItem(5).setEnabled(true);   // Camera back
                            mMenu.getItem(6).setEnabled(false);   // Flash on
                            mMenu.getItem(7).setEnabled(false);   // Flash off

                            mStreamer.requestStream(new FKStreamer.RequestStreamCallback() {
                                @Override
                                public void done(@Nullable FKStream fkStream, @Nullable FKError error) {

                                    mProgress.setVisibility(View.GONE);
                                    mStream = fkStream;
                                    mStreamID = mStream.getId();
                                    mStreamer.startStreaming(fkStream, new MyFKStreamingCallback());
                                }
                            });
                        }
                    });
                }
            }
        });

        mRescueButton = findViewById(R.id.RescueButton);

        mRescueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (bHelp) {

                    Toast toast = Toast.makeText(getApplication().getBaseContext(), R.string.HelpInProgress, Toast.LENGTH_LONG);
                    toast.show();

                } else if (bRescue) {

                    Toast toast = Toast.makeText(getApplication().getBaseContext(), R.string.RescueInProgress, Toast.LENGTH_LONG);
                    toast.show();

                } else {

                    if (mPlayStreamID != null && !mPlayStreamID.isEmpty()) {

                        mProgress.setVisibility(View.VISIBLE);
                        bRescue = true;

                        View mainContainer = findViewById(R.id.fragmentContainer);

                        mPlayerView = new FKPlayerView(getApplication().getBaseContext());
                        mPlayerView.setLayoutParams(mainContainer.getLayoutParams());     // to get the right size for the player

                        ViewGroup parent = (ViewGroup) mainContainer.getParent();
                        int index = parent.indexOfChild(mainContainer);
                        parent.removeView(mainContainer);
                        parent.addView(mPlayerView, index);

                        mPlayerView.setPlayerListener(new MyFKPlayerCallback());
                     //   mPlayerView.getExoPlayerView(); // Give access to ExoPlayerView
                     //   mPlayerView.setControllerAutoShow(false); // For example

                        ActionRescue();

                        mPlayerView.play(mPlayStreamID);

                    } else {

                        Toast toast = Toast.makeText(getApplication().getBaseContext(), R.string.NoStreamReply, Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            }
        });

        m911Button = findViewById(R.id.a911Button);
        m911Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Call911Number();
            }
        });

        mResetButton = findViewById(R.id.ResetButton);
        mResetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                mProgress.setVisibility(View.GONE);

                ActionReset();
            }
        });

        mSoundButton = findViewById(R.id.SoundButton);
        mSoundButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // change the image
                if (bMute) {

                    mSoundButton.setText(R.string.SoundButtonText);
                } else {

                    PinCodeUI(REQUEST_PIN_CODE_SOUND);
                }
                bMute = !bMute;
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     *  installing latest security fixes throught play services, e.g. TLS1.2 support.
     *  But dont push anybody by annoying messages to upgrade Play Services, we dont rely on it.
     */
    private void installPlayServiceSecurityUpdates() {
        try {

            ProviderInstaller.installIfNeeded(getApplicationContext());

        } catch (Exception e ) {
            Log.e(TAG, "Error installing play service features", e);
        }
    }

    private float calculateDistance(double lat1, double lon1, double lat2, double lon2) {

        float[] distance = new float[2];
        Location.distanceBetween(lat1, lon1, lat2, lon2, distance);

        return distance[0];
    }

    class MyCameraPrevueCallback implements  FKCamera.CaptureCallback{

        @Override
        public void onCaptureReady(@NonNull Bitmap bitmap) {

            mHelpCapture = bitmap;  // todo?
        }
    }

    class MyFKStreamingCallback implements FKStreamer.StreamingCallback {
        @Override
        public void onSteamWillStartUnless(@Nullable FKStream fkStream, @Nullable FKError fkError) {

            if (fkError != null) {

                Toast.makeText(getApplicationContext(), "Error streaming: " + fkError, Toast.LENGTH_LONG).show();
                return;
            }

            mProgress.setVisibility(View.GONE);
            mStream = fkStream;

            ActionHelp();
        }

        @Override
        public void onStreamDidStop(@Nullable FKStream fkStream, FKError fkError) {
            if (fkError != null) {

                Toast.makeText(getApplication().getBaseContext(), "Error streaming: " + fkError.toString(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onStreamingUpdateAvailable(boolean b) {
        }
    }

    class MyFKPlayerCallback implements FKPlayerView.Callback {
        @Override
        public void onPlayerWillPlay(@Nullable FKStream fkStream, @Nullable FKError fkError) {

            if (fkError != null) {

                Toast.makeText(getApplication().getBaseContext(), "Error playback: " + fkError.toString(), Toast.LENGTH_LONG).show();
            }

            mProgress.setVisibility(View.GONE);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker at our current location.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng mLatLng = getLocationLatLong();
        mMap.addMarker(new MarkerOptions().position(mLatLng).title("Current location:"));

        final float GEOFENCE_RADIUS = 1000.0f;

        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(mLatLng.latitude, mLatLng.longitude))
                .radius(GEOFENCE_RADIUS)
                .fillColor(R.color.colorGeoFence)
                .strokeColor(Color.LTGRAY)
                .strokeWidth(2);
        mMap.addCircle(circleOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng location) {

                onMapAddLocation(location);

            }
        });
    }

    public void onMapAddLocation(LatLng mNewLatLng) {

        if (BuildConfig.DEBUG)
            debugLatLng = mNewLatLng ;
    }

    public void onMapAddHelpGPSSelected(LatLng mLatLng) {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(mLatLng);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.helpgpselected));

        IWmarkers.add(mMap.addMarker(markerOptions));
    }

    public void onMapAddHelpGPS(LatLng mLatLng) {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(mLatLng);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.helpgps));

        IWmarkers.add(mMap.addMarker(markerOptions));
    }

    public void onMapAddRescueGPSSelected(LatLng mLatLng) {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(mLatLng);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.rescuegpselected));

        IWmarkers.add(mMap.addMarker(markerOptions));
    }

    public void onMapAddRescueGPS(LatLng mLatLng) {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(mLatLng);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.rescuegps));

        IWmarkers.add(mMap.addMarker(markerOptions));
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView messageTextView;

        public MessageViewHolder(View v) {
            super(v);
            itemView.setOnClickListener(this);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }

        @Override
        public void onClick(View v) {

            //Toast.makeText(v.getContext(), "position = " + getLayoutPosition(), Toast.LENGTH_SHORT).show();
            FriendlyMessage msg = mFirebaseAdapter.getItem(getLayoutPosition());

            if (msg.getmsgType().equals("HELP")) {

                Iterator<Marker> iter = IWmarkers.iterator();  // clear?
                while (iter.hasNext()) {

                    Marker smarker = iter.next();
                    smarker.setVisible(false);
                }

                mPlayStreamID = msg.getStreamID();
                mSelectedHelpGPs = msg.getLatLng();

                if (mSeletectedHelpMsgView != null) {

                    mSeletectedHelpMsgView.setBackgroundColor(Color.WHITE);  // reset the old one
                }
                mSeletectedHelpMsgView = v;
                mSeletectedHelpMsgView.setBackgroundColor(Color.YELLOW);

                IWmarkers.get(getLayoutPosition()).setVisible(true);
            }
        }
    }

    private LatLng getLocationLatLong() {
        LatLng mLatLong;

        if (mCurrentLocation != null && mCurrentLocation.getLatitude() != 0) {

            mLatLong = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

        } else if (mLastKnownLocation != null && mLastKnownLocation.getLatitude() != 0) {

            mLatLong = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());

        } else {

            GPSTracker gps = new GPSTracker(MainActivity.this);

            // Check if GPS enabled
            double longitude = 0;
            double latitude = 0;
            if (gps.canGetLocation()) {

                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
            }
            mLatLong = new LatLng(latitude, longitude);  // Crossroads
        }

        return mLatLong;
    }

    public void makeUseOfNewLocation(Location location) {
        if (isBetterLocation(location, mCurrentLocation)) {

            mLastKnownLocation = mCurrentLocation;
            mCurrentLocation = location;
        }
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWENTY_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWENTY_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 500;  // 200

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {

            return true;
        } else if (isNewer && !isLessAccurate) {

            return true;
        } else {

            boolean flag = isNewer && !isSignificantlyLessAccurate && isFromSameProvider;

            return flag;
        }
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {

            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.

        String mPinCodeState = savedInstanceState.getString("pincode_state");
        if (mPinCodeState != null && !mPinCodeState.isEmpty() && mPinCodeState.equals(PIN_CODE_OK)) {

            mUserPinCode = PinCodeViewIntent.getStringExtra("pincode_number");
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.

        String mPinCodeState = savedInstanceState.getString("pincode_state");
        if (mPinCodeState != null && !mPinCodeState.isEmpty() && mPinCodeState.equals(PIN_CODE_OK)) {

            mUserPinCode = PinCodeViewIntent.getStringExtra("pincode_number");
        }
    }

    public void ActionRescue() {

        String msg = (String) this.getResources().getText(R.string.rescue_msg);

        LatLng mLatLng = getLocationLatLong();
        Double lat = mLatLng.latitude;
        Double lng = mLatLng.longitude;

        if (BuildConfig.DEBUG) {
            if (debugLatLng != null) {
                lat = debugLatLng.latitude;
                lng = debugLatLng.longitude;
            }
        }

        String timeStamp = new SimpleDateFormat(getString(R.string.TimeStampSeed)).format(new Date());
        String mMsgID = getUniqueID(getString(R.string.MsgUniqueID));
        mCurrentRescueID = mPlayStreamID;

        FriendlyMessage friendlyMessage = new FriendlyMessage(mMsgID, msg, mUsername, lat.toString(), lng.toString(), mPlayStreamID, "RESCUE", timeStamp);
        mFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(friendlyMessage);
        mFirebaseAnalytics.logEvent(MESSAGE_SENT_EVENT, null);

        debugLatLng = null;
    }

    public void ActionHelp() {

        String msg = (String) this.getResources().getText(R.string.help_msg);

        LatLng mLatLng = getLocationLatLong();
        Double lat = mLatLng.latitude;
        Double lng = mLatLng.longitude;

        if (BuildConfig.DEBUG) {
            if (debugLatLng != null) {
                lat = debugLatLng.latitude;
                lng = debugLatLng.longitude;
            }
        }

        String timeStamp = new SimpleDateFormat( getString(R.string.TimeStampSeed)).format(new Date());
        String mMsgID = getUniqueID(getString(R.string.MsgUniqueID));
        mCurrentHelpID = mStreamID;

        FriendlyMessage friendlyMessage = new FriendlyMessage(mMsgID, msg, mUsername, lat.toString(), lng.toString(), mStreamID, "HELP", timeStamp);
        mFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(friendlyMessage);
        mFirebaseAnalytics.logEvent(MESSAGE_SENT_EVENT, null);

        debugLatLng = null;
    }

    String getUniqueID(String randseed) {

        UUID uid = UUID.fromString(randseed);

        return UUID.randomUUID().toString();
    }

    public void ActionReset() {

        mCurrentHelpID = null;
        mCurrentRescueID = null;
        mSelectedHelpGPs = null;
        if (mSeletectedHelpMsgView != null) {

            mSeletectedHelpMsgView.setBackgroundColor(Color.WHITE);
            mSeletectedHelpMsgView = null;
        }
        if ( mSeletectedRescueMsgView != null) {

            mSeletectedRescueMsgView.setBackgroundColor(Color.WHITE);
            mSeletectedRescueMsgView = null;
        }

        Iterator<Marker> iter = IWmarkers.iterator();
        while (iter.hasNext()) {

            Marker smarker = iter.next();
            smarker.setVisible(true);
        }

        try {
            if (mEMSPlaying) {

                mEMSPlaying = false;
                mAudioPlayer.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        bHelp = false;
        bRescue = false;

        mMenu.getItem(4).setEnabled(false);   // Camera front
        mMenu.getItem(5).setEnabled(false);   // Camera back
        mMenu.getItem(6).setEnabled(false);   // Flash on
        mMenu.getItem(7).setEnabled(false);   // Flash off

        try {
            if (mStreamer != null && mStreamer.isStreaming()) {

                mStreamer.stopStreaming();
                mStreamer = null;

                View mainContainer = findViewById(R.id.fragmentContainer);

                mCameraFragment.onStop();

                fragmentManager = getSupportFragmentManager();
                fragmentManager.executePendingTransactions();
                if (mainContainer != null) {
                    mainContainer.setBackgroundColor(Color.BLACK);
                }

                mCameraFragment.getFragmentManager().beginTransaction().remove(mCameraFragment).commit();
                mCameraFragment.onDestroy();
                mCameraFragment = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (mPlayerView != null && mPlayerView.isPlaying()) {

                mPlayerView.stop();
                mPlayerView.removeAllViews();
                mPlayerView = null;

                View mainContainer = findViewById(R.id.fragmentContainer);
                if (mainContainer != null) {
                    mainContainer.setBackgroundColor(Color.BLACK);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mProgress.setVisibility(View.GONE);
    }

    private Boolean IsSelf(FriendlyMessage friendlyMessage) {
        return (mUsername.equals(friendlyMessage.getName()));
    }

    public void Call911Number() {

        //   Uri callUri = Uri.parse("tel://911");
        Uri callUri = Uri.parse("tel://<911>");    // me
        Intent callIntent = new Intent(Intent.ACTION_CALL, callUri);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            requestForCallPermission();

        } else {

            startActivity(callIntent);
        }
    }

    public void requestForCallPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Uri callUri = Uri.parse("tel:1+555-555-5555");
                    Intent callIntent = new Intent(Intent.ACTION_CALL, callUri);
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                }
                break;

            case PERMISSION_REQUEST_GPS:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    try {
                        mLocationManager.requestLocationUpdates(mLocationProvider, 0, 0, mLocationListener);
                        mLastKnownLocation = mLocationManager.getLastKnownLocation(mLocationProvider);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public void requestForGPSPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_GPS);
    }

    private Action getMessageViewAction(FriendlyMessage friendlyMessage) {

        return new Action.Builder(Action.Builder.VIEW_ACTION).setObject(friendlyMessage.getName(), MESSAGE_URL.concat(friendlyMessage.getId())).setMetadata(new Action.Metadata.Builder().setUpload(false)).build();
    }

    private Indexable getMessageIndexable(FriendlyMessage friendlyMessage) {
        PersonBuilder sender = Indexables.personBuilder().setIsSelf(mUsername.equals(friendlyMessage.getName())).setName(friendlyMessage.getName()).setUrl(MESSAGE_URL.concat(friendlyMessage.getId() + "/sender"));

        PersonBuilder recipient = Indexables.personBuilder().setName(mUsername).setUrl(MESSAGE_URL.concat(friendlyMessage.getId() + "/recipient"));
        Indexable messageToIndex = Indexables.messageBuilder().setName(friendlyMessage.getText()).setUrl(MESSAGE_URL.concat(friendlyMessage.getId())).setSender(sender).setRecipient(recipient).build();

        return messageToIndex;
    }

    @Override
    public void onPause() {
   /*     if (mAdView != null) {
            mAdView.pause();
        }*/
        mFirebaseAdapter.stopListening();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
      /*  if (mAdView != null) {
            mAdView.resume();
        }*/
        mFirebaseAdapter.startListening();
    }

    @Override
    public void onDestroy() {
  /*      if (mAdView != null) {
            mAdView.destroy();
        }*/
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        mMenu = menu;

        mMenu.getItem(4).setEnabled(false);   // Camera front
        mMenu.getItem(5).setEnabled(false);   // Camera back
        mMenu.getItem(6).setEnabled(false);   // Flash on
        mMenu.getItem(7).setEnabled(false);   // Flash off

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.invite_menu:

                sendInvitation();
                return true;

            case R.id.sign_out_menu:

                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mFirebaseUser = null;
                mUsername = ANONYMOUS;
                mPhotoUrl = null;
                startActivity(new Intent(this, SignInActivity.class));
                finish();
                return true;

            case R.id.fresh_config_menu:

                fetchConfig();
                return true;

            case R.id.setpincode_menu:

                PinCodeUI(PIN_CODE_SET);
                return true;

            case R.id.camara_front:
                try {

                    mCamera.switchToPosition(FKCamera.Position.FRONT);

                    mMenu.getItem(4).setEnabled(false);   // Front off
                    mMenu.getItem(5).setEnabled(true);    // Back on
                    mMenu.getItem(6).setEnabled(false);   // Flash on
                    mMenu.getItem(7).setEnabled(false);   // Flash off

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;

            case R.id.camara_back:
                try {

                    mCamera.switchToPosition(FKCamera.Position.BACK);

                    mMenu.getItem(4).setEnabled(true);   // Front on
                    mMenu.getItem(5).setEnabled(false);  // Back off

                    if (mCamera.isFlashAvailable()) {
                        mMenu.getItem(6).setEnabled(true);   // Flash on
                        mMenu.getItem(7).setEnabled(true);   // Flash off
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;

            case R.id.camara_flashon:
                try {

                    mCamera.setFlashEnabled(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;

            case R.id.camara_flashoff:
                try {

                    mCamera.setFlashEnabled(false);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;

            case R.id.about:
            {
                Intent about = new Intent(this, AboutActivity.class);
                about.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                startActivity(about);
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void PinCodeUI(int pincode_state) {

        PinCodeViewIntent = new Intent(this, PinEntryView.class);
        PinCodeViewIntent = PinCodeViewIntent.putExtra("pincode_state", Integer.toString(pincode_state));
        PinCodeViewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);

        startActivityForResult(PinCodeViewIntent, pincode_state);
    }

    private void causeCrash() {
        throw new NullPointerException("Fake null pointer exception");
    }

    private void sendInvitation() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title)).setMessage(getString(R.string.invitation_message)).setCallToActionText(getString(R.string.invitation_cta)).build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    // Fetch the config to determine the allowed length of messages.
    public void fetchConfig() {
        long cacheExpiration = 3600; // 1 hour in seconds
        // If developer mode is enabled reduce cacheExpiration to 0 so that each fetch goes to the
        // server. This should not be used in release builds.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Make the fetched config available via FirebaseRemoteConfig get<type> calls.
                mFirebaseRemoteConfig.activateFetched();
                applyRetrievedLengthLimit();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // There has been an error fetching the config
                Log.w(TAG, "Error fetching config", e);
                applyRetrievedLengthLimit();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Use Firebase Measurement to log that invitation was sent.
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "inv_sent");

                // Check how many invitations were sent and log.
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                Log.d(TAG, "Invitations sent: " + ids.length);
            } else {
                // Use Firebase Measurement to log that invitation was not sent
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "inv_not_sent");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, payload);

                // Sending failed or it was canceled, show failure message to the user
                Log.d(TAG, "Failed to send invitation.");
            }

        } else if (resultCode == REQUEST_PIN_CODE) {

            if (requestCode == REQUEST_PIN_CODE_RESET) {

                mProgress.setVisibility(View.VISIBLE);
                ActionReset();

            } else if (requestCode == REQUEST_PIN_CODE_SOUND) {

                mSoundButton.setText("MUTE");
                if (mEMSPlaying) {
                    mAudioPlayer.stop();
                    mEMSPlaying = false;
                }

            } else if (requestCode == REQUEST_PIN_SET) {

                mUserPinCode = PinCodeViewIntent.getStringExtra("pincode_number");
            }
        }
    }

    private void putImageInStorage(StorageReference storageReference, Uri uri, final String key) {
        storageReference.putFile(uri).addOnCompleteListener(MainActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {

                    LatLng mLatLng = getLocationLatLong();
                    Double lat = mLatLng.latitude;
                    Double lng = mLatLng.longitude;

                    Log.w(TAG, "Image upload task not supported.");

                 //   FriendlyMessage friendlyMessage = new FriendlyMessage(null, mUsername, lat.toString(), lng.toString(), mPhotoUrl, task.getResult().getUploadSessionUri().toString());
                 //   mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(key).setValue(friendlyMessage);
                } else {
                    Log.w(TAG, "Image upload task was not successful.", task.getException());
                }
            }
        });
    }

    /**
     * Apply retrieved length limit to edit text field. This result may be fresh from the server or it may be from
     * cached values.
     */
    private void applyRetrievedLengthLimit() {
        Long friendly_msg_length = mFirebaseRemoteConfig.getLong("friendly_msg_length");
        //  mMessageText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(friendly_msg_length.intValue())});
        //  mMessageText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(friendly_msg_length.intValue())});
        Log.d(TAG, "FML is: " + friendly_msg_length);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
}