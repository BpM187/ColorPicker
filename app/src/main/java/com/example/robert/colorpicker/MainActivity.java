package com.example.robert.colorpicker;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button chooseImage;
    TextView rgbValue, hexValue;
    ImageView colorDisplay, selectedImage;
    String rgb, hex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializations();
    }

    public void initializations(){
        chooseImage = findViewById(R.id.choose_image);
        rgbValue = findViewById(R.id.rgb_color);
        hexValue = findViewById(R.id.hex_color);
        colorDisplay = findViewById(R.id.color_display);
        selectedImage = findViewById(R.id.selected_image);

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start intent for select image
                Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickImage, 1);
            }
        });

        selectedImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                try {

                    final int action = event.getAction(); // get touch
                    final int evX = (int) event.getX(); // get x coordinate
                    final int evY = (int) event.getY(); // get y coordinate

                    // get color from pixel
                    int touchColor = getColor(selectedImage, evX, evY);
                    // get R,G,B value of the pixel
                    int r = (touchColor >> 16) & 0xFF;
                    int g = (touchColor >> 8) & 0xFF;
                    int b = (touchColor >> 0) & 0xFF;
                    rgb = String.valueOf(r) + "," + String.valueOf(g) + "," + String.valueOf(b);

                    rgbValue.setText("RGB: " + rgb);

                    // get hex value
                    hex = Integer.toHexString(touchColor);
                    if (hex.length() > 2) {
                        hex = hex.substring(2, hex.length()); // remove alpha
                    }
                    if (action == MotionEvent.ACTION_UP) {
                        // set touch event
                        colorDisplay.setBackgroundColor(touchColor);
                        hexValue.setText("HEX: #" + hex);
                    }
                } catch(Exception e){

                }
                return true;
            }
        });
    }

    private int getColor(ImageView selectedImage, int evX, int evY) {
        selectedImage.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(selectedImage.getDrawingCache());
        selectedImage.setDrawingCacheEnabled(false);
        return bitmap.getPixel(evX, evY); // return the touched pixel
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && !data.equals(null)){
            Uri image = data.getData();
            selectedImage.setImageURI(image);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.picker_menu, menu); // inflate menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.btn_copy: copySelectedColor();
                break;
            case R.id.btn_share: shareColor();
                break;
            case R.id.btn_about: aboutMe();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void copySelectedColor(){
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Clip", "#" + hex);
        Toast.makeText(this, "copied #" + hex, Toast.LENGTH_LONG).show();
        if(clipboardManager != null)
            clipboardManager.setPrimaryClip(clip);
    }

    public void shareColor(){
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, "#" + hex);
        startActivity(Intent.createChooser(share, "Share hex Color"));
    }

    public void aboutMe(){
        Toast.makeText(this, "I am a simple programmer who is trying to do something", Toast.LENGTH_LONG).show();
    }
}
