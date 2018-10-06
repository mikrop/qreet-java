package cz.mikropsoft.qreet.scheme;

/**
 * Režim tržby.
 *
 * @author Michal Hájek, <a href="mailto:mikrop@centrum.cz">mikrop@centrum.cz</a>
 * @since 30.09.2018
 */
public enum Rezim {
    BEZNY("0"),
    ZJEDNODUSENY("1")
    ;

    private final String value;

    private Rezim(String value) {
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
     * {@link Rezim} na základě předaného value.
     *
     * @param value hodnota
     * @return {@link Rezim}
     */
    public static Rezim parse(String value) {
        for (Rezim rezim : Rezim.values()) {
            if (rezim.getValue().equals(value)) {
                return rezim;
            }
        }
        return null;
    }

}
