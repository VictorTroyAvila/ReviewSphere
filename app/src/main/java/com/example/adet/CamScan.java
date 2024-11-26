package com.example.adet;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.Navigation;

import com.example.adet.databinding.ActivityCamScanBinding;
import com.example.adet.databinding.ActivityNotebookBinding;
import com.google.common.util.concurrent.ListenableFuture;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CamScan extends AppCompatActivity {

    private ImageView takePhoto;

    private Intent theIntent;

    private ActivityCamScanBinding viewBinding;
    private ImageCapture imageCapture = null;
    private ExecutorService cameraExecutor;
    private static final String TAG = "CameraXApp";
    private static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    private static final String[] REQUIRED_PERMISSIONS;

    static {
        List<String> permissionsList = new ArrayList<>();
        permissionsList.add(android.Manifest.permission.CAMERA);
        permissionsList.add(android.Manifest.permission.RECORD_AUDIO);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        REQUIRED_PERMISSIONS = permissionsList.toArray(new String[0]);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cam_scan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        theIntent = getIntent();
        takePhoto = findViewById(R.id.takePhoto);

        viewBinding = ActivityCamScanBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            requestPermissions();
        }

        viewBinding.takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    private void takePhoto() {
        if (imageCapture == null) {
            return;
        }
        // Create time-stamped name and MediaStore entry.
        String name = new SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image");
        }

        // Create output options object which contains file + metadata
        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(
                        getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues)
                        .build();

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onError(@NonNull ImageCaptureException exc) {
                        Log.e(TAG, "Photo capture failed: " + exc.getMessage(), exc);
                    }

                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                        String msg = "Photo capture succeeded: " + output.getSavedUri();
                        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, msg);

                        Uri savedImageUri = output.getSavedUri();

                        if (savedImageUri != null) {
                            viewBinding.camscanPreview.post(() -> {
                                try {
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), savedImageUri);// Rotate the bitmap to portrait if necessary
                                    int orientation = getOrientation(savedImageUri);
                                    if (orientation != ExifInterface.ORIENTATION_UNDEFINED && orientation != ExifInterface.ORIENTATION_NORMAL) {
                                        Matrix matrix = new Matrix();
                                        matrix.postRotate(getRotationAngle(orientation));
                                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                                    }

                                    TessBaseAPI tessBaseAPI = new TessBaseAPI();
                                    String dataPath = getFilesDir().getAbsolutePath() + "/tesseract";
                                    String language = "eng";

                                    // Make sure the necessary directories exist
                                    File dir = new File(dataPath + "/tessdata");
                                    if (!dir.exists()) {
                                        dir.mkdirs();
                                    }

                                    // Copy the language data file from assets to the app's internal storage
                                    try {
                                        AssetManager assetManager = getAssets();
                                        InputStream inputStream = assetManager.open("tessdata/" + language + ".traineddata");
                                        OutputStream outputStream = new FileOutputStream(new File(dir, language + ".traineddata"));
                                        byte[] buffer = new byte[1024];
                                        int read;
                                        while ((read = inputStream.read(buffer)) != -1) {
                                            outputStream.write(buffer, 0, read);
                                        }
                                        inputStream.close();
                                        outputStream.flush();
                                        outputStream.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    // Initialize Tesseract
                                    tessBaseAPI.init(dataPath, language);

                                    // Set the image for OCR
                                    tessBaseAPI.setImage(bitmap);

                                    // Perform OCR
                                    String recognizedText = tessBaseAPI.getUTF8Text();

                                    Intent imgText = new Intent(CamScan.this, Notebook.class);
                                    imgText.putExtra("Fname", theIntent.getStringExtra("Fname"));
                                    imgText.putExtra("imgtext", recognizedText);

                                    Log.d(TAG, "Recognized text: " + recognizedText);

                                    tessBaseAPI.end();
                                    startActivity(imgText);

                                } catch (IOException e) {
                                    Log.e(TAG, "Failed to load image: " + e.getMessage());
                                }
                            });
                        } else {
                            Log.e(TAG, "Saved image URI is null");
                        }
                    }
                });
    }
    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Preview
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewBinding.previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().setTargetRotation(Surface.ROTATION_0).build();

                // Select back camera as a default
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                // Unbind use cases before rebinding
                cameraProvider.unbindAll();

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (ExecutionException | InterruptedException exc) {
                Log.e(TAG, "Use case binding failed", exc);
            }}, ContextCompat.getMainExecutor(this));
    }
    private void requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS);
    }
    private boolean allPermissionsGranted() {
        for(String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getBaseContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    private ActivityResultLauncher<String[]> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    result -> {
                        // Handle Permission granted/rejected
                        boolean permissionGranted = true;
                        for (Map.Entry<String, Boolean> entry :result.entrySet()) {
                            if (Arrays.asList(REQUIRED_PERMISSIONS).contains(entry.getKey()) && !entry.getValue()) {
                                permissionGranted = false;
                                break; // Exit loop if any required permission is denied
                            }
                        }
                        if (!permissionGranted) {
                            Toast.makeText(getBaseContext(), "Permission request denied", Toast.LENGTH_SHORT).show();
                        } else {
                            startCamera();
                        }
                    });

    private int getOrientation(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        ExifInterface exifInterface = new ExifInterface(inputStream);
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        inputStream.close(); // Close the input stream
        return orientation;
    }
    private float getRotationAngle(int orientation) {
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return 90;
            case ExifInterface.ORIENTATION_ROTATE_180:
                return 180;
            case ExifInterface.ORIENTATION_ROTATE_270:
                return 270;
            default:
                return 0;
        }
    }
}