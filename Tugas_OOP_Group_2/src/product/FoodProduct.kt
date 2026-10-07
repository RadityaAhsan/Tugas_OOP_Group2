package product

/**
 * Produk kategori makanan.
 *
 * Aturan diskon: 5% dasar, +20% jika organik.
 *
 * @property expiryDate Tanggal kadaluarsa (format bebas, contoh "2027-03-01")
 * @property weight Berat dalam gram
 * @property isOrganic Makanan organik mendapat diskon ekstra
 */
class FoodProduct(
    id: String,
    name: String,
    price: Double,
    stock: Int,
    val expiryDate: String,
    val weight: Double,
    val isOrganic: Boolean
) : Product(id, name, price, stock) {

    /** @return Diskon per unit untuk produk makanan */
    override fun calculateDiscount(): Double {
        var discount = 0.0
        discount += price * 0.05
        if (isOrganic) discount += price * 0.20
        return discount
    }

    /** @return "Makanan" */
    override fun getCategory(): String = "Makanan"

    /** Menampilkan info dasar ditambah berat, kadaluarsa, dan status organik. */
    override fun displayInfo() {
        super.displayInfo()
        println("Berat       : $weight gram")
        println("Kadaluarsa  : $expiryDate")
        println("Organik     : ${if (isOrganic) "✅ Ya" else "❌ Tidak"}")
        println("=".repeat(50))
    }
}
