package com.app.manage_restaurant.security;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import reactor.netty.http.server.HttpServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Configuration
public class NettySilentConfig implements WebServerFactoryCustomizer<NettyReactiveWebServerFactory> {

    @Override
    public void customize(NettyReactiveWebServerFactory factory) {
        factory.addServerCustomizers(this::customizeHttpServer);
    }

    private HttpServer customizeHttpServer(HttpServer httpServer) {
        return httpServer.doOnConnection(conn ->
            conn.addHandlerLast("silentMalformedRequestHandler", new ChannelInboundHandlerAdapter() {
                @Override
                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                    if (isMalformedRequest(cause)) {
                        // Ferme silencieusement la connexion
                        ctx.close();
                    } else {
                        // Autres exceptions → comportement normal
                        ctx.fireExceptionCaught(cause);
                    }
                }

                private boolean isMalformedRequest(Throwable cause) {
                    if (cause instanceof IllegalArgumentException && cause.getMessage() != null) {
                        String msg = cause.getMessage();
                        return msg.contains("Illegal character in request line") ||
                               msg.contains("Invalid HTTP request") ||
                               msg.contains("decodeResult: failure");
                    }
                    return false;
                }
            })
        );
    }
}
