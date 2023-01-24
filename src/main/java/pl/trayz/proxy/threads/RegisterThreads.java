package pl.trayz.proxy.threads;

import pl.trayz.proxy.ProxyApp;
import pl.trayz.proxy.threads.threads.KeepAliveThread;
import pl.trayz.proxy.utils.Dispatcher;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Trayz
 **/

/**
 * Register threads (Using Dispatcher class)
 */
public class RegisterThreads {
    public RegisterThreads(ProxyApp proxyApp) {
        Dispatcher.getScheduled().scheduleAtFixedRate(new KeepAliveThread(proxyApp), 0, 15, TimeUnit.SECONDS);
    }
}
