package com.example.feature_contact.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.common.R
import com.example.domain.data.NetworkResult
import com.example.feature_contact.ui.ContactItem
import com.example.feature_contact.viewmodel.ContactViewModel
import com.example.ui.screen.HomeTopBar
import com.example.ui.theme.SoftLightGray
import com.example.ui.theme.White

@Composable
fun Contacts(){

    val viewModel: ContactViewModel = hiltViewModel()

    val state by viewModel.contactListState.collectAsState()

    var isSearch by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    var isRefreshing by remember { mutableStateOf(false) }
    val refreshState = rememberPullToRefreshState()


    LaunchedEffect(Unit) {
        viewModel.getContactList()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            HomeTopBar(
                header = stringResource(R.string.contact),
                search = isSearch,
                query = query,
                onQueryChange = { query = it },
                onSearchClick = { isSearch = !isSearch },
                onBackClick = {
                    isSearch = false
                    query = ""
                }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(White)
            ) {


                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 14.dp, bottom = 24.dp)
                        .width(30.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(50))
                        .background(SoftLightGray.copy(alpha = 0.5f))
                )

                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        isRefreshing = true
                        viewModel.syncRefreshContact()
                    },
                    state = refreshState
                )
                {
                    when (val result = state) {

                        is NetworkResult.Loading -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }

                        is NetworkResult.Success -> {
                            isRefreshing = false

                            val contacts = result.data ?: emptyList()

                            LazyColumn {
                                items(contacts, key = {it.id }) { item ->

                                    ContactItem(
                                        imageUri = item.profile_url,
                                        onImageSelected = {},
                                        name = item.name,
                                        status = item.status
                                    )

                                }
                            }
                        }

                        is NetworkResult.Error -> {
                            isRefreshing = false
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(result.message ?: "Error")
                            }
                        }

                        else -> Unit
                    }
                }
            }

        }

    }

}