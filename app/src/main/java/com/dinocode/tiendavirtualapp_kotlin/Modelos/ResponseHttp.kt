package com.dinocode.tiendavirtualapp_kotlin.Modelos

import com.google.gson.annotations.SerializedName

class ResponseHttp (

    @SerializedName("message") val message : String,
    @SerializedName("success") val isSuccess : Boolean,
    @SerializedName("error") val error : String,
    @SerializedName("preferenceId") val preferenceId : String,
    @SerializedName("init_point") val init_point : String

) {
    override fun toString(): String {
        return "ResponseHttp(message='$message', isSuccess=$isSuccess, error='$error', preferenceId='$preferenceId', init_point='$init_point')"
    }
}