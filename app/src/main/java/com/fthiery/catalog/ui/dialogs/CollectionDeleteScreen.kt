package com.fthiery.catalog.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fthiery.catalog.ui.baselevel.angles
import com.fthiery.catalog.ui.baselevel.cornerSizes
import com.fthiery.catalog.ui.baselevel.quadrilateralShape
import com.fthiery.catalog.viewmodels.CollectionViewModel

@Composable
fun CollectionDeleteScreen(
    viewModel: CollectionViewModel,
    navController: NavController,
    onComplete: (Long) -> Unit = {}
) {
    val angle = -6f
    Surface(
        shape = quadrilateralShape(
            cornerSizes(topStart = 32.dp, bottomEnd = 32.dp, default = 12.dp),
            angles(horizontal = angle)
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Delete the collection ${viewModel.editCollection.name}",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.rotate(angle)
            )
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .rotate(angle)
                    .fillMaxWidth()
            ) {
                Button(
                    shape = quadrilateralShape(cornerSizes(4.dp), angles(vertical = angle)),
                    onClick = { navController.navigateUp() }
                ) {
                    Text("Cancel")
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    shape = quadrilateralShape(
                        cornerSizes(bottomEnd = 20.dp, default = 4.dp),
                        angles(vertical = angle)
                    ),
                    colors = buttonColors(backgroundColor = MaterialTheme.colors.error),
                    onClick = {
                        /* TODO: Sauvegarde la collection au lieu de l'effacer */
                        viewModel.saveCollection(
                            onComplete = {
                                onComplete(it)
                                navController.navigateUp()
                            })
                    }
                ) {
                    Text("Delete")
                }
            }
        }
    }
}