package com.example.android.firebaseupload;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
//import java.net.URL;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;

public class MainActivity extends AppCompatActivity {
    public static final int pickimage = 123;
    private Uri filepath;
    Button choose, upload, download;
    ImageView img;
    TextView dir;
    private Uri url;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        choose = (Button) findViewById(R.id.btn_choose);
        upload = (Button) findViewById(R.id.btn_upload);
        img = (ImageView) findViewById(R.id.imageView);
        download = (Button) findViewById(R.id.btn_download);
        storageReference = FirebaseStorage.getInstance().getReference();
        dir = (TextView) findViewById(R.id.dir);
        //for choosing file
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showfile();
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download();
            }
        });

//for upload
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadfile();
            }
        });

    }


    private void download() {


        try {
            final File localFile = File.createTempFile("application", "pdf");


            StorageReference riversRef = storageReference.child("pdf/c#.pdf");
            riversRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Successfully downloaded data to local file
                            // ...
                            String x = localFile.getAbsolutePath();
                            Toast.makeText(MainActivity.this, "Hey, its downloaded in " + x, Toast.LENGTH_LONG).show();
                            dir.setText(x);
                            ////////////
//                            File file = new File(x);
//                            Intent intent = new Intent(Intent.ACTION_VIEW);
//                            intent.setDataAndType(Uri.fromFile(file), "application/pdf");
//                           // intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                            startActivity(intent);

                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
                            startActivity(browserIntent);
                            ////////////
                            //String gt = taskSnapshot.toString();
                            // Toast.makeText(MainActivity.this, gt, Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle failed download
                    // ...
                    Toast.makeText(MainActivity.this, "Didnt download", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void uploadfile() {
        if (filepath != null) {


            //Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg")); //we already have the uri
            final StorageReference riversRef = storageReference.child("pdf/c2#.pdf");

            riversRef.putFile(filepath)//filepath here is the URI object
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();
                            url = taskSnapshot.getDownloadUrl();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                            Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            // Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
        }

    }

    private void showfile() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select an image"), pickimage);
    }


    //executed after image is selected
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == pickimage && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filepath = data.getData();//enough to upload...but still have to display image in app hence more code
            try {
                //  File file = new File("kotlin-docs.pdf");//####
                //init array with file length//####
                // byte[] bytesArray = new byte[(int) file.length()];//#####
                // PDDocument doc = PDDocument.load(new File (new URL("file:///Internal storage/Download/Good day.pdf").getFile()));
                // Toast.makeText(MainActivity.this,"Number of pages: "+doc.getNumberOfPages(),Toast.LENGTH_LONG).show();


                ////////////////////////**********

                // File file = new File( new URL(filepath.getPath()).getFile());

                //init array with file length
                // byte[] bytesArray = new byte[(int) file.length()];
                PDDocument doc = PDDocument.load(getContentResolver().openInputStream(filepath));

                Toast.makeText(MainActivity.this, "Number of pages: " + doc.getNumberOfPages(), Toast.LENGTH_LONG).show();
                Toast.makeText(MainActivity.this, "Number of pages: " + doc.getDocumentInformation().getTitle(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage() + "Filepath: " + filepath.getLastPathSegment(), Toast.LENGTH_LONG).show();
                Toast.makeText(MainActivity.this, e.getMessage() + "Filepath: " + filepath.getPath(), Toast.LENGTH_LONG).show();
                Log.e("file", e.getMessage().toString());
                Log.e("file", e.getLocalizedMessage());
                Log.e("file", e.getStackTrace().toString());
            }
            try {
                Bitmap btm = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                img.setImageBitmap(btm);
            } catch (Exception e) {

            }
        }
    }
}
