import order.OrderStatus
import payment.BankTransferPayment
import payment.CreditCardPayment
import payment.PaymentResult
import payment.QRISPayment
import product.ClothingProduct
import product.ElectronicProduct
import product.FoodProduct
import product.Product
import system.ECommerceSystem
import util.CurrencyFormatter

/** Mencetak judul bagian demo. */
private fun section(title: String) = println("\n--- $title ---")

/** Entry point: mendemonstrasikan seluruh fitur dan 4 pilar OOP. */
fun main() {
    // 1. Inisialisasi sistem
    val system = ECommerceSystem("Toko Online Kampus")
    println("=".repeat(55))
    println("🛍️ SELAMAT DATANG DI ${system.name.uppercase()}")
    println("=".repeat(55))

    // 2. Tambah produk (2 per kategori)
    section("MENAMBAHKAN PRODUK")
    val laptop = ElectronicProduct("E001", "Laptop Gaming", 15_000_000.0, 10, "ASUS", 36, true)
    val earbuds = ElectronicProduct("E002", "Earbuds Basic", 250_000.0, 25, "Xiaomi", 12, false)
    val jaket = ClothingProduct("C001", "Jaket Musim Dingin", 400_000.0, 20, "XL", "Wol", true)
    val kaos = ClothingProduct("C002", "Kaos Polos", 80_000.0, 50, "M", "Katun", false)
    val beras = FoodProduct("F001", "Beras Organik 5kg", 90_000.0, 30, "2027-03-01", 5000.0, true)
    val mie = FoodProduct("F002", "Mie Instan", 3_500.0, 200, "2027-01-15", 80.0, false)
    listOf(laptop, earbuds, jaket, kaos, beras, mie).forEach { system.addProduct(it) }
    system.addProduct(mie) // coba tambah ID duplikat -> ditolak

    // 3. Register user (minimal 2)
    section("REGISTRASI USER")
    system.registerUser("budi", "budi@kampus.ac.id", "rahasia123")
    system.registerUser("siti", "siti@kampus.ac.id", "pass456")
    system.registerUser("budi", "lain@kampus.ac.id", "x") // duplikat -> ditolak

    // 4. Tampilkan semua produk
    println("")
    system.displayAllProducts()

    // 5-8. User 1 (budi): belanja, keranjang, checkout kartu kredit, tampilkan order
    val budi = system.findUser("budi")!!
    section("BUDI BELANJA")
    budi.getCart().addItem(laptop, 1)
    budi.getCart().addItem(jaket, 2)
    budi.getCart().addItem(mie, 0)       // jumlah tidak valid
    budi.getCart().addItem(earbuds, 999) // stok tidak cukup

    section("KERANJANG BUDI")
    budi.getCart().displayCart()

    section("CHECKOUT BUDI (percobaan 1: CVV salah)")
    system.checkout("budi", CreditCardPayment("1234567812345678", "12/28", "12"))

    section("CHECKOUT BUDI (percobaan 2: kartu benar)")
    val orderBudi = system.checkout("budi", CreditCardPayment("1234567812345678", "12/28", "123"))
    orderBudi?.displayOrder()

    section("ORDER BUDI")
    budi.displayOrders()

    // 9. User 2 (siti): hal yang sama dengan QRIS, lalu transfer bank
    val siti = system.findUser("siti")!!
    section("SITI BELANJA")
    siti.getCart().addItem(beras, 3)
    siti.getCart().addItem(kaos, 2)
    siti.getCart().addItem(mie, 10)
    siti.getCart().removeItem(kaos) // hapus: stok kaos dikembalikan
    siti.getCart().displayCart()

    section("CHECKOUT SITI (QRIS)")
    val orderSiti = system.checkout("siti", QRISPayment("QR-0123456789", "MERCHANT-01"))
    orderSiti?.displayOrder()

    section("SITI BELANJA LAGI (Transfer Bank)")
    siti.getCart().addItem(earbuds, 1)
    val orderSiti2 = system.checkout("siti", BankTransferPayment("BCA", "1234567890"))

    // Update status order (sealed class)
    section("UPDATE STATUS ORDER")
    orderBudi?.updateStatus(OrderStatus.Shipped)
    orderBudi?.updateStatus(OrderStatus.Delivered)
    orderBudi?.updateStatus(OrderStatus.Cancelled("coba ubah")) // ditolak: sudah final
    orderSiti2?.updateStatus(OrderStatus.Cancelled("Pelanggan berubah pikiran"))

    // 10. Laporan penjualan
    println("")
    system.displaySalesReport()

    // 11. Polimorfisme
    section("DEMO POLIMORFISME")
    val products: List<Product> = system.getProducts() // List<Product> berisi 3 subclass berbeda
    products.forEach { p ->
        // calculateDiscount() dipanggil lewat referensi Product, perilaku mengikuti tipe aslinya
        println("${p.getCategory().padEnd(10)} - ${p.name.padEnd(20)} diskon Rp ${CurrencyFormatter.format(p.calculateDiscount())}")
    }
    println()
    products.forEach { p ->
        // when + is (smart cast otomatis di setiap cabang)
        val detail = when (p) {
            is ElectronicProduct -> "Merek ${p.brand}, garansi ${p.warrantyMonths} bln"
            is ClothingProduct -> "Ukuran ${p.size}, bahan ${p.material}"
            is FoodProduct -> "Berat ${p.weight} g, kadaluarsa ${p.expiryDate}"
            else -> "Produk umum"
        }
        println("${p.name}: $detail")
    }

    // 12. Sealed class: when exhaustive tanpa else
    section("DEMO SEALED CLASS")
    val statuses = listOf(
        OrderStatus.Pending, OrderStatus.Paid, OrderStatus.Shipped,
        OrderStatus.Delivered, OrderStatus.Cancelled("Stok habis")
    )
    statuses.forEach { s ->
        val aksi = when (s) { // exhaustive: kompiler memastikan semua varian tertangani
            OrderStatus.Pending -> "Tunggu pembayaran pelanggan"
            OrderStatus.Paid -> "Siapkan barang untuk dikirim"
            OrderStatus.Shipped -> "Pantau pengiriman"
            OrderStatus.Delivered -> "Selesai"
            is OrderStatus.Cancelled -> "Alasan batal: ${s.reason}"
        }
        println("${s.display().padEnd(32)} final=${s.isFinal().toString().padEnd(5)} -> $aksi")
    }
    val hasil: List<PaymentResult> = listOf(
        PaymentResult.Success("TX-1"), PaymentResult.Failed("Saldo kurang", 500), PaymentResult.Pending
    )
    hasil.forEach {
        println(when (it) {
            is PaymentResult.Success -> "Sukses, id=${it.transactionId}"
            is PaymentResult.Failed -> "Gagal: ${it.reason} (${it.errorCode})"
            PaymentResult.Pending -> "Pending"
        })
    }

    // 13. Enkapsulasi
    section("DEMO ENKAPSULASI")
    println("Harga laptop (lewat getter publik): ${laptop.formattedPrice}")
    println("Password budi benar? ${budi.authenticate("rahasia123")}")
    println("Password budi salah? ${budi.authenticate("tebakan")}")
    println("Validasi harga negatif ditolak? ${!laptop.updatePrice(-1.0)}")

    // 14. Smart casting & safe cast
    section("DEMO SMART CASTING")
    val unknown: Product = system.findProductById("E001")!!
    if (unknown is ElectronicProduct) {
        println("Smart cast otomatis: brand = ${unknown.brand}") // tanpa cast eksplisit
    }
    val asFood: FoodProduct? = unknown as? FoodProduct // safe cast: null, tidak melempar exception
    println("E001 sebagai FoodProduct? $asFood")
    val asElectronic = unknown as? ElectronicProduct
    println("E001 sebagai ElectronicProduct? ${asElectronic?.name} (premium=${asElectronic?.isPremium})")

    println("\n" + "=".repeat(55))
    println("✅ DEMO SELESAI")
    println("=".repeat(55))
}