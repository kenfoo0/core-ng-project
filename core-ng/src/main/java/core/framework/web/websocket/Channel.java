package core.framework.web.websocket;

import javax.annotation.Nullable;

/**
 * @author neo
 */
public interface Channel {
    <T> void send(T message);

    Context context();

    void close();

    void join(String room);

    void leave(String room);

    interface Context {
        @Nullable
        Object get(String key); // channel is stateful, context key usually be static put onConnect, so in most of case get(key) expects result, that's why here is designed to return Object, not Optional<T>

        void put(String key, @Nullable Object value);
    }
}
