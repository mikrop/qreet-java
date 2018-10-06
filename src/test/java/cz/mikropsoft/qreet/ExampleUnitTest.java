package cz.mikropsoft.qreet;

import cz.mikropsoft.qreet.scheme.EetUctenka;
import cz.mikropsoft.qreet.scheme.Rezim;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    /*
        Datum a čas přijetí tržby: 2017-05-06T14:01:10+02:00
        Celková částka tržby: 34113,00,- Kč
        Režim tržby: běžný
        BKP kód: 6455B192-D697186A-6AB1971A-1E9B146B-CDD5007B
        FIK kód: 2c4ccf70-0055-44f2-804e-3056786dd351-ff
        DIČ poplatníka: CZ7900110063

        Pouze povinné položky
            Pro FIK - 1101705061401074323134400085176503411300
            Pro BKP - 2101705061401168333761836002264103411300

        S nepovinnou položku DIČ
            Pro FIK - 14017050614017900110063074323134400085176503411300
            Pro BKP - 24017050614017900110063168333761836002264103411300
     */
    private static final String UCTENKA_CODE = "24017050614017900110063168333761836002264103411300";

    private static final DateTimeFormatter DATUM_TRANSAKCE_FORMAT = DateTimeFormatter.ofPattern("uuuu.MM.dd");
    private static final LocalDate datumTransakce = LocalDate.parse("2017.05.06", DATUM_TRANSAKCE_FORMAT);
    private static final DateTimeFormatter CAS_TRANSAKCE_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final LocalTime casTransakce = LocalTime.parse("14:01", CAS_TRANSAKCE_FORMAT);

    private EetUctenka uctenka;

    @Before
    public void init() {
        this.uctenka = new EetUctenka(
                /*"2c4ccf70-0055-44f2-804e-3056786dd351-ff"*/ null,
                "6455B192-D697186A-6AB1971A-1E9B146B-CDD5007B",
                "CZ7900110063",
                34113.00d,
                datumTransakce,
                casTransakce,
                Rezim.BEZNY
        );
    }

    /**
     * Spojí předaná čísla a převede je do hexadecimálního tvaru.
     *
     * @param prvni zdroj
     * @param druhe další číslo
     * @return BKP v hexadecimálním tvaru
     */
    private static String toHexBkp(Long prvni, Long druhe) {
        return (Long.toHexString(prvni) + "-" + Long.toHexString(druhe)).toUpperCase();
    }

    @Test
    public void toHexString() throws Exception {

        String bkp = "16833376183600226410";
        Long prvni = 1683337618L;
        Long druhe = 3600226410L;
        String hex = "6455B192-D697186A"; // -6AB1971A-1E9B146B-CDD5007B
        String dst = ExampleUnitTest.toHexBkp(prvni, druhe);

        assertEquals(hex, dst);
    }

    @Test
    public void qreetToString() throws Exception {
        String text = uctenka.toString();
        assertEquals(UCTENKA_CODE, text);
    }

    @Test
    public void parseQreet() throws Exception {
        EetUctenka uctenka = EetUctenka.parse(UCTENKA_CODE);
        Assert.assertEquals(this.uctenka.toString(), uctenka.toString());
    }

    @Test
    public void qreetToFile() throws Exception {

        String text = uctenka.toString();
        File file = QRCode.from(text)
                .to(ImageType.JPG).file("QRCode.jpg");
        assertNotNull(file);
    }

}