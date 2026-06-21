package dev.lionk.infojump.logic

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.Manifold
import dev.lionk.infojump.actions.ActionManager

class MyContactListener : ContactListener {
    var footContacts = 0
        private set
    var climbBlockContacts = 0
        private set
    var lastContact:Long = 0

    override fun beginContact(contact: Contact) {
        if(hasData(contact, "feet") && getOther(contact, "feet")?.userData == null){
            footContacts++
        }
        if(hasData(contact, "feet") && getOther(contact, "feet")?.userData == "climbable"){
            climbBlockContacts++
        }
        if(hasData(contact, "player")){
            ActionManager.handleAction(getOther(contact, "player"))
        }
    }

    fun reset(){
        climbBlockContacts = 0
    }

    private fun hasData(contact: Contact, data: String): Boolean {
        return (contact.fixtureA.userData == data)||(contact.fixtureB.userData == data)
    }

    private fun getOther(contact: Contact, wrongData: String): Fixture? {
        if(contact.fixtureA.userData == wrongData) return contact.fixtureB
        return if(contact.fixtureB.userData == wrongData) contact.fixtureA
        else null
    }

    override fun endContact(contact: Contact) {
        if(hasData(contact, "feet") && getOther(contact, "feet")?.userData == null){
            footContacts--
            if(footContacts == 0){
                lastContact = System.currentTimeMillis()
            }
        }
        if(hasData(contact, "feet") && getOther(contact, "feet")?.userData == "climbable"){
            climbBlockContacts--
        }
        if(hasData(contact, "player")){
            ActionManager.handleLeaveAction(getOther(contact, "player")?.userData as? String)
        }
    }

    fun timeSinceLastContact(): Long {
        return System.currentTimeMillis() - lastContact
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
