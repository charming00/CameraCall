package com.charming.cameracall;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

public class MainActivity extends Activity {

    private Button button;
    private Camera camera;
    private CameraView cameraView;
    private static final int FRONT = 1;//前置摄像头标记
    private static final int BACK = 2;//后置摄像头标记
    private int currentCameraType = -1;//当前打开的摄像头标记

    private ImageView mFrontImageView;
    private ImageView mShadowImageView;

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!checkCamera()) {
            MainActivity.this.finish();
        }
        try {
            camera = openCamera(FRONT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    changeCamera();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        cameraView = (CameraView) findViewById(R.id.cameraview);
        cameraView.init(camera);

        mShadowImageView = (ImageView) findViewById(R.id.shadow_image);


        mFrontImageView = (ImageView) findViewById(R.id.front_image);
        mFrontImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mShadowImageView.getVisibility() == View.INVISIBLE) {
                    Log.d(TAG, "mShadowImageView change to visible");
                    mShadowImageView.setVisibility(View.VISIBLE);
                } else if (mShadowImageView.getVisibility() == View.VISIBLE) {
                    Log.d(TAG, "mShadowImageView change to invisible");
                    mShadowImageView.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    /**
     * @return 摄像头是否存在
     */
    private boolean checkCamera() {
        return MainActivity.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @SuppressLint("NewApi")
    private Camera openCamera(int type) {
        int frontIndex = -1;
        int backIndex = -1;
        int cameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int cameraIndex = 0; cameraIndex < cameraCount; cameraIndex++) {
            Camera.getCameraInfo(cameraIndex, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                frontIndex = cameraIndex;
            } else if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                backIndex = cameraIndex;
            }
        }

        currentCameraType = type;
        if (type == FRONT && frontIndex != -1) {
            return Camera.open(frontIndex);
        } else if (type == BACK && backIndex != -1) {
            return Camera.open(backIndex);
        }
        return null;
    }

    private void changeCamera() throws IOException {
        camera.stopPreview();
        camera.release();
        if (currentCameraType == FRONT) {
            camera = openCamera(BACK);
        } else if (currentCameraType == BACK) {
            camera = openCamera(FRONT);
        }
        camera.setPreviewDisplay(cameraView.getHolder());
        camera.startPreview();
    }
}