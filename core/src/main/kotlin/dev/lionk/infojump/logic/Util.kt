package dev.lionk.infojump.logic

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold

class MyContactListener : ContactListener {
    var footContacts = 0

    override fun beginContact(contact: Contact) {
        val fa = contact.fixtureA
        val fb = contact.fixtureB

        if (fa.userData == "feet" || fb.userData == "feet") {
            footContacts++
        }
    }

    override fun endContact(contact: Contact) {
        val fa = contact.fixtureA
        val fb = contact.fixtureB

        if (fa.userData == "feet" || fb.userData == "feet") {
            footContacts--
        }
    }

    override fun preSolve(
        contact: Contact?,
        oldManifold: Manifold?
    ) {

    }

    override fun postSolve(
        contact: Contact?,
        impulse: ContactImpulse?
    ) {
    }
}
