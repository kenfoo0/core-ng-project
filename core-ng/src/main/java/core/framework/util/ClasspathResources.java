package core.framework.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.net.URLConnection;

import static core.framework.util.Strings.format;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author neo
 */
public final class ClasspathResources {
    public static String text(String path) {
        return new String(bytes(path), UTF_8);
    }

    public static byte[] bytes(String path) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL resource = loader.getResource(path);
        if (resource == null) throw new Error("can not load resource, path=" + path);

        URLConnection connection;
        int length;
        try {
            connection = resource.openConnection();
            length = connection.getContentLength();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        if (length <= 0) {
            throw new Error(format("unexpected length of classpath resource, path={}, length={}", path, length));
        }

        try (InputStream stream = connection.getInputStream()) {
            return InputStreams.bytesWithExpectedLength(stream, length);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

