package pl.trayz.proxy.plugins;

import com.google.common.collect.Maps;
import com.google.common.reflect.ClassPath;
import lombok.Getter;
import lombok.SneakyThrows;
import pl.trayz.proxy.ProxyApp;
import pl.trayz.proxy.commands.api.Command;
import pl.trayz.proxy.commands.api.CommandMeta;
import pl.trayz.proxy.plugins.interfaces.ProxyPlugin;
import pl.trayz.proxy.utils.Dispatcher;
import pl.trayz.proxy.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * @Author: Trayz
 **/

/**
 * Loading plugin from plugins directory
 */
@Getter
public class PluginLoader {

    /**
     * List of loaded plugins
     */
    private final List<ProxyPlugin> plugins = new ArrayList<>();

    /**
     * List of loaded commands
     */
    private final Map<CommandMeta, Command> commandMap = Maps.newConcurrentMap();

    /**
     * Loading plugins from plugins directory
     * if directory not exists and external, directory don't be created
     * if directory not exists and !external, it will be created
     */
    @SneakyThrows
    public PluginLoader(boolean external) {
        final File directory = new File("plugins");

        /**
         * Load internal plugins
         */
        Dispatcher.getService().execute(() -> {
            try {
                ClassPath classPath = ClassPath.from(ProxyApp.class.getClassLoader());
                Set<ClassPath.ClassInfo> classes = classPath.getAllClasses();

                for (ClassPath.ClassInfo classInfo : classes) {
                    checkClass(classInfo.getName(), ProxyApp.class.getClassLoader());
                }

                if (!directory.exists() && external) {
                    Logger.logInfo("Loaded " + plugins.size() + " plugins!");
                    return;
                }

                if (!directory.exists()) {
                    directory.mkdir();
                }

                /**
                 * Loading jar files from plugins directory
                 */
                for (final File file : Arrays.stream(directory.listFiles()).filter(file -> file.getName().endsWith(".jar")).collect(Collectors.toList())) {
                    try {
                        JarFile jarFile = new JarFile(file);
                        Enumeration<JarEntry> e = jarFile.entries();

                        URL[] urls = {new URL("jar:file:" + file.getPath() + "!/")};
                        URLClassLoader cl = URLClassLoader.newInstance(urls);

                        while (e.hasMoreElements()) {

                            JarEntry je = e.nextElement();
                            if (je.isDirectory() || !je.getName().endsWith(".class")) {
                                continue;
                            }

                            String className = je.getName().substring(0, je.getName().length() - 6);
                            checkClass(className, cl);
                        }

                        jarFile.close();
                        cl.close();
                    } catch (Exception e) {
                        Logger.logError("Error while loading plugin " + file.getName() + " " + e.getMessage());
                        e.printStackTrace();
                    } catch (NoClassDefFoundError ignored) {
                    }

                }

                Logger.logInfo("Loaded " + plugins.size() + " plugins!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Checking class if it's plugin main or command
     */
    @SneakyThrows
    private void checkClass(String className, ClassLoader cl) {
        try {
            className = className.replace('/', '.');
            Class c = cl.loadClass(className);

            //LoadMainClass
            ProxyPlugin annotation = (ProxyPlugin) c.getDeclaredAnnotation(ProxyPlugin.class);
            if (annotation != null) {
                c.newInstance();
                plugins.add(annotation);
                Logger.logInfo("Loaded plugin " + annotation.name() + " by " + Arrays.toString(annotation.authors()) + " version: " + annotation.version() + " description: " + annotation.description());
            }

            //LoadCommands
            CommandMeta annotationCommand = (CommandMeta) c.getDeclaredAnnotation(CommandMeta.class);
            if (annotationCommand != null) {
                if (c.getSuperclass().equals(Command.class)) {
                    commandMap.put(annotationCommand, (Command) c.newInstance());
                }
            }
        }
        catch (Exception e) {
            Logger.logError("Error while loading class " + className + " " + e.getMessage());
            e.printStackTrace();
        } catch (NoClassDefFoundError | VerifyError ignored) {
        }
    }

}
