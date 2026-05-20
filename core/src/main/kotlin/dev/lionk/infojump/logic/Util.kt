package dev.lionk.infojump.logic

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import dev.lionk.infojump.actions.ActionManager

class MyContactListener : ContactListener {
    var footContacts = 0

    override fun beginContact(contact: Contact) {
        if(hasData(contact, "feet")){
            footContacts++
        }
        if(hasData(contact, "player")){
            ActionManager.handleAction(getOther(contact, "player"))
        }
    }

    private fun hasData(contact: Contact, data: String): Boolean {
        return (contact.fixtureA.userData == data)||(contact.fixtureB.userData == data)
    }

    private fun getOther(contact: Contact, wrongData: String): String? {
        if(contact.fixtureA.userData == wrongData) return contact.fixtureB.userData as? String
        return if(contact.fixtureB.userData == wrongData) contact.fixtureA.userData as? String
        else null
    }

    override fun endContact(contact: Contact) {
        if(hasData(contact, "feet")){
            footContacts--
        }
        if(hasData(contact, "player")){
            ActionManager.handleLeaveAction(getOther(contact, "player"))
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
