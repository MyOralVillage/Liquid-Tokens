package org.myoralvillage.android.ui.scan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.app.AlertDialog;


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
import org.myoralvillage.android.ui.MainActivity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import dmax.dialog.SpotsDialog;


public class ScanQR extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public ScanQR() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ScanQR newInstance() {
        ScanQR fragment = new ScanQR();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, "");
        args.putString(ARG_PARAM2, "");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    CameraView cameraView;
    Button btnDetect;
    AlertDialog waitingDialouge;

    @Override
    public void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        cameraView.stop();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scan_qr, container, false);

        cameraView = (CameraView) view.findViewById(R.id.cameraview);
        btnDetect = (Button) view.findViewById(R.id.btn_detect);
        waitingDialouge = new SpotsDialog.Builder()
                .setContext(getActivity())
                .setMessage("Please Wait")
                .setCancelable(false)
                .build();

        btnDetect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                cameraView.start();
                cameraView.captureImage();
            }
        });

        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                waitingDialouge.show();
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
        return view;
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
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
        Log.v("LOC", "loc finished runDetector");
    }

    private void processResult(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
        Log.v("LOC", "loc got to processResult");
        for(FirebaseVisionBarcode item : firebaseVisionBarcodes){
            Log.v("LOC", "loc 1");
            int value_type = item.getValueType();
            Log.v("LOC", "loc 2");
            switch (value_type) {
                case FirebaseVisionBarcode.TYPE_TEXT: {
                    Log.v("LOC", "loc case text");
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(item.getRawValue());
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

                case FirebaseVisionBarcode.TYPE_URL: {
                    //start browse url
                    Log.v("LOC", "loc case url");
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getRawValue()));
                    startActivity(intent);
                }
                break;

                case FirebaseVisionBarcode.TYPE_CONTACT_INFO: {
                    Log.v("LOC", "loc case contact info");
                    String info = new StringBuilder("Name: ")
                            .append(item.getContactInfo().getName().getFormattedName())
                            .append("\n")
                            .append("Adress: ")
                            .append(item.getContactInfo().getAddresses().get(0).getAddressLines())
                            .append("\n")
                            .append("Email: ")
                            .append(item.getContactInfo().getEmails().get(0).getAddress())
                            .toString();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
            Log.v("LOC", "loc pre_dialouge close");
            waitingDialouge.dismiss();
            Log.v("LOC", "loc after_dialouge close");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
