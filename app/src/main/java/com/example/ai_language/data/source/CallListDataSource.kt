package com.example.ai_language.data.source

class CallListDataSource {

}

//class DictionaryDataSource @Inject constructor(
//    private val dictionaryService : DictionaryService
//) {
//    fun getDictionaryByOpenApi(serviceKey: String, numOfRows: String, pageNo: String) : Flow<ResponseBodys> = flow{
//        val result =dictionaryService.getDictionaryByOpenApi(serviceKey,numOfRows,pageNo)
//        emit(result)
//    }.catch {
//        Log.e("Get Dictionary By OpenApi Failure", it.message.toString())
//    }
//}