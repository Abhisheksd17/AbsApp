package com.example.feature_profile.viewmodel

import androidx.lifecycle.ViewModel
import com.example.common.datastore.DataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileDetailsViewModel @Inject constructor(
    private val datastore: DataStore,
): ViewModel() {



    val userProfile = datastore.getUserProfile()


}