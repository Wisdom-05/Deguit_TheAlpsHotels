package ph.edu.comteq.thealpshotels

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.gson.Gson
import ph.edu.comteq.thealpshotels.ui.theme.TheAlpsHotelsTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TheAlpsHotelsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Homepage(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Homepage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var hotels by remember { mutableStateOf(emptyList<Hotel>()) }
    var searchQuery by remember { mutableStateOf("") }
    val filteredHotels = hotels.filter { hotel ->
        hotel.hotel_name.contains(searchQuery, ignoreCase = true)
    }

    //load json data
    LaunchedEffect(Unit) {
        val json = context.assets.open("hotels.json")
            .bufferedReader()
            .use { it.readText()}
        val gson = Gson()
        val hotelArray = gson.fromJson(json, Array<Hotel>::class.java)
        hotels = hotelArray.toList()
    }
    // Main container
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: title
            Text(
                text = "The Alp's Hotels",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold

            )
            Image(
                painter = painterResource(id = R.drawable.france_national_flag),
                contentDescription = "France Flag",
                modifier = Modifier
                    .size(45.dp)
                    .width(20.dp)
            )
            // Right side: logo
            Spacer(modifier = Modifier.width(90.dp))
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "User Icon",
                modifier = Modifier.width(45.dp)
            )
        }
        //search box
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {searchQuery = it},
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            placeholder = {Text("Search...")},
            singleLine = true
        )
        //hotel list

        LazyColumn (
            modifier = Modifier.fillMaxSize()
        ){
            items(filteredHotels) { hotel ->
                HotelCard(hotel)

            }
        }
    }
}

@Composable
fun HotelCard(hotel: Hotel){
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ){
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            //Hotel Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/${hotel.hotel_cover_image}")
                    .crossfade(true)
                    .build(),
                contentDescription = hotel.hotel_name,
                placeholder = painterResource(R.drawable.ic_launcher_background),
                modifier = Modifier.size(120.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            //hotel info
            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 13.dp)
            ) {
                Text(
                    text = hotel.hotel_name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    val rating = hotel.hotel_rating.roundToInt()
                    Text(
                        text = hotel.hotel_rating.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold

                    )
                    // stars
                    repeat(4){
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Star",
                            tint = Color.Yellow,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = "${hotel.hotel_to_ski_distance} km from Alp's ski lift",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomepagePreview() {
    TheAlpsHotelsTheme {
        Homepage()
    }
}