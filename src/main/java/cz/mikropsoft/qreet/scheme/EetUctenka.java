package cz.mikropsoft.qreet.scheme;

import cz.mikropsoft.qreet.utils.StringUtils;
import net.glxn.qrgen.core.scheme.Schema;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * EET účtenka.
 *
 * @author Michal Hájek, <a href="mailto:mikrop@centrum.cz">mikrop@centrum.cz</a>
 * @since 09.03.2019
 */
public class EetUctenka extends Schema {

    // Vzor data s časem
    public static final SimpleDateFormat DATUM_CAS_TRANSAKCE_FORMAT = new SimpleDateFormat("yyMMddHHmm");

    /**
     * VERZE : REŽIM TRŽBY : DATUM      : DIČ        : KÓDY                 : ČÁSTKA
     * 14    : 0           : 1705061401 : 7900110063 : 07432313440008517650 : 3411300       pro FIK
     * 24    : 0           : 1705061401 : 7900110063 : 16833376183600226410 : 3411300       pro BKP
     */
    private static final Pattern QR_PATTERN = Pattern.compile("^(\\d{2})(\\d{1})(\\d{10})(\\d{10})(\\d{20})(\\d+)");

    private Rezim rezim;
    private Date datumCasTransakce;
    private Dic dic;
    private Kod kod;
    private double castka;

    /**
     * Účtenka kód pro účely účtenkové loterie.
     */
    public EetUctenka() {
        super();
    }

    /**
     * Privátní konstruktor.
     *
     * @param kod FIK nebo BKP
     * @param dic nepovinný DIČ poplatníka
     * @param castka zaplaceno
     * @param datumCasTransakce datum a čas kdy byla platba provedena
     * @param rezim režim v jakém byla účtenka vystavena
     */
    private EetUctenka(Kod kod, String dic, double castka, Date datumCasTransakce, Rezim rezim) {
        this.kod = kod;
        this.dic = Dic.parse(dic);
        this.castka = castka;
        this.datumCasTransakce = datumCasTransakce;
        this.rezim = rezim;
    }

    /**
     * Statická factory k vytvoření {@link EetUctenka} s fiskálním identifikačním kódem.
     *
     * @param fik fiskální identifikační kód
     * @param dic nepovinný DIČ poplatníka
     * @param castka zaplaceno
     * @param datumCasTransakce datum a čas kdy byla platba provedena
     * @param rezim režim v jakém byla účtenka vystavena
     * @return naplněná {@link EetUctenka}
     * @see EetUctenka (Kod, String, double, Date, Rezim)
     */
    public static EetUctenka ofFik(String fik, String dic, double castka, Date datumCasTransakce, Rezim rezim) {
        return new EetUctenka(Kod.ofFik(fik), dic, castka, datumCasTransakce, rezim);
    }

    /**
     * Statická factory k vytvoření {@link EetUctenka} s bezpečnostním kódem poplatníka.
     *
     * @param bkp bezpečnostní kód poplatníka
     * @param dic nepovinný DIČ poplatníka
     * @param castka zaplaceno
     * @param datumCasTransakce datum a čas kdy byla platba provedena
     * @param rezim režim v jakém byla účtenka vystavena
     * @return naplněná {@link EetUctenka}
     * @see EetUctenka (Kod, String, double, Date, Rezim)
     */
    public static EetUctenka ofBkp(String bkp, String dic, double castka, Date datumCasTransakce, Rezim rezim) {
        return new EetUctenka(Kod.ofBkp(bkp), dic, castka, datumCasTransakce, rezim);
    }

    /**
     * Zakódovaná verze v dekadické soustavě (2 číslice).
     *
     * @return 2 číslice dekadické soustavy
     */
    private Verze getVerze() {
        return new Verze(kod.getTyp(), dic.getVerze());
    }

    /**
     * Vrací {@link Rezim} v jakém byla účtenka vystavena.
     *
     * @return režim
     */
    private Rezim getRezim() {
        return rezim;
    }

    /**
     * 12 číslic datum a čas tržby ve formátu {@link #DATUM_CAS_TRANSAKCE_FORMAT}, formát ISO 8601.
     *
     * @return datum a čas transakce
     */
    private String qrDatum() {
        if (datumCasTransakce == null) {
            throw new IllegalArgumentException("Datum transakce musí být předán");
        } else {
            return DATUM_CAS_TRANSAKCE_FORMAT.format(datumCasTransakce);
        }
    }

    /**
     * 8-10 číslic DIČ.
     *
     * @return DIČ poplatníka
     */
    private Dic getDic() {
        return dic;
    }

    /**
     * Kód FIK nebo BKP.
     *
     * @return {@link Kod}
     */
    private Kod getKod() {
        return kod;
    }

    /**
     * 1-10 znaků z množiny [0-9.] cena na účtence v Kč. Desetinné číslo, max. 2 desetinné cifry, Tečka jako oddělovač desetinných míst.
     * Maximální možná hodnota je 9 999 999.99
     *
     * @return částka
     */
    private String qrCastka() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("cs", "CZ"));
        DecimalFormat CASTKA_FORMAT = new DecimalFormat("##0.00", symbols);
        return CASTKA_FORMAT.format(castka).replace(",", "");
    }

    /**
     * Dekódovat předaný řetězec do objektu {@link EetUctenka}.
     *
     * @param value zakódovaná informaci o účtence
     * @return naplněný objekt {@link EetUctenka}
     */
    @Override
    public EetUctenka parseSchema(String value) {
        if (value != null) {

            Matcher matcher = QR_PATTERN.matcher(value);
            if (matcher.matches()) {

                Verze verze = Verze.parse(matcher.group(1));
                this.rezim = Rezim.parse(matcher.group(2));
                this.datumCasTransakce = StringUtils
                        .parseDatumCasTransakce(matcher.group(3), DATUM_CAS_TRANSAKCE_FORMAT);
                this.dic = Dic.parse(matcher.group(4));
                this.kod = Kod.parse(verze.getTyp(), matcher.group(5));
                String s = matcher.group(6);
                this.castka = Double.valueOf(s) / 100;

                return this;
            }
            throw new IllegalArgumentException("Parsování předaného kódu: " + value + ", se nezdařilo");

        } else {
            throw new IllegalArgumentException("Toto není validní QR kód EET účtenky: " + value);
        }
    }

    /**
     * VERZE : REŽIM TRŽBY : DATUM : DIČ : KÓDY : ČÁSTKA
     *
     * @return zakódovaná informaci o účtence
     */
    @Override
    public String generateString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getVerze().qrValue());
        sb.append(getRezim().qrValue());
        sb.append(qrDatum());
        Dic dic = getDic();
        if (dic.isNotEmpty()) {
            sb.append(dic.qrValue());
        }
        sb.append(getKod().qrValue());
        sb.append(qrCastka());
        return sb.toString();
    }
}
