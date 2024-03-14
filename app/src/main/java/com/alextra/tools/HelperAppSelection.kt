import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ListItem
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AppSelectionScreen() {
    val context = LocalContext.current
    val packageManager = context.packageManager
    var searchText by remember { mutableStateOf("") }

    val appList = remember {
        getAppList(packageManager)
    }

    val filteredApps = remember(searchText) {
        appList.filter { it.resolvePackageName.contains(searchText, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { newText ->
                searchText = newText
            },
            placeholder = { Text(text = "Search Apps") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(onSearch = {
                // You can handle search action here if needed
            }),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        LazyColumn {
            items(filteredApps.size) { appInfo ->
                AppListItem(appInfo.toString()) {
                    launchApp(context, appInfo.toString())
                }
            }
        }
    }
}

@Composable
fun AppListItem(packageName: String, onClick: () -> Unit) {
    ListItem(
        text = { Text(text = packageName) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    )
}

private fun getAppList(packageManager: PackageManager): List<ResolveInfo> {
    val intent = Intent(Intent.ACTION_MAIN, null)
    intent.addCategory(Intent.CATEGORY_LAUNCHER)
    return packageManager.queryIntentActivities(intent, 0)
}

private fun launchApp(context: android.content.Context, packageName: String) {
    val intent = context.packageManager.getLaunchIntentForPackage(packageName)
    if (intent != null) {
        context.startActivity(intent)
    }
}

@Preview(showBackground = true)
@Composable
fun AppSelectionScreenPreview() {
    MyAppTheme {
        AppSelectionScreen()
    }
}

@Composable
fun MyAppTheme(content: @Composable () -> Unit) {
    val typography = MaterialTheme.typography
    val colors = MaterialTheme.colorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        content = content
    )
}

class HelperAppSelection : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                Surface(
                    color = Color.White,
                    modifier = Modifier.fillMaxSize()
                ) {
                    AppSelectionScreen()
                }
            }
        }
    }
}
