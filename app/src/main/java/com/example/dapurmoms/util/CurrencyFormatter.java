package com.example.dapurmoms.util;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utilitas untuk memformat angka ke format mata uang Rupiah Indonesia.
 * Menggunakan format lokal Indonesia (id_ID) dengan pemisah ribuan titik.
 *
 * <p>Contoh output:
 * <ul>
 *   <li>{@code formatRupiah(1234567)} → "Rp 1.234.567"</li>
 *   <li>{@code formatRupiahWithSign(1234567)} → "+Rp 1.234.567"</li>
 *   <li>{@code formatRupiahWithSign(-500000)} → "-Rp 500.000"</li>
 * </ul>
 */
public final class CurrencyFormatter {

    /** Locale Indonesia untuk format angka */
    private static final Locale LOCALE_ID = new Locale("id", "ID");

    /** NumberFormat instance untuk format Rupiah */
    private static final NumberFormat numberFormat = NumberFormat.getNumberInstance(LOCALE_ID);

    // Mencegah instantiasi
    private CurrencyFormatter() {
        throw new UnsupportedOperationException("Utility class tidak boleh diinstansiasi");
    }

    /**
     * Memformat jumlah uang ke format Rupiah Indonesia.
     * Contoh: 1234567 → "Rp 1.234.567"
     *
     * @param amount jumlah uang dalam Rupiah
     * @return string terformat dalam format Rupiah
     */
    public static String formatRupiah(long amount) {
        return "Rp " + numberFormat.format(amount);
    }

    /**
     * Memformat jumlah uang ke format Rupiah dengan tanda positif atau negatif.
     * Contoh:
     * <ul>
     *   <li>1234567 → "+Rp 1.234.567"</li>
     *   <li>-500000 → "-Rp 500.000"</li>
     *   <li>0 → "Rp 0"</li>
     * </ul>
     *
     * @param amount jumlah uang dalam Rupiah (bisa positif atau negatif)
     * @return string terformat dalam format Rupiah dengan tanda
     */
    public static String formatRupiahWithSign(long amount) {
        if (amount > 0) {
            return "+Rp " + numberFormat.format(amount);
        } else if (amount < 0) {
            return "-Rp " + numberFormat.format(Math.abs(amount));
        } else {
            return "Rp 0";
        }
    }
}
