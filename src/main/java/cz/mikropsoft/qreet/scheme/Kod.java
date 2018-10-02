package cz.mikropsoft.qreet.scheme;

import cz.mikropsoft.qreet.utils.StringUtils;

/**
 * Atribut kód.
 *
 * @author Michal Hájek, <a href="mailto:mikrop@centrum.cz">mikrop@centrum.cz</a>
 * @since 01.10.2018
 */
public class Kod {

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
        this.value = value;
        this.typ = typ;
    }

    /**
     * Celkem tedy 20 číslic.
     *
     * @return 20 číslic dekadické soustavy a doplněny zleva nulami
     */
    public String getValue() {
        return value;
    }

    public Typ getTyp() {
        return typ;
    }

    /**
     * Pro registraci účtenky stačí první tři skupiny po 8 a 4 hexadecimálních číslicích. Jednotlivé skupiny jsou
     * převedeny do dekadické soustavy a doplněny zleva nulami na celkový počet 10, 5 a 5 číslic, celkem tedy 20 číslic.
     *
     * @param fik fiskální identifikační kód
     * @return {@link Kod}
     */
    public static Kod encodeFik(String fik) {
        String value = StringUtils.encodeFik(fik);
        return new Kod(value, Typ.FIK);
    }

    /**
     * Pro registraci účtenky stačí první dvě skupiny po 8 hexadecimálních číslicích. Jednotlivé skupiny jsou
     * převedeny do dekadické soustavy a doplněny zleva nulami na celkový počet 10 a 10 číslic, celkem tedy 20 číslic.
     *
     * @param bkp bezpečnostní kód poplatníka
     * @return {@link Kod}
     */
    public static Kod encodeBkp(String bkp) {
        String value = StringUtils.encodeBkp(bkp);
        return new Kod(value, Typ.BKP);
    }

    @Override
    public String toString() {
        return "Kod{" +
                "value='" + value + '\'' +
                ", typ=" + typ +
                '}';
    }

}
