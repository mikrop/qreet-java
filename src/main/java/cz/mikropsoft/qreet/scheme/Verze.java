package cz.mikropsoft.qreet.scheme;

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
     * Číselná reprezentace {@link Verze}.
     *
     * @param kod fiskální identifikační kód (FIK), nebo bezpečnostní kód poplatníka (BKP)
     * @param dic DIČ poplatníka
     * @return verze
     */
    private static Integer ofTypAndDic(Kod kod, String dic) {
        StringBuilder sb = new StringBuilder();

        Kod.Typ typ = kod.getTyp();
        if (typ == null) {
            throw new IllegalArgumentException("Attribut kód (FIK, nebo BKP) musí být předán");
        } else {
            sb.append(typ.getValue());
        }

        if (dic == null) {
            sb.append("1");
        } else {
            int length = dic.length();
            if (length == 8) {
                sb.append("2");
            } else if (length == 9) {
                sb.append("3");
            } else if (length == 10) {
                sb.append("4");
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
        return Verze.ofTypAndDic(uctenka.getKod(), uctenka.getDic());
    }

}
