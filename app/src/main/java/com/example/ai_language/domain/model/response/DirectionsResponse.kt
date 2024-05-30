package com.example.ai_language.domain.model.response


data class DirectionsResponse(
    val geocoded_waypoints: List<GeocodedWaypoint> = emptyList(),
    val routes: List<Route> = emptyList(),
    val status: String = ""
)

data class GeocodedWaypoint(
    val geocoder_status: String = "",
    val partial_match: Boolean = false,
    val place_id: String = "",
    val types: List<String> = emptyList()
)

data class Route(
    val bounds: Bounds = Bounds(),
    val copyrights: String = "",
    val legs: List<Leg> = emptyList(),
    val overview_polyline: Polyline = Polyline(),
    val summary: String = "",
    val warnings: List<String> = emptyList(),
    val waypoint_order: List<Int> = emptyList()
)

data class Bounds(
    val northeast: Location = Location(),
    val southwest: Location = Location()
)

data class Leg(
    val arrival_time: Time = Time(),
    val departure_time: Time = Time(),
    val distance: Distance = Distance(),
    val duration: Duration = Duration(),
    val end_address: String = "",
    val end_location: Location = Location(),
    val start_address: String = "",
    val start_location: Location = Location(),
    val steps: List<Step> = emptyList(),
    val traffic_speed_entry: List<Any> = emptyList(),
    val via_waypoint: List<Any> = emptyList()
)

data class Step(
    val distance: Distance = Distance(),
    val duration: Duration = Duration(),
    val end_location: Location = Location(),
    val html_instructions: String = "",
    val polyline: Polyline = Polyline(),
    val start_location: Location = Location(),
    var travel_mode: String = "",
    val transit_details: TransitDetails = TransitDetails(),
    val steps: List<Step> = emptyList(),
    var img:Int = 0
)

data class Time(
    val text: String = "",
    val time_zone: String = "",
    val value: Long = 0
)

data class Distance(
    val text: String = "",
    val value: Int = 0
)

data class Duration(
    val text: String = "",
    val value: Int = 0
)

data class Polyline(
    val points: String = ""
)

data class Location(
    val lat: Double = 0.0,
    val lng: Double = 0.0
)

data class TransitDetails(
    val arrival_stop: Stop = Stop(),
    val arrival_time: Time = Time(),
    val departure_stop: Stop = Stop(),
    val departure_time: Time = Time(),
    val headsign: String = "",
    val headway: Int = 0,
    val line: Line = Line(),
    val num_stops: Int = 0
)

data class Stop(
    val location: Location = Location(),
    val name: String = ""
)

data class Line(
    val agencies: List<Agency> = emptyList(),
    val color: String = "",
    val name: String = "",
    val short_name: String = "",
    val text_color: String = "",
    val vehicle: Vehicle = Vehicle()
)

data class Agency(
    val name: String = "",
    val url: String = ""
)

data class Vehicle(
    val icon: String = "",
    val name: String = "",
    val type: String = ""
)
