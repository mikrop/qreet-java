package cz.mikropsoft.qreet.utils;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtils {

    private static final Pattern FIK_PATTERN = Pattern.compile("^([0-9A-F]{8})-([0-9A-F]{4})(-[0-9A-F]{4}-[0-9A-F]{4}-[0-9A-F]{12}-[0-9A-F]{2})?$");
    private static final Pattern BKP_PATTERN = Pattern.compile("^([0-9A-F]{8})-([0-9A-F]{8})(-[0-9A-F]{8}-[0-9A-F]{8}-[0-9A-F]{8})?$");
    private static final Pattern DIC_PATTERN = Pattern.compile("^(CZ)?(\\d{8,10})$");

    /**
     * Předanou hexadecimální číslici tranformuje do dekadické soustavy a doplní ji nulami na celkový počet n číslic.
     *
     * @param s zdroj k transformaci
     * @param n maximální počet znaků, na které bude text doplněn
     * @return výsledek transformace
     */
    private static String encodeLeftPad(@NotNull String s, int n) {
        long l = Long.parseLong(s.trim(), 16);
        return String.format("%1$" + n + "s", l).replace(' ', '0');
    }

    /**
     * @see #encodeLeftPad(String, int)
     */
    private static String encodeLeftPad10(@NotNull String s) {
        return encodeLeftPad(s, 10);
    }

    /**
     * První tři skupiny po 8 a 4 hexadecimálních číslicích jsou převedeny do dekadické soustavy a doplněny
     * zleva nulami na celkový počet 10, 5 a 5 číslic, celkem tedy 20 číslic.
     *
     * @param fik zdroj
     * @return první tři skupiny
     */
    public static String encodeFik(@NotNull String fik) {

        if (fik == null) {
            throw new IllegalArgumentException("Hodnota FIK musí být předána.");
        }

        Matcher matcher = FIK_PATTERN.matcher(fik.toUpperCase());
        if (matcher.matches()) {

            StringBuilder sb = new StringBuilder();
            sb.append(StringUtils.encodeLeftPad10(matcher.group(1)));
            sb.append(StringUtils.encodeLeftPad(matcher.group(2), 5));
            sb.append(StringUtils.encodeLeftPad(matcher.group(3), 5));
            return sb.toString();
        }
        throw new IllegalArgumentException("Předaný FIK neodpovídá vzoru \"xxxxxxxx-xxxx-xxxx[-xxxx-xxxxxxxxxxxx-xx]\"");
    }

    /**
     * První dvě skupiny po 8 hexadecimálních číslicích jsou převedeny do dekadické soustavy a doplněny
     * zleva nulami na celkový počet 10 a 10 číslic, celkem tedy 20 číslic.
     *
     * @param bkp zdroj
     * @return první dvě skupiny
     */
    public static String encodeBkp(@NotNull String bkp) {

        if (bkp == null) {
            throw new IllegalArgumentException("Hodnota BKP musí být předána.");
        }

        Matcher matcher = BKP_PATTERN.matcher(bkp.toUpperCase());
        if (matcher.matches()) {

            StringBuilder sb = new StringBuilder();
            sb.append(StringUtils.encodeLeftPad10(matcher.group(1)));
            sb.append(StringUtils.encodeLeftPad10(matcher.group(2)));
            return sb.toString();
        }
        throw new IllegalArgumentException("Předaný BKP neodpovídá vzoru \"xxxxxxxx-xxxxxxxx[-xxxxxxxx-xxxxxxxx-xxxxxxxx]\"");
    }

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
