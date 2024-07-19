package com.example.pdfannotations.pdf;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pdfannotations.R;
import com.example.pdfannotations.databinding.ActivityMainBinding;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDDocumentCatalog;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import com.tom_roush.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
import com.tom_roush.pdfbox.rendering.ImageType;
import com.tom_roush.pdfbox.rendering.PDFRenderer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    PDDocument document;
    ImageView markerView;
    int mPageCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//initialize Library
        PDFBoxResourceLoader.init(getApplicationContext());

        binding.pdfview.fromAsset("timer.pdf").show();

        try {
            document = PDDocument.load(getAssets().open("timer.pdf"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        binding.container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        addMarker(event.getX(), event.getY());
                        return true;
                    }
                }


                return false;
            }
        });

    }

    private void addMarker(float x, float y) {
        markerView = new ImageView(this);
        markerView.setImageResource(R.drawable.ic_mark);
        markerView.setMaxHeight(40);
        markerView.setMaxWidth(40);
        markerView.setAdjustViewBounds(true);
        markerView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        markerView.setX(x);
        markerView.setY(y);

        markerView.setLayoutParams(params);
        binding.container.addView(markerView);

        enableMarkerDrag(markerView);
    }


    private void enableMarkerDrag(final ImageView markerView) {
        markerView.setOnTouchListener(markerViewTouchListener);
    }

    View.OnTouchListener markerViewTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float dX = 0, dY = 0;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    dX = v.getX() - event.getRawX();
                    dY = v.getY() - event.getRawY();

                    Log.d("MAIN", "dX: " + dX);
                    Log.d("MAIN", "dY: " + dY);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float newX = event.getRawX() + dX;
                    float newY = event.getRawY() + dY;
                    v.setX(newX);
                    v.setY(newY);
                    //set value
                    return true;
//                case MotionEvent.ACTION_UP:
//                    try {
//                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_mark);
//                        PDImageXObject setImage = JPEGFactory.createFromImage(document, bitmap);
//                        setImage.setWidth(40);
//                        setImage.setHeight(40);
//                        PDPage pdPage = document.getDocumentCatalog().getPages().get(0);
//                        PDPageContentStream contentStream = new PDPageContentStream(document, pdPage, PDPageContentStream.AppendMode.APPEND, true, true);
//
//                        contentStream.drawImage(setImage,v.getX(),binding.imageView.getHeight() - v.getY() -40);
//                        contentStream.close();
//                        v.setVisibility(View.GONE);
//                        renderPdf();
//                    }catch (Exception e){
//
//                    }
//                    return true;
            }


            return false;
        }
    };

    private void zoom() {
        binding.pdfview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap annotationBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_mark);

                int x = (int) v.getX() + 50;
                int y = (int) v.getY() + 50;

                Canvas canvas = new Canvas(annotationBitmap);
                canvas.drawBitmap(annotationBitmap, x, y, null);

//                binding.pdfview.setImageBitmap(annotationBitmap);

            }
        });

    }
}