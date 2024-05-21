package woowacourse.shopping.ui.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.model.data.CartsImpl
import woowacourse.shopping.ui.cart.adapter.CartAdapter
import woowacourse.shopping.ui.cart.viewmodel.CartViewModel
import woowacourse.shopping.ui.cart.viewmodel.CartViewModelFactory

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var adapter: CartAdapter
    private val viewModel: CartViewModel by viewModels { CartViewModelFactory(CartsImpl) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinding()
        setCartAdapter()
        observeCartItems()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart)
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }

    private fun observeCartItems() {
        viewModel.cart.observe(this) {
            adapter.setData(it)
        }
    }

    private fun setCartAdapter() {
        binding.rvCart.itemAnimator = null
        adapter =
            CartAdapter { cartId ->
                viewModel.removeCartItem(cartId)
            }
        binding.rvCart.adapter = adapter
    }

    companion object {
        fun startActivity(context: Context) =
            Intent(context, CartActivity::class.java).run {
                context.startActivity(this)
            }
    }
}
