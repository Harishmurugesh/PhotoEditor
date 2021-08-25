package com.example.photoeditor2;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button btn1;
    Button btn2;
    Button btn3;
    Button btn4;
    Button btn5;
    EditText text;
    ImageView image;
    public ArrayList<Bitmap> bitmaps;
    public int count = 0;

    final int REQUEST = 1;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        text = (EditText) findViewById(R.id.text);
        btn1 = (Button) findViewById(R.id.selectImage);
        btn2 = (Button) findViewById(R.id.draw);
        btn3 = (Button) findViewById(R.id.blackandwhite);
        btn4 = (Button) findViewById(R.id.send);
        btn5 = (Button) findViewById(R.id.undo);
        image = (ImageView) findViewById(R.id.selectedImage);

        bitmaps = new ArrayList<>();


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent , REQUEST);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(uri != null && text.getText().toString()!=null && !text.getText().toString().equals(""))
                {
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) image.getDrawable();
                    Bitmap b = bitmapDrawable.getBitmap();
                    Bitmap processedBitmap = EditBitmap(b);
                    if(processedBitmap != null)
                    {
                        image.setImageBitmap(processedBitmap);
                        updateArray();
                        Toast.makeText(getApplicationContext() , "Done" , Toast.LENGTH_SHORT).show();

                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext() , "Enter Both Values" , Toast.LENGTH_SHORT).show();
                }
            }
        });


        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BitmapDrawable bitmapDrawable = (BitmapDrawable) image.getDrawable();

                if(bitmapDrawable != null) {
                    Bitmap bit = bitmapDrawable.getBitmap();
                    Bitmap newBit = blackWhite(bit);
                    image.setImageBitmap(newBit);
                    updateArray();

                }
                else
                {
                    Toast.makeText(getApplicationContext() ,"Select Image" , Toast.LENGTH_SHORT).show();
                }
            }
        });



        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BitmapDrawable bitmapDrawable = (BitmapDrawable) image.getDrawable();

                if(bitmapDrawable != null) {
                    Bitmap bit = bitmapDrawable.getBitmap();
                    send(bit);
                }
                else
                {
                    Toast.makeText(getApplicationContext() ,"Select Image" , Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(count==0)
                    Toast.makeText(getApplicationContext(),"Nothing to Undo",Toast.LENGTH_SHORT).show();
                else
                    deleteArray();
            }
        });


    }



    private Bitmap EditBitmap(Bitmap b) {
        Bitmap bm1 = null;
        Bitmap newBitmap = null;

        bm1 = b;
        Bitmap.Config config = bm1.getConfig();
        if (config == null) {
            config = Bitmap.Config.ARGB_8888;
        }

        newBitmap = Bitmap.createBitmap(bm1.getWidth(), bm1.getHeight(), config);
        Canvas newCanvas = new Canvas(newBitmap);

        newCanvas.drawBitmap(bm1, 0, 0, null);

        String captionString = text.getText().toString();

        if (captionString != null) {
            Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintText.setColor(Color.RED);
            paintText.setTextSize(400);
            paintText.setStyle(Paint.Style.FILL);
            paintText.setShadowLayer(10f, 10f, 10f, Color.BLACK);


            Rect rectText = new Rect();
            paintText.getTextBounds(captionString, 0, captionString.length(), rectText);

            newCanvas.drawText(captionString, 0, rectText.height(), paintText);

        } else {
            Toast.makeText(getApplicationContext(), "Enter the String", Toast.LENGTH_LONG).show();
        }


        return newBitmap;



    }



    public Bitmap blackWhite(Bitmap b){

        int height = b.getHeight();
        int width = b.getWidth();

        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter c = new ColorMatrixColorFilter(colorMatrix);

        Paint paint = new Paint();
        paint.setColorFilter(c);

        Bitmap finalBit = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(finalBit);
        canvas.drawBitmap(b, 0, 0, paint);


        return finalBit;
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK)
        {
            if(requestCode == REQUEST)
            {
                uri = data.getData();
                Log.i("Tag",""+uri);
                Bitmap bitmap1 = null;
                try
                {
                    bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                image.setImageBitmap(bitmap1);
                updateArray();
            }
        }

    }


    public void send(Bitmap b){

        String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(),b,"PHOTO EDITOR",null);
        Intent intent = new Intent(Intent.ACTION_SEND);
        Uri bitmapUri = Uri.parse(bitmapPath);

        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM,bitmapUri);
        intent.putExtra(Intent.EXTRA_TEXT,"PHOTO EDITOR APP");
        startActivity(Intent.createChooser(intent,"Share Image"));
    }

    public void updateArray(){

        BitmapDrawable bitmapDrawable = (BitmapDrawable) image.getDrawable();
        bitmaps.add(bitmapDrawable.getBitmap());
        ++count;

    }

    public void deleteArray(){

        bitmaps.remove(count-1);
        --count;
        if(count!=0)
            image.setImageBitmap(bitmaps.get(count-1));
        else
            Toast.makeText(getApplicationContext(),"Nothing to Undo",Toast.LENGTH_SHORT).show();

    }


}