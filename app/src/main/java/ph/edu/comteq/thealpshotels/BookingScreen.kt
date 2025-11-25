package ph.edu.comteq.thealpshotels

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    
    // Auto-calculate rooms based on total guests
    val totalGuests = adults + children
    val rooms = remember(totalGuests, room.room_total_number_of_guests) {
        if (totalGuests == 0) 1 else
            (totalGuests + room.room_total_number_of_guests - 1) / room.room_total_number_of_guests
    }
    
    // Auto-calculate price
    val price = remember(rooms, checkInDate, checkOutDate, travelPurpose) {
        calculatePrice(
            roomPrice = room.room_price_for_one_night,
            rooms = rooms,
            checkInDate = checkInDate,
            checkOutDate = checkOutDate,
            travelPurpose = travelPurpose
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Booking Confirm",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF3F51B5)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF212121)
                )
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
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "You are going to reserve:",
                fontSize = 16.sp,
                color = Color(0xFF616161)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = hotelDetails.hotel_name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Room details card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = room.room_type,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Bed: ${room.room_bed_type}",
                        fontSize = 14.sp,
                        color = Color(0xFF616161)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total number of guests: $totalGuests",
                            fontSize = 14.sp,
                            color = Color(0xFF616161)
                        )
                        Text(
                            text = "€ ${room.room_price_for_one_night.toInt()}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Form",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Name inputs
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
            
            // Date inputs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = checkInDate,
                    onValueChange = { newValue ->
                        // Allow free typing, but format when a valid date is entered
                        val formatted = parseAndFormatDate(newValue)
                        checkInDate = formatted ?: newValue
                    },
                    label = { Text("Check-in date") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    placeholder = { Text("09/10/2024") }
                )
                OutlinedTextField(
                    value = checkOutDate,
                    onValueChange = { newValue ->
                        // Allow free typing, but format when a valid date is entered
                        val formatted = parseAndFormatDate(newValue)
                        checkOutDate = formatted ?: newValue
                    },
                    label = { Text("Check-out date") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    placeholder = { Text("09/15/2024") }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Adults, Children, Rooms
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Adults", fontSize = 12.sp, color = Color(0xFF616161))
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { if (adults > 1) adults-- },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("$adults", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            IconButton(
                                onClick = { adults++ },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Children", fontSize = 12.sp, color = Color(0xFF616161))
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { if (children > 0) children-- },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("$children", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            IconButton(
                                onClick = { children++ },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Room", fontSize = 12.sp, color = Color(0xFF616161))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$rooms", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Travel purpose radio buttons
            Text(
                text = "Travel for business?",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF212121)
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
                    Text("For sightseeing", modifier = Modifier.padding(start = 4.dp))
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { travelPurpose = "For business with a meeting room + € 150" }
                ) {
                    RadioButton(
                        selected = travelPurpose == "For business with a meeting room + € 150",
                        onClick = { travelPurpose = "For business with a meeting room + € 150" }
                    )
                    Text("For business with a meeting room + € 150", modifier = Modifier.padding(start = 4.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Payment method radio buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Which way to pay?",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF212121)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { paymentMethod = "Cash" }
                    ) {
                        RadioButton(
                            selected = paymentMethod == "Cash",
                            onClick = { paymentMethod = "Cash" }
                        )
                        Text("Cash", modifier = Modifier.padding(start = 4.dp))
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { paymentMethod = "Credit card" }
                    ) {
                        RadioButton(
                            selected = paymentMethod == "Credit card",
                            onClick = { paymentMethod = "Credit card" }
                        )
                        Text("Credit card", modifier = Modifier.padding(start = 4.dp))
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { paymentMethod = "E-Pay" }
                    ) {
                        RadioButton(
                            selected = paymentMethod == "E-Pay",
                            onClick = { paymentMethod = "E-Pay" }
                        )
                        Text("E-Pay", modifier = Modifier.padding(start = 4.dp))
                    }
                }
                
                // Price display
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(
                        text = "€ ${price.toInt()}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Book now button
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
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3F51B5)
                )
            ) {
                Text(
                    text = "Book now",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

fun parseAndFormatDate(input: String): String? {
    if (input.isEmpty() || input.length < 4) return null
    
    // Check if already in target format (e.g., "Tue, Sep 10, 2024")
    val targetFormat = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.US)
    try {
        targetFormat.parse(input)
        return input // Already in correct format
    } catch (_: Exception) {
        // Continue to try other formats
    }
    
    // Try format: 09/10/2024
    try {
        val format1 = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        format1.isLenient = false
        val date = format1.parse(input)
        if (date != null) {
            return targetFormat.format(date)
        }
    } catch (_: Exception) {
        // Continue
    }
    
    // Try format: 09-10-2024
    try {
        val format2 = SimpleDateFormat("MM-dd-yyyy", Locale.US)
        format2.isLenient = false
        val date = format2.parse(input)
        if (date != null) {
            return targetFormat.format(date)
        }
    } catch (_: Exception) {
        // Continue
    }
    
    // Try format: Sep 10 2024
    try {
        val format3 = SimpleDateFormat("MMM dd yyyy", Locale.US)
        format3.isLenient = false
        val date = format3.parse(input)
        if (date != null) {
            return targetFormat.format(date)
        }
    } catch (_: Exception) {
        // Continue
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
            
            // Add business meeting room fee
            if (travelPurpose == "For business with a meeting room + € 150") {
                totalPrice += 150.0
            }
            
            return totalPrice
        }
    } catch (_: Exception) {
        // Return base price if date parsing fails
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
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My bookings",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF3F51B5)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF212121)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "List of my bookings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (bookings.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No bookings yet",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(bookings) { index, booking ->
                        BookingCard(
                            bookingNumber = index + 1,
                            booking = booking
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BookingCard(bookingNumber: Int, booking: Booking) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$bookingNumber",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${booking.firstName} ${booking.lastName}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Text(
                    text = booking.hotelName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${booking.checkInDate} to ${booking.checkOutDate}",
                    fontSize = 14.sp,
                    color = Color(0xFF616161)
                )
                Text(
                    text = "${booking.adults} Adults, ${booking.children} Children, ${booking.rooms} Room",
                    fontSize = 14.sp,
                    color = Color(0xFF616161)
                )
                Text(
                    text = "${booking.travelPurpose} Pay with ${booking.paymentMethod.lowercase()}",
                    fontSize = 14.sp,
                    color = Color(0xFF616161)
                )
            }
            
            Text(
                text = "€ ${booking.price.toInt()}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}
