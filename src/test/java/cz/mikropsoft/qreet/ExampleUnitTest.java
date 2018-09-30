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

    private static final String UCTENKA_CODE = "EET*1.0*BKP:DE7AB57EF9F1B523*DIC:45316872*KC:117.00*DT:201809281015*R:B";

    private static final DateTimeFormatter DATUM_TRANSAKCE_FORMAT = DateTimeFormatter.ofPattern("uuuu.MM.dd");
    private static final LocalDate datumTransakce = LocalDate.parse("2018.09.28", DATUM_TRANSAKCE_FORMAT);
    private static final DateTimeFormatter CAS_TRANSAKCE_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final LocalTime casTransakce = LocalTime.parse("10:15", CAS_TRANSAKCE_FORMAT);

    private EetUctenka uctenka;

    @Before
    public void init() {
        this.uctenka = new EetUctenka(null,
                "DE7AB57EF9F1B523",
                "45316872",
                117d,
                datumTransakce,
                casTransakce,
                Rezim.B
        );
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