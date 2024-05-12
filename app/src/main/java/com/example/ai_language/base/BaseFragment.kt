package com.example.ai_language.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding>(@LayoutRes private val layoutRes: Int) : Fragment() {
    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding ?: throw IllegalStateException("Trying to access the binding outside of the view lifecycle.")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("BaseFragment", "onViewCreated - view created")
        setLayout()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("BaseFragment", "onDestroyView - clearing binding")
        _binding = null
    }

    abstract fun setLayout()
}