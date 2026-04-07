# Huong Dan Deploy Len Render Bang Docker Hub

## 1) Build va push image len Docker Hub
Thay `<dockerhub_user>` bang username Docker Hub cua ban.

```bash
docker build -t <dockerhub_user>/xinnghiphep:1.0.0 .
docker login
docker push <dockerhub_user>/xinnghiphep:1.0.0
```

## 2) Tao PostgreSQL tren Render (goi free)
1. Vao Render Dashboard -> `New` -> `Postgres`
2. Tao database va cho den khi trang thai `Available`
3. Luu cac thong tin ket noi:
- `Host`
- `Port`
- `Database`
- `User`
- `Password`

## 3) Tao Web Service tu image Docker Hub
1. Vao Render Dashboard -> `New` -> `Web Service`
2. Chon `Deploy an existing image from a registry`
3. Dien Image URL: `<dockerhub_user>/xinnghiphep:1.0.0`
4. Dat `Port` la `8080`

## 4) Khai bao bien moi truong cho app
Trong Web Service -> `Environment`, them:

```env
DB_VENDOR=postgres
DB_URL=jdbc:postgresql://<host>:<port>/<database>
DB_USER=<user>
DB_PASSWORD=<password>
HIBERNATE_HBM2DDL_AUTO=update
HIBERNATE_SHOW_SQL=true
HIBERNATE_FORMAT_SQL=true
```

Bien moi truong khuyen nghi (de chuc nang email/avatar on dinh hon tren deploy):

```env
MAIL_FROM_EMAIL=<your_gmail>
MAIL_APP_PASSWORD=<your_gmail_app_password>
MAIL_SMTP_HOST=smtp.gmail.com
MAIL_SMTP_PORT=587
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS_ENABLE=true

CLOUDINARY_URL=<cloudinary_url>
# Hoac dung bo 3 ben duoi neu khong dung CLOUDINARY_URL
# CLOUDINARY_CLOUD_NAME=<cloud_name>
# CLOUDINARY_API_KEY=<api_key>
# CLOUDINARY_API_SECRET=<api_secret>
```

## 5) Cap nhat phien ban image moi
```bash
docker build -t <dockerhub_user>/xinnghiphep:1.0.1 .
docker push <dockerhub_user>/xinnghiphep:1.0.1
```
Sau do vao Render doi tag image sang `1.0.1` va deploy lai.

## 6) Nap du lieu mau vao Postgres Render
Neu tai khoan Render khong co SQL Editor, dung `psql` qua Docker.

Quan trong: phai mo terminal dung tai thu muc local `XinNghiPhep` (noi co file `seed_postgres_render.sql`) roi moi chay lenh sau.

```powershell
Get-Content .\seed_postgres_render.sql -Raw | docker run --rm -i postgres:16 psql "postgresql://<user>:<password>@<host>/<database>?sslmode=require"
```

Neu thanh cong, output se ket thuc bang `COMMIT`.

## Luu y
- Local van mac dinh dung SQL Server, khong can set env.
- `seed_sample_data.sql` la script SQL Server, khong chay truc tiep tren PostgreSQL.
- Script dung cho Render PostgreSQL la `seed_postgres_render.sql`.
- Neu lo password database, hay rotate password trong Render va cap nhat lai `DB_PASSWORD` trong Web Service.

## URL Da Deploy
Available at your primary URL: https://xinnghiphep-1-0-0.onrender.com
