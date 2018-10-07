package jp.cordea.mackerelclient.api.response

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type

class Tsdbs(val tsdbs: Map<String, Map<String, Tsdb>>)

class Tsdb(val metricValue: Float?)

class TsdbsDeserializer : JsonDeserializer<Tsdbs> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Tsdbs {
        val ts = HashMap<String, Map<String, Tsdb>>()
        json ?: return Tsdbs(ts)
        if (json.asJsonObject.has("tsdbLatest")) {
            val tsObj = (json as JsonObject).get("tsdbLatest") as JsonObject
            for (tso in tsObj.entrySet()) {
                val values = HashMap<String, Tsdb>()
                for (tsoo in tso.value.asJsonObject.entrySet()) {
                    (tsoo.value as? JsonObject)?.let {
                        it.get("value").asFloat.let {
                            values.put(tsoo.key, Tsdb(it))
                        }
                    }
                }
                ts.put(tso.key, values)
            }
        }
        return Tsdbs(ts)
    }
}
