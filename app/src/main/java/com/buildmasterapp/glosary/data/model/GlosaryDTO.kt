package com.buildmasterapp.glosary.data.model

data class GlosaryTermDTO(val id: String, val termino: String, val definicion: String, val ejemplos: List<String>)
data class GuideDTO(val id: String, val categoria: String, val contenido: String)
data class ContextTipDTO(val paso: String, val mensaje: String, val linkGuia: String?, val linkGlosario: String?)

