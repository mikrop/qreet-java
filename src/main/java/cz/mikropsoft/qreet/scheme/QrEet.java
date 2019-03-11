package cz.mikropsoft.qreet.scheme;

/**
 * Attribut specifikace QR kódu pro účely účtenkové loterie.
 *
 * @author Michal Hájek, <a href="mailto:mikrop@centrum.cz">mikrop@centrum.cz</a>
 * @since 09.03.2019
 */
public interface QrEet extends java.io.Serializable {

    /**
     * Zakódovaná informaci QR kódu.
     *
     * @return hodnota
     */
    String qrValue();

}
