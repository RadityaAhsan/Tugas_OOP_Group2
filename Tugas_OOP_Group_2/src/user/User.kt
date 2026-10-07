package user

import cart.ShoppingCart
import order.Order
import order.OrderStatus
import payment.PaymentMethod
import payment.PaymentResult
import util.CurrencyFormatter

/**
 * Pengguna (pembeli) di sistem e-commerce.
 *
 * @property username Nama pengguna unik
 * @property email Email pengguna
 * @param password Kata sandi. **Private** dan tidak punya getter: hanya bisa diverifikasi lewat [authenticate].
 */
class User(
    val username: String,
    val email: String,
    private val password: String
) {
    private val orders = mutableListOf<Order>()
    private val cart = ShoppingCart(username)

    /** Jumlah order yang pernah dibuat. */
    val orderCount: Int
        get() = orders.size

    /** Total uang yang sudah dibelanjakan. */
    val totalSpent: Double
        get() = orders.sumOf { it.totalPrice }

    /**
     * Memverifikasi kata sandi.
     *
     * @param inputPassword Kata sandi yang dimasukkan
     * @return `true` jika cocok
     */
    fun authenticate(inputPassword: String): Boolean = password == inputPassword

    /** @return Keranjang milik user ini */
    fun getCart(): ShoppingCart = cart

    /** @return Salinan read-only daftar order user */
    fun getOrders(): List<Order> = orders.toList()

    /**
     * Checkout isi keranjang dengan metode pembayaran tertentu.
     *
     * - [PaymentResult.Success]: order dibuat berstatus Paid, keranjang dikosongkan.
     * - [PaymentResult.Failed]: keranjang dipertahankan agar user bisa mencoba metode lain.
     * - [PaymentResult.Pending]: stok dikembalikan dan keranjang dikosongkan.
     *
     * @param paymentMethod Metode pembayaran
     * @return [Order] jika berhasil, `null` jika gagal
     */
    fun checkout(paymentMethod: PaymentMethod): Order? {
        if (cart.isEmpty()) {
            println("❌ Keranjang kosong")
            return null
        }

        val amount = cart.getTotalPrice()
        println("💳 Memproses pembayaran dengan ${paymentMethod.name}...")
        println("   Biaya admin: Rp ${CurrencyFormatter.format(paymentMethod.getFee(amount))}")

        // when sebagai expression pada sealed class => wajib exhaustive, tanpa else
        return when (val result = paymentMethod.processPayment(amount)) {
            is PaymentResult.Success -> {
                println("✅ Pembayaran berhasil! ID: ${result.transactionId}")
                val order = createOrder()
                order.updateStatus(OrderStatus.Paid)
                orders.add(order)
                println("✅ Order ${order.id} berhasil dibuat")
                order
            }
            is PaymentResult.Failed -> {
                println("❌ Pembayaran gagal: ${result.reason} (kode ${result.errorCode})")
                null
            }
            PaymentResult.Pending -> {
                println("⏳ Pembayaran pending, stok dikembalikan...")
                cart.releaseAll()
                null
            }
        }
    }

    /** Membuat order dari isi keranjang lalu mengosongkan keranjang. */
    private fun createOrder(): Order {
        val order = Order(Order.generateId(), username, cart.getItems())
        cart.clearAfterCheckout()
        return order
    }

    /** Menampilkan riwayat order. */
    fun displayOrders() {
        println("=".repeat(50))
        println("📋 RIWAYAT ORDER - $username")
        println("=".repeat(50))

        if (orders.isEmpty()) {
            println("   Belum ada order")
        } else {
            orders.forEachIndexed { index, order ->
                println("${index + 1}. Order ${order.id} - ${order.status.display()}")
                println("   Total: Rp ${CurrencyFormatter.format(order.totalPrice)}")
            }
        }
        println("Total Belanja: Rp ${CurrencyFormatter.format(totalSpent)}")
        println("=".repeat(50))
    }
}
