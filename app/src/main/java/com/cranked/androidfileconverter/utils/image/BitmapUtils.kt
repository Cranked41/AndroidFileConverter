package com.cranked.androidfileconverter.utils.image

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.*
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import androidx.annotation.NonNull
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.cranked.androidfileconverter.BuildConfig
import com.cranked.androidfileconverter.R
import com.cranked.androidfileconverter.utils.Constants
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


object BitmapUtils {
    fun pdfToBitmap(context: Context, pdfFile: File): ArrayList<Bitmap> {
        val bitmaps: ArrayList<Bitmap> = ArrayList()
        try {
            val renderer = PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY))
            var bitmap: Bitmap
            val pageCount = renderer.pageCount
            for (i in 0 until pageCount) {
                val page = renderer.openPage(i)
                val width: Int = context.resources.displayMetrics.densityDpi / 72 * page.width
                val height: Int = context.resources.displayMetrics.densityDpi / 72 * page.height
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                bitmaps.add(bitmap)

                // close the page
                page.close()
            }
            // close the renderer
            renderer.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return bitmaps
    }

    fun getImageOfPdf(context: Context, file: File, index: Int): Bitmap {
        val bitmaps: ArrayList<Bitmap> = ArrayList()
        try {
            val renderer = PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY))
            var bitmap: Bitmap
            for (i in 0..index) {
                val page = renderer.openPage(i)
                val width: Int = context.resources.displayMetrics.densityDpi / 72 * page.width
                val height: Int = context.resources.displayMetrics.densityDpi / 72 * page.height
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                bitmaps.add(bitmap)

                // close the page
                page.close()
            }
            // close the renderer
            renderer.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return bitmaps[index]
    }

    fun getRoundedBitmap(resources: Resources, bitmap: Bitmap, cornerRadius: Float): Bitmap {
        var roundedBitmap = RoundedBitmapDrawableFactory.create(resources, bitmap)
        roundedBitmap.cornerRadius = cornerRadius
        return roundedBitmap.toBitmap()
    }
    fun takePhoto(fragment: Fragment, path: String) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        var file: File? = null

        if (intent.resolveActivity(fragment.requireActivity().packageManager) != null) {
            try {
                file = createImageFile(path)
            } catch (e: Exception) {
            }
            val uri = FileProvider.getUriForFile(fragment.requireActivity(), BuildConfig.APPLICATION_ID + ".provider", file!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            fragment.startActivityForResult(intent, Constants.RESULT_ADD_PHOTO)
        }
    }

    private fun createImageFile(filePath: String): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val file = File(filePath)
        file.mkdirs()

        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            file /* directory */
        )
    }
}