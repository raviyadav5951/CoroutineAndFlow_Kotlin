package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase2.callbacks

import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.AndroidVersion
import com.lukaslechner.coroutineusecasesonandroid.mock.VersionFeatures
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SequentialNetworkRequestsCallbacksViewModel(
    private val mockApi: CallbackMockApi = mockApi()
) : BaseViewModel<UiState>() {

    //declaring the calls here to clear them for garbage collection

    private var getAndroidVersionCall:Call<List<AndroidVersion>>?=null
    private var getAndroidFeaturesCall:Call<VersionFeatures>?=null


    override fun onCleared() {
        super.onCleared()
        getAndroidVersionCall?.cancel()
        getAndroidFeaturesCall?.cancel()
    }

    fun perform2SequentialNetworkRequest() {
        uiState.value=UiState.Loading


         getAndroidVersionCall=mockApi.getRecentAndroidVersions()
        getAndroidVersionCall!!.enqueue(object : Callback<List<AndroidVersion>> {
            override fun onResponse(
                call: Call<List<AndroidVersion>>,
                response: Response<List<AndroidVersion>>
            ) {
                if(response.isSuccessful){
                    val mostRecentVersion=response.body()?.last()

                    //making second call for fetching the features of the last api version

                     getAndroidFeaturesCall=mockApi.getAndroidVersionFeatures(mostRecentVersion!!.apiLevel)
                    getAndroidFeaturesCall!!.enqueue(object :Callback<VersionFeatures>{
                        override fun onResponse(
                            call: Call<VersionFeatures>,
                            response: Response<VersionFeatures>
                        ) {
                            if(response.isSuccessful){

                                val latestFeatures=response.body()
                                uiState.value=UiState.Success(latestFeatures!!)
                            }
                            else{
                                uiState.value=UiState.Error("Something went wrong in network call")
                            }
                        }

                        override fun onFailure(call: Call<VersionFeatures>, t: Throwable) {
                            uiState.value=UiState.Error("Something went wrong in getting features")
                        }
                    })


                }
                else{
                    uiState.value=UiState.Error("Network request failed")
                }


            }

            override fun onFailure(call: Call<List<AndroidVersion>>, t: Throwable) {
                uiState.value=UiState.Error("Something unexpected happened")
            }

        })
    }
}