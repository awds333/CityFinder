package com.fedchanka.cityfinder.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fedchanka.cityfinder.R
import com.fedchanka.cityfinder.model.domain.City
import com.fedchanka.cityfinder.ui.theme.CityFinderTheme
import com.fedchanka.cityfinder.ui.theme.Read
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CityFinderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val viewModel = getViewModel<MainViewModel>()
                    val mainState = viewModel.mainState.collectAsState()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        CoordinatesInput(
                            coordinatesInputState = mainState.value.coordinatesInputState,
                            onCoordinatesChanged = { coordinates ->
                                viewModel.coordinatesChanged(
                                    coordinates
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        LazyColumn {
                            items(mainState.value.foundCities) { city ->
                                Box(modifier = Modifier.padding(5.dp)) {
                                    CityComposable(city = city)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CoordinatesInput(
    coordinatesInputState: CoordinatesInputState,
    onCoordinatesChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        OutlinedTextField(
            value = coordinatesInputState.raw,
            onValueChange = onCoordinatesChanged,
            label = { Text(text = stringResource(id = R.string.coordinates)) },
            trailingIcon = {
                when (coordinatesInputState.searchState) {
                    is SearchState.Normal -> Icon(
                        painter = painterResource(id = android.R.drawable.ic_dialog_map),
                        contentDescription = "Map icon"
                    )
                    is SearchState.InProgress -> CircularProgressIndicator()
                    is SearchState.Error -> {/*Error is read enough*/
                    }
                }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(errorBorderColor = Read),
            isError = coordinatesInputState.searchState is SearchState.Error,
            maxLines = 1,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = modifier
        )
        when (coordinatesInputState.searchState) {
            is SearchState.Error -> Text(
                text = stringResource(id = coordinatesInputState.searchState.message),
                color = Read
            )
            is SearchState.NotFound -> Text(
                text = stringResource(id = R.string.nothing_for_this_coordinates),
            )
        }
    }
}

@Composable
fun CityComposable(city: City) {
    Card(
        modifier = Modifier.shadow(elevation = 6.dp)
    ) {
        Text(
            text = city.name,
            fontSize = 24.sp,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CityComposable(city = City("London"))
}