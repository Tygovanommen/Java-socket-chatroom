package user;

import java.util.HashMap;
import java.util.Map;

public class TextValidator {

    private final Map<String, Integer> emojis = new HashMap<String, Integer>() {{
        put(":)", 0x1F60A);
        put(":d", 0x1F604);
        put(";)", 0x1F609);
        put(":o", 0x1F62E);
        put(":p", 0x1F60B);
        put(":$", 0x1F633);
        put(":(", 0x1F615);
        put(":'(", 0x1F625);
        put(":|", 0x1F610);
        put(">:)", 0x1F608);
        put(">:(", 0x1F621);
        put(":]", 0x1F60F);
        put("<3", 0x2764);
    }};

    /**
     * Loop through all emoji's and see if text in message can be replace
     * @param message that needs emoji replacement
     * @return new message with emoji's
     */
    public String replaceEmoji(String message) {
        if (message != null) {
            for (HashMap.Entry<String, Integer> emojis : emojis.entrySet()) {
                message = message.replace(emojis.getKey(), new String(Character.toChars(emojis.getValue())));
            }
        }
        return message;
    }

}