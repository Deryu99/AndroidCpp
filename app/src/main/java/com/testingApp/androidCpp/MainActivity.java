package com.testingApp.androidCpp;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
import com.testingApp.androidCpp.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{android.Manifest.permission.CAMERA};
    private ActivityMainBinding activityMainBinding;
    private ExecutorService cameraExecutor;
    private ImageCapture imageCapture;
    public boolean plasmaOn;

    // Used to load the 'androidCpp' library on application startup.
    static {
        System.loadLibrary("androidCpp");
        System.loadLibrary("plasma");
    }

    /**
     * A native method that is implemented by the 'androidCpp' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public native String stringFromOtherJNI();

    // implementend by libplasma.so
    private native void renderPlasma(Bitmap bitmap, long time_ms);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
        plasmaOn = false;

        // This callback will only be called when Plasma is on
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if (plasmaOn) {
                    setContentView(R.layout.activity_main);
                    plasmaOn = false;
                }
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);


        // Example of a call to a native method
        String txt = stringFromJNI()+" / "+stringFromOtherJNI();
        activityMainBinding.sampleText.setText(txt);
    }

    public void runCpp(View view) throws IOException {
        Toast.makeText(this, getString(R.string.run_cpp)+" button clicked", Toast.LENGTH_SHORT).show();

        Runtime rt = Runtime.getRuntime();
        String[] commands = {"test.exe", "-a1"};
        Process proc = rt.exec(commands);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));

        // Read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }
    }

    public void enableCamera(View view){
        // Request camera permissions
        if (!allPermissionsGranted())
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);

//        cameraExecutor = Executors.newSingleThreadExecutor();
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    private boolean allPermissionsGranted(){
        return ContextCompat.checkSelfPermission(
                getBaseContext(), REQUIRED_PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED;
    }

    public void createPlasmaView(View view) {
        Display display = getWindowManager().getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);
        setContentView(new PlasmaView(this, displaySize.x, displaySize.y));

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                setContentView(new PlasmaView(view.getContext(), displaySize.x, displaySize.y));
                plasmaOn = true;
            }
        });
    }

    public void enableCalculator(View view) {
        Intent intent = new Intent(this, CalculatorActivity.class);
        startActivity(intent);
    }

    @SuppressLint("ViewConstructor")
    class PlasmaView extends View {
        private Bitmap mBitmap;
        private long mStartTime;

        public PlasmaView(Context context, int width, int height) {
            super(context);
            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            mStartTime = System.currentTimeMillis();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            renderPlasma(mBitmap, System.currentTimeMillis() - mStartTime);
            canvas.drawBitmap(mBitmap, 0, 0, null);
            // force a redraw, with a different time-based pattern.
            invalidate();
        }
    }
}