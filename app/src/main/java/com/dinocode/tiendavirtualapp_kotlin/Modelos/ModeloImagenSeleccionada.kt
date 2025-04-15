package com.dinocode.tiendavirtualapp_kotlin.Modelos

import android.net.Uri

class ModeloImagenSeleccionada {

    var id = ""
    var imagenUri : Uri ?= null
    var imagenUrl : String ?=null
    var deInternet = false

    constructor()

    constructor(id: String, imagenUri: Uri?, imagenUrl: String?, deInternet: Boolean) {
        this.id = id
        this.imagenUri = imagenUri
        this.imagenUrl = imagenUrl
        this.deInternet = deInternet
    }


}