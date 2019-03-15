package cz.mikropsoft.qreet.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class StringUtils {

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
