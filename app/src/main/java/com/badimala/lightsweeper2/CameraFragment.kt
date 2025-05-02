package com.badimala.lightsweeper2

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LightingColorFilter
import android.graphics.Paint
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast

import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.min

class CameraFragment : Fragment() {
    private var photoFile: File? = null
    private lateinit var photoImageView: ImageView
    private lateinit var brightnessSeekBar: SeekBar
    private lateinit var saveButton: Button

    // For adding brightness
    private var multColor = -0x1
    private var addColor = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parentView = inflater.inflate(R.layout.fragment_camera, container, false)

        photoImageView = parentView.findViewById(R.id.photo)

        saveButton = parentView.findViewById(R.id.save_button)
        saveButton.setOnClickListener { savePhotoClick() }
        saveButton.isEnabled = false

        parentView.findViewById<Button>(R.id.take_photo_button).setOnClickListener { takePhotoClick() }

        brightnessSeekBar = parentView.findViewById(R.id.brightness_seek_bar)
        brightnessSeekBar.visibility = View.INVISIBLE

        brightnessSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                changeBrightness(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        return parentView
    }

    private fun takePhotoClick() {

        // Create the File for saving the photo
        photoFile = createImageFile()

        // Create a content URI to grant camera app write permission to photoFile
        val photoUri = FileProvider.getUriForFile(
            requireActivity(), "com.badimala.lightsweeper2.fileprovider", photoFile!!)

        // Start camera app
        takePicture.launch(photoUri)
    }

    private val takePicture = registerForActivityResult(
        TakePicture()
    ) { success ->
        if (success) {
            displayPhoto()
            brightnessSeekBar.progress = 100
            brightnessSeekBar.visibility = View.VISIBLE
            changeBrightness(brightnessSeekBar.progress)
            saveButton.isEnabled = true
        }
    }

    private fun createImageFile(): File {

        // Create a unique image filename
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFilename = "photo_$timeStamp.jpg"

        // Get file path where the app can save a private image
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(storageDir, imageFilename)
    }

    private fun displayPhoto() {
        // Get ImageView dimensions
        val targetWidth = photoImageView.width
        val targetHeight = photoImageView.height

        // Get bitmap dimensions
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(photoFile!!.absolutePath, bmOptions)
        val photoWidth = bmOptions.outWidth
        val photoHeight = bmOptions.outHeight

        // Determine how much to scale down the image
        val scaleFactor = min(photoWidth / targetWidth, photoHeight / targetHeight)

        // Decode the image file into a smaller bitmap that fills the ImageView
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor
        val bitmap = BitmapFactory.decodeFile(photoFile!!.absolutePath, bmOptions)

        // Display smaller bitmap
        photoImageView.setImageBitmap(bitmap)
    }

    private fun changeBrightness(brightness: Int) {
        // 100 is the middle value
        if (brightness > 100) {
            // Add color
            val addMult = brightness / 100f - 1
            addColor = Color.argb(
                255, (255 * addMult).toInt(), (255 * addMult).toInt(),
                (255 * addMult).toInt()
            )
            multColor = -0x1
        } else {
            // Scale color down
            val brightMult = brightness / 100f
            multColor = Color.argb(
                255, (255 * brightMult).toInt(), (255 * brightMult).toInt(),
                (255 * brightMult).toInt()
            )
            addColor = 0
        }

        val colorFilter = LightingColorFilter(multColor, addColor)
        photoImageView.colorFilter = colorFilter
    }

    private fun savePhotoClick() {
        // Don't allow Save button to be pressed while image is saving
        saveButton.isEnabled = false

        if (photoFile != null) {

            // Save image in background thread
            CoroutineScope(Dispatchers.Main).launch {
                saveAlteredPhoto(photoFile!!, multColor, addColor)

                // Show message
                Toast.makeText(requireContext(), R.string.photo_saved, Toast.LENGTH_LONG).show()

                // Allow Save button to be clicked again
                saveButton.isEnabled = true
            }
        }
    }

    private suspend fun saveAlteredPhoto(photoFile: File, filterMultColor: Int,
                                         filterAddColor: Int) = withContext(Dispatchers.IO) {
        // Read original image
        val origBitmap = BitmapFactory.decodeFile(photoFile.absolutePath, null)

        // Create a new origBitmap with the same dimensions as the original
        val alteredBitmap = Bitmap.createBitmap(origBitmap.width, origBitmap.height,
            origBitmap.config!!
        )

        // Draw original origBitmap on canvas and apply the color filter
        val canvas = Canvas(alteredBitmap)
        val paint = Paint()
        val colorFilter = LightingColorFilter(filterMultColor, filterAddColor)
        paint.colorFilter = colorFilter
        canvas.drawBitmap(origBitmap, 0f, 0f, paint)

        // Save bitmap as JPEG
        val imageFile = File(photoFile.absolutePath)
        imageFile.outputStream().use { outStream ->
            alteredBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
        }
    }
}