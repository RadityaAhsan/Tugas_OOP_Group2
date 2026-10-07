package order

import product.Product
import util.CurrencyFormatter

/**
 * Pesanan hasil checkout.
 *
 * @property id ID unik pesanan
 * @property customerName Username pelanggan
 * @property items Daftar produk dan jumlahnya (salinan dari keranjang)
 * @property status Status pesanan saat ini; hanya bisa diubah lewat [updateStatus]
 */
class Order(
    val id: String,
    val customerName: String,
    items: Map<Product, Int>,
    status: OrderStatus = OrderStatus.Pending
) {
    /** Salinan items supaya perubahan keranjang setelah checkout tidak memengaruhi pesanan. */
    val items: Map<Product, Int> = items.toMap()

    /** Status pesanan; setter private demi menjaga aturan transisi status. */
    var status: OrderStatus = status
        private set

    /** Total harga setelah diskon. */
    val totalPrice: Double
        get() = items.entries.sumOf { (product, qty) -> product.getDiscountedPrice() * qty }

    /** Total diskon yang didapat. */
    val totalDiscount: Double
        get() = items.entries.sumOf { (product, qty) -> product.calculateDiscount() * qty }

    /** Waktu pesanan dibuat. */
    val createdAt: String = java.time.LocalDateTime.now().withNano(0).toString()

    /**
     * Mengubah status pesanan.
     *
     * @param newStatus Status baru
     * @return `false` jika status saat ini sudah final
     */
    fun updateStatus(newStatus: OrderStatus): Boolean {
        if (status.isFinal()) {
            println("❌ Status sudah final, tidak bisa diubah")
            return false
        }
        status = newStatus
        println("✅ Status order $id diubah menjadi: ${status.display()}")
        return true
    }

    /** Menampilkan detail pesanan. */
    fun displayOrder() {
        println("=".repeat(55))
        println("📋 DETAIL ORDER")
        println("=".repeat(55))
        println("ID Order    : $id")
        println("Pelanggan   : $customerName")
        println("Tanggal     : $createdAt")
        println("Status      : ${status.display()}")
        println("-".repeat(55))
        println("Items:")
        items.forEach { (product, quantity) ->
            println("   ${product.name} x$quantity = Rp ${CurrencyFormatter.format(product.getDiscountedPrice() * quantity)}")
        }
        println("-".repeat(55))
        println("Total Diskon: Rp ${CurrencyFormatter.format(totalDiscount)}")
        println("Total Harga : Rp ${CurrencyFormatter.format(totalPrice)}")
        println("=".repeat(55))
    }

    companion object {
        private var sequence = 0

        /**
         * Membuat ID order unik berbasis waktu + nomor urut
         * (nomor urut mencegah ID kembar bila dua order dibuat di milidetik yang sama).
         */
        fun generateId(): String = "ORD-${System.currentTimeMillis()}-${++sequence}"
    }
}
