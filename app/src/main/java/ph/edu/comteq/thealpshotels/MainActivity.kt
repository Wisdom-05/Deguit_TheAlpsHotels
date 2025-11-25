package ph.edu.comteq.thealpshotels

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
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
                val navController = rememberNavController()
                var bookings by remember { mutableStateOf<List<Booking>>(emptyList()) }

                NavHost(navController = navController, startDestination = "home") {

                    // Home screen
                    composable("home") {
                        Homepage(
                            onProfileClick = { navController.navigate("profile") },
                            onHotelClick = { hotel ->
                                if (hotel.hotel_name == "Résidence Pierre & Vacances Premium les Crets") {
                                    val context = this@MainActivity
                                    val json = context.assets.open("hotels_details.1000.json")
                                        .bufferedReader().use { it.readText() }
                                    val gson = Gson()
                                    val hotelDetails =
                                        gson.fromJson(json, HotelDetails::class.java)
                                    val hotelDetailsJson =
                                        Uri.encode(Gson().toJson(hotelDetails))
                                    navController.navigate("details/$hotelDetailsJson")
                                }
                            }
                        )
                    }

                    // Profile screen
                    composable("profile") {
                        ProfileScreen(
                            onBackClick = { navController.popBackStack() },
                            onMyBookingsClick = { navController.navigate("myBookings") }
                        )
                    }

                    // Hotel details screen
                    composable(
                        route = "details/{hotelDetailsJson}",
                        arguments = listOf(navArgument("hotelDetailsJson") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val hotelDetailsJson =
                            backStackEntry.arguments?.getString("hotelDetailsJson") ?: ""
                        val hotelDetails =
                            Gson().fromJson(Uri.decode(hotelDetailsJson), HotelDetails::class.java)
                        HotelDetailsScreen(
                            hotelDetails = hotelDetails,
                            onBackClick = { navController.popBackStack() },
                            onRoomClick = { room, hotel ->
                                val bookingData = BookingData(room, hotel)
                                val bookingDataJson = Uri.encode(Gson().toJson(bookingData))
                                navController.navigate("booking/$bookingDataJson")
                            }
                        )
                    }

                    // Booking confirm screen
                    composable(
                        route = "booking/{bookingDataJson}",
                        arguments = listOf(navArgument("bookingDataJson") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val bookingDataJson =
                            backStackEntry.arguments?.getString("bookingDataJson") ?: ""
                        val bookingData =
                            Gson().fromJson(Uri.decode(bookingDataJson), BookingData::class.java)
                        BookingConfirmScreen(
                            hotelDetails = bookingData.hotelDetails,
                            room = bookingData.room,
                            onBackClick = { navController.popBackStack() },
                            onBookingComplete = { booking ->
                                bookings = bookings + booking
                                navController.navigate("myBookings") {
                                    popUpTo("home") { inclusive = false }
                                }
                            }
                        )
                    }

                    // My bookings screen
                    composable("myBookings") {
                        MyBookingsScreen(
                            bookings = bookings,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

// Helper data class for navigation
data class BookingData(
    val room: Room,
    val hotelDetails: HotelDetails
)

// ---------------- HOMEPAGE -----------------

@Composable
fun Homepage(onProfileClick: () -> Unit, onHotelClick: (Hotel) -> Unit) {
    val context = LocalContext.current
    var hotels by remember { mutableStateOf(emptyList<Hotel>()) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredHotels = hotels.filter {
        it.hotel_name.contains(searchQuery, ignoreCase = true)
    }

    LaunchedEffect(Unit) {
        val json = context.assets.open("hotels.json").bufferedReader().use { it.readText() }
        val gson = Gson()
        hotels = gson.fromJson(json, Array<Hotel>::class.java).toList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("The Alps' Hotels", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.france_national_flag),
                    contentDescription = "Flag",
                    modifier = Modifier.size(32.dp)
                )
            }

            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "Profile",
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onProfileClick() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search hotels...") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Hotel list
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(filteredHotels) { hotel ->
                HotelCard(hotel = hotel) { onHotelClick(hotel) }
            }
        }
    }
}

// ---------------- HOTEL CARD -----------------

@Composable
fun HotelCard(hotel: Hotel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/${hotel.hotel_cover_image}")
                    .crossfade(true)
                    .build(),
                contentDescription = hotel.hotel_name,
                placeholder = painterResource(R.drawable.ic_launcher_background),
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = hotel.hotel_name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val rating = hotel.hotel_rating.roundToInt()
                    repeat(rating) {
                        Icon(Icons.Filled.Star, null, tint = Color(0xFFFFA500), modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("%.1f".format(hotel.hotel_rating), fontSize = 14.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text("${hotel.hotel_to_ski_distance} km to ski lift", fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}

// ---------------- PROFILE SCREEN -----------------

@Composable
fun ProfileScreen(onBackClick: () -> Unit, onMyBookingsClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- Top Row (with Back Arrow, Title, Flag, Invisible Icon) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Back button
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onBackClick() }
                )

                Spacer(modifier = Modifier.width(12.dp))

                // App title
                Text(
                    text = "The Alps' Hotels",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(8.dp))

                // France flag
                Image(
                    painter = painterResource(id = R.drawable.france_national_flag),
                    contentDescription = "France Flag",
                    modifier = Modifier.size(32.dp)
                )
            }

            // Right-side user icon (optional)
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "Profile Icon",
                modifier = Modifier.size(32.dp)
            )
        }

        // Invisible icon to balance layout
        Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = Color.Transparent
        )
    }

    //  Centered Profile Info Section
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.wiz),
                contentDescription = "Profile Photo",
                modifier = Modifier
                    .size(130.dp)
                    .clip(RoundedCornerShape(65.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "John Wisdom Deguit",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Developer",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "John Wisdom Deguit is an IT student at COMTEQ Computer and Business College. " +
                        "This application was developed as part of the course, Mobile Applications Development, " +
                        "and was adopted from World Skills Lyon 2024.",
                fontSize = 16.sp,
                lineHeight = 22.sp,
                color = Color.DarkGray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onMyBookingsClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3F51B5)
                )
            ) {
                Text(
                    text = "My Bookings",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
