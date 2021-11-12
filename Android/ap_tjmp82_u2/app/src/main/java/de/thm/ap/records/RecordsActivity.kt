package de.thm.ap.records


import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.SparseBooleanArray
import android.view.*
import android.widget.*
import androidx.annotation.MenuRes
import androidx.appcompat.app.AlertDialog
import de.thm.ap.records.databinding.ActivityRecordsBinding
import de.thm.ap.records.model.Record
import de.thm.ap.records.model.Stats
import de.thm.ap.records.persistence.RecordDAO
import java.util.*
import kotlin.collections.ArrayList

class RecordsActivity : AppCompatActivity(){

    private lateinit var binding: ActivityRecordsBinding
    private var records= listOf<Record>()
    private lateinit var adapter : ArrayAdapter<Record>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recordListView.emptyView = binding.recordListEmptyView

    }

    override fun onStart() {
        super.onStart()

        records = RecordDAO.get(this).findAll()

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_activated_1, records)

        binding.recordListView.adapter = adapter



        binding.recordListView.setOnItemClickListener { parent: AdapterView<*>, view, position, id ->
            Intent(this, RecordFormActivity::class.java).also {
                //val idRecordToUpdate = records.get(position).id;
                val idRecordToUpdate = records[position].id;
                //Log.d("RecordInfo", records.get(position).id.toString())
                it.putExtra("idRecordToUpdate", idRecordToUpdate)
                startActivity(it)
            }
        }


        binding.recordListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL

        binding.recordListView.setMultiChoiceModeListener(object : AbsListView.MultiChoiceModeListener {

            val checkedRecordsList =ArrayList<Record>()

            override fun onItemCheckedStateChanged( mode: ActionMode?, position: Int, id: Long, checked: Boolean) {
                if(checked) {
                    adapter.getItem(position)?.let { checkedRecordsList.add(it) }
                }else{
                    checkedRecordsList.remove(adapter.getItem(position))
                }
                mode?.title = "${checkedRecordsList.size} ausgewählt"
            }

            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                val menuInflater: MenuInflater = mode!!.menuInflater
                menuInflater.inflate(R.menu.menu_actions, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
               // val checkedItems: SparseBooleanArray = binding.recordListView.checkedItemPositions

                return when (item?.itemId) {
                    R.id.action_delete -> {
                        AlertDialog.Builder(this@RecordsActivity).apply{
                            setMessage("Sollen die Leistungen wircklich gelöschtwerden?")
                            setPositiveButton("löschen"){ _, _ ->
                                checkedRecordsList.forEach{ RecordDAO.get(this@RecordsActivity).delete(it) }
                                adapter.clear()
                                adapter.addAll(RecordDAO.get(this@RecordsActivity).findAll())
                                checkedRecordsList.clear()

                            }
                            setNegativeButton("cancel", null)
                            show()
                        }
                        mode?.finish()
                        true
                    }
                    R.id.action_email -> {
                        Intent(Intent.ACTION_SEND).also {
                            var text = StringBuilder()
                            checkedRecordsList.forEach {
                                text.append(it.toString())
                                text.append(" ")
                            }

                            it.data = Uri.parse("mailto")
                            it.type = "text/plain"
                            it.putExtra(Intent.EXTRA_SUBJECT, "Meine Leistungen ${checkedRecordsList.size}")
                            it.putExtra(Intent.EXTRA_TEXT, "\n\n ${text.toString()}")
                            startActivity(it)
                        }
                        mode?.finish()
                        true
                    }
                    else -> false
                }
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
                //
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu
        // This adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.records, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {

            R.id.action_add -> {
                val i = Intent(this, RecordFormActivity::class.java)
                startActivity(i)
                true
            }

            R.id.action_stats -> {
                var listRecs = RecordDAO.get(this).findAll()
                //var stats = Stats(listRecs)
               // stats.setDateForSats()
                var alertDia = AlertDialog.Builder(this)
                .setTitle(R.string.stats)
                .setMessage("${Stats(listRecs)}")
                .setNeutralButton(R.string.close, null)
                .show()

                alertDia.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getColor(R.color.dialogCloseColor))

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    fun onSave(view: android.view.View) {
    }
}

