package payment

/**
 * Pembayaran dengan QRIS (fee 0,5%).
 */
class QRISPayment(
    private val qrCode: String,
    private val merchantId: String
) : PaymentMethod {
    override val name = "QRIS"

    /** @return 0,5% dari [amount] */
    override fun getFee(amount: Double): Double = amount * 0.005

    /** Memvalidasi panjang kode QR (minimal 10 karakter). */
    override fun processPayment(amount: Double): PaymentResult {
        if (qrCode.length < 10) return PaymentResult.Failed("Kode QR tidak valid", 403)
        return PaymentResult.Success("QR-${System.currentTimeMillis()}")
    }
}
