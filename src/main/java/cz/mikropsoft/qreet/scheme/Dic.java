package cz.mikropsoft.qreet.scheme;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DIČ poplatníka - tato položka není povinná.
 *
 * @author Michal Hájek, <a href="mailto:mikrop@centrum.cz">mikrop@centrum.cz</a>
 * @since 09.03.2019
 */
public class Dic implements QrEet {

    // Vzor pro DIČ
    private static final Pattern DIC_PATTERN = Pattern.compile("^(CZ)?(\\d{8,10})$");

    /**
     * Druhá číslice z attributu {@link Verze}.
     * <ul>
     *      <li>1 - atribut DIČ je prázdný</li>
     *      <li>2 - atribut DIČ má délku 8 číslic</li>
     *      <li>3 - atribut DIČ má délku 9 číslic</li>
     *      <li>4 - atribut DIČ má délku 10 číslic</li>
     *      <li>5-9 - rezervováno pro budoucí použití</li>
     *      <li>0 - nepřípustná hodnota</li>
     * </ul>
     */
    public enum Verze implements QrEet {
        PRAZDNY("1"),
        OSM_CISLIC("2"),
        DEVET_CISLIC("3"),
        DESET_CISLIC("4")
        ;

        private final String value;

        private Verze(String value) {
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
         * @see Verze#parse(char)
         */
        private static Verze parse(int value) {
            String s = Integer.toString(value);
            for (Verze verze : Verze.values()) {
                if (verze.qrValue().equals(s)) {
                    return verze;
                }
            }
            return null;
        }

        /**
         * Vrací {@link Verze} na základě předaného value.
         *
         * @param codePoint dekadická číslice
         * @return {@link Verze}, nebo {@code null}
         */
        public static Verze parse(char codePoint) {
            int value = Character.getNumericValue(codePoint);
            return Verze.parse(value);
        }
    }

    private String value;
    private Verze verze;

    /**
     * Privátní konstruktor.
     *
     * @param value hodnota DIČ
     * @param verze {@link Dic.Verze}
     */
    private Dic(String value, Dic.Verze verze) {
        this.value = value;
        this.verze = verze;
    }

    /**
     * Vrací {@link Dic} na základě předaného value.
     *
     * @param dic DIČ poplatníka - tato položka není povinná
     * @return {@link Dic}
     */
    public static Dic parse(String dic) {

        if (dic == null) {
            return new Dic(null, Verze.PRAZDNY);
        } else {

            Matcher matcher = DIC_PATTERN.matcher(dic.toUpperCase());
            if (matcher.matches()) {

                String value = matcher.group(2);
                int length = value.length();
                if (length == 8) {
                    return new Dic(value, Verze.OSM_CISLIC);
                } else if (length == 9) {
                    return new Dic(value, Verze.DEVET_CISLIC);
                } else if (length == 10) {
                    return new Dic(value, Verze.DESET_CISLIC);
                }
            }
            throw new IllegalArgumentException("Předaný DIČ neodpovídá vzoru \"CZ[0-9]{8,10}\"");
        }
    }

    /**
     * DIČ poplatníka.
     *
     * @return dekadická číslice
     */
    public String qrValue() {
        return value;
    }

    /**
     * Výčtová hodnota {@link Verze}.
     *
     * @return verze DIČ poplatníka
     */
    public Verze getVerze() {
        if (verze == null) {
            throw new IllegalArgumentException("Verze je null");
        }
        return verze;
    }

    /**
     * DIČ is empty.
     *
     * @return vrací {@code true} pokud je DIČ prázdný
     */
    public boolean isNotEmpty() {
        return value != null && value.length() != 0;
    }

    @Override
    public String toString() {
        return "Dic{" +
                "value='" + value + '\'' +
                ", verze=" + verze +
                '}';
    }
}
