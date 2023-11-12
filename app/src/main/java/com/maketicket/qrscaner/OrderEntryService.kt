package com.maketicket.qrscaner

import com.maketicket.qrscaner.ui.body.EventBody
import com.maketicket.qrscaner.ui.model.*
import com.maketicket.qrscaner.ui.response.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface OrderEntryService {
    //https://www.makeidsystems.com/makeid/index.php?r=site/OrderTicketEntradaApi&code=62f7160e0ddb96.33472963
    @GET("index.php?r=site/OrderTicketEntradaApi") //listo
    suspend  fun getOrderEntry(@Query("code") code: String, @Query("key") key: String, @Query("id_event") id_event: Int, @Query("id_article") id_article:String ):Response<OrdenEntryResponse>
    @GET("code")
    fun getOrdercode(@Query("code") code: String):Response<List<String>>
    //https://www.makeidsystems.com/makeid/index.php?r=site/OrderTicketApi&code=62f7160e0ddb96.33472963
    @GET("index.php?r=site/OrderTicketApi") //no c toca
    suspend fun getOrderDetail(@Query("code") code: String, @Query("key") key: String): Response<OrderDetailResponse>

    //https://www.makeidsystems.com/makeid/index.php?r=site/OrderAdmittedApi&id_event=609&key=638b9001677a96.18151035&
    @GET("index.php?r=site/OrderAdmittedApi") // no se toca
    suspend fun getOrderAdmitted(@Query("id_event") id_event: Int, @Query("key") key: String): Response<OrderAdmittedResponse>

    //https://www.makeidsystems.com/makeid/index.php?r=site/OrderDetailApi&code=62fc1c233cc2f6.83658561&key=12345
    @GET("index.php?r=site/OrderDetailApi")
    suspend fun getDetail(@Query("code") code: String, @Query("key") key: String,  @Query("id_event") id_event: Int, @Query("id_article") id_article: String): Response<OrderDetailResponse>

    //https://www.makeidsystems.com/makeid/index.php?r=site/EventPartakerApi&key=639bdd8b8d3e94.89244283&id_event=559&code=764
    @GET("index.php?r=site/EventPartakerApi")  // no se toca
    suspend fun getEventPartaker(@Query("code") code: String, @Query("key") key: String,  @Query("id_event") id_event: Int): Response<OrderPartakerResponse>

    //https://www.makeidsystems.com/makeid/index.php?r=site/SpaceChair&key=63b4b3eba51e48.20916573&id_artilce=7012
    @GET("index.php?r=site/SpaceChair") //no se toca
    suspend fun getSillas(@Query("key") key: String,  @Query("id_artilce") id_artilce: Int): Response<ArrayList<Silla>>
    //https://www.makeidsystems.com/makeid/index.php?r=site/Sillaorder&key=63b4b3eba51e48.20916573&chair=399786
    @GET("index.php?r=site/Sillaorder")  //no se toca
    suspend fun getDetailSilla(@Query("key") key: String,  @Query("chair") chair: String): Response<DetailSilla>
    //https://www.makeidsystems.com/makeid/index.php?r=site/OrderArticles&key=63b4b3eba51e48.20916573&id_event=563&id_article=2677
    @GET("index.php?r=site/OrderArticles")  //modificado
    suspend fun getTicketByOrden(@Query("key") key: String,  @Query("id_event") id_event: Int,  @Query("id_article") id_article: String): Response<ArrayList<TicketByFunction>>
    //REPORTES
    //https://www.makeidsystems.com/makeid/index.php?r=site/ventaresumen&key=63b4b3eba51e48.20916573&id_event=563
    @GET("index.php?r=site/ventaresumen")// no se toca
    suspend fun reportVentas(@Query("key") key: String,  @Query("id_event") id_event: Int): Response<ReportVentas>
    //https://www.makeidsystems.com/makeid/index.php?r=site/incomesresumen&key=63b4b3eba51e48.20916573&id_event=563
    @GET("index.php?r=site/incomesresumen")//no se toca
    suspend fun reportIcomes(@Query("key") key: String,  @Query("id_event") id_event: Int): Response<ReportIcomes>
    //https://makeidsystems.com/makeid/index.php?r=site/ventarordenn&key=63b4b3eba51e48.20916573&id_event=641
    @GET("index.php?r=site/ventarordenn") // no se toca
    suspend fun reportDetailOrder(@Query("key") key: String,  @Query("id_event") id_event: Int): Response<ReportDetailOrder>

    //MESAS
    //https://www.makeidsystems.com/makeid/index.php?r=site/szone&key=63b4b3eba51e48.20916573&id_event=681
    @GET("index.php?r=site/szone") // no se toca
    suspend fun getMesaZonas(@Query("key") key: String,  @Query("id_event") id_event: Int): Response<ArrayList<MesaZona>>
    //https://www.makeidsystems.com/makeid/index.php?r=site/sTablez&key=63b4b3eba51e48.20916573&id_zone=838
    @GET("index.php?r=site/sTablez")  //no se toca
    suspend fun getMesaTablas(@Query("key") key: String,  @Query("id_zone") id_zone: Int): Response<ArrayList<MesaTabla>>
    //https://www.makeidsystems.com/makeid/index.php?r=site/Chairrr&key=63b4b3eba51e48.20916573&id_table=106330
    @GET("index.php?r=site/Chairrr")  // no se toca
    suspend fun getMesaSilla(@Query("key") key: String,  @Query("id_table") id_table: Int): Response<ArrayList<MesaSilla>>
    //https://www.makeidsystems.com/makeid/index.php?r=site/Sillaorder&key=63b4b3eba51e48.20916573&chair=399786
    @GET("index.php?r=site/Sillaorder")  //no se toca
    suspend fun getMesaOrden(@Query("key") key: String,  @Query("chair") chair: Int): Response<MesaOrden>
    //https://makeidsystems.com/makeid/index.php?r=site/orderRegistada&key=63b4b3eba51e48.20916573&id_event=673
    @GET("index.php?r=site/orderRegistada") //no se toca
    suspend fun getOrderEntry(@Query("key") key: String,  @Query("id_event") id_event: Int): Response<ArrayList<OrderEntry>>
    //https://makeidsystems.com/makeid/index.php?r=site/OrderIdentidad&key=63b4b3eba51e48.20916573&id_event=641&identidad=17340784
    @GET("index.php?r=site/OrderIdentidad")   //no se toca
    suspend fun getOrderDNI(@Query("key") key: String,  @Query("id_event") id_event: Int, @Query("identidad") identidad: String): Response<ArrayList<OrderDNI>>
    //https://makeidsystems.com/makeid/index.php?r=site/EspacioSillas&key=63b4b3eba51e48.20916573&id_article=6514
    @GET("index.php?r=site/EspacioSillas")  //no se toca
    suspend fun getEspacioSillas(@Query("key") key: String,  @Query("id_article") id_article: Int): Response<EspacioArray>
    //https://www.makeidsystems.com/makeid/index.php?r=site/OrderIngresada&id_event=680&key=63b4b3eba51e48.20916573
    @GET("index.php?r=site/OrderIngresada") //no se toca
    suspend fun  getOrderEntered(@Query("key") key: String,  @Query("id_event") id_event: Int):Response<OrderEnteredResponse>
    //https://www.makeidsystems.com/makeid/index.php?r=site/ZonaSillas&id_event=691&id_article=7289&key=63b4b3eba51e48.20916573
    @GET("index.php?r=site/ZonaSillas")  //no se toca la funcion
    suspend fun  getMesa(@Query("key") key: String,  @Query("id_event") id_event: Int,  @Query("id_article") id_article: Int):Response<SpaceMesaResponse>


    //new system
    //https://www.maketicket.com.ve/api/makeidsystems/external/event/list
    @GET("event/list")
    suspend fun  getEventList(@Header("client") client:String, @Header("clientKey") key:String):Response<ListEventResponse>
    //https://www.maketicket.com.ve/api/makeidsystems/external/banner/store
    @POST("banner/store")
    suspend fun  setBanner(@Header("client") client:String, @Header("clientKey") key:String, @Body eventBody: EventBody):Response<SetEventResponse>
    //https://www.maketicket.com.ve/api/makeidsystems/external/event/store
    @POST("event/store")
    suspend fun  setEvent(@Header("client") client:String, @Header("clientKey") key:String, @Body eventBody: EventBody):Response<SetBannerResponse>
}