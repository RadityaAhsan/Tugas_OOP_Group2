package product

import util.CurrencyFormatter

/**
 * Kelas induk abstrak untuk semua produk di sistem e-commerce.
 *
 * Kelas ini menerapkan **abstraksi** (metode abstrak diskon & kategori) dan
 * **enkapsulasi** (harga disimpan private, stok hanya diubah lewat metode).
 *
 * @property id ID unik produk
 * @property name Nama produk
 * @property stock Jumlah stok saat ini (hanya bisa diubah lewat [reduceStock] / [restoreStock])
 */
abstract class Product(
    val id: String,
    val name: String,
    price: Double,
    stock: Int
) {
    // ============================================================
    // PROPERTI
    // ============================================================

    /** Penyimpan harga asli. Private: tidak bisa diakses dari luar kelas. */
    private var _price: Double = price

    /**
     * Harga dasar produk. Bisa dibaca oleh subclass (protected) untuk
     * menghitung diskon, tetapi tidak terlihat dari luar hierarki kelas.
     */
    protected val price: Double
        get() = _price

    /** Stok produk. Setter private agar tidak bisa dimanipulasi sembarangan. */
    var stock: Int = stock
        private set

    /** Harga dengan format Rupiah, contoh: `Rp 15.000.000`. */
    val formattedPrice: String
        get() = "Rp ${formatRupiah(_price)}"

    init {
        require(price >= 0) { "Harga tidak boleh negatif" }
        require(stock >= 0) { "Stok tidak boleh negatif" }
    }

    // ============================================================
    // METODE ABSTRAK
    // ============================================================

    /**
     * Menghitung diskon yang berlaku untuk produk.
     *
     * Aturan diskon berbeda untuk tiap kategori sehingga wajib
     * diimplementasikan oleh subclass (polimorfisme).
     *
     * @return Jumlah diskon dalam Rupiah (per unit)
     */
    abstract fun calculateDiscount(): Double

    /**
     * Mendapatkan nama kategori produk.
     *
     * @return Nama kategori, contoh: "Elektronik"
     */
    abstract fun getCategory(): String

    // ============================================================
    // METODE CONCRETE
    // ============================================================

    /**
     * Menghitung harga setelah diskon.
     *
     * @return Harga akhir per unit dalam Rupiah
     */
    open fun getDiscountedPrice(): Double {
        return _price - calculateDiscount()
    }

    /** Menampilkan informasi produk ke layar. Dapat di-override subclass. */
    open fun displayInfo() {
        println("=".repeat(50))
        println("📦 ${getCategory()} - $name")
        println("ID          : $id")
        println("Harga       : $formattedPrice")
        println("Diskon      : Rp ${formatRupiah(calculateDiscount())}")
        println("Harga Akhir : Rp ${formatRupiah(getDiscountedPrice())}")
        println("Stok        : $stock")
        println("=".repeat(50))
    }

    /**
     * Mengurangi stok produk.
     *
     * @param quantity Jumlah yang dikurangi (harus > 0)
     * @return `true` jika stok cukup dan berhasil dikurangi, `false` jika gagal
     */
    fun reduceStock(quantity: Int): Boolean {
        if (quantity <= 0 || stock < quantity) return false
        stock -= quantity
        return true
    }

    /**
     * Mengembalikan stok (misalnya saat item dihapus dari keranjang).
     *
     * @param quantity Jumlah yang dikembalikan (harus > 0)
     */
    fun restoreStock(quantity: Int) {
        if (quantity > 0) stock += quantity
    }

    /**
     * Mengubah harga produk dengan validasi.
     *
     * @param newPrice Harga baru (tidak boleh negatif)
     * @return `true` jika harga berhasil diubah
     */
    fun updatePrice(newPrice: Double): Boolean {
        if (newPrice < 0) return false
        _price = newPrice
        return true
    }

    /** Dua produk dianggap sama jika ID-nya sama (penting karena dipakai sebagai key Map). */
    override fun equals(other: Any?): Boolean = other is Product && other.id == id

    override fun hashCode(): Int = id.hashCode()

    // ============================================================
    // HELPER METHOD (Protected)
    // ============================================================

    /**
     * Memformat nominal menjadi string Rupiah dengan pemisah ribuan.
     *
     * @param nominal Nilai yang diformat
     */
    protected fun formatRupiah(nominal: Double): String = CurrencyFormatter.format(nominal)
}
