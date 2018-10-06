package cz.mikropsoft.qreet.utils;

import com.sun.istack.internal.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtils {

    // Vzor pro DIČ
    private static final Pattern DIC_PATTERN = Pattern.compile("^(CZ)?(\\d{8,10})$");

    /**
     * Parsuje DIČ z předaného řetězce.
     *
     * @param dic zdroj
     * @return vypasovaný DIČ
     */
    @Nullable
    public static String parseDic(String dic) {

        if (dic != null) {
            Matcher matcher = DIC_PATTERN.matcher(dic.toUpperCase());
            if (matcher.matches()) {
                String result = matcher.group(2);
                return result;
            }
            throw new IllegalArgumentException("Předaný DIČ neodpovídá vzoru \"CZ[0-9]{8,10}\"");
        }
        return null;
    }

}
