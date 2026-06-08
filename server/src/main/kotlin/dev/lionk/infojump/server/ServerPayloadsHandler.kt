package dev.lionk.infojump.server

import dev.lionk.infojump.LionLog
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

class ServerPayloadsHandler(private val webSocketServer: WebSocketServer) : SimpleChannelInboundHandler<String>() {

    @Throws(Exception::class)
    override fun handlerAdded(ctx: ChannelHandlerContext) {
        val incoming: Channel = ctx.channel()
        webSocketServer.addClientChannel(incoming) // Add channel to WebSocketServer's client group
        LionLog.server("Incoming connection from ${incoming.remoteAddress()}")
    }

    @Throws(Exception::class)
    override fun handlerRemoved(ctx: ChannelHandlerContext) {
        val incoming: Channel = ctx.channel()
        webSocketServer.removeClientChannel(incoming) // Remove channel from WebSocketServer's client group
        GameManager.removePlayer(incoming.remoteAddress().toString())
        LionLog.server("Connection with ${incoming.remoteAddress()} lost")
    }

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, msg: String?) {
        val incoming: Channel = ctx.channel()
        if (msg != null) {
            // Invoke the onReceive callback in WebSocketServer
            webSocketServer.onReceive?.invoke(incoming.remoteAddress().toString(), msg)
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        System.err.println("Error handling client " + ctx.channel().remoteAddress())
        cause.printStackTrace()
        ctx.close()
    }
}
