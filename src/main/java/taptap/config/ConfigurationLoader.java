package taptap.config;

import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import taptap.exception.TapTapClientException;

import java.io.InputStream;
import java.util.Map;

public class ConfigurationLoader {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ConfigurationLoader.class.getName());

    static String configFileName="config.yaml";


    public static Config loadConfiguration(String configFileName) throws TapTapClientException {

        logger.info("Loading configuration from "+configFileName);

        if (configFileName == null)
            configFileName = ConfigurationLoader.configFileName;

        Yaml yaml = new Yaml();
        try (InputStream inputStream = Config.class.getClassLoader().getResourceAsStream(configFileName)) {

            logger.debug("Configuration file opened, loading into object");

            Map<String, Object> data = yaml.load(inputStream);
            return yaml.loadAs(yaml.dump(data), Config.class);
        }
        catch (Exception e) {
            TapTapClientException tapClientException =  new TapTapClientException("Error in reading configuration file. ",e);
            logger.error("Error in reading configuration file.",tapClientException);
            throw tapClientException;
        }

    }

}
