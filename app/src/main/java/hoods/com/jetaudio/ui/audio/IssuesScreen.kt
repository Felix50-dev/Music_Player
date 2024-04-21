package hoods.com.jetaudio.ui.audio

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Date

@Composable
fun IssuesScreen(
) {

}

@Composable
fun IssueItem(

) {

}

@Composable
fun IssuesTrackerAppBar(date: Date) {
    Row (
        modifier = Modifier.fillMaxWidth()
    ){
        Column {
            Text(
                text = "Today",
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = date.toString(),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.Settings,
            modifier = Modifier.clickable { },
            contentDescription = null
        )
    }
}

