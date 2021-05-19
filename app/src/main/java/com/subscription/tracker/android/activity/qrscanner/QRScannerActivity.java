package com.subscription.tracker.android.activity.qrscanner;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.subscription.tracker.android.R;
import com.subscription.tracker.android.activity.main.AdminOwnerInstrActivity;
import com.subscription.tracker.android.activity.qrscanner.utils.CustomDetector;
import com.subscription.tracker.android.entity.response.SingleUserData;
import com.subscription.tracker.android.entity.response.UserSubscription;
import com.subscription.tracker.android.services.EncryptionService;
import com.subscription.tracker.android.services.SharedPreferencesService;
import com.subscription.tracker.android.services.SubscriptionService;

import java.io.IOException;
import java.util.Date;

public class QRScannerActivity extends AppCompatActivity {

    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private SingleUserData singleUserResponse;
    private static final int PERMISSION_REQUEST_USE_CAMERA = 78;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scanner);
        this.singleUserResponse = new SharedPreferencesService(this)
                .getObject(R.string.preference_user_data_key, SingleUserData.class);;
        this.surfaceView = findViewById(R.id.qr_scanner_camera_view);
        this.barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();
        this.cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedFps(1f)
                .build();
        this.surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(QRScannerActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_USE_CAMERA);
                    }
                    cameraSource.start(holder);
                } catch (IOException e) {
                    Toast.makeText(QRScannerActivity.this, getString(R.string.error_general), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                barcodeDetector.release();
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new CustomDetector() {

            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCode = detections.getDetectedItems();
                if (qrCode.size() > 0 && !isDetectionReceived()) {
                    setDetectionReceived(true);
                    processQrCodeScanned(qrCode, this);
                }
            }
        });
    }

    private void processQrCodeScanned(final SparseArray<Barcode> qrCode, final CustomDetector processor) {
        final String displayValue = qrCode.valueAt(0).displayValue;
        final String decryptedData = new EncryptionService().decryptString(displayValue);
        try {
            final UserSubscription userSubscription = new ObjectMapper().readValue(decryptedData, UserSubscription.class);
            if (isValidSubscription(userSubscription)) {
                QRScannerActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showUseSubscriptionDialog(userSubscription, processor);
                    }
                });
            }
        } catch (JsonProcessingException e) {
            Toast.makeText(QRScannerActivity.this, getString(R.string.error_general), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isValidSubscription(final UserSubscription userSubscription) {
        final Date deadline = userSubscription.getDeadline();
        if (deadline == null || deadline.before(new Date())) {
            Toast.makeText(QRScannerActivity.this, getString(R.string.error_user_subscription_is_above_deadline), Toast.LENGTH_LONG).show();
            return false;
        }
        if (userSubscription.getVisitsLimit() != 0 && userSubscription.getVisitCounter() >= userSubscription.getVisitsLimit()) {
            Toast.makeText(QRScannerActivity.this, getString(R.string.error_user_subscription_is_invalid_or_full_used), Toast.LENGTH_LONG).show();
            return false;
        }
        if (userSubscription.getUserId() == null || userSubscription.getId() == null) {
            Toast.makeText(QRScannerActivity.this, getString(R.string.error_qr_code_mal_format), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           final String[] permissions,
                                           final int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_USE_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.recreate();
            } else {
                final Intent intent = new Intent(this, AdminOwnerInstrActivity.class);
                startActivity(intent);
            }
        }
    }

    private void showUseSubscriptionDialog(final UserSubscription userSubscription, final CustomDetector processor) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(QRScannerActivity.this);
        builder.setTitle(getString(R.string.use_subscription_dialog_title));
        builder.setMessage(getString(R.string.use_subscription_dialog_message));
        builder.setPositiveButton(getString(R.string.use_subscription_dialog_positive_response),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                new SubscriptionService(QRScannerActivity.this)
                        .useVisitForUser(userSubscription, singleUserResponse.getId());
                dialog.cancel();
                processor.setDetectionReceived(false);
            }
        });
        builder.setNegativeButton(getString(R.string.use_subscription_dialog_negative_response), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                dialog.cancel();
                processor.setDetectionReceived(false);
            }
        });
        builder.show();
    }

}
