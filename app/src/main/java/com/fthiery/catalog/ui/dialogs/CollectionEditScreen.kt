package com.fthiery.catalog.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fthiery.catalog.R
import com.fthiery.catalog.models.ItemCollection
import com.fthiery.catalog.ui.baselevel.angles
import com.fthiery.catalog.ui.baselevel.cornerSizes
import com.fthiery.catalog.ui.baselevel.quadrilateralShape
import com.fthiery.catalog.ui.theme.angle
import com.fthiery.catalog.viewmodels.CollectionViewModel

@Composable
fun CollectionEditScreen(
    viewModel: CollectionViewModel,
    navController: NavController,
    onComplete: (Long) -> Unit = {},
    collection: ItemCollection
) {
    var name by rememberSaveable(collection.name) { mutableStateOf(collection.name) }

    /* TODO: utiliser un angle global dépendant de LayoutDirection */
    Surface(
        shape = quadrilateralShape(
            cornerSizes(topStart = 32.dp, bottomEnd = 32.dp, default = 12.dp),
            angles(horizontal = MaterialTheme.shapes.angle)
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = stringResource(
                    id = if (viewModel.editCollection.id != 0L) R.string.edit_collection
                    else R.string.add_a_collection
                ),
                style = MaterialTheme.typography.h6,
                modifier = Modifier.rotate(MaterialTheme.shapes.angle)
            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Collection name") },
                shape = quadrilateralShape(
                    cornerSizes(4.dp),
                    angles(vertical = MaterialTheme.shapes.angle)
                ),
                modifier = Modifier.rotate(MaterialTheme.shapes.angle),
                textStyle = MaterialTheme.typography.body1
            )
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .rotate(MaterialTheme.shapes.angle)
                    .fillMaxWidth()
            ) {
                Button(
                    shape = quadrilateralShape(
                        cornerSizes(4.dp),
                        angles(vertical = MaterialTheme.shapes.angle)
                    ),
                    onClick = { navController.navigateUp() }
                ) {
                    Text("Cancel")
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    shape = quadrilateralShape(
                        cornerSizes(bottomEnd = 20.dp, default = 4.dp),
                        angles(vertical = MaterialTheme.shapes.angle)
                    ),
                    onClick = {
                        viewModel.editCollection.name = name
                        viewModel.saveCollection(
                            onComplete = {
                                onComplete(it)
                                navController.navigateUp()
                            })
                    }
                ) {
                    Text("Validate")
                }
            }
        }
    }
}