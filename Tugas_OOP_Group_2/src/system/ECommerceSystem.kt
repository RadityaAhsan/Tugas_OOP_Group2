package system

import order.Order
import order.OrderStatus
import payment.PaymentMethod
import product.Product
import user.User
import util.CurrencyFormatter

/**
 * Controller utama sistem e-commerce: mengelola produk, user, dan order.
 *
 * @property name Nama toko
 */
class ECommerceSystem(val name: String = "Toko Online") {

    private val products = mutableListOf<Product>()
    private val users = mutableListOf<User>()
    private val orders = mutableListOf<Order>()

    // ============================================================
    // MANAJEMEN PRODUK
    // ============================================================

    /**
     * Menambahkan produk. Menolak ID yang sudah terdaftar.
     *
     * @param product Produk baru (subclass apa pun dari [Product])
     */
    fun addProduct(product: Product) {
        if (products.any { it.id == product.id }) {
            println("❌ Produk dengan ID ${product.id} sudah ada")
            return
        }
        products.add(product)
        println("✅ Produk ${product.name} ditambahkan")
    }

    /**
     * Mencari produk berdasarkan nama atau ID (tidak case-sensitive).
     *
     * @param keyword Kata kunci
     */
    fun searchProduct(keyword: String): List<Product> = products.filter {
        it.name.lowercase().contains(keyword.lowercase()) ||
            it.id.lowercase().contains(keyword.lowercase())
    }

    /** @return Produk dengan ID tertentu, atau `null` */
    fun findProductById(id: String): Product? = products.find { it.id == id }

    /** @return Salinan read-only seluruh produk */
    fun getProducts(): List<Product> = products.toList()

    /** Menampilkan seluruh produk (pemanggilan `displayInfo()` bersifat polimorfik). */
    fun displayAllProducts() {
        println("=".repeat(55))
        println("📦 SEMUA PRODUK")
        println("=".repeat(55))
        println("Total: ${products.size} produk")
        println("-".repeat(55))
        products.forEach { it.displayInfo() }
    }

    // ============================================================
    // MANAJEMEN USER
    // ============================================================

    /**
     * Mendaftarkan user baru.
     *
     * @return `false` jika username sudah dipakai
     */
    fun registerUser(username: String, email: String, password: String): Boolean {
        if (users.any { it.username == username }) {
            println("❌ Username $username sudah digunakan")
            return false
        }
        users.add(User(username, email, password))
        println("✅ User $username berhasil didaftarkan")
        return true
    }

    /** @return User dengan username tertentu, atau `null` */
    fun findUser(username: String): User? = users.find { it.username == username }

    // ============================================================
    // MANAJEMEN ORDER
    // ============================================================

    /** Mencatat order ke sistem. */
    fun addOrder(order: Order) {
        orders.add(order)
    }

    /**
     * Checkout atas nama user, lalu mencatat order ke laporan penjualan sistem.
     *
     * @param username Username pembeli
     * @param paymentMethod Metode pembayaran
     * @return Order jika berhasil, `null` jika gagal
     */
    fun checkout(username: String, paymentMethod: PaymentMethod): Order? {
        val user = findUser(username)
        if (user == null) {
            println("❌ User $username tidak ditemukan")
            return null
        }
        val order = user.checkout(paymentMethod) ?: return null
        addOrder(order)
        return order
    }

    /** @return Total pendapatan dari order yang tidak dibatalkan */
    fun getTotalRevenue(): Double = orders
        .filter { it.status !is OrderStatus.Cancelled }
        .sumOf { it.totalPrice }

    /** @return Jumlah order di sistem */
    fun getOrderCount(): Int = orders.size

    /** Menampilkan laporan penjualan. */
    fun displaySalesReport() {
        println("=".repeat(55))
        println("📊 LAPORAN PENJUALAN - $name")
        println("=".repeat(55))
        println("Total Order   : ${getOrderCount()}")
        println("Total Revenue : Rp ${CurrencyFormatter.format(getTotalRevenue())}")
        println("-".repeat(55))

        if (orders.isEmpty()) {
            println("   Belum ada penjualan")
        } else {
            orders.forEachIndexed { index, order ->
                println("${index + 1}. ${order.id} - ${order.customerName}")
                println("   Status: ${order.status.display()}")
                println("   Total: Rp ${CurrencyFormatter.format(order.totalPrice)}")
            }
        }
        println("=".repeat(55))
    }
}
