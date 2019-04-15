# qreet-java

[![Build Status](https://travis-ci.org/mikrop/qreet-java.svg?branch=master)](https://travis-ci.org/mikrop/qreet-java)
[![](https://jitpack.io/v/mikrop/qreet-java.svg)](https://jitpack.io/#mikrop/qreet-java)

## QR EET JAVA

JAVA implementace QR kódů pro EET účtenky dle [oficiální specifikace](http://www.etrzby.cz/assets/cs/prilohy/Specifikace-QR-kodu.pdf) pro snadné zpracování údajů na účtenkách registrovaných v systému EET.

## Jak uložit QR kód účtenky na disk
```
EetUctenka uctenka = EetUctenka.ofBkp(
        "6455B192-D697186A-6AB1971A-1E9B146B-CDD5007B", // Kód BKP
        null, // Nepovinný parametr DIČ
        34113.00d, // Částka
        new Date(), // Datum a čas
        Rezim.BEZNY // Režim tržby
);
QRCode.from(uctenka).writeTo(new FileOutputStream("C:/tmp/QRBKP.jpg")); 
```
