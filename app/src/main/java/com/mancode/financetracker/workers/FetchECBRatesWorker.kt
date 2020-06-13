package com.mancode.financetracker.workers

import android.content.Context
import android.util.Log
import android.util.Xml
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.mancode.financetracker.database.entity.CurrencyEntity
import com.mancode.financetracker.ui.prefs.PreferenceAccessor
import com.mancode.financetracker.utils.InjectorUtils
import okhttp3.*
import org.threeten.bp.LocalDate
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.Reader

fun AppCompatActivity.fetchExchangeRates() {
    val date = PreferenceAccessor.ratesFetchDate
    if (date != null && date.isEqual(LocalDate.now())) return
    val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    val fetchWork = OneTimeWorkRequest.Builder(FetchECBRatesWorker::class.java)
            .setConstraints(constraints)
            .build()
    WorkManager.getInstance(applicationContext).enqueue(fetchWork)
}

class FetchECBRatesWorker(context: Context, params: WorkerParameters)
    : Worker(context, params) {

    override fun doWork(): Result {

        val httpClient = OkHttpClient()
        val url = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml"

        val request = Request.Builder()
                .url(url)
                .build()
        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("FetchECBRatesWorker", "Fetching exchange rates from ECB failed")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStream = response.body?.charStream()
                    if (responseStream != null) {
                        val repo = InjectorUtils.getCurrencyRepository(applicationContext)
                        val currencyList = ECBXmlParser().parse(responseStream)
                        currencyList.add(CurrencyEntity("EUR", 1.0, null))
                        repo.insertAll(currencyList)
                        PreferenceAccessor.ratesFetchDate = LocalDate.now()
                    }
                }
            }
        })
        return Result.success()
    }

    class ECBXmlParser {

        private val ns: String? = null

        @Throws(XmlPullParserException::class, IOException::class)
        fun parse(stream: Reader): MutableList<CurrencyEntity> {
            stream.use {
                val parser: XmlPullParser = Xml.newPullParser()
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                parser.setInput(stream)
                parser.nextTag()
                return readXml(parser)
            }
        }

        private fun readXml(parser: XmlPullParser): MutableList<CurrencyEntity> {
            val currencies = mutableListOf<CurrencyEntity>()

            parser.require(XmlPullParser.START_TAG, ns, "gesmes:Envelope")
            var date: LocalDate? = null
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.eventType != XmlPullParser.START_TAG) continue
                if (parser.name == "Cube") {
                    if (parser.attributeCount != 0) {
                        if (parser.getAttributeName(0) == "time") {
                            date = LocalDate.parse(parser.getAttributeValue(0))
                        } else if (parser.getAttributeName(0) == "currency") {
                            currencies.add(readRate(parser, date))
                        }
                    } else continue
                } else {
                    skip(parser)
                }
            }
            return currencies
        }

        private fun readRate(parser: XmlPullParser, date: LocalDate?): CurrencyEntity {
            parser.require(XmlPullParser.START_TAG, ns, "Cube")
            val currencySymbol = parser.getAttributeValue(null, "currency")
            val rate = parser.getAttributeValue(null, "rate").toDouble()
            parser.nextTag()
            return CurrencyEntity(currencySymbol, rate, date)
        }

        private fun skip(parser: XmlPullParser) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                throw IllegalStateException()
            }
            var depth = 1
            while (depth != 0) {
                when (parser.next()) {
                    XmlPullParser.END_TAG -> depth--
                    XmlPullParser.START_TAG -> depth++
                }
            }
        }
    }
}