package io.github.space_cleaner.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;

import io.github.space_cleaner.GameResources;
import io.github.space_cleaner.GameSettings;

public class BonusObject extends GameObject {

    private long spawnTime;
    private long lifeTime = 5000;
    private boolean collected = false;

    public BonusObject(float x, float y, World world) {
        super(GameResources.BONUS_IMG_PATH,
            (int) x, (int) y,
            GameSettings.BONUS_WIDTH, GameSettings.BONUS_HEIGHT,
            GameSettings.BONUS_BIT,
            world);
        body.setLinearVelocity(new Vector2(0, -30));
        body.setGravityScale(0);
        Filter filter = new Filter();
        filter.categoryBits = GameSettings.BONUS_BIT;
        filter.maskBits = GameSettings.SHIP_BIT;
        body.getFixtureList().first().setFilterData(filter);
        spawnTime = TimeUtils.millis();
    }

    public boolean isExpired() {
        return TimeUtils.millis() - spawnTime >= lifeTime;
    }

    public void collect() {
        collected = true;
    }

    public boolean isCollected() {
        return collected;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (collected) return;
        float alpha = 0.7f + 0.3f * (float) Math.sin(TimeUtils.millis() * 0.01);
        batch.setColor(1, 1, 1, alpha);
        super.draw(batch);
        batch.setColor(1, 1, 1, 1);
    }
}
