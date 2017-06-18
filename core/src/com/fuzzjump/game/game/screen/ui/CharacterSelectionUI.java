package com.fuzzjump.game.game.screen.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.SnapshotArray;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.game.game.player.Appearance;
import com.fuzzjump.game.game.player.Profile;
import com.fuzzjump.game.game.player.unlockable.Unlockable;
import com.fuzzjump.game.game.player.unlockable.UnlockableColorizer;
import com.fuzzjump.game.game.player.unlockable.UnlockableDefinition;
import com.fuzzjump.game.game.player.unlockable.UnlockableRepository;
import com.fuzzjump.game.game.screen.component.CategoryFrame;
import com.fuzzjump.game.game.screen.component.ColorDrawable;
import com.fuzzjump.game.game.screen.component.FuzzDialog;
import com.fuzzjump.game.game.screen.component.Fuzzle;
import com.fuzzjump.game.util.Helper;
import com.fuzzjump.libgdxscreens.ScreenLoader;
import com.fuzzjump.libgdxscreens.StageUI;
import com.fuzzjump.libgdxscreens.graphics.ColorGroup;

import java.util.List;

import static com.fuzzjump.game.game.Assets.createCFrameStyle;
import static com.fuzzjump.game.game.Assets.createDefaultTBStyle;
import static com.fuzzjump.game.game.Assets.createDialogStyle;

public class CharacterSelectionUI extends StageUI implements Appearance.AppearanceChangeListener {

    // TODO - Maybe a better system for this?
    private final MenuUI parent;
    private final Stage stage;
    private final Profile profile;
    private final UnlockableRepository definitions;
    private final UnlockableColorizer colorizer;
    private final ScreenLoader loader;

    private Table topTable;
    private Table midTable;
    private Table lowMidTable;
    private Table lowTable;
    private Table itemsContainer;

    private int selectedCategory;


    private ClickListener entryClickListener = new ClickListener() {

        @Override
        public void clicked(InputEvent event, float x, float y) {
            UnlockableEntry entry = (UnlockableEntry) event.getListenerActor();
            if (entry.selected)
                return;
            if (!entry.unlocked) {
                showBuyDialog(entry);
                return;
            }
            selectUnlockable(entry);
        }

    };

    private ClickListener colorClickListener = new ClickListener() {

        @Override
        public void clicked(InputEvent event, float x, float y) {
            ColorEntry colorEntry = (ColorEntry) event.getListenerActor();
            if (colorEntry.selected)
                return;
            SnapshotArray<Actor> colorEntries = colorSet.getChildren();
            int newIndex = 0;
            for (int i = 0; i < colorEntries.size; i++) {
                Actor actor = colorEntries.get(i);
                if (actor != event.getListenerActor()) {
                    ((ColorEntry) actor).setSelected(false);
                } else {
                    newIndex = i;
                }
            }
            colorEntry.setSelected(true);
            UnlockableEntry unlockableEntry = (UnlockableEntry) colorEntry.getUserObject();
            profile.getAppearance().setColorIndex(unlockableEntry.unlockable.getId(), newIndex);
        }

    };

    private ColorSet colorSet;

    private Image unlockableImage;
    private Label buyingUnlockableLabel;
    private Label costLabel;
    private Dialog buyingDialog;

    public CharacterSelectionUI(MenuUI parent, Stage stage, Profile profile, UnlockableRepository definitions, UnlockableColorizer colorizer) {
        super(parent.getTextures(), parent.getGameSkin());
        this.parent = parent;
        this.stage = stage;
        this.stageScreen = parent.getStageScreen();
        this.profile = profile;
        this.definitions = definitions;
        this.colorizer = colorizer;
        this.loader = stageScreen.getLoader();
    }

    @Override
    public void init() {

        loader.add(() -> {
            buyingDialog = new FuzzDialog("", createDialogStyle(this), 0.75f, 0.8352089253422888f);

            parent.register(Assets.MenuUI.BUYING_DIALOG, buyingDialog);

            buyingDialog.setModal(true);

            buyingUnlockableLabel = new Label("Item name", getGameSkin(), "default");
            unlockableImage = new Image();
            unlockableImage.setScaling(Scaling.fit);

            buyingDialog.getContentTable().add(buyingUnlockableLabel).padTop(Value.percentHeight(.1f, buyingDialog)).row();
            buyingDialog.getContentTable().add(unlockableImage).center().expand().size(Value.percentWidth(.45f, buyingDialog)).row();

            Table costTable = new Table();

            costTable.add(new Image(textures.getTextureRegionDrawable("kerpow-coin"))).size(Value.percentHeight(1f, costTable)).left();
            costTable.add(costLabel = new Label("cost", getGameSkin(), "default")).padLeft(Value.percentWidth(.01f, buyingDialog)).expand().right();

            Value padBottom = Value.percentHeight(.035f, buyingDialog);

            buyingDialog.getContentTable().add(costTable).height(Value.percentHeight(.075f, buyingDialog)).padBottom(padBottom).center();

            TextButton cancelButton = new TextButton("Cancel", createDefaultTBStyle(this));
            TextButton purchaseButton = new TextButton("Buy", createDefaultTBStyle(this));

            Helper.addClickAction(cancelButton, (e, x, y) -> buyingDialog.hide());

            parent.register(Assets.MenuUI.SELECT_BUY_BUTTON, purchaseButton);

            Value padSide = Value.percentWidth(.05f, buyingDialog);
            buyingDialog.getButtonTable().add(cancelButton).size(Value.percentWidth(.475f, buyingDialog), Value.percentWidth(0.1315789473684211f, buyingDialog)).left().padLeft(padSide).padBottom(padBottom).expand();
            buyingDialog.getButtonTable().add(purchaseButton).size(Value.percentWidth(.475f, buyingDialog), Value.percentWidth(0.1315789473684211f, buyingDialog)).right().padRight(padSide).padBottom(padBottom).expand();
        });
        loader.add(() -> initTopTable(loader));
        loader.add(() -> initMidtable(loader));
        loader.add(this::initLowMidTable);
        loader.add(this::initLowTable);


        selectedCategory = 0;
        profile.getAppearance().addChangeListener(this);

        loader.add(() -> {
            add(topTable).size(Value.percentWidth(1f, this), Value.percentWidth(0.2157697975873454f, this)).row();
            add(midTable).size(Value.percentWidth(1f, this), Value.percentWidth(0.6704595716097392f, this)).row();
            add(lowMidTable).size(Value.percentWidth(1f, this), Value.percentWidth(0.21576796231076f, this)).row();
            add(lowTable).size(Value.percentWidth(1f, this), Value.percentWidth(0.17139831558315f, this)).row();
        });
    }

    private ClickListener frameClickListener = new ClickListener() {

        @Override
        public void clicked(InputEvent event, float x, float y) {
            int index = 0;
            for (Actor actor : topTable.getChildren()) {
                CategoryFrame frame = (CategoryFrame) actor;
                if (frame == event.getListenerActor()) {
                    if (frame.isChecked())
                        return;
                    frame.setChecked(true);
                    selectedCategory = index;
                    displayLoad();
                } else {
                    frame.setChecked(false);
                }
                index++;
            }
        }

    };

    public void initTopTable(ScreenLoader loader) {
        topTable = new Table();
        topTable.setBackground(textures.getTextureRegionDrawable("ui-panel-character1"));

        Value pad = Value.percentWidth(.01f, topTable);
        Value width = Value.percentWidth(.15f, topTable);
        Value height = Value.percentWidth(0.160335f, topTable);

        for (int index = 0; index < Appearance.COUNT; index++) {
            CategoryFrame frame = new CategoryFrame(Appearance.TITLES[index], getGameSkin(), createCFrameStyle(parent));
            frame.init();
            if (index == 0) {
                frame.setChecked(true);
            }
            topTable.add(frame).padLeft(pad).padRight(pad).size(width, height).expand();
            frame.addListener(frameClickListener);
            frame.setUserObject(index);
        }
        loader.add(this::appearanceChanged);
    }

    public void initMidtable(ScreenLoader loader) {
        midTable = new Table();
        midTable.setBackground(textures.getTextureRegionDrawable("ui-panel-character2"));

        Value padInside = Value.percentWidth(.05f, midTable);
        Value padOutside = Value.percentWidth(.035f, midTable);
        colorSet = new ColorSet(Value.percentWidth(.15f, midTable), Value.percentHeight(.015f, midTable));
        ScrollPane scrollPane = new ScrollPane(colorSet);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.layout();
        Table holder = new Table();
        holder.add(scrollPane).fill().expand();
        midTable.add(holder).size(Value.percentWidth(.15f, midTable), Value.percentHeight(.85f, midTable)).padLeft(padOutside).padRight(padInside);

        // TODO Do we really need two fuzzle instances?
        Fuzzle fuzzle = new Fuzzle(this, colorizer, profile);
        fuzzle.load(loader);

        midTable.add(fuzzle).size(Value.percentWidth(.6f, midTable)).center().expand();

        midTable.add(new Actor()).size(Value.percentWidth(.15f, midTable), Value.percentHeight(.85f, midTable)).padRight(padOutside).padLeft(padInside);
    }

    public void initLowMidTable() {
        lowMidTable = new Table();
        lowMidTable.setBackground(textures.getTextureRegionDrawable("ui-panel-character3"));

        Image leftImage = new Image(textures.getTextureRegionDrawable("ui-left-icon"), Scaling.fit);
        Image rightImage = new Image(textures.getTextureRegionDrawable("ui-right-icon"), Scaling.fit);
        ScrollPane itemScroller = new ScrollPane(itemsContainer = new Table());
        itemScroller.setScrollingDisabled(false, true);
        itemScroller.layout();
        Table holder = new Table();
        holder.add(itemScroller).fill().expand();

        Value imageSize = Value.percentWidth(.10f, lowMidTable);

        lowMidTable.add(leftImage).size(imageSize);
        lowMidTable.add(holder).expand();
        lowMidTable.add(rightImage).size(imageSize);
    }

    public void displayLoad() {
        Dialog dialog = parent.actor(Dialog.class, Assets.MenuUI.PROGRESS_DIALOG);
        Label label = parent.actor(Assets.MenuUI.PROGRESS_LABEL);
        Button button = parent.actor(Assets.MenuUI.CLOSE_BUTTON);
        label.setText("Loading");
        button.setVisible(false);
        dialog.show(stage);
        Action showAction = dialog.getActions().get(dialog.getActions().size - 1);
        dialog.addAction(Actions.sequence(Actions.after(showAction), Actions.run(this::populateItemsTable)));
    }

    private void populateItemsTable() {
        itemsContainer.clearChildren();

        Value size = Value.percentHeight(.75f, lowMidTable);
        Value pad = Value.percentHeight(.05f, lowMidTable);

        List<UnlockableDefinition> defs = definitions.getDefinitions(selectedCategory, profile.getAppearance().getEquip(Appearance.FUZZLE).getDefinition().getId());
        boolean oneSelected = false;
        for (int i = 0; i < defs.size(); i++) {
            UnlockableDefinition def = defs.get(i);
            Unlockable unlockable = profile.getAppearance().getItem(def);
            UnlockableEntry entry = new UnlockableEntry(def, unlockable);
            if (unlockable != null) {
                boolean selected = profile.getAppearance().getEquip(selectedCategory) == unlockable;
                entry.setSelected(selected);
                if (selected) {
                    selectUnlockable(entry);
                    oneSelected = true;
                }
            }
            entry.addListener(entryClickListener);
            itemsContainer.add(entry).size(size).padLeft(pad).padRight(pad);
        }
        if (!oneSelected) {
            colorSet.update(null);
        }
        parent.actor(Dialog.class, Assets.MenuUI.PROGRESS_DIALOG).hide();
    }

    private void selectUnlockable(UnlockableEntry entry) {
        for (Actor actor : itemsContainer.getChildren()) {
            if (actor != entry) {
                ((UnlockableEntry) actor).setSelected(false);
            }
        }
        profile.getAppearance().setEquip(selectedCategory, entry.getUnlockable().getId());
        entry.setSelected(true);
        refreshColors(entry);
    }

    private void refreshColors(UnlockableEntry entry) {
        colorSet.clearChildren();
        int selectedColor = entry.getUnlockable().getColorIndex();
        ColorGroup[] defColorGroups = entry.unlockableDefinition.getColorGroups();
        if (defColorGroups == null) {
            colorSet.update(null);
            return;
        }
        ColorEntry[] colorEntries = new ColorEntry[defColorGroups.length];
        for (int i = 0; i < defColorGroups.length; i++) {
            //show the base colorString
            ColorEntry colorEntry = new ColorEntry(Color.valueOf(defColorGroups[i].colors[0].colorString.substring(1)));
            colorEntry.setUserObject(entry);
            colorEntry.selected = selectedColor == i;
            colorEntries[i] = colorEntry;
            colorEntry.addListener(colorClickListener);
        }
        colorSet.update(colorEntries);
    }

    private void showBuyDialog(UnlockableEntry entry) {
        buyingDialog.show(stage);
        buyingUnlockableLabel.setText(entry.getUnlockableDefinition().getName());
        unlockableImage.setDrawable(entry.accessoryImg);
        costLabel.setText(String.valueOf(entry.getUnlockableDefinition().getCost()));

        //parent.context(Assets.MenuUI.SELECTED_UNLOCK, entry);
    }

    public void initLowTable() {
        lowTable = new Table();
        lowTable.setBackground(textures.getTextureRegionDrawable("ui-panel-character4"));

        TextButton backButton = new TextButton("Back", createDefaultTBStyle(parent));
        TextButton storeButton = new TextButton("Store", createDefaultTBStyle(parent));

        backButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                backPressed();
            }

        });

        Value outerPad = Value.percentWidth(.025f, lowTable);
        Value upperPad = Value.percentHeight(.025f, lowTable);
        Value width = Value.percentWidth(.45f, lowTable);
        Value height = Value.percentWidth(.12888888f, lowTable);

        lowTable.add(backButton).size(width, height).pad(upperPad, outerPad, upperPad, outerPad).expand();
        lowTable.add(storeButton).size(width, height).pad(upperPad, outerPad, upperPad, outerPad).expand();
    }


    @Override
    public void backPressed() {
        final Appearance appearance = profile.getAppearance();
        if (appearance.changed()) {
            System.out.println("Saving profile");
            final Dialog dialog = parent.actor(Dialog.class, Assets.MenuUI.PROGRESS_DIALOG);
            final TextButton closeButton = parent.actor(Assets.MenuUI.CLOSE_BUTTON);
            final Image progressImage = parent.actor(Assets.MenuUI.PROGRESS_IMAGE);
            final Label messageLabel = parent.actor(Assets.MenuUI.PROGRESS_LABEL);
            messageLabel.setText("Saving profile...");
            dialog.show(stage);

        } else {
            parent.showMain();
        }
    }

    @Override
    public void appearanceChanged() {
        for (int i = 0; i < Appearance.COUNT; i++) {
            final int index = i;
            loader.add(() -> {
                getCategoryFrame(index).setCategoryDrawable(colorizer.getColored(textures, profile.getAppearance().getEquip(index), false));
            });
        }
    }

    public CategoryFrame getCategoryFrame(int index) {
        return (CategoryFrame) topTable.getChildren().get(index);
    }

    public void showing() {
        populateItemsTable();
        profile.getAppearance().snapshot();
    }

    private class UnlockableEntry extends Actor {

        private final UnlockableDefinition unlockableDefinition;

        private final Drawable lock;
        private final Drawable coinIcon;
        private final BitmapFont font;

        private TextureRegionDrawable accessoryImg;

        private boolean unlocked;
        private boolean selected;

        private Unlockable unlockable;

        private Drawable bg;

        private UnlockableEntry(UnlockableDefinition definition, Unlockable unlockable) {
            this.bg = textures.getTextureRegionDrawable("ui-wait-square");
            this.lock = textures.getTextureRegionDrawable("ui-lock");
            this.coinIcon = textures.getTextureRegionDrawable("kerpow-coin");
            this.accessoryImg = new TextureRegionDrawable(colorizer.getColored(textures, definition, unlockable == null ? 0 : unlockable.getColorIndex(), true));
            this.font = getGameSkin().getFont(Assets.PROFILE_FONT);
            this.unlockableDefinition = definition;
            this.unlocked = unlockable != null;
            this.unlockable = unlockable;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            float accessoryWidth = getWidth() * .85f;
            float accessoryHeight = accessoryWidth / ((float) accessoryImg.getRegion().getRegionWidth() / (float) accessoryImg.getRegion().getRegionHeight());
            float accessoryX = getX() + getWidth() / 2 - accessoryWidth / 2;
            float accessoryY = getY() + getHeight() / 2 - accessoryHeight / 2f;
            accessoryImg.draw(batch, accessoryX, accessoryY, accessoryWidth, accessoryHeight);
            if (!unlocked) {
                float lockHeight = getHeight() * .5f;
                float lockWidth = (lockHeight) / 1.666f;
                float lockX = getX() + getWidth() / 2 - lockWidth / 2;
                float lockY = getY() + getHeight() / 2 - lockHeight / 2f;
                lock.draw(batch, lockX, lockY, lockWidth, lockHeight);
            }
            if (selected) {
                textures.getTextureRegionDrawable("ui-frame-selected").draw(batch, getX() - 2, getY() - 2, getWidth() + 4, getHeight() + 4);
            }
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public UnlockableDefinition getUnlockableDefinition() {
            return unlockableDefinition;
        }

        public void setBg(Drawable bg) {
            this.bg = bg;
        }

        public Unlockable getUnlockable() {
            return unlockable;
        }

        public void setUnlockable(Unlockable unlockable) {
            if (unlockable != null)
                unlocked = true;
            this.unlockable = unlockable;
            selectUnlockable(this);
        }

        public void setAccessoryImg(TextureRegion colorize) {
            accessoryImg.setRegion(colorize);
        }
    }

    private class CustomizeCategory extends Table {

        private final Label titleLbl;
        private final Image img;
        private final int category;

        public CustomizeCategory(int category, String title, Drawable initialDrawable, Drawable background, String labelStyle) {
            this.titleLbl = new Label(title, getSkin(), labelStyle);
            if (initialDrawable == null)
                initialDrawable = textures.getTextureRegionDrawable("ui-question-mark");
            this.img = new Image(initialDrawable);
            this.category = category;
            setBackground(background);
            addActors();
        }

        public void addActors() {
            titleLbl.setAlignment(Align.center);
            img.setScaling(Scaling.fit);
            add(titleLbl).center();
            row();
            add(img).expand().center().pad(Value.percentHeight(.05f, this));
        }

        public int getCategory() {
            return category;
        }

    }

    private class UnlockableGroup extends Table {

        private UnlockableEntry[] unlockables;

        public UnlockableGroup() {
        }

        public void addActors() {
            if (unlockables == null)
                return;
            for (int i = 0; i < unlockables.length; i++) {
                add(unlockables[i]).size(Gdx.graphics.getHeight() / 7.667f).pad(Gdx.graphics.getHeight() / 375).center().row();
            }
        }

        public void update(UnlockableEntry[] unlockables) {
            clearChildren();
            this.unlockables = unlockables;
            addActors();
        }

        public UnlockableEntry[] getEntries() {
            return unlockables;
        }
    }


    private class ColorSet extends Table {

        private final Value size;
        private final Value pad;

        private ColorEntry[] colors;

        public ColorSet(Value size, Value pad) {
            this.size = size;
            this.pad = pad;
        }

        public void addActors() {
            if (colors == null)
                return;
            for (int i = 0; i < colors.length; i++) {
                add(colors[i]).size(size).padTop(pad).padBottom(pad).row();
            }
        }

        public void update(ColorEntry[] colorEntries) {
            clearChildren();
            if (colorEntries == null) {
                setVisible(false);
            } else {
                setVisible(true);
                this.colors = colorEntries;
                addActors();
            }
        }

        public ColorEntry[] getEntries() {
            return colors;
        }

    }

    private class ColorEntry extends Image {

        boolean selected;

        public ColorEntry(Color color) {
            super(new ColorDrawable(color, 1, 1));
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
            if (selected) {
                textures.getTextureRegionDrawable("ui-frame-selected").draw(batch, getX() - 2, getY() - 2, getWidth() + 4, getHeight() + 4);
            }
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }
}
