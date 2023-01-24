package pl.trayz.proxy.configuration.api;

import com.google.gson.Gson;
import lombok.Data;
import pl.trayz.proxy.configuration.api.annotations.Config;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * @Author: Trayz
 **/

/**
 * Configuration loader class
 */
@Data
public class Configuration {

    private final Gson gson;

    /**
     * Creates new configuration loader with gson
     */
    public Configuration() {
        this.gson = new Gson().newBuilder().setPrettyPrinting().serializeNulls().create();
    }

    /**
     * Load configuration
     */
    public <T> T load(Class<T> tClass, Object defaults) throws IOException {
        if(!tClass.isAnnotationPresent(Config.class))
            return null;

        File file = new File(tClass.getAnnotation(Config.class).file());
        if(file.exists())
            return gson.fromJson(Files.readString(file.toPath()), tClass);

        return save(tClass, defaults);
    }

    /**
     * Save configuration
     */
    public <T> T save(Class<T> tClass, Object defaults) throws IOException {
        if (!tClass.isAnnotationPresent(Config.class))
            return null;

        String path = tClass.getAnnotation(Config.class).file();

        if(path.contains("/")) {
            String[] split = path.split("/");
            File file = new File(path.replace(split[split.length-1],""));
            if(!file.exists())
                file.mkdirs();
        }

        File file = new File(path);
        if (!file.exists())
            file.createNewFile();


        String json = gson.toJson(defaults);
        try (PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8)) {
            writer.println(json);
        }

        return (T) defaults;
    }
}
