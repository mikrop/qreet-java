package cz.mikropsoft.qreet.scheme;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import net.glxn.qrgen.core.scheme.Schema;
import net.glxn.qrgen.core.scheme.SchemeUtil;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * EET účtenka.
 */
public class EetUctenka extends Schema {

    protected static final String FIK = "FIK";
    protected static final String BKP = "BKP";
    protected static final String DIC = "DIC";
    protected static final String KC = "KC";
    protected static final String DT = "DT";
    protected static final String R = "R";

    private static final String VERSION = "1.0";
    private static final DateTimeFormatter DATUM_CAS_TRANSAKCE_FORMAT = DateTimeFormatter.ofPattern("uuuuMMddHHmm");

    private String fik;
    private String bkp;
    private String dic;
    private double castka;
    private LocalDate datumTransakce;
    private LocalTime casTransakce;
    private Rezim rezim;

    /**
     * Účtenka kód pro účely účtenkové loterie.
     */
    public EetUctenka() {
        super();
    }

    public EetUctenka(@Nullable String fik, @Nullable String bkp, @NotNull String dic, double castka, @NotNull LocalDate datumTransakce,
                      @NotNull LocalTime casTransakce, @NotNull Rezim rezim) {

        if (fik == null && bkp == null) {
            throw new IllegalArgumentException("FIK, nebo BKP musí být předán");
        }

        this.fik = fik;
        this.bkp = bkp;
        this.dic = dic;
        this.castka = castka;
        this.datumTransakce = datumTransakce;
        this.casTransakce = casTransakce;
        this.rezim = rezim;
    }

    public EetUctenka(@Nullable String fik, @Nullable String bkp, @NotNull String dic, double castka,
                      @NotNull LocalDateTime datumCasTransakce, @NotNull Rezim rezim) {
        this(fik, bkp, dic, castka, datumCasTransakce.toLocalDate(), datumCasTransakce.toLocalTime(), rezim);
    }

    /**
     * Označení verze QR EET.
     *
     * @return verze
     */
    public static String getVersion() {
        return VERSION;
    }

    /**
     * 16 znaků z množiny [A-F0-9] FIK kód, prvních 16 znaků (3 skupiny) bez mezer či pomlček.
     *
     * @return FIK
     */
    public String getFik() {
        if (fik == null && bkp == null) {
            throw new IllegalArgumentException("FIK musí být předán");
        }
        return fik;
    }

    public void setFik(@Nullable String fik) {
        this.fik = fik;
    }

    /**
     * 16 znaků z množiny [A-F0-9] BKP kód, prvních 16 znaků (2 skupiny) bez mezer či pomlček.
     *
     * @return BKP
     */
    public String getBkp() {
        if (fik == null && bkp == null) {
            throw new IllegalArgumentException("BKP musí být předán");
        }
        return bkp;
    }

    public void setBkp(@Nullable String bkp) {
        this.bkp = bkp;
    }

    /**
     * 8-10 číslic DIČ, bez předpony "CZ".
     *
     * @return DIČ
     */
    public String getDic() {
        if (dic == null) {
            throw new IllegalArgumentException("DIČ musí být předán");
        } else {
            if (dic.startsWith("CZ")) {
                return dic.substring(2, dic.length());
            }
            return dic;
        }
    }

    public void setDic(@NotNull String dic) {
        this.dic = dic;
    }

    /**
     * 1-10 znaků z množiny [0-9.] cena na účtence v Kč. Desetinné číslo, max. 2 desetinné cifry, Tečka jako oddělovač desetinných míst.
     * Maximální možná hodnota je 9 999 999.99
     *
     * @return částka
     */
    private String getKc() {
        if (castka <= 0) {
            throw new IllegalArgumentException("Částka musí být větší než nula");
        }
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat CASTKA_FORMAT = new DecimalFormat("###,###,###.00", symbols);
        return CASTKA_FORMAT.format(castka);
    }

    public void setCastka(double castka) {
        this.castka = castka;
    }

    /**
     * 12 číslic datum a čas tržby ve formátu {@link #DATUM_CAS_TRANSAKCE_FORMAT}, formát ISO 8601.
     *
     * @return datum a čas tržby
     */
    private String getDt() {
        if (datumTransakce == null) {
            throw new IllegalArgumentException("Datum transakce musí být předán");
        } else if (casTransakce == null) {
            throw new IllegalArgumentException("Čas transakce musí být předán");
        } else {
            LocalDateTime dt = LocalDateTime.of(datumTransakce, casTransakce);
            return DATUM_CAS_TRANSAKCE_FORMAT.format(dt);
        }
    }

    public void setDatumTransakce(@NotNull LocalDate datumTransakce) {
        this.datumTransakce = datumTransakce;
    }

    public void setCasTransakce(@NotNull LocalTime casTransakce) {
        this.casTransakce = casTransakce;
    }

    /**
     * 1 symbol B nebo Z Režim tržby [B]ěžný nebo [Z]jednodušený.
     *
     * @return režim
     */
    private String getR() {
        if (rezim == null) {
            throw new IllegalArgumentException("Režim musí být předán");
        }
        return rezim.name();
    }

    public void setRezim(@NotNull Rezim rezim) {
        this.rezim = rezim;
    }

    @Override
    public String toString() {
        return generateString();
    }

    @Override
    public Schema parseSchema(String code) {
        if (code != null && code.toUpperCase().startsWith("EET*" + EetUctenka.getVersion())) {
            Map<String, String> parameters = SchemeUtil.getParameters(code.toUpperCase(), "\\*");
            if (parameters.containsKey(FIK)) {
                this.setFik(parameters.get(FIK));
            }

            if (parameters.containsKey(BKP)) {
                this.setBkp(parameters.get(BKP));
            }

            if (parameters.containsKey(DIC)) {
                this.setDic(parameters.get(DIC));
            }

            if (parameters.containsKey(KC)) {
                String s = parameters.get(KC);
                this.setCastka(Double.parseDouble(s));
            }

            if (parameters.containsKey(DT)) {
                String s = parameters.get(DT);
                LocalDateTime datumCasTransakce = LocalDateTime.parse(s, DATUM_CAS_TRANSAKCE_FORMAT);
                this.setDatumTransakce(datumCasTransakce.toLocalDate());
                this.setCasTransakce(datumCasTransakce.toLocalTime());
            }

            if (parameters.containsKey(R)) {
                String name = parameters.get(R);
                this.setRezim(Rezim.valueOf(name));
            }

            return this;
        } else {
            throw new IllegalArgumentException("this is not a valid EET účtenka code: " + code);
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
        Map<String, Object> parameters = new LinkedHashMap<>();

        String fik = getFik();
        if (fik != null) {
            parameters.put(FIK, fik);
        }

        String bkp = getBkp();
        if (bkp != null) {
            parameters.put(BKP, bkp);
        }

        parameters.put(DIC, getDic());
        parameters.put(KC, getKc());
        parameters.put(DT, getDt());
        parameters.put(R, getR());

        final StringBuilder sb = new StringBuilder("EET*");
        sb.append(EetUctenka.getVersion());
        for (Map.Entry<String, Object> entry: parameters.entrySet()) {
            sb.append("*")
                    .append(entry.getKey())
                    .append(":")
                    .append(entry.getValue());
        }
        return sb.toString();
    }

}
