package dev.lionk.infojump.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.utils.viewport.ScreenViewport
import dev.lionk.infojump.Main
import dev.lionk.infojump.data.Settings
import dev.lionk.infojump.game.GameManager
import dev.lionk.infojump.multiplayer.MultiplayerManager
import dev.lionk.infojump.payloads.HandshakePayload
import dev.lionk.infojump.payloads.Player
import dev.lionk.infojump.payloads.PlayerListUpdatePayload
import dev.lionk.infojump.rendering.TextureManager
import dev.lionk.infojump.tick.TickQueue
import dev.lionk.infojump.views.components.InputField
import dev.lionk.infojump.views.menu.MenuBackground

class MultiplayerView: AbstractView() {
    val stage: Stage = Stage(ScreenViewport())
    val background: MenuBackground = MenuBackground(stage, "menu_multiplayer.background")
    val font: BitmapFont
    val buttonBackground = NinePatchDrawable(NinePatch(TextureManager.getTexture("ui.buttons.default"), 8, 8, 8, 8))
    val ipInput : InputField
    val portInput : InputField
    val usernameInput : InputField
    var connectionStage : ConnectionStage = ConnectionStage.WaitingForInput
    val loginTable = Table()

    init {
        Gdx.input.inputProcessor = stage


        loginTable.setFillParent(false)
        loginTable.setDebug(Settings.isDebugging)
        loginTable.center()
        loginTable.background = buttonBackground

        font = createFont(
            size = 32,
            color = Color.WHITE,
            borderSize = 1.3f,
            borderColor = Color.BLACK
        )



        ipInput = InputField(
            "Server-Adresse",
            "spielserver.xyz",
            font
        )

        portInput = InputField(
            "Port",
            "67810",
            font
        )

        usernameInput = InputField(
            "Benutzername",
            "Michiiiiiiiii",
            font
        )

        val backButton = TextButton(
            "Zurück",
            TextButton.TextButtonStyle(null, null, null, font),
        )
        backButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                Main.INSTANCE.changeView("menu")
            }
        })
        backButton.pad(0f, 0f, 0f, 10f)
        val confirmButton = TextButton(
            "Verbinden",
            TextButton.TextButtonStyle(null, null, null, font),
        )
        confirmButton.pad(0f, 10f, 0f, 0f)

        confirmButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                TickQueue.addFunction {
                    if(!ipInput.getText().isNullOrBlank()
                        && !portInput.getText().isNullOrBlank()
                        && !usernameInput.getText().isNullOrBlank()) {
                        connectionStage = ConnectionStage.TCPConnecting
                        MultiplayerManager.connect(ipInput.getText(), portInput.getText(), usernameInput.getText())
                    }
                }
            }
        })

        val wrapper = Table()
        wrapper.setFillParent(false)
        wrapper.setDebug(Settings.isDebugging)
        wrapper.center()
        wrapper.add(backButton)
        wrapper.add(confirmButton)
        wrapper.pad(10f)


        loginTable.add(ipInput.getActor())
        loginTable.row()
        loginTable.add(portInput.getActor())
        loginTable.row()
        loginTable.add(usernameInput.getActor())
        loginTable.row()
        loginTable.add(wrapper)



        val container = Container<Table>(loginTable)
        container.center()
        container.setFillParent(true)

        stage.addActor(container)

    }

    fun handleHandshake(
        handshake: HandshakePayload
    ){
        if(handshake.success) {
            loginTable.isVisible = false
            createMultiplayerView(handshake.players)
            connectionStage = ConnectionStage.Connected
        }else connectionStage = ConnectionStage.WaitingForInput
    }

    fun handlePlayerListUpdate(
        packet: PlayerListUpdatePayload
    ){
        createMultiplayerView(packet.list)
    }


    val playerListTable = Table()

    private fun createMultiplayerView(
        players:List<Player>
    ){
        playerListTable.clear()

        playerListTable.setFillParent(false)
        playerListTable.setDebug(Settings.isDebugging)
        playerListTable.pad(10f)
        playerListTable.center().padRight(50f)

        val textStyle = TextButton.TextButtonStyle(null, null, null, font)
        for (player in players){
            playerListTable.add(TextButton(
                player.name, textStyle
            ))
            if (player.ready) playerListTable.add(
                Image(TextureManager.getTexture("ui.buttons.haken"))
            )
            playerListTable.row()
        }

        if(!(players.find { it.name == MultiplayerManager.name }?.ready?:false)){
            playerListTable.row()
            val buttonStyle = TextButton.TextButtonStyle(buttonBackground, null, null, font)
            buttonStyle.overFontColor = Color.SKY
            playerListTable.add(
                TextButton(
                    "Bereit",
                    buttonStyle
                ).apply {
                    addListener(object : ChangeListener() {
                        override fun changed(event: ChangeEvent?, actor: Actor?) {
                            MultiplayerManager.sendReadyCheck()
                        }
                    })

                    //background(buttonBackground)
                    //background = buttonBackground
                }
            )
            playerListTable.row()
        }

        val container = Container<Table>(playerListTable)
        container.center()
        container.setFillParent(true)

        stage.addActor(container)
    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        background.render(Gdx.graphics.deltaTime)
        stage.act(Gdx.graphics.deltaTime)

        stage.draw()
    }

    override fun dispose() {
        stage.dispose()
    }

    override fun handleInput() {

    }

    override fun onResize(width: Int, height: Int) {
        background.onResize(width, height)
        stage.viewport.update(width, height, true)
    }
}

enum class ConnectionStage{
    WaitingForInput,
    TCPConnecting,
    Connected
}
