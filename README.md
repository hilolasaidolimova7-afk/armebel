# AR Mebel (IKEA Place'ga o'xshash)

Android + ARCore + SceneView (Filament) + Jetpack Compose.

## Ishlatish
1. Ilovani oching, kameraga ruxsat bering.
2. Telefonni sekin harakatlantirib polni skanerlang.
3. Pastdan mebelni tanlang, yuzaga bosing – model qo'yiladi.
4. Modelni bosib: 1 barmoq – surish, 2 barmoq – aylantirish/kattalashtirish.
5. Korzinka tugmasi – hammasini o'chiradi.

## Yangi model qo'shish
`.glb` faylni `app/src/main/assets/models/` ga tashlang va `ArScreen.kt` dagi `catalog` ro'yxatiga qo'shing.

## APK build (GitHub Actions)
Repoga push qiling → **Actions** → **Build APK** → **Artifacts** dan `ArFurniture-debug` yuklab oling.

## Talablar
ARCore qo'llab-quvvatlaydigan telefon (Android 7.0+) va Google Play Services for AR.
