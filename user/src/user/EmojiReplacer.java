package user;

import java.util.HashMap;
import java.util.Map;

public class EmojiReplacer {

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

    public String replaceString(String message) {
        String[] messagePieces = message.split("\\s+");
        for (int i = 0; i < messagePieces.length; i++) {
            if (emojis.containsKey(messagePieces[i])) {
                messagePieces[i] = new String(Character.toChars(emojis.get(messagePieces[i])));
            }
        }
        return String.join(" ", messagePieces);
    }
}
