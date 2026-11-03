package com.example.cipherappproject

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Class that stores input filter specifications
 *
 * @property searchName is the user input of a desired campsite name
 */
class SearchFilter{
    private var searchName = mutableStateOf("")

    fun getName(): MutableState<String> {
        return searchName
    }
    fun setName(newName:String) {
        searchName = mutableStateOf(newName)
    }
    //TODO: select what values to filter by, connect each to user input in SearchFiltersView
    //give default values of null??
}