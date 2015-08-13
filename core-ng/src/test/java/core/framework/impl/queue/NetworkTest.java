package core.framework.impl.queue;

import core.framework.api.util.Network;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author neo
 */
public class NetworkTest {
    @Test
    public void localHostAddress() {
        String hostAddress = Network.localHostAddress();
        Assert.assertNotNull(hostAddress);
    }
}