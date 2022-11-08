package com.testingApp.androidCpp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.testingApp.androidCpp.databinding.ActivityCameraBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity {

    private final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    private ActivityCameraBinding activityCameraBinding;
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    // Used to load the 'plasma' library on application startup.
    static {
        System.loadLibrary("plasma");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCameraBinding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.previewView);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(this::runnableListener,
                ContextCompat.getMainExecutor(this));
    }

    // Listener
    public void runnableListener() {
        // Used to bind the lifecycle of cameras to the lifecycle owner
        ProcessCameraProvider cameraProvider = null;
        try {
            cameraProvider = cameraProviderFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // Unbind use cases before rebinding
        assert cameraProvider != null;
        cameraProvider.unbindAll();

        imageCapture = new ImageCapture.Builder()
                .setTargetRotation((int) findViewById(R.id.my_root_camera)
                                            .getRootView().getRotation()).build();
        cameraProvider.bindToLifecycle(this,
                CameraSelector.DEFAULT_BACK_CAMERA, imageCapture,preview);
    }

    public void takePhoto(View view) {
//        imageCapture = new ImageCapture.Builder()
//                .setTargetRotation(view.getDisplay().getRotation()).build();
        String name = new SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis());
//        File file = new File(getContext().getExternalCacheDir()
//                + File.separator + System.currentTimeMillis() + ".png");

        imageCapture.takePicture(Executors.newSingleThreadExecutor(),
                new ImageCapture.OnImageCapturedCallback() {
            @SuppressLint("UnsafeOptInUsageError")
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                Bitmap bm = convertImageProxyToBitmap(Objects.requireNonNull(image));
            }

            @Override
            public void onError(ImageCaptureException error) {
                System.out.println("The image is not there");
            }
        });
    }

    private Bitmap convertImageProxyToBitmap(ImageProxy image) {
        ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
        byteBuffer.rewind();
        byte[] bytes = new byte[byteBuffer.capacity()];
        byteBuffer.get(bytes);
        byte[] clonedBytes = bytes.clone();
        return BitmapFactory.decodeByteArray(clonedBytes, 0, clonedBytes.length);
    }

//    public void takePhoto(View view) {
//        imageCapture = new ImageCapture.Builder()
//                .setTargetRotation((int) view.getRotation())
//                .build();
//        imageCapture.takePicture(cameraExecutor, new ImageCapture.OnImageCapturedCallback() {
//            @Override
//            public void onCaptureSuccess(@NonNull ImageProxy image) {
//                System.out.println("Bla Blas");
//            }
//
//            @Override
//            public void onError(ImageCaptureException error) {
//                // insert your code here.
//            };
//        });
//    }

}