package user;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Property {

    private final Properties mainProperty = new Properties();

    public Property() {
        String path = "./user.properties";
        try {
            FileInputStream file = new FileInputStream(path);
            mainProperty.load(file);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getProperty(String property) {
        return mainProperty.getProperty(property);
    }
}
