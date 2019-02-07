package com.example.android.barcode;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    SurfaceView cameraPreview;
    BarcodeDetector detector;
    CameraSource cameraSource;
    SurfaceHolder holder;

    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, 200);
        }


        cameraPreview = findViewById(R.id.camera_preview);
        text = findViewById(R.id.text);
        cameraPreview.setZOrderMediaOverlay(true);
        holder = cameraPreview.getHolder();

        cameraPreview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
      //  Box box = new Box(this);
      //  addContentView(box, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

//        holder.setFixedSize(getWindow().getWindowManager()
//                .getDefaultDisplay().getWidth(), getWindow().getWindowManager()
//                .getDefaultDisplay().getHeight());


        detector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        if(!detector.isOperational()){
            Toast.makeText(MainActivity.this,"Sorry,couldn't setup detector",Toast.LENGTH_SHORT).show();
            this.finish();

        }
        cameraSource = new CameraSource.Builder(this,detector).setFacing(CameraSource.CAMERA_FACING_BACK).setAutoFocusEnabled(true)
                .setRequestedFps(24).setRequestedPreviewSize(1600,1024).build();

        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try{
                    if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                        cameraSource.start(cameraPreview.getHolder());
                      //  DrawFocusRect(10,10,10,10,Color.BLUE);
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
             //   DrawFocusRect(10,10,10,10,Color.BLUE);
            }
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//                Canvas canvas = holder.lockCanvas();
//                if (canvas != null) {
//                    draw(canvas);
//                    holder.unlockCanvasAndPost(canvas);
//                }
//            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

                cameraSource.stop();

            }
        });

        detector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodeSparseArray = detections.getDetectedItems();
                if(barcodeSparseArray.size() != 0){
                    text.post(new Runnable() {
                        @Override
                        public void run() {
                            text.setText(barcodeSparseArray.valueAt(0).displayValue);
                            //Toast.makeText(MainActivity.this,barcodeSparseArray.valueAt(0).toString(),Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }

        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraSource.release();
        detector.release();
    }



    public void draw(Canvas canvas) {
        canvas.drawColor(Color.BLUE);
       // canvas.drawBitmap(this.bmp, 25, 25, null);
    }



    public class Box extends View {
        private Paint paint = new Paint();
        Box(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) { // Override the onDraw() Method
            super.onDraw(canvas);

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.RED);
            paint.setStrokeWidth(.1f);

            //center
            int x0 = canvas.getWidth()/2;
            int y0 = canvas.getHeight()/2;
            int dx = canvas.getHeight()/3;
            int dy = canvas.getHeight()/3;
            //draw guide box
            canvas.drawRect(x0-dx, y0-dy, x0+dx, y0+dy, paint);



        }
    }

    private void DrawFocusRect(float RectLeft, float RectTop, float RectRight, float RectBottom, int color)
    {

       Canvas canvas = holder.lockCanvas();
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        //border's properties
       Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(3);
        canvas.drawRect(RectLeft, RectTop, RectRight, RectBottom, paint);


        holder.unlockCanvasAndPost(canvas);
    }
}


