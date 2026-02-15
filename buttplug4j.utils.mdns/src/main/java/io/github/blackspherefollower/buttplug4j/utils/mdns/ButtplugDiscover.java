package io.github.blackspherefollower.buttplug4j.utils.mdns;

import javax.jmdns.JmmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import javax.jmdns.ServiceTypeListener;
import java.io.Closeable;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * ButtplugDiscover.
 */
public final class ButtplugDiscover implements ServiceListener, Closeable {
    /**
     * Known servers.
     */
    private final ConcurrentHashMap<String, ConcurrentSkipListSet<URI>> servers = new ConcurrentHashMap<>();
    /**
     * mDNS implementation.
     */
    private final JmmDNS jmmdns;
    /**
     * Event handler.
     */
    private DiscovereyEventHandler discovereyEventHandler;

    /**
     * Constructor.
     *
     * @param aDiscovereyEventHandler event handler
     */
    public ButtplugDiscover(final DiscovereyEventHandler aDiscovereyEventHandler) {
        this.discovereyEventHandler = aDiscovereyEventHandler;
        jmmdns = JmmDNS.Factory.getInstance();
        jmmdns.addServiceListener("_intiface_engine._tcp.local.", this);
    }

    @Override
    public void serviceAdded(final ServiceEvent serviceEvent) {
        // Not used
    }

    @Override
    public void serviceRemoved(final ServiceEvent event) {
        servers.remove(event.getInfo().getName());
        if (discovereyEventHandler != null) {
            discovereyEventHandler.lostButtplug(event.getInfo().getName());
        }
    }

    @Override
    public void serviceResolved(final ServiceEvent event) {
        ConcurrentSkipListSet<URI> set = new ConcurrentSkipListSet<>();
        for (Inet4Address addr : event.getInfo().getInet4Addresses()) {
            try {
                set.add(new URI("ws://" + addr.getHostAddress() + ":" + event.getInfo().getPort()));
            } catch (URISyntaxException e) {
                // Log weirdness
            }
        }
        ConcurrentSkipListSet<URI> set2 = servers.putIfAbsent(event.getName(), set);
        if (set2 != null) {
            for (URI uri : set) {
                set2.add(uri);
            }
        }

        if (discovereyEventHandler != null) {
            discovereyEventHandler.foundButtplug(event.getName(), set);
        }
    }

    /**
     * Get known servers.
     *
     * @return servers
     */
    public ConcurrentHashMap<String, ConcurrentSkipListSet<URI>> getServers() {
        return servers;
    }

    @Override
    public void close() throws IOException {
        jmmdns.close();
    }

    /**
     * DiscovereyEventHandler interface.
     */
    public interface DiscovereyEventHandler {
        /**
         * Called when Buttplug server found.
         *
         * @param name      name
         * @param addresses addresses
         */
        void foundButtplug(String name, Set<URI> addresses);

        /**
         * Called when Buttplug server lost.
         *
         * @param name name
         */
        void lostButtplug(String name);
    }

}
