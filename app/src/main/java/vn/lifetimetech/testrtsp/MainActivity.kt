package vn.lifetimetech.testrtsp

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.potterhsu.rtsplibrary.NativeCallback
import com.potterhsu.rtsplibrary.RtspClient


class MainActivity : AppCompatActivity() {

    private var isPlaying = false
    private lateinit var rtspClient: RtspClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val edtText: EditText = findViewById(R.id.edt_text)

        val ivPreview: ImageView = findViewById(R.id.ivPreview)
        rtspClient = RtspClient(NativeCallback { frame, nChannel, width, height ->
            ivPreview.post {
                val area = width * height
                val pixels = IntArray(area)
                for (i in 0 until area) {
                    var r = frame[3 * i].toInt()
                    var g = frame[3 * i + 1].toInt()
                    var b = frame[3 * i + 2].toInt()
                    if (r < 0) r += 255
                    if (g < 0) g += 255
                    if (b < 0) b += 255
                    pixels[i] = Color.rgb(r, g, b)
                }
                val bmp =
                    Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
                ivPreview.setImageBitmap(bmp)
            }
        })

        val playBtn: Button = findViewById(R.id.button)
        val stopBtn: Button = findViewById(R.id.button2)


        playBtn.setOnClickListener {
            Thread(Runnable { rtspClient.play(edtText.text.toString()) }).start()
        }

        stopBtn.setOnClickListener {
            rtspClient.stop()
        }
    }

    override fun onStop() {
        rtspClient.dispose()
        super.onStop()
    }
}
