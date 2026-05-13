package io.github.space_cleaner.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Random;

import io.github.space_cleaner.GameSettings;

public class TrashObject extends GameObject {
    private static final int paddingHorizontal = 30;
    private static final float BONUS_DROP_CHANCE = 0.2f;
    private static final Random random = new Random();

    private int livesLeft;
    private boolean bonusSpawned = false;

    public TrashObject(int width, int height, String texturePath, World world) {
        super(
            texturePath,
            calculateX(width),
            GameSettings.SCREEN_HEIGHT + height / 2,
            width, height,
            GameSettings.TRASH_BIT,
            world
        );
        body.setLinearVelocity(new Vector2(0, -GameSettings.TRASH_VELOCITY));
        livesLeft = 1;
    }

    private static int calculateX(int width) {
        return width / 2 + paddingHorizontal + random.nextInt(GameSettings.SCREEN_WIDTH - 2 * paddingHorizontal - width);
    }

    public boolean isAlive() {
        return livesLeft > 0;
    }

    public boolean isInFrame() {
        return getY() + height / 2 > 0;
    }

    @Override
    public void hit() {
        livesLeft--;
    }

    public BonusObject trySpawnBonus(World world) {
        if (bonusSpawned) return null;
        bonusSpawned = true;
        if (random.nextFloat() < BONUS_DROP_CHANCE) {
            return new BonusObject(getX(), getY(), world);
        }
        return null;
    }
}
