package io.nekohasekai.sagernet.ui

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import io.nekohasekai.sagernet.R
import io.nekohasekai.sagernet.databinding.LayoutGetCertBinding
import io.nekohasekai.sagernet.ktx.onMainDispatcher
import io.nekohasekai.sagernet.ktx.runOnDefaultDispatcher
//import io.nekohasekai.sagernet.ktx.snackbar
import android.content.ClipData
import android.content.ClipboardManager
//import io.nekohasekai.sagernet.widget.UndoSnackbarManager
import com.google.android.material.snackbar.Snackbar
import libcore.Libcore

class GetCertActivity : ThemedActivity() {

    private lateinit var binding: LayoutGetCertBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LayoutGetCertBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.apply {
            setTitle(R.string.get_cert)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }
        binding.getCert.setOnClickListener {
            copyCert()
        }
    }

    fun copyCert() {
        binding.waitLayout.isVisible = true

        val server = binding.pinCertServer.text.toString()
        val serverName = binding.pinCertServerName.text.toString()

        runOnDefaultDispatcher {
            try {
                val certificate = Libcore.pinCert(server, serverName)

                if (certificate.isNullOrEmpty()) {
                    onMainDispatcher {
                        binding.waitLayout.isVisible = false
                        AlertDialog.Builder(this@GetCertActivity)
                            .setTitle(R.string.error_title)
                            .setMessage(R.string.get_cert_fail)
                            .setPositiveButton(android.R.string.ok) { _, _ ->
//                                finish()
                            }
                            .setOnCancelListener {
//                                finish()
                            }
                            .runCatching { show() }
                    }
                } else {
                    // 复制到剪贴板
                    val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText("Certificate", certificate)
                    clipboardManager.setPrimaryClip(clipData)

                    val snackbar = Snackbar.make(binding.root, R.string.get_cert_success, Snackbar.LENGTH_SHORT)
                    snackbar.show()

                    onMainDispatcher {
                        binding.waitLayout.isVisible = false
                    }
                }
            } catch (e: Exception) {
                onMainDispatcher {
                    binding.waitLayout.isVisible = false
                }
            }
        }
    }

}
