package cz.mikropsoft.qreet.scheme;

import cz.mikropsoft.qreet.utils.StringUtils;
import net.glxn.qrgen.core.scheme.Schema;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * EET účtenka.
 */
public class EetUctenka extends Schema {

    private static final DateTimeFormatter DATUM_CAS_TRANSAKCE_FORMAT = DateTimeFormatter.ofPattern("uuMMddHHmm");

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

    public EetUctenka(String fik, String bkp, String dic, double castka, LocalDate datumTransakce,
                      LocalTime casTransakce, Rezim rezim) {

        this.rezim = rezim;
        this.datumTransakce = datumTransakce;
        this.casTransakce = casTransakce;
        this.dic = StringUtils.parseDic(dic);

        if (fik == null && bkp == null) {
            throw new IllegalArgumentException("FIK, nebo BKP musí být předán");
        } else {
            if (fik != null) {
                this.kod = Kod.encodeFik(fik);
            } else if (bkp != null) {
                this.kod = Kod.encodeBkp(bkp);
            } else {
                throw new IllegalArgumentException("");
            }
        }
        this.castka = castka;
    }

    public EetUctenka(String fik, String bkp, String dic, double castka,
                      LocalDateTime datumCasTransakce, Rezim rezim) {
        this(fik, bkp, dic, castka, datumCasTransakce.toLocalDate(), datumCasTransakce.toLocalTime(), rezim);
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
     * 16 znaků z množiny [A-F0-9] FIK kód, prvních 16 znaků (3 skupiny) bez mezer či pomlček.
     *
     * @return FIK
     */
    public Kod getKod() {
        return kod;
    }

    public void setKod(Kod kod) {
        this.kod = kod;
    }

    /**
     * 8-10 číslic DIČ.
     *
     * @return DIČ
     */
    public String getDic() {
        return dic;
    }

    public void setDic(String dic) {
        this.dic = StringUtils.parseDic(dic);
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

    public void setCastka(double castka) {
        this.castka = castka;
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

    public void setDatumTransakce(LocalDate datumTransakce) {
        this.datumTransakce = datumTransakce;
    }

    public void setCasTransakce(LocalTime casTransakce) {
        this.casTransakce = casTransakce;
    }

    /**
     * 1 symbol B nebo Z Režim tržby [B]ěžný nebo [Z]jednodušený.
     *
     * @return režim
     */
    private String getRezim() {
        if (rezim == null) {
            throw new IllegalArgumentException("Režim musí být předán");
        }
        return rezim.getValue();
    }

    public void setRezim(Rezim rezim) {
        this.rezim = rezim;
    }

    @Override
    public String toString() {
        return generateString();
    }

    @Override
    public Schema parseSchema(String code) {
        if (code != null) {
            throw new NotImplementedException();
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
        sb.append(getKod().getValue());
        sb.append(getCastka());
        return sb.toString();
    }

}
