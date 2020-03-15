package lt.markmerkk.widgets.network

import okhttp3.ResponseBody
import retrofit2.http.*
import rx.Single

interface Api {
    @GET("/changelog")
    fun changelog(): Single<ResponseBody>
}