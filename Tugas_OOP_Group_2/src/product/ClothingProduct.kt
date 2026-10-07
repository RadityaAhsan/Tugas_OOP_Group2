package product

/**
 * Produk kategori pakaian.
 *
 * Aturan diskon: 10% dasar, +15% jika seasonal, +5% untuk ukuran XL/XXL/XXXL.
 *
 * @property size Ukuran pakaian (S, M, L, XL, ...)
 * @property material Bahan pakaian
 * @property isSeasonal Pakaian musiman mendapat diskon khusus
 */
class ClothingProduct(
    id: String,
    name: String,
    price: Double,
    stock: Int,
    val size: String,
    val material: String,
    val isSeasonal: Boolean
) : Product(id, name, price, stock) {

    /** @return Diskon per unit untuk produk pakaian */
    override fun calculateDiscount(): Double {
        var discount = 0.0
        discount += price * 0.10
        if (isSeasonal) discount += price * 0.15
        if (size.uppercase() in listOf("XL", "XXL", "XXXL")) discount += price * 0.05
        return discount
    }

    /** @return "Pakaian" */
    override fun getCategory(): String = "Pakaian"

    /** Menampilkan info dasar ditambah ukuran, bahan, dan status seasonal. */
    override fun displayInfo() {
        super.displayInfo()
        println("Ukuran    : $size")
        println("Bahan     : $material")
        println("Seasonal  : ${if (isSeasonal) "✅ Ya" else "❌ Tidak"}")
        println("=".repeat(50))
    }
}
