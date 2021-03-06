package android.common.utils.photo

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.fragment.app.FragmentActivity
import androidx.core.content.FileProvider
import android.common.componentes.BuildConfig
import java.io.File

/**
 * Created by KerriGan on 2017/7/12.
 */
abstract class CropPhotoHelper {

    companion object {
        @JvmStatic
        protected val PHOTO_RESULT = 0x1000
    }

    // 图片缩放
    fun photoZoom(uri: Uri, fragmentActivity: androidx.fragment.app.FragmentActivity, outputPath: String) {
        val intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(uri, "image/*")
        intent.putExtra("crop", "true")
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 200)
        intent.putExtra("outputY", 200)
        intent.putExtra("return-data", false)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(File(outputPath)))
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString())
        fragmentActivity.startActivityForResult(intent, PHOTO_RESULT)
    }

    abstract fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

    abstract fun clearCache()

    abstract fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
}