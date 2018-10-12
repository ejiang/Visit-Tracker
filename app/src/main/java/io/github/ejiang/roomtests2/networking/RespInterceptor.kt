package io.github.ejiang.roomtests2.networking

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

private const val CLIENT_ID = "H5LFM4FEGETE42BLIGAK14Z31RECKOICMNEX1ZCBAQCHVDIT"
private const val CLIENT_SECRET = "FOJVCEV4MKKWTSR1TGWMXSYPIBXX4G1XKVC5S5GTNORQRK1V"
private const val API_VERSION = "20180323"

class RespInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val r : Request = chain.request()
        val u: HttpUrl = r.url().newBuilder().addQueryParameter("client_id", CLIENT_ID)
                .addQueryParameter("client_secret", CLIENT_SECRET)
                .addQueryParameter("v", API_VERSION)
                .build()

        val r2 : Request = chain.request().newBuilder().url(u).build()
        val res : Response = chain.proceed(r2)
        return res
    }
}
