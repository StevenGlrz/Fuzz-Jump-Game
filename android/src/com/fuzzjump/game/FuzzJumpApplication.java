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

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.fuzzjump.game.game.StageScreen;
import com.fuzzjump.game.game.screens.SplashScreen;
import com.fuzzjump.game.net.GameSession;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FuzzJumpApplication extends AndroidApplication {

    private FuzzJump game;
    private View gameView;
    private RelativeLayout layout;
    private ImageView splashScreen;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String ip = "fj-matchmakingserver-462470304.us-west-2.elb.amazonaws.com";
            int port = 6893;
            if (extras.containsKey("ip"))
                ip = extras.getString("ip");
            if (extras.containsKey("port"))
                port = Integer.parseInt(extras.getString("port"));
            GameSession.MATCHMAKING_IP = ip;
            GameSession.MATCHMAKING_PORT = port;
            if (extras.containsKey("passthrough")) {
                SplashScreen.PASS_THROUGH = extras.getBoolean("passthrough", true);
            }
        }
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.fuzzjump.game", PackageManager.GET_SIGNATURES);
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
        try {
            game = new FuzzJump(executor, new AndroidGraphicsLoader(executor, getCachePath()));
            layout = new RelativeLayout(this);

            gameView = initializeForView(game, config);
            layout.addView(gameView);
            setContentView(layout);

            /*graphics.getView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                private boolean keyboardOpen = false;
                @Override
                public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (game != null && game.getStage() != null) {
                        if (!keyboardOpen) {
                            keyboardOpen = true;
                        } else if (keyboardOpen) {
                            keyboardOpen = false;
                            closeKeyboard();
                        }
                        if (keyboardOpen && game.getStage().getKeyboardFocus() == null) {
                            keyboardOpen = false;
                        }
                    }
                }
            });*/
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        game.onPause();

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void closeKeyboard() {
        if (game != null)
            ((StageScreen) game.getScreen()).closeKeyboard();
    }

    @Override
    public void onBackPressed() {
        ((StageScreen) game.getScreen()).backPressed();
    }

    public void openURL(String link) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(intent);
    }

}
