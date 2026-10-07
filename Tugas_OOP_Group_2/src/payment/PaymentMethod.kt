package payment

/**
 * Kontrak untuk semua metode pembayaran (abstraksi lewat interface).
 */
interface PaymentMethod {
    /** Nama metode pembayaran. */
    val name: String

    /**
     * Memproses pembayaran.
     *
     * @param amount Nominal yang dibayar
     * @return [PaymentResult] hasil pembayaran
     */
    fun processPayment(amount: Double): PaymentResult

    /**
     * Menghitung biaya administrasi metode pembayaran.
     *
     * @param amount Nominal transaksi
     * @return Biaya dalam Rupiah
     */
    fun getFee(amount: Double): Double
}

/** Hasil pembayaran (sealed class agar `when` exhaustive). */
sealed class PaymentResult {
    /** Pembayaran sukses. @property transactionId ID transaksi */
    data class Success(val transactionId: String) : PaymentResult()

    /** Pembayaran gagal. @property reason Alasan @property errorCode Kode error */
    data class Failed(val reason: String, val errorCode: Int) : PaymentResult()

    /** Pembayaran masih diproses. */
    data object Pending : PaymentResult()
}
