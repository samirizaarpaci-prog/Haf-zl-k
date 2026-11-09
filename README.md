# Hafızlık

Hafızlıkta ilerlemeyi takip etmek ve görmek için Android uygulaması. İlk açılışta hangi usulle ezber yapacağın sorulur; usul sonra Ayarlar'dan değiştirilebilir:

- **Osmanlı Usulü:** her cüzün son sayfasından geriye doğru; her gün sıradaki cüzden bir çiğ sayfa, o cüzün önceki sayfaları pişmiş olarak okunur.
- **Pakistan Usulü:** baştan sona sırayla; her gün sebak (yeni ders), sebki (bulunduğun cüzün başından bugüne kadarki kısmı) ve menzil (biten eski cüzlerden sıradaki parça).
- **Cüz Usulü:** her gün sırayla bir yeni sayfa; cüzün kaçıncı sayfasındaysan o kadar cüz geri gidip o cüz tekrar edilir. 22. cüzden itibaren 21–30. cüzler 10'ar sayfalık yan sırayla eklenir; günlük tekrar en fazla 30 sayfa.
- **Sure Sure:** 114 sure arasından seçerek, ayet ayet.

## Telefona kurmak

1. Bu sayfadaki **Releases** bölümüne gir (telefonda sayfanın aşağısında).
2. En üstteki sürümde **Hafizlik-1.0.X.apk** dosyasına dokun ve indir.
3. İndirilen dosyayı aç. "Bilinmeyen uygulamalar" uyarısı çıkarsa **Ayarlar**'a gir, **Bu kaynaktan izin ver**'i aç, geri dön ve **Yükle**'ye bas.

Yeni sürüm eskisinin üstüne kurulur; ilerlemen silinmez.

## Nasıl derleniyor

`main` dalına her yüklemede GitHub, APK'yı kendi bilgisayarlarında derler ve Releases sayfasına koyar (Actions sekmesindeki **Android APK** iş akışı). Derleme başarısız olursa hata kaydı **Issues** bölümüne yazılır.

## Klasörler

- `app/src/main/assets/index.html`: uygulamanın kendisi (tek sayfa).
- `app/src/main/java/com/sraatolye/hafizlik/MainActivity.java`: sayfayı telefonda tam ekran açan Android kabuğu.
- `keys/deneme-anahtari.p12`: deneme APK'larının imza anahtarı. Gizli değildir. Her sürüm aynı anahtarla imzalandığı için güncellemeler eskisinin üstüne kurulur. Google Play'e yüklenecek paket ayrı ve gizli bir anahtarla imzalanacak.

## Veriler

İlerleme telefonda, uygulamanın içinde saklanır; internete gönderilmez. Telefon değiştirmeden ya da uygulamayı silmeden önce **Ayarlar › Yedek › Yedeği kopyala** ile yedek al, yeni telefonda **Yedekten geri yükle** ile aktar.

Geliştirici: SRA Atölye
