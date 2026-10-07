package payment

/**
 * Pembayaran dengan transfer bank (fee 1%, minimal Rp 5.000).
 */
class BankTransferPayment(
    private val bankName: String,
    private val accountNumber: String
) : PaymentMethod {
    override val name = "Transfer Bank ($bankName)"

    /** @return 1% dari [amount], minimal Rp 5.000 */
    override fun getFee(amount: Double): Double = maxOf(amount * 0.01, 5000.0)

    /** Memvalidasi nomor rekening (minimal 8 digit). */
    override fun processPayment(amount: Double): PaymentResult {
        if (accountNumber.length < 8) return PaymentResult.Failed("Nomor rekening tidak valid", 404)
        return PaymentResult.Success("BT-${System.currentTimeMillis()}")
    }
}
