package cz.mikropsoft.qreet.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    /**
     * Parsuje předaný řetězec (datum a čas transakce).
     *
     * @param datumCasTransakce datum a čas transakce
     * @param format formát předaného data
     * @return {@link Date}
     */
    public static Date parseDatumCasTransakce(String datumCasTransakce, SimpleDateFormat format) {
        try {
            return format.parse(datumCasTransakce);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Chyba parsování data a času transakce.", e);
        }
    }

}
