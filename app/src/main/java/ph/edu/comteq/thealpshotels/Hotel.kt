package ph.edu.comteq.thealpshotels

data class Hotel(
    val hotel_id: Int,
    val hotel_name: String,
    val hotel_rating: Double,
    val hotel_to_ski_distance: Double,
    val hotel_cover_image: String
)

data class HotelDetails(
    val hotel_id: Int,
    val hotel_name: String,
    val guest_reviews: GuestReviews,
    val rooms: List<Room>
)

data class GuestReviews(
    val ratings_categories: List<Map<String, Double>>,
    val reviews_objects: List<ReviewObject>
)

data class ReviewObject(
    val username: String,
    val country: String,
    val review_text: String
)

data class Room(
    val room_id: Int,
    val room_type: String,
    val room_bed_type: String,
    val room_total_number_of_guests: Int,
    val room_features: List<String>,
    val room_price_for_one_night: Double
)

data class Booking(
    val bookingId: Int,
    val firstName: String,
    val lastName: String,
    val hotelName: String,
    val roomType: String,
    val checkInDate: String,
    val checkOutDate: String,
    val adults: Int,
    val children: Int,
    val rooms: Int,
    val travelPurpose: String, // "For sightseeing" or "For business with a meeting room"
    val paymentMethod: String, // "Cash", "Credit card", or "E-Pay"
    val price: Double
)