package com.example.cipherappproject

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Class that stores input filter specifications
 *
 * @property searchName the user input of a desired campsite name
 */
/*
 * Built by Bailey
 * Nov 2 2026
 * Updates:
 *
 */
class SearchFilter{
    private var searchName = mutableStateOf("")

    fun getName(): String {
        return searchName.toString()
    }
    fun setName(newName:String) {
        searchName = mutableStateOf(newName)
    }
    //TODO: select what values to filter by, connect each to user input in SearchFiltersView
    //give default values of null??
}