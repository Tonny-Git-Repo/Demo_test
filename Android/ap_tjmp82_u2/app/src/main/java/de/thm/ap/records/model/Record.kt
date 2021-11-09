package de.thm.ap.records.model

import java.io.Serializable


data class Record(var moduleNum: String , var moduleName: String , var year: Int , var isSummerTerm: Boolean ,
                  var isHalfWeighted: Boolean , var crp: Int, var mark: Int): Serializable {
    var id: Int? = null

    override fun toString(): String {
        super.toString()

        return "$moduleName $moduleNum (${mark}% ${crp}crp)"
    }
}