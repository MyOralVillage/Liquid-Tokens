package org.myoralvillage.android.ui.scan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import org.myoralvillage.android.R;
import org.myoralvillage.android.ui.transaction.TransactionActivity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import dmax.dialog.SpotsDialog;

public class CameraScanActivity extends AppCompatActivity {

    public static final String EXTRA_CONTACT_UID = "ContactUid";
    private String contactUid;
    private CameraView cameraView;
    private Button btnDetect;
    private Button btnCancel;
    private AlertDialog waitingDialogue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
        addListeners();

        cameraView = this.findViewById(R.id.cameraview);
        waitingDialogue = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Please Wait")
                .setCancelable(true)
                .build();

        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                waitingDialogue.show();
                Bitmap bitmap = cameraKitImage.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, cameraView.getWidth(), cameraView.getHeight(), false);
                cameraView.stop();

                runDetector(bitmap);
                Log.v("LOC", "loc call to detector done");

            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });
    }

    private void addListeners(){
        btnDetect = this.findViewById(R.id.btn_detect);
        btnDetect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                cameraView.start();
                cameraView.captureImage();
            }
        });

        btnCancel = this.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();

            }
        });
    }
    private void startTransaction(){
        Intent intent = new Intent(this, TransactionActivity.class);
        intent.putExtra(TransactionActivity.EXTRA_TRANSACTION_SEND_TO, contactUid);
        intent.putExtra(TransactionActivity.EXTRA_TRANSACTION_TYPE, TransactionActivity.TRANSACTION_TYPE_SEND);

        startActivity(intent);
        finish();
    }

    private void runDetector(Bitmap bitmap) {
        Log.v("LOC", "loc got runDetector");
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionBarcodeDetectorOptions options = new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(
                        FirebaseVisionBarcode.FORMAT_QR_CODE,
                        FirebaseVisionBarcode.FORMAT_PDF417 //or anytype
                )
                .build();
        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance().getVisionBarcodeDetector();

        detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                        processResult(firebaseVisionBarcodes);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CameraScanActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
        Log.v("LOC", "loc finished runDetector");
    }

    private void processResult(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
        Log.v("LOC", "loc got to processResult");
        if (firebaseVisionBarcodes.isEmpty()){
            Log.v("LOC", "barcode is empty");
            waitingDialogue.dismiss();
            cameraView.stop();
            cameraView.start();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Try Again!");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            for(FirebaseVisionBarcode item : firebaseVisionBarcodes) {
                Log.v("LOC", "loc 1");
                int value_type = item.getValueType();
                Log.v("LOC", "loc 2");
                switch (value_type) {
                    case FirebaseVisionBarcode.TYPE_TEXT: {
                        Log.v("LOC", "loc case text" + item.getRawValue());
                        contactUid = item.getRawValue();
                        startTransaction();

                    }
                    break;

                    case FirebaseVisionBarcode.TYPE_URL: {
                        //start browse url
                        Log.v("LOC", "loc case url");
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getRawValue()));
                        startActivity(intent);
                    }
                    break;

                    case FirebaseVisionBarcode.TYPE_CONTACT_INFO: {
                        Log.v("LOC", "loc case contact info");
                        String info = "Name: " +
                                item.getContactInfo().getName().getFormattedName() +
                                "\n" +
                                "Address: " +
                                String.valueOf(item.getContactInfo().getAddresses().get(0).getAddressLines()) +
                                "\n" +
                                "Email: " +
                                item.getContactInfo().getEmails().get(0).getAddress();
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(info);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                dialogInterface.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    break;

                    default:
                        break;
                }
                Log.v("LOC", "loc pre_dialogue close");
                waitingDialogue.dismiss();
                Log.v("LOC", "loc after_dialogue close");
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraView.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cameraView.stop();

    }
}

