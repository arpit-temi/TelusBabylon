package com.temi.babylon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.material.imageview.ShapeableImageView;
import com.robotemi.sdk.Robot;
import com.robotemi.sdk.TtsRequest;
import com.robotemi.sdk.listeners.OnRobotReadyListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnRobotReadyListener, Robot.TtsListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    Robot robot = Robot.getInstance();
    ArrayList scriptList;
    int count = 0;
    TtsRequest firstRequest = TtsRequest.create("Hello, Doctor Gideion.", false);
    private TextView tv_title;
    LinearLayoutCompat body;
    VideoView videoView;
    ShapeableImageView telusButton;

    @Override
    protected void onStart() {
        super.onStart();
        robot.addOnRobotReadyListener(this);
        robot.addTtsListener(this);

        scriptList = new ArrayList<String>();
        /**
         * PART 1
         */
        scriptList.add("May I call you Ibrahim."); // 0
        scriptList.add("I’m the world’s first personal robot with proprietary autonomous navigation."); // 1
        scriptList.add("My AI technology, allows me to connect  seamlessly into TELUS environments.");// 2

        scriptList.add("I’m excited to introduce me to yourself over the holiday period, thank you so much for inviting me into your home"); // 3
        scriptList.add("TELUS have created safe working environments through touchless in-store experiences"); // 4
        scriptList.add("My voice activated system will work in synergy with all TELUS stores and environments"); // 5
        scriptList.add("I can greet and meet customers, retain detailed information on the products and "); // 6

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_title = findViewById(R.id.tv_title);
        videoView = findViewById(R.id.videoView);
        body = findViewById(R.id.body);
        telusButton = findViewById(R.id.telusButton);


        createAnimation(tv_title);

//        tv_title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
//        tv_title.setSelected(true);
//        tv_title.setSingleLine(true);

//        rootLayout.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    public void run() {
//                        robot.showTopBar();
//                    }
//                }, 3000);
//
//                robot.hideTopBar();
//                return false;
//            }
//        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        robot.removeOnRobotReadyListener(this);
        robot.removeTtsListener(this);
    }


    @Override
    public void onTtsStatusChanged(@NotNull TtsRequest ttsRequest) {

        Log.d(TAG, "onTtsStatusChanged: count : "+count);
        if (String.valueOf(ttsRequest.getStatus()).equalsIgnoreCase("completed")) {

            switch (count) {

                case 0:
                case 1:
                case 2:
                case 4:
                case 5:
                case 6:
                    robot.speak(TtsRequest.create((String) scriptList.get(count), false));
                    count++;
                    break;
                case 3:
                    body.setVisibility(View.INVISIBLE);
                    videoView.setVisibility(View.VISIBLE);
                    telusButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(MainActivity.this, "Hellooo", Toast.LENGTH_SHORT).show();
                        }
                    });

                    String path = "android.resource://com.temi.babylon/" + R.raw.temi_intro;
                    videoView.setVideoURI(Uri.parse(path));
                    videoView.start();
                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            tv_title.setText("Telus Store");
                            videoView.setVisibility(View.INVISIBLE);
                            body.setVisibility(View.VISIBLE);
                            robot.speak(TtsRequest.create((String) scriptList.get(count), false));
                            count++;
                        }
                    });


                    break;
                default:
                    Log.i(TAG, "onTtsStatusChanged: No Text Found");
            }

        }

    }

    @Override
    public void onRobotReady(boolean b) {
        if (b) {
            robot.speak(firstRequest);
        }
        try {
            final ActivityInfo activityInfo = getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA);
            robot.onStart(activityInfo);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        robot.hideTopBar();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    public void createAnimation(View view){
        Animation a = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        a.reset();
        view.clearAnimation();
        view.startAnimation(a);
    }
}