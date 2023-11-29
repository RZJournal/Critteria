package com.example.insectrecognitionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import com.example.insectrecognitionapp.ml.Model;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final int pic_id = 123;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final int REQUEST_LOCATION_PERMISSION = 201;
    private static final int REQUEST_GALLERY_PERMISSION = 202;
    private static final int REQUEST_GALLERY = 101;
    Button camera_open_id, gallery_open_id;
    ImageView click_image_id;
    TextView result;
    private List<Insect> insects;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
        }


        click_image_id = findViewById(R.id.insectImageView);
        camera_open_id = findViewById(R.id.recognizeButton);
        gallery_open_id =findViewById(R.id.galleryButton);
        result = findViewById(R.id.insectResultTextView);

        String jsonString = loadJSONFromAsset("insect_info.json");

        insects = new Gson().fromJson(jsonString, new TypeToken<List<Insect>>(){}.getType());

        camera_open_id.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                openCamera();
            } else {
                requestCameraPermission();
            }
        });

        gallery_open_id.setOnClickListener(v -> {
            if (checkGalleryPermission()) {
                openGallery();
            } else {
                requestGalleryPermission();
            }
        });
    }

    private String loadJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream inputStream = getAssets().open(filename);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }





    // Insect Classification
    public void classifyImage(Bitmap image){
        try {
            Model model = Model.newInstance(getApplicationContext());

            float[] normalizedPixels = normalizePixels(image);

            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            inputFeature0.loadArray(normalizedPixels, new int[]{1, 224, 224, 3});

            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();

            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }

            String[] classes = {
                    "rice leaf roller",
                    "rice leaf caterpillar",
                    "paddy stem maggot",
                    "asiatic rice borer",
                    "yellow rice borer",
                    "rice gall midge",
                    "Rice Stemfly",
                    "brown plant hopper",
                    "white backed plant hopper",
                    "small brown plant hopper",
                    "rice water weevil",
                    "rice leafhopper",
                    "grain spreader thrips",
                    "rice shell pest",
                    "grub",
                    "mole cricket",
                    "wireworm",
                    "white margined moth",
                    "black cutworm",
                    "large cutworm",
                    "yellow cutworm",
                    "red spider",
                    "corn borer",
                    "army worm",
                    "aphids",
                    "Potosiabre vitarsis",
                    "peach borer",
                    "english grain aphid",
                    "green bug",
                    "bird cherry-oataphid",
                    "wheat blossom midge",
                    "penthaleus major",
                    "longlegged spider mite",
                    "wheat phloeothrips",
                    "wheat sawfly",
                    "cerodonta denticornis",
                    "beet fly",
                    "flea beetle",
                    "cabbage army worm",
                    "beet army worm",
                    "Beet spot flies",
                    "meadow moth",
                    "beet weevil",
                    "serica orientalis",
                    "alfalfa weevil",
                    "flax budworm",
                    "alfalfa plant bug",
                    "tarnished plant bug",
                    "Locustoidea",
                    "lytta polita",
                    "legume blister beetle",
                    "blister beetle",
                    "therioaphis maculata Buckton",
                    "odontothrips loti",
                    "Thrips",
                    "alfalfa seed chalcid",
                    "Pieris canidia",
                    "Apolygus lucorum",
                    "Limacodidae",
                    "Viteus vitifoliae",
                    "Colomerus vitis",
                    "Brevipoalpus lewisi McGregor",
                    "oides decempunctata",
                    "Polyphagotars onemus latus",
                    "Pseudococcus comstocki Kuwana",
                    "parathrene regalis",
                    "Ampelophaga",
                    "Lycorma delicatula",
                    "Xylotrechus",
                    "Cicadella viridis",
                    "Miridae",
                    "Trialeurodes vaporariorum",
                    "Erythroneura apicalis",
                    "Papilio xuthus",
                    "Panonchus citri McGregor",
                    "Phyllocoptes oleiverus ashmead",
                    "Icerya purchasi Maskell",
                    "Unaspis yanonensis",
                    "Ceroplastes rubens",
                    "Chrysomphalus aonidum",
                    "Parlatoria zizyphus Lucus",
                    "Nipaecoccus vastalor",
                    "Aleurocanthus spiniferus",
                    "Tetradacus c Bactrocera minax",
                    "Dacus dorsalis(Hendel)",
                    "Bactrocera tsuneonis",
                    "Prodenia litura",
                    "Adristyrannus",
                    "Phyllocnistis citrella Stainton",
                    "Toxoptera citricidus",
                    "Toxoptera aurantii",
                    "Aphis citricola Vander Goot",
                    "Scirtothrips dorsalis Hood",
                    "Dasineura sp",
                    "Lawana imitata Melichar",
                    "Salurnis marginella Guerr",
                    "Deporaus marginatus Pascoe",
                    "Chlumetia transversa",
                    "Mango flat beak leafhopper",
                    "Rhytidodera bowrinii white",
                    "Sternochetus frigidus",
                    "Cicadellidae"
            };

            String input = classes[maxPos];
            String output = input.substring(0, 1).toUpperCase() + input.substring(1);
            result.setText(output);

            Insect insect = insects.get(maxPos);
            System.out.println(maxPos);
            String insectName = insect.getName();
            List<String> location = insect.getLocation();
            String description = insect.getDescription();
            String habitat = insect.getHabitat();
            String identificationTips = insect.getIdentificationTips();
            String ecologicalImpact = insect.getEcologicalImpact();
            String controlMethods = insect.getControlMethods();
            String preventionTips = insect.getPreventionTips();

            showPopup(insectName, location, description, habitat, identificationTips, ecologicalImpact, controlMethods, preventionTips);
            model.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private float[] normalizePixels(Bitmap image) {
        Bitmap resizedImage = Bitmap.createScaledBitmap(image, 224, 224, true);

        int[] intValues = new int[224 * 224];
        resizedImage.getPixels(intValues, 0, resizedImage.getWidth(), 0, 0, resizedImage.getWidth(), resizedImage.getHeight());

        float[] normalizedPixels = new float[224 * 224 * 3];
        int pixel = 0;
        for (int i = 0; i < 224; i++) {
            for (int j = 0; j < 224; j++) {
                int val = intValues[pixel++];
                normalizedPixels[i * 224 * 3 + j * 3] = ((val >> 16) & 0xFF) / 255.0f;
                normalizedPixels[i * 224 * 3 + j * 3 + 1] = ((val >> 8) & 0xFF) / 255.0f;
                normalizedPixels[i * 224 * 3 + j * 3 + 2] = (val & 0xFF) / 255.0f;
            }
        }
        return normalizedPixels;
    }







    public void showPopup(String insectName, List<String> location, String description, String habitat, String identificationTips, String ecologicalImpact, String controlMethods, String preventionTips) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View popupView = getLayoutInflater().inflate(R.layout.popup, null);

        TextView popupTitleTextView = popupView.findViewById(R.id.popupTitleTextView);
        TextView popupLocationTextView = popupView.findViewById(R.id.popupLocationTextView);
        TextView popupDescriptionTextView = popupView.findViewById(R.id.popupDescriptionTextView);
        TextView popupHabitatTextView = popupView.findViewById(R.id.popupHabitatTextView);
        TextView popupIdentificationTextView = popupView.findViewById(R.id.popupIdentificationTextView);
        TextView popupEcologicalImpactTextView = popupView.findViewById(R.id.popupEcologicalImpactTextView);
        TextView popupControlMethodsTextView = popupView.findViewById(R.id.popupControlMethodsTextView);
        TextView popupPreventionTipsTextView = popupView.findViewById(R.id.popupPreventionTipsTextView);
        Button locationSettingsButton = popupView.findViewById(R.id.locationSettingsButton);
        locationSettingsButton.setOnClickListener(v -> {
            if (checkLocationPermission()) {
                // Get the user's continent (you need to implement this method)
                String userContinent = getUserContinent();

                if (userContinent != null && !location.contains(userContinent)) {
                    showToast("Insect is not native to your continent!");
                }
            } else {
                requestLocationPermission();
            }
        });





        popupTitleTextView.setText(insectName);
        popupLocationTextView.setText("Location: " + TextUtils.join(", ", location));
        popupDescriptionTextView.setText("Description: " + description);
        popupHabitatTextView.setText("Habitat: " + habitat);
        popupIdentificationTextView.setText("Identification Tips: " + identificationTips);
        popupEcologicalImpactTextView.setText("Ecological Impact: " + ecologicalImpact);
        popupControlMethodsTextView.setText("Control Methods: " + controlMethods);
        popupPreventionTipsTextView.setText("Prevention Tips: " + preventionTips);

        builder.setView(popupView);
        AlertDialog dialog = builder.create();
        dialog.show();
    }















    // Camera Methods
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == pic_id && resultCode == RESULT_OK && data != null && data.getExtras() != null) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            click_image_id.setImageBitmap(image);
            classifyImage(image);
        } else if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                click_image_id.setImageBitmap(selectedImage);
                classifyImage(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
                showToast("Failed to load image from gallery.");
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                showToast("Camera permission is required to use the camera.");
            }
        }
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    private void openCamera() {
        Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera_intent, pic_id);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }











    // Gallery Methods
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_GALLERY);
    }

    private boolean checkGalleryPermission() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestGalleryPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_GALLERY_PERMISSION);
    }











    // Location Methods
    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
    }

    private boolean checkInsectLocation(List<String> insectContinents, String userContinent) {
        for (String continent : insectContinents) {
            if (continent.equalsIgnoreCase(userContinent)) {
                return true;
            }
        }
        return false;
    }

    public String getUserContinent() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://ipinfo.io")
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseData = response.body().string();
                JSONObject jsonObject = new JSONObject(responseData);
                String country = jsonObject.getString("country");
                String continent = getContinentForCountryCode(country);
                return continent;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getContinentForCountryCode(String countryCode) {
        if ("US".equalsIgnoreCase(countryCode) || "CA".equalsIgnoreCase(countryCode)) {
            return "North America";
        } else if ("BR".equalsIgnoreCase(countryCode) || "AR".equalsIgnoreCase(countryCode)) {
            return "South America";
        } else if ("GB".equalsIgnoreCase(countryCode) || "FR".equalsIgnoreCase(countryCode)) {
            return "Europe";
        } else if ("CN".equalsIgnoreCase(countryCode) || "JP".equalsIgnoreCase(countryCode)) {
            return "Asia";
        } else if ("AU".equalsIgnoreCase(countryCode)) {
            return "Australia";
        } else {
            return "Unknown";
        }
    }
}