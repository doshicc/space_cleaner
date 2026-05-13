package io.github.space_cleaner.managers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

import io.github.space_cleaner.GameSettings;
import io.github.space_cleaner.objects.BonusObject;
import io.github.space_cleaner.objects.GameObject;
import io.github.space_cleaner.objects.ShipObject;

public class ContactManager {
    World world;

    public ContactManager(World world) {
        this.world = world;

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture fixA = contact.getFixtureA();
                Fixture fixB = contact.getFixtureB();

                int cDef = fixA.getFilterData().categoryBits;
                int cDef2 = fixB.getFilterData().categoryBits;

                // Столкновение мусора с пулей или кораблём
                if ((cDef == GameSettings.TRASH_BIT && cDef2 == GameSettings.BULLET_BIT) ||
                    (cDef2 == GameSettings.TRASH_BIT && cDef == GameSettings.BULLET_BIT) ||
                    (cDef == GameSettings.TRASH_BIT && cDef2 == GameSettings.SHIP_BIT) ||
                    (cDef2 == GameSettings.TRASH_BIT && cDef == GameSettings.SHIP_BIT)) {

                    Object userDataA = fixA.getUserData();
                    Object userDataB = fixB.getUserData();
                    if (userDataA instanceof GameObject) {
                        ((GameObject) userDataA).hit();
                    }
                    if (userDataB instanceof GameObject) {
                        ((GameObject) userDataB).hit();
                    }
                }

                if ((cDef == GameSettings.SHIP_BIT && cDef2 == GameSettings.BONUS_BIT) ||
                    (cDef2 == GameSettings.SHIP_BIT && cDef == GameSettings.BONUS_BIT)) {

                    Object userDataA = fixA.getUserData();
                    Object userDataB = fixB.getUserData();

                    BonusObject bonus = null;
                    ShipObject ship = null;

                    if (userDataA instanceof BonusObject && userDataB instanceof ShipObject) {
                        bonus = (BonusObject) userDataA;
                        ship = (ShipObject) userDataB;
                    } else if (userDataB instanceof BonusObject && userDataA instanceof ShipObject) {
                        bonus = (BonusObject) userDataB;
                        ship = (ShipObject) userDataA;
                    }

                    if (bonus != null && ship != null && !bonus.isCollected()) {
                        bonus.collect();
                        ship.addLife();
                    }
                }
            }

            @Override
            public void endContact(Contact contact) {}

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {}

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {}
        });
    }
}
