package com.fuzzjump.game.net.requests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Steven on 12/12/2014.
 */
public class ImageWebRequest extends WebRequest {

    private Texture texture;
    private String imageURL;

    public ImageWebRequest(String imageURL) {
        this.imageURL = imageURL;
    }

    @Override
    public void connect(WebRequestCallback callback) {
        this.callback = callback;

        Net.HttpRequest httpGet = new Net.HttpRequest(Net.HttpMethods.GET);
        httpGet.setUrl(imageURL);
        Gdx.net.sendHttpRequest(httpGet, this);
    }

    @Override
    public void handleHttpResponse(Net.HttpResponse httpResponse) {
        final byte[] image = httpResponse.getResult();
        Gdx.app.postRunnable(new Runnable() {
            public void run () {
                Pixmap pixmap = new Pixmap(image, 0, image.length);
                texture = new Texture(pixmap);
                callback.onResponse(null);
            }
        });
    }

    public Texture getTexture() {
        return texture;
    }

    @Override
    public void failed(Throwable throwable) {

    }

    @Override
    public void cancelled() {

    }
}
