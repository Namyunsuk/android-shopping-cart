package woowacourse.shopping.ui.detail.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.data.CartDao
import woowacourse.shopping.model.data.ProductDao
import woowacourse.shopping.ui.utils.Event

class ProductDetailViewModel(
    private val productDao: ProductDao,
    private val cartDao: CartDao,
) : ViewModel() {
    private val _error: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(false))
    val error: LiveData<Event<Boolean>> get() = _error

    private val _errorMsg: MutableLiveData<Event<String>> = MutableLiveData(Event(""))
    val errorMsg: LiveData<Event<String>> get() = _errorMsg

    private val _product: MutableLiveData<Product> = MutableLiveData()
    val product: LiveData<Product> get() = _product

    fun loadProduct(productId: Long) {
        runCatching {
            productDao.find(productId)
        }.onSuccess {
            _error.setErrorHandled(false)
            _product.value = it
        }.onFailure {
            _error.setErrorHandled(true)
            _errorMsg.setErrorHandled(it.message.toString())
        }
    }

    fun addProductToCart() {
        _product.value?.let {
            cartDao.save(it)
        }
    }

    private fun <T> MutableLiveData<Event<T>>.setErrorHandled(value: T?) {
        if (this.value?.hasBeenHandled == false) {
            value?.let { this.value = Event(it) }
        }
    }
}
