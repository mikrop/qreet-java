package cz.mikropsoft.qreet.scheme;

/**
 * Režim tržby [B]ěžný nebo [Z]jednodušený.
 * U Běžného režimu tržby je pro úspěšné zpracování účtenky třeba poskytnout buď prvních 16 znaků z FIK kódu nebo prvních 16 znaků z BKP kódu.
 * U Zjednodušeného režimu je třeba poskytnout prvních 16 znaků z BKP kódu. Pokud není uveden klíč R pro režim tržby, předpokládá se Běžný režim tržby.
 *
 * @author Michal Hájek, <a href="mailto:mikrop@centrum.cz">mikrop@centrum.cz</a>
 * @since 30.09.2018
 */
public enum Rezim {
    B("Běžný"),
    Z("Zjednodušený")
    ;

    private final String caption;

    private Rezim(String caption) {
        this.caption = caption;
    }

    /**
     * Čitelný popisek.
     *
     * @return český popisek
     */
    public String getCaption() {
        return caption;
    }

}
