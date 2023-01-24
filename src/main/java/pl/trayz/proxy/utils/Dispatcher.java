package pl.trayz.proxy.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @Author: Trayz
 **/

/**
 * Util for multi-threading
 */

public class Dispatcher {

    @Getter private static final ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor();
    @Getter @Setter private static ExecutorService service = Executors.newFixedThreadPool(3);

}
