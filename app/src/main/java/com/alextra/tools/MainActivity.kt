package com.alextra.tools

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.ui.AppBarConfiguration
import com.alextra.tools.ui.main.SectionsPagerAdapter
import com.alextra.tools.databinding.ActivityMainBinding
import com.alextra.tools.databinding.ActivityWriteToNfcBinding
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset

class UserActivity {
    // activity has a recordType, a set of recordTags, a set of categories, and maybe a single comment
    var recordType: String = ""
    var recordTags: Set<String> = setOf()
    var categories: Set<String> = setOf()
    var comment: String = ""
}

class MainActivity : AppCompatActivity() {

    fun launchTimerActivityIntent(activityName: String, recordComment: String, recordTags: Set<String>) {
        startActivity(Intent("com.razeeman.util.simpletimetracker.STOP_ACTIVITY")) // swap activities
        val intent = Intent("com.razeeman.util.simpletimetracker.ACTION_START_ACTIVITY")
        intent.putExtra("extra_activity_name", activityName)
        intent.putExtra("extra_record_comment", recordComment)
        intent.putExtra("extra_record_tag", recordTags.joinToString())
        startActivity(intent)
    }

    // serialize parameters of launchTimerActivityIntent to string
    fun serializeLaunchTimerActivityIntent(activityName: String, recordComment: String, recordTags: Set<String>): String {
        return "com.razeeman.util.simpletimetracker.ACTION_START_ACTIVITY" + " " + activityName + " " + recordComment + " " + recordTags.joinToString()
    }
    // deserialize parameters of launchTimerActivityIntent from string
    fun deserializeLaunchTimerActivityIntent(serializedIntent: String): Triple<String, String, Set<String>> {
        val parts = serializedIntent.split(" ")
        return Triple(parts[1], parts[2], parts.subList(3, parts.size).toSet())
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

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


        val actions = hashMapOf(
            "START_ACTIVITY" to "com.razeeman.util.simpletimetracker.ACTION_START_ACTIVITY",
            "STOP_ACTIVITY" to "com.razeeman.util.simpletimetracker.ACTION_STOP_ACTIVITY",
            "STOP_ALL_ACTIVITIES" to "com.razeeman.util.simpletimetracker.ACTION_STOP_ALL_ACTIVITIES",
            "STOP_SHORTEST_ACTIVITY" to "com.razeeman.util.simpletimetracker.ACTION_STOP_SHORTEST_ACTIVITY",
            "STOP_LONGEST_ACTIVITY" to "com.razeeman.util.simpletimetracker.ACTION_STOP_LONGEST_ACTIVITY",
            "RESTART_ACTIVITY" to "com.razeeman.util.simpletimetracker.ACTION_RESTART_ACTIVITY"
        )

        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            println("SOMETHIng WAS DIFESCOEVerd")
            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.also { rawMessages ->
                val messages = rawMessages.map { it as NdefMessage }
                println("BIG MESSAGE HERE")
                for (message in messages) {
                    for (record in message.records) {
                        val payload = record.payload
                        val text = String(payload, Charset.forName("US-ASCII"))
                        println("NFC Message: $text")
                    }
                }
            }
            // write to the NFC tag
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            if (tag != null && NFCMessageToWrite != "") writeNfcTag(tag, "Hello, NFC!")
        }
    }

    val NFCMessageToWrite = "";

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

    private lateinit var binding: ActivityMainBinding
    // read only

    // set of categories
    private val categories: MutableSet<String> = mutableSetOf()
    // set of recordTypes
    private val recordTypes: MutableSet<String> = mutableSetOf()
    // set of recordTags
    private val recordTags: MutableSet<String> = mutableSetOf()
    // what categories are associated with what recordTypes
    private val categoryRecordType: MutableMap<String, String> = mutableMapOf()




    private val pickFileResultLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        // Handle the returned Uri
        if (uri != null) {
            readTextFromUri(uri)
        }
    }

    private fun readTextFromUri(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        var line: String?
        println("CCCC")
        while (reader.readLine().also { line = it } != null) {
            stringBuilder.append(line)
            println( line)
            // split line by tabs
            var fields: List<String>? = line?.split("\t") ?: continue

            /*
            first field is the type of entry, for the type "recordType" the fields are: id, name
for the field "category" the fields are: id, name
for the "typeCategory" the fields are:  recordType, categoryType
for the "recordTag" the fields are: recordTypeID, id, name

I want you to load all this data into an hashmap,
             */
            // record Type



            // the switch case equivalent is
            when(fields?.get(0)) {
                "recordType" -> {
                    recordTypes.add(fields[1])
                }
                "category" -> {
                    categories.add(fields[1])
                }
                "typeCategory" -> {
                    categoryRecordType[fields[1]] = fields[2]
                }
                "recordTag" -> {
                    recordTags.add(fields[2])
                }
                else -> {
                }
            }

        }
        inputStream?.close()
        // Here, 'stringBuilder.toString()' is your file content.
        // Update your UI or do further processing with the file content as needed.
        // put the file content in the UI

        // print the file content Debug

        //val text = findViewById<android.widget.TextView>(R.id.fileContent)
        //text.text = stringBuilder.toString()
    }


    fun pickFile(view : View) { // the OnClickListener tries to find stuff in the MainActivity
        // Launch the document picker
        // get nfc tag

        pickFileResultLauncher.launch(arrayOf("*/*"))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = binding.fab






        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        onNewIntent(intent);
    }
}