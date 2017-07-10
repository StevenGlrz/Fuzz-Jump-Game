package com.fuzzjump.game;


import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.fuzzjump.game.platform.PlatformModule;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FuzzJumpApplication extends AndroidApplication {

    private FuzzJump fuzzJump;
    private View gameView;
    private RelativeLayout layout;
    private ImageView splashScreen;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);


        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.fuzzjump.fuzzJump", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);


        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = true;
        config.useCompass = true;
        config.numSamples = 2;

        ExecutorService executor = Executors.newFixedThreadPool(2);
            fuzzJump = new FuzzJump(
                    //TODO!!
                    new FuzzJumpParams("", "", 9),
                    new PlatformModule(new AndroidGraphicsLoader(Gdx.files.getExternalStoragePath()))
            );
            layout = new RelativeLayout(this);

            gameView = initializeForView(fuzzJump, config);
            layout.addView(gameView);
            setContentView(layout);

            /*graphics.getView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                private boolean keyboardOpen = false;
                @Override
                public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (fuzzJump != null && fuzzJump.getStage() != null) {
                        if (!keyboardOpen) {
                            keyboardOpen = true;
                        } else if (keyboardOpen) {
                            keyboardOpen = false;
                            closeKeyboard();
                        }
                        if (keyboardOpen && fuzzJump.getStage().getKeyboardFocus() == null) {
                            keyboardOpen = false;
                        }
                    }
                }
            });*/
    }

    private String getCachePath() throws IOException {
        String path = getFilesDir().getAbsolutePath();
        File file = new File(path, "Kerpow");
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getCanonicalPath();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        closeKeyboard();
        //fuzzJump.onPause();

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void closeKeyboard() {
        //if (fuzzJump != null)
            //((StageScreen) fuzzJump.getScreen()).closeKeyboard();
    }

    @Override
    public void onBackPressed() {
        //((StageScreen) fuzzJump.getScreen()).backPressed();
    }

    public void openURL(String link) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(intent);
    }

}
