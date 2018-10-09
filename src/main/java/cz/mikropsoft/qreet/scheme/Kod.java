package cz.mikropsoft.qreet.scheme;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Atribut kód.
 *
 * @author Michal Hájek, <a href="mailto:mikrop@centrum.cz">mikrop@centrum.cz</a>
 * @since 01.10.2018
 */
public class Kod {

    private static final char KOD_SEPARATOR = '-';

    /** 8-4-4-4-12-2 */
    private static final Pattern FIK_PATTERN = Pattern.compile("^([0-9A-F]{8})-([0-9A-F]{4})-([0-9A-F]{4})(-[0-9A-F]{4}-[0-9A-F]{12}-[0-9A-F]{2})?$");
    private static final Pattern DECODE_FIK_PATTERN = Pattern.compile("^(\\d{10})(\\d{5})(\\d{5})$");

    /** 8-8-8-8-8 */
    private static final Pattern BKP_PATTERN = Pattern.compile("^([0-9A-F]{8})-([0-9A-F]{8})(-[0-9A-F]{8}-[0-9A-F]{8}-[0-9A-F]{8})?$");
    private static final Pattern DECODE_BKP_PATTERN = Pattern.compile("^(\\d{10})(\\d{10})$");

    /**
     * První číslice
     * <ul>
     *     <li>1 - atribut KÓDY obsahuje FIK</li>
     *     <li>2 - atribut KÓDY obsahuje BKP</li>
     *     <li>3-9 - rezervováno pro budoucí rozšíření</li>
     *     <li>0 - nepřípustná hodnota</li>
     * </ul>
     */
    public enum Typ {
        FIK("1"),
        BKP("2")
        ;

        private final String value;

        private Typ(String value) {
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
         * {@link Typ} na základě předaného value.
         *
         * @param value hodnota
         * @return {@link Typ}
         */
        public static Typ parse(String value) {
            for (Typ typ : Typ.values()) {
                if (typ.getValue().equals(value)) {
                    return typ;
                }
            }
            return null;
        }
    };

    private String value;
    private Kod.Typ typ;

    /**
     * Privátní konstruktor.
     *
     * @param value celkem tedy 20 číslic
     * @param typ {@link Kod.Typ}
     */
    private Kod(String value, Kod.Typ typ) {

        if (value == null) {
            throw new IllegalArgumentException("Kód musí být předán");
        }

        this.value = value.toUpperCase();
        this.typ = typ;
    }

    /**
     * Statická factory.
     *
     * @param fik fiskální identifikační kód
     * @param bkp bezpečnostní kód poplatníka
     * @return {@link Kod}
     */
    public static Kod create(String fik, String bkp) {
        if (fik == null && bkp == null) {
            throw new IllegalArgumentException("Kód musí být předán");
        } else {
            if (fik != null) {
                return Kod.ofFik(fik);
            } else {
                return Kod.ofBkp(bkp);
            }
        }
    }

//    /**
//     * Factory k vytvoření kódu.
//     *
//     * @param kod
//     * @param typ
//     * @return
//     */
//    public static Kod create(String kod, Kod.Typ typ) {
//
//        if (kod == null) {
//            throw new IllegalArgumentException("Kód musí být předán");
//        }
//        if (typ == null) {
//            throw new IllegalArgumentException("Typ musí být předán");
//        }
//
//        switch (typ) {
//            case FIK:
//                return Kod.ofFik(kod);
//            case BKP:
//                return Kod.ofBkp(kod);
//            default:
//                throw new IllegalStateException("Nepodporovaný typ kódu: " + typ);
//        }
//    }

    /**
     * Pro registraci účtenky stačí první tři skupiny po 8 a 4 hexadecimálních číslicích. Jednotlivé skupiny jsou
     * převedeny do dekadické soustavy a doplněny zleva nulami na celkový počet 10, 5 a 5 číslic, celkem tedy 20 číslic.
     *
     * @param fik fiskální identifikační kód
     * @return {@link Kod}
     */
    public static Kod ofFik(String fik) {
        return new Kod(fik, Typ.FIK);
    }

    /**
     * Pro registraci účtenky stačí první dvě skupiny po 8 hexadecimálních číslicích. Jednotlivé skupiny jsou
     * převedeny do dekadické soustavy a doplněny zleva nulami na celkový počet 10 a 10 číslic, celkem tedy 20 číslic.
     *
     * @param bkp bezpečnostní kód poplatníka
     * @return {@link Kod}
     */
    public static Kod ofBkp(String bkp) {
        return new Kod(bkp, Typ.BKP);
    }

    /**
     * Výčtová hodnota {@link Typ}.
     *
     * @return typ kódu
     */
    public Typ getTyp() {
        if (typ == null) {
            throw new IllegalArgumentException("Typ kódu je null");
        }
        return typ;
    }

    /**
     * Kód v dekadické soustavě (20 číslic).
     *
     * @return 20 číslic dekadické soustavy a doplněny zleva nulami
     */
    public String encode() {
        switch (getTyp()) {
            case FIK:
                return encodeFik();
            case BKP:
                return encodeBkp();
            default:
                throw new IllegalStateException("Nepodporovaný typ kódu: " + typ);
        }
    }

    /**
     * Předanou hexadecimální číslici tranformuje do dekadické soustavy a doplní ji nulami na celkový počet n číslic.
     *
     * @param s zdroj k transformaci
     * @param n maximální počet znaků, na které bude text doplněn
     * @return výsledek transformace
     */
    private static String encodeLeftPad(String s, int n) {
        long l = Long.parseLong(s.trim(), 16);
        return String.format("%1$" + n + "s", l).replace(' ', '0');
    }

    /**
     * @see #encodeLeftPad(String, int)
     */
    private static String encodeLeftPad10(String s) {
        return encodeLeftPad(s, 10);
    }

    /**
     * První tři skupiny po 8 a 4 hexadecimálních číslicích jsou převedeny do dekadické soustavy a doplněny
     * zleva nulami na celkový počet 10, 5 a 5 číslic, celkem tedy 20 číslic.
     *
     * @return první tři skupiny
     */
    private String encodeFik() {

        if (value == null) {
            throw new IllegalArgumentException("Kód FIK musí být předán.");
        }

        Matcher matcher = FIK_PATTERN.matcher(value.toUpperCase());
        if (matcher.matches()) {

            StringBuilder sb = new StringBuilder();
            sb.append(Kod.encodeLeftPad10(matcher.group(1)));
            sb.append(Kod.encodeLeftPad(matcher.group(2), 5));
            sb.append(Kod.encodeLeftPad(matcher.group(3), 5));
            return sb.toString();
        }
        throw new IllegalArgumentException("Předaný FIK: " + value + ", neodpovídá vzoru \"xxxxxxxx-xxxx-xxxx\"");
    }

    /**
     * První dvě skupiny po 8 hexadecimálních číslicích jsou převedeny do dekadické soustavy a doplněny
     * zleva nulami na celkový počet 10 a 10 číslic, celkem tedy 20 číslic.
     *
     * @return první dvě skupiny
     */
    private String encodeBkp() {

        if (value == null) {
            throw new IllegalArgumentException("Kód BKP musí být předán.");
        }

        Matcher matcher = BKP_PATTERN.matcher(value.toUpperCase());
        if (matcher.matches()) {

            StringBuilder sb = new StringBuilder();
            sb.append(Kod.encodeLeftPad10(matcher.group(1)));
            sb.append(Kod.encodeLeftPad10(matcher.group(2)));
            return sb.toString();
        }
        throw new IllegalArgumentException("Předaný BKP: " + value + ", neodpovídá vzoru \"xxxxxxxx-xxxxxxxx\"");
    }

    /**
     * Dekóduje předaný kód do hexadecimální číslice.
     *
     * @param s zdroj k transformaci
     * @return výsledek transformace
     */
    private static String decodeLeftPad(final String s) {

        if (s == null) {
            throw new IllegalArgumentException("Kód musí být předán.");
        } else if (5 == s.length() || 10 == s.length()) {

            long l = Long.valueOf(s);
            return Long.toHexString(l);
        }
        throw new IllegalArgumentException("Délka neodpovídá znakům.");
    }

    /**
     * Dekóduje předaný FIK, do hexadecimálního tvaru.
     *
     * @param value
     * @return dekódovaný FIK
     */
    public static Kod decodeFik(String value) {

        if (value == null || value.length() != 20) {
            throw new IllegalArgumentException("Délka FIK neodpovídá dvaceti znakům.");
        }

        Matcher matcher = DECODE_FIK_PATTERN.matcher(value);
        if (matcher.matches()) {

            StringBuilder sb = new StringBuilder();
            sb.append(Kod.decodeLeftPad(matcher.group(1)));
            sb.append(KOD_SEPARATOR);
            sb.append(Kod.decodeLeftPad(matcher.group(2)));
            sb.append(KOD_SEPARATOR);
            sb.append(Kod.decodeLeftPad(matcher.group(3)));
            return Kod.ofFik(sb.toString());
        }
        throw new IllegalArgumentException("Předaný FIK: " + value + ", neodpovídá vzoru \"xxxxxxxx-xxxx-xxxx\"");
    }

    /**
     * Dekóduje předaný BKP, do hexadecimálního tvaru.
     *
     * @param value
     * @return dekódovaný BKP
     */
    public static Kod decodeBkp(String value) {

        if (value == null || value.length() != 20) {
            throw new IllegalArgumentException("Délka BKP neodpovídá dvaceti znakům.");
        }

        Matcher matcher = DECODE_BKP_PATTERN.matcher(value);
        if (matcher.matches()) {

            StringBuilder sb = new StringBuilder();
            sb.append(Kod.decodeLeftPad(matcher.group(1)));
            sb.append(KOD_SEPARATOR);
            sb.append(Kod.decodeLeftPad(matcher.group(2)));
            return Kod.ofBkp(sb.toString());
        }
        throw new IllegalArgumentException("Předaný BKP: " + value + ", neodpovídá vzoru \"xxxxxxxx-xxxxxxxx\"");
    }

    @Override
    public String toString() {
        return "Kod{" +
                "value='" + value + '\'' +
                ", typ=" + typ +
                '}';
    }

}
