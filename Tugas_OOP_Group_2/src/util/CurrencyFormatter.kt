package util

/**
 * Utilitas untuk memformat nilai mata uang Rupiah.
 *
 * Sebelumnya fungsi format ini diduplikasi di beberapa kelas; dengan objek ini
 * logikanya cukup ditulis satu kali (prinsip DRY).
 */
object CurrencyFormatter {

    /**
     * Memformat angka menjadi string dengan pemisah ribuan titik.
     *
     * Contoh: `15000000.0` menjadi `"15.000.000"`.
     *
     * @param nominal Nilai dalam Rupiah (bagian desimal dibuang)
     * @return String bernominal dengan pemisah ribuan
     */
    fun format(nominal: Double): String {
        val str = nominal.toLong().toString()
        val builder = StringBuilder()
        var count = 0
        for (i in str.length - 1 downTo 0) {
            builder.insert(0, str[i])
            count++
            if (count % 3 == 0 && i > 0 && str[i - 1] != '-') {
                builder.insert(0, ".")
            }
        }
        return builder.toString()
    }
}
