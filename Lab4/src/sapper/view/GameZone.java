package sapper.view;

import common.SapperUser;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import sapper.model.SapperModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameZone implements Serializable {
    private SapperModel _mapConfigure;
    private boolean _isEnded;
    private final List<SapperUser> players;
    private final List<SapperModel> models;

    private GameZoneAction onWinAction;
    private GameZoneAction onLoseAction;
    private GameZoneAction onFirstStepAction;
    private GameZoneAction onOpenAction;
    private GameZoneAction onMarkToggledAction;

    public GameZone(SapperModel mapConfigure) {
        _mapConfigure = mapConfigure;
        players = new ArrayList<>();
        models = new ArrayList<>();
        _isEnded = true;

        onWinAction = (GameZoneAction & Serializable) (user, row, col) ->
                System.out.println(user.getUsername() + " won.");
        onLoseAction = (GameZoneAction & Serializable) (user, row, col) ->
                System.out.println(user.getUsername() + " lost.");
        onFirstStepAction = (GameZoneAction & Serializable) (user, row, col) ->
                System.out.println(user.getUsername() + " made the first step in row " + row + " and col " + col + ".");
        onOpenAction = (GameZoneAction & Serializable) (user, row, col) ->
                System.out.println(user.getUsername() + " opened cell in row " + row + " and col " + col + ".");
        onMarkToggledAction = (GameZoneAction & Serializable) (user, row, col) ->
                System.out.println(user.getUsername() + " toggled mark in row " + row + " and col " + col + ".");
    }

    public GameZone(SapperModel mapConfigure, AnchorPane container) {
        _mapConfigure = mapConfigure;
        players = new ArrayList<>();
        models = new ArrayList<>();
        _isEnded = true;

        Platform.runLater(() -> {
            container.getChildren().clear();
            container.getChildren().add(setParent(null,  new HashMap<>()));
        });
    }

    public void setMapConfigure(SapperModel mapConfigure) {
        _mapConfigure = mapConfigure;
    }

    public SapperModel getMapConfigure() {
        return _mapConfigure;
    }

    public void addPlayer(SapperUser user) {
        if(!players.contains(user)) {
            players.add(user);
            models.add(new SapperModel(_mapConfigure.getBombCount(), _mapConfigure.getField().length));
        }
    }

    public void removePlayer(SapperUser user) {
        int index = 0;
        for(SapperUser current : players) {
            if(current.equals(user)) break;
            index++;
        }
        players.remove(index);
        models.remove(index);
    }

    public GridPane setParent(SapperUser userOwner, HashMap<SapperUser, SapperView> sapperViews) {
        GridPane sappers = new GridPane();
        sappers.setAlignment(Pos.CENTER);
        AnchorPane.setTopAnchor(sappers, 0.0);
        AnchorPane.setRightAnchor(sappers, 0.0);
        AnchorPane.setBottomAnchor(sappers, 0.0);
        AnchorPane.setLeftAnchor(sappers, 0.0);

        sapperViews.clear();
        for (int i = 0; i < players.size(); i++) {
            SapperUser sapperUser = players.get(i);
            SapperModel model = models.get(i);
            SapperView view = new SapperView(model);

            if(userOwner == null || userOwner.equals(sapperUser)) {
                view.getSapperGrid().setStyle("-fx-border-color: red; -fx-border-width: 2px");
                view.setOnWinAction((row, col) -> onWinAction.run(sapperUser, row, col));
                view.setOnLoseAction((row, col) -> onLoseAction.run(sapperUser, row, col));
                view.setOnFirstStepAction((row, col) -> onFirstStepAction.run(sapperUser, row, col));
                view.setOnOpenAction((row, col) -> onOpenAction.run(sapperUser, row, col));
                view.setOnMarkToggledAction((row, col) -> onMarkToggledAction.run(sapperUser, row, col));
            } else {
                view.removeGridOnMouseClick();
            }

            int row = (int) Math.floor((double) i / 2);
            int col = i % 2;

            VBox usernameAndSapperGrid = new VBox();
            usernameAndSapperGrid.getChildren().add(new Label("Player: " + sapperUser.getUsername()));
            usernameAndSapperGrid.getChildren().add(view.getSapperGrid());
            sappers.add(usernameAndSapperGrid, col, row);
            sapperViews.put(sapperUser, view);
        }

        return sappers;
    }

    public GameZone copy() {
        GameZone gameZone = new GameZone(new SapperModel(_mapConfigure.getBombCount(), _mapConfigure.getField().length));
        for(int i = 0; i < players.size(); i++) {
            gameZone.players.add(players.get(i));
            gameZone.models.add(models.get(i));
        }
        return gameZone;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Game zone {\n");

        for(int i = 0; i < players.size(); i++) {
            sb.append("Player: ")
                    .append(players.get(i).getUsername())
                    .append("\n")
                    .append(models.get(i)).append("\n");
        }

        return sb.substring(0, sb.length() - 1) + "}";
    }

    public GameZoneAction getOnWinAction() {
        return onWinAction;
    }

    public void setOnWinAction(GameZoneAction onWinAction) {
        this.onWinAction = onWinAction;
    }

    public GameZoneAction getOnLoseAction() {
        return onLoseAction;
    }

    public void setOnLoseAction(GameZoneAction onLoseAction) {
        this.onLoseAction = onLoseAction;
    }

    public GameZoneAction getOnFirstStepAction() {
        return onFirstStepAction;
    }

    public void setOnFirstStepAction(GameZoneAction onFirstStepAction) {
        this.onFirstStepAction = onFirstStepAction;
    }

    public GameZoneAction getOnOpenAction() {
        return onOpenAction;
    }

    public void setOnOpenAction(GameZoneAction onOpenAction) {
        this.onOpenAction = onOpenAction;
    }

    public GameZoneAction getOnMarkToggledAction() {
        return onMarkToggledAction;
    }

    public void setOnMarkToggledAction(GameZoneAction onMarkToggledAction) {
        this.onMarkToggledAction = onMarkToggledAction;
    }

    public SapperModel getSapperModel(SapperUser user) {
        for(int i = 0; i < players.size(); i++) {
            if(user.equals(players.get(i))) return models.get(i);
        }
        return null;
    }

    public void setField(SapperUser user, SapperModel model) {
        for(int i = 0; i < players.size(); i++) {
            if(user.equals(players.get(i))) {
                models.set(i, model);
                return;
            }
        }
    }

    public void gameStart() {
        _isEnded = false;
    }

    public void gameEnd() {
        _isEnded = true;
    }

    public boolean isEnded() {
        return _isEnded;
    }
}
