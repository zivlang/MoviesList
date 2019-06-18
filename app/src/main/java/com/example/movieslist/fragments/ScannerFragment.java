package com.example.movieslist.fragments;

import android.support.v4.app.Fragment;

public class ScannerFragment extends Fragment {

//    ScanResultReceiver resultCallback;
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//
//        // This makes sure that the container activity has implemented
//        // the callback interface. If not, it throws an exception
//        try {
//            resultCallback = (ScanResultReceiver) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement ScanResultReceiver");
//        }
//    }
//
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(ScannerFragment.this);
//
////        IntentIntegrator integrator = new IntentIntegrator(getActivity());
//        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
//        integrator.setPrompt("Scan a barcode");
//        integrator.setCameraId(0);  // Use a specific camera of the device
////        integrator.setResultDisplayDuration(0);
//        integrator.initiateScan();
//    }
//
//    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        //retrieve scan result
//        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
//        ScanResultReceiver parentActivity = (ScanResultReceiver) this.getActivity();
//
//        if (scanningResult != null) {
//            //we have a result
//            String codeContent = scanningResult.getContents();
//            String codeFormat = scanningResult.getFormatName();
//            Log.i("Monitoring: ", codeContent+" "+codeFormat);
//
//            // send received data
//            if (parentActivity != null) {
//                parentActivity.scanResultData(codeFormat,codeContent);
//                Log.i("Monitoring: ", codeContent+" "+codeFormat);
//
//            }
//        }else{
//            // send exception
//            if (parentActivity != null) {
//                String noResultErrorMsg = "No scan data received!";
//                parentActivity.scanResultData(new NoScanResultException(noResultErrorMsg));
//            }
//        }
//    }
//
}
