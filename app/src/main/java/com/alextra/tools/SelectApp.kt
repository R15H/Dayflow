package com.alextra.tools

import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class AppSelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_app_selection)



        /*
        setContent {
            Text("Hello, World!")
        }
        val appList = getAppList()
        val appListView = findViewById<ListView>(R.id.appListView)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, appList)
        appListView.adapter = adapter

        appListView.setOnItemClickListener(AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedApp = appList[position]
            launchApp(selectedApp)
        })

         */
    }

    private fun getAppList(): List<String> {
        val appList = mutableListOf<String>()
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        val packageManager = packageManager
        val resolveInfoList: List<ResolveInfo> = packageManager.queryIntentActivities(intent, 0)

        for (resolveInfo in resolveInfoList) {
            appList.add(resolveInfo.activityInfo.packageName)
        }

        return appList
    }

    private fun launchApp(packageName: String) {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            startActivity(intent)
        }
    }
}