package com.example.match4game;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Settings extends AppCompatActivity {
    private final int[] playerColors = {R.color.playerColor1, R.color.playerColor2,
            R.color.playerColor3, R.color.playerColor4, R.color.playerColor5,
            R.color.playerColor6, R.color.playerColor7, R.color.playerColor8};
    private Spinner spnP1Color, spnP2Color;
    private ImageView imgColorPreview1, imgColorPreview2;
    private SeekBar skbVolume;
    private TextView txtVolume;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String SPINNER1 = "spinner1";
    private static final String SPINNER2 = "spinner2";
    private static final String PLAYER_1_COLOR = "player1Color";
    private static final String PLAYER_2_COLOR = "player2Color";
    private static final String VOLUME = "volume";
    private int selectionBuffer1, selectionBuffer2;
    private SoundPool soundPool;
    private int sound1, sound2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        spnP1Color = findViewById(R.id.spnP1Color);
        spnP2Color = findViewById(R.id.spnP2Color);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.colors, R.layout.spinner_layout);
        spnP1Color.setAdapter(adapter);
        spnP2Color.setAdapter(adapter);
        spnP1Color.getBackground().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        spnP2Color.getBackground().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        imgColorPreview1 = findViewById(R.id.imgColorPreview1);
        imgColorPreview2 = findViewById(R.id.imgColorPreview2);
        skbVolume = findViewById(R.id.skbVolume);
        txtVolume = findViewById(R.id.txtVolume);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(2)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else { soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0); }
        sound1 = soundPool.load(this, R.raw.place_piece, 1);
        sound2 = soundPool.load(this, R.raw.player_win, 1);

        spnP1Color.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == selectionBuffer2) {
                    spnP1Color.setSelection(selectionBuffer1);
                    Toast.makeText(Settings.this, "This color is already in use!", Toast.LENGTH_SHORT).show();
                }else {
                    imgColorPreview1.setColorFilter(getResources().getColor(playerColors[position]));
                    selectionBuffer1 = position;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spnP2Color.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == selectionBuffer1) {
                    spnP2Color.setSelection(selectionBuffer2);
                    Toast.makeText(Settings.this, "This color is already in use!", Toast.LENGTH_SHORT).show();
                }else {
                    imgColorPreview2.setColorFilter(getResources().getColor(playerColors[position]));
                    selectionBuffer2 = position;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        skbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { txtVolume.setText(progress + "%"); }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { soundPool.play(sound1, skbVolume.getProgress() / 100.0f, skbVolume.getProgress() / 100.0f, 0, 0, 1); }
        });

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            saveData();
            goMainMenu();
        });

        Button btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(v -> goMainMenu());

        if(savedInstanceState != null) { restoreData(savedInstanceState); }
        else { loadData(); }
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SPINNER1, spnP1Color.getSelectedItemPosition());
        editor.putInt(SPINNER2, spnP2Color.getSelectedItemPosition());
        editor.putInt(PLAYER_1_COLOR, playerColors[spnP1Color.getSelectedItemPosition()]);
        editor.putInt(PLAYER_2_COLOR, playerColors[spnP2Color.getSelectedItemPosition()]);
        editor.putInt(VOLUME, skbVolume.getProgress());
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        selectionBuffer1 = sharedPreferences.getInt(SPINNER1, 0);
        selectionBuffer2 = sharedPreferences.getInt(SPINNER2, 1);
        spnP1Color.setSelection(selectionBuffer1);
        spnP2Color.setSelection(selectionBuffer2);
        imgColorPreview1.setColorFilter(getResources().getColor(playerColors[selectionBuffer1]));
        imgColorPreview2.setColorFilter(getResources().getColor(playerColors[selectionBuffer2]));
        skbVolume.setProgress(sharedPreferences.getInt(VOLUME, 100));
        txtVolume.setText(sharedPreferences.getInt(VOLUME, 100) + "%");
    }

    private void restoreData(Bundle savedInstanceState) {
        selectionBuffer1 = savedInstanceState.getInt(SPINNER1);
        selectionBuffer2 = savedInstanceState.getInt(SPINNER2);
        spnP1Color.setSelection(selectionBuffer1);
        spnP2Color.setSelection(selectionBuffer2);
        imgColorPreview1.setColorFilter(getResources().getColor(playerColors[selectionBuffer1]));
        imgColorPreview2.setColorFilter(getResources().getColor(playerColors[selectionBuffer2]));
        skbVolume.setProgress(savedInstanceState.getInt(VOLUME));
        txtVolume.setText(savedInstanceState.getInt(VOLUME) + "%");
    }

    private void goMainMenu() { finish(); }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SPINNER1, spnP1Color.getSelectedItemPosition());
        outState.putInt(SPINNER2, spnP2Color.getSelectedItemPosition());
        outState.putInt(VOLUME, skbVolume.getProgress());
    }
}