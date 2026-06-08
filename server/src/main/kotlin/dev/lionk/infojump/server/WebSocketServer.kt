package dev.lionk.infojump.server

import dev.lionk.infojump.LionLog
import dev.lionk.infojump.payloads.LionDeserialization
import dev.lionk.infojump.payloads.Payload
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.MultiThreadIoEventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.nio.NioIoHandler
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.string.StringDecoder
import io.netty.handler.codec.string.StringEncoder
import io.netty.channel.group.ChannelGroup
import io.netty.channel.group.DefaultChannelGroup
import io.netty.util.concurrent.GlobalEventExecutor

class WebSocketServer(
    val port: Int
) {
    // Centralized management of connected client channels
    private val clients: ChannelGroup = DefaultChannelGroup(GlobalEventExecutor.INSTANCE)

    // Callback for when a message is received
    var onReceive: ((sourceAddress: String, message: String) -> Unit)? = { sourceAddress, message ->
        LionLog.server("Received message from $sourceAddress: $message")
        PayloadManager.handleMessage(sourceAddress, message)
    }

    private var bossGroup: EventLoopGroup? = null
    private var workerGroup: EventLoopGroup? = null
    private var _isRunning: Boolean = false

    val isRunning: Boolean
        get() = _isRunning

    fun start() {
        if (_isRunning) {
            LionLog.server("Server is already running on port $port")
            return
        }

        bossGroup = MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory())
        workerGroup = MultiThreadIoEventLoopGroup(NioIoHandler.newFactory())

        try {
            val b = ServerBootstrap()
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(object : ChannelInitializer<SocketChannel?>() {
                    override fun initChannel(ch: SocketChannel?) {
                        val pipeline = ch?.pipeline()

                        pipeline?.addLast(StringDecoder())
                        pipeline?.addLast(StringEncoder())
                        // Pass the WebSocketServer instance to the handler
                        pipeline?.addLast(ServerPayloadsHandler(this@WebSocketServer))
                    }
                })

            LionLog.server("Server started on port $port")
            _isRunning = true
            val f = b.bind(port).sync()
            f.channel().closeFuture().sync()
        } finally {
            stop() // Ensure groups are shut down even if an exception occurs
        }
    }

    fun stop() {
        if (!_isRunning) {
            LionLog.server("Server is not running.")
            return
        }
        LionLog.server("Stopping server on port $port")
        workerGroup?.shutdownGracefully()
        bossGroup?.shutdownGracefully()
        _isRunning = false
        LionLog.server("Server stopped.")
    }

    /**
     * Sends a message to one or more specified client targets.
     * @param message The message content to send.
     * @param targets A list of client addresses (as Strings) to send the message to.
     *                If the list is empty, the message will be sent to all connected clients.
     */
    fun send(message: String, targets: List<String>) {
        if (targets.isEmpty()) {
            // Send to all clients if no specific targets are provided
            clients.writeAndFlush(message + "\n")
            LionLog.server("Sent message to all clients: $message")
        } else {
            for (channel in clients) {
                if (targets.contains(channel.remoteAddress().toString())) {
                    channel.writeAndFlush(message + "\n")
                    LionLog.server("Sent message to ${channel.remoteAddress()}: $message")
                }
            }
        }
    }

    // Method to allow ServerPayloadsHandler to add/remove channels
    internal fun addClientChannel(channel: Channel) {
        clients.add(channel)
    }

    internal fun removeClientChannel(channel: Channel) {
        clients.remove(channel)
    }
}
