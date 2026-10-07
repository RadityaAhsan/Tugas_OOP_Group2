package cart

import product.Product
import util.CurrencyFormatter

/**
 * Keranjang belanja milik satu pengguna.
 *
 * Saat produk dimasukkan ke keranjang, stok langsung dikurangi (reservasi stok).
 * Saat produk dihapus, stok dikembalikan.
 *
 * @property owner Username pemilik keranjang
 */
class ShoppingCart(private val owner: String) {

    /** Isi keranjang: Produk -> Jumlah. Private agar hanya bisa diubah lewat metode di kelas ini. */
    private val items = mutableMapOf<Product, Int>()

    /** Total jumlah unit di keranjang. Setter private. */
    var totalItems: Int = 0
        private set

    /**
     * Menambahkan produk ke keranjang.
     *
     * @param product Produk yang ditambahkan
     * @param quantity Jumlah (harus > 0 dan tidak melebihi stok)
     * @return `true` jika berhasil
     */
    fun addItem(product: Product, quantity: Int): Boolean {
        if (quantity <= 0) {
            println("❌ Jumlah harus lebih dari 0")
            return false
        }
        if (!product.reduceStock(quantity)) {
            println("❌ Stok ${product.name} tidak mencukupi (tersedia: ${product.stock})")
            return false
        }
        items[product] = items.getOrDefault(product, 0) + quantity
        totalItems += quantity
        println("✅ ${product.name} x$quantity ditambahkan ke keranjang")
        return true
    }

    /**
     * Menghapus satu jenis produk dari keranjang dan mengembalikan stoknya.
     *
     * @param product Produk yang dihapus
     * @return `true` jika produk ada di keranjang
     */
    fun removeItem(product: Product): Boolean {
        val quantity = items[product] ?: return false
        items.remove(product)
        totalItems -= quantity
        product.restoreStock(quantity)
        println("✅ ${product.name} dihapus dari keranjang")
        return true
    }

    /** @return Total harga setelah diskon untuk seluruh isi keranjang */
    fun getTotalPrice(): Double = items.entries.sumOf { (product, qty) ->
        product.getDiscountedPrice() * qty
    }

    /** @return Total diskon untuk seluruh isi keranjang */
    fun getTotalDiscount(): Double = items.entries.sumOf { (product, qty) ->
        product.calculateDiscount() * qty
    }

    /** @return Salinan read-only isi keranjang (enkapsulasi: map asli tidak bocor keluar) */
    fun getItems(): Map<Product, Int> = items.toMap()

    /** @return `true` jika keranjang kosong */
    fun isEmpty(): Boolean = items.isEmpty()

    /**
     * Mengosongkan keranjang setelah checkout berhasil.
     * Stok **tidak** dikembalikan karena barang sudah terjual.
     */
    internal fun clearAfterCheckout() {
        items.clear()
        totalItems = 0
    }

    /**
     * Mengosongkan keranjang dan mengembalikan seluruh stok
     * (dipakai saat pembayaran dibatalkan / pending).
     */
    internal fun releaseAll() {
        items.forEach { (product, qty) -> product.restoreStock(qty) }
        items.clear()
        totalItems = 0
    }

    /** Menampilkan isi keranjang beserta total diskon dan total belanja. */
    fun displayCart() {
        println("=".repeat(50))
        println("🛒 KERANJANG BELANJA - $owner")
        println("=".repeat(50))

        if (items.isEmpty()) {
            println("   Keranjang kosong")
        } else {
            items.forEach { (product, quantity) ->
                println("${product.name} x$quantity = Rp ${CurrencyFormatter.format(product.getDiscountedPrice() * quantity)}")
                println("   (Diskon: Rp ${CurrencyFormatter.format(product.calculateDiscount() * quantity)})")
            }
            println("-".repeat(50))
            println("Total Diskon : Rp ${CurrencyFormatter.format(getTotalDiscount())}")
            println("Total Belanja: Rp ${CurrencyFormatter.format(getTotalPrice())}")
        }
        println("=".repeat(50))
    }
}
