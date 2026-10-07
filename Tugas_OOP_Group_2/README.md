# Tugas Kelompok Pertemuan 4 — Sistem Manajemen E-Commerce (Kotlin)

Implementasi 4 pilar OOP (Enkapsulasi, Pewarisan, Polimorfisme, Abstraksi) pada sistem toko online sederhana.

Struktur Proyek

```
src/
├── Main.kt                      # Fungsi main() demo seluruh fitur
├── product/                     # Product (abstract), Electronic/Clothing/FoodProduct
├── cart/ShoppingCart.kt
├── order/                       # Order, OrderStatus (sealed class)
├── payment/                     # PaymentMethod (interface) + CreditCard, QRIS, BankTransfer
├── user/User.kt
├── system/ECommerceSystem.kt
└── util/CurrencyFormatter.kt    # Format Rupiah (menggantikan 4x kode duplikat)
docs/
├── Laporan_Tugas_OOP.md         # Laporan sesuai template (konversi ke PDF sebelum dikumpulkan)
└── output_demo.txt              # Output lengkap program
```