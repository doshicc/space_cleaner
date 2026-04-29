package io.github.space_cleaner.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;

import io.github.space_cleaner.GameResources;
import io.github.space_cleaner.GameSession;
import io.github.space_cleaner.GameSettings;
import io.github.space_cleaner.MyGdxGame;
import io.github.space_cleaner.objects.ShipObject;
import io.github.space_cleaner.objects.TrashObject;

public class GameScreen extends ScreenAdapter {

    MyGdxGame myGdxGame;
    GameSession gameSession;
    ShipObject shipObject;

    ArrayList<TrashObject> trashArray;

    public GameScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;
        gameSession = new GameSession();

        trashArray = new ArrayList<>();

        shipObject = new ShipObject(
            GameSettings.SCREEN_WIDTH / 2, 150,
            GameSettings.SHIP_WIDTH, GameSettings.SHIP_HEIGHT,
            GameResources.SHIP_IMG_PATH,
            myGdxGame.world
        );

    }

    @Override
    public void show() {
        gameSession.startGame();
    }

    @Override
    public void render(float delta) {

        myGdxGame.stepWorld();
        handleInput();

        if (gameSession.shouldSpawnTrash()) {
            TrashObject trashObject = new TrashObject(
                GameSettings.TRASH_WIDTH, GameSettings.TRASH_HEIGHT,
                GameResources.TRASH_IMG_PATH,
                myGdxGame.world
            );
            trashArray.add(trashObject);
        }

        updateTrash();

        draw();
    }

    private void handleInput() {
        if (Gdx.input.isTouched()) {
            myGdxGame.touch = myGdxGame.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            shipObject.move(myGdxGame.touch);
        }
    }

    private void draw() {

        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined);
        ScreenUtils.clear(Color.CLEAR);

        myGdxGame.batch.begin();
        for (TrashObject trash : trashArray) trash.draw(myGdxGame.batch);
        shipObject.draw(myGdxGame.batch);
        myGdxGame.batch.end();

    }

    private void updateTrash() {
        for (int i = 0; i < trashArray.size(); i++) {
            if (!trashArray.get(i).isInFrame()) {
                myGdxGame.world.destroyBody(trashArray.get(i).body);
                trashArray.remove(i--);
            }
        }
    }
}
