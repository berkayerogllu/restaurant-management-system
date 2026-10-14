# Restaurant Management System

Restoran yönetimi için geliştirilmiş full-stack bir uygulama: masa, menü, müşteri, sipariş, rezervasyon yönetimi, mutfak ekranı akışı, raporlama/dashboard, JWT tabanlı kimlik doğrulama ve bunların hepsini kullanan bir React ara yüzü içerir.

## Teknoloji Yığını

**Backend**
- Java 21, Spring Boot 4.1.1
- Spring Data JPA (Hibernate) + PostgreSQL
- Flyway (veritabanı migration'ları)
- Spring Security + JWT (kimlik doğrulama)

**Frontend**
- React 19 + TypeScript + Vite
- React Router, Axios

**Altyapı**
- Docker / Docker Compose (backend + frontend + PostgreSQL, tek komutla ayağa kalkar)

## Özellikler

- **Masa yönetimi**: CRUD, durum takibi (`AVAILABLE`, `OCCUPIED`, `RESERVED`, `CLEANING`), doluluk geçmişi
- **Menü yönetimi**: CRUD, kategori, stok durumu (`available`), filtreleme
- **Müşteri yönetimi**: CRUD, masa tekillik kontrolü
- **Sipariş yönetimi**: kalem bazlı (adet, not, sipariş anı fiyatı), durum akışı (`RECEIVED → IN_PREPARATION → READY → SERVED → PAID`, her aşamadan `CANCELLED`), mutfak kuyruğu, masa bazlı aktif sipariş, telefon numarasına göre sipariş geçmişi
- **Rezervasyon**: zaman çakışma kontrolü, check-in akışı (otomatik müşteri oturtma), bugünün rezervasyonları, no-show tespiti
- **Dashboard/Raporlama**: ciro (günlük/haftalık/aylık), en çok satan ürünler, saatlik yoğunluk, anlık özet (aktif sipariş, mutfak kuyruğu, masa durumları)
- **Kimlik doğrulama**: JWT tabanlı, tüm endpoint'ler login gerektirir (tek tip hesap, rol ayrımı yok)
- **Pagination & sorting**: tüm liste endpoint'lerinde `?page=&size=&sort=`
- **Ara yüz**: sidebar navigasyon, renk kodlu masa kartları (🟢 boş / 🔴 dolu / 🟣 rezerve / 🔵 temizlikte), sipariş tutarları masa kartlarında anlık görünür

## Gereksinimler

- Docker + Docker Compose (önerilen — başka bir şey kurmana gerek yok)

**Veya** lokal çalıştırmak istersen:
- Java 21 (JDK)
- Node.js 20+ ve npm
- PostgreSQL 18 (ya da uyumlu bir sürüm)

---

## Hızlı Başlangıç (Docker ile — önerilen)

Bu yöntem backend, frontend ve PostgreSQL'i tek komutla ayağa kaldırır.

### 1. `.env` dosyasını oluştur

```bash
cp .env.example .env
```

`.env` dosyasını açıp değerleri doldur:

```env
DB_USER=restaurant_admin
DB_PASSWORD=<güçlü-bir-şifre>
DB_NAME=restaurant_db
DB_PORT=5434

JWT_SECRET=<en az 32 karakterlik rastgele bir string>
JWT_EXPIRATION_MS=86400000

STAFF_BOOTSTRAP_USERNAME=admin
STAFF_BOOTSTRAP_PASSWORD=<güçlü bir şifre>

CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173
FRONTEND_PORT=3000
FRONTEND_API_BASE_URL=http://localhost:8082/api
```

> `JWT_SECRET` için rastgele bir değer üretmek istersen: `openssl rand -base64 64`

### 2. Sistemi ayağa kaldır

```bash
docker compose up -d --build
```

Bu komut üç servisi başlatır:

| Servis | Adres | Açıklama |
|---|---|---|
| `postgres` | `localhost:${DB_PORT}` | Veritabanı |
| `app` | `http://localhost:8082` | Backend API |
| `frontend` | `http://localhost:3000` | Ara yüz |

İlk açılışta:
- PostgreSQL container'ı başlar (healthcheck geçene kadar bekler)
- Backend derlenir ve Flyway migration'larını otomatik uygular
- Veritabanı boşsa, `.env`'deki bilgilerle ilk admin hesabı otomatik oluşturulur
- Frontend derlenir ve nginx ile statik olarak servis edilir

### 3. Kullan

Tarayıcıda **`http://localhost:3000`** adresine git, `.env`'deki `STAFF_BOOTSTRAP_USERNAME`/`STAFF_BOOTSTRAP_PASSWORD` ile giriş yap.

### Durdurma

```bash
docker compose down        # container'ları durdurur, veritabanı verisi kalır (named volume)
docker compose down -v     # container'ları durdurur VE veritabanını tamamen siler
```

---

## Lokal Çalıştırma (Docker'sız)

### Backend

1. Yerel bir PostgreSQL instance'ın çalışıyor olmalı (varsayılan: `localhost:5434`).
2. Proje kökünde `.env.properties` dosyası oluştur:

   ```env
   DB_USER=restaurant_admin
   DB_PASSWORD=<şifren>

   JWT_SECRET=<rastgele bir string>
   JWT_EXPIRATION_MS=86400000

   STAFF_BOOTSTRAP_USERNAME=admin
   STAFF_BOOTSTRAP_PASSWORD=<güçlü bir şifre>
   ```

3. Çalıştır:

   ```bash
   ./mvnw spring-boot:run
   ```

   Backend `http://localhost:8082` üzerinde ayağa kalkar.

### Frontend

1. `frontend/` klasörüne geç ve bağımlılıkları kur:

   ```bash
   cd frontend
   npm install
   ```

2. Dev server'ı başlat:

   ```bash
   npm run dev
   ```

   Frontend `http://localhost:5173` üzerinde ayağa kalkar ve backend'e `http://localhost:8082/api` adresinden bağlanır (`frontend/.env.development`).

> Backend'in CORS ayarı varsayılan olarak `http://localhost:5173` ve `http://localhost:3000`'e izin verir; farklı bir port kullanıyorsan `CORS_ALLOWED_ORIGINS` değişkenini güncellemen gerekir.

---

## Kimlik Doğrulama Notları

- `POST /api/auth/login` dışında **her endpoint token gerektirir**.
- Uygulama ilk açılışta, veritabanında hiç hesap yoksa, `.env`/`.env.properties`'teki `STAFF_BOOTSTRAP_USERNAME`/`STAFF_BOOTSTRAP_PASSWORD` ile otomatik bir hesap oluşturur.
- Yeni hesap eklemek için (**sadece login olmuş bir kullanıcı** yapabilir):
  ```bash
  curl -X POST http://localhost:8082/api/auth/register \
    -H "Authorization: Bearer <token>" \
    -H "Content-Type: application/json" \
    -d '{"username":"garson1","password":"en az 8 karakter"}'
  ```
- Tüm hesaplar eşit yetkiye sahiptir (rol/izin ayrımı yok).

## Veritabanı Migration'ları (Flyway)

Şema değişiklikleri `src/main/resources/db/migration/` altında `V{numara}__{açıklama}.sql` formatında dosyalarla yönetilir. `spring.jpa.hibernate.ddl-auto=validate` olduğu için Hibernate şemayı **değiştirmez**, sadece entity'lerle migration'ların uyumlu olduğunu doğrular.

Yeni bir entity alanı eklerken:
1. Entity'yi güncelle.
2. `db/migration/` altına bir sonraki numarayla (`V2__...sql`) migration dosyası ekle.
3. Uygulamayı başlat — Flyway otomatik uygular.

## API Genel Bakışı

| Kaynak | Base path |
|---|---|
| Kimlik doğrulama | `/api/auth` |
| Masalar | `/api/tables` |
| Menü | `/api/menu-items` |
| Müşteriler | `/api/customers` |
| Siparişler | `/api/orders` |
| Rezervasyonlar | `/api/reservations` |
| Dashboard | `/api/dashboard` |

Tüm liste endpoint'leri `?page=0&size=20&sort=alan,asc|desc` destekler.

## Proje Yapısı

```
restaurant-system/
├── src/main/java/com/berkay/restaurant_system/
│   ├── controllers/   REST endpoint'leri
│   ├── services/      iş mantığı (interface + impl)
│   ├── repositories/  Spring Data JPA repository'leri
│   ├── entities/      JPA entity'leri
│   ├── dtos/          request/response veri transfer nesneleri
│   ├── mappers/       entity <-> DTO dönüşümleri
│   ├── security/      JWT, auth filter, security config
│   ├── config/        Flyway manuel wiring
│   └── exceptions/    global exception handling
├── src/main/resources/
│   ├── application.properties
│   └── db/migration/  Flyway migration dosyaları
├── frontend/
│   ├── src/
│   │   ├── api/        backend ile konuşan axios istemcileri
│   │   ├── components/ Layout, ProtectedRoute, StatusBadge
│   │   ├── context/    AuthContext (JWT token yönetimi)
│   │   ├── pages/      Dashboard, Tables, Menu, Orders, Reservations, Login
│   │   └── types/      backend DTO'larıyla birebir eşleşen TS tipleri
│   ├── Dockerfile       (Node build → nginx serve)
│   └── nginx.conf
├── Dockerfile           (backend: Maven build → JRE runtime)
└── docker-compose.yml   (postgres + app + frontend)
```
