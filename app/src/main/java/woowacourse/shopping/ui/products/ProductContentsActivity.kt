package woowacourse.shopping.ui.products

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityProductContentsBinding
import woowacourse.shopping.model.data.CartsImpl
import woowacourse.shopping.model.data.ProductsImpl
import woowacourse.shopping.ui.cart.CartActivity
import woowacourse.shopping.ui.detail.ProductDetailActivity
import woowacourse.shopping.ui.products.adapter.ProductAdapter
import woowacourse.shopping.ui.products.adapter.RecentProductAdapter
import woowacourse.shopping.ui.products.viewmodel.ProductContentsViewModel
import woowacourse.shopping.ui.products.viewmodel.ProductContentsViewModelFactory
import woowacourse.shopping.ui.utils.urlToImage

class ProductContentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductContentsBinding
    private lateinit var productAdapter: ProductAdapter
    private lateinit var recentProductAdapter: RecentProductAdapter
    private val viewModel: ProductContentsViewModel by viewModels {
        ProductContentsViewModelFactory(ProductsImpl, CartsImpl)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinding()
        setProductAdapter()
        setRecentProductAdapter()
        initToolbar()
        observeProductItems()
        observeRecentProductItems()
        setOnRecyclerViewScrollListener()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadCartItems()
    }

    private fun initToolbar() {
        binding.ivCart.setOnClickListener {
            CartActivity.startActivity(this)
        }
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_contents)
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }

    private fun setProductAdapter() {
        binding.rvProducts.itemAnimator = null
        productAdapter =
            ProductAdapter(
                { ProductDetailActivity.startActivity(this, it) },
                { viewModel.plusCount(it) },
                { viewModel.minusCount(it) },
            )
        binding.rvProducts.adapter = productAdapter
    }

    private fun setRecentProductAdapter() {
        binding.rvRecentProducts.itemAnimator = null
        recentProductAdapter =
            RecentProductAdapter {
                ProductDetailActivity.startActivity(this, it)
            }
        binding.rvRecentProducts.adapter = recentProductAdapter
    }

    private fun observeProductItems() {
        viewModel.productWithQuantity.observe(this) {
            productAdapter.setData(it)
        }
    }

    private fun observeRecentProductItems() {
        viewModel.recentProducts.observe(this) {
            recentProductAdapter.setRecentProducts(it)
        }
    }

    private fun setOnRecyclerViewScrollListener() {
        binding.rvProducts.addOnScrollListener(
            onScrollListener(),
        )
    }

    private fun onScrollListener() =
        object : RecyclerView.OnScrollListener() {
            override fun onScrolled(
                recyclerView: RecyclerView,
                dx: Int,
                dy: Int,
            ) {
                super.onScrolled(recyclerView, dx, dy)
                binding.btnLoadMore.visibility =
                    if (isLastItemVisible(recyclerView)) View.VISIBLE else View.GONE
            }
        }

    private fun isLastItemVisible(recyclerView: RecyclerView) =
        (recyclerView.layoutManager as GridLayoutManager)
            .findLastCompletelyVisibleItemPosition() == adapterItemSize()

    private fun adapterItemSize() = recentProductAdapter.itemCount - OFFSET

    companion object {
        private const val OFFSET = 1
    }
}

@BindingAdapter("imageUrl")
fun ImageView.bindUrlToImage(imageUrl: String?) {
    urlToImage(context, imageUrl)
}

@BindingAdapter("isVisible")
fun View.setIsVisible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}
