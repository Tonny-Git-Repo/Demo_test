package de.thm.ap.records

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import de.thm.ap.records.databinding.ActivityRecordFormBinding
import de.thm.ap.records.model.Record
import de.thm.ap.records.persistence.RecordDAO
import android.R.layout.simple_dropdown_item_1line
import android.R.layout.simple_spinner_dropdown_item
import android.text.Editable
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.lang.Exception

class RecordFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordFormBinding

    var record = Record("", "", 2015, false, false, 0 , 0)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecordFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // configure suggestions in auto complete text view

        val names = resources.getStringArray(R.array.module_names)
        val adapter = ArrayAdapter(this, simple_dropdown_item_1line, names)
        binding.moduleName.setAdapter(adapter)
        binding.moduleName.onFocusChangeListener = View.OnFocusChangeListener{ v, hasFocus  ->
            binding.moduleName.showDropDown()
        }

        // configure year spinner
        binding.year.adapter = ArrayAdapter(this, simple_spinner_dropdown_item, getYears())

        //Check if it is a record to Update
        record.id = if(intent.getSerializableExtra("idRecordToUpdate")!= null)
        {(intent.getSerializableExtra("idRecordToUpdate")).toString().toInt()} else {null}

        //Set the input Values of the formulary for a record to update
        if(record.id != null){
            var record = RecordDAO.get(this).findById(record.id!!)
            binding.moduleNum.setText(record?.moduleNum)
            binding.moduleName.setText(record?.moduleName)
            binding.isHalfWeighted.isChecked = record?.isHalfWeighted == true
            binding.isSummerTerm.isChecked = record?.isSummerTerm == true
            if (record != null) {
                binding.crp.setText(record.crp.toString())
                binding.mark.setText(record.mark.toString())
            }

            //Update the text of the save Button to Update for records to update
            binding.save.text = "Update"
        }


    }

    fun onSave(view: View) {
        // validate user input

        var isValid = true

        record.let {

            //Check if modulNum is not empty
        it.moduleNum = binding.moduleNum.text.toString().trim().ifEmpty {
            binding.moduleNum.error = getString(R.string.module_num_not_empty)
            isValid = false
            ""
        }

            //Check if moduleName is not empty
       it.moduleName = binding.moduleName.text.toString().trim().ifEmpty {
            binding.moduleName.error = getString(R.string.modul_name_not_empty)
            isValid = false
            ""
        }

        try {
            //Set the mark value of the record
            it.mark = if (binding.mark.text.toString().trim().isEmpty()) {
                0
            } else {
                binding.mark.text.toString().trim().toInt()
            }

            //Set the crp of the record
            it.crp = if (binding.crp.text.toString().trim().isEmpty() || binding.crp.text.toString().toInt() > 15) {
                binding.crp.error = getString(R.string.credit_point_not_empty)
                isValid = false
                0
            } else {
                0
            }

        } catch (e: Exception) {
            Log.e(RecordFormActivity::class.java.toString(), e.toString())
            isValid = false
            Toast.makeText(this@RecordFormActivity, "Credit Point oder Note dürfen keine Buchstaben enthalten!", Toast.LENGTH_LONG).show()
        }


       if (isValid) {
           //Initialise the rest of the record before saving/updating it
               it.year = (binding.year.selectedItem.toString()).toInt()
               it.isSummerTerm = binding.isSummerTerm.isChecked
               it.isHalfWeighted = binding.isHalfWeighted.isChecked
               it.crp = (binding.crp.text.toString()).toInt()
            if (it.id == null) {
                //Save a new created record
                RecordDAO.get(this).persist(it)
            } else {
                //update a record
                RecordDAO.get(this).update(it)
            }
            finish()
        }
        }
    }

    private fun getYears(): Array<String> {
        return resources.getStringArray(R.array.years)
    }

}
