package io.github.yitzy299.ledgerblockbotlink

import com.google.gson.JsonParser
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists

class Config {
    var listeners: MutableList<Listener> = mutableListOf()

    fun readConfig(path: Path): Config {
        val config = Config()

        if (path.exists()) {
            val json = JsonParser().parse(Files.readString(path)).asJsonArray

            json.forEach {
                val jsonObject = it.asJsonObject
                val block = jsonObject.get("block").asString
                val actions = jsonObject.getAsJsonArray("actions")
                val actionsList = mutableListOf<String>()
                actions.forEach { element ->
                    actionsList.add(element.asString)
                }

                listeners.add(Listener(actionsList, block))

            }
        }

        return config
    }
}