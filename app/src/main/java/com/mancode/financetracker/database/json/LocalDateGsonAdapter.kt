package com.mancode.financetracker.database.json

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.mancode.financetracker.database.converter.DateConverter

import org.threeten.bp.LocalDate

import java.io.IOException

class LocalDateGsonAdapter : TypeAdapter<LocalDate>() {
    @Throws(IOException::class)
    override fun write(writer: JsonWriter, value: LocalDate?) {
        if (value == null) {
            writer.nullValue()
            return
        }
        writer.value(DateConverter.toString(value))

    }

    @Throws(IOException::class)
    override fun read(reader: JsonReader): LocalDate? {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return null
        }
        return DateConverter.toDate(reader.nextString())
    }
}
