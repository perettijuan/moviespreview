package com.jpp.mpdata.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.jpp.mpdomain.MovieStateRate
import java.lang.reflect.Type

/**
 * [JsonDeserializer] used to map the [MovieStateRate] obtained from the backend.
 * Since the value in [MovieState] can assume different types, we're forced to add
 * a custom deserializer to parse the proper value.
 */
internal class MovieStateRateJsonDeserializer : JsonDeserializer<MovieStateRate> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): MovieStateRate {
        return try {
            MovieStateRate(
                    isRated = true,
                    value = json.asJsonObject.get("value").asString
            )
        } catch (e: Exception) {
            MovieStateRate(isRated = false)
        }
    }
}
