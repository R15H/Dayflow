package com.alextra.tools

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.alextra.tools.databinding.ActivityWriteToNfcBinding
import java.nio.charset.Charset

class WriteToNFC : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityWriteToNfcBinding

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.also { rawMessages ->
                val messages = rawMessages.map { it as NdefMessage }
                println("BIG MESSAGE HERE")
                // Process the messages array.
                for (message in messages) {
                    for (record in message.records) {
                        val payload = record.payload
                        val text = String(payload, Charset.forName("US-ASCII"))
                        println("NFC Message: $text")
                    }
                }
            }
        }
        Log.i("NFC", "onNewIntent: " + intent.action.toString())
        println(intent.action.toString())
        if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            // print everything about the tag
            println("Tag ID: ${tag?.id}")
            println("Tag Tech List: ${tag?.techList?.joinToString()}")
            if (tag != null) {
                writeNfcTag(tag, "Hello, NFC!")
            }
        }
    }


    fun writeNfcTag(tag: Tag, nfcMessage: String): Boolean {
        val nfcTag = Ndef.get(tag)
        val mimeRecord = NdefRecord.createMime("application/com.alextra.tools", nfcMessage.toByteArray(
            Charset.forName("US-ASCII")))
        val ndefMessage = NdefMessage(arrayOf(mimeRecord))

        return try {
            nfcTag?.let {
                it.connect()
                if (it.isWritable) {
                    it.writeNdefMessage(ndefMessage)
                    it.close()
                    true
                } else {
                    Log.e("NFC", "NFC Tag is read-only.")
                    false
                }
            } ?: run {
                Log.e("NFC", "NFC Tag is not NDEF.")
                false
            }
        } catch (e: Exception) {
            Log.e("NFC", "Error writing NFC Tag", e)
            false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWriteToNfcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_write_to_nfc)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_write_to_nfc)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}