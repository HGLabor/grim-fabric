package org.bukkit;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * @deprecated
 */
@Deprecated
public enum ChatColor {
    BLACK('0', 0) {
    },
    DARK_BLUE('1', 1) {
    },
    DARK_GREEN('2', 2) {
    },
    DARK_AQUA('3', 3) {
    },
    DARK_RED('4', 4) {
    },
    DARK_PURPLE('5', 5) {
    },
    GOLD('6', 6) {
    },
    GRAY('7', 7) {
    },
    DARK_GRAY('8', 8) {
    },
    BLUE('9', 9) {
    },
    GREEN('a', 10) {
    },
    AQUA('b', 11) {
    },
    RED('c', 12) {
    },
    LIGHT_PURPLE('d', 13) {
    },
    YELLOW('e', 14) {
    },
    WHITE('f', 15) {
    },
    MAGIC('k', 16, true) {
    },
    BOLD('l', 17, true) {
    },
    STRIKETHROUGH('m', 18, true) {
    },
    UNDERLINE('n', 19, true) {
    },
    ITALIC('o', 20, true) {
    },
    RESET('r', 21) {
    };

    public static final char COLOR_CHAR = '§';
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf('§') + "[0-9A-FK-ORX]");
    private final int intCode;
    private final char code;
    private final boolean isFormat;
    private final String toString;
    private static final Map<Integer, ChatColor> BY_ID = Maps.newHashMap();
    private static final Map<Character, ChatColor> BY_CHAR = Maps.newHashMap();

    private ChatColor(char code, int intCode) {
        this(code, intCode, false);
    }

    private ChatColor(char code, int intCode, boolean isFormat) {
        this.code = code;
        this.intCode = intCode;
        this.isFormat = isFormat;
        this.toString = new String(new char[]{'§', code});
    }

    public char getChar() {
        return this.code;
    }

    public @NotNull String toString() {
        return this.toString;
    }

    public boolean isFormat() {
        return this.isFormat;
    }

    public boolean isColor() {
        return !this.isFormat && this != RESET;
    }

    public static @Nullable ChatColor getByChar(char code) {
        return (ChatColor) BY_CHAR.get(code);
    }

    public static @Nullable ChatColor getByChar(@NotNull String code) {
        Preconditions.checkArgument(code != null, "Code cannot be null");
        Preconditions.checkArgument(code.length() > 0, "Code must have at least one char");
        return (ChatColor) BY_CHAR.get(code.charAt(0));
    }

    @Contract("!null -> !null; null -> null")
    public static @Nullable String stripColor(@Nullable String input) {
        return input == null ? null : STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

    public static @NotNull String translateAlternateColorCodes(char altColorChar, @NotNull String textToTranslate) {
        Preconditions.checkArgument(textToTranslate != null, "Cannot translate null text");
        char[] b = textToTranslate.toCharArray();

        for (int i = 0; i < b.length - 1; ++i) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(b[i + 1]) > -1) {
                b[i] = 167;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }

        return new String(b);
    }

    public static @NotNull String getLastColors(@NotNull String input) {
        Preconditions.checkArgument(input != null, "Cannot get last colors from null text");
        String result = "";
        int length = input.length();

        for (int index = length - 1; index > -1; --index) {
            char section = input.charAt(index);
            if (section == 167 && index < length - 1) {
                String hexColor = getHexColor(input, index);
                if (hexColor != null) {
                    result = hexColor + result;
                    break;
                }

                char c = input.charAt(index + 1);
                ChatColor color = getByChar(c);
                if (color != null) {
                    String var10000 = color.toString();
                    result = var10000 + result;
                    if (color.isColor() || color.equals(RESET)) {
                        break;
                    }
                }
            }
        }

        return result;
    }

    private static @Nullable String getHexColor(@NotNull String input, int index) {
        if (index < 12) {
            return null;
        } else if (input.charAt(index - 11) == 'x' && input.charAt(index - 12) == 167) {
            for (int i = index - 10; i <= index; i += 2) {
                if (input.charAt(i) != 167) {
                    return null;
                }
            }

            for (int i = index - 9; i <= index + 1; i += 2) {
                char toCheck = input.charAt(i);
                if (toCheck < '0' || toCheck > 'f') {
                    return null;
                }

                if (toCheck > '9' && toCheck < 'A') {
                    return null;
                }

                if (toCheck > 'F' && toCheck < 'a') {
                    return null;
                }
            }

            return input.substring(index - 12, index + 2);
        } else {
            return null;
        }
    }

    static {
        for (ChatColor color : values()) {
            BY_ID.put(color.intCode, color);
            BY_CHAR.put(color.code, color);
        }

    }
}
