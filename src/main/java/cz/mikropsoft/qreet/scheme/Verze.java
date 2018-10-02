package cz.mikropsoft.qreet.scheme;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

/**
 * Dvě číslice, každá nabývající hodnoty 1-9, číselník ovlivňující formát výsledného kódu, přípustné hodnoty:
 *
 * První číslice
 * <ul>
 *     <li>1 - atribut KÓDY obsahuje FIK</li>
 *     <li>2 - atribut KÓDY obsahuje BKP</li>
 *     <li>3-9 - rezervováno pro budoucí rozšíření</li>
 *     <li>0 - nepřípustná hodnota</li>
 * </ul>
 *
 * Druhá číslice
 * <ul>
 *      <li>1 - atribut DIČ je prázdný</li>
 *      <li>2 - atribut DIČ má délku 8 číslic</li>
 *      <li>3 - atribut DIČ má délku 9 číslic</li>
 *      <li>4 - atribut DIČ má délku 10 číslic</li>
 *      <li>5-9 - rezervováno pro budoucí použití</li>
 *      <li>0 - nepřípustná hodnota</li>
 * </ul>
 *
 * @author Michal Hájek, <a href="mailto:mikrop@centrum.cz">mikrop@centrum.cz</a>
 * @since 30.09.2018
 */
public class Verze {

    /**
     * Builder pro jednodušší sestavení čísla verze;
     */
    public static class Builder {

        private Kod.Typ typ;
        private String dic;

        public Builder(@NotNull Kod.Typ typ, @Nullable String dic) {
            this.typ = typ;
            this.dic = dic;
        }

        public Integer build() {

            String verze;

            if (typ == null) {
                throw new IllegalArgumentException("Attribut kód (FIK, nebo BKP) musí být předán");
            } else {
                verze = typ.getValue();
            }

            if (dic == null) {
                verze += "1";
            } else {
                int length = dic.length();
                if (length == 8) {
                    verze += "2";
                } else if (length == 9) {
                    verze += "3";
                } else if (length == 10) {
                    verze += "4";
                } else {
                    throw new IllegalArgumentException("Attribut DIČ nebyl přenán, nebo má neplatnou délku.");
                }
            }
            return Integer.valueOf(verze);
        }
    }

}
