package com.fuzzjump.game.net.requests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.fuzzjump.game.game.ui.components.FuzzFeed;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Steven on 12/10/2014.
 */
public class FuzzFeedWebRequest extends WebRequest {

    private FuzzFeed feed;

    public FuzzFeedWebRequest(FuzzFeed feed) {
        this.feed = feed;
    }

    @Override
    public void connect(WebRequestCallback callback) {
        Net.HttpRequest httpGet = new Net.HttpRequest(Net.HttpMethods.GET);
        httpGet.setUrl(API_URL + "action/?action=fuzzfeed");
        httpGet.setContent(parameters.toString());
        Gdx.net.sendHttpRequest(httpGet, this);
    }

    @Override
    public void handleHttpResponse(Net.HttpResponse httpResponse) {
        /*try {
            JSONArray data = new JSONArray(httpResponse.getResultAsString());
            for (int i = 0; i < data.length(); i++) {
                final JSONObject post = data.getJSONObject(i);

                final ImageWebRequest image = new ImageWebRequest(post.getString("image_link"));
                final String redirectURL = post.getString("redirect_link");
                image.connect(new WebRequestCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        feed.addPost(image.getTexture(), redirectURL);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void failed(Throwable t) {
        t.printStackTrace();
    }

    @Override
    public void cancelled() {
    }


}
