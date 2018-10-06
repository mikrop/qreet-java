package cz.mikropsoft.qreet.scheme;

/**
 * Dvě číslice, každá nabývající hodnoty 1-9, číselník ovlivňující formát výsledného kódu, přípustné hodnoty:
 *
 * @author Michal Hájek, <a href="mailto:mikrop@centrum.cz">mikrop@centrum.cz</a>
 * @since 30.09.2018
 */
public class Verze {

    /**
     * Druhá číslice
     * <ul>
     *      <li>1 - atribut DIČ je prázdný</li>
     *      <li>2 - atribut DIČ má délku 8 číslic</li>
     *      <li>3 - atribut DIČ má délku 9 číslic</li>
     *      <li>4 - atribut DIČ má délku 10 číslic</li>
     *      <li>5-9 - rezervováno pro budoucí použití</li>
     *      <li>0 - nepřípustná hodnota</li>
     * </ul>
     */
    public enum Dic {
        PRAZDNY("1"),
        OSM_CISLIC("2"),
        DEVET_CISLIC("3"),
        DESET_CISLIC("4")
        ;

        private final String value;

        private Dic(String value) {
            this.value = value;
        }

        /**
         * Dekadická číslice s přípustnými hodnotami 0 a 1, kde 0 reprezentuje běžný režim a 1 reprezentuje
         * zjednodušený režim.
         *
         * @return dekadická číslice
         */
        public String getValue() {
            return value;
        }

        /**
         * {@link Dic} na základě předaného value.
         *
         * @param value hodnota
         * @return {@link Dic}
         */
        public static Dic parse(String value) {
            for (Dic dic : Dic.values()) {
                if (dic.getValue().equals(value)) {
                    return dic;
                }
            }
            return null;
        }
    };

    private Kod.Typ typ;
    private Dic dic;

    /**
     * Privátní konstruktor.
     *
     * @param typ {@link Kod.Typ}
     * @param dic {@link Dic}
     */
    private Verze(Kod.Typ typ, Dic dic) {
        this.typ = typ;
        this.dic = dic;
    }

    /**
     * Číselná reprezentace {@link Verze}.
     *
     * @param kod fiskální identifikační kód (FIK), nebo bezpečnostní kód poplatníka (BKP)
     * @param dic DIČ poplatníka
     * @return verze
     */
    private static Integer ofKodAndDic(Kod kod, String dic) {

        if (kod == null) {
            throw new IllegalArgumentException("Kód musí být předán");
        }

        StringBuilder sb = new StringBuilder();
        Kod.Typ typ = kod.getTyp();
        if (typ == null) {
            throw new IllegalArgumentException("Typ kódu musí být předán");
        } else {
            sb.append(typ.getValue());
        }

        if (dic == null) {
            sb.append(Dic.PRAZDNY.getValue());
        } else {
            int length = dic.length();
            if (length == 8) {
                sb.append(Dic.OSM_CISLIC.getValue());
            } else if (length == 9) {
                sb.append(Dic.DESET_CISLIC.getValue());
            } else if (length == 10) {
                sb.append(Dic.DESET_CISLIC.getValue());
            } else {
                throw new IllegalArgumentException("Attribut DIČ nebyl přenán, nebo má neplatnou délku.");
            }
        }
        return Integer.valueOf(sb.toString());
    }

    /**
     * Číselná reprezentace {@link Verze}.
     *
     * @param uctenka zdrojová účtenka
     * @return verze
     */
    public static Integer ofUctenka(EetUctenka uctenka) {
        return Verze.ofKodAndDic(uctenka.getKod(), uctenka.getDic());
    }

    /**
     * {@link Verze} na základě předaného value.
     *
     * @param value dvě čísla
     * @return {@link Verze}
     */
    public static Verze parse(String value) {

        if (value == null) {
            throw new IllegalArgumentException("Číslo verze nebylo předáno.");
        } else if (value.length() == 2) {

            Kod.Typ typ = Kod.Typ.parse("" + value.charAt(0));
            Dic dic = Dic.parse("" + value.charAt(1));
            return new Verze(typ, dic);

        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Vrací typ z této verze.
     *
     * @return {@link Kod.Typ}
     */
    public Kod.Typ getTyp() {
        if (typ == null) {
            throw new IllegalArgumentException("Typ kódu je null");
        }
        return typ;
    }

}
