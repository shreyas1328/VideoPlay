package com.shreyas.videoplay;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class ExoPlayer extends AppCompatActivity {

    private PlayerView mPlayerView;
    private com.google.android.exoplayer2.ExoPlayer player;
    private Uri uri;
    private String uri1;
    private AppCompatSeekBar mExoSeekBar;
    private TextView mTvStartPos, mTvEndPos, mLandscapeErrorText;

    private ImageButton mBtnExoControl;

    private static final String TAG = "ExoPlayer1";
    private long playbackPosition;
    private int currentWindow;
    private boolean playReady;
    private Handler handler;
    private Runnable updatePlayer;


    private long delay = 500;
    boolean a = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_player);

        mBtnExoControl = findViewById(R.id.exoControlBtn);
        mExoSeekBar = findViewById(R.id.exoSeekBar);
        mTvStartPos = findViewById(R.id.exoStartposition);
        mTvEndPos = findViewById(R.id.exoEndposition);
        mLandscapeErrorText = findViewById(R.id.landscapeErrorText);
//        mBtnClick = findViewById(R.id.btnClick);

        mPlayerView = findViewById(R.id.exoPlayerView);
        Intent intent = getIntent();
        uri1 = intent.getStringExtra("path");
        Log.d(TAG, "onCreate: " + uri1);

        handler = new Handler();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            Toast.makeText(this, "portraitMode", Toast.LENGTH_SHORT).show();
        }else if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Alert").setMessage("Doesnt support LandScape mode").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.create().show();
        }
    }

    private void seekBarPosition() {
        mExoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.seekTo(seekBar.getProgress());
            }
        });
    }

    private void updateThePlayer() {
        updatePlayer = new Runnable() {
            @Override
            public void run() {

                @SuppressLint("DefaultLocale") String totDur = String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(player.getDuration()),
                        TimeUnit.MILLISECONDS.toMinutes(player.getDuration()) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(player.getDuration())), // The change is in this line
                        TimeUnit.MILLISECONDS.toSeconds(player.getDuration()) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(player.getDuration())));
                @SuppressLint("DefaultLocale") String curDur = String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(player.getCurrentPosition()),
                        TimeUnit.MILLISECONDS.toMinutes(player.getCurrentPosition()) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(player.getCurrentPosition())), // The change is in this line
                        TimeUnit.MILLISECONDS.toSeconds(player.getCurrentPosition()) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(player.getCurrentPosition())));

                mTvStartPos.setText(curDur);
                mTvEndPos.setText(totDur);
                mExoSeekBar.setMax((int) player.getDuration());
                mExoSeekBar.setProgress((int) player.getCurrentPosition());
                handler.postDelayed(updatePlayer, delay);
            }
        };


    }


    private void playerEventListener() {
//        player.addListener(listn);

        player.addListener(new Player.EventListener() {



            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int state) {

                playReady = playWhenReady;
//                Log.d("state", "onPlayerStateChanged: " + state + "  " + player.getDuration());

                switch (state) {
                    case 1:
                        break;

                    case 2:
                        break;

                    case 3:
                        break;

                    case 4:
                        Log.d("statee", "onPlayerStateChanged: " + ((int) player.getCurrentPosition() - (int) player.getDuration()) / 10);
                        if ((( player.getCurrentPosition() -  player.getDuration()) / 10) == 0) {
                            player.seekTo(0);
                            mBtnExoControl.setImageResource(R.drawable.exo_play);
                            player.setPlayWhenReady(false);
                        }
                        break;
                }
            }

        });
    }

    private void exoControls() {
        mBtnExoControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (playReady) {
                    player.setPlayWhenReady(false);
                    mBtnExoControl.setImageResource(R.drawable.exo_play);
                } else {
                    
                    player.setPlayWhenReady(true);
                    mBtnExoControl.setImageResource(R.drawable.exo_pause);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("activity", String.format("onStart: "  ));

        initailizePlayer();

//
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUi();
        initailizePlayer();
    }



    @Override
    protected void onPause() {
        super.onPause();
        Log.d("activity", "onPause: " + player.getCurrentPosition());
        playbackPosition = player.getCurrentPosition();
        getPosition();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getPosition();
        Log.d("activity", "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("activity", "onDestroy: ");
       releasePlayer();
    }

    private void initailizePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), new DefaultRenderersFactory(this),
                new DefaultTrackSelector(), new DefaultLoadControl());

        mPlayerView.setPlayer(player);
        Log.d("position", "releasePlayer: " + playbackPosition);
        if (playbackPosition != 0) {
            player.seekTo(playbackPosition);
            player.setPlayWhenReady(true);
            mBtnExoControl.setImageResource(R.drawable.exo_pause);
        }
        Log.d("position", "releasePlayer: " + playbackPosition);
        landscapeError();
        exoControls();
        playerEventListener();
//        Log.d(TAG, "initailizePlayer: " + playReady);

        updateThePlayer();
        seekBarPosition();

        handler.postDelayed(updatePlayer, delay);

        uri = Uri.fromFile(new File(uri1));
        String playerinfo = Util.getUserAgent(getApplicationContext(), "exoPlayerinfo");
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(), playerinfo);
        MediaSource mediaSourceUri = buildMediaSourceUri(uri, dataSourceFactory);
        Log.d(TAG, "initailizePlayer: " + player.getCurrentPosition());
        player.prepare(mediaSourceUri, true, false);
    }

    private MediaSource buildMediaSourceUri(Uri uri, DefaultDataSourceFactory dataSourceFactory) {
        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory().setConstantBitrateSeekingEnabled(true);
        return new ExtractorMediaSource.Factory(dataSourceFactory).setExtractorsFactory(extractorsFactory).createMediaSource(uri);
    }

    private void hideSystemUi() {
        mPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void getPosition() {
        if (player != null) {
            handler.removeCallbacks(updatePlayer);
            currentWindow = player.getCurrentWindowIndex();
            playReady = player.getPlayWhenReady();
            Log.d(TAG, "releasePlayer: " + playReady);


        }
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void landscapeError() {
        mPlayerView.getPlayer().getVideoComponent().addVideoListener(new VideoListener() {
            @Override
            public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                Log.d("1200", "onVideoSizeChanged: "+width+"     "+height);
                if (width >= height) {
                    mBtnExoControl.setVisibility(View.GONE);
                    mExoSeekBar.setVisibility(View.GONE);
                    mTvEndPos.setVisibility(View.GONE);
                    mTvStartPos.setVisibility(View.GONE);
                    mPlayerView.setForeground(getDrawable(R.color.trans_black));
                    mLandscapeErrorText.setVisibility(View.VISIBLE);
//                    final AlertDialog.Builder builder = new AlertDialog.Builder(ExoPlayer.this);
//                    builder.setTitle("Alert!!!").setMessage("Video format is not supported").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.dismiss();
//                        }
//                    }).create().show();
                }
            }
        });
    }
}
