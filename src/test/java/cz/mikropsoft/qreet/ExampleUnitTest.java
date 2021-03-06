package cz.mikropsoft.qreet;

import cz.mikropsoft.qreet.scheme.EetUctenka;
import cz.mikropsoft.qreet.scheme.Rezim;
import cz.mikropsoft.qreet.utils.StringUtils;
import net.glxn.qrgen.javase.QRCode;
import org.junit.Before;
import org.junit.Test;

import java.io.FileOutputStream;

import static cz.mikropsoft.qreet.scheme.EetUctenka.DATUM_CAS_TRANSAKCE_FORMAT;
import static org.junit.Assert.assertEquals;

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
    private static final EetUctenka FIK = EetUctenka.ofFik(
            "2c4ccf70-0055-44f2-804e-3056786dd351-ff",
            "CZ7900110063",
            34113.00d,
            StringUtils.parseDatumCasTransakce("1705061401", DATUM_CAS_TRANSAKCE_FORMAT),
            Rezim.BEZNY
    );
    private static final EetUctenka BKP = EetUctenka.ofBkp(
            "6455B192-D697186A-6AB1971A-1E9B146B-CDD5007B",
            "CZ7900110063",
            34113.00d,
            StringUtils.parseDatumCasTransakce("1705061401", DATUM_CAS_TRANSAKCE_FORMAT),
            Rezim.BEZNY
    );

    private EetUctenka uctenka;

    @Before
    public void init() {
        this.uctenka = /*(Math.random() < 0.5) ? FIK :*/ BKP;
    }

    /**
     * Jak zakódovat informaci o účtence do QR řetězce.
     */
    @Test
    public void generateString() throws Exception {
        String text = uctenka.generateString();
        assertEquals(UCTENKA_CODE, text);
    }

    /**
     * Jak dekódovat předaný řetězec do objektu {@link EetUctenka}
     */
    @Test
    public void parseSchema() throws Exception {
        EetUctenka uctenka = new EetUctenka().parseSchema(UCTENKA_CODE);
        assertEquals(this.uctenka.generateString(), uctenka.generateString());
    }

    /**
     * Jak uložit QR kód jako obrázek na disk.
     */
    @Test
    public void qreetToFile() throws Exception {
        QRCode.from(uctenka)
                .writeTo(new FileOutputStream("C:/tmp/QRBKP.jpg"));
    }

}