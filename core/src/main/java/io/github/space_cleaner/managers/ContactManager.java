package io.github.space_cleaner.managers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

import io.github.space_cleaner.GameSettings;
import io.github.space_cleaner.objects.GameObject;

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

                if (cDef == GameSettings.TRASH_BIT && cDef2 == GameSettings.BULLET_BIT
                    || cDef2 == GameSettings.TRASH_BIT && cDef == GameSettings.BULLET_BIT
                    || cDef == GameSettings.TRASH_BIT && cDef2 == GameSettings.SHIP_BIT
                    || cDef2 == GameSettings.TRASH_BIT && cDef == GameSettings.SHIP_BIT) {

                    Object userDataA = fixA.getUserData();
                    Object userDataB = fixB.getUserData();
                    // проверка на то, что объект нужного нам типа
                    if (userDataA instanceof GameObject) ((GameObject) userDataA).hit();
                    if (userDataB instanceof GameObject) ((GameObject) userDataB).hit();

                }
            }

            @Override
            public void endContact(Contact contact) {
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        });

    }
}
