package cz.mikropsoft.qreet.scheme;

/**
 * Dvě číslice, každá nabývající hodnoty 1-9, číselník ovlivňující formát výsledného kódu.
 *
 * @author Michal Hájek, <a href="mailto:mikrop@centrum.cz">mikrop@centrum.cz</a>
 * @since 09.03.2019
 */
public class Verze implements QrEet {

    private Kod.Typ typ;
    private Dic.Verze dic;

    /**
     * Privátní konstruktor.
     *
     * @param typ {@link Kod.Typ}
     * @param dic {@link Dic}
     */
    public Verze(Kod.Typ typ, Dic.Verze dic) {
        this.typ = typ;
        this.dic = dic;
    }

    /**
     * Vrací {@link Verze} na základě předaného value.
     *
     * @param value dvojčíslí
     * @return {@link Verze}
     */
    public static Verze parse(String value) {

        if (value == null) {
            throw new IllegalArgumentException("Číslo verze nebylo předáno.");
        } else if (value.length() == 2) {

            Kod.Typ typ = Kod.Typ.parse(value.charAt(0));
            Dic.Verze dic = Dic.Verze.parse(value.charAt(1));
            return new Verze(typ, dic);

        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Vrací typ kódu.
     *
     * @return {@link Kod.Typ}
     */
    public Kod.Typ getTyp() {
        if (typ == null) {
            throw new IllegalArgumentException("Typ kódu je null");
        }
        return typ;
    }

//    /**
//     * Vrací verzi DIČ poplatníka.
//     *
//     * @return {@link Dic.Verze}
//     */
//    public Dic.Verze getDic() {
//        if (dic == null) {
//            throw new IllegalArgumentException("Verze DIČ poplatníka je null");
//        }
//        return dic;
//    }

    /**
     * {@link Verze} v dekadické soustavě (2 číslice).
     *
     * @return 2 číslice dekadické soustavy
     */
    public String qrValue() {
        return typ.qrValue() + dic.qrValue();
    }

    @Override
    public String toString() {
        return "Verze{" +
                "typ=" + typ +
                ", dic=" + dic +
                '}';
    }
}
