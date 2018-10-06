package cz.mikropsoft.qreet.scheme;

import cz.mikropsoft.qreet.utils.StringUtils;
import net.glxn.qrgen.core.scheme.Schema;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * EET účtenka.
 */
public class EetUctenka extends Schema {

    // Vzor data s časem
    private static final DateTimeFormatter DATUM_CAS_TRANSAKCE_FORMAT = DateTimeFormatter.ofPattern("uuMMddHHmm");

    /**
     * VERZE : REŽIM TRŽBY : DATUM      : DIČ        : KÓDY                 : ČÁSTKA
     * 14    : 0           : 1705061401 : 7900110063 : 07432313440008517650 : 3411300       pro FIK
     * 24    : 0           : 1705061401 : 7900110063 : 16833376183600226410 : 3411300       pro BKP
     */
    private static final Pattern QR_PATTERN = Pattern.compile("^(\\d{2})(\\d{1})(\\d{10})(\\d{10})(\\d{20})(\\d+)");

    private Rezim rezim;
    private LocalDate datumTransakce;
    private LocalTime casTransakce;
    private String dic;
    private Kod kod;
    private double castka;

    /**
     * Účtenka kód pro účely účtenkové loterie.
     */
    public EetUctenka() {
        super();
    }

    private EetUctenka(Kod kod, String dic, double castka, LocalDate datumTransakce,
                       LocalTime casTransakce, Rezim rezim) {

        this.rezim = rezim;
        this.datumTransakce = datumTransakce;
        this.casTransakce = casTransakce;
        this.dic = StringUtils.parseDic(dic);
        this.kod = kod;
        this.castka = castka;
    }

    public EetUctenka(Kod kod, String dic, double castka, LocalDateTime datumCasTransakce, Rezim rezim) {
        this(kod, dic, castka, datumCasTransakce.toLocalDate(), datumCasTransakce.toLocalTime(), rezim);
    }

    public EetUctenka(String fik, String bkp, String dic, double castka, LocalDate datumTransakce,
                      LocalTime casTransakce, Rezim rezim) {
        this(Kod.create(fik, bkp), dic, castka, datumTransakce, casTransakce, rezim);
    }

    /**
     * Verze QR kódu.
     *
     * @return {@link Verze}
     */
    public Integer getVerze() {
        return Verze.ofUctenka(this);
    }

    /**
     * Celkem tedy 20 číslic.
     *
     * @return FIK
     */
    public Kod getKod() {
        return kod;
    }

    /**
     * 8-10 číslic DIČ.
     *
     * @return DIČ
     */
    public String getDic() {
        return dic;
    }

    /**
     * 1-10 znaků z množiny [0-9.] cena na účtence v Kč. Desetinné číslo, max. 2 desetinné cifry, Tečka jako oddělovač desetinných míst.
     * Maximální možná hodnota je 9 999 999.99
     *
     * @return částka
     */
    private String getCastka() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("cs", "CZ"));
        DecimalFormat CASTKA_FORMAT = new DecimalFormat("##0.00", symbols);
        return CASTKA_FORMAT.format(castka).replace(",", "");
    }

    /**
     * 12 číslic datum a čas tržby ve formátu {@link #DATUM_CAS_TRANSAKCE_FORMAT}, formát ISO 8601.
     *
     * @return datum a čas transakce
     */
    private String getDatum() {
        if (datumTransakce == null) {
            throw new IllegalArgumentException("Datum transakce musí být předán");
        } else if (casTransakce == null) {
            throw new IllegalArgumentException("Čas transakce musí být předán");
        } else {
            LocalDateTime dt = LocalDateTime.of(datumTransakce, casTransakce);
            return DATUM_CAS_TRANSAKCE_FORMAT.format(dt);
        }
    }

    /**
     * Vrací {@link Rezim} v jakém byla účtenka vystavena.
     *
     * @return režim
     */
    private String getRezim() {
        if (rezim == null) {
            throw new IllegalArgumentException("Režim musí být předán");
        }
        return rezim.getValue();
    }

    @Override
    public String toString() {
        return generateString();
    }

    @Override
    public Schema parseSchema(String code) {
        if (code != null) {

            Matcher matcher = QR_PATTERN.matcher(code);
            if (matcher.matches()) {

                Verze verze = Verze.parse(matcher.group(1));
                this.rezim = Rezim.parse(matcher.group(2));

                LocalDateTime datumCasTransakce = LocalDateTime.parse(matcher.group(3), DATUM_CAS_TRANSAKCE_FORMAT);
                this.datumTransakce = datumCasTransakce.toLocalDate();
                this.casTransakce = datumCasTransakce.toLocalTime();

                this.dic = StringUtils.parseDic(matcher.group(4));

                Kod.Typ typ = verze.getTyp();
                if (typ == null) {
                    throw new IllegalArgumentException("Typ kódu je null");
                }
                String value = matcher.group(5);
                switch (typ) {
                    case FIK:

                        /*
                            2c4ccf70-0055-44f2-804e-3056786dd351-ff
                            07432313440008517650
                         */
                        this.kod = Kod.decodeFik(value);
                        break;
                    case BKP:

                        /*
                            6455B192-D697186A-6AB1971A-1E9B146B-CDD5007B
                            16833376183600226410
                         */
                        this.kod = Kod.decodeBkp(value);
                        break;
                    default:
                        throw new IllegalStateException("Nepodporovaný typ kódu: " + typ);
                }

                String s = matcher.group(6);
                this.castka = Double.valueOf(s) / 100;

                return this;
            }
            throw new IllegalArgumentException("Parsování předaného kódu: " + code + ", se nezdařilo");

        } else {
            throw new IllegalArgumentException("Toto není validní QR kód EET účtenky: " + code);
        }
    }

    /**
     * Vaparsuje z předanoho řetězce objekt {@link EetUctenka}.
     *
     * @param code QR kód pro účely účtenkové loterie
     * @return {@link EetUctenka}
     */
    public static EetUctenka parse(String code) {
        EetUctenka uctenka = new EetUctenka();
        uctenka.parseSchema(code);
        return uctenka;
    }

    @Override
    public String generateString() {

        // VERZE : REŽIM TRŽBY: DATUM : DIČ: KÓDY : ČÁSTKA
        StringBuilder sb = new StringBuilder();
        sb.append(getVerze());
        sb.append(getRezim());
        sb.append(getDatum());
        String dic = getDic();
        if (dic != null) {
            sb.append(dic);
        }
        sb.append(getKod().encode());
        sb.append(getCastka());
        return sb.toString();
    }

}
