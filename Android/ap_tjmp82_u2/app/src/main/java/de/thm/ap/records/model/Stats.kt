package de.thm.ap.records.model

import android.util.Log
import android.widget.Toast
import de.thm.ap.records.RecordFormActivity


class Stats(var recordsStat: List<Record>) {

    var sumCrp = 0
    var crpToEnd = 156
    var sumHalfWeighted = 0
    var averageMark= 0
    var listSize = recordsStat.size


    fun setDateForSats(){
        for(item in recordsStat){
            if(item.isHalfWeighted){
                sumHalfWeighted++

            }
            sumCrp += item.crp
        }

    }

    fun calculateAverageM() : Int {
        var sum = 0
        var avg  = 0
        var crp = 0
        recordsStat.let { it ->
            it.forEach { sum += if (it.isHalfWeighted) ((it.mark * it.crp) / 2 ) else (it.mark * it.crp)
                crp += if (it.isHalfWeighted) (it.crp / 2 ) else it.crp
            }
            avg = if (sum > 0) sum / crp else 0
        }
        return avg
    }
    init {
        setDateForSats()
        averageMark = calculateAverageM()
    }

    override fun toString(): String {
        super.toString()

        return "Leistungen  ${listSize}\n50% Leistungen ${sumHalfWeighted}\nSumme Crp ${sumCrp}\n" +
                "Crp bis Ziel ${crpToEnd}\nDurchschnitt ${averageMark}%"
    }
}