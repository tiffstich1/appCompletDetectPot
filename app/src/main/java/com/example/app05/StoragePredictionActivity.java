package com.example.app05;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StoragePredictionActivity extends AppCompatActivity {

    private Button select_image;
    private Button take_im, ok;
    private ImageView image_v;
    private static TextView affiche, affiche2;
    private objectDetectorClass objectDetectorClass;
    int SELECT_PICTURE = 200;
    String currentPhotoPath;
    String currentImage;
    Uri photo;
    SharedPreferences sh ;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_prediction);

        take_im = findViewById(R.id.take_im);
        ok = findViewById(R.id.ok);
        select_image = findViewById(R.id.select_image);
        image_v = findViewById(R.id.image_v);
        affiche = findViewById(R.id.affiche);
        affiche2 = findViewById(R.id.affiche2);


        try {

            objectDetectorClass = new objectDetectorClass(getAssets(), "custom_model.tflite", "custom_label.txt", 320);
            Log.d("MainActivity", "Model is successfully loaded");
        } catch (IOException e) {
            Log.d("MainActivity", "Getting some error");
            e.printStackTrace();
        }


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPreferences = getSharedPreferences("MyComposant", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();

                // write all the data entered by the user in SharedPreference and apply
                myEdit.putString("transformateur", affiche.getText().toString());
                myEdit.putString("isolateur", affiche2.getText().toString());
                myEdit.apply();


                startActivity(new Intent(StoragePredictionActivity.this,AccueilActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
/*
                Intent intent = new Intent();
                intent.putExtra(Intent.EXTRA_TEXT, stringToPassBack);
                setResult(RESULT_OK, intent);
                finish();
*/
            }
        });
        take_im.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {



                try {
                    dispatchTakePictureIntent();
                    Toast.makeText(getApplicationContext(), "activite nei nei nei", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "activite faite", Toast.LENGTH_LONG).show();
                //galleryAddPic();


                    // Launch camera if we have permission


            }

        });

        select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                image_chooser();
            }
        });
    }


    private void image_chooser() {

        Intent i = new Intent();

        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {

           // image_v.setImageURI(Uri.parse(currentPhotoPath));

            if (photo != null) {

                Log.d("StoragePrediction", "Output_uri" + photo);

                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photo);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Mat selected_image = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8SC4);//CV_8SC4
                Utils.bitmapToMat(bitmap, selected_image);

                selected_image = objectDetectorClass.recognizePhoto(selected_image);

                Bitmap bitmap1 = null;
                bitmap1 = Bitmap.createBitmap(selected_image.cols(), selected_image.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(selected_image, bitmap1);

                image_v.setImageBitmap(bitmap1);

            }
            //currentImage = objectDetectorClass.recognizePhoto(currentPhotoPath);
/*


            if(resultCode==RESULT_OK){
                Bitmap image = null;


                    image = (Bitmap) data.getExtras().get("data");
                    if( image != null){



                        Mat selected_image = null;



                        selected_image = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8SC4);
                        Utils.bitmapToMat(image, selected_image);

                        selected_image=objectDetectorClass.recognizePhoto(selected_image);

                        Bitmap bitmap1=null;
                        bitmap1=Bitmap.createBitmap(selected_image.cols(),selected_image.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(selected_image, bitmap1);

                        image_v.setImageBitmap(bitmap1);

                    }*/

        } else {
            if (resultCode == RESULT_OK) {
                if (requestCode == SELECT_PICTURE) {
                    Uri selectedImageUri = data.getData();
                    if (selectedImageUri != null) {

                        Log.d("StoragePrediction", "Output_uri" + selectedImageUri);

                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Mat selected_image = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8SC4);//CV_8SC4
                        Utils.bitmapToMat(bitmap, selected_image);

                        selected_image = objectDetectorClass.recognizePhoto(selected_image);

                        Bitmap bitmap1 = null;
                        bitmap1 = Bitmap.createBitmap(selected_image.cols(), selected_image.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(selected_image, bitmap1);

                        image_v.setImageBitmap(bitmap1);

                    }
                }
            }

        }
    }

        static final int REQUEST_IMAGE_CAPTURE = 1;
/*
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

 */


        private File createImageFile () throws IOException {
            // Create an image file name

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,  // prefix
                    ".jpg",         //suffix
                    storageDir      // directory
            );

            // Save a file: path for use with ACTION_VIEW intents
            //  sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))));
            currentPhotoPath = image.getAbsolutePath();
            Log.i("test1", "dispatchTakePictureIntent: " + currentPhotoPath);
            return image;

        }

        private void dispatchTakePictureIntent () throws IOException {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            Toast.makeText(getApplicationContext(), "..........enter dans la classe", Toast.LENGTH_LONG).show();
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                photoFile = createImageFile();
                Log.i("test2", "dispatchTakePictureIntent: " + photoFile.toString());
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.android.fileprovider",
                            photoFile);
                    Log.i("test3", "dispatchTakePictureIntent: " + photoURI.toString());
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    photo=photoURI;
                    // galleryAddPic();

                }
            }
        }


        private void galleryAddPic () {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(currentPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
        }

        private void addToGallery () {
            Intent galleryIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(currentPhotoPath);
            Uri picUri = Uri.fromFile(f);
            galleryIntent.setData(picUri);
            this.sendBroadcast(galleryIntent);


        }


    public static class objectDetectorClass {
        // should start from small letter

        // this is used to load model and predict
        private Interpreter interpreter;
        // store all label in array
        private List<String> labelList;
        private int INPUT_SIZE;
        private int PIXEL_SIZE=3; // for RGB
        private int IMAGE_MEAN=0;
        private  float IMAGE_STD=255.0f;
        // use to initialize gpu in app
        private GpuDelegate gpuDelegate;
        private int height=0;
        private  int width=0;



        public objectDetectorClass(AssetManager assetManager, String modelPath, String labelPath, int inputSize) throws IOException{
            INPUT_SIZE=inputSize;
            // use to define gpu or cpu // no. of threads
            Interpreter.Options options=new Interpreter.Options();
            gpuDelegate=new GpuDelegate();
            options.addDelegate(gpuDelegate);
            options.setNumThreads(4); // set it according to your phone
            // loading model
            interpreter=new Interpreter(loadModelFile(assetManager,modelPath),options);
            // load labelmap
            labelList=loadLabelList(assetManager,labelPath);


        }

        private List<String> loadLabelList(AssetManager assetManager, String labelPath) throws IOException {
            // to store label
            List<String> labelList=new ArrayList<>();
            // create a new reader
            BufferedReader reader=new BufferedReader(new InputStreamReader(assetManager.open(labelPath)));
            String line;
            // loop through each line and store it to labelList
            while ((line=reader.readLine())!=null){
                labelList.add(line);
            }
            reader.close();
            return labelList;
        }

        private ByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
            // use to get description of file
            AssetFileDescriptor fileDescriptor=assetManager.openFd(modelPath);
            FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
            FileChannel fileChannel=inputStream.getChannel();
            long startOffset =fileDescriptor.getStartOffset();
            long declaredLength=fileDescriptor.getDeclaredLength();

            return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,declaredLength);
        }
        // create new Mat function
        public Mat recognizeImage(Mat mat_image){
            // Rotate original image by 90 degree get get portrait frame

            // This change was done in video: Does Your App Keep Crashing? | Watch This Video For Solution.
            // This will fix crashing problem of the app

            Mat rotated_mat_image=new Mat();

            Mat a=mat_image.t();
            Core.flip(a,rotated_mat_image,1);
            // Release mat
            a.release();

            // if you do not do this process you will get improper prediction, less no. of object
            // now convert it to bitmap
            Bitmap bitmap=null;
            bitmap=Bitmap.createBitmap(rotated_mat_image.cols(),rotated_mat_image.rows(),Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(rotated_mat_image,bitmap);
            // define height and width
            height=bitmap.getHeight();
            width=bitmap.getWidth();

            // scale the bitmap to input size of model
            Bitmap scaledBitmap=Bitmap.createScaledBitmap(bitmap,INPUT_SIZE,INPUT_SIZE,false);

            // convert bitmap to bytebuffer as model input should be in it
            ByteBuffer byteBuffer=convertBitmapToByteBuffer(scaledBitmap);

            // defining output
            // 10: top 10 object detected
            // 4: there coordinate in image
            //  float[][][]result=new float[1][10][4];
            Object[] input=new Object[1];
            input[0]=byteBuffer;

            Map<Integer,Object> output_map=new TreeMap<>();
            // we are not going to use this method of output
            // instead we create treemap of three array (boxes,score,classes)

            float[][][]boxes =new float[1][10][4];
            // 10: top 10 object detected
            // 4: there coordinate in image
            float[][] scores=new float[1][10];
            // stores scores of 10 object
            float[][] classes=new float[1][10];
            // stores class of object

            // add it to object_map;
            output_map.put(1,boxes);
            output_map.put(3,classes);
            output_map.put(0,scores);

            // now predict
            interpreter.runForMultipleInputsOutputs(input,output_map);
            // Before watching this video please watch my previous 2 video of
            //      1. Loading tensorflow lite model
            //      2. Predicting object
            // In this video we will draw boxes and label it with it's name

            Object value=output_map.get(1);
            Object Object_class=output_map.get(3);
            Object score=output_map.get(0);



            ///////////(ETO MAKA DONNEE IMAGE)

            // loop through each object
            // as output has only 10 boxes

            for (int i=0;i<10;i++){
                float class_value=(float) Array.get(Array.get(Object_class,0),i);
                float score_value=(float) Array.get(Array.get(score,0),i);
                // define threshold for score

                System.out.println("///////////////////////////////////////////////////");
                System.out.println(class_value);

                // Here you can change threshold according to your model
                // Now we will do some change to improve app
                if(score_value>0.3){
                    Object box1=Array.get(Array.get(value,0),i);
                    // we are multiplying it with Original height and width of frame

                    float top=(float) Array.get(box1,0)*height;
                    float left=(float) Array.get(box1,1)*width;
                    float bottom=(float) Array.get(box1,2)*height;
                    float right=(float) Array.get(box1,3)*width;
                    // draw rectangle in Original frame //  starting point    // ending point of box  // color of box       thickness
                    Imgproc.rectangle(rotated_mat_image,new Point(left,top),new Point(right,bottom),new Scalar(0, 255, 0, 255),2);
                    // write text on frame
                    // string of class name of object  // starting point                         // color of text           // size of text
                    Imgproc.putText(rotated_mat_image,labelList.get((int) class_value),new Point(left,top),3,1,new Scalar(255, 0, 0, 255),2);
                }

            }



            // select device and run

            // before returning rotate back by -90 degree

            // Do same here
            Mat b=rotated_mat_image.t();
            Core.flip(b,mat_image,0);
            b.release();
            // Now for second change go to CameraBridgeViewBase
            return mat_image;
        }

        public Mat recognizePhoto(Mat mat_image){



            // if you do not do this process you will get improper prediction, less no. of object
            // now convert it to bitmap
            Bitmap bitmap=null;
            bitmap=Bitmap.createBitmap(mat_image.cols(),mat_image.rows(),Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mat_image,bitmap);
            // define height and width
            height=bitmap.getHeight();
            width=bitmap.getWidth();

            //***************************************///////////////////
            // scale the bitmap to input size of model
            Bitmap scaledBitmap=Bitmap.createScaledBitmap(bitmap,INPUT_SIZE,INPUT_SIZE,false);

            // convert bitmap to bytebuffer as model input should be in it
            ByteBuffer byteBuffer=convertBitmapToByteBuffer(scaledBitmap);

            // defining output
            // 10: top 10 object detected
            // 4: there coordinate in image
            //  float[][][]result=new float[1][10][4];
            Object[] input=new Object[1];
            input[0]=byteBuffer;

            Map<Integer,Object> output_map=new TreeMap<>();
            // we are not going to use this method of output
            // instead we create treemap of three array (boxes,score,classes)

            float[][][]boxes =new float[1][10][4];
            // 10: top 10 object detected
            // 4: there coordinate in image
            float[][] scores=new float[1][10];
            // stores scores of 10 object
            float[][] classes=new float[1][10];
            // stores class of object

            // add it to object_map;
            output_map.put(1,boxes);
            output_map.put(3,classes);
            output_map.put(0,scores);

            // now predict
            interpreter.runForMultipleInputsOutputs(input,output_map);
            // Before watching this video please watch my previous 2 video of
            //      1. Loading tensorflow lite model
            //      2. Predicting object
            // In this video we will draw boxes and label it with it's name

            Object value=output_map.get(1);
            Object Object_class=output_map.get(3);
            Object score=output_map.get(0);



            ///////////(ETO MAKA DONNEE IMAGE)

            // loop through each object
            // as output has only 10 boxes
            int isolateur=0;
            int transformateur=0;
            int poteau=0;
            for (int i=0;i<10;i++) {
                float class_value = (float) Array.get(Array.get(Object_class, 0), i);
                float score_value = (float) Array.get(Array.get(score, 0), i);
                // define threshold for score
             /*   if (class_value == 0.0) {
                    isolateur++;
                } else if (class_value == 1.0) {
                    transformateur++;
                } else if (class_value == 2.0) {
                    poteau++;
                }*/
                System.out.println("///////////////////////////////////////////////////");
                System.out.println(class_value);

                // Here you can change threshold according to your model
                // Now we will do some change to improve app
                if (score_value > 0.3) {
                    Object box1 = Array.get(Array.get(value, 0), i);
                    // we are multiplying it with Original height and width of frame

                    float top = (float) Array.get(box1, 0) * height;
                    float left = (float) Array.get(box1, 1) * width;
                    float bottom = (float) Array.get(box1, 2) * height;
                    float right = (float) Array.get(box1, 3) * width;
                    // draw rectangle in Original frame //  starting point    // ending point of box  // color of box       thickness
                    Imgproc.rectangle(mat_image, new Point(left, top), new Point(right, bottom), new Scalar(0, 255, 0, 255), 2);
                    // write text on frame
                    // string of class name of object  // starting point                         // color of text           // size of text
                    Imgproc.putText(mat_image, labelList.get((int) class_value), new Point(left, top), 3, 1, new Scalar(255, 0, 0, 255), 2);

                    if (class_value == 0.0) {
                        isolateur++;
                    } else if (class_value == 1.0) {
                        transformateur++;
                    } else if (class_value == 2.0) {
                        poteau++;
                    }


                }


            }
            affiche.setText("transformateur  "+transformateur);
            affiche2.setText("isolateur  "+isolateur);




            return mat_image;
        }



        private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
            ByteBuffer byteBuffer;
            // some model input should be quant=0  for some quant=1
            // for this quant=0
            // Change quant=1
            // As we are scaling image from 0-255 to 0-1

            int quant=1;
            int size_images=INPUT_SIZE;
            if(quant==0){
                byteBuffer=ByteBuffer.allocateDirect(1*size_images*size_images*3);
            }
            else {
                byteBuffer=ByteBuffer.allocateDirect(4*1*size_images*size_images*3);
            }
            byteBuffer.order(ByteOrder.nativeOrder());
            int[] intValues=new int[size_images*size_images];
            bitmap.getPixels(intValues,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
            int pixel=0;

            // some error
            //now run
            for (int i=0;i<size_images;++i){
                for (int j=0;j<size_images;++j){
                    final  int val=intValues[pixel++];
                    if(quant==0){
                        byteBuffer.put((byte) ((val>>16)&0xFF));
                        byteBuffer.put((byte) ((val>>8)&0xFF));
                        byteBuffer.put((byte) (val&0xFF));
                    }
                    else {
                        // paste this
                        byteBuffer.putFloat((((val >> 16) & 0xFF))/255.0f);
                        byteBuffer.putFloat((((val >> 8) & 0xFF))/255.0f);
                        byteBuffer.putFloat((((val) & 0xFF))/255.0f);
                    }
                }
            }
            return byteBuffer;
        }
    }

}

