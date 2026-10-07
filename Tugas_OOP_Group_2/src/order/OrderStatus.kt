package order

/**
 * Status pesanan yang jumlah variannya terbatas (sealed class).
 *
 * Karena sealed, `when` pada [OrderStatus] bisa exhaustive tanpa `else`.
 */
sealed class OrderStatus {

    /** Pesanan dibuat, menunggu pembayaran. */
    data object Pending : OrderStatus() {
        override fun display() = "⏳ Menunggu Pembayaran"
    }

    /** Pesanan sudah dibayar. */
    data object Paid : OrderStatus() {
        override fun display() = "✅ Dibayar"
    }

    /** Pesanan sedang dikirim. */
    data object Shipped : OrderStatus() {
        override fun display() = "🚚 Dikirim"
    }

    /** Pesanan sudah diterima pelanggan. */
    data object Delivered : OrderStatus() {
        override fun display() = "📦 Diterima"
    }

    /**
     * Pesanan dibatalkan.
     *
     * @property reason Alasan pembatalan
     */
    data class Cancelled(val reason: String) : OrderStatus() {
        override fun display() = "❌ Dibatalkan: $reason"
    }

    /** @return Teks status untuk ditampilkan ke pengguna */
    abstract fun display(): String

    /** @return `true` jika status sudah final ([Delivered] atau [Cancelled]) dan tidak boleh diubah */
    fun isFinal(): Boolean = this is Delivered || this is Cancelled
}
