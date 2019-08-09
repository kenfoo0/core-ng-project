package core.framework.impl.web.http;

import core.framework.web.exception.ForbiddenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author neo
 */
public class IPv4AccessControl {
    private final Logger logger = LoggerFactory.getLogger(IPv4AccessControl.class);
    public IPv4Ranges allow;
    public IPv4Ranges deny;

    public void validate(String clientIP) {
        try {
            InetAddress address = InetAddress.getByName(clientIP);
            if (isLocal(address)) return;

            if (!allow(address.getAddress())) {
                throw new ForbiddenException("access denied");
            }
        } catch (UnknownHostException e) {
            throw new Error(e);  // client ip format is already validated in ClientIPParser, so here it won't resolve DNS
        }
    }

    boolean isLocal(InetAddress address) {
        return address.isLoopbackAddress() || address.isSiteLocalAddress();
    }

    boolean allow(byte[] address) {
        if (address.length > 4) {   // only support ipv4, as Cloud LB generally uses ipv4 endpoint (gcloud supports both ipv4/v6, but ipv6 is not majority yet)
            logger.debug("skip with ipv6 address");
            return true;
        }
        if (deny != null) {
            if (allow != null && allow.matches(address)) {  // if deny is defined, check allow for exception if any
                return true;
            }
            return !deny.matches(address);  // allow by default
        } else if (allow != null) { // if only allow is defined, deny by default
            return allow.matches(address);
        } else {
            throw new Error("unexpected state, allow and deny must not both be null");
        }
    }
}