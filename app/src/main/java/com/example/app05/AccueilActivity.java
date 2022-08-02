package com.example.app05;


import static android.location.LocationManager.GPS_PROVIDER;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.app05.Controllers.FirebaseDAO;
import com.example.app05.Controllers.LocationDAO;
import com.example.app05.Models.Location;
import com.example.app05.Models.StringArrayModel;
import com.example.app05.ml.Model;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AccueilActivity extends AppCompatActivity implements  LocationListener{
    //Partie Localisation
    private LocationManager locationManager;
    private LocationListener locationListener;
    private TextView AddressText;
    private Button locationButton;
    private LocationRequest locationRequest;
    private FirebaseAuth mAuth;
    private Button btnLogout;
    private Button synchroButton;
    private ProgressBar progressBar;
    private Button composant;
    private Spinner spinner;

    private String poteau;
    private String poteauMl;

    private String inconnu01;
    private String inconnu02;


    private String longitude;
    private String latitude;
    private Handler handler = new Handler();

    private int section;



    //Partie ML
    TextView result, result1, numero , sectionPot;
    ImageView imageView;
    Button picture;
    ImageButton delete;
    int imageSize = 224;
    int i ,sect, rang;

    //section
    LinearLayout layout;
    Button add;
    private Button retour;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        //////////////sharedpreference
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        String se1 = sh.getString("numero", null);
        String a1 = sh.getString("sectionPot", null);



        final View view = getLayoutInflater().inflate(R.layout.activity_accueil, null);

        //poteau = StringArrayModel.courses[0];
        inconnu01 = "inconnu";
        inconnu02 = "Poteau en ...";
        i = 0;

        //section
        layout = findViewById(R.id.container);
        add =(Button) findViewById(R.id.add);

        mAuth = FirebaseAuth.getInstance();


        //section
        retour=findViewById(R.id.retour);

        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();


            }
        });
        if(se1 != null && a1 != null){
            final View view2 = getLayoutInflater().inflate(R.layout.activity_accueil, null);
                /*delete = view.findViewById(R.id.delete);
                locationButton =(Button) view.findViewById(R.id.locationButton);
                btnLogout = (Button) view.findViewById(R.id.btnLogout);
                AddressText =(TextView) view.findViewById(R.id.addressText);
                synchroButton = (Button) view.findViewById(R.id.synchroButton);
*/

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy / hh.mm.ss.SSS", Locale.getDefault());
            String date = sdf.format(new Date());
            i=1;

            //section= i;
            listener( view2);

            numero.setText("Rang "+(i));
            sectionPot.setText("Section "+longitude +"/"+latitude +"/"+ date);
            rang = i;
            layout.addView(view2);
        }
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = getLayoutInflater().inflate(R.layout.activity_accueil, null);
                /*delete = view.findViewById(R.id.delete);
                locationButton =(Button) view.findViewById(R.id.locationButton);
                btnLogout = (Button) view.findViewById(R.id.btnLogout);
                AddressText =(TextView) view.findViewById(R.id.addressText);
                synchroButton = (Button) view.findViewById(R.id.synchroButton);
*/

                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy / hh.mm.ss.SSS", Locale.getDefault());
                String date = sdf.format(new Date());
               i=1;

                //section= i;
                listener( view);

                numero.setText("Rang "+(i));
                sectionPot.setText("Section "+longitude +"/"+latitude +"/"+ date);
                rang = i;
                layout.addView(view);

            }
        });





        /*Getting Authorisation*/
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
            } else {
                // do request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 8);

            }
        }







        getLocation();




    }

    private void listener(View view)
    {

        //Partie ML
        result = view.findViewById(R.id.result);
        result1 = view.findViewById(R.id.result1);
        imageView = view.findViewById(R.id.imageView);
        picture = view.findViewById(R.id.button);

        numero = view.findViewById(R.id.numero);
        sectionPot = view.findViewById(R.id.sectionPot);

        delete = view.findViewById(R.id.delete);
        locationButton =(Button) view.findViewById(R.id.locationButton);
        btnLogout = (Button) view.findViewById(R.id.btnLogout);
        AddressText =(TextView) view.findViewById(R.id.addressText);
        synchroButton = (Button) view.findViewById(R.id.synchroButton);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        progressBar.setIndeterminate(false);
        composant = (Button) view.findViewById(R.id.composant);
        spinner= (Spinner) view.findViewById(R.id.coursesspinner);

        //Partie ML
       /* result = view.findViewById(R.id.result);
        result1 = view.findViewById(R.id.result1);
        imageView = view.findViewById(R.id.imageView);
        picture = view.findViewById(R.id.button);
*/
////////////////////////////////////////////////////////////////////////
        composant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    Intent intent = new Intent(getApplicationContext(), DetectionActivity.class);
                    startActivity(intent);


            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.removeView(view);


            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                numero = view.findViewById(R.id.numero);
                sectionPot = view.findViewById(R.id.sectionPot);
                spinner= (Spinner) view.findViewById(R.id.coursesspinner);



                numero.setText("Rang "+i);

                String s = numero.getText().toString();
                String p = sectionPot.getText().toString();

                int section= parseInt(s.split(" ")[1]);
                int pos = spinner.getSelectedItemPosition();
                if(poteauMl == null) {
                    poteau = StringArrayModel.courses[pos];
                }else{
                    poteau = poteauMl;
                    poteauMl=null;
                }


                Toast.makeText(getApplicationContext(), ""+poteau, Toast.LENGTH_LONG).show();
                getLocation();

                System.out.println("111/////////////"+poteau);
                if (poteau != inconnu02){
                    if(poteau != inconnu01) {
                        if (longitude != null) {

                            SharedPreferences sh = getSharedPreferences("MyComposant", MODE_PRIVATE);

                            String trans = sh.getString("transformateur", "");
                            String iso = sh.getString("isolateur", "");

                              if(trans != "" && iso != ""){

                                System.out.println("222/////////////"+poteau);
                                FirebaseDAO firebaseDAO = new FirebaseDAO();
                                Location location = new Location();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy / hh.mm.ss.SSS", Locale.getDefault());
                                String date = sdf.format(new Date());
                                LocationDAO locationDAO = new LocationDAO(getApplicationContext());
                                location.setLongitude(parseDouble(longitude.trim()));
                                location.setLatitude(parseDouble(latitude.trim()));
                                location.setIdUser(firebaseDAO.getUserUID());
                                location.setDate(date);
                                location.setPoteau(poteau);
                                location.setSection(p);
                                location.setRang(section);
                                location.setComposant("  "+trans+" / / "+iso);
                                locationDAO.insertLocation(location);

                                ArrayList<Location> arrayList = locationDAO.selectAllLocation();

                                  if (longitude != null && trans != "") {
                                      i++;
                                  }

                                  sh.edit().remove("transformateur").commit();
                                  sh.edit().remove("isolateur").commit();


                                if (arrayList.size() > 0) {
                                    Toast.makeText(getApplicationContext(), "Enrregistrement avec succee", Toast.LENGTH_LONG).show();
                                }

                              } else {
                                  Toast.makeText(getApplicationContext(), "Veuillez entrer le composant", Toast.LENGTH_LONG).show();
                              }
                            trans = "";
                            iso = "";

                        } else {
                            Toast.makeText(getApplicationContext(), "En attente de la localisation", Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(getApplicationContext(), "Erreur poteau inconnu", Toast.LENGTH_LONG).show();

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Erreur poteau inconnu", Toast.LENGTH_LONG).show();
                }

            }


        });
        synchroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                doStartProgressBar2();

                LocationDAO locationDAO = new LocationDAO(getApplicationContext());
                ArrayList<Location> arrayList = locationDAO.selectAllLocation();

                if(internetConnectionAvailability())
                {
                    if(arrayList.size()>0)
                    {
                        //synchroButton.setEnabled(false);
                        for (int i=0; i<arrayList.size(); i++)
                        {
                            sendLocation(arrayList.get(i));

                        }

                    }else
                    {
                        Toast.makeText(getApplicationContext(),"Le stockage interne est vide",Toast.LENGTH_LONG).show();
                    }
                   // synchroButton.setEnabled(true);
                }else
                {
                    Toast.makeText(getApplicationContext(),"Pas de connexion internet",Toast.LENGTH_LONG).show();
                }



            }
        });

        //Partie ML
        picture.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                result = view.findViewById(R.id.result);
                result1 = view.findViewById(R.id.result1);
                imageView = view.findViewById(R.id.imageView);
                // Launch camera if we have permission
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);
                } else {
                    //Request camera permission if we don't have it.
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);

                }

            }
        });

        // LIste deroulante(SPINNER)
        ArrayAdapter<String> commandsTable = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, StringArrayModel.courses) ;
        spinner.setAdapter(commandsTable);
        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int position = spinner.getSelectedItemPosition();
                  poteau = StringArrayModel.courses[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });





    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getLocation()
    {
        try{
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(GPS_PROVIDER, 1000, 1, (LocationListener) this);

        } catch (SecurityException e)
        {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
            Log.d("Test2", e.getMessage());
        }
    }


    @Override
    public void onLocationChanged(@NonNull android.location.Location location) {
        AddressText =(TextView) findViewById(R.id.addressText);
        if (AddressText != null) {
            AddressText.setText("Latitude:" + location.getLatitude() + ", \nLongitude:" + location.getLongitude());
        }
        longitude =""+ location.getLongitude();
        latitude =""+ location.getLatitude();
        //Toast.makeText(getApplicationContext(), "Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(@NonNull List<android.location.Location> locations) {
        //LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Toast.makeText(getApplicationContext(),"Localisation désactivé",Toast.LENGTH_LONG).show();
        Log.d("Test2", "Localisation désactivé");
    }

    private void sendLocation(Location location)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Location")
                .add(location)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        LocationDAO locationDAO = new LocationDAO(getApplicationContext()) ;
                        locationDAO.deleteLocation(location.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Erreur lors de la synchronisation",Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean internetConnectionAvailability()
    {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }


    //Partie ML

public void classifyImage(Bitmap image) {
    try {
        Model model = Model.newInstance(getApplicationContext());

        // Creates inputs for reference.
        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] intValues = new int[imageSize * imageSize];
        image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
        int pixel = 0;
        for (int i = 0; i < imageSize; i++) {
            for (int j = 0; j < imageSize; j++) {
                int val = intValues[pixel++];
                byteBuffer.putFloat(((val >> 16) & 0xFF) * (1f / 255.f));
                byteBuffer.putFloat(((val >> 8) & 0xFF) * (1f / 255.f));
                byteBuffer.putFloat((val & 0xFF) * (1f / 255.f));
            }

        }

        inputFeature0.loadBuffer(byteBuffer);

        // Runs model inference and gets result.
        Model.Outputs outputs = model.process(inputFeature0);
        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

        float[] confidences = outputFeature0.getFloatArray();
        int emp;
        int maxPos = 0;
        float maxConfidence = 0;
        for (int i = 0; i < confidences.length; i++) {
            if (confidences[i] > maxConfidence) {
                maxConfidence = confidences[i];
                maxPos = i;

            }
        }
        String[] classes = {"Bois", "Beton", "Beton_avec_transfo", "Pylone", "inconnu"};
        result.setText(classes[maxPos]);
            emp = maxPos;
                   if (emp >= 0) {
                       poteauMl = StringArrayModel.courses[maxPos];
                   }


        String s = "";
        for (int i = 0; i < classes.length; i++) {
            s += String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100);
        }
        result1.setText(s);

        // Releases model resources if no longer used.
        model.close();
    } catch (IOException e) {
        // TODO Handle the exception
    }
}
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }

    protected void onPause() {
        super.onPause();

        // Creating a shared pref object
        // with a file name "MySharedPref"
        // in private mode

    }
    private void doStartProgressBar2()  {
        this.progressBar.setIndeterminate(true);

        Thread thread = new Thread(new Runnable()  {

            @Override
            public void run() {
                // Update interface
                handler.post(new Runnable() {
                    public void run() {

                        synchroButton.setEnabled(false);
                    }
                });
                // Do something ... (Update database,..)
                SystemClock.sleep(5000); // Sleep 5 seconds.

                progressBar.setIndeterminate(false);
                progressBar.setMax(1);
                progressBar.setProgress(1);

                // Update interface
                handler.post(new Runnable() {
                    public void run() {

                        synchroButton.setEnabled(true);
                    }
                });
            }
        });
        thread.start();
    }
}

