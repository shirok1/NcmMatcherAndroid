package tech.shirok1.ncm.matcher.android

import android.Manifest
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.add
import androidx.fragment.app.commit
import org.jaudiotagger.audio.exceptions.NoReadPermissionsException
import tech.shirok1.ncm.matcher.android.databinding.ActivityMainBinding
import tech.shirok1.ncm.matcher.java.libncmapi.android.FileUriOperation.writeMeta
import tech.shirok1.ncm.matcher.java.libncmapi.android.UriParser.getMediaUri

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val clipboardManager by lazy { getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

    @Suppress("UNUSED_PARAMETER")
    fun onPaste(view: View) {
        binding.editTextNcmUrl.apply {
            text.clear()
            text.append(clipboardManager.primaryClip?.getItemAt(0)?.text)
        }
    }

    private lateinit var binding: ActivityMainBinding
    private val model: MainViewModel by viewModels()
    private val singleInfoViewModel: SingleInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        openDocumentAction = registerForActivityResult(
            ActivityResultContracts.OpenDocument(), openDocumentCallback())
        grantReadStorageAndExternalOpenAction = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if (it) {
//                Toast.makeText(this.applicationContext,
//                    getString(R.string.strg_prmsn_granted, getString(R.string.finish_button)),
//                    Toast.LENGTH_LONG).show()
                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                ) externalOpenAudioFile() else throw NoReadPermissionsException()
            } else {
                Toast.makeText(this.applicationContext,
                    getString(R.string.strg_prmsn_failed),
                    Toast.LENGTH_LONG).show()
            }
        }
        singleInfoViewModel.isMetaAvailable.observe(this) { model.metaReady.value = it }
        binding.editTextNcmUrl.addTextChangedListener {
            model.ncmUrl.value = it?.toString()
        }
        model.selectEnabled.observe(this) { binding.buttonSelect.isEnabled = it }
        model.finishEnabled.observe(this) { binding.buttonFinish.isEnabled = it }
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
            }
        }
        model.songId.observe(this) {
            supportFragmentManager.commit {
                if (it != null) {
                    if (binding.fcMusicInfo.childCount == 0) add<SingleInfoFragment>(R.id.fcMusicInfo)
                    singleInfoViewModel.loadMeta(it)
                } else {
                    if (binding.fcMusicInfo.childCount != 0) remove(supportFragmentManager.findFragmentById(
                        R.id.fcMusicInfo)!!)
                }
            }
        }
    }

    private fun openDocumentCallback(): (result: Uri?) -> Unit = {
        Log.i("Uri", it?.toString() ?: "Url is null")
        var fileName: String? = null
        if (it != null && it.let {
                fileName = DocumentFile.fromSingleUri(this, it)?.name
                if (fileName != null) true else {
                    Log.i("Audio Uri", "Uri is not null but invalid!")
                    false
                }
            }) {
            binding.textViewFileName.text =
                getString(R.string.file_name_display_template, fileName)
            model.audioFileUri.value = it
        } else {
            binding.textViewFileName.text = ""
            model.audioFileUri.value = null
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onSelect(view: View) {
        openDocumentAction.launch(arrayOf("audio/mpeg", "audio/flac"))
    }

    @Suppress("UNUSED_PARAMETER")
    fun onFinish(view: View) {
        if (model.audioFileUri.value == null) {
            Log.i("Path", "uri is null")
            return
        } else {
            Log.i("Path", model.audioFileUri.value?.path ?: "path is null")
        }
        singleInfoViewModel.meta.value?.let {
            model.audioFileUri.value!!.writeMeta(view.context, it)
            this.runOnUiThread {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.afterwrite_dialog_title))
                    setMessage(getString(R.string.afterwrite_dialog_message, it.name))
                    setPositiveButton(getString(R.string.afterwrite_dialog_positive)) { _, _ ->
                        if (ActivityCompat.checkSelfPermission(view.context,
                                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        ) {
                            grantReadStorageAndExternalOpenAction.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        } else {
                            externalOpenAudioFile()
                        }
                    }
                    setNegativeButton(getString(R.string.afterwrite_dialog_negative)) { dialog, _ -> dialog.dismiss() }
                    show()
                }
            }
        }
    }

    @RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    private fun externalOpenAudioFile() {
        model.audioFileUri.value?.let {
            val intent = Intent(Intent.ACTION_VIEW)
            val mediaUri = it.getMediaUri(this, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            Log.i("MediaUri", mediaUri.toString())
            intent.setDataAndType(mediaUri, "audio/*")
            val title = "Now open this audio file with:"
            val chooser = Intent.createChooser(intent, title)
            startActivity(chooser)
        }
    }

    private lateinit var openDocumentAction: ActivityResultLauncher<Array<String>>
    private lateinit var grantReadStorageAndExternalOpenAction: ActivityResultLauncher<String>
}