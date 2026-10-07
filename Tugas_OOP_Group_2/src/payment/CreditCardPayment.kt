package payment

/**
 * Pembayaran dengan kartu kredit (fee 2%).
 *
 * Data kartu bersifat private (enkapsulasi).
 */
class CreditCardPayment(
    private val cardNumber: String,
    private val expiryDate: String,
    private val cvv: String
) : PaymentMethod {
    override val name = "Kartu Kredit"

    /** @return 2% dari [amount] */
    override fun getFee(amount: Double): Double = amount * 0.02

    /** Memvalidasi nomor kartu (16 digit) dan CVV (3 digit). */
    override fun processPayment(amount: Double): PaymentResult {
        if (cardNumber.length < 16) return PaymentResult.Failed("Nomor kartu tidak valid", 401)
        if (cvv.length != 3) return PaymentResult.Failed("CVV tidak valid", 402)
        return PaymentResult.Success("CC-${System.currentTimeMillis()}")
    }
}
