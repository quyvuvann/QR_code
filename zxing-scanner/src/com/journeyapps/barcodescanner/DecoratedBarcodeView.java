package com.journeyapps.barcodescanner;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Rational;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.DecodeFormatManager;
import com.google.zxing.client.android.DecodeHintManager;
import com.google.zxing.client.android.Intents;
import com.google.zxing.client.android.R;
import com.google.zxing.client.android.camera.open.OpenCameraInterface;
import com.journeyapps.barcodescanner.camera.CameraInstance;
import com.journeyapps.barcodescanner.camera.CameraManager;
import com.journeyapps.barcodescanner.camera.CameraParametersCallback;
import com.journeyapps.barcodescanner.camera.CameraSettings;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Encapsulates BarcodeView, ViewfinderView and status text.
 * <p>
 * To customize the UI, use BarcodeView and ViewfinderView directly.
 */
public class DecoratedBarcodeView extends FrameLayout {
    private BarcodeView barcodeView;
    private ViewfinderView viewFinder;
    private TextView statusView;
    private Camera camera;
    private CameraManager cameraManager;
    private CameraInstance cameraInstance;
    private String cameraId;
    private CameraCharacteristics characteristics;
    private CameraSettings settings = new CameraSettings();




    /**
     * The instance of @link TorchListener to send events callback.
     */
    private TorchListener torchListener;

    private class WrappedCallback implements BarcodeCallback {
        private BarcodeCallback delegate;

        public WrappedCallback(BarcodeCallback delegate) {
            this.delegate = delegate;
        }

        @Override
        public void barcodeResult(BarcodeResult result) {
            delegate.barcodeResult(result);
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
            for (ResultPoint point : resultPoints) {
                viewFinder.addPossibleResultPoint(point);
            }
            delegate.possibleResultPoints(resultPoints);
        }
    }

    public DecoratedBarcodeView(Context context) {
        super(context);
        initialize();
    }

    public DecoratedBarcodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(attrs);
    }

    public DecoratedBarcodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(attrs);
    }

    /**
     * Initialize the view with the xml configuration based on styleable attributes.
     *
     * @param attrs The attributes to use on view.
     */
    private void initialize(AttributeSet attrs) {
        // Get attributes set on view
        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.zxing_view);

        int scannerLayout = attributes.getResourceId(
                R.styleable.zxing_view_zxing_scanner_layout, R.layout.zxing_barcode_scanner);

        attributes.recycle();

        inflate(getContext(), scannerLayout, this);

        barcodeView = findViewById(R.id.zxing_barcode_surface);

        if (barcodeView == null) {
            throw new IllegalArgumentException(
                    "There is no a com.journeyapps.barcodescanner.BarcodeView on provided layout " +
                            "with the id \"zxing_barcode_surface\".");
        }

        // Pass on any preview-related attributes
        barcodeView.initializeAttributes(attrs);


        viewFinder = findViewById(R.id.zxing_viewfinder_view);

        if (viewFinder == null) {
            throw new IllegalArgumentException(
                    "There is no a com.journeyapps.barcodescanner.ViewfinderView on provided layout " +
                            "with the id \"zxing_viewfinder_view\".");
        }

        viewFinder.setCameraPreview(barcodeView);

        // statusView is optional
        statusView = findViewById(R.id.zxing_status_view);
        camera = OpenCameraInterface.open(settings.getRequestedCameraId());
    }

    /**
     * Initialize with no custom attributes set.
     */
    private void initialize() {
        initialize(null);
    }

    /**
     * Convenience method to initialize camera id, decode formats and prompt message from an intent.
     *
     * @param intent the intent, as generated by IntentIntegrator
     */
    public void initializeFromIntent(Intent intent) {
        // Scan the formats the intent requested, and return the result to the calling activity.
        Set<BarcodeFormat> decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
        Map<DecodeHintType, Object> decodeHints = DecodeHintManager.parseDecodeHints(intent);

        CameraSettings settings = new CameraSettings();

        if (intent.hasExtra(Intents.Scan.CAMERA_ID)) {
            int cameraId = intent.getIntExtra(Intents.Scan.CAMERA_ID, -1);
            if (cameraId >= 0) {
                settings.setRequestedCameraId(cameraId);
            }
        }

        if (intent.hasExtra(Intents.Scan.TORCH_ENABLED)) {
            if (intent.getBooleanExtra(Intents.Scan.TORCH_ENABLED, false)) {
                this.setTorchOn();
            }
        }

        String customPromptMessage = intent.getStringExtra(Intents.Scan.PROMPT_MESSAGE);
        if (customPromptMessage != null) {
            setStatusText(customPromptMessage);
        }

        // Check what type of scan. Default: normal scan
        int scanType = intent.getIntExtra(Intents.Scan.SCAN_TYPE, 0);

        String characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);

        MultiFormatReader reader = new MultiFormatReader();
        reader.setHints(decodeHints);

        barcodeView.setCameraSettings(settings);
        barcodeView.setDecoderFactory(new DefaultDecoderFactory(decodeFormats, decodeHints, characterSet, scanType));
    }

    public void setCameraSettings(CameraSettings cameraSettings) {
        barcodeView.setCameraSettings(cameraSettings);
    }

    public void setDecoderFactory(DecoderFactory decoderFactory) {
        barcodeView.setDecoderFactory(decoderFactory);
    }

    public DecoderFactory getDecoderFactory() {
        return barcodeView.getDecoderFactory();
    }

    public CameraSettings getCameraSettings() {
        return barcodeView.getCameraSettings();
    }

    public void setStatusText(String text) {
        // statusView is optional when using a custom layout
        if (statusView != null) {
            statusView.setText(text);
        }
    }

    /**
     * @see BarcodeView#pause()
     */
    public void pause() {
        barcodeView.pause();
    }

    /**
     * @see BarcodeView#pauseAndWait()
     */
    public void pauseAndWait() {
        barcodeView.pauseAndWait();
    }

    /**
     * @see BarcodeView#resume()
     */
    public void resume() {
        barcodeView.resume();
    }

    public BarcodeView getBarcodeView() {
        return findViewById(R.id.zxing_barcode_surface);
    }

    public ViewfinderView getViewFinder() {
        return viewFinder;
    }

    public TextView getStatusView() {
        return statusView;
    }

    /**
     * @see BarcodeView#decodeSingle(BarcodeCallback)
     */
    public void decodeSingle(BarcodeCallback callback) {
        barcodeView.decodeSingle(new WrappedCallback(callback));
    }

    /**
     * @see BarcodeView#decodeContinuous(BarcodeCallback)
     */
    public void decodeContinuous(BarcodeCallback callback) {
        barcodeView.decodeContinuous(new WrappedCallback(callback));
    }

    /**
     * Turn on the device's flashlight.
     */
    public void setTorchOn() {
        barcodeView.setTorch(true);
        if (torchListener != null) {
            torchListener.onTorchOn();
        }
    }

    /**
     * Turn off the device's flashlight.
     */
    public void setTorchOff() {
        barcodeView.setTorch(false);

        if (torchListener != null) {
            torchListener.onTorchOff();
        }
    }

    /**
     * Changes the settings for Camera.
     * Must be called after {@link #resume()}.
     *
     * @param callback {@link CameraParametersCallback}
     */
    public void changeCameraParameters(CameraParametersCallback callback) {
        barcodeView.changeCameraParameters(callback);
    }

    /**
     * Handles focus, camera, volume up and volume down keys.
     * <p>
     * Note that this view is not usually focused, so the Activity should call this directly.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_FOCUS:
            case KeyEvent.KEYCODE_CAMERA:
                // Handle these events so they don't launch the Camera app
                return true;
            // Use volume up/down to turn on light
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                setTorchOff();
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                setTorchOn();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setTorchListener(TorchListener listener) {
        this.torchListener = listener;
    }

    /**
     * The Listener to torch/fflashlight events (turn on, turn off).
     */
    public interface TorchListener {

        void onTorchOn();

        void onTorchOff();
    }


    public void switchCamera() {
        CameraSettings currentCameraSettings = barcodeView.getCameraSettings();
        int currentCameraId = currentCameraSettings.getRequestedCameraId();

        int newCameraId = (currentCameraId == CameraSettings.CAMERA_FACING_BACK)
                ? CameraSettings.CAMERA_FACING_FRONT
                : CameraSettings.CAMERA_FACING_BACK;

        CameraSettings newCameraSettings = new CameraSettings();
        newCameraSettings.setRequestedCameraId(newCameraId);

        barcodeView.pause();
        barcodeView.setCameraSettings(newCameraSettings);
        barcodeView.resume();
    }

    public void zoomCamera(Integer value){
        cameraManager  = new CameraManager(getContext().getApplicationContext());
        cameraManager.zoomCamera(value);
    }
    public void zoomin(){
        cameraManager  = new CameraManager(getContext().getApplicationContext());
        cameraManager.zoomIn();
    }
    public void zoomOut(){
        cameraManager  = new CameraManager(getContext().getApplicationContext());
        cameraManager.zoomOut();
    }


}
