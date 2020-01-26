package app.printer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.print.PrintHelper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

public class MainActivity extends AppCompatActivity {

    String buttonText = "Нажмите";//Текст на кнопке
    final String URL = "Your URL";//Ваша ссылка

    int QRcodeWidth = 200;//Длинна стороны кода

    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView imageView = findViewById(R.id.imageView);//ImageView, в который сохраняется изображение с кодом

        //LinearLayout layout = findViewById(R.id.pdf_linear);
        //layout.animate().alpha(0); - сделать Layout с данными чека невидимым, в нём сохранён шаблон с чеком, т.е. содержимое этого layout конвертируется
        //в изображение, которое отправляется на печать

        btn = findViewById(R.id.button);//Определение обхекта кнопки
        btn.setText(buttonText);//Назначение ей названия
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {//Назначение действия
                try
                {
                    Bitmap bmp =  TextToQr(URL);//Сохранить Qr код ссылки из переменной URL
                    imageView.setImageBitmap(bmp);//Отображение полученного кода
                    generateIMG();//Генерация изображения с LinearLayout с изображение и печать
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    void printBmp(Bitmap bmp){//Функция печати. К сожалению, не имею принтера, но соголасно документации должно работать - https://developer.android.com/training/printing/photos.html
        PrintHelper photoPrinter = new PrintHelper(getApplicationContext());//
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);

        photoPrinter.printBitmap("Check", bmp);//Печать Bitmap изображения, присваивание ему названия "Check"

    }

    void generateIMG(){
        DisplayMetrics displayMetrics = new DisplayMetrics();//Определение объекта для сохранения размеров экрана

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);//Получение размеров экрана и запись в объект

        LinearLayout layout = findViewById(R.id.linear);

        Bitmap bmp = loadBitmapFromView(layout, layout.getWidth(), layout.getHeight());//Получение Bitmap изображения с LinearLayout с id linear
        bmp = Bitmap.createScaledBitmap(bmp, displayMetrics.heightPixels, displayMetrics.widthPixels, true);//Создание масштабированного изображения из bmp
        //передача высоты, широты

        printBmp(bmp);//Печать изображения
    }

    public Bitmap loadBitmapFromView(View v, int width, int height) {//Получение изображения с переданного объекта, в данном случае - LinearLayout
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);

        return b;
    }

    Bitmap TextToQr(String text){//Генерация Qr кода
        BitMatrix result;
        Bitmap bitmap;
        try
        {
            result = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, QRcodeWidth, QRcodeWidth, null);

            int w = result.getWidth();
            int h = result.getHeight();
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                int offset = y * w;
                for (int x = 0; x < w; x++) {
                    pixels[offset + x] = result.get(x, y) ? Color.BLACK:Color.WHITE;
                }
            }
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QRcodeWidth, 0, 0, w, h);
        } catch (Exception iae) {
            iae.printStackTrace();
            return null;
        }
        return bitmap;
    }
}
