package taptap.config;

import org.yaml.snakeyaml.Yaml;
import taptap.exception.TapTapClientException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigurationLoader {

    static String configFileName="config.yaml";

    private static final Logger logger = Logger.getLogger(ConfigurationLoader.class.getName());

    public static TaptapHttpClientConfigs loadConfiguration(String configFileName) throws TapTapClientException {

        logger.log(Level.INFO,"Loading configuration from "+configFileName);

        if (configFileName == null)
            configFileName = ConfigurationLoader.configFileName;

        Yaml yaml = new Yaml();
        try (InputStream inputStream = TaptapHttpClientConfigs.class.getClassLoader().getResourceAsStream(configFileName)) {

            logger.log(Level.FINE,"Configuration file opened, loading into object");

            Map<String, Object> data = yaml.load(inputStream);
            return yaml.loadAs(yaml.dump(data), TaptapHttpClientConfigs.class);
        }
        catch (Exception e) {
            TapTapClientException tapClientException =  new TapTapClientException("Error in reading configuration file. ",e);
            logger.log(Level.SEVERE,"Error in reading configuration file.",tapClientException);
            throw tapClientException;
        }

    }

}
