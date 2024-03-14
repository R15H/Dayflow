package com.alextra.tools

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.alextra.tools.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.InputStreamReader

import android.content.ContentResolver
import com.alextra.tools.databinding.FragmentMainBinding
import com.alextra.tools.databinding.FragmentTimeManagementBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TimeManagement.newInstance] factory method to
 * create an instance of this fragment.
 */
class TimeManagement : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null



    private var _binding: FragmentTimeManagementBinding? = null
    private val binding get() = _binding!! // the bang operator (!!) is used to tell the compiler that the property is not null and to force the unwrapping of the value.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        _binding = FragmentTimeManagementBinding.inflate(layoutInflater)
        val root = binding.root
        binding.importActivitiesButton.setOnClickListener {
            println("jj")
        }


        // attach event listenter on click to button
        //val button = findViewById<android.widget.Button>(R.id.importActivitiesButton)
        //button.setOnClickListener { pickFile() }



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time_management, container, false)

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TimeManagement.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TimeManagement().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}