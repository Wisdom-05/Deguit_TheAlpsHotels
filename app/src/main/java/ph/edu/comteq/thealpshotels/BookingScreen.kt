package ph.edu.comteq.thealpshotels

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingConfirmScreen(
    hotelDetails: HotelDetails,
    room: Room,
    onBackClick: () -> Unit,
    onBookingComplete: (Booking) -> Unit
) {
    var firstName by remember { mutableStateOf("First Name") }
    var lastName by remember { mutableStateOf("Last Name") }
    var checkInDate by remember { mutableStateOf("Tue, Sep 10, 2024") }
    var checkOutDate by remember { mutableStateOf("Sun, Sep 15, 2024") }
    var adults by remember { mutableIntStateOf(2) }
    var children by remember { mutableIntStateOf(0) }
    var travelPurpose by remember { mutableStateOf("For sightseeing") }
    var paymentMethod by remember { mutableStateOf("Cash") }

    val totalGuests = adults + children
    val rooms = remember(totalGuests, room.room_total_number_of_guests) {
        if (totalGuests == 0) 1 else (totalGuests + room.room_total_number_of_guests - 1) / room.room_total_number_of_guests
    }

    val price = remember(rooms, checkInDate, checkOutDate, travelPurpose) {
        calculatePrice(
            roomPrice = room.room_price_for_one_night,
            rooms = rooms,
            checkInDate = checkInDate,
            checkOutDate = checkOutDate,
            travelPurpose = travelPurpose
        )
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Confirm Booking",
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "You are about to reserve:",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = hotelDetails.hotel_name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = room.room_type,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Bed: ${room.room_bed_type}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total guests: $totalGuests",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "€ ${room.room_price_for_one_night.toInt()}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Your Details",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = checkInDate,
                    onValueChange = { newValue ->
                        val formatted = parseAndFormatDate(newValue)
                        checkInDate = formatted ?: newValue
                    },
                    label = { Text("Check-in date") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    placeholder = { Text("MM/DD/YYYY") }
                )
                OutlinedTextField(
                    value = checkOutDate,
                    onValueChange = { newValue ->
                        val formatted = parseAndFormatDate(newValue)
                        checkOutDate = formatted ?: newValue
                    },
                    label = { Text("Check-out date") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    placeholder = { Text("MM/DD/YYYY") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CounterCard("Adults", adults, { if (adults > 1) adults-- }, { adults++ }, Modifier.weight(1f))
                CounterCard("Children", children, { if (children > 0) children-- }, { children++ }, Modifier.weight(1f))

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Room(s)", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$rooms", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Are you travelling for business?",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { travelPurpose = "For sightseeing" }
                ) {
                    RadioButton(
                        selected = travelPurpose == "For sightseeing",
                        onClick = { travelPurpose = "For sightseeing" }
                    )
                    Text("No, for leisure", modifier = Modifier.padding(start = 4.dp))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { travelPurpose = "For business with a meeting room + € 150" }
                ) {
                    RadioButton(
                        selected = travelPurpose == "For business with a meeting room + € 150",
                        onClick = { travelPurpose = "For business with a meeting room + € 150" }
                    )
                    Text("Yes (+ €150)", modifier = Modifier.padding(start = 4.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Payment Method",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf("Cash", "Credit Card", "E-Pay").forEach { method ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { paymentMethod = method }
                        ) {
                            RadioButton(
                                selected = paymentMethod == method,
                                onClick = { paymentMethod = method }
                            )
                            Text(method, modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End,
                ) {
                    Text("Total Price", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "€ ${price.toInt()}",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (validateBooking(firstName, lastName, checkInDate, checkOutDate)) {
                        val booking = Booking(
                            bookingId = System.currentTimeMillis().toInt(),
                            firstName = firstName,
                            lastName = lastName,
                            hotelName = hotelDetails.hotel_name,
                            roomType = room.room_type,
                            checkInDate = checkInDate,
                            checkOutDate = checkOutDate,
                            adults = adults,
                            children = children,
                            rooms = rooms,
                            travelPurpose = travelPurpose,
                            paymentMethod = paymentMethod,
                            price = price
                        )
                        onBookingComplete(booking)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Book Now",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CounterCard(
    label: String,
    value: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDecrement, modifier = Modifier.size(32.dp)) {
                    Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Text("$value", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                IconButton(onClick = onIncrement, modifier = Modifier.size(32.dp)) {
                    Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

fun parseAndFormatDate(input: String): String? {
    if (input.isEmpty() || input.length < 4) return null

    val targetFormat = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.US)
    try {
        targetFormat.parse(input)
        return input
    } catch (_: Exception) {
    }

    val formats = listOf(
        SimpleDateFormat("MM/dd/yyyy", Locale.US),
        SimpleDateFormat("MM-dd-yyyy", Locale.US),
        SimpleDateFormat("MMM dd yyyy", Locale.US)
    )

    for (format in formats) {
        try {
            format.isLenient = false
            val date = format.parse(input)
            if (date != null) {
                return targetFormat.format(date)
            }
        } catch (_: Exception) {
        }
    }

    return null
}

fun calculatePrice(
    roomPrice: Double,
    rooms: Int,
    checkInDate: String,
    checkOutDate: String,
    travelPurpose: String
): Double {
    val dateFormat = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.US)
    try {
        val checkIn = dateFormat.parse(checkInDate)
        val checkOut = dateFormat.parse(checkOutDate)

        if (checkIn != null && checkOut != null && checkOut.after(checkIn)) {
            val days = ((checkOut.time - checkIn.time) / (1000 * 60 * 60 * 24)).toInt()
            var totalPrice = roomPrice * rooms * days

            if (travelPurpose.contains("business", ignoreCase = true)) {
                totalPrice += 150.0
            }

            return totalPrice
        }
    } catch (_: Exception) {
    }

    return roomPrice * rooms
}

fun validateBooking(
    firstName: String,
    lastName: String,
    checkInDate: String,
    checkOutDate: String
): Boolean {
    if (firstName.isEmpty() || firstName == "First Name") return false
    if (lastName.isEmpty() || lastName == "Last Name") return false

    val dateFormat = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.US)
    try {
        val checkIn = dateFormat.parse(checkInDate)
        val checkOut = dateFormat.parse(checkOutDate)

        if (checkIn == null || checkOut == null) return false
        if (!checkOut.after(checkIn)) return false
    } catch (_: Exception) {
        return false
    }

    return true
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    bookings: List<Booking>,
    onBackClick: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "My Bookings",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        if (bookings.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.EventBusy,
                        contentDescription = "No bookings",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "You have no bookings yet.",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Your reservations will appear here once you've made a booking.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(bookings) { _, booking ->
                    BookingCard(booking = booking)
                }
            }
        }
    }
}

@Composable
fun BookingCard(booking: Booking) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = booking.hotelName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Booking #${booking.bookingId.toString().takeLast(4)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "€${booking.price.toInt()}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            InfoRowWithIcon(Icons.Outlined.Person, "Guest", "${booking.firstName} ${booking.lastName}")
            InfoRowWithIcon(Icons.Outlined.CalendarToday, "Dates", "${booking.checkInDate} to ${booking.checkOutDate}")
            InfoRowWithIcon(Icons.Outlined.Group, "Guests", "${booking.adults} Adults, ${booking.children} Children (${booking.rooms} Room/s)")
            InfoRowWithIcon(Icons.Outlined.WorkOutline, "Purpose", booking.travelPurpose)
            InfoRowWithIcon(Icons.Outlined.CreditCard, "Payment", "Paid with ${booking.paymentMethod}")
        }
    }
}

@Composable
private fun InfoRowWithIcon(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
