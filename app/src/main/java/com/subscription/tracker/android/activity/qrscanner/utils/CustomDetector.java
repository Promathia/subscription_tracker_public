package com.subscription.tracker.android.activity.qrscanner.utils;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;

public abstract class CustomDetector implements Detector.Processor<Barcode> {

    private boolean isDetectionReceived = false;

    public boolean isDetectionReceived() {
        return isDetectionReceived;
    }

    public void setDetectionReceived(boolean detectionReceived) {
        isDetectionReceived = detectionReceived;
    }
}
