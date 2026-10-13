# Restaurant Management System

Restoran yönetimi için geliştirilmiş bir Spring Boot REST API'si: masa, menü, müşteri, sipariş, rezervasyon yönetimi, mutfak ekranı akışı, raporlama/dashboard ve JWT tabanlı kimlik doğrulama içerir.

## Teknoloji Yığını

- Java 21, Spring Boot 4.1.1
- Spring Data JPA (Hibernate) + PostgreSQL
- Flyway (veritabanı migration'ları)
- Spring Security + JWT (kimlik doğrulama)
- Docker / Docker Compose

## Özellikler

- **Masa yönetimi**: CRUD, durum takibi (`AVAILABLE`, `OCCUPIED`, `RESERVED`, `CLEANING`), doluluk geçmişi
- **Menü yönetimi**: CRUD, kategori, stok durumu (`available`), filtreleme
- **Müşteri yönetimi**: CRUD, masa tekillik kontrolü
- **Sipariş yönetimi**: kalem bazlı (adet, not, sipariş anı fiyatı), durum akışı (`RECEIVED → IN_PREPARATION → READY → SERVED → PAID`, her aşamadan `CANCELLED`), mutfak kuyruğu, masa bazlı aktif sipariş, telefon numarasına göre sipariş geçmişi
- **Rezervasyon**: zaman çakışma kontrolü, check-in akışı (otomatik müşteri oturtma), bugünün rezervasyonları, no-show tespiti
- **Dashboard/Raporlama**: ciro (günlük/haftalık/aylık), en çok satan ürünler, saatlik yoğunluk, anlık özet (aktif sipariş, mutfak kuyruğu, masa durumları)
- **Kimlik doğrulama**: JWT tabanlı, tüm endpoint'ler login gerektirir (tek tip hesap, rol ayrımı yok)
- **Pagination & sorting**: tüm liste endpoint'lerinde `?page=&size=&sort=`

## Gereksinimler

- Docker + Docker Compose (önerilen — başka bir şey kurmana gerek yok)

**Veya** lokal çalıştırmak istersen:
- Java 21 (JDK)
- PostgreSQL 18 (ya da uyumlu bir sürüm)

---

## Hızlı Başlangıç (Docker ile — önerilen)

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
```

> `JWT_SECRET` için rastgele bir değer üretmek istersen: `openssl rand -base64 64`

### 2. Sistemi ayağa kaldır

```bash
docker compose up -d --build
```

Bu komut:
- PostgreSQL container'ını başlatır (healthcheck geçene kadar bekler)
- Uygulamayı derler ve başlatır (`http://localhost:8082`)
- Flyway migration'larını otomatik uygular
- Veritabanı boşsa, `.env`'deki bilgilerle ilk admin hesabını otomatik oluşturur

### 3. Doğrula

```bash
curl -i http://localhost:8082/api/tables
```

`401 Unauthorized` dönmesi beklenen davranıştır — önce login olman gerekiyor:

```bash
curl -X POST http://localhost:8082/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"<.env deki STAFF_BOOTSTRAP_PASSWORD>"}'
```

Dönen `token`'ı sonraki her istekte `Authorization: Bearer <token>` header'ı olarak kullan.

### Durdurma

```bash
docker compose down        # container'ları durdurur, veritabanı verisi kalır (named volume)
docker compose down -v     # container'ları durdurur VE veritabanını tamamen siler
```

---

## Lokal Çalıştırma (Docker'sız)

### 1. PostgreSQL'i hazırla

Yerel bir PostgreSQL instance'ın çalışıyor olmalı. Varsayılan bağlantı `localhost:5434` bekliyor (`application.properties`'teki default'lar).

### 2. `.env.properties` dosyasını oluştur

Proje kökünde:

```env
DB_USER=restaurant_admin
DB_PASSWORD=<şifren>

JWT_SECRET=<rastgele bir string>
JWT_EXPIRATION_MS=86400000

STAFF_BOOTSTRAP_USERNAME=admin
STAFF_BOOTSTRAP_PASSWORD=<güçlü bir şifre>
```

### 3. Çalıştır

```bash
./mvnw spring-boot:run
```

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
src/main/java/com/berkay/restaurant_system/
├── controllers/   REST endpoint'leri
├── services/      iş mantığı (interface + impl)
├── repositories/  Spring Data JPA repository'leri
├── entities/      JPA entity'leri
├── dtos/          request/response veri transfer nesneleri
├── mappers/       entity <-> DTO dönüşümleri
├── security/      JWT, auth filter, security config
├── config/        Flyway manuel wiring
└── exceptions/    global exception handling
src/main/resources/
├── application.properties
└── db/migration/  Flyway migration dosyaları
```
