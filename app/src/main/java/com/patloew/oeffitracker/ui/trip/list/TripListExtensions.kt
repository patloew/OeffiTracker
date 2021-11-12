package com.patloew.oeffitracker.ui.trip.list

import com.patloew.oeffitracker.data.model.Trip
import com.patloew.oeffitracker.ui.distanceFormat
import com.patloew.oeffitracker.ui.formatDurationShort

val Trip.hasAdditionalInfos: Boolean
    get() = distance != null || duration != null || delay != null

val Trip.additionalInfo: String
    get() = buildString {
        if (distance != null) {
            append(distanceFormat.format(distance))
        }
        if (duration != null) {
            if (distance != null) append(' ')
            append(formatDurationShort(duration))
        }
        if (delay != null) {
            if (distance != null && duration == null) append(' ')
            append('+').append(formatDurationShort(delay))
        }
    }
