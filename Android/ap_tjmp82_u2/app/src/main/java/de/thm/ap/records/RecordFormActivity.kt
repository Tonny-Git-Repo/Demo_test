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


        if(intent.getSerializableExtra("idRecordToUpdate")!= null){
            record.id = intent?.getSerializableExtra("idRecordToUpdate") as Int?
            var record =  RecordDAO.get(this).findById(record.id!!)
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

        it.moduleNum = binding.moduleNum.text.toString().trim().ifEmpty {
            binding.moduleNum.error = getString(R.string.module_num_not_empty)
            isValid = false
            ""
        }

       it.moduleName = binding.moduleName.text.toString().trim().ifEmpty {
            binding.moduleName.error = getString(R.string.modul_name_not_empty)
            isValid = false
            ""
        }


        it.crp = if (binding.crp.text.toString().trim().isEmpty()) {
            binding.crp.error = getString(R.string.credit_point_not_empty)
            isValid = false
            0
        } else {
            0
        }

       if (isValid) {
               it.year = (binding.year.selectedItem.toString()).toInt()
               it.isSummerTerm = binding.isSummerTerm.isChecked
               it.isHalfWeighted = binding.isHalfWeighted.isChecked
               it.mark = (binding.mark.text.toString()).toInt()
               it.crp = (binding.crp.text.toString()).toInt()
            if (it.id == null) {
                RecordDAO.get(this).persist(it)
            } else {
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
