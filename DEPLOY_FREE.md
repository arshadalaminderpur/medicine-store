# Free Deployment Guide (Koyeb + Supabase)

This project can be deployed for free with:

- App hosting: Koyeb Free Web Service
- Database: Supabase Free Postgres

## 1) Create free database (Supabase)

1. Create a Supabase account and new free project.
2. Open `Project Settings -> Database`.
3. Collect the values:
   - host
   - port
   - database
   - user
   - password
4. Build JDBC URL:

`jdbc:postgresql://<host>:5432/postgres?sslmode=require`

## 2) Deploy app from GitHub (Koyeb)

1. Create Koyeb account.
2. Click **Create Web Service**.
3. Choose **GitHub** and select this repository + branch.
4. Choose **Dockerfile** as builder (uses the `Dockerfile` in project root).
5. Select **Free** instance type.
6. Add environment variables:
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
7. Deploy.

## 3) Verify app

1. Open your generated `*.koyeb.app` URL.
2. Try creating a medicine and listing records.
3. Check Koyeb logs if DB connection fails.

## Notes about free tiers

- Free app instances can sleep when idle and wake with a cold start.
- Free database plans have storage/usage limits.
- This setup is ideal for MVP/demo usage.
