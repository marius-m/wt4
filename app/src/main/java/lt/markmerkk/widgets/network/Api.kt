package lt.markmerkk.widgets.network

import okhttp3.ResponseBody
import retrofit2.http.*
import rx.Single

interface Api {
    @GET("master/CHANGELOG.mdasdf")
    fun changelog(): Single<ResponseBody>
}