package com.fuzzjump.game.game.screen;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.fuzzjump.api.invite.IInviteService;
import com.fuzzjump.api.model.unlockable.Unlockable;
import com.fuzzjump.api.model.unlockable.UnlockablePurchase;
import com.fuzzjump.api.unlockable.IUnlockableService;
import com.fuzzjump.api.unlockable.model.UnlockablePurchaseRequest;
import com.fuzzjump.api.unlockable.model.UnlockableResponse;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.game.game.player.Profile;
import com.fuzzjump.game.game.screen.ui.CharacterSelectionUI;
import com.fuzzjump.game.game.screen.ui.MenuUI;
import com.fuzzjump.game.io.FuzzPersistence;
import com.fuzzjump.game.util.GraphicsScheduler;
import com.fuzzjump.libgdxscreens.screen.StageScreen;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;

import static com.fuzzjump.game.game.Assets.MenuUI.PUBLIC_GAME;
import static com.fuzzjump.game.game.Assets.MenuUI.SELECT_BUY_BUTTON;

/**
 * Created by Steven Galarza on 6/16/2017.
 */
public class MenuScreen extends StageScreen<MenuUI> {

    private final Profile profile;
    private final IUnlockableService unlockableService;
    private final IInviteService inviteService;
    private final GraphicsScheduler scheduler;
    private final FuzzPersistence persistence;

    private boolean checkInvites;

    @Inject
    public MenuScreen(MenuUI ui,
                      Profile profile,
                      IUnlockableService unlockableService,
                      IInviteService inviteService,
                      GraphicsScheduler scheduler,
                      FuzzPersistence persistence) {
        super(ui);
        this.profile = profile;
        this.unlockableService = unlockableService;
        this.inviteService = inviteService;
        this.scheduler = scheduler;
        this.persistence = persistence;
    }

    @Override
    public void onReady() {

    }

    @Override
    public void onShow() {
        checkInvites = true;
        Observable.timer(2000, TimeUnit.MILLISECONDS)
                .repeatUntil(() -> checkInvites)
                .flatMap(ignored -> inviteService.getInvites())
                .subscribe(invites -> ui().addInvites(invites.getBody()), err -> {
                    //TODO log this.
                    System.out.println("Error fetching invites: " + err.getMessage());
                });
    }

    @Override
    public void onClose() {
        checkInvites = false;
    }

    @Override
    public void clicked(int id, Actor actor) {
        switch (id) {
            case PUBLIC_GAME:
                screenHandler.showScreen(WaitingScreen.class);
                break;
            case SELECT_BUY_BUTTON:
                buySelectedUnlockable();
                break;
        }
    }

    private void buySelectedUnlockable() {
        final MenuUI ui = ui();
        final CharacterSelectionUI.UnlockableEntry buyEntry = ui.actor(Assets.MenuUI.SELECTED_UNLOCK);
        if (buyEntry.getUnlockableDefinition().getCost() > profile.getCoins()) {
            ui.displayMessage("Not enough coins!", false);
            return;
        }
        ui.displayMessage("Purchasing", true);
        unlockableService.purchaseUnlockable(new UnlockablePurchaseRequest(buyEntry.getUnlockableDefinition().getId()))
                .observeOn(scheduler)
                .subscribe(this::handleUnlockablePurchase, e -> {
                    ui.displayMessage("Purchase failed", false); // on error
                });
    }

    private void handleUnlockablePurchase(UnlockableResponse response) {
        final MenuUI ui = ui();
        final CharacterSelectionUI.UnlockableEntry purchaseSelection = ui.actor(Assets.MenuUI.SELECTED_UNLOCK);
        if (response.isSuccess()) {
            UnlockablePurchase purchase = response.getBody();

            switch (purchase.getPurchaseStatus()) {
                case UnlockablePurchase.PURCHASE_NOT_ENOUGH_COINS:
                    ui.displayMessage("Not enough coins!", false);
                    break;
                case UnlockablePurchase.PURCHASE_ALREADY_PURCHASED:
                    ui.displayMessage("Already purchased!", false);
                    break;
                case UnlockablePurchase.PURCHASE_SUCCESS:
                    Unlockable unlockable = purchase.getUnlockable();
                    if (unlockable != null) { // Shouldn't be null, but you know Java ...
                        purchaseSelection.setSelected(true);
                        purchaseSelection.setUnlockable(profile.getAppearance().createUnlockable(unlockable));
                        profile.setCoins(profile.getCoins() - purchaseSelection.getUnlockableDefinition().getCost());

                        persistence.saveProfile();
                        ui.closeMessage();
                    }
                    break;
                default:
                    ui.displayMessage("Purchase failed", false);
                    break;
            }
        } else {
            ui.displayMessage("Purchase failed", false);
        }
    }

}
