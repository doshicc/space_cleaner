package io.github.space_cleaner.views;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.space_cleaner.GameResources;

public class LiveView extends View {

    private static final int livePadding = 6;
    private static final int MAX_LIVES = 5;

    private Texture texture;
    private int leftLives;

    public LiveView(float x, float y) {
        super(x, y);
        texture = new Texture(GameResources.LIVE_IMG_PATH);
        this.width = texture.getWidth();
        this.height = texture.getHeight();
        leftLives = 0;
    }

    public void setLeftLives(int leftLives) {
        this.leftLives = Math.min(leftLives, MAX_LIVES);
    }

    @Override
    public void draw(SpriteBatch batch) {
        for (int i = 0; i < leftLives; i++) {
            float heartX = x + i * (width + livePadding);
            batch.draw(texture, heartX, y, width, height);
        }
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
