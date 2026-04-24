package com.example.feature_contact.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.data.NetworkResult
import com.example.domain.repository.ContactRepository
import com.example.model.contact.ContactDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val contactRepository: ContactRepository
): ViewModel() {



    private val _contactListState = MutableStateFlow<NetworkResult<List<ContactDomain>>>(NetworkResult.Idle())
    val contactListState = _contactListState.asStateFlow()

    fun getContactList(){
        viewModelScope.launch {
            contactRepository.fetchContacts().collect{ response ->
                _contactListState.value = response
            }
        }
    }

    fun syncRefreshContact(){
        viewModelScope.launch {
            contactRepository.syncRefreshContact().collect { response->
                _contactListState.value=response
            }
        }
    }

}