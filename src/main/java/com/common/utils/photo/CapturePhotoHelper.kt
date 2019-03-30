package com.common.utils.photo

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.FileProvider
import android.text.TextUtils
import com.common.componentes.BuildConfig
import com.common.utils.activity.ActivityUtil
import com.common.utils.file.FileUtil
import java.io.File


/**
 * Created by KerriGan on 2017/7/11.
 */
class CapturePhotoHelper(fragmentActivity: FragmentActivity) : CropPhotoHelper() {
    private var mActivity: FragmentActivity? = null

    companion object {
        private const val TAKE_PHOTO = 0x1001
        private val IMAGE_PATH = Environment.getExternalStorageDirectory().absolutePath
        private const val REQUEST_CODE = 10002
    }

    private var authority: String = ""

    init {
        mActivity = fragmentActivity
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == TAKE_PHOTO) {
                val picture = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    FileProvider.getUriForFile(
                            mActivity, authority, File(IMAGE_PATH, "temp.jpg"))
                } else {
                    Uri.fromFile(File(IMAGE_PATH, "temp.jpg"))
                }
                photoZoom(picture, mActivity!!, IMAGE_PATH + "/head.png")
            }

            if (requestCode == PHOTO_RESULT) {
                //get corp image
                val file = File(IMAGE_PATH + "/head.png")
                FileUtil.copyFile2InternalPath(file, "head.png", mActivity!!)

                File(IMAGE_PATH + "/temp.jpg").delete()
                file.delete()
            }
        }
    }


    fun takePhoto(authority: String) {
        clearCache()
        this.authority = authority

        if (ActivityCompat.checkSelfPermission(mActivity!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity!!, arrayOf(Manifest.permission.CAMERA), REQUEST_CODE)
            return
        }

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val photoUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                    mActivity,
                    this.authority,
                    File(IMAGE_PATH, "temp.jpg"))
        } else {
            Uri.fromFile(File(IMAGE_PATH, "temp.jpg"))
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        mActivity?.startActivityForResult(intent, TAKE_PHOTO)
    }

    override fun clearCache() {
        File(IMAGE_PATH + "/temp.jpg").delete()
        File(IMAGE_PATH + "/head.png").delete()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE) {
            val act = mActivity ?: return
            //AppOpsManager
            if (ActivityCompat.checkSelfPermission(act, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                if (!TextUtils.isEmpty(this.authority)) {
                    takePhoto(this.authority!!)
                }
            } else {
                val builder = AlertDialog.Builder(act)
                builder.setTitle("Warn")
                        .setMessage("Need to get camera permission.")
                        .setPositiveButton(android.R.string.ok) { dialog, which ->
                            act.startActivity(ActivityUtil.getAppDetailSettingIntent(act))
                        }
                builder.show()
            }
        }
    }
}