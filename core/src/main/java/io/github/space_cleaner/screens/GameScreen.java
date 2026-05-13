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
import io.github.space_cleaner.GameState;
import io.github.space_cleaner.MyGdxGame;
import io.github.space_cleaner.managers.ContactManager;
import io.github.space_cleaner.managers.MemoryManager;
import io.github.space_cleaner.objects.BonusObject;
import io.github.space_cleaner.objects.BulletObject;
import io.github.space_cleaner.objects.ShipObject;
import io.github.space_cleaner.objects.TrashObject;
import io.github.space_cleaner.views.ButtonView;
import io.github.space_cleaner.views.LiveView;
import io.github.space_cleaner.views.MovingBackgroundView;
import io.github.space_cleaner.views.RecordsListView;
import io.github.space_cleaner.views.TextView;
import io.github.space_cleaner.views.ImageView;

public class GameScreen extends ScreenAdapter {
    MyGdxGame myGdxGame;
    GameSession gameSession;
    ShipObject shipObject;
    ArrayList<TrashObject> trashArray;
    ArrayList<BulletObject> bulletArray;
    ArrayList<BonusObject> bonusesArray;
    ContactManager contactManager;
    MovingBackgroundView backgroundView;
    ImageView topBlackoutView;
    LiveView liveView;
    TextView scoreTextView;
    ButtonView pauseButton;
    ImageView fullBlackoutView;
    TextView pauseTextView;
    ButtonView homeButton;
    ButtonView continueButton;
    TextView recordsTextView;
    RecordsListView recordsListView;
    ButtonView homeButton2;

    public GameScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;
        gameSession = new GameSession();

        contactManager = new ContactManager(myGdxGame.world);

        trashArray = new ArrayList<>();
        bulletArray = new ArrayList<>();
        bonusesArray = new ArrayList<>();

        shipObject = new ShipObject(
            GameSettings.SCREEN_WIDTH / 2, 150,
            GameSettings.SHIP_WIDTH, GameSettings.SHIP_HEIGHT,
            GameResources.SHIP_IMG_PATH,
            myGdxGame.world
        );

        backgroundView = new MovingBackgroundView(GameResources.BACKGROUND_IMG_PATH);
        topBlackoutView = new ImageView(0, 1180, GameResources.BLACKOUT_TOP_IMG_PATH);
        liveView = new LiveView(305, 1215);
        scoreTextView = new TextView(myGdxGame.commonWhiteFont, 50, 1215);
        pauseButton = new ButtonView(
            605, 1200,
            46, 54,
            GameResources.PAUSE_IMG_PATH
        );

        fullBlackoutView = new ImageView(0, 0, GameResources.BLACKOUT_FULL_IMG_PATH);
        pauseTextView = new TextView(myGdxGame.largeWhiteFont, 282, 842, "Pause");
        homeButton = new ButtonView(
            138, 695,
            200, 70,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            "Home"
        );
        continueButton = new ButtonView(
            393, 695,
            200, 70,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            "Continue"
        );

        recordsListView = new RecordsListView(myGdxGame.commonWhiteFont, 690);
        recordsTextView = new TextView(myGdxGame.largeWhiteFont, 206, 842, "Last records");
        homeButton2 = new ButtonView(
            280, 365,
            160, 70,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            "Home"
        );

    }

    @Override
    public void show() {
        restartGame();
    }

    @Override
    public void render(float delta) {

        handleInput();

        if (gameSession.state == GameState.PLAYING) {
            if (gameSession.shouldSpawnTrash()) {
                TrashObject trashObject = new TrashObject(
                    GameSettings.TRASH_WIDTH, GameSettings.TRASH_HEIGHT,
                    GameResources.TRASH_IMG_PATH,
                    myGdxGame.world
                );
                trashArray.add(trashObject);
            }

            if (shipObject.needToShoot()) {
                BulletObject laserBullet = new BulletObject(
                    shipObject.getX(), shipObject.getY() + shipObject.height / 2,
                    GameSettings.BULLET_WIDTH, GameSettings.BULLET_HEIGHT,
                    GameResources.BULLET_IMG_PATH,
                    myGdxGame.world
                );
                bulletArray.add(laserBullet);
                if (myGdxGame.audioManager.isSoundOn) myGdxGame.audioManager.shootSound.play();
            }

            if (!shipObject.isAlive()) {
                gameSession.endGame();
                recordsListView.setRecords(MemoryManager.loadRecordsTable());
            }

            updateTrash();
            updateBullets();
            updateBonuses();
            backgroundView.move();
            gameSession.updateScore();
            scoreTextView.setText("Score: " + gameSession.getScore());
            liveView.setLeftLives(shipObject.getLiveLeft());

            myGdxGame.stepWorld();
        }

        draw();
    }

    private void handleInput() {
        if (Gdx.input.isTouched()) {
            myGdxGame.touch = myGdxGame.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

            switch (gameSession.state) {
                case PLAYING:
                    if (pauseButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        gameSession.pauseGame();
                    }
                    shipObject.move(myGdxGame.touch);
                    break;

                case PAUSED:
                    if (continueButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        gameSession.resumeGame();
                    }
                    if (homeButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        myGdxGame.setScreen(myGdxGame.menuScreen);
                    }
                    break;

                case ENDED:

                    if (homeButton2.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        myGdxGame.setScreen(myGdxGame.menuScreen);
                    }
                    break;
            }

        }
    }

    private void draw() {

        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined);
        ScreenUtils.clear(Color.CLEAR);

        myGdxGame.batch.begin();
        backgroundView.draw(myGdxGame.batch);
        for (TrashObject trash : trashArray) trash.draw(myGdxGame.batch);
        shipObject.draw(myGdxGame.batch);
        for (BulletObject bullet : bulletArray) bullet.draw(myGdxGame.batch);
        topBlackoutView.draw(myGdxGame.batch);
        scoreTextView.draw(myGdxGame.batch);
        liveView.draw(myGdxGame.batch);
        pauseButton.draw(myGdxGame.batch);
        for (BonusObject bonus : bonusesArray) {
            bonus.draw(myGdxGame.batch);
        }

        if (gameSession.state == GameState.PAUSED) {
            fullBlackoutView.draw(myGdxGame.batch);
            pauseTextView.draw(myGdxGame.batch);
            homeButton.draw(myGdxGame.batch);
            continueButton.draw(myGdxGame.batch);
        } else if (gameSession.state == GameState.ENDED) {
            fullBlackoutView.draw(myGdxGame.batch);
            recordsTextView.draw(myGdxGame.batch);
            recordsListView.draw(myGdxGame.batch);
            homeButton2.draw(myGdxGame.batch);
        }

        myGdxGame.batch.end();

    }

    private void updateTrash() {
        for (int i = 0; i < trashArray.size(); i++) {
            TrashObject trash = trashArray.get(i);
            boolean isOutOfFrame = !trash.isInFrame();
            boolean isDead = !trash.isAlive();

            if (isOutOfFrame || isDead) {
                if (isDead) {
                    BonusObject bonus = trash.trySpawnBonus(myGdxGame.world);
                    if (bonus != null) {
                        bonusesArray.add(bonus);
                    }

                    gameSession.destructionRegistration();
                    if (myGdxGame.audioManager.isSoundOn) {
                        myGdxGame.audioManager.explosionSound.play(0.2f);
                    }
                }

                myGdxGame.world.destroyBody(trash.body);
                trashArray.remove(i--);
            }
        }
    }

    private void updateBonuses() {
        for (int i = 0; i < bonusesArray.size(); i++) {
            BonusObject bonus = bonusesArray.get(i);

            // Если бонус собран или истекло время – удаляем
            if (bonus.isCollected() || bonus.isExpired()) {
                if (!bonus.isCollected()) {
                    myGdxGame.world.destroyBody(bonus.body);
                }
                bonus.dispose();
                bonusesArray.remove(i);
                i--;
            }
        }
    }

    private void updateBullets() {
        for (int i = 0; i < bulletArray.size(); i++) {
            if (bulletArray.get(i).hasToBeDestroyed()) {
                myGdxGame.world.destroyBody(bulletArray.get(i).body);
                bulletArray.remove(i--);
            }
        }
    }

    private void restartGame() {

        for (int i = 0; i < trashArray.size(); i++) {
            myGdxGame.world.destroyBody(trashArray.get(i).body);
            trashArray.remove(i--);
        }

        if (shipObject != null) {
            myGdxGame.world.destroyBody(shipObject.body);
        }

        shipObject = new ShipObject(
            GameSettings.SCREEN_WIDTH / 2, 150,
            GameSettings.SHIP_WIDTH, GameSettings.SHIP_HEIGHT,
            GameResources.SHIP_IMG_PATH,
            myGdxGame.world
        );

        bulletArray.clear();
        bonusesArray.clear();
        gameSession.startGame();
    }

}
