package com.fuzzjump.game.game.ui.components;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

/**
 * Created by stephen on 12/15/2014.
 */
public class NotificationEntry extends Table {

    private final NotificationType type;
    private final long id;
    private final Image fuzzImage;
    private final Label playerName;
    private final TextButton button1;
    private final TextButton button2;

    public enum NotificationType {

        FRIEND_REQUEST("Friend request from ", "Accept", "Decline"),
        GAME_REQUEST("Game invite from ", "Join", "Decline");

        private final String title;
        private final String button1;
        private final String button2;

        NotificationType(String title, String button1, String button2) {
            this.title = title;
            this.button1 = button1;
            this.button2 = button2;
        }
    }

    public NotificationEntry(NotificationType type, Skin skin, String name, long id) {
        this.type = type;
        this.fuzzImage = new Image(skin.getDrawable("fuzzle0"), Scaling.fillY, Align.center);
        this.playerName = new Label(type.title + name, skin, "small");
        playerName.setAlignment(Align.center);
        this.button1 = new TextButton(type.button1, skin, "small-text");
        this.button2 = new TextButton(type.button2, skin, "small-text");

        this.id = id;
        init();
    }

    public void init() {
        Value pad = Value.percentWidth(.1f, this);
        Table contentTable = new Table();
        add(fuzzImage).size(Value.percentWidth(.15f, this), Value.percentHeight(1f, this)).left();
        add(contentTable).padRight(pad).expand().size(Value.percentWidth(.75f, this), Value.percentHeight(1f, this)).padRight(Value.percentWidth(.05f, this)).right();

        contentTable.add(playerName).colspan(2).expand().size(Value.percentWidth(1f, contentTable), Value.percentHeight(.5f, contentTable)).padBottom(Value.percentHeight(.1f, contentTable));
        contentTable.row();
        contentTable.add(button1).size(Value.percentWidth(.45f, contentTable), Value.percentHeight(.5f, contentTable)).left();
        contentTable.add(button2).size(Value.percentWidth(.45f, contentTable), Value.percentHeight(.5f, contentTable)).right();
    }

    public TextButton getButton1() {
        return button1;
    }
    public TextButton getButton2() {
        return button2;
    }
}
