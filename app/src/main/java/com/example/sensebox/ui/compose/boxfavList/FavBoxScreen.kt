package com.example.sensebox.ui.compose.boxfavList

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sensebox.R
import com.example.sensebox.data.fake.FakeBoxDataProvider
import com.example.sensebox.data.model.Box
import com.example.sensebox.ui.compose.boxlist.BoxNameAndSensorColumn
import com.example.sensebox.ui.compose.home.TopBarWithMenu

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FavBoxScreen (
    onMenuClick: () -> Unit,
    viewModel: BoxFavViewModel = hiltViewModel(),
    onBoxClick: (String) -> Unit,
) {
    val visibleState = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    val uiState by viewModel.favBoxUiState.collectAsState()
    var openDialogDeleteBox by remember { mutableStateOf(false ) }
    var openDialogDeleteAllBoxes by remember { mutableStateOf(false ) }
    var boxToDelete by remember { mutableStateOf("") }
    Scaffold(
        topBar = { TopBarWithMenu(onMenuClick = onMenuClick, title = stringResource(id = R.string.favorite_title, uiState.favBoxes.size))
        },
        floatingActionButton = {
                FloatingActionButton(
                    onClick = { openDialogDeleteAllBoxes = true },
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete_all),
                    )
                }
        }
    ) { padding ->
        AnimatedVisibility(
            visibleState = visibleState,
            enter = fadeIn(
                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
            ),
            exit = fadeOut()
        ) {
            Column() {
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    items(items = uiState.favBoxes, key = { box -> box.id }) { box ->
                        FavBoxListItem(
                            box = box,
                            onBoxClick = onBoxClick,
                            onDeleteClick = {
                                openDialogDeleteBox = true
                                boxToDelete = it
                            },
                            modifier = Modifier
                                .padding(start = 12.dp, end = 12.dp, top = 12.dp)
                                .animateItemPlacement(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                        )
                    }
                }
            }

            if (openDialogDeleteBox) {
                DeleteConfirmationDialog(
                    title = stringResource(R.string.delete_box_warning),
                    text = stringResource(R.string.are_you_sure_warning),
                    onConfirm = {
                        viewModel.deleteFavBox(boxToDelete)
                        openDialogDeleteBox = false
                    },
                    onDismiss = { openDialogDeleteBox = false }
                )
            }
            if (openDialogDeleteAllBoxes) {
                DeleteConfirmationDialog(
                    title = stringResource(R.string.clear_all_items_warning),
                    text = stringResource(R.string.text_delete_all_database_warning),
                    onConfirm = {
                        viewModel.deleteAll()
                        openDialogDeleteAllBoxes = false
                    },
                    onDismiss = { openDialogDeleteAllBoxes = false }
                )
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog (
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        modifier = Modifier.width(250.dp),
        onDismissRequest = onDismiss,
        title = { Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
        ) },
        text = {
            Box(modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
            }
       },
        icon = { Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(50.dp)
        ) },
        dismissButton = {
            Card  {
                Text(
                    text = stringResource(R.string.cancel),
                    modifier = Modifier
                        .clickable { onDismiss() }
                        .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp),
                    style = MaterialTheme.typography.titleMedium,
                )

            } },
        confirmButton = {
            Card {
                Text(
                    text = stringResource(R.string.ok),
                    modifier = Modifier
                        .clickable { onConfirm() }
                        .padding(start = 37.dp, end = 37.dp, top = 10.dp, bottom = 10.dp),
                    style = MaterialTheme.typography.titleMedium,
                )
            } }
    )
}

@Composable
fun FavBoxListItem (
    modifier: Modifier = Modifier,
    box: Box,
    onBoxClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    Card(
        modifier = modifier
            .clickable {
                onBoxClick(box.id)
            },
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column (modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                BoxNameAndSensorColumn(box = box)
            }
            IconButton(onClick = { onDeleteClick(box.id) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DeleteConfirmationDialogPreview() {
    DeleteConfirmationDialog(
        title = stringResource(R.string.clear_all_items_warning),
        text = stringResource(R.string.text_delete_all_database_warning),
        onConfirm = {},
        onDismiss = {}
    )
}

@Preview(showBackground = true)
@Composable
fun FavBoxListItemPreview() {
    FavBoxListItem(box = FakeBoxDataProvider.fakeBoxListItem, onBoxClick = {}, onDeleteClick = {})
}

