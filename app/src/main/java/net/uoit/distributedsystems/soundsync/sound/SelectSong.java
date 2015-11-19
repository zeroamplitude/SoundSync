package net.uoit.distributedsystems.soundsync.sound;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import net.uoit.distributedsystems.soundsync.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ubuntu on 19/11/15.
 */
public class SelectSong extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_song);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK){
            Uri uri = data.getData();
            try {
                InputStream in = getContentResolver().openInputStream(uri);
                byte[] inputData = getBytes(in);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("byteArr", inputData);
                setResult(RESULT_OK, returnIntent);
            } catch (IOException ioe) {
                System.out.print("Error: Unable to retrieve sound file");
                ioe.printStackTrace();
            } finally {
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public void selectSong(View view) {
        Intent getSongIntent = new Intent();
        getSongIntent.setType("audio/*");
        getSongIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(getSongIntent,"Select Audio "), 1);
    }
}
