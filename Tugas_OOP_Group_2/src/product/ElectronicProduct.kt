package product

/**
 * Produk kategori elektronik.
 *
 * Aturan diskon: 5% dasar, +10% jika premium, +5% jika garansi lebih dari 24 bulan.
 *
 * @property brand Merek produk
 * @property warrantyMonths Durasi garansi dalam bulan
 * @property isPremium Produk premium mendapat diskon ekstra
 */
class ElectronicProduct(
    id: String,
    name: String,
    price: Double,
    stock: Int,
    val brand: String,
    val warrantyMonths: Int,
    val isPremium: Boolean
) : Product(id, name, price, stock) {

    /** @return Diskon per unit untuk produk elektronik */
    override fun calculateDiscount(): Double {
        var discount = 0.0
        discount += price * 0.05
        if (isPremium) discount += price * 0.10
        if (warrantyMonths > 24) discount += price * 0.05
        return discount
    }

    /** @return "Elektronik" */
    override fun getCategory(): String = "Elektronik"

    /** Menampilkan info dasar ditambah merek, garansi, dan status premium. */
    override fun displayInfo() {
        super.displayInfo()
        println("Merek     : $brand")
        println("Garansi   : $warrantyMonths bulan")
        println("Premium   : ${if (isPremium) "✅ Ya" else "❌ Tidak"}")
        println("=".repeat(50))
    }
}
