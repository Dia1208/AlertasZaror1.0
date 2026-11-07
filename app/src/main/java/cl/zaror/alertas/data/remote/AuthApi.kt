package cl.zaror.alertas.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

data class LoginRequest(
    val telefono: String,
    val clave: String
)

data class RegistroRequest(
    val nombre: String,
    val telefono: String,
    val direccion: String,
    val clave: String
)

data class TokenResponse(
    val token: String,
    val tipo: String,
    val expiraEnSegundos: Long
)

data class UsuarioResponse(
    val id: Long,
    val nombre: String,
    val telefono: String,
    val rol: String,
    val estado: String
)

interface AuthApi {

    @POST("api/auth/login")
    suspend fun login(
        @Body datos: LoginRequest
    ): TokenResponse

    @POST("api/auth/registro")
    suspend fun registrar(
        @Body datos: RegistroRequest
    ): UsuarioResponse

    @GET("api/auth/yo")
    suspend fun obtenerPerfil(
        @Header("Authorization") autorizacion: String
    ): UsuarioResponse
}