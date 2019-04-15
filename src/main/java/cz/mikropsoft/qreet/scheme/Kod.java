package cz.mikropsoft.qreet.scheme;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Kódy FIK nebo BKP, zakódované jako dekadické číslice.
 *
 * @author Michal Hájek, <a href="mailto:mikrop@centrum.cz">mikrop@centrum.cz</a>
 * @since 09.03.2019
 */
public class Kod implements QrEet {

    private static final char KOD_SEPARATOR = '-';

    /**
     * Pro registraci účtenky stačí první tři skupiny po 8 a 4 hexadecimálních číslicích. Jednotlivé skupiny jsou
     * převedeny do dekadické soustavy a doplněny zleva nulami na celkový počet 10, 5 a 5 číslic, celkem tedy 20 číslic.
     */
    private static final Pattern FIK_PATTERN = Pattern.compile("^([0-9A-F]{8})-([0-9A-F]{4})-([0-9A-F]{4})(-[0-9A-F]{4}-[0-9A-F]{12}-[0-9A-F]{2})?$");
    private static final Pattern DECODE_FIK_PATTERN = Pattern.compile("^(\\d{10})(\\d{5})(\\d{5})$");

    /**
     * Pro registraci účtenky stačí první dvě skupiny po 8 hexadecimálních číslicích. Jednotlivé skupiny jsou
     * převedeny do dekadické soustavy a doplněny nulami na celkový počet 10 a 10 číslic, celkem tedy 20 číslic.
     */
    private static final Pattern BKP_PATTERN = Pattern.compile("^([0-9A-F]{8})-([0-9A-F]{8})(-[0-9A-F]{8}-[0-9A-F]{8}-[0-9A-F]{8})?$");
    private static final Pattern DECODE_BKP_PATTERN = Pattern.compile("^(\\d{10})(\\d{10})$");

    /**
     * FIK nebo BKP, zakódovaný jako dekadické číslice.
     * <ul>
     *     <li>1 - atribut KÓDY obsahuje FIK</li>
     *     <li>2 - atribut KÓDY obsahuje BKP</li>
     *     <li>3-9 - rezervováno pro budoucí rozšíření</li>
     *     <li>0 - nepřípustná hodnota</li>
     * </ul>
     */
    public enum Typ implements QrEet {
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
        public String qrValue() {
            return value;
        }

        /**
         * @see Kod.Typ#parse(char)
         */
        private static Typ parse(int value) {
            String s = Integer.toString(value);
            for (Typ typ : Typ.values()) {
                if (typ.qrValue().equals(s)) {
                    return typ;
                }
            }
            throw new IllegalArgumentException();
        }

        /**
         * Vrací {@link Typ} na základě předaného value.
         *
         * @param codePoint dekadická číslice
         * @return {@link Typ}
         * @throws IllegalArgumentException {@link Typ} se nepodařilo dle předané číslice určit
         */
        public static Typ parse(char codePoint) {
            int value = Character.getNumericValue(codePoint);
            return Kod.Typ.parse(value);
        }
    }

    private String value;
    private Kod.Typ typ;

    /**
     * Privátní konstruktor.
     *
     * @param value FIK nebo BKP
     * @param typ {@link Kod.Typ}
     */
    private Kod(String value, Kod.Typ typ) {
        this.value = value.toUpperCase();
        this.typ = typ;
    }

    /**
     * Pro registraci účtenky stačí první tři skupiny po 8 a 4 hexadecimálních číslicích. Jednotlivé skupiny jsou
     * převedeny do dekadické soustavy a doplněny zleva nulami na celkový počet 10, 5 a 5 číslic, celkem tedy 20 číslic.
     *
     * @param fik fiskální identifikační kód
     * @return {@link Kod}
     */
    public static Kod ofFik(String fik) {

        if (fik == null) {
            throw new IllegalArgumentException("Kód FIK musí být předán.");
        }

        Matcher matcher = FIK_PATTERN.matcher(fik.toUpperCase());
        if (matcher.matches()) {
            return new Kod(fik, Typ.FIK);
        }
        throw new IllegalArgumentException("Předaný FIK: " + fik + ", neodpovídá vzoru \"xxxxxxxx-xxxx-xxxx\"");
    }

    /**
     * Pro registraci účtenky stačí první dvě skupiny po 8 hexadecimálních číslicích. Jednotlivé skupiny jsou
     * převedeny do dekadické soustavy a doplněny zleva nulami na celkový počet 10 a 10 číslic, celkem tedy 20 číslic.
     *
     * @param bkp bezpečnostní kód poplatníka
     * @return {@link Kod}
     */
    public static Kod ofBkp(String bkp) {

        if (bkp == null) {
            throw new IllegalArgumentException("Kód BKP musí být předán.");
        }

        Matcher matcher = BKP_PATTERN.matcher(bkp.toUpperCase());
        if (matcher.matches()) {
            return new Kod(bkp, Typ.BKP);
        }
        throw new IllegalArgumentException("Předaný BKP: " + bkp + ", neodpovídá vzoru \"xxxxxxxx-xxxxxxxx\"");
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
     * Předanou hexadecimální číslici tranformuje do dekadické soustavy a doplní ji nulami na celkový počet n číslic.
     *
     * @param s hexadecimální číslice k transformaci
     * @return výsledek transformace
     */
    private static String encodeDecimal(final String s) {

        if (s == null) {
            throw new IllegalArgumentException("Hexadecimální číslice musí být předána.");
        }

        int length = s.length();
        if (length != 4 && length != 8) {
            throw new IllegalArgumentException();
        }

        int n = (length + 4) / 5 * 5; // Délka jako násobek pěti
        long l = Long.parseLong(s.trim(), 16);
        return  String.format("%1$" + n + "s", l).replace(' ', '0');
    }

    /**
     * {@link Kod} v dekadické soustavě (20 číslic).
     *
     * @return 20 číslic dekadické soustavy a doplněny zleva nulami
     */
    public String qrValue() {
        StringBuilder sb = new StringBuilder();
        switch (getTyp()) {
            case FIK:

                Matcher fik = FIK_PATTERN.matcher(value);
                if (fik.matches()) {
                    sb.append(Kod.encodeDecimal(fik.group(1)));
                    sb.append(Kod.encodeDecimal(fik.group(2)));
                    sb.append(Kod.encodeDecimal(fik.group(3)));
                }
                return sb.toString();

            case BKP:

                Matcher bkp = BKP_PATTERN.matcher(value);
                if (bkp.matches()) {
                    sb.append(Kod.encodeDecimal(bkp.group(1)));
                    sb.append(Kod.encodeDecimal(bkp.group(2)));
                }
                return sb.toString();

            default:
                throw new IllegalStateException("Nepodporovaný typ kódu: " + typ);
        }
    }

    /**
     * Dekóduje předaný kód do hexadecimální číslice.
     *
     * @param s decimální číslice k transformaci
     * @return výsledek transformace
     */
    private static String decodeDecimal(final String s) {

        if (s == null) {
            throw new IllegalArgumentException("Decimální číslice musí být předána.");
        }

        int length = s.length();
        if (5 == length || 10 == length) {
            long l = Long.valueOf(s);
            return Long.toHexString(l);
        }
        throw new IllegalArgumentException("Délka neodpovídá znakům.");
    }

    /**
     * Vrací {@link Kod} na základě předaného value.
     *
     * @param value FIK
     * @return FIK {@link Kod}
     */
    private static Kod parseFik(String value) {

        if (value == null || value.length() != 20) {
            throw new IllegalArgumentException("Délka FIK neodpovídá dvaceti znakům.");
        }

        Matcher matcher = DECODE_FIK_PATTERN.matcher(value);
        if (matcher.matches()) {

            StringBuilder sb = new StringBuilder();
            sb.append(Kod.decodeDecimal(matcher.group(1)));
            sb.append(KOD_SEPARATOR);
            sb.append(Kod.decodeDecimal(matcher.group(2)));
            sb.append(KOD_SEPARATOR);
            sb.append(Kod.decodeDecimal(matcher.group(3)));
            return Kod.ofFik(sb.toString());
        }
        throw new IllegalArgumentException("Předaný FIK: " + value + ", neodpovídá vzoru \"dddddddddddddddddddd\"");
    }

    /**
     * Vrací {@link Kod} na základě předaného value.
     *
     * @param value BKP
     * @return BKP {@link Kod}
     */
    private static Kod parseBkp(String value) {

        if (value == null || value.length() != 20) {
            throw new IllegalArgumentException("Délka BKP neodpovídá dvaceti znakům.");
        }

        Matcher matcher = DECODE_BKP_PATTERN.matcher(value);
        if (matcher.matches()) {

            StringBuilder sb = new StringBuilder();
            sb.append(Kod.decodeDecimal(matcher.group(1)));
            sb.append(KOD_SEPARATOR);
            sb.append(Kod.decodeDecimal(matcher.group(2)));
            return Kod.ofBkp(sb.toString());
        }
        throw new IllegalArgumentException("Předaný BKP: " + value + ", neodpovídá vzoru \"dddddddddddddddddddd\"");
    }

    /**
     * Vrací {@link Kod} na základě předaného value.
     *
     * @param typ předaného kódu
     * @param value hodnota bezpečnostního kód poplatníka, nebo fiskálního identifikačního kódu
     * @return naplněný {@link Kod}
     */
    public static Kod parse(Kod.Typ typ, String value) {

        if (typ == null) {
            throw new IllegalArgumentException("Nebyl předán typ kódu.");
        }

        switch (typ) {
            case FIK:
                /*
                    2c4ccf70-0055-44f2-804e-3056786dd351-ff
                    07432313440008517650
                 */
                return Kod.parseFik(value);
            case BKP:
                /*
                    6455B192-D697186A-6AB1971A-1E9B146B-CDD5007B
                    16833376183600226410
                 */
                return Kod.parseBkp(value);
            default:
                throw new IllegalStateException("Nepodporovaný typ kódu: " + typ);
        }
    }

    @Override
    public String toString() {
        return "Kod{" +
                "value='" + value + '\'' +
                ", typ=" + typ +
                '}';
    }
}
